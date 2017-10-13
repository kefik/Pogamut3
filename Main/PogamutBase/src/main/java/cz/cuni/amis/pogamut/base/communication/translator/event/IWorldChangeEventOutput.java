package cz.cuni.amis.pogamut.base.communication.translator.event;

import cz.cuni.amis.pogamut.base.communication.exception.CommunicationException;
import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentNotRunningException;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;

/**
 * Interface providing events from the world.
 * @author Jimmy
 */
public interface IWorldChangeEventOutput extends IComponent {

	/**
	 * Returns next event of the world.
	 * <p><p>
	 * May block.
	 * 
	 * @return
	 */
	public IWorldChangeEvent getEvent() throws CommunicationException, ComponentNotRunningException;
	
}