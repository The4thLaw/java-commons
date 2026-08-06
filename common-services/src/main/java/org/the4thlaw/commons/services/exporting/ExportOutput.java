package org.the4thlaw.commons.services.exporting;

import java.nio.file.Path;

/**
 * Represents the output of an export.
 * 
 * @param file The {@link Path} containing the data.
 * @param fileName The name of the file to provide to the user.
 * @since 1.5
 */
public record ExportOutput(Path file, String fileName) {
}
