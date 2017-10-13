package cz.cuni.amis.pogamut.multi.communication.worldview.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.controller.ISharedComponentControlHelper;
import cz.cuni.amis.pogamut.base.component.controller.ISharedComponentController;
import cz.cuni.amis.pogamut.base.component.controller.SharedComponentControlHelper;
import cz.cuni.amis.pogamut.base.component.controller.SharedComponentController;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.ILifecycleBus;
import cz.cuni.amis.pogamut.multi.agent.ITeamId;
import cz.cuni.amis.pogamut.multi.agent.impl.TeamedAgentId;
import cz.cuni.amis.pogamut.multi.communication.worldview.ILocalWorldView;
import cz.cuni.amis.pogamut.multi.communication.worldview.ISharedWorldView;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedProperty;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.IStaticWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.property.PropertyId;
import cz.cuni.amis.pogamut.multi.utils.timekey.TimeKey;
import cz.cuni.amis.pogamut.multi.utils.timekey.TimeKeyManager;
import cz.cuni.amis.utils.ClassUtils;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.listener.IListener;
import cz.cuni.amis.utils.listener.Listeners;
import cz.cuni.amis.utils.listener.ListenersMap;
import cz.cuni.amis.utils.maps.HashTriMap;
import cz.cuni.amis.utils.maps.LazyMap;
import cz.cuni.amis.utils.maps.WeakHashQuadMap;
import cz.cuni.amis.utils.maps.WeakHashTriMap;
import cz.cuni.amis.utils.token.Token;
import cz.cuni.amis.utils.token.Tokens;

@SuppressWarnings("unchecked")
public abstract class AbstractSharedWorldView implements ISharedWorldView {

	public static final Token COMPONENT_ID = Tokens.get("AbstractSharedWorldView");
	
	/**
	 * LocalWorldViews registered with this sharedWorldView
	 */
	protected HashMap<TeamedAgentId, ILocalWorldView> localWorldViews = new HashMap<TeamedAgentId, ILocalWorldView>();
	
	/**
	 * Holds all sharedProperties in the world weakly-referenced by timeKey, this map is used for storing shadowCopies of properties.
	 */
	protected WeakHashQuadMap<TimeKey, ITeamId, WorldObjectId, PropertyId, ISharedProperty> sharedProperties =
		new WeakHashQuadMap<TimeKey, ITeamId, WorldObjectId, PropertyId, ISharedProperty>(8,8,32,8);
	
	/**
	 * Synchronized version of sharedProperties. If you iterate over this map, you must still synchronize manually however!
	 */
	protected Map<TimeKey, Map<ITeamId, Map<WorldObjectId, Map<PropertyId, ISharedProperty>>>> syncSharedProperties = 
		Collections.synchronizedMap( sharedProperties );
	
	/**
	 * SharedProperties currently considered most-recent.
	 */
	protected HashTriMap<ITeamId, WorldObjectId, PropertyId, ISharedProperty> currentSharedProperties = 
		new HashTriMap<ITeamId, WorldObjectId, PropertyId, ISharedProperty>(8,32,8);
	
	/**
	 * Synchronized version of currentSharedProperties. If you iterate over this map, you must still synchronize manually however!
	 */
	protected Map<ITeamId, Map<WorldObjectId, Map<PropertyId, ISharedProperty>>> syncCurrentSharedProperties =
		Collections.synchronizedMap( currentSharedProperties );
	
	/**
	 * Map of staticWorldObjects, these objects never change, so there is no need for shadow copies.
	 */
	protected Map<WorldObjectId, IStaticWorldObject> staticWorldObjects =
		Collections.synchronizedMap( new HashMap<WorldObjectId, IStaticWorldObject>(32) );
	
	/**
	 * Cached sharedWorldObjects.
	 */
	protected WeakHashTriMap<TimeKey, ITeamId, WorldObjectId, ISharedWorldObject> sharedWorldObjects = 
		new WeakHashTriMap<TimeKey, ITeamId, WorldObjectId, ISharedWorldObject>(8,8,32);
	
	/**
	 * Synchronized version of cached sharedWorldObjects.
	 */
	protected Map<TimeKey, Map<ITeamId, Map<WorldObjectId, ISharedWorldObject>>> syncSharedWorldObjects = 
		Collections.synchronizedMap( sharedWorldObjects );
	
	protected HashMap<WorldObjectId, Class> idClassMap = new HashMap<WorldObjectId, Class>();
	
