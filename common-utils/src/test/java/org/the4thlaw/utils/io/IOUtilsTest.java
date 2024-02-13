package org.the4thlaw.utils.io;

import java.io.Closeable;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests for {@link IOUtils}.
 */
class IOUtilsTest {
    /**
	 * Tests {@link IOUtils#closeQuietly(Closeable)}.
	 *
	 * @throws IOException should never be thrown.
	 */
	@Test
	void testCloseQuietlyCloseableNormal() throws IOException {
		IOUtils.closeQuietly((Closeable) null);
		Closeable closeable = Mockito.mock(Closeable.class);
		IOUtils.closeQuietly(closeable);
		Mockito.verify(closeable, Mockito.times(1)).close();
	}

	/**
	 * Tests {@link IOUtils#closeQuietly(Closeable)} when the closeable throws an Exception.
	 *
	 * @throws IOException should never be thrown.
	 */
	@Test
	void testCloseQuietlyCloseableException() throws IOException {
		Closeable closeable = Mockito.mock(Closeable.class);
		Mockito.doThrow(IOException.class).when(closeable).close();
		IOUtils.closeQuietly(closeable);
		Mockito.verify(closeable, Mockito.times(1)).close();
	}
}
