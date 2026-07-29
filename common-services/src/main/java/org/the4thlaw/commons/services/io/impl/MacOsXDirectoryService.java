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
    protected Path getCacheDirectoryPath() {
        return HOME.resolve("Library").resolve("Caches").resolve(appName);
    }

    @Override
    protected Path getConfigurationDirectoryPath() {
        return HOME.resolve("Library").resolve("Preferences").resolve(appName);
    }

    @Override
    protected Path getDataDirectoryPath() {
        return HOME.resolve("Library").resolve("Application Support").resolve(appName);
    }

    @Override
    protected Path getRuntimeDirectoryPath() {
        return getDataDirectory().resolve("runtime");
    }

    @Override
    protected Path getStateDirectoryPath() {
        return getDataDirectory().resolve("state");
    }
}
