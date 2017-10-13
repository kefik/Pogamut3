package cz.cuni.amis.events.impl;

import java.util.Collection;

import cz.cuni.amis.events.IEventBoard;
import cz.cuni.amis.events.event.IEvent;
import cz.cuni.amis.events.event.IEventListener;
import cz.cuni.amis.utils.ClassUtils;
import cz.cuni.amis.utils.listener.IListener;
import cz.cuni.amis.utils.listener.Listeners;
import cz.cuni.amis.utils.listener.ListenersMap;

@SuppressWarnings("unchecked")
public class EventBoard extends BaseEventInput implements IEventBoard {

	// ========================================
	// IEventBoard IMPLEMENTATION
	// ========================================

	/**
	 * Level A Listeners
	 * <p><p>
	 * Map of the event listeners, key is the event class where the listener is hooked to.
	 */
	protected ListenersMap<Class> eventListeners = new ListenersMap<Class>();
	
	@Override
	public void addEventListener(Class<?> eventClass, IEventListener<?> listener) {
		eventListeners.add(eventClass, listener);
	}

	@Override
	public boolean isListening(Class<?> eventClass, IEventListener<?> listener) {
		return eventListeners.isListening(eventClass, listener);
	}

	@Override
	public boolean isListening(IEventListener<?> listener) {
		return eventListeners.isListening(listener);
	}

	@Override
	public void removeEventListener(Class<?> eventClass, IEventListener<?> listener) {
		eventListeners.remove(eventClass, listener);
	}

	@Override
	public void removeListener(IEventListener<?> listener) {
		eventListeners.remove(listener);
	}
	
	// ========================================
	// BaseEventInput IMPLEMENTATION
	// ========================================
	
	/**
	 * Class that notifies listeners about the event.
	 * @author Jimmy
	 */
	protected static class ListenerNotifier<T> implements Listeners.ListenerNotifier<IListener> {

		/**
		 * Event that is being processed.
		 */
		private T event = null;
		
		@Override
		public T getEvent() {
			return event;
		}
		
		public void setEvent(T event) {
			this.event = event;			
		}

		/**
		 * Method that is used to notify the listener.
		 */
		@Override
		public void notify(IListener listener) {
			listener.notify(event);
		}
		
	}
	
	/**
	 * Notifier object - preallocated, this will raise events on the listeners.
	 */
	protected ListenerNotifier notifier = new ListenerNotifier();
	
	/**
	 * Notifies "event listeners" about the event according to subclasses/class of the 'event'.
	 * @param event
	 */
	protected void notifyLevelAListeners(IEvent event) {
		Collection<Class> eventClasses = ClassUtils.getSubclasses(event.getClass());
		notifier.setEvent(event);
		for (Class eventClass : eventClasses) {
			eventListeners.notify(eventClass, notifier);
		}
	}
	
	@Override
	protected void processEvent(IEvent event) {
		notifyLevelAListeners(event);
	}

}
