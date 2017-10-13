package cz.cuni.amis.pogamut.base.utils.future;

import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Exception that is thrown whenever the result of {@link ComponentFuture} can't be obtained.
 * @author Jimmy
 */
public class ComponentFutureException extends PogamutException {

	public ComponentFutureException(String message, Object origin) {
		super(message, origin);
	}
	
	public ComponentFutureException(String message, Throwable cause) {
		super(message, cause);
	}

}
