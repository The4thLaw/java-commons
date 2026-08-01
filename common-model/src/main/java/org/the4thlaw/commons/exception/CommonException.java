package org.the4thlaw.commons.exception;

import java.util.List;

/**
 * The4thLaw commons standard exception.
 */
public class CommonException extends Exception implements ICommonException  {
	private final ErrorCode code;

	/**
	 * Creates a new exception.
	 *
	 * @param code The code of the error.
	 * @param details Any details that could assist debugging.
	 */
	public CommonException(ErrorCode code, String... details) {
		super(toMessage(code, details));
		this.code = code;
	}

	/**
	 * Creates a new exception.
	 *
	 * @param code The code of the error.
	 * @param cause The cause of the error.
	 * @param details Any details that could assist debugging.
	 */
	public CommonException(ErrorCode code, Throwable cause, String... details) {
		super(toMessage(code, details), cause);
		this.code = code;
	}

	/**
	 * Converts the error code and potential details to a single error message.
	 *
	 * @param code The code of the error.
	 * @param details Any details that could assist debugging.
	 * @return An error message.
	 */
	protected static String toMessage(ErrorCode code, String... details) {
		if (code == null) {
			throw new IllegalArgumentException("Cannot create an exception with a null code");
		}
		if (details == null || details.length < 1) {
			return code.toString();
		}
		return code.toString() + " " + List.of(details);
	}

	@Override
	public boolean is(ErrorCode targetCode) {
		return code.equals(targetCode);
	}
}
