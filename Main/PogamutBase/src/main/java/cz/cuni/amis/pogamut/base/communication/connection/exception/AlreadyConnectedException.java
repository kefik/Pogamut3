package cz.cuni.amis.pogamut.base.communication.connection.exception;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.communication.connection.IWorldConnection;
import cz.cuni.amis.pogamut.base.communication.exception.CommunicationException;

/**
 * Thrown when some pre-connect method is invoked and {@link IWorldConnection} is already connected. 
 * @author Jimmy
 *
 */
public class AlreadyConnectedException extends CommunicationException {
	
	/**
	 * Constructs a new exception with the specified detail message.
	 * <p><p>
	 * Not logging anything anywhere on its own.
	 * 
	 * @param message
	 * @param log
	 * @param origin which object does produced the exception
	 */
	public AlreadyConnectedException(String message, Logger log, Object origin) {
		super(message, log, origin);
	}


}
