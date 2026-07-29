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
}
