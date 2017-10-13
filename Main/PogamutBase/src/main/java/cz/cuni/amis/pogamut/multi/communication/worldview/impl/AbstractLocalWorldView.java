package cz.cuni.amis.pogamut.multi.communication.worldview.impl;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.controller.ComponentControlHelper;
import cz.cuni.amis.pogamut.base.component.controller.ComponentController;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.controller.IComponentControlHelper;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.ILifecycleBus;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.multi.agent.ITeamedAgentId;
import cz.cuni.amis.pogamut.multi.communication.worldview.ILocalWorldView;
import cz.cuni.amis.pogamut.multi.communication.worldview.ISharedWorldView;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ICompositeWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ILocalWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.IStaticWorldObject;
import cz.cuni.amis.pogamut.multi.utils.exception.TimeKeyNotLockedException;
import cz.cuni.amis.pogamut.multi.utils.timekey.TimeKey;
import cz.cuni.amis.pogamut.multi.utils.timekey.TimeKeyManager;
import cz.cuni.amis.utils.ClassUtils;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.listener.IListener;
import cz.cuni.amis.utils.listener.Listeners;
import cz.cuni.amis.utils.listener.ListenersMap;
import cz.cuni.amis.utils.maps.AbstractLazyMap;
import cz.cuni.amis.utils.maps.HashMapSet;
import cz.cuni.amis.utils.maps.LazyMap;
import cz.cuni.amis.utils.maps.WeakHashMapMap;
import cz.cuni.amis.utils.token.Token;
import cz.cuni.amis.utils.token.Tokens;

