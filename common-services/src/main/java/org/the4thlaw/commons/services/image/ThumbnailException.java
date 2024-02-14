package org.the4thlaw.commons.services.image;

/**
 * Generic exception for Thumbnail issues.
 */
public class ThumbnailException extends RuntimeException {
	/**
	 * Constructs a new exception with the specified detail message. The cause is not initialized, and may subsequently
	 * be initialized by a call to {@link #initCause}.
	 *
	 * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()}
	 *            method.
	 */
	public ThumbnailException(String message) {
		super(message);
	}

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * <p>
	 * Note that the detail message associated with {@code cause} is <i>not</i> automatically incorporated in this
	 * exception's detail message.
	 * </p>
	 *
	 * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
	 * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method). (A {@code null}
	 *            value is permitted, and indicates that the cause is nonexistent or unknown.)
	 */
	public ThumbnailException(String message, Throwable cause) {
		super(message, cause);
	}
}