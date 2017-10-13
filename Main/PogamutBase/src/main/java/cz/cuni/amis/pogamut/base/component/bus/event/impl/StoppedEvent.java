package cz.cuni.amis.pogamut.base.component.bus.event.impl;

import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.event.IStoppedEvent;

public class StoppedEvent<SOURCE extends IComponent> extends ComponentEvent<SOURCE> implements IStoppedEvent<SOURCE> {

	private String message;

	public StoppedEvent(SOURCE component) {
		super(component);
		this.message = "Stopped.";
	}
	
	public StoppedEvent(SOURCE component, String message) {
		super(component);
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
	
}
