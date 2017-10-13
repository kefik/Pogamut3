package cz.cuni.amis.pogamut.base.component.bus.exception;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.IComponentEvent;

public class ComponentBusNotRunningException extends ComponentBusException {

	public ComponentBusNotRunningException(IComponentEvent event, IComponentBus bus) {
		super("Component bus is not running, can't process event: " + event + ".", bus);
	}
	
	public ComponentBusNotRunningException(IComponentEvent event, Logger log, IComponentBus bus) {
		super("Component bus is not running, can't process event: " + event + ".", log, bus);
	}
	
}
