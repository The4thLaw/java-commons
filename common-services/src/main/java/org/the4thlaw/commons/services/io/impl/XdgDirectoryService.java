package org.the4thlaw.commons.services.io.impl;

import java.nio.file.Path;
import java.util.Locale;

import org.the4thlaw.commons.services.io.DirectoryException;
import org.the4thlaw.commons.services.io.StandardDirectory;

import org.apache.commons.lang3.SystemUtils;

/**
 * XDG-compliant version of the directory service.
 * <ul>
 * <li>https://specifications.freedesktop.org/basedir/latest/</li>
 * <li>https://gist.github.com/roalcantara/107ba66dfa3b9d023ac9329e639bc58c</li>
 * <li>https://wiki.archlinux.org/title/XDG_Base_Directory</li>
 * </ul>
 * 
 * @since 1.5
 */
public class XdgDirectoryService extends BaseDirectoryService {
	private static record XdgData(String variable, Path defaultValue) {
	}

	public XdgDirectoryService(String appName) {
		super(appName.toLowerCase(Locale.ROOT));
	}

	private static XdgData getXdgData(StandardDirectory directory) {
		switch (directory) {
			case CACHE:
				return new XdgData("XDG_CACHE_HOME", HOME.resolve(".cache"));
			case CONFIGURATION:
				return new XdgData("XDG_CONFIG_HOME", HOME.resolve(".config"));
			case DATA:
				return new XdgData("XDG_DATA_HOME", HOME.resolve(".local").resolve("share"));
			case RUNTIME:
				return new XdgData("XDG_RUNTIME_DIR", null);
			case STATE:
				return new XdgData("XDG_STATE_HOME", HOME.resolve(".local").resolve("state"));
			case TEMP:
				return new XdgData("TMPDIR", Path.of(SystemUtils.JAVA_IO_TMPDIR));
			default:
				throw new UnsupportedOperationException("Unsupported standard directory: " + directory);
		}
	}

	private static final Path getXdgDirectory(StandardDirectory directory) {
		XdgData data = getXdgData(directory);

		String env = System.getenv(data.variable());
		if (env != null) {
			return Path.of(env);
		}
		Path def = data.defaultValue();
		if (def == null) {
			throw new DirectoryException("No value for environment variable "
					+ data.variable() + " and no default value available");
		}
		return def;
	}

	private final Path getAppXdgDirectory(StandardDirectory directory) {
		return getXdgDirectory(directory).resolve(appName);
	}

	@Override
	public Path getCacheDirectory() {
		return getAppXdgDirectory(StandardDirectory.CACHE);
	}

	@Override
	public Path getConfigurationDirectory() {
		return getAppXdgDirectory(StandardDirectory.CONFIGURATION);
	}

	@Override
	public Path getDataDirectory() {
		return getAppXdgDirectory(StandardDirectory.DATA);
	}

	@Override
	public Path getRuntimeDirectory() {
		return getAppXdgDirectory(StandardDirectory.RUNTIME);
	}

	@Override
	public Path getStateDirectory() {
		return getAppXdgDirectory(StandardDirectory.STATE);
	}

	@Override
	public Path getTempDirectory() {
		String user = SystemUtils.USER_NAME;
		return getAppXdgDirectory(StandardDirectory.TEMP)
				.resolve(appName + "_" + user);
	}
}