	protected Map<WorldObjectId, Class> syncIdClassMap = Collections.synchronizedMap( idClassMap ); 
	
	//WORLDVIEW CONTROL//
	
	/**
	 * This method is  called when a new localWorldView is created and wants to use this sharedWorldView,
	 * the method registers the LocalWorldView with the sharedWorldView's sharedComponentBus and also
	 * internally stores the information about which WorldViews are registered to it.
	 * 
	 * @param localWV The local WorldView to to register.
	 * @param bus ILifecycleBus of the corresponding LocalWorldView
	 */
	@Override
	public void registerLocalWorldView( ILocalWorldView localWV, ILifecycleBus bus) {
		this.localWorldViews.put( (TeamedAgentId)localWV.getAgentId(), localWV);
		this.addComponentBus( localWV.getAgentId(), bus, new ComponentDependencies() );
	}
	
	//OBJECTS//
	
	/**
	 * Returns exactly the requested property
	 */
	protected ISharedProperty getSharedProperty(PropertyId id, ITeamId teamId, TimeKey time) {
		ISharedProperty result = syncSharedProperties.get(time).get(teamId).get(id.getWorldObjectId()).get(id);
		if ( result != null )
		{
			return result;
		}
		return currentSharedProperties.get(teamId, id.getWorldObjectId(), id);
	}
	
	/**
	 * Returns all shared properties belonging to the specified object.
	 * @param objectId
	 * @param teamId
	 * @param time
	 * @return
	 */
	protected Collection<ISharedProperty> getSharedProperties(WorldObjectId objectId, ITeamId teamId, TimeKey time)
	{
		LinkedList<ISharedProperty> lst = new LinkedList<ISharedProperty>();
		Set<PropertyId> set = syncCurrentSharedProperties.get(teamId).get(objectId).keySet();
		for ( PropertyId propId : set)
		{
			lst.add( getSharedProperty(propId, teamId, time) );
		}
		return lst;
	}
	

	/**
	 * Creates a sharedWorldObject of the specified id.
	 * This method constructs the objects from sharedProperties so it must be overriden by a WorldView that is aware of sharedObjectTypes
	 * and can construct the correct objects.
	 * @param id
	 * @param teamId
	 * @param time
	 * @return
	 */
	protected abstract ISharedWorldObject createSharedObject(Class msgClass, WorldObjectId id, ITeamId teamId, TimeKey time);

	@Override
	public ISharedWorldObject getShared(ITeamId teamId, WorldObjectId objectId, TimeKey time) {
		ISharedWorldObject value = sharedWorldObjects.get(time, teamId, objectId);
		
		if ( value != null ) // is object pre-cached
		{
			return value;
		}
	
		Class msgClass = syncIdClassMap.get(objectId);
		value =  createSharedObject(msgClass, objectId, teamId, time); //if not, create it
		if (value == null) {
			throw new PogamutException("SharedObject for objectId=" + objectId + ", teamId = " + teamId + ", time = " + time + ", could not have been created, createSharedObject(objectId, teamId, time) returned null!", this);
		}
		sharedWorldObjects.put(time, teamId, objectId, value); //cache the object
		return value;
	}

	@Override
	public IStaticWorldObject getStatic(WorldObjectId id) {
		return staticWorldObjects.get(id);
	}
	
	//PROTECTED OBJECT METHODS//
	
	/**
	 * if the object already exists, no changes are made
	 */
	protected void addStaticWorldObject(IStaticWorldObject object) {
		synchronized(this.staticWorldObjects) {
			this.staticWorldObjects.put(object.getId(), object);
		}
	}
	
	protected void removeStaticWorldObject(WorldObjectId id) {
		synchronized(this.staticWorldObjects) {
			this.staticWorldObjects.remove(id);
		}
	}
	
	protected void removeStaticWorldObject(IStaticWorldObject object) {
		synchronized(this.staticWorldObjects) {
			this.staticWorldObjects.remove(object.getId());
		}
	}
	
	/**
	 * adds this shared property only for the specified team
	 * @param property
	 * @param teamId
	 */
	protected void addSharedProperty(ISharedProperty property, ITeamId teamId) {
		synchronized(syncCurrentSharedProperties) {
			syncCurrentSharedProperties.get(teamId).get(property.getObjectId()).put(property.getPropertyId(),property);
		}
	}
	
