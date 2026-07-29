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
			case CONFIGURATION -> getConfigurationDirectoryPath();
			case CACHE -> getCacheDirectoryPath();
			case DATA -> getDataDirectoryPath();
			case EXPORT -> getExportDirectoryPath();
			case IMAGES -> getImagesDirectoryPath();
			case LOGS -> getLogsDirectoryPath();
			case RUNTIME -> getRuntimeDirectoryPath();
			case STATE -> getStateDirectoryPath();
			case TEMP -> getTempDirectoryPath();
			case THUMBNAILS -> getThumbnailsDirectoryPath();
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
	public final Path getCacheDirectory() {
		return create(getCacheDirectoryPath());
	}

	protected abstract Path getCacheDirectoryPath();

	@Override
	public final Path getConfigurationDirectory() {
		return create(getConfigurationDirectoryPath());
	}

	protected abstract Path getConfigurationDirectoryPath();

	@Override
	public final Path getDataDirectory() {
		return create(getDataDirectoryPath());
	}

	protected abstract Path getDataDirectoryPath();

	@Override
	public final Path getExportDirectory() {
		return create(getExportDirectoryPath());
	}

	protected Path getExportDirectoryPath() {
		return getTempDirectory().resolve("exports");
	}

	@Override
	public final Path getImagesDirectory() {
		return create(getImagesDirectory());
	}

	protected Path getImagesDirectoryPath() {
		return getDataDirectory().resolve("images");
	}

	@Override
	public final Path getLogsDirectory() {
		return create(getLogsDirectoryPath());
	}

	protected Path getLogsDirectoryPath() {
		return getStateDirectory().resolve("logs");
	}

	@Override
	public final Path getRuntimeDirectory() {
		return create(getRuntimeDirectoryPath());
	}

	protected abstract Path getRuntimeDirectoryPath();

	@Override
	public final Path getStateDirectory() {
		return create(getStateDirectoryPath());
	}

	protected abstract Path getStateDirectoryPath();

	@Override
	public final Path getTempDirectory() {
		return create(getTempDirectoryPath());
	}

	protected Path getTempDirectoryPath() {
		String user = SystemUtils.USER_NAME;
		return Path.of(SystemUtils.JAVA_IO_TMPDIR)
				.resolve(appName + "_" + user);
	}

	@Override
	public final Path getThumbnailsDirectory() {
		return create(getThumbnailsDirectoryPath());
	}

	protected Path getThumbnailsDirectoryPath() {
		return getCacheDirectory().resolve("thumbnails");
	}
}
