package cz.cuni.amis.pogamut.base.component.bus.event.impl;

import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.event.IStoppingEvent;

public class StoppingEvent<SOURCE extends IComponent> extends ComponentEvent<SOURCE> implements IStoppingEvent<SOURCE> {

	private String message;

	public StoppingEvent(SOURCE component) {
		super(component);
		this.message = "Stopping.";
	}
	
	public StoppingEvent(SOURCE component, String message) {
		super(component);
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return message;
	}

}
