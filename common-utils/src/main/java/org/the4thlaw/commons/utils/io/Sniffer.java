package org.the4thlaw.commons.utils.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An utility that sniffs part of a file to try to identify it.
 */
public class Sniffer {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);
    private static final int SNIFF_DEFAULT_BUFFER = 512;
    
    /**
	 * Sniffs the first bytes of a file, and tries to match them to a specific pattern.
	 * 
	 * @param file The file to sniff
	 * @param byteCount The maximum number of bytes to sniff
	 * @param charset The excepted character set of the file
	 * @param pattern The pattern to match
	 * @return <code>true</code> if sniffing was successful and the sniffed content matches the pattern.
	 *         <code>false</code> otherwise.
	 */
	public static boolean sniffFile(Path file, int byteCount, Charset charset, Pattern pattern) {
		try (InputStream input = Files.newInputStream(file)) {
			byte[] buffer = new byte[byteCount];
			int count = IOUtils.read(input, buffer);
			LOGGER.debug("Sniffed the first {} bytes of {}", count, file);

			String readString = new String(buffer, charset);
			if (pattern.matcher(readString).matches()) {
				return true;
			}
		} catch (IOException e) {
			LOGGER.warn("Failed to sniff {}, assuming no match", file, e);
		}
		return false;
	}

	/**
	 * Sniffs the first few bytes of a file, and tries to match them as UTF-8 to a specific pattern.
	 * 
	 * @param file The file to sniff
	 * @param pattern The pattern to match
	 * @return <code>true</code> if sniffing was successful and the sniffed content matches the pattern.
	 *         <code>false</code> otherwise.
	 */
	public static boolean sniffFile(Path file, Pattern pattern) {
		return sniffFile(file, SNIFF_DEFAULT_BUFFER, StandardCharsets.UTF_8, pattern);
	}
}
