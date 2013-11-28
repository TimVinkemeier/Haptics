package de.timvinkemeier.haptics.exceptions;

// TODO: Auto-generated Javadoc
/**
 * The Class AmbiguousDefinitionException.
 */
public class AmbiguousDefinitionException extends Exception {

	/**
	 * Default UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new ambiguous definition exception.
	 */
	public AmbiguousDefinitionException() {
		super();
	}

	/**
	 * Instantiates a new ambiguous definition exception.
	 *
	 * @param message the message
	 */
	public AmbiguousDefinitionException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new ambiguous definition exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 * @param enableSuppression the enable suppression
	 * @param writableStackTrace the writable stack trace
	 */
	public AmbiguousDefinitionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Instantiates a new ambiguous definition exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public AmbiguousDefinitionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new ambiguous definition exception.
	 *
	 * @param cause the cause
	 */
	public AmbiguousDefinitionException(Throwable cause) {
		super(cause);
	}

}
