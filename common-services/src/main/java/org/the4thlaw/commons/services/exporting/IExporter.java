package org.the4thlaw.commons.services.exporting;

import java.nio.file.Path;

import org.the4thlaw.commons.exception.CommonException;


public interface IExporter {

	/**
	 * Exports the library to a temporary file.
	 *
	 * @return The exported data
	 * @throws CommonException In case of error during export.
	 */
	Path export() throws CommonException;

	/**
	 * Returns the expected extension of the exported file.
	 *
	 * @param withResources Defines whether the extension applies to an export with or without resources.
	 * @return The extension (without heading ".")
	 */
	String getExtension(boolean withResources);
}
