package org.the4thlaw.commons.services.io;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

import org.the4thlaw.commons.services.io.impl.MacOsXDirectoryService;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryServiceFactory.class);

    private Function<String, ? extends IDirectoryService> implementation;
    private String appName;

    public DirectoryServiceFactory withAppName(String appName) {
        this.appName = appName;
        return this;
    }

    public DirectoryServiceFactory withImplementation(Class<? extends IDirectoryService> implementation) {
        Constructor<? extends IDirectoryService> cons;
        try {
            cons = implementation.getConstructor(String.class);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new DirectoryException("Failed to find a matching constructor", e);
        }
        this.implementation = (String n) -> {
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
        if (this.implementation != null) {
            // Never override a previously configured implementation
            return this;
        }

        if (SystemUtils.IS_OS_WINDOWS) {
            this.implementation = WindowsDirectoryService::new;
        } else if (SystemUtils.IS_OS_MAC_OSX) {
            this.implementation = MacOsXDirectoryService::new;
        } else if (SystemUtils.IS_OS_UNIX) {
            this.implementation = XdgDirectoryService::new;
        } else {
            throw new IllegalStateException("Couldn't detect the OS");
        }
        return this;
    }

    public DirectoryServiceFactory autoDetectLegacy() {
        if (SystemUtils.IS_OS_UNIX) {
            if (StringUtils.isBlank(appName)) {
                throw new IllegalStateException("The application name must be set before auto-detecting legacy mode");
            }
            IDirectoryService rooted = new RootedDirectoryService(appName);
            Path rootedDataDir = rooted.getDirectory(StandardDirectory.DATA, false);
            if (Files.exists(rootedDataDir)) {
                LOGGER.info("Auto-detected the legacy RootedDirectoryService due to the existence of {}",
                        rootedDataDir);
                this.implementation = RootedDirectoryService::new;
            }
        }
        return this;
    }

    private void validate() {
        if (StringUtils.isBlank(appName)) {
            throw new IllegalStateException("Illegal app name: " + appName);
        }
        if (implementation == null) {
            throw new IllegalStateException("Missing implementation class");
        }
    }

    public IDirectoryService build() {
        validate();
        IDirectoryService service = implementation.apply(appName);
        LOGGER.info("Directory service class is {}", service.getClass().getCanonicalName());
        return service;
    }
}
