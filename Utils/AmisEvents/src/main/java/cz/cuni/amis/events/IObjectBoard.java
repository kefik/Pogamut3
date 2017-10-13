package cz.cuni.amis.events;

import object.IObjectEventListener;
import object.IObjectId;
import cz.cuni.amis.events.event.IEventListener;

/**
 * Interface for the event processor.
 * 
 * @author Jimmy
 */
public interface IObjectBoard extends IEventBoard {
	
	// ======
	// EVENTS
	// ======

	/**
	 * Adds listener to all events that happens on any object of the 'objectClass' (Level B listeners).
	 * <p><p>
	 * Events passed to the listener are filtered according to the 'objectClass'.
	 * <p><p>
	 * <b>WARNING:</b> even though the method does not require templated classes and listener, you must be sure
	 * that 'listener' accepts all events (IWorldObjectEvent) for objects of 'objectClass'.
	 *
	 * @param objectClass which object class you want to listen at
	 * @param eventClass which event class you want to receive
	 * @param listener where you want to handle these events
	 */
	public void addObjectListener(Class<?> objectClass, IObjectEventListener<?> listener);

	/**
	 * Adds listener to a specified 'event' that occurs on the specific 'objectClass' (Level C listeners).
	 * <p><p>
	 * Events passed to the listener are filtered according to the 'event' and 'objectClass' of the object the event happened upon.
	 * <p><p>
	 * <b>WARNING:</b> even though the method does not require templated classes and listener, you must be sure
	 * that 'listener' accepts 'eventClass' for objects of 'objectClass'.
	 *
	 * <p>
	 * eventClass can be any implementor of {@link IWorldObjectEvent}. E.g.
	 * {@link WorldObjectAppearedEvent}, {@link WorldObjectDestroyedEvent}, {@link WorldObjectDisappearedEvent},
	 * {@link WorldObjectFirstEncounteredEvent} or {@link WorldObjectUpdatedEvent}.
	 * </p>
	 *
	 * @param objectClass which object class you want to listen at
	 * @param eventClass which event class you want to receive
	 * @param listener where you want to handle these events
	 */
	public void addObjectListener(Class<?> objectClass, Class<?> eventClass, IObjectEventListener<?> listener);

	/**
	 * Adds listener to all events that happens on object with specific 'objectId' (Level D listeners).
	 * <p><p>
	 * Events passed to the listener are filtered according to the 'objectId' of the object.
	 * <p><p>
	 * <b>WARNING:</b> you must ensure that 'listener' can accept the event raised on object of specified 'objectId'.
	 *
	 * @param objectId which object you want to listen at
	 * @param listener where you want to handle events
	 */
	public void addObjectListener(IObjectId objectId, IObjectEventListener<?> listener);

	/**
	 * Adds listener to a specified 'event' that occurs on the specific object with 'objectId' (Level E listeners).
	 * <p><p>
	 * Events passed to the listener are filtered according to the 'event' and 'objectId' of the object.
	 * <p><p>
	 * <b>WARNING:</b> even though the method does not require templated classes and listener, you must be sure
	 * that 'listener' accepts 'eventClass' for objects of specified 'objectId'.
	 *
	 * @param objectId which object you want to listen at
	 * @param eventClass which event classes you want to receive
	 * @param listener where you want to handle these events
	 */
	public void addObjectListener(IObjectId objectId, Class<?> eventClass, IObjectEventListener<?> listener);

	/**
	 * Removes listener from specific 'objectClass' listening for specified 'event' (Level B listeners).
	 *
	 * @param objectClass class of objects you want the listener to remove from
	 * @param eventClass which events class you want to remove the listener from
	 * @param listener you want to remove
	 */
	public void removeObjectListener(Class<?> objectClass, IObjectEventListener<?> listener);
	
	/**
	 * Removes listener from specific 'objectClass' listening for specified 'event' (Level C listeners).
	 *
	 * @param objectClass class of objects you want the listener to remove from
	 * @param eventClass which events class you want to remove the listener from
	 * @param listener you want to remove
	 */
	public void removeObjectListener(Class<?> objectClass, Class<?> eventClass, IObjectEventListener<?> listener);


	/**
	 * Removes listener from objects with specified 'objectId' (Level D Listeners).
	 *
	 * @param objectId id of object you want the listener to remove from
	 * @param listener you want to remove
	 */
	public void removeObjectListener(IObjectId objectId, IObjectEventListener<?> listener);

	/**
	 * Removes listener to a specified 'event' that occurs on the specific object with 'objectId' (Level E listeners).
	 *
	 * @param objectId id of object you want the listener to remove from
	 * @param eventClass event class you want to stop receiving in the listener
	 * @param listener you want to remove
	 */
	public void removeObjectListener(IObjectId objectId, Class<?> eventClass, IObjectEventListener<?> listener);

	/**
	 * Removes listener from every listeners category (from every listener level).
	 * <p><p>
	 * <b>WARNING:</b> Can be time consuming! (Iterating through all levels of listeners.)
	 *
	 * @param listener you want to remove from all listener levels
	 */
	public void removeListener(IEventListener<?> listener);

	/**
	 * Tests whether the 'listener' is listening at specified 'objectClass' (Level B listeners).
	 *
	 * @param objectClass where the listener is tested
	 * @param listener that is tested
	 * @return whether the listener is listening
	 */
	public boolean isListening(Class<?> objectClass, IObjectEventListener<?> listener);	
	
	/**
	 * Tests whether the 'listener' is listening at specified 'objectClass' for specified 'event' (Level C listeners).
	 *
	 * @param objectClass where the listener is tested
	 * @param eventClass where the listener is tested
	 * @param listener that is tested
	 * @return whether the listener is listening
	 */
	public boolean isListening(Class<?> objectClass, Class<?> eventClass, IObjectEventListener<?> listener);


	/**
	 * Tests whether the 'listener' is listening at specified 'objectId' (Level D Listeners).
	 *
	 * @param objectId where the listener is tested
	 * @param listener that is tested
	 * @return whether the listener is listening
	 */
	public boolean isListening(IObjectId objectId, IObjectEventListener<?> listener);

	/**
	 * Tests whether the 'listener' is listening to a specified 'event' that occurs on the specific object with 'objectId' (Level E listeners).
	 *
	 * @param objectId where the listener is tested
	 * @param eventClass what class is tested
	 * @param listener that is tested
	 * @return whether the listener is listening
	 */
	public boolean isListening(IObjectId objectId, Class<?> eventClass, IObjectEventListener<?> listener);

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