package org.the4thlaw.commons.services.importing;

import java.io.InputStream;

import org.the4thlaw.commons.exception.CommonException;

/**
 * This service allows importing app data to various formats.
 * <p>
 * Importers need to be registered.
 * </p>
 * @since 1.5
 */
public interface IImportService {

	/**
	 * Registers an importer that supports a new file format.
	 *
	 * @param importer The importer to register.
	 */
	void registerImporter(IImporter importer);

	/**
	 * Performs the import of a file, assuming a supporting importer has been registered.
	 *
	 * @param originalFilename The original file name, as uploaded.
	 * @param content The uploaded content.
	 * @throws CommonException In case of error during import.
	 */
	void importFile(String originalFilename, InputStream content) throws CommonException;

}
