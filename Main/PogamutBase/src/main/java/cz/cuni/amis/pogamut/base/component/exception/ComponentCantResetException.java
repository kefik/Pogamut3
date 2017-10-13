package cz.cuni.amis.pogamut.base.component.exception;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.component.bus.exception.FatalErrorPropagatingEventException;

public class ComponentCantResetException extends ComponentException {

	/**
	 * Constructs a new exception with the specified detail message.
	 * <p><p>
	 * Not logging anything anywhere on its own.
	 * 
	 * @param message
	 * @param origin which object does produced the exception
	 */
	public ComponentCantResetException(String message, Object origin) {
		super(message, origin);
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
	public ComponentCantResetException(String message, Throwable cause, Object origin) {
		super(message, (cause == null ? null : (cause instanceof FatalErrorPropagatingEventException ? ((FatalErrorPropagatingEventException)cause).getCause() == null ? cause : ((FatalErrorPropagatingEventException)cause).getCause() : cause)), origin);
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
	public ComponentCantResetException(String message, Logger log, Object origin){
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
	public ComponentCantResetException(String message, Throwable cause, Logger log, Object origin) {
		super(message, (cause == null ? null : (cause instanceof FatalErrorPropagatingEventException ? ((FatalErrorPropagatingEventException)cause).getCause() == null ? cause : ((FatalErrorPropagatingEventException)cause).getCause() : cause)), log, origin);
	}
	
}
