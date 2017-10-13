package cz.cuni.amis.pogamut.base.component.bus.event.impl;

import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.event.IStartedEvent;

public class StartedEvent<SOURCE extends IComponent> extends ComponentEvent<SOURCE> implements IStartedEvent<SOURCE> {
	
	private String message;

	public StartedEvent(SOURCE component) {
		super(component);
		this.message = "Started.";
	}
	
	public StartedEvent(SOURCE component, String message) {
		super(component);
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
	
}
