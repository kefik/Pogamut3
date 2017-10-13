package cz.cuni.amis.pogamut.base.component.bus.event.impl;

import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.event.IStartingPausedEvent;

public class StartingPausedEvent<SOURCE extends IComponent> extends ComponentEvent<SOURCE> implements IStartingPausedEvent<SOURCE> {

	private String message;

	public StartingPausedEvent(SOURCE component) {
		super(component);
		this.message = "Starting and then pausing.";
	}
	
	public StartingPausedEvent(SOURCE component, String message) {
		super(component);
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
	
}
