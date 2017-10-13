
package cz.cuni.amis.pogamut.multi.communication.worldview;

import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectDestroyedEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectFirstEncounteredEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.ILifecycleBus;
import cz.cuni.amis.pogamut.base.utils.guice.AgentTeamScoped;
import cz.cuni.amis.pogamut.base3d.worldview.object.event.WorldObjectAppearedEvent;
import cz.cuni.amis.pogamut.base3d.worldview.object.event.WorldObjectDisappearedEvent;
import cz.cuni.amis.pogamut.multi.agent.ITeamId;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.IStaticWorldObject;
import cz.cuni.amis.pogamut.multi.utils.timekey.TimeKey;

/**
 * Interface for WorldViews for multi-agent systems
 * @author srlok
 *
 */
public interface ISharedWorldView extends ISharedWorldChangeEventInput {

	/**
	 * This method is  called when a new localWorldView is created and wants to use this sharedWorldView,
	 * the method registers the LocalWorldView with the sharedWorldView's sharedComponentBus and also
	 * internally stores the information about which WorldViews are registered to it.
	 * 
	 * @param localWV The local WorldView to to register.
	 * @param bus ILifecycleBus of the corresponding LocalWorldView
	 */
	public void registerLocalWorldView( ILocalWorldView localWV, ILifecycleBus bus);
	
	// ======
	// EVENTS
	// ======

	
	//TODO think about adding 1 listener level (specific agent)
	
	/**
	 * Adds listener to a specific event (Level A listeners). Note that the event listener must be able
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
	public void addEventListener(Class<?> eventClass, IWorldEventListener<?> listener);
	
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
	public void addObjectListener(Class<?> objectClass, IWorldObjectEventListener<?, ?> listener);

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
	public void addObjectListener(Class<?> objectClass, Class<?> eventClass, IWorldObjectEventListener<?,?> listener);

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
	public void addObjectListener(WorldObjectId objectId, IWorldObjectEventListener<?, ?> listener);

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
	public void addObjectListener(WorldObjectId objectId, Class<?> eventClass, IWorldObjectEventListener<?, ?> listener);

	/**
	 * Removes listener from a specific event (Level A listeners).
	 *
	 * @param eventClass which events class you want to remove the listener from
	 * @param listener you want to remove
	 */
	public void removeEventListener(Class<?> eventClass, IWorldEventListener<?> listener);

	/**
	 * Removes listener from specific 'objectClass' listening for specified 'event' (Level B listeners).
	 *
	 * @param objectClass class of objects you want the listener to remove from
	 * @param eventClass which events class you want to remove the listener from
	 * @param listener you want to remove
	 */
	public void removeObjectListener(Class<?> objectClass, IWorldObjectEventListener<?,?> listener);
	
	/**
	 * Removes listener from specific 'objectClass' listening for specified 'event' (Level C listeners).
	 *
	 * @param objectClass class of objects you want the listener to remove from
	 * @param eventClass which events class you want to remove the listener from
	 * @param listener you want to remove
	 */
	public void removeObjectListener(Class<?> objectClass, Class<?> eventClass, IWorldObjectEventListener<?,?> listener);


	/**
	 * Removes listener from objects with specified 'objectId' (Level D Listeners).
	 *
	 * @param objectId id of object you want the listener to remove from
	 * @param listener you want to remove
	 */
	public void removeObjectListener(WorldObjectId objectId, IWorldObjectEventListener<?, ?> listener);

	/**
	 * Removes listener to a specified 'event' that occurs on the specific object with 'objectId' (Level E listeners).
	 *
	 * @param objectId id of object you want the listener to remove from
	 * @param eventClass event class you want to stop receiving in the listener
	 * @param listener you want to remove
	 */
	public void removeObjectListener(WorldObjectId objectId, Class<?> eventClass, IWorldObjectEventListener<?, ?> listener);

	/**
	 * Removes listener from every listeners category (from every listener level).
	 * <p><p>
	 * <b>WARNING:</b> Can be time consuming! (Iterating through all levels of listeners.)
	 *
	 * @param listener you want to remove from all listener levels
	 */
	public void removeListener(IWorldEventListener<?> listener);

	/**
	 * Tests whether the 'listener' is listening to a specific event (Level A listeners).
	 *
	 * @param eventClass which events you want to receive
	 * @param listener that is tested
	 * @return whether the listener is listening
	 */
	public boolean isListening(Class<?> eventClass, IWorldEventListener<?> listener);

	/**
	 * Tests whether the 'listener' is listening at specified 'objectClass' (Level B listeners).
	 *
	 * @param objectClass where the listener is tested
	 * @param listener that is tested
	 * @return whether the listener is listening
	 */
	public boolean isListening(Class<?> objectClass, IWorldObjectEventListener<?,?> listener);	
	
	/**
	 * Tests whether the 'listener' is listening at specified 'objectClass' for specified 'event' (Level C listeners).
	 *
	 * @param objectClass where the listener is tested
	 * @param eventClass where the listener is tested
	 * @param listener that is tested
	 * @return whether the listener is listening
	 */
	public boolean isListening(Class<?> objectClass, Class<?> eventClass, IWorldObjectEventListener<?,?> listener);


	/**
	 * Tests whether the 'listener' is listening at specified 'objectId' (Level D Listeners).
	 *
	 * @param objectId where the listener is tested
	 * @param listener that is tested
	 * @return whether the listener is listening
	 */
	public boolean isListening(WorldObjectId objectId, IWorldObjectEventListener<?, ?> listener);

	/**
	 * Tests whether the 'listener' is listening to a specified 'event' that occurs on the specific object with 'objectId' (Level E listeners).
	 *
	 * @param objectId where the listener is tested
	 * @param eventClass what class is tested
	 * @param listener that is tested
	 * @return whether the listener is listening
	 */
	public boolean isListening(WorldObjectId objectId, Class<?> eventClass, IWorldObjectEventListener<?, ?> listener);

	/**
	 * Checks whether this listener is hooked to the world view (at any listener level).
	 * <p><p>
	 * <b>WARNING:</b> Can be time consuming! (Iterating through all levels of listeners.)
	 *
	 * @param listener
	 * @return
	 */
	public boolean isListening(IWorldEventListener<?> listener);

	// =======
	// OBJECTS
	// =======

	/**
	 * Returns only the static part of a requested object, static part of each objects only contains properties,
	 * that will NOT be changed over time. Therefore the static part of each object is immutable and always up-to-date.
	 * @param id - WorldObjectId of the entire WorldObject
	 */
	public IStaticWorldObject getStatic(WorldObjectId id);
	
	/**
	 * Returns the shared part of a requested object. Shared part of every worldObject contains properties, that are relevant
	 * to multiple agents (usually a team) and are not subjective in nature (every agent sees them differently).
	 * Good example is a boolean property isSpawned . Also note, that many shared properties are computed from other knowledge
	 * about the object (isVisible => isSpawned). For this reason, the value of a shared property can get "dirty" over time, so don't
	 * forget to check if your property is up-to-date.
	 * @param teamId
	 * @param objectId
	 * @return
	 */
	public ISharedWorldObject getShared(ITeamId teamId, WorldObjectId objectId, TimeKey time); 

	
}
