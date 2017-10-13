package cz.cuni.amis.pogamut.base.component.bus.event.impl;

import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.event.IResetEvent;

public class ResetEvent<SOURCE extends IComponent> extends ComponentEvent<SOURCE> implements IResetEvent<SOURCE> {
	
	public ResetEvent(SOURCE component) {
		super(component);
	}
	
}
