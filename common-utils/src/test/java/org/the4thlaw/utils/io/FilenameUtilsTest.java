package org.the4thlaw.utils.io;

import static org.assertj.core.api.Assertions.assertThat;
import static org.the4thlaw.utils.io.FilenameUtils.getFileExtension;

import org.the4thlaw.utils.image.ImageUtils;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ImageUtils}.
 */
public class FilenameUtilsTest {
    @Test
    public void testGetFileExtension() {
        assertThat(getFileExtension("foo.jpg")).isEqualTo("jpg");
        assertThat(getFileExtension("foo./etc/passwd")).isNull();
        assertThat(getFileExtension("foo")).isNull();
    }
}
