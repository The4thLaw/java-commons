package org.the4thlaw.commons.services.io.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class BaseDirectoryService {
	/**
	 * Creates a directory if it doesn't exist. Manages exceptions.
	 * 
	 * @return The directory. Guaranteed to exist.
	 */
	protected final Path create(Path directory) {
		try {
			return Files.createDirectories(directory);
		} catch (IOException e) {
			// TODO: create a specific exception for this
			throw new RuntimeException("Failed to retriev the directory at " + directory, e);
		}
	}
}
