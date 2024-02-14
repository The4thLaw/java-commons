package org.the4thlaw.commons.utils.io;

import java.io.Closeable;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * General purpose I/O utils.
 */
public class IOUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(IOUtils.class);
	private static final String STREAM_CLOSE_ERROR = "Failed to close stream";

	private IOUtils() {
	}

	/**
	 * Unconditionally close a {@link Closeable}.
	 * <p>
	 * Equivalent to {@link Closeable#close()}, except any exceptions will be logged and ignored. This is typically used
	 * in finally blocks.
	 * </p>
	 * <p>
	 * Similar to Apache Commons' method, but actually logs any error rather than discarding them.
	 * </p>
	 *
	 * @param closeable The object to close, may be null or already closed
	 */
	public static void closeQuietly(Closeable closeable) {
		if (closeable == null) {
			return;
		}
		try {
			closeable.close();
		} catch (IOException e) {
			LOGGER.warn(STREAM_CLOSE_ERROR, e);
		}
	}
}
