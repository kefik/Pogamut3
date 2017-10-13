package cz.cuni.amis.pogamut.base.component.bus.event.impl;

import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.event.IResumingEvent;

public class ResumingEvent<SOURCE extends IComponent> extends ComponentEvent<SOURCE> implements IResumingEvent<SOURCE> {
	
	private String message;

	public ResumingEvent(SOURCE component) {
		super(component);
		this.message = "Resuming.";
	}
	
	public ResumingEvent(SOURCE component, String message) {
		super(component);
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}


}
