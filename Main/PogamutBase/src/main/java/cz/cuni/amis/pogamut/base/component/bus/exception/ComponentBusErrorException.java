package cz.cuni.amis.pogamut.base.component.bus.exception;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.IComponentEvent;
import cz.cuni.amis.utils.exception.PogamutException;

public class ComponentBusErrorException extends PogamutException {
	
	public ComponentBusErrorException(String message, Throwable cause, IComponentBus origin) {
		super(message, cause, origin);
	}
	
	public ComponentBusErrorException(String message, Logger log, Throwable cause, IComponentBus origin) {
		super(message, cause, log, origin);
	}
	
	public ComponentBusErrorException(IComponentEvent event, IComponentBus origin) {
		super("Fatal error has happened while processing " + event + ".", origin);
	}

	public ComponentBusErrorException(IComponentEvent event, Logger log, IComponentBus origin) {
		super("Fatal error has happened while processing " + event + ".", log, origin);
	}
	
	public ComponentBusErrorException(String message, IComponentEvent event, IComponentBus origin) {
		super("Fatal error has happened while processing " + event + ": " + message, origin);
	}
	
	public ComponentBusErrorException(String message, IComponentEvent event, Logger log, IComponentBus origin) {
		super("Fatal error has happened while processing " + event + ": " + message, log, origin);
	}

}
