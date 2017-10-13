package cz.cuni.amis.pogamut.base.component.bus.exception;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.IComponentEvent;

public class FatalErrorPropagatingEventException extends ComponentBusException {
	
	public FatalErrorPropagatingEventException(IComponentEvent event, IComponentBus origin) {
		super("Exception happened during the propagation of: " + event, origin);
	}
	
	public FatalErrorPropagatingEventException(IComponentEvent event, Logger log, IComponentBus origin) {
		super("Exception happened during the propagation of: " + event, log, origin);
	}
	
	public FatalErrorPropagatingEventException(IComponentEvent event, Throwable cause, IComponentBus origin) {
		super("Exception happened during the propagation of: " + event, cause, origin);
	}
	
	public FatalErrorPropagatingEventException(IComponentEvent event, Throwable cause, Logger log, IComponentBus origin) {
		super("Exception happened during the propagation of: " + event, cause, log, origin);
	}

}
