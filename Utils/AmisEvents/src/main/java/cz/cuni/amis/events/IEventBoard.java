package cz.cuni.amis.events;

import cz.cuni.amis.events.event.IEventListener;

/**
 * Interface for the event processor.
 * 
 * @author Jimmy
 */
public interface IEventBoard extends IEventInput {
	
	// ======
	// EVENTS
	// ======

	/**
	 * Adds listener to a specific event. Note that the event listener must be able
	 * to handle events of the class 'event'.
	 * <p><p>
	 * It is the most general type of listener-registration allowing you to sniff any type of events.
	 * <p><p>
	 * Events passed to the listener are filtered only according to the 'event' class.
	 * <p><p>
	 * <b>WARNING:</b> even though the method does not require templated class and listener, you must be sure
	 * that 'listener' can accept 'eventClass'.
	 *
	 * @param eventClass which event types you want to receive
	 * @param listener where you want to handle these events
	 */
	public void addEventListener(Class<?> eventClass, IEventListener<?> listener);
	
	/**
	 * Removes listener from a specific event (Level A listeners).
	 *
	 * @param eventClass which events class you want to remove the listener from
	 * @param listener you want to remove
	 */
	public void removeEventListener(Class<?> eventClass, IEventListener<?> listener);

	/**
	 * Removes listener from every listeners category (from every listener level).
	 * <p><p>
	 * <b>WARNING:</b> Can be time consuming! (Iterating through all levels of listeners.)
	 *
	 * @param listener you want to remove from all listener levels
	 */
	public void removeListener(IEventListener<?> listener);

	/**
	 * Tests whether the 'listener' is listening to a specific event (Level A listeners).
	 *
	 * @param eventClass which events you want to receive
	 * @param listener that is tested
	 * @return whether the listener is listening
	 */
	public boolean isListening(Class<?> eventClass, IEventListener<?> listener);

	/**
	 * Checks whether this listener is hooked to the world view (at any listener level).
	 * <p><p>
	 * <b>WARNING:</b> Can be time consuming! (Iterating through all levels of listeners.)
	 *
	 * @param listener
	 * @return
	 */
	public boolean isListening(IEventListener<?> listener);

}