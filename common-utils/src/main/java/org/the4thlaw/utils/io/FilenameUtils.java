package org.the4thlaw.utils.io;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Utilities to work on file names.
 */
public final class FilenameUtils {
	private static final Pattern FILE_EXT_EXCLUSIONS = Pattern.compile("[^A-Za-z0-9]");

	private FilenameUtils() {
		// No instance
	}

	/**
	 * Returns the extension of a file.
	 * <p>
	 * Special characters are stripped out to avoid potential injections
	 * </p>
	 * 
	 * @param fileName The name of the file.
	 * @return The lowercase extension, or {@code null} if there was no extension.
	 */
	public static String getFileExtension(String fileName) {
		if (fileName == null) {
			return null;
		}
		String baseExt = org.apache.commons.io.FilenameUtils.getExtension(fileName);
		if (baseExt.isEmpty()) {
			return null;
		}
		return FILE_EXT_EXCLUSIONS.matcher(baseExt).replaceAll("").toLowerCase(Locale.ROOT);
	}
}
