package org.the4thlaw.commons.services.io;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

import org.the4thlaw.commons.services.io.impl.MacOsXDirectoryService;
import org.the4thlaw.commons.services.io.impl.PortableDirectoryService;
import org.the4thlaw.commons.services.io.impl.RootedDirectoryService;
import org.the4thlaw.commons.services.io.impl.WindowsDirectoryService;
import org.the4thlaw.commons.services.io.impl.XdgDirectoryService;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for the directory services.
 * 
 * @since 1.5
 */
public class DirectoryServiceFactory {
    // TODO: add method to create temporary files

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryServiceFactory.class);

    private String appName;
    private String systemProperty;
    private Function<String, ? extends IDirectoryService> desiredImplementation;
    private boolean isAutoDetect;
    private boolean isAutoDetectLegacy;

    public DirectoryServiceFactory withAppName(String appName) {
        this.appName = appName;
        return this;
    }

    public DirectoryServiceFactory withPortableSystemPropertyOverride(String systemProperty) {
        this.systemProperty = systemProperty;
        return this;
    }

    public DirectoryServiceFactory withImplementation(Class<? extends IDirectoryService> implementation) {
        Constructor<? extends IDirectoryService> cons;
        try {
            cons = implementation.getConstructor(String.class);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new DirectoryException("Failed to find a matching constructor", e);
        }
        this.desiredImplementation = n -> {
            try {
                return cons.newInstance(n);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                throw new DirectoryException("Failed to instantiate the desired implementation", e);
            }
        };
        return this;
    }

    public DirectoryServiceFactory autoDetectImplementation() {
        isAutoDetect = true;
        return this;
    }

    public DirectoryServiceFactory autoDetectLegacy() {
        isAutoDetectLegacy = true;
        return this;
    }

    public IDirectoryService build() {
        if (StringUtils.isBlank(appName)) {
            throw new IllegalStateException("Illegal app name: " + appName);
        }

        Function<String, ? extends IDirectoryService> implementation = null;

        if (StringUtils.isNotBlank(systemProperty)) {
            String propValue = System.getProperty(systemProperty);

            if (StringUtils.isNotBlank(propValue)) {
                Path desiredPath = Path.of(propValue);
                implementation = n -> new PortableDirectoryService(desiredPath, n);
            }
        }

        if (implementation == null && desiredImplementation != null) {
            implementation = desiredImplementation;
        } 
        
        if (implementation == null && isAutoDetectLegacy && SystemUtils.IS_OS_UNIX) {
            IDirectoryService rooted = new RootedDirectoryService(appName);
            Path rootedDataDir = rooted.getDirectory(StandardDirectory.DATA, false);
            if (Files.exists(rootedDataDir)) {
                LOGGER.info("Auto-detected the legacy RootedDirectoryService due to the existence of {}",
                        rootedDataDir);
                implementation = RootedDirectoryService::new;
            }
        }

        if (implementation == null && isAutoDetect) {
            if (SystemUtils.IS_OS_WINDOWS) {
                implementation = WindowsDirectoryService::new;
            } else if (SystemUtils.IS_OS_MAC_OSX) {
                implementation = MacOsXDirectoryService::new;
            } else if (SystemUtils.IS_OS_UNIX) {
                implementation = XdgDirectoryService::new;
            } else {
                throw new IllegalStateException("Couldn't detect the OS");
            }
        }

        if (implementation == null) {
            throw new IllegalStateException("Missing implementation class");
        }

        IDirectoryService service = implementation.apply(appName);
        LOGGER.info("Directory service class is {}", service.getClass().getCanonicalName());
        return service;
    }
}
