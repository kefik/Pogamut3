package cz.cuni.amis.pogamut.base.component.bus;

import cz.cuni.amis.pogamut.base.component.IComponent;

/**
 * Event that may happen on the EventBut.
 * <p><p>
 * Notice that {@link ComponentBus} does not require to operate only for components. Even though it is generally
 * better to transmit events only from descendants of {@link IComponent}s as {@link ComponentBus} provides
 * better support for listeners in such cases.
 * 
 * @author Jimmy
 *
 * @param SOURCE
 */
public interface IComponentEvent<SOURCE extends IComponent> {

	public SOURCE getSource();
	
}
