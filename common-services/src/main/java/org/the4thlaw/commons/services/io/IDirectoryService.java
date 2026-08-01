package org.the4thlaw.commons.services.io;

import java.nio.file.Path;

/**
 * A service that retrieves directories based on standard guidelines (up to the implementor).
 * @since 1.5
 */
public interface IDirectoryService {
	/**
	 * Gets a standard directory. The returned directory is guaranteed to exist.
	 * 
	 * @param directory The directory to get.
	 */
	default Path getDirectory(StandardDirectory directory) {
		return getDirectory(directory, true);
	}

	/**
	 * Gets a standard directory.
	 * 
	 * @param directory The directory to get.
	 * @param autoCreate Whether to auto-create the directory if it is missing.
	 */
	Path getDirectory(StandardDirectory directory, boolean autoCreate);

	/** Gets the {@link StandardDirectory#CACHE} directory. */
	Path getCacheDirectory();
	/** Gets the {@link StandardDirectory#CONFIGURATION} directory. */
	Path getConfigurationDirectory();
	/** Gets the {@link StandardDirectory#DATA} directory. */
	Path getDataDirectory();
	/** Gets the {@link StandardDirectory#EXPORT} directory. */
	Path getExportDirectory();
	/** Gets the {@link StandardDirectory#IMAGES} directory. */
	Path getImagesDirectory();
	/** Gets the {@link StandardDirectory#LOGS} directory. */
	Path getLogsDirectory();
	/** Gets the {@link StandardDirectory#RUNTIME} directory. */
	Path getRuntimeDirectory();
	/** Gets the {@link StandardDirectory#STATE} directory. */
	Path getStateDirectory();
	/** Gets the {@link StandardDirectory#TEMP} directory. */
	Path getTempDirectory();
	/** Gets the {@link StandardDirectory#THUMBNAILS} directory. */
	Path getThumbnailsDirectory();

	/**
	 * Creates a temporary file in the application temporary directory. The file is marked as to be deleted on exit.
	 *
	 * @param prefix The prefix string to be used in generating the file's name; must be at least three characters long
	 * @return The created file.
	 */
	default Path createTempFile(String prefix) {
		return createTempFile(prefix, null, null);
	}

	/**
	 * Creates a temporary file in the application temporary directory. The file is marked as to be deleted on exit.
	 *
	 * @param prefix The prefix string to be used in generating the file's name; must be at least three characters long
	 * @param suffix The suffix string to be used in generating the file's name; may be <code>null</code>, in which case
	 *            the suffix <code>".tmp"</code> will be used
	 * @return The created file.
	 */
	default Path createTempFile(String prefix, String suffix) {
		return createTempFile(prefix, suffix, null);
	}

	/**
	 * Creates a temporary file in the specified directory. The file is marked as to be deleted on exit.
	 *
	 * @param prefix The prefix string to be used in generating the file's name; must be at least three characters long
	 * @param suffix The suffix string to be used in generating the file's name; may be <code>null</code>, in which case
	 *            the suffix <code>".tmp"</code> will be used
	 * @param directory The directory in which the file is to be created, or <code>null</code> if the default
	 *            temporary-file directory is to be used
	 * @return The created file.
	 */
	Path createTempFile(String prefix, String suffix, Path directory);
}
