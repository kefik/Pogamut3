package cz.cuni.amis.pogamut.base.component.bus.exception;

import java.util.logging.Logger;

import cz.cuni.amis.utils.exception.PogamutException;

public class NoComponentForClassException extends PogamutException {

	public NoComponentForClassException(Class cls, Object origin) {
		super("No component found for class " + cls, origin);
	}
	
	public NoComponentForClassException(Class cls, Logger log, Object origin) {
		super("No component found for class " + cls, log, origin);
	}

}
