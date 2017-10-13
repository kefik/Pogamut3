package cz.cuni.amis.pogamut.base.component.bus.event.impl;

import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.event.IPausingEvent;

public class PausingEvent<SOURCE extends IComponent> extends ComponentEvent<SOURCE> implements IPausingEvent<SOURCE> {
	
	private String message;

	public PausingEvent(SOURCE component) {
		super(component);
		this.message = "Pausing.";
	}
	
	public PausingEvent(SOURCE component, String message) {
		super(component);
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}


}
