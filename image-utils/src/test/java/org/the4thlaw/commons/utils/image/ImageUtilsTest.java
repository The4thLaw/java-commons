package org.the4thlaw.commons.utils.image;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

import org.the4thlaw.commons.utils.io.FileUtils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests for {@link ImageUtils}.
 */
class ImageUtilsTest {
	private static Path getFile(String resourcePath) throws URISyntaxException {
		URL url = ImageUtilsTest.class.getResource(resourcePath);
		assertThat(url).withFailMessage(() -> resourcePath + " not found").isNotNull();
		return Paths.get(url.toURI());
	}

	/**
	 * Tests {@link ImageUtils#getImageWidth(File)}.
	 * 
	 * @throws IOException In case of error while getting the image width.
	 * @throws URISyntaxException In case of error while getting the test data.
	 */
	@Test
	void getImageWidth() throws IOException, URISyntaxException {
		assertThat(ImageUtils.getImageWidth(getFile("/image-42x16.jpg").toFile())).isEqualTo(42);
		assertThat(ImageUtils.getImageWidth(getFile("/image-42x16.png").toFile())).isEqualTo(42);
	}

	@ParameterizedTest
	@ValueSource(strings =	{ "/jpg-srgb.jpg", "/png-srgb.png", "/jpg-cmyk.jpg", "/webp-srgb.webp" })
	void resize(String resourcePath) throws IOException, URISyntaxException {
		Path output = null;
		try {
			output = Files.createTempFile("ImageUtilsTest-" + resourcePath.substring(1) + "-", ".jpg");
			Path o = output;
			ImageUtils.resize(getFile(resourcePath), 100, (f) -> o);
		} finally {
			FileUtils.deleteQuietly(output);
		}
	}
}
