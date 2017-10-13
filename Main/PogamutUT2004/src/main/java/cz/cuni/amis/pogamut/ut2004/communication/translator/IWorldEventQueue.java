package cz.cuni.amis.pogamut.ut2004.communication.translator;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.ImplementedBy;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;

/**
 * Interface for the world event queue that is used to transport new world events from the FSM to MessageTranslator. 
 * @author Jimmy
 */
@ImplementedBy(value=IWorldEventQueue.Queue.class)
public interface IWorldEventQueue {
	
	/**
	 * Add event to the queue.
	 * @param event
	 */
	public void pushEvent(IWorldChangeEvent event);
	
	/**
	 * Add events to the queue.
	 * @param events
	 */
	public void pushEvent(IWorldChangeEvent[] events);
	
	/**
	 * Returns all events in the queue + removes them from the queue.
	 * <p><p>
	 * If there are no event this must return an empty array. 
	 * @return
	 */
	public IWorldChangeEvent[] popEvents();
	
	public static class Queue implements IWorldEventQueue {
		
		private List<IWorldChangeEvent> events = new ArrayList<IWorldChangeEvent>(50);

		@Override
		public IWorldChangeEvent[] popEvents() {
			IWorldChangeEvent[] newEvents = this.events.toArray(new IWorldChangeEvent[events.size()]);
			this.events.clear();
			return newEvents;
		}
		
		@Override
		public void pushEvent(IWorldChangeEvent event) {
			events.add(event);
		}

		@Override
		public void pushEvent(IWorldChangeEvent[] events) {
			for (IWorldChangeEvent event : events) this.events.add(event);
		} 
		
	}

}