/**
 * WorldView responsible for single agent.
 * The LocalWorldView always knows which timeKey is considered actual to its agent and makes sure, that the objects stay consistent
 * for that timeKey and that newer objects are stored for further use.
 * Abstract worldView handles data management and listener handling.
 * @author srlok
 *
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractLocalWorldView implements ILocalWorldView {
	
	public static final Token COMPONENT_ID = Tokens.get("AbstractLocalWorldView");
	
	//sharedWorldView encompassing the entire game
	protected ISharedWorldView sharedWorldView;
	//agentId of the agent associated with this worldView
	protected ITeamedAgentId agentId;
	
	// OBJECT MAPS //
	//local objects//
	
	//objects belonging to current time-key
	protected Map<WorldObjectId, ILocalWorldObject> actLocalWorldObjects =
				Collections.synchronizedMap( new HashMap<WorldObjectId, ILocalWorldObject>() );
	
	//map used to determine the keySet of maps returned when getAll(class) is called.
	protected HashMapSet<Class, WorldObjectId> classMap = 
				new HashMapSet<Class,WorldObjectId>();

	protected Map<Class, Set<WorldObjectId> > syncClassMap = 
				Collections.synchronizedMap( classMap );
	
	//saved different TimeKey-versions of localObjects
	WeakHashMapMap<TimeKey, WorldObjectId, ILocalWorldObject> localWorldObjects = 
		new WeakHashMapMap<TimeKey, WorldObjectId, ILocalWorldObject>();
	
	//composite objects //
	// ??? //
	/**
	 * 
	 * The map contains: <Class<?>, ? extends Map< WorldObjectId, ? extends ICompositeWorldObject>>
	 * But due to generic quirks, we must keep it untemplated.
	 */
	HashMap compositeClassMap = 
		new HashMap<Class<?>, LazyCompositeObjectMap<ICompositeWorldObject>>();
	
	/**
	 * This map acts as a cache for already-created compositeObjects, the weak reference in this map
	 * makes sure that objects no longer needed are removed by the garbage collector whenever it is needed.
	 */
	WeakHashMapMap<TimeKey, WorldObjectId, ICompositeWorldObject> cachedCompositeWorldObjects = 
		new WeakHashMapMap<TimeKey, WorldObjectId, ICompositeWorldObject>();
	
	//TIME KEYS//
	TimeKey currentTimeKey;
	
	
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
					ListenersMap<Class> listeners = new ListenersMap<Class>();
					listeners.setLog(log, "LevelC-" + key.getSimpleName());
					return listeners;
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
					ListenersMap<Class> listeners = new ListenersMap<Class>();
					listeners.setLog(log, "LevelE-" + key.getStringId());
					return listeners;
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
		
	protected LogCategory log;

	protected ILifecycleBus eventBus;

	protected ComponentController<IComponent> controller;
	

	public AbstractLocalWorldView(ComponentDependencies dependencies, ILifecycleBus bus, IAgentLogger logger, ISharedWorldView sharedWV, ITeamedAgentId agentId) {
		this.log = logger.getCategory(getComponentId().getToken());		
		this.agentId = agentId;
		this.eventBus = bus;
		this.controller = new ComponentController(this, control, this.eventBus, this.log, dependencies);
		this.sharedWorldView = sharedWV;
		sharedWorldView.registerLocalWorldView(this, bus);
		
		this.eventListeners.setLog(log, "LevelA");
		this.objectsListeners.setLog(log, "LevelB");
		this.specificObjectListeners.setLog(log, "LevelD");
		
	}
	

	//
	//
	// WORLD VIEW CONTROL
	//
	//
	
    protected IComponentControlHelper control = new ComponentControlHelper() {
		
    	@Override
		public void start() throws PogamutException {
    		log.finest("LocalWorldView [ " + agentId + " ] Starting...");
    		AbstractLocalWorldView.this.start();
		}
    	
    	@Override
		public void prePause() throws PogamutException {
    		AbstractLocalWorldView.this.prePause();
		}
    	
    	@Override
		public void pause() throws PogamutException {
    		AbstractLocalWorldView.this.pause();
    	}
    	
    	@Override
		public void resume() throws PogamutException {
    		AbstractLocalWorldView.this.resume();
    	}
    	
    	@Override
		public void preStop() throws PogamutException {
    		AbstractLocalWorldView.this.preStop();
		}
    	
    	@Override
		public void stop() throws PogamutException {
    		AbstractLocalWorldView.this.stop();
		}
		
    	@Override
		public void kill() {
    		AbstractLocalWorldView.this.kill();
		}
		
		@Override
		public void reset() {
			AbstractLocalWorldView.this.reset();
		}
		
	};
	
	/**
	 * Cleans up internal data structures, called from start/stop/kill/reset methods.
	 * <p><p>
	 * If you override this method, do not forget to call super.cleanUp().
	 */
	protected void cleanUp() {
		synchronized( this.actLocalWorldObjects ) {
			actLocalWorldObjects.clear();
		}
		synchronized(classMap) {
			classMap.clear();
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
	
	
	
	/**
	 * <p>Method used for returning the appropriate CompositeObject type made from the three provided object parts.
	 * All parts must share the same WorldObjectId and must belong to the same CompositeObject class. Also, none of the parts
	 * may be null. </p>
	 * <p>The method should return a proxy-type CompositeObject, meaning that the object will only wrap all objectParts and no data
	 * will actually be copied.</p>
	 * <p>This method must be <i>overriden</i> by a object-awareWorldView which has information about all the object classes in the world and
	 * is able to call the correct constructors</p>
	 * @param localObject local part of the object
	 * @param sharedObject shared part of the object
	 * @param staticObject static part of the object
	 * @return Proxy-type compositeObject.
	 */
	protected abstract ICompositeWorldObject createCompositeObject(ILocalWorldObject localObject, ISharedWorldObject sharedObject, IStaticWorldObject staticObject);
	
	
	/**
	 * Returns agentId of the agent associated with this WorldView.
	 * @return teamed AgentId 
	 */
	public ITeamedAgentId getAgentId()
	{
		return this.agentId;
	}
	
	  //////////////////
	 //OBJECT METHODS//
	//////////////////
	
	/**
	 * This is a class for lazy maps holding CompositeWorldObjects . These maps will be returned by all getAll methods,
	 * the map ensures, that the CompositeObject creation from its parts is delayed as long as possible (it will only be invoked,
	 * when the object itself is requested).
	 * <br>
	 * If you need to iterate over the map, iterate over the keySet, so you will preserve the lazy-behavior.
	 */
	protected class LazyCompositeObjectMap<T extends ICompositeWorldObject> extends AbstractLazyMap<WorldObjectId,T>
	{
		
		private long currentTime;
		private WeakReference<TimeKey> timeKey;
		
		public LazyCompositeObjectMap( long time)
		{
			super();
			this.currentTime = time;
			this.timeKey = new WeakReference<TimeKey>(TimeKey.get(currentTime));
		}
		
		LazyCompositeObjectMap(Set<WorldObjectId> keySet, long time)
		{
			super(keySet);
			this.currentTime = time;
			this.timeKey = new WeakReference<TimeKey>(TimeKey.get(currentTime));
		}
		
		public boolean setTimeKey( long newTime)
		{
			if (currentTimeKey.equals(newTime))
			{
				return false;
			}
			super.clearCache();
			this.currentTime = newTime;		
			this.timeKey = new WeakReference<TimeKey>(TimeKey.get(currentTime));
			return true;
		}
		
		@Override
		protected T create(Object key) {
			return (T)AbstractLocalWorldView.this.get((WorldObjectId)key, this.timeKey.get());
		}
		
	}
	
	
	/////////////////
	//LOCAL objects//
	/////////////////
	
	@Override
	public ILocalWorldObject getLocal(WorldObjectId objectId) {
		return getLocal(objectId, getCurrentTimeKey() );
	}

	/**
	 * Returns the LocalObject associated with the provided TimeKey.
	 * Returns the local part of requested WorldObject. A local part for every object contains properties subjective to only one agent.
	 * (like isVisible). 
	 * @param objectId
	 * @param time
	 * @return
	 */
	protected ILocalWorldObject getLocal(WorldObjectId objectId, TimeKey time) {
		ILocalWorldObject value = localWorldObjects.get( time, objectId );
		if ( value != null) { return value; }
		return actLocalWorldObjects.get( objectId );
	}

	
	// COMPOSITE objects //
	
	/**
	 * Returns the object actual to the specified TimeKey.
	 * Returns the CompositeWorldObject for the requested Id. Composite objects behave like old WorldObjects, they contain all object fields
	 * and the data is guaranteed to be consistent (meaning all fields are actual for the same timeKey).
	 * Any object returned by this method should be compatible with any non-multi method (ie. a method expecting just IWorldObject).
	 * @param objectId
	 * @param time
	 * @return
	 * @throws
	 */
	protected ICompositeWorldObject get(WorldObjectId objectId, TimeKey time) {
		ICompositeWorldObject obj = cachedCompositeWorldObjects.get( time, objectId);
		if ( obj != null )
		{
			return obj;
		}		
		else
		{
		obj = createCompositeObject(
				this.getLocal( objectId, time ),
				sharedWorldView.getShared(agentId.getTeamId(), objectId, time),
				sharedWorldView.getStatic(objectId));
				cachedCompositeWorldObjects.put(time,objectId,obj);
		}
		return obj;
	}

	@Override
	public ICompositeWorldObject get(WorldObjectId objectId) {
		return this.get(objectId, getCurrentTimeKey() );
	}

        @Override
        public <T extends ICompositeWorldObject> T get(WorldObjectId objectId, Class<T> clazz) {
            IWorldObject obj = get(objectId);
            if(obj == null){
                return null;
            }
            else if(clazz.isAssignableFrom(obj.getClass())){
                return (T)obj;
            } else {
                throw new ClassCastException("Object with id " + objectId + " is not of class " + clazz);
            }
        }                

	/**
	 * Returns all objects sorted according to class. All of the classMaps are lazy-implemented, so the CompositeObject will only be created
	 * on get method. If you need to iterate over the map, but you don't need the actual objects, iterate over the keySet.
	 * Do not hold reference to this map! Always use new getAll() call when you need this map again or you risk that some classMaps will get
	 * new timeKeys. By calling getAll(Class, time != thisTime).
	 * @param time
	 * @return
	 */
	protected Map<Class, Map<WorldObjectId, ICompositeWorldObject>> getAll(TimeKey time) {
		for ( Object oC : compositeClassMap.keySet() )
		{
			Class c = (Class)oC;
			((LazyCompositeObjectMap)(compositeClassMap.get(c))).setTimeKey( time.getTime() );
		}
		return compositeClassMap;
	}

	//TODO maybe ensuring this doesn't happen???
	@Override
	/***
	 * Careful, do not store this map, always use a new getAll() call since the map can get invalidated when timeKeys change
	 * the map returned from this method, will overwrite the sub-map returned from this map.
	 */
	public Map<Class, Map<WorldObjectId, ICompositeWorldObject>> getAll() {
		return getAll(getCurrentTimeKey());
	}

	/**
	 * Returns a lazy-implemented classMap containing CompositeWorldObjects current to the specified TimeKey.
	 * <p> WARNING : do not store this map, always use the getAll() method or copy the map if you really need it stored somewhere. 
	 * The TimeKey can be overriden by calling another getAll( Class==Class, time!=time) .
	 * @param <T>
	 * @param type
	 * @param time
	 * @return
	 */
	protected <T extends IWorldObject> Map<WorldObjectId, T> getAll(Class<T> type,
			TimeKey time) 
	{
		LazyCompositeObjectMap map = (LazyCompositeObjectMap) this.compositeClassMap.get(type);
		if ( map == null )
		{
			map = new LazyCompositeObjectMap(time.getTime());
			compositeClassMap.put(type, map);
			return map;
		}
		
		map.setTimeKey(time.getTime());
		return map;
	}

	@Override
	public <T extends IWorldObject> Map<WorldObjectId, T> getAll(Class<T> type) {
		return getAll(type, getCurrentTimeKey());
	}

	@Override
	public Map<WorldObjectId, ICompositeWorldObject> get() {
		return get(getCurrentTimeKey());
	}

	/**
	 * Returns a map of all CompositeWorldObjects in the world. <br>
	 * The map is lazy, the Composite objects are created only when a get() method is called.
	 * If you need to iterate over this map and you don't need all of the values
	 * iterate over the keySet. 
	 * <br>
	 * The map will contain objects current to the provided TimeKey.
	 * @param time
	 * @return
	 */
	protected Map<WorldObjectId, ICompositeWorldObject> get(TimeKey time)
	{
		LazyCompositeObjectMap objects = new LazyCompositeObjectMap(this.actLocalWorldObjects.keySet(),time.getTime());
		return objects;
	}
	
	//SINGLE//
	
	@Override
	public <T extends IWorldObject> T getSingle(Class<T> cls) {
		return this.getSingle(cls, this.getCurrentTimeKey());
	}
	
	
	protected <T extends IWorldObject> T getSingle(Class<T> cls, TimeKey time) {
		Collection<T> vals = getAll(cls, time).values();
		if (vals.size() == 1)
		{
			return vals.iterator().next();
		}
		throw new IllegalArgumentException();
	}
	
	//protected object methods//
	
	/**
	 * Returns the most recent instance of object with the specified id.
	 * 
	 * @param id id of the object
	 * 
	 * @return most recent object instance
	 */
	protected ILocalWorldObject getMostRecentLocalWorldObject( WorldObjectId id )
	{
		return this.actLocalWorldObjects.get( id );
	}
	
	/**
	 * Adds this object as old for all heldTimeKeys that currently hold no old copy of this object.
	 */
	protected synchronized void addOldLocalWorldObject( ILocalWorldObject obj, long eventTime)
	{
		
		for (Long time : TimeKeyManager.get().getHeldKeys() )
		{
			if ( time < eventTime )
			{				
				synchronized( localWorldObjects )
				{
					TimeKey timeKey = TimeKey.get(time);
					if ( localWorldObjects.get( timeKey, obj.getId() ) == null)
					{
						//log.fine("Adding shadowCopy : [Et:"+eventTime+";St:"+timeKey.getTime()+"] ; " + obj.toString());
						localWorldObjects.put( timeKey , obj.getId(),obj);
					}
				}
			}
		}
	}
	
	/**
	 * Helper method for adding a new object into all object maps. (used in CREATED events).
	 * @param obj
	 */
	protected synchronized void addLocalWorldObject( ILocalWorldObject obj)
	{
		actLocalWorldObjects.put(obj.getId(), obj);
		for ( Class cls : ClassUtils.getSubclasses(obj.getCompositeClass()) )
		{
			//????
			LazyCompositeObjectMap map = (LazyCompositeObjectMap)compositeClassMap.get(cls);
			if ( map == null)
			{
				map = new LazyCompositeObjectMap( currentTimeKey.getTime() );
				compositeClassMap.put(cls, map);
			}
			map.keySet().add(obj.getId());	
			syncClassMap.get(cls).add( obj.getId() );
		}
		
	}
	
	/**
	 * Helper method to remove a localWorldObject from all corresponding object maps (used in DESTROYED events).
	 * @param obj
	 */
	protected synchronized void removeLocalWorldObject( ILocalWorldObject obj)
	{
		actLocalWorldObjects.remove(obj.getId());
		for ( Class cls : ClassUtils.getSubclasses(obj.getCompositeClass()) )
		{
			LazyCompositeObjectMap map = (LazyCompositeObjectMap)compositeClassMap.get(cls);
			if ( map != null )
			{
				map.keySet().remove(obj.getId());
			}
			syncClassMap.get(cls).remove(obj.getId());
		}
		
	}
	
	////////////
	//TIME-KEY//
	////////////
	
	/**
	 * Method for initializing the first timeKey, returns false if the
	 * TimeKey is already set.s
	 * @param key
	 * @return
	 */
	@Override
	public boolean setInitialTime( TimeKey key )
	{
		if ( currentTimeKey == null )
		{
			currentTimeKey = key;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean setCurrentTime( TimeKey key )
	{
		currentTimeKey = key;
		return true;
	}
	
	@Override
	public TimeKey getCurrentTimeKey() {
		return this.currentTimeKey;
	}

	@Override
	public void lockTime(long time) {
		TimeKeyManager.get().lock(time);
	}

	@Override
	public void unlockTime(long time) {
		try {
			TimeKeyManager.get().unlock(time);
		} catch (TimeKeyNotLockedException e) {
			log.warning("Trying to unlock timeKey : " + time + " which is not locked. ");
		}
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

	@Override
	public ILifecycleBus getEventBus() {
		return this.eventBus;
	}

}
