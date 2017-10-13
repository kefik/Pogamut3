package cz.cuni.amis.pogamut.base.component.bus.event.impl;

import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;

public class ComponentBusErrorEvent extends FatalErrorEvent<IComponentBus> {

	public ComponentBusErrorEvent(IComponentBus component, Throwable cause) {
		super(component, cause);
	}
	
	public ComponentBusErrorEvent(IComponentBus component, String message) {
		super(component, message);
	}

}
