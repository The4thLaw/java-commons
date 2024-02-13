package org.the4thlaw.utils.image;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ImageUtils}.
 */
class ImageUtilsTest {
	private static File getFile(String resourcePath) throws URISyntaxException {
		URL url = ImageUtilsTest.class.getResource("/image-42x16.png");
		assertThat(url).withFailMessage(() -> resourcePath + " not found").isNotNull();
		return Paths.get(url.toURI()).toFile();
	}

	/**
	 * Tests {@link ImageUtils#getImageWidth(File)}.
	 * 
	 * @throws IOException In case of error while getting the image width.
	 * @throws URISyntaxException In case of error while getting the test data.
	 */
	@Test
	void getImageWidth() throws IOException, URISyntaxException {
		assertThat(ImageUtils.getImageWidth(getFile("/image-42x16.jpg"))).isEqualTo(42);
		assertThat(ImageUtils.getImageWidth(getFile("/image-42x16.png"))).isEqualTo(42);
	}
}
