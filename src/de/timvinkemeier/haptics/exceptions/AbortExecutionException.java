package de.timvinkemeier.haptics.exceptions;

// TODO: Auto-generated Javadoc
/**
 * The Class AbortExecutionException.
 */
public class AbortExecutionException extends Exception {

	/**
	 * Default UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new abort execution exception.
	 */
	public AbortExecutionException() {
		super();
	}

	/**
	 * Instantiates a new abort execution exception.
	 *
	 * @param message the message
	 */
	public AbortExecutionException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new abort execution exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 * @param enableSuppression the enable suppression
	 * @param writableStackTrace the writable stack trace
	 */
	public AbortExecutionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Instantiates a new abort execution exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public AbortExecutionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new abort execution exception.
	 *
	 * @param cause the cause
	 */
	public AbortExecutionException(Throwable cause) {
		super(cause);
	}

}
