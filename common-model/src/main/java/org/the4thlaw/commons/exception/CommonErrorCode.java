package org.the4thlaw.commons.exception;

/**
 * Error codes for the common exception.
 * @since 1.5
 */
public enum CommonErrorCode implements ErrorCode {
	/** This import format is not supported. */
	IMPORT_FORMAT_NOT_SUPPORTED(11000),
	/** The system encountered an I/O error during import. */
	IMPORT_IO_ERROR(11001),
	/** The system encountered a parse error during import. */
	IMPORT_PARSE_ERROR(11002),
	/** The system encountered an error while restoring the images. */
	IMPORT_IMAGES_ERROR(11003),
	/** This version of the schema is not supported by the application. */
	IMPORT_WRONG_SCHEMA(11004),
	/** The system encountered an I/O error during export. */
	EXPORT_IO_ERROR(16000),
	/** An assumption about the database could not be met. */
	EXPORT_DB_CONSISTENCY_ERROR(16001),
	/** The system encountered a parse error during export. */
	EXPORT_XML_ERROR(16002),
	/** Generic I/O operation error. */
	IO_GENERIC_ERROR(20000),
	/** There was an issue during a file compression. */
	IO_COMPRESSION_ERROR(20001);

	private final int numericCode;

	CommonErrorCode(int numericCode) {
		this.numericCode = numericCode;
	}

	@Override
	public int getNumericCode() {
		return numericCode;
	}

	@Override
	public String toString() {
		return "COMMON-ERR-" + numericCode + ": " + name();
	}
}
