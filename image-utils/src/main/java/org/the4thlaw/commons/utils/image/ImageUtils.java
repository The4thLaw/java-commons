package org.the4thlaw.commons.utils.image;

import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Locale;
import java.util.function.Function;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

import org.the4thlaw.commons.utils.io.FileUtils;
import org.the4thlaw.commons.utils.io.FilenameUtils;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities to manipulate images.
 * <p>
 * This class still uses the old {@link File} class rather than the new Path abstraction because Java's 2D API still
 * does as well.
 * </p>
 */
public final class ImageUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImageUtils.class);

	private ImageUtils() {
	}

	/**
	 * Gets the width of an image. In most case, this operation only checks the headers. If that fails, the image will
	 * be loaded in memory.
	 * 
	 * @param image The image to scan.
	 * @return The image width.
	 * @throws IOException If getting the width fails.
	 */
	public static int getImageWidth(File image) throws IOException {
		int w = getImageWidthEfficient(image);
		if (w > 0) {
			LOGGER.trace("Efficiently got the image width for {}", image);
			return w;
		}
		return getImageWidthMemoryIntensive(image);
	}

	// Solution from https://stackoverflow.com/a/12164026/109813
	// tested as more efficient by https://stackoverflow.com/a/14888091/109813
	private static int getImageWidthEfficient(File image) {
		String suffix = FilenameUtils.getFileExtension(image.getName());
		Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
		while (iter.hasNext()) {
			ImageReader reader = iter.next();
			try (ImageInputStream stream = new FileImageInputStream(image)) {
				reader.setInput(stream);
				return reader.getWidth(reader.getMinIndex());
			} catch (IOException e) {
				LOGGER.debug("Failed to determine the image width efficiently", e);
				return -1;
			} finally {
				reader.dispose();
			}
		}
		return -1;

	}

	private static int getImageWidthMemoryIntensive(File image) throws IOException {
		BufferedImage buffImage = null;
		try {
			buffImage = ImageIO.read(image);
			return buffImage.getWidth();
		} finally {
			if (buffImage != null) {
				buffImage.flush();
			}
		}
	}

	public enum ImageOutputFormat {
		JPG, PNG;

		public String getImageIOFormat() {
			return name().toLowerCase(Locale.ROOT);
		}

		public String getFileExtension() {
			return getImageIOFormat();
		}
	}

	public static Path resize(Path image, int maxWidth, Function<ImageOutputFormat, Path> outputPath)
			throws IIOException {
		BufferedImage buffImage;
		try {
			buffImage = ImageIO.read(image.toFile());
		} catch (IOException e) {
			throw new IIOException("I/O error while reading the source image", e);
		}

		if (buffImage == null) {
			throw new IIOException("Failed to open image " + image + ", potential unsupported image format");
		}

		long time = System.currentTimeMillis();
		LOGGER.trace("Resizing image {} at width {}", image, maxWidth);

		BufferedImage buffThumb = Scalr.resize(buffImage, Method.ULTRA_QUALITY, Mode.FIT_TO_WIDTH, maxWidth, 0,
				Scalr.OP_ANTIALIAS);
		LOGGER.debug("Thumbnail for {} generated in {}ms", image, System.currentTimeMillis() - time);

		ImageOutputFormat outputFormat = Transparency.OPAQUE == buffThumb.getTransparency() ? ImageOutputFormat.JPG
				: ImageOutputFormat.PNG;
		Path output = outputPath.apply(outputFormat);

		try {
			ImageIO.write(buffThumb, outputFormat.getImageIOFormat(), output.toFile());
		} catch (IOException e) {
			// Ensure we don't store invalid contents
			FileUtils.deleteQuietly(output);
			throw new IIOException("I/O error while writing the thumbnail", e);
		} finally {
			buffImage.flush();
			buffThumb.flush();
		}

		return output;
	}
}
