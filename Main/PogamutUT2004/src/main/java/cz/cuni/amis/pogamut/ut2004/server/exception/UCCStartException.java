/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.ut2004.server.exception;

import java.util.logging.Logger;

import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Exception raised when UCCWrapper fails to start the UT Server instance.
 * @author Ik
 */
public class UCCStartException extends PogamutException {

	/**
	 * Constructs a new exception with the specified detail message.
	 * <p><p>
	 * Not logging anything anywhere on its own.
	 * 
	 * @param message
	 * @param origin which object does produced the exception
	 */
	public UCCStartException(String message, Object origin) {
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
	public UCCStartException(String message, Throwable cause, Object origin) {
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
	public UCCStartException(String message, Logger log, Object origin){
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
	public UCCStartException(String message, Throwable cause, Logger log, Object origin) {
		super(message, cause, log, origin);
	}	
}
