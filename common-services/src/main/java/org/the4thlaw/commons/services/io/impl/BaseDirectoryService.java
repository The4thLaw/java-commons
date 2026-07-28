package org.the4thlaw.commons.services.io.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import org.the4thlaw.commons.services.io.DirectoryException;
import org.the4thlaw.commons.services.io.IDirectoryService;
import org.the4thlaw.commons.services.io.StandardDirectory;

import org.apache.commons.lang3.SystemUtils;

/**
 * Base class for directory services, providing utilities and reference implementations.
 * 
 * @since 1.5
 */
public abstract class BaseDirectoryService implements IDirectoryService {
	protected static final Path HOME = Path.of(SystemUtils.USER_HOME);

	protected final String appName;

	public BaseDirectoryService(String appName) {
		this.appName = (SystemUtils.IS_OS_WINDOWS || SystemUtils.IS_OS_MAC_OSX)
				? appName
				: appName.toLowerCase(Locale.ROOT);
	}

	public Path getDirectory(StandardDirectory directory, boolean autoCreate) {
		Path dirPath = switch (directory) {
			case CONFIGURATION -> getConfigurationDirectory();
			case CACHE -> getCacheDirectory();
			case DATA -> getDataDirectory();
			case EXPORT -> getExportDirectory();
			case IMAGES -> getImagesDirectory();
			case LOGS -> getLogsDirectory();
			case RUNTIME -> getRuntimeDirectory();
			case STATE -> getStateDirectory();
			case TEMP -> getTempDirectory();
			case THUMBNAILS -> getThumbnailsDirectory();
			default -> throw new DirectoryException("Unsupported standard directory: " + directory);
		};
		if (autoCreate) {
			return create(dirPath);
		}
		return dirPath;
	}

	/**
	 * Creates a directory if it doesn't exist. Manages exceptions.
	 * 
	 * @return The directory. Guaranteed to exist.
	 */
	private final Path create(Path directory) {
		try {
			return Files.createDirectories(directory);
		} catch (IOException e) {
			throw new DirectoryException("Failed to retrieve the directory at " + directory, e);
		}
	}

	@Override
	public Path getExportDirectory() {
		return getTempDirectory().resolve("exports");
	}

	@Override
	public Path getImagesDirectory() {
		return getDataDirectory().resolve("images");
	}

	@Override
	public Path getLogsDirectory() {
		return getStateDirectory().resolve("logs");
	}

	@Override
	public Path getTempDirectory() {
		String user = SystemUtils.USER_NAME;
		return Path.of(SystemUtils.JAVA_IO_TMPDIR)
				.resolve(appName + "_" + user);
	}

	@Override
	public Path getThumbnailsDirectory() {
		return getCacheDirectory().resolve("thumbnails");
	}
}
