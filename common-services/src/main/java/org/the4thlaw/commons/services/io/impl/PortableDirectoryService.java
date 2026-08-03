package org.the4thlaw.commons.services.io.impl;

import java.nio.file.Path;

/**
 * A {@link RootedDirectoryService} implementation that keeps all data under a single directory
 * at a specific location, including temporary files.
 * <p>
 * Useful for portable mode.
 * </p>
 * 
 * @since 1.5
 */
public class PortableDirectoryService extends RootedDirectoryService {

	public PortableDirectoryService(Path root, String appName) {
		super(root, appName);
	}
	
	protected Path getTempDirectoryPath() {
		return getDataDirectory().resolve("temp");
	}
}
