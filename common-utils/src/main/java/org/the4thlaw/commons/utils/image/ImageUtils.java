package org.the4thlaw.commons.utils.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

import org.the4thlaw.commons.utils.io.FilenameUtils;

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
}