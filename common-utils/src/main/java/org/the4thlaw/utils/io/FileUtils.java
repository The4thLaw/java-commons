package org.the4thlaw.utils.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic file utils.
 */
public final class FileUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

	private FileUtils() {
	}

	/**
	 * Deletes a file, but logs if deletion failed. If deletion failed, the file is marked for deletion on exit, in case
	 * the lock has disappeared in the mean time.
	 * <p>
	 * This method ignores the file if it is not a regular file.
	 * </p>
	 * 
	 * @param file The file to delete.
	 */
	public static void delete(File file) {
		delete(file.toPath());
	}

	/**
	 * Deletes a file, but logs if deletion failed. If deletion failed, the file is marked for deletion on exit, in case
	 * the lock has disappeared in the mean time.
	 * <p>
	 * This method ignores the file if it is not a regular file.
	 * </p>
	 * 
	 * @param file The file to delete.
	 */
	public static void delete(Path file) {
		if (file == null) {
			// Do nothing, could be used in finally blocks
			return;
		}

		if (!(Files.exists(file) && Files.isRegularFile(file))) {
			LOGGER.debug("Doesn't exist or not a regular file: {}", file.toAbsolutePath());
			return;
		}

		try {
			Files.delete(file);
		} catch (IOException e) {
			LOGGER.warn("Failed to delete file at {}", file.toAbsolutePath(), e);
			file.toFile().deleteOnExit();
		}
	}

	/**
	 * Deletes a directory recursively, and log if deletion failed but don't throw an exception.
	 * 
	 * @param directory The directory to delete.
	 */
	public static void deleteDirectory(Path directory) {
		if (directory == null) {
			return;
		}
		deleteDirectory(directory.toFile());
	}

	/**
	 * Deletes a directory recursively, and log if deletion failed but don't throw an exception.
	 * 
	 * @param directory The directory to delete.
	 */
	public static void deleteDirectory(File directory) {
		if (directory == null) {
			return;
		}
		LOGGER.debug("Deleting directory at {}", directory);
		try {
			org.apache.commons.io.FileUtils.deleteDirectory(directory);
		} catch (IOException e) {
			LOGGER.warn("Failed to delete directory at {}", directory, e);
		}
	}
	
}