	/**
	 * adds this shared property for all teams
	 * @param property
	 */
	protected void addSharedProperty(ISharedProperty property) {
		synchronized(syncCurrentSharedProperties) {
			for ( ITeamId team : syncCurrentSharedProperties.keySet() ) {
				this.addSharedProperty(property, team);
			}
		}
	}
	
	protected void removeSharedProperty(ISharedProperty property, ITeamId teamId) {
		if (teamId == null) {
			removeSharedProperty(property);
			return;
		}
		synchronized(syncCurrentSharedProperties) {
			this.syncCurrentSharedProperties.get(teamId).get(property.getObjectId()).remove(property.getPropertyId());
		}
	}
	
	protected void removeSharedProperty(ISharedProperty property) {
		synchronized(syncCurrentSharedProperties) {
			for (ITeamId team : syncCurrentSharedProperties.keySet()) {
				this.removeSharedProperty(property, team);
			}
		}
	}
	
	/**
	 * adds all shared properties from this object for all teams
	 * @param object
	 */
	protected void addSharedWorldObject(ISharedWorldObject object) {
		synchronized(syncCurrentSharedProperties) {
			for ( ISharedProperty property : object.getProperties().values() ) {
				addSharedProperty( property );
			}
		}
	}
	
	/**
	 * Adds the provided sharedProperty for all currently held timeKeys. Any old properties inserted before will NOT be overriden, this method
	 * only adds new shadowCopies.
	 * @param property
	 * @param teamId
	 * @param eventTime time of the event causing the property update
	 */
	protected void addOldSharedProperty(ISharedProperty property, ITeamId teamId, long eventTime) {
		for (Long t : TimeKeyManager.get().getHeldKeys()) 
		{
			if (t < eventTime) 
			{
				TimeKey timeKey = TimeKey.get(t);
				Map<PropertyId, ISharedProperty> props = sharedProperties.get(timeKey, teamId, property.getObjectId());
				synchronized(props) 
				{
					ISharedProperty old = props.get(property.getPropertyId());
					if ( old == null) 
					{
						
						props.put(property.getPropertyId(), property);
					}
				}
			}
		}
	}
	
	
	//LISTENERS//
	
	private static class ListenerNotifier<T> implements Listeners.ListenerNotifier<IListener> {

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
	private ListenerNotifier notifier = new ListenerNotifier();

	
	/**
	 * Level A Listeners
	 * <p><p>
	 * Map of the event listeners, key is the event class where the listener is hooked to.
	 */
	private ListenersMap<Class> eventListeners = new ListenersMap<Class>();
	
	/**
	 * Level B Listeners
	 * <p><p>
	 * Map of the object class to the object listeners.
	 */
	private ListenersMap<Class> objectsListeners = new ListenersMap<Class>();
	
	/**
	 * Level C listeners
	 * <p><p>
	 * Map of event listeners on some object class.
	 * <p><p>
	 * First key is eventClass, second key is objectClass.
	 */
	private Map<Class, ListenersMap<Class>> objectEventListeners = 
		Collections.synchronizedMap(
			new LazyMap<Class, ListenersMap<Class>>(){

				@Override
				protected ListenersMap<Class> create(Class key) {
					return new ListenersMap<Class>();
				}
			}
		);
		
	/**
	 * Level D Listeners
	 * <p><p>
	 * Listeners listening on all events on a specific object.
	 */
	private ListenersMap<WorldObjectId> specificObjectListeners = new ListenersMap<WorldObjectId>();
	
	/**
	 * Level E Listeners
	 * <p><p>
	 * Listeners listening for some specific event on some specific object.
	 * <p><p>
	 * Map of (IWorldObjectId, class of IWorldEvent or desc.).  
	 */
	private Map<WorldObjectId, ListenersMap<Class>> specificObjectEventListeners = 
		Collections.synchronizedMap(
			new LazyMap<WorldObjectId, ListenersMap<Class>>() {

				@Override
				protected ListenersMap<Class> create(WorldObjectId key) {
					return new ListenersMap<Class>();
				}
			}
		);
		
	    
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
	private Queue<IWorldEvent> raiseEventsList = new ConcurrentLinkedQueue<IWorldEvent>();
		
	protected Logger log;

	protected ISharedComponentController<ISharedWorldView> controller;

	public AbstractSharedWorldView( Logger logger) {
		this.log = logger;
		this.controller = new SharedComponentController<ISharedWorldView>(this, control, logger);
		
		this.eventListeners.setLog(log, "LevelA");
		this.objectsListeners.setLog(log, "LevelB");
		this.specificObjectListeners.setLog(log, "LevelD");
	}
	
