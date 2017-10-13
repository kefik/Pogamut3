package cz.cuni.amis.pogamut.base.component.bus.event.impl;

import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.event.IPausedEvent;

public class PausedEvent<SOURCE extends IComponent> extends ComponentEvent<SOURCE> implements IPausedEvent<SOURCE> {
	
	private String message;

	public PausedEvent(SOURCE component) {
		super(component);
		this.message = "Paused.";
	}
	
	public PausedEvent(SOURCE component, String message) {
		super(component);
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
