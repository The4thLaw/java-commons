package org.the4thlaw.commons.exception;

/**
 * Common interface for application error codes.
 */
public interface ErrorCode {
	/**
	 * Gets the numeric code for the error.
	 *
	 * @return The numeric code.
	 */
	int getNumericCode();
}