	//
	//
	// WORLD VIEW CONTROL
	//
	//
	
	@Override
	public void addComponentBus(IAgentId agentId, ILifecycleBus bus,
			ComponentDependencies dependencies) {
		this.controller.addComponentBus(agentId, bus, dependencies);
		
	}

	@Override
	public void removeComponentBus(IAgentId agentId, ILifecycleBus bus) {
		this.controller.removeComponentBus(agentId, bus);		
	}

   
	
    protected ISharedComponentControlHelper control = new SharedComponentControlHelper() {
		
    	@Override
		public void start() throws PogamutException {
    		AbstractSharedWorldView.this.start();
		}
    	
    	@Override
		public void prePause() throws PogamutException {
    		AbstractSharedWorldView.this.prePause();
		}
    	
    	@Override
		public void pause() throws PogamutException {
    		AbstractSharedWorldView.this.pause();
    	}
    	
    	@Override
		public void resume() throws PogamutException {
    		AbstractSharedWorldView.this.resume();
    	}
    	
    	@Override
		public void preStop() throws PogamutException {
    		AbstractSharedWorldView.this.preStop();
		}
    	
    	@Override
		public void stop() throws PogamutException {
    		AbstractSharedWorldView.this.stop();
		}
		
    	@Override
		public void kill() {
    		AbstractSharedWorldView.this.kill();
		}
		
		@Override
		public void reset() {
			AbstractSharedWorldView.this.reset();
		}
		
	};
	
	/**
	 * Cleans up internal data structures, called from start/stop/kill/reset methods.
	 * <p><p>
	 * If you override this method, do not forget to call super.cleanUp().
	 */
	protected void cleanUp() {
		// TODO: srlok: should not we clear more stuff?
		synchronized( this.sharedProperties ) {
			sharedProperties.clear();
		}
		synchronized(this.staticWorldObjects) {
			staticWorldObjects.clear();
		}
		synchronized(this.sharedWorldObjects)
		{
			sharedWorldObjects.clear();
		}
		synchronized(raiseEventsList) {	
			raiseEventsList.clear();
		}
	}
	
	/**
	 * Starts the world view. 
	 * <p><p>
	 * If you override this method, do not forget to call super.start().
	 */
	protected void start() {
		cleanUp();
	}
	
	/**
	 * Pre-pauses the world view.
	 * <p><p>
	 * If you override this method, do not forget to call super.preStop().
	 */
	protected void prePause() {
	}
	
	/**
	 * Pauses the world view. 
	 * <p><p>
	 * If you override this method, do not forget to call super.start().
	 */
	protected void pause() {
	}
	
	/**
	 * Resumes the world view. 
	 * <p><p>
	 * If you override this method, do not forget to call super.start().
	 */
	protected void resume() {
	}
	
	/**
	 * Pre-stops the world view.
	 * <p><p>
	 * If you override this method, do not forget to call super.preStop().
	 */
	protected void preStop() {
	}
	
	/**
	 * Stops the world view.
	 * <p><p>
	 * If you override this method, do not forget to call super.stop().
	 */
	protected void stop() {
		cleanUp();
	}
	
	/**
	 * Kills the world view.
	 * <p><p>
	 * If you override this method, do not forget to call super.stop().
	 */
	protected void kill() {
		cleanUp();
	}

	/**
	 * Resets the world view so it is start()able again.
	 * <p><p>
	 * If you override this method, do not forget to call super.reset().
	 */
	protected void reset() {
		cleanUp();
	}
	
	protected boolean isRunning() {
		return controller.isRunning();
	}
	
	protected boolean isPaused() {
		return controller.isPaused();
	}
	
	@Override
	public Token getComponentId() {
		return COMPONENT_ID;
	}
	
	/////////////
	//LISTENERS//
	////////////
	
	@Override
	public void addEventListener(Class<?> event, IWorldEventListener<?> listener) {
		eventListeners.add(event, listener);
	}
	
	@Override
	public void addObjectListener(Class<?> objectClass, IWorldObjectEventListener<?, ?> listener) {
		objectsListeners.add(objectClass, listener);
	}

	@Override
	public void addObjectListener(Class<?> objectClass, Class<?> eventClass, IWorldObjectEventListener<?,?> listener) {
		ListenersMap<Class> listeners = objectEventListeners.get(eventClass);
		listeners.add(objectClass, listener);
	}
	
