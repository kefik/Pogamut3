package cz.cuni.amis.pogamut.base.component.bus.event.impl;

import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.event.IStartingEvent;

public class StartingEvent<SOURCE extends IComponent> extends ComponentEvent<SOURCE> implements IStartingEvent<SOURCE> {

	private String message;

	public StartingEvent(SOURCE component) {
		super(component);
		this.message = "Starting.";
	}
	
	public StartingEvent(SOURCE component, String message) {
		super(component);
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
	
}
