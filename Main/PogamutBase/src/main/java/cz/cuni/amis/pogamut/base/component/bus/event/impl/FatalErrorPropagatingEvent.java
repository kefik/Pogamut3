package cz.cuni.amis.pogamut.base.component.bus.event.impl;

import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentEvent;

public class FatalErrorPropagatingEvent<SOURCE extends IComponent> extends FatalErrorEvent<SOURCE> {
	
	private IComponentEvent event;

	public FatalErrorPropagatingEvent(SOURCE component, String message, Throwable cause, IComponentEvent event) {
		super(component, message, cause);
		this.event = event;
	}

	public IComponentEvent getEvent() {
		return event;
	}
	
}
