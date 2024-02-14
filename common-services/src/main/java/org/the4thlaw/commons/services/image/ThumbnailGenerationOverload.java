package org.the4thlaw.commons.services.image;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception happening due to excessing requests while generating the thumbnail, if no surrogate could be found.
 */
@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class ThumbnailGenerationOverload extends ThumbnailException {
	private static final long serialVersionUID = -1903648607988955726L;

	/**
	 * Constructs a new exception with the specified detail message. The cause is not initialized, and may subsequently
	 * be initialized by a call to {@link #initCause}.
	 *
	 * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()}
	 *            method.
	 */
	public ThumbnailGenerationOverload(String message) {
		super(message);
	}
}