	@Override
	public void addObjectListener(WorldObjectId objectId, IWorldObjectEventListener<?, ?> listener) {
		specificObjectListeners.add(objectId, listener);
	}
	
	@Override
	public void addObjectListener(WorldObjectId objectId, Class<?> eventClass, IWorldObjectEventListener<?, ?> listener) {
		ListenersMap<Class> listeners = specificObjectEventListeners.get(objectId);
		listeners.add(eventClass, listener);
	}
	
	@Override
	public boolean isListening(Class<?> eventClass, IWorldEventListener<?> listener) {
		return eventListeners.isListening(eventClass, listener);
	}
	
	@Override
	public boolean isListening(Class<?> objectClass, IWorldObjectEventListener<?, ?> listener) {
		return objectsListeners.isListening(listener);
	}


	@Override
	public boolean isListening(Class<?> objectClass, Class<?> eventClass, IWorldObjectEventListener<?, ?> listener) {
		if (objectEventListeners.containsKey(objectClass)) { // be careful not to create unnecessary ListenersMap
			return objectEventListeners.get(eventClass).isListening(objectClass, listener);
		} else {
			return false;
		}
	}

	@Override
	public boolean isListening(WorldObjectId objectId, IWorldObjectEventListener<?, ?> listener) {
		return specificObjectListeners.isListening(objectId, listener);
	}

	@Override
	public boolean isListening(WorldObjectId objectId, Class<?> eventClass, IWorldObjectEventListener<?, ?> listener) {
		if (specificObjectEventListeners.containsKey(objectId)) { // be careful not to create unnecessary ListenersMap
			return specificObjectEventListeners.get(objectId).isListening(eventClass, listener);
		} else {
			return false;
		}
		
	}
	
	@Override
	public boolean isListening(IWorldEventListener<?> listener) {
		if (eventListeners.isListening(listener) ||
			specificObjectListeners.isListening(listener)) {
			return true;
		}
		synchronized(objectEventListeners) {
			for (ListenersMap<Class> listeners : objectEventListeners.values()) {
				if (listeners.isListening(listener)) return true;
			}
		}
		synchronized(specificObjectEventListeners) {
			for (ListenersMap<Class> listeners : specificObjectEventListeners.values()) {
				if (listeners.isListening(listener)) return true;
			}
		}
		return false;
	}

	@Override
	public void removeEventListener(Class<?> eventClass, IWorldEventListener<?> listener) {
		eventListeners.remove(eventClass, listener);
	}

	@Override
	public void removeObjectListener(Class<?> objectClass,	IWorldObjectEventListener<?, ?> listener) {
		objectsListeners.remove(objectClass, listener);
	}
	
	@Override
	public void removeObjectListener(Class<?> objectClass, Class<?> eventClass,	IWorldObjectEventListener<?, ?> listener) {
		if (objectEventListeners.containsKey(eventClass)) { // be careful not to create unnecessary ListenersMap
			objectEventListeners.get(eventClass).remove(objectClass, listener);
		}
	}

	@Override
	public void removeObjectListener(WorldObjectId objectId, IWorldObjectEventListener<?, ?> listener) {
		specificObjectListeners.remove(objectId, listener);
	}

	@Override
	public void removeObjectListener(WorldObjectId objectId, Class<?> eventClass, IWorldObjectEventListener<?, ?> listener) {
		if (specificObjectEventListeners.containsKey(objectId)) { // be careful not to create unnecessary ListenersMap
			specificObjectEventListeners.get(objectId).remove(eventClass, listener);
		}
	}
	
		
	@Override
	public void removeListener(IWorldEventListener<?> listener) {
		eventListeners.remove(listener);
		synchronized(objectEventListeners) {
			for (ListenersMap<Class> listeners : objectEventListeners.values()) {
				listeners.remove(listener);
			}
		}
		specificObjectListeners.remove(listener);
		specificObjectEventListeners.remove(listener);
	}

