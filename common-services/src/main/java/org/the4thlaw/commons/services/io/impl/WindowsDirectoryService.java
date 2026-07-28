package org.the4thlaw.commons.services.io.impl;

import java.nio.file.Path;

import org.apache.commons.lang3.SystemUtils;
/**
 * A directory service implementation where data follows the Microsoft standards.
 * 
 * @since 1.5
 */
public class WindowsDirectoryService extends RootedDirectoryService {
    public WindowsDirectoryService(String appName) {
        super(appName);
    }

    @Override
	public Path getTempDirectory() {
		String user = SystemUtils.USER_NAME;
		return Path.of(SystemUtils.JAVA_IO_TMPDIR)
				.resolve(appName + "_" + user);
	}
}
