package org.the4thlaw.commons.services.io.impl;

import java.nio.file.Path;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

/**
 * A directory service implementation where all data is rooted in a parent directory.
 * 
 * @since 1.5
 */
public class RootedDirectoryService extends BaseDirectoryService {
    private final Path root;

    public RootedDirectoryService(String appName) {
        super(appName);

        Path root;
        if (SystemUtils.IS_OS_WINDOWS) {
            // On Windows, try to send the settings to the Application Data folder
            // rather than just the home, if possible
            String baseDirectory = System.getenv("APPDATA");
            if (StringUtils.isBlank(baseDirectory)) {
                root = HOME;
            } else {
                root = Path.of(baseDirectory);
            }
        } else if (SystemUtils.IS_OS_MAC_OSX) {
            // https://www.google.com/search?q=os+x+"where+to+put+files"
            // https://developer.apple.com/library/mac/#documentation/General/Conceptual/
            // MOSXAppProgrammingGuide/AppRuntime/AppRuntime.html

            root = HOME.resolve("Library").resolve("Application Support");
        } else {
            root = HOME;
        }

        String dirName = SystemUtils.IS_OS_WINDOWS ? this.appName : "." + this.appName;
        this.root = root.resolve(dirName);
    }

    public RootedDirectoryService(Path root, String appName) {
        super(appName);
        String dirName = SystemUtils.IS_OS_WINDOWS ? this.appName : "." + this.appName;
        this.root = root.resolve(dirName);
    }

    @Override
    public Path getCacheDirectory() {
        return root.resolve("cache");
    }

    @Override
    public Path getConfigurationDirectory() {
        return root;
    }

    @Override
    public Path getDataDirectory() {
        return root;
    }

    @Override
    public Path getExportDirectory() {
        return root.resolve("exports");
    }

    @Override
    public Path getRuntimeDirectory() {
        return root.resolve("runtime");
    }

    @Override
    public Path getStateDirectory() {
        return root.resolve("state");
    }

    @Override
    public Path getThumbnailsDirectory() {
        return root.resolve("thumbnails");
    }
}
