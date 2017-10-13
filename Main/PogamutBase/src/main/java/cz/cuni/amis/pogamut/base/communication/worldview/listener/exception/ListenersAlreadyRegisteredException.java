package cz.cuni.amis.pogamut.base.communication.worldview.listener.exception;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.communication.worldview.listener.IListenerRegistrator;
import cz.cuni.amis.utils.exception.PogamutException;

public class ListenersAlreadyRegisteredException extends PogamutException {

	public ListenersAlreadyRegisteredException(IListenerRegistrator origin) {
		super("Listeners already registered.", origin);
	}

	public ListenersAlreadyRegisteredException(Logger log, IListenerRegistrator origin) {
		super("Listeners already registered.", log, origin);
	}

}
