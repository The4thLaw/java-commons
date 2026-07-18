package org.the4thlaw.commons.services.io.impl;

import java.nio.file.Path;
import java.util.Locale;

import org.the4thlaw.commons.services.io.IDirectoryService;
import org.the4thlaw.commons.services.io.StandardDirectory;


/**
 * XDG-compliant version of the directory service.
 * <ul>
 * <li>https://specifications.freedesktop.org/basedir/latest/</li>
 * <li>https://gist.github.com/roalcantara/107ba66dfa3b9d023ac9329e639bc58c</li>
 * <li>https://wiki.archlinux.org/title/XDG_Base_Directory</li>
 * </ul>
 */
public class XdgDirectoryService extends BaseDirectoryService implements IDirectoryService {
	private static record XdgData(String variable, Path defaultValue) {
	}

	private static final Path HOME = Path.of(System.getProperty("user.home"));

	private String appName;

	public XdgDirectoryService(String appName) {
		this.appName = appName.toLowerCase(Locale.ROOT);
	}

	private static XdgData getXdgData(StandardDirectory directory) {
		switch (directory) {
			case CONFIGURATION:
				return new XdgData("XDG_CONFIG_HOME", HOME.resolve(".config"));
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
		return data.defaultValue();
	}

	private final Path getAppXdgDirectory(StandardDirectory directory) {
		return create(getXdgDirectory(directory).resolve(appName));
	}

	@Override
	public Path getConfigurationDirectory() {
		return getAppXdgDirectory(StandardDirectory.CONFIGURATION);
	}

}
