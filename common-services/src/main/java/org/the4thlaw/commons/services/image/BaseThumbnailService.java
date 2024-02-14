package org.the4thlaw.commons.services.image;

import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.the4thlaw.commons.utils.image.ImageUtils;
import org.the4thlaw.commons.utils.io.FileUtils;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Base class to implement Spring services managing thumbnails.
 */
public abstract class BaseThumbnailService {
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseThumbnailService.class);
	private static final Pattern THUMB_DIR_PATTERN = Pattern.compile("^\\d+w$");
	private static final double LENIENCY_WIDTH_FACTOR = 1.2;
	private static final int THREAD_POOL_RATE = 60 * 60 * 1000;
	/** The absolute maximum number of thumb threads that can run in parallel. */
	private static final int MAX_RUNNING_THUMBS = 10;
	/**
	 * Timeout for thumbnail generation. Firefox and Chrome's default request timeouts are 300 seconds but we don't want
	 * to wait that long.
	 */
	private static final int THUMB_TIMEOUT_SECONDS = 150;

	@FunctionalInterface
	public interface ImageSupplier {
		Path getImage();
	}

	private final ThreadPoolExecutor executor;
	private final Path thumbnailDirectory;
	private final Optional<Integer> fixedThreads;

	/**
	 * Constructor allowing to set the thumbnail directory and queue size.
	 *
	 * @param thumbnailDirectory The directory where thumbnails are stored.
	 * @param queueSize The size of the thumbnail generation queue.
	 */
	protected BaseThumbnailService(Path thumbnailDirectory, int queueSize) {
		this(thumbnailDirectory, queueSize, Optional.empty());
	}

	/**
	 * Constructor allowing to set the thumbnail directory and queue size.
	 *
	 * @param thumbnailDirectory The directory where thumbnails are stored.
	 * @param queueSize The size of the thumbnail generation queue.
	 * @param threadCount The maximum thread count. Will be automatic if missing.
	 */
	protected BaseThumbnailService(Path thumbnailDirectory, int queueSize, Optional<Integer> threadCount) {
		this.thumbnailDirectory = thumbnailDirectory;
		this.fixedThreads = threadCount;

		// Another option would be to use a LIFO but it seems like it will be pretty confusing for users
		// (see https://stackoverflow.com/a/8272674/109813)
		executor = new ThreadPoolExecutor(0, 1, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>(queueSize));
		executor.allowCoreThreadTimeOut(true);
		setThumbnailPoolSize();
	}

	/**
	 * Sets the thumbnail pool size. If the pool size is dynamic, should be called at periodic intervals to cope with
	 * changes in the CPU configuration as described by {@link Runtime#availableProcessors()}.
	 */
	@Scheduled(initialDelay = THREAD_POOL_RATE, fixedRate = THREAD_POOL_RATE)
	public void setThumbnailPoolSize() {
		if (fixedThreads.isPresent()) {
			int systemMaxThreads = fixedThreads.get();
			LOGGER.info("Setting thumbnail pool size: fixed = {}", systemMaxThreads);
			executor.setMaximumPoolSize(systemMaxThreads);
			executor.setCorePoolSize(systemMaxThreads);
			return;
		}

		int cores = Runtime.getRuntime().availableProcessors();
		long memory = Runtime.getRuntime().maxMemory();

		// Allow at most one thumbnail thread per two cores
		int coreLimit = cores / 2;
		// Allow at most one thumbnail thread per (roughly) 256MB of RAM
		long memoryLimit = memory / (255_000_000);
		// Take the minimum of those two, constrained
		int maxThreads = (int) Math.min(Math.min(coreLimit, memoryLimit), MAX_RUNNING_THUMBS);

		LOGGER.info("Setting thumbnail pool size: core = {}, memory = {}, final = {}", coreLimit, memoryLimit,
				maxThreads);
		// Gotcha: the maximum pool size is only used when the queue is full. What we need is a fixed pool size
		// where the core threads can time out
		executor.setMaximumPoolSize(maxThreads);
		executor.setCorePoolSize(maxThreads);
	}

	public ImageRetrievalResponse getThumbnail(long id, int maxWidth, boolean lenient, ImageSupplier imageFileLoader)			 {
		Path directoryBySize = thumbnailDirectory.resolve(maxWidth + "w");

		// Check cache (two possible formats - jpg is more likely so check it first)
		ImageRetrievalResponse cached = getCachedThumbnail(directoryBySize, id);
		if (cached != null) {
			return cached;
		}

		// No cache hit, check for leniency
		Path image = imageFileLoader.getImage();
		int originalWidth;
		try {
			originalWidth = ImageUtils.getImageWidth(image.toFile());
		} catch (IOException e) {
			throw new ThumbnailException("I/O error while getting image width for " + image, e);
		}
		if (maxWidth >= originalWidth || (lenient && maxWidth * LENIENCY_WIDTH_FACTOR >= originalWidth)) {
			LOGGER.debug("Leniently returning the original image for {}, it's {}px wide instead of the requested {}",
					id, originalWidth, maxWidth);
			// Return the original image, we don't have anything larger or the requested width is close enough
			// to the original not to warrant the creation of a resized version
			return new ImageRetrievalResponse(image);
		}

		/*
		No cache hit, generate thumbnail.
		Thumbnails are generated in parallel threads so that we can limit the number of ongoing generations.
		However, we still block the request while waiting for the result because the browser is expecting the
		thumbnail.
		This is just a way to limit resource usage in constrained environments. It impacts the user experience
		but without this, we could just kill the JVM with OutOfMemoryErrors...
		 */
		long submissionTime = System.currentTimeMillis();

		try {
			Future<ImageRetrievalResponse> submission = executor
					.submit(() -> generateThumbnail(id, image, maxWidth, directoryBySize, submissionTime));
			LOGGER.trace("Thumbnail generation submitted for image {} at width {}", id, maxWidth);
			logThumbnailExecutorStats();
			return submission.get(THUMB_TIMEOUT_SECONDS, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			LOGGER.warn("Interrupted while generating a thumbnail for image {} at width {}", id, maxWidth, e);
			throw new ThumbnailException("Interrupted during thumbnail generation");
		} catch (ExecutionException | TimeoutException | RejectedExecutionException e) {
			LOGGER.warn(
					"Failed to generate a thumbnail for image {} at width {}, will attempt to provide a fallback. Reason is: {}",
					id, maxWidth, e.getMessage());
			return getFallbackThumbnail(id, maxWidth);
		}
	}

	private static ImageRetrievalResponse getCachedThumbnail(Path directoryBySize, long id) {
		Path jpgThumb = directoryBySize.resolve(id + ".jpg");
		if (Files.exists(jpgThumb)) {
			return new ImageRetrievalResponse(jpgThumb);
		}
		Path pngThumb = directoryBySize.resolve(id + ".png");
		if (Files.exists(pngThumb)) {
			return new ImageRetrievalResponse(pngThumb);
		}
		return null;
	}

	private ImageRetrievalResponse getFallbackThumbnail(long id, int maxWidth)			{
		List<Integer> availableWidths;
		try (Stream<Path> list = Files.list(thumbnailDirectory)) {
			availableWidths = list
					.filter(Files::isDirectory)
					// Keep only the names
					.map(p -> p.getFileName().toString())
					// Filter what seem to be thumbnail directories
					.filter(n -> THUMB_DIR_PATTERN.matcher(n).matches())
					// Parse the width
					.map(f -> Integer.parseInt(f.substring(0, f.length() - 1)))
					// Sort so that the closest to maxWidth comes first
					.sorted((a, b) -> Math.abs(maxWidth - a) - Math.abs(maxWidth - b))
					.collect(Collectors.toList());
		} catch (IOException e) {
			throw new ThumbnailException("Could not find a fallback thumbnail for image" + id, e);
		}

		LOGGER.trace("Found the following possible thumbnail sizes: {}", availableWidths);

		for (int width : availableWidths) {
			Path directoryBySize = thumbnailDirectory.resolve(width + "w");
			ImageRetrievalResponse cached = getCachedThumbnail(directoryBySize, id);
			if (cached != null) {
				LOGGER.debug("Found a fallback thumbnail for image {} at size {} instead of size {}", id, width,
						maxWidth);
				cached.setExact(false);
				return cached;
			}
		}

		// If all else fails, abort
		throw new ThumbnailGenerationOverload("Could not find a fallback thumbnail for image" + id);
	}

	private ImageRetrievalResponse generateThumbnail(long id, Path image, int maxWidth,
			Path directoryBySize, long submissionTime) {
		// If the task was submitted but the request timed out, just complete the task without doing anything
		long secondsSinceSubmission = (System.currentTimeMillis() - submissionTime) / 1000;
		if (secondsSinceSubmission > THUMB_TIMEOUT_SECONDS) {
			LOGGER.debug("Discarded thubmnail generation for image {} at width {}: "
					+ "the request timed out in the meantime (submitted {} seconds ago)",
					id, maxWidth, secondsSinceSubmission);
			return null;
		}

		BufferedImage buffImage;
		try {
			buffImage = ImageIO.read(image.toFile());
		} catch (IOException e) {
			throw new ThumbnailException("I/O error while reading the source image", e);
		}

		long time = System.currentTimeMillis();
		logThumbnailExecutorStats();
		LOGGER.trace("Generating thumbnail for image {} at width {}", id, maxWidth);

		// Avoid creating the directory if we return the original image
		if (!Files.isDirectory(directoryBySize)) {
			try {
				Files.createDirectories(directoryBySize);
			} catch (IOException e) {
				throw new ThumbnailException("I/O error while creating thumbnail directory", e);
			}
			LOGGER.debug("Creating thumbnail directory: {}", directoryBySize);
		} else {
			LOGGER.trace("Thumbnail directory exists: {}", directoryBySize);
		}

		BufferedImage buffThumb = Scalr.resize(buffImage, Method.ULTRA_QUALITY, Mode.FIT_TO_WIDTH, maxWidth, 0,
				Scalr.OP_ANTIALIAS);
		LOGGER.debug("Thumbnail for {} generated in {}ms", id, System.currentTimeMillis() - time);

		Path jpgThumb = directoryBySize.resolve(id + ".jpg");
		Path pngThumb = directoryBySize.resolve(id + ".png");
		try {
			// Write opaque images as JPG, transparent images as PNG
			if (Transparency.OPAQUE == buffThumb.getTransparency()) {
				ImageIO.write(buffThumb, "jpg", jpgThumb.toFile());
				return new ImageRetrievalResponse(jpgThumb);
			} else {
				ImageIO.write(buffThumb, "png", pngThumb.toFile());
				return new ImageRetrievalResponse(pngThumb);
			}
		} catch (IOException e) {
			// Ensure we don't store invalid contents
			FileUtils.deleteQuietly(jpgThumb);
			FileUtils.deleteQuietly(pngThumb);
			throw new ThumbnailException("I/O error while writing the thumbnail", e);
		} finally {
			buffImage.flush();
			buffThumb.flush();
		}
	}

	private void logThumbnailExecutorStats() {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Thumbnail executor stats: {} active, {} in pool (max: {}), {} queued",
					executor.getActiveCount(),
					executor.getPoolSize(), executor.getMaximumPoolSize(), executor.getQueue().size());
		}
	}
}
