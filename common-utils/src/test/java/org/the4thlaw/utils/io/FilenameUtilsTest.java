package org.the4thlaw.utils.io;

import static org.assertj.core.api.Assertions.assertThat;
import static org.the4thlaw.utils.io.FilenameUtils.getFileExtension;

import org.the4thlaw.utils.image.ImageUtils;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ImageUtils}.
 */
class FilenameUtilsTest {
    @Test
    void testGetFileExtension() {
        assertThat(getFileExtension("foo.jpg")).isEqualTo("jpg");
        assertThat(getFileExtension("foo.JPG")).isEqualTo("jpg");
        assertThat(getFileExtension("foo./etc/passwd")).isNull();
        assertThat(getFileExtension("foo.mp3")).isEqualTo("mp3");
		assertThat(getFileExtension("foo.mp3$")).isEqualTo("mp3");
        assertThat(getFileExtension(null)).isNull();
        assertThat(getFileExtension("foo")).isNull();
    }
}
