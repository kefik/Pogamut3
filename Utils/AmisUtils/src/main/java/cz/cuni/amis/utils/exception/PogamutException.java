package cz.cuni.amis.utils.exception;

import java.util.logging.Logger;

import cz.cuni.amis.utils.ExceptionToString;
import java.util.logging.Level;

/**
 * Ancestor of all exceptions of the Pogamut platform (except PogamutIOException and PogamutRuntimeException). It automatically logs the exception to the 
 * platform log of the respective agent were the exception occurred.
 * <p><p>
 * Note that Pogamut exceptions are storing the information whether they have been already logged + the instance
 * of the object that has created (thrown) them.
 * <p><p>
 * Because of the 'origin' object you don't have to bother to prefix your exceptions with origin's name.
 * All you have to care about is to have properly implemented toString() method of your objects
 * so the messages looks pretty.
 * <p><p>
 * By design-choice, every PogamutException is RuntimeException - note that if exception is thrown you will
 * probably just tore down everything or propagate it higher - no need to write throws to every method
 * just because we want to state that something might throw an exception.
 * 
 * @author Jimmy
 */
@SuppressWarnings("serial")
public class PogamutException extends RuntimeException {
	
	/**
	 * Whether the exception has been logged to the Platform logger
	 */
	private boolean hasBeenLogged = false;
	
	/**
	 * Object that has thrown this exception.
	 */
	private Object origin;
	
	/**
	 * Constructs exception based on the cause an origin.
	 * @param cause
	 * @param origin may be null
	 */
	public PogamutException(Throwable cause, Object origin) {
		super(cause);
		this.origin = origin;
	}
	
	/**
	 * Constructs a new exception with the specified detail message.
	 * <p><p>
	 * Not logging anything anywhere on its own.
	 * 
	 * @param message
	 * @param origin which object does produced the exception
	 */
	public PogamutException(String message, Object origin) {
		super(origin.toString() + ": " + message);
		this.origin = origin;
	}
	
	/**
	 * Constructs a new exception with the specified detail message.
	 * <p><p>
	 * Not logging anything anywhere on its own.
	 * 
	 * @param message
	 * @param cause
	 */
	public PogamutException(String message, Throwable cause) {
		super(message, cause);
		this.origin = cause;
	}
	
	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * <p><p>
	 * Not logging anything anywhere on its own.
	 * 
	 * @param message
	 * @param cause
	 * @param origin object that thrown the exception, may be null
	 */
	public PogamutException(String message, Throwable cause, Object origin) {
		super((origin == null ? "" : origin.toString() + ": ") + message + " (caused by: " + cause.getMessage() + ")", cause);
		this.origin = origin;
	}

	
	/**
	 * Constructs a new exception with the specified detail message.
	 * <p><p>
	 * Logs the exception via specified Logger.
	 * 
	 * @param message
	 * @param origin which object does produced the exception, may be null
	 */
	public PogamutException(String message, Logger log, Object origin){
		super((origin == null ? "" : origin.toString() + ": ") + message);
		this.origin = origin;
		logException(log);
	}
	
	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * <p><p>
	 * Logs the exception via specified Logger.
	 * 
	 * @param message
	 * @param cause
	 * @param origin object that thrown the exception
	 */
	public PogamutException(Throwable cause, Logger log, Object origin) {
		super(cause);
		this.origin = origin;
		logException(log);		
	}
	
	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * <p><p>
	 * Logs the exception via specified Logger.
	 * 
	 * @param message
	 * @param cause
	 * @param origin object that thrown the exception, may be null
	 */
	public PogamutException(String message, Throwable cause, Logger log, Object origin) {
		super((origin == null ? "" : origin.toString() + ": ") + message + " (caused by: " + cause.getMessage() + ")", cause);
		this.origin = origin;
		logException(log);		
	}
	
	/**
	 * Whether the exception has been logged to any logger.
	 * <p><p>
	 * Note that sometimes it's not possible to log the exception as it is created
	 * due to lack of the logger - therefore we propagate this information through
	 * the stack and as soon as it encounters some Pogamut-platform catch block
	 * it will be logged.
	 * 
	 * @return
	 */
	public boolean isLogged() {
		return hasBeenLogged;
	}
	
	/**
	 * Set whether the exception has been logged to the Platform logger, if not
	 * it will be logged as soon as it encounters some catch block from the Pogamut
	 * platform.
	 * 
	 * @param logged
	 */
	public void setLogged(boolean logged) {
		this.hasBeenLogged = logged;
	}
	
	/**
	 * Returns the object that has thrown the exception.
	 * @return
	 */
	public Object getOrigin() {
		return origin;
	}
	
	/**
	 * Serialize the exception to String.
	 */
	public String toString() {
		if (this == null) return "PogamutException-instantiating";
		return getClass().getSimpleName() + "[" + getMessage() + "]";
	}
	
	/**
	 * Logs the exception to the log + sets isLogged() to true.
	 */
	public synchronized void logException(Logger log) {
		try {
			if (log == null) return;
			if (log.isLoggable(Level.SEVERE)) log.severe(ExceptionToString.process(getMessage(), this));
			this.hasBeenLogged = true;			
		} catch (Exception e) {
			System.err.println("PogamutException (and can't log to log '"+log.getName()+"' because \""+e.getMessage()+"\"), exception: " + toString());			
			this.printStackTrace(System.err);
		}			
	}
	
	/**
	 * Logs the exception to the log iff !isLogged().
	 * <p><p>
	 * Logs the exception only once! Successive calls do nothing.
	 */
	public synchronized void logExceptionOnce(Logger log) {
		if (log == null) return;
		if (isLogged()) return;
		logException(log);			
	}
	
}
