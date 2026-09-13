package org.the4thlaw.commons.services.importing;

import java.nio.file.Path;

import org.the4thlaw.commons.exception.CommonException;


/**
 * Defines the contract for importers.
 * <p>
 * All importers must be stateless.
 * </p>
 * 
 * @since 1.5
 */
public interface IImporter {
	/**
	 * Checks whether the importer supports the provided file.
	 * 
	 * @param originalFilename The file name, as uploaded.
	 * @param file The content of the uploaded file (name is meaningless).
	 * @return <code>true</code> if the importer supports this file. Else, <code>false</code>.
	 * @throws CommonException In case of unrecoverable error during check.
	 */
	boolean supports(String originalFilename, Path file) throws CommonException;

	/**
	 * Performs the import.
	 * 
	 * @param originalFilename The file name, as uploaded.
	 * @param file The content of the uploaded file (name is meaningless).
	 * @throws CommonException In case of error during import.
	 */
	void importFile(String originalFilename, Path file) throws CommonException;
}
