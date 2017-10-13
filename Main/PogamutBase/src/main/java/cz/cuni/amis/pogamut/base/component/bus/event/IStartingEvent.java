package cz.cuni.amis.pogamut.base.component.bus.event;

import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentEvent;

public interface IStartingEvent<SOURCE extends IComponent> extends IComponentEvent<SOURCE> {

	/**
	 * Provides human readable information why the component is starting.
	 * @return
	 */
	public String getMessage();
	
}
