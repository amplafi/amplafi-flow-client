package org.amplafi.flow.utils;

/**
 * Thrown in case of a problem with test generation
 */
public class GenerationException extends Exception {

	/**
	 * @param msg
	 *            - error message
	 */
	public GenerationException(String msg) {
		super(msg);
	}

	/**
	 * @param msg
	 *            - error message
	 * @param cause
	 *            - original exception
	 */
	public GenerationException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
