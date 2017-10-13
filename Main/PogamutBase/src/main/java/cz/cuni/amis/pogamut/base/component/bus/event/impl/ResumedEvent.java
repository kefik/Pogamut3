package cz.cuni.amis.pogamut.base.component.bus.event.impl;

import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.event.IResumedEvent;

public class ResumedEvent<SOURCE extends IComponent> extends ComponentEvent<SOURCE> implements IResumedEvent<SOURCE> {
	
	private String message;

	public ResumedEvent(SOURCE component) {
		super(component);
		this.message = "Resumed.";
	}
	
	public ResumedEvent(SOURCE component, String message) {
		super(component);
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}


}
