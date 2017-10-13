package cz.cuni.amis.pogamut.base.component.bus.event;

import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentEvent;

/**
 * The component stopped its job.
 * 
 * @author Jimmy
 */
public interface IStoppedEvent<SOURCE extends IComponent> extends IComponentEvent<SOURCE> {

	/**
	 * Provides human readable information why the component is stopping.
	 * @return
	 */
	public String getMessage();
	
}
