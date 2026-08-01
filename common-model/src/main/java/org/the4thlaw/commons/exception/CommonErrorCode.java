package org.the4thlaw.commons.exception;

public enum CommonErrorCode implements ErrorCode {
	/** The system encountered an I/O error during export. */
	EXPORT_IO_ERROR(16000),
	/** An assumption about the database could not be met. */
	EXPORT_DB_CONSISTENCY_ERROR(16001),
	/** The system encountered a parse error during export. */
	EXPORT_XML_ERROR(16002),
	/** Generic I/O operation error. */
	IO_GENERIC_ERROR(20000);

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
