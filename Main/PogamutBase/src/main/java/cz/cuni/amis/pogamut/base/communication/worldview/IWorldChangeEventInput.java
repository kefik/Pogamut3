package cz.cuni.amis.pogamut.base.communication.worldview;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentNotRunningException;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentPausedException;

/**
 * Input interface for the world view. {@link IWorldView} receives new events through this interface.
 * 
 * @author Jimmy
 */
public interface IWorldChangeEventInput extends IComponent {

	/**
	 * New event was generated from the world.
	 * 
	 * @param event
	 */
	public void notify(IWorldChangeEvent event) throws ComponentNotRunningException, ComponentPausedException;
	
	/**
	 * Raise another event after current one finishes its propagation.
	 * <p><p>
	 * Won't propagate the event if the world view is locked!.
	 * 
	 * @param event
	 * @throws ComponentNotRunningException
	 * @throws ComponentPausedException
	 */
	public void notifyAfterPropagation(IWorldChangeEvent event) throws ComponentNotRunningException, ComponentPausedException;
	
	/**
	 * Notify immediately will process the event right away, it won't use "event recursion buffer" to postpone the processing of the event.
	 * <p><p>
	 * This will work even if the world view is locked!
	 * 
	 * @param event
	 * @throws ComponentNotRunningException
	 * @throws ComponentPausedException
	 */
	public void notifyImmediately(IWorldChangeEvent event) throws ComponentNotRunningException, ComponentPausedException;
    
}