	// EVENT PROCESSING
	
	
	/**
	 * Process new IWorldEvent - notify all the listeners about it. Forbids recursion.
	 * <p><p>
	 * Use in the descendants to process new IWorldChangeEvent.
	 * <p><p>
	 * Does not catch any exceptions!
	 * <p><p>
	 * Synchronized!
	 * 
	 * @param event
	 */
	protected synchronized void raiseEvent(IWorldEvent event) {
		// method is synchronized - only one thread inside at given time
		
		// is this method recursively called? 
		if (raiseEventProcessing) {
			// yes it is -> that means the previous event has not been
			// processed! ... store this event and allows the previous one
			// to be fully processed (e.g. postpone raising this event)
			raiseEventsList.add(event);
			return;
		} else {
			// no it is not ... so raise the flag that we're inside the method
			raiseEventProcessing = true;
		}
		// process event
				
		innerRaiseEvent(event);
		
		// check the events list size, do we have more events to process?
		while(raiseEventsList.size() != 0) {
			// yes we do -> do it!
			innerRaiseEvent(raiseEventsList.poll());			
		}
		// all events has been processed, drop the flag that we're inside the method
		raiseEventProcessing = false;			
	}
		
	//
	//
	// EVENT PROCESSING
	//
	//
		
	/**
	 * Helper method used ONLY FROM innerRaiseEvent. DO NOT USE OUTSIDE THAT METHOD!
	 * @param event
	 */
	private void notifyLevelAListeners(IWorldEvent event) {
		Collection<Class> eventClasses = ClassUtils.getSubclasses(event.getClass());
		notifier.setEvent(event);
		for (Class eventClass : eventClasses) {
			eventListeners.notify(eventClass, notifier);
		}
	}
	
	/**
	 * Helper method used ONLY FROM innerRaiseEvent. DO NOT USE OUTSIDE THAT METHOD!
	 * @param event
	 */
	private void notifyLevelBListeners(IWorldObjectEvent event) {
		IWorldObject object = event.getObject();
		Collection<Class> objectClasses = ClassUtils.getSubclasses(object.getClass());
		notifier.setEvent(event);
		for (Class objectClass : objectClasses) {
			objectsListeners.notify(objectClass, notifier);
		}
	}

	
	/**
	 * Helper method used ONLY FROM innerRaiseEvent. DO NOT USE OUTSIDE THAT METHOD!
	 * @param event
	 */
	private void notifyLevelCListeners(IWorldObjectEvent event) {
		Collection<Class> eventClasses = ClassUtils.getSubclasses(event.getClass());
		Collection<Class> objectClasses = ClassUtils.getSubclasses(event.getObject().getClass());
		notifier.setEvent(event);
		for (Class eventClass : eventClasses) {
			ListenersMap<Class> listeners = objectEventListeners.get(eventClass);
			if (listeners == null) continue;
			if (!listeners.hasListeners()) continue;
			for (Class objectClass : objectClasses) {
				listeners.notify(objectClass, notifier);			
			}
		}
	}
	
	/**
	 * Helper method used ONLY FROM innerRaiseEvent. DO NOT USE OUTSIDE THAT METHOD!
	 * @param event
	 */
	private void notifyLevelDListeners(IWorldObjectEvent event) {
		notifier.setEvent(event);
		specificObjectListeners.notify(event.getId(), notifier);
	}
	
	/**
	 * Helper method used ONLY FROM innerRaiseEvent. DO NOT USE OUTSIDE THAT METHOD!
	 * @param event
	 */
	private void notifyLevelEListeners(IWorldObjectEvent event) {
		notifier.setEvent(event);
		WorldObjectId objectId = event.getId();
		ListenersMap<Class> listeners = specificObjectEventListeners.get(objectId);
		if (listeners.hasListeners()) {
			Collection<Class> eventClasses = ClassUtils.getSubclasses(event.getClass());
			notifier.setEvent(event);
			for (Class eventClass : eventClasses) {
				listeners.notify(eventClass, notifier);			
			}
		}
	}
	
	/**
	 * Process new IWorldEvent - DO NOT CALL SEPARATELY - must be called only from raiseEvent(),
	 * that forbids recursion of its calls.
	 * <p><p>
	 * Contains the sequence in which the listeners are informed about the event.
	 * @param event
	 */
	private void innerRaiseEvent(IWorldEvent event) {
		if (log.isLoggable(Level.FINEST)) log.finest("notifying " + event);
		notifyLevelAListeners(event);		
		if (event instanceof IWorldObjectEvent) {
			// now we may notify other listeners as well
			IWorldObjectEvent objectEvent = (IWorldObjectEvent)event;
			notifyLevelBListeners(objectEvent);
			notifyLevelCListeners(objectEvent);
			notifyLevelDListeners(objectEvent);
			notifyLevelEListeners(objectEvent);
		}
	}
}
