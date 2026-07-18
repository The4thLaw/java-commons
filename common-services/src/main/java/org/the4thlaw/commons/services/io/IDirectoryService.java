package org.the4thlaw.commons.services.io;

import java.nio.file.Path;
import java.util.function.Function;

import org.the4thlaw.commons.services.io.impl.XdgDirectoryService;

import org.apache.commons.lang3.SystemUtils;

public interface IDirectoryService {
	/**
	 * Gets a standard directory. The returned directory is guaranteed to exist.
	 * 
	 * @param directory The directory to get.
	 */
	default Path getDirectory(StandardDirectory directory) {
		switch(directory) {
			case CONFIGURATION:
				return getConfigurationDirectory();
			case CACHE:
			case DATA:
			case EXPORT:
			case IMAGES:
			case RUNTIME:
			case STATE:
			case TEMP:
			case THUMBNAILS:
			default:
				// Do nothing
		}
		throw new UnsupportedOperationException("Unsupported standard directory: " + directory);
	}

	Path getConfigurationDirectory();

	/**
	 * Gets the service that is relevant for the current OS.
	 * 
	 * @return service builder. Takes one parameter as argument: the application name.
	 */
	default Function<String, ? extends IDirectoryService> autoDetectServiceClass() {
		if (SystemUtils.IS_OS_LINUX) {
			return XdgDirectoryService::new;
		}
		throw new IllegalStateException("Couldn't detect the OS");
	}
}
