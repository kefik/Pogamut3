package cz.cuni.amis.pogamut.multi.communication.worldview;

import java.util.Map;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldChangeEventInput;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectDestroyedEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectFirstEncounteredEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentNotRunningException;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentPausedException;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base3d.worldview.object.event.WorldObjectAppearedEvent;
import cz.cuni.amis.pogamut.base3d.worldview.object.event.WorldObjectDisappearedEvent;
import cz.cuni.amis.pogamut.multi.agent.ITeamedAgentId;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ICompositeWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ILocalWorldObject;
import cz.cuni.amis.pogamut.multi.utils.exception.TimeKeyNotLockedException;
import cz.cuni.amis.pogamut.multi.utils.timekey.TimeKey;

/**
 * Interface for a World View local to a single agent in a multi-agent system
 * any implementation must implement the necessary object maps!
 * 
 * TODO: [srlok] what are the problems using TimeKey? does it always return the information to any TimeKey I may obtain?
 *       Am I supposed to always provide the TimeKey that is Current? What about locking?
 * 
 * @author srlok
 *
 */
public interface ILocalWorldView extends IWorldChangeEventInput {
	
	
	// =========
	// COMPONENT
	// =========
	
	public IComponentBus getEventBus();
	
	/**
	 * Calls notify without waiting for batches, locks or anything else.
	 */
	public void notifyImmediately( IWorldChangeEvent event )
		throws ComponentNotRunningException, ComponentPausedException;
	
	//=======//
	//OBJECTS//
	//=======//
	
	//GET METHODS//
	//local-oriented//
	
	public ITeamedAgentId getAgentId();
	
	/**
	 * Returns the most current LocalObject. <BR>
	 * Returns the local part of requested WorldObject. A local part for every object contains properties subjective to only one agent.
	 * (like isVisible). 
	 * @param objectId
	 * @return
	 */
	public ILocalWorldObject getLocal( WorldObjectId objectId);
	
	//////////////////////
	//composite-oriented//
	//////////////////////
	

	/**
	 * 
	 * @param objectId
	 * @return
	 */
	public ICompositeWorldObject get( WorldObjectId objectId );

        /**
         * Returns object with specific id and class, if it exists.
         * @param <T>
         * @param objectId
         * @param clazz
         * @return The object or null if it does not exist
         * @throws ClassCastException if the object exists, but is not of specified class
         */
        public <T extends ICompositeWorldObject> T get( WorldObjectId objectId, Class<T> clazz);
	
	/**
	 * Returns a map of all CompositeWorldObjects in the world. <br>
	 * The map is lazy, the Composite objects are created only when a get() method is called.
	 * If you need to iterate over this map and you don't need all of the values
	 * iterate over the keySet. 
	 * <br>
	 * The map will contain objects current to timeKey actual on calling of this method.
	 * @return
	 */
	public Map<WorldObjectId, ICompositeWorldObject> get();
	
	
	
	/**
	 * Returns all objects sorted according to class. All of the classMaps are lazy-implemented, so the CompositeObject will only be created
	 * on get method. If you need to iterate over the map, but you don't need the actual objects, iterate over the keySet.
	 * Do not hold reference to this map! Always use new getAll() call when you need this map again or you risk that some classMaps will get
	 * new timeKeys. By calling getAll(Class, time != thisTime).
	 * @return
	 */
	public Map<Class, Map<WorldObjectId, ICompositeWorldObject>> getAll();
	
	
	/**
	 * 
	 * @param <T>
	 * @param type
	 * @return
	 */
	public <T extends IWorldObject> Map<WorldObjectId, T> getAll(Class<T> type);
	
	
	//singleton oriented//
	
	/**
	 * 
	 */
	public <T extends IWorldObject> T getSingle(Class<T> cls);
		
	//========//
	//TIME_KEY//
	//========//
	
	public boolean setInitialTime( TimeKey timeKey );
	public boolean setCurrentTime( TimeKey timeKey );
	
	
	void lockTime(long time);
	void unlockTime(long time) throws TimeKeyNotLockedException;
	TimeKey getCurrentTimeKey();
	
	//=========//
	//LISTENERS//
	//=========//
	
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

	
}
