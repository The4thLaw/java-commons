package org.the4thlaw.commons.exception;

/**
 * The4thLaw commons unchecked exception.
 */
public class CommonRuntimeException extends RuntimeException implements ICommonException {
	private final ErrorCode code;

	/**
	 * Creates a new exception.
	 *
	 * @param code The code of the error.
	 * @param details Any details that could assist debugging.
	 */
	public CommonRuntimeException(ErrorCode code, String... details) {
		super(CommonException.toMessage(code, details));
		this.code = code;
	}

	/**
	 * Creates a new exception.
	 *
	 * @param code The code of the error.
	 * @param cause The cause of the error.
	 * @param details Any details that could assist debugging.
	 */
	public CommonRuntimeException(ErrorCode code, Throwable cause, String... details) {
		super(CommonException.toMessage(code, details), cause);
		this.code = code;
	}

	@Override
	public boolean is(ErrorCode targetCode) {
		return code.equals(targetCode);
	}
}
