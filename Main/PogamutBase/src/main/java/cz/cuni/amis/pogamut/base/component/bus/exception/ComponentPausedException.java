package cz.cuni.amis.pogamut.base.component.bus.exception;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.component.controller.ComponentState;
import cz.cuni.amis.pogamut.base.component.exception.ComponentException;

/**
 * This exception is thrown whenever you call some running-state-dependent method from the public interface of
 * some {@link IComponent}. It means that the component is paused or pausing.
 *
 * @author Jimmy
 */
public class ComponentPausedException extends ComponentException {
	
	/**
	 * Constructs a new exception with the specified detail message.
	 * <p><p>
	 * Not logging anything anywhere on its own.
	 * 
	 * @param state
	 * @param origin which object does produced the exception
	 */
	public ComponentPausedException(ComponentState state, Object origin) {
		super("In state: " + state + ".", origin);
	}
	
	/**
	 * Constructs a new exception with the specified detail message.
	 * <p><p>
	 * Not logging anything anywhere on its own.
	 * 
	 * @param message
	 * @param origin which object does produced the exception
	 */
	public ComponentPausedException(String message, Object origin) {
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
	public ComponentPausedException(ComponentState state, Throwable cause, Object origin) {
		super("In state: " + state + ".", cause, origin);
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
	public ComponentPausedException(String message, Throwable cause, Object origin) {
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
	public ComponentPausedException(ComponentState state, Logger log, Object origin){
		super("In state: " + state + ".", log, origin);
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
	public ComponentPausedException(String message, Logger log, Object origin){
		super(message, log, origin);
	}
	
	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * <p><p>
	 * Logs the exception via specified Logger.
	 * 
	 * @param state
	 * @param cause
	 * @param log
	 * @param origin which object does produced the exception
	 */
	public ComponentPausedException(ComponentState state, Throwable cause, Logger log, Object origin) {
		super("In state: " + state + ".", cause, log, origin);
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
	public ComponentPausedException(String message, Throwable cause, Logger log, Object origin) {
		super(message, cause, log, origin);
	}
	
}
