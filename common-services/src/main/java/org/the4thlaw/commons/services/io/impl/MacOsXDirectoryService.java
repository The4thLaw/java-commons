package org.the4thlaw.commons.services.io.impl;

import java.nio.file.Path;

/**
 * A directory service implementation where data follows the Apple standards.
 * 
 * @since 1.5
 */
public class MacOsXDirectoryService extends BaseDirectoryService {

    public MacOsXDirectoryService(String appName) {
       super(appName);
    }

    @Override
    public Path getCacheDirectory() {
        return HOME.resolve("Library").resolve("Caches").resolve(appName);
    }

    @Override
    public Path getConfigurationDirectory() {
        return HOME.resolve("Library").resolve("Preferences").resolve(appName);
    }

    @Override
    public Path getDataDirectory() {
        return HOME.resolve("Library").resolve("Application Support").resolve(appName);
    }

    @Override
    public Path getRuntimeDirectory() {
        return getDataDirectory().resolve("runtime");
    }

    @Override
    public Path getStateDirectory() {
        return getDataDirectory().resolve("state");
    }
}
