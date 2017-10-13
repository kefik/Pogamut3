package cz.cuni.amis.pogamut.base.component.bus.event;

import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentEvent;

public interface IStoppingEvent<SOURCE extends IComponent> extends IComponentEvent<SOURCE> {
	
	/**
	 * Provides human readable information why the component is stopping.
	 * @return
	 */
	public String getMessage();
	
}
