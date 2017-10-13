package cz.cuni.amis.pogamut.base.component.bus.event;

import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentEvent;

/**
 * Marks that the component has started its work.
 * @author Jimmy
 */
public interface IStartedEvent<SOURCE extends IComponent> extends IComponentEvent<SOURCE> {
	
	/**
	 * Provides human readable information why the component has started.
	 * @return
	 */
	public String getMessage();
	
}
