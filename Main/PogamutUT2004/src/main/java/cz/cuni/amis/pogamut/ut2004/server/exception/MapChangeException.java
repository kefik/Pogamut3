package cz.cuni.amis.pogamut.ut2004.server.exception;

import cz.cuni.amis.pogamut.ut2004.server.IUT2004Server;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Thrown by {@link IUT2004Server} implementors when the map change fails.
 * @author Jimmy
 *
 */
public class MapChangeException extends PogamutException {

	public MapChangeException(String message, Throwable cause) {
		super(message, cause);
	}

	public MapChangeException(String message, Object origin) {
		super(message, origin);
	}
    
}