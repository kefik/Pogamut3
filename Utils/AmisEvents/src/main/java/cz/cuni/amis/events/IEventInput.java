package cz.cuni.amis.events;

import cz.cuni.amis.events.event.IEvent;

/**
 * Input interface for the world view. {@link IEventBoard}, {@link IObjectBoard} receives new events through this interface.
 * 
 * @author Jimmy
 */
public interface IEventInput {

	/**
	 * New event was generated.
	 * 
	 * @param event
	 */
	public void notify(IEvent event);
	
	/**
	 * Notify immediately will process the event right away, it won't use "event recursion buffer" to postpone the processing of the event.
	 * 
	 * @param event
	 */
	public void notifyNow(IEvent event);
    
}
