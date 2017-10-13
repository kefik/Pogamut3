package cz.cuni.amis.events.impl;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import cz.cuni.amis.events.IEventInput;
import cz.cuni.amis.events.event.IEvent;

public abstract class BaseEventInput implements IEventInput {

	// ==================================
	// IEventInput interface
	// ==================================
	
	/**
	 * New event was generated. 
	 * <p><p>
	 * Passes event into {@link BaseEventInput#raiseEvent(IEvent)} that is preventing recursion.
	 * 
	 * @param event
	 */
	public void notify(IEvent event) {
		raiseEvent(event);
	}
	
	/**
	 * Notify immediately will process the event right away, it won't use "event recursion buffer" to postpone the processing of the event.
	 * <p><p>
	 * Passes event into {@link BaseEventInput#directEvent(IEvent)} that is only synchronized and allows recursion during event resolution.
	 * 
	 * @param event
	 */
	public void notifyNow(IEvent event) {
		directEvent(event);
	}
	
	// ======================
	// IMPLEMENTATION
	// ======================
	
	/**
	 * Flag that is telling us whether there is an event being processed or not.
	 * <p><p>
	 * It is managed only by raiseEvent() method - DO NOT MODIFY OUTSIDE IT!
	 */
	private boolean raiseEventProcessing = false;
	
	/**
	 * List of events we have to process.
	 * <p><p>
	 * It is managed only by raiseEvent() method - DO NOT MODIFY THIS OUTSIDE THAT METHOD!
	 */
	private Queue<IEvent> raiseEventsList = new ConcurrentLinkedQueue<IEvent>();
	
	/**
	 * Process new IEvent - notify all the listeners about it. Forbids recursion. (If you want to allow recursion, call {@link BaseEventInput#processEvent(IEvent)} 
	 * directly instead}.
	 * <p><p>
	 * Use in the descendants to process new IWorldChangeEvent.
	 * <p><p>
	 * Does not catch any exceptions!
	 * <p><p>
	 * Synchronized!
	 * 
	 * @param event
	 */
	protected synchronized void raiseEvent(IEvent event) {
		// is this method recursively called? 
		if (raiseEventProcessing) {
			// yes it is -> that means the previous event has not been
			// processed! ... store this event and allows the previous one
			// to be fully processed (e.g. postpone raising this event)
			raiseEventsList.add(event);
			return;
		} else {
			// no it is not ... so raise the flag that we're inside the method
			//              ... as we're synchronized we do not need to handle multithreading here
			raiseEventProcessing = true;
		}
	
		// process event
				
		directEvent(event);
		
		// check the events list size, do we have more events to process?
		while(raiseEventsList.size() != 0) {
			// yes we do -> do it!
			processEvent(raiseEventsList.poll());			
		}
		
		// all events has been processed, drop the flag that we're inside the method
		raiseEventProcessing = false;		
	}
	
	/**
	 * Just synchronization point in the event processing. Recalls {@link BaseEventInput#processEvent(IEvent)} immediately.
	 * @param event
	 */
	protected synchronized void directEvent(IEvent event) {
		processEvent(event);
	}
	
	// ===================================
	// DESCENDANTS EXTENSION POINTS
	// ===================================
	
	/**
	 * Processes the event according to object's event semantics.
	 */
	protected abstract void processEvent(IEvent event);
	
}
