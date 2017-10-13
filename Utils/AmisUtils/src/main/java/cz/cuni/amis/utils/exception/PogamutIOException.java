package cz.cuni.amis.utils.exception;

import java.util.logging.Logger;

/**
 * Ancestor of all communication exception that might arise from the Pogamut platform. Used
 * when dealing with classes like Socket / Reader / Writer etc.
 * @author Jimmy
 */
@SuppressWarnings("serial")
public class PogamutIOException extends PogamutException {
	
	/**
	 * Constructs a new exception with the specified detail message.
	 * <p><p>
	 * Not logging anything anywhere on its own.
	 * 
	 * @param message
	 * @param origin which object does produced the exception
	 */
	public PogamutIOException(String message, Object origin) {
		super(message, origin);
	}
	
	/**
	 * Constructs a new exception with the specified detail message.
	 * <p><p>
	 * Not logging anything anywhere on its own.
	 * 
	 * @param message
	 * @param cause
	 */
	public PogamutIOException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new exception with the specified cause.
	 * <p><p>
	 * Not logging anything anywhere on its own.
	 * 
	 * @param cause
	 * @param origin
	 */
	public PogamutIOException(Throwable cause, Object origin) {
		super(cause, origin);
	}
	
	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * <p><p>
	 * Not logging anything anywhere on its own.
	 * 
	 * @param message
	 * @param cause
	 * @param origin which object does produced the exception
	 */
	public PogamutIOException(String message, Throwable cause, Object origin) {
		super(message, cause, origin);
	}

	
	/**
	 * Constructs a new exception with the specified detail message.
	 * <p><p>
	 * Logs the exception via specified Logger.
	 * 
	 * @param message
	 * @param log
	 * @param origin which object does produced the exception
	 */
	public PogamutIOException(String message, Logger log, Object origin){
		super(message, log, origin);
	}
	
	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * <p><p>
	 * Logs the exception via specified Logger.
	 * 
	 * @param message
	 * @param cause
	 * @param origin which object does produced the exception
	 */
	public PogamutIOException(Throwable cause, Logger log, Object origin) {
		super(cause, log, origin);
	}
	
	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * <p><p>
	 * Logs the exception via specified Logger.
	 * 
	 * @param message
	 * @param cause
	 * @param origin which object does produced the exception
	 */
	public PogamutIOException(String message, Throwable cause, Logger log, Object origin) {
		super(message, cause, log, origin);
	}
	
}
