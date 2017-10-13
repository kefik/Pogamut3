package cz.cuni.amis.pogamut.base.communication.worldview.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.controller.ComponentControlHelper;
import cz.cuni.amis.pogamut.base.component.controller.ComponentController;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.controller.IComponentControlHelper;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.utils.ClassUtils;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.listener.IListener;
import cz.cuni.amis.utils.listener.Listeners;
import cz.cuni.amis.utils.listener.ListenersMap;
import cz.cuni.amis.utils.maps.HashMapMap;
import cz.cuni.amis.utils.maps.LazyMap;
import cz.cuni.amis.utils.token.Token;
import cz.cuni.amis.utils.token.Tokens;

/**
 * Abstract world view is implementing some of the tedious things every WorldView will surely
 * implement -&gt; maps for holding the references to all world objects either according to their
 * id and type (class). It also implements a map of listeners for events that may
 * be generated in the world.
 * <p><p>
 * For raising new IWorldEvent in descendants call protected method raiseEvent(IWorldEvent event), that
 * is preventing recursion.
 * <p><p>
 * Note that there is a big advantage in how the listeners are called and how objects are stored.<p>
 * The event notifying method (raiseEvent()) is respecting 
 * the class/interface hierarchy thus informing listeners hooked on all
 * levels of the hierarchy.
 * <p><p>
 * The items are stored according the the class/interface hierarchy as well!
 * <p><p>
 * <b>Example:</b>You have interface ItemEvent (extends IWorldObjectEvent) and it's implementation WeaponEvent and HealthEvent. Perheps
 * you want to listen for all events on WeaponEvent, so you will create IWorldEventListener&lt;WeaponEvent&gt;.
 * But hey - you may want to listen on both WeaponEvent and HealthEvent (and perheps any ItemEvent there is),
 * that's easy - just create IWorldEventListener&lt;ItemEvent&gt; and you will receive both WeaponEvent and HealthEvent.
 * That's because during event handling we're probing the event class ancestors / interfaces and informing
 * all listeners on all class-hierarchy levels. 
 * <p><p>
 * Ultimately you may create IWorldEventListener&lt;IWorldEvent&gt; to
 * catch all events the world view produces (be careful because it may cause serious performance hit if you
 * process these events slowly).
 * <p><p>
 * Same goes for storing the items under it's class in the 'worldObjects'.
 * 
 * @author Jimmy
 */
@AgentScoped
@SuppressWarnings("unchecked")
public abstract class AbstractWorldView implements IWorldView {
	
	public static final Token COMPONENT_ID = Tokens.get("WorldView");
	
	/**
	 * Class that notifies listeners about the world view event.
	 * @author Jimmy
	 */
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
	 * Unsynchronized map that holds all the objects according to their type
	 * in the maps.
	 * <p><p>
	 * Due to nature of generics we can't typed this field, it holds maps of objects
	 * according to their classes. 
	 * <p>
	 * Map &lt; Class, Map &lt; IWorldObjectId, IWorldObject of Class &gt; &gt;
	 */
	private HashMapMap<Class, WorldObjectId, IWorldObject> worldObjects = 
		new HashMapMap<Class, WorldObjectId, IWorldObject>();
	
	/**
	 * Synchronized version of world objects.
	 */
	private Map<Class, Map<WorldObjectId, IWorldObject>> syncWorldObjects = 
		Collections.synchronizedMap(worldObjects);
			
	/**
	 * Synchronized map of all the world objects that are present in the worldview.
	 */
	private Map<WorldObjectId, IWorldObject> knownObjects =
		Collections.synchronizedMap(new HashMap<WorldObjectId, IWorldObject>());
		
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

	protected IComponentBus eventBus;

	protected ComponentController<IComponent> controller;

	public AbstractWorldView(ComponentDependencies dependencies, IComponentBus bus, IAgentLogger logger) {
		this.log = logger.getCategory(getComponentId().getToken());		
		this.eventBus = bus;
		this.controller = new ComponentController(this, control, this.eventBus, this.log, dependencies);

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
		public void startPaused() {
    		AbstractWorldView.this.start(true);
		}
    	
    	@Override
		public void start() throws PogamutException {
    		AbstractWorldView.this.start(false);
		}
    	
    	@Override
		public void prePause() throws PogamutException {
    		AbstractWorldView.this.prePause();
		}
    	
    	@Override
		public void pause() throws PogamutException {
    		AbstractWorldView.this.pause();
    	}
    	
    	@Override
		public void resume() throws PogamutException {
    		AbstractWorldView.this.resume();
    	}
    	
    	@Override
		public void preStop() throws PogamutException {
    		AbstractWorldView.this.preStop();
		}
    	
    	@Override
		public void stop() throws PogamutException {
    		AbstractWorldView.this.stop();
		}
		
    	@Override
		public void kill() {
    		AbstractWorldView.this.kill();
		}
		
		@Override
		public void reset() {
			AbstractWorldView.this.reset();
		}
		
	};
	
	/**
	 * Cleans up internal data structures, called from start/stop/kill/reset methods.
	 * <p><p>
	 * If you override this method, do not forget to call super.cleanUp().
	 */
	protected void cleanUp() {
		synchronized(worldObjects) {
			worldObjects.clear();
		}
		synchronized(knownObjects) {
			knownObjects.clear();
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
	protected void start(boolean startPaused) {
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
	
	public LogCategory getLog() {
		return log;
	}
	
	//
	//
	// COMPONENTS
	//
	//
	
	@Override
	public IComponentBus getEventBus() {
		return eventBus;
	}
	
	//
	//
	// EVENT LISTENERS
	// 
	//
	
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
	
	//
	//
	// OBJECTS PUBLIC METHOD
	//
	//
	
	@Override
	public Map<Class, Map<WorldObjectId, IWorldObject>> getAll() {
		return syncWorldObjects;
	}
			
	@Override
	public <T extends IWorldObject> Map<WorldObjectId, T> getAll(Class<T> type) {	
		return (Map<WorldObjectId, T>) syncWorldObjects.get(type);
	}

    @Override
    public <T extends IWorldObject> T getSingle(Class<T> cls) {
        Collection<T> col = getAll(cls).values();
        if(col.size() > 1) throw new IllegalArgumentException("There must be at most one object of given class (" + cls.getName() + ") to use this method. But there were more instances (" + col.size() + ").");
        if(col.size() < 1) return null;
        synchronized(col) {
        	return col.iterator().next();
        }
    }

	@Override
	public IWorldObject get(WorldObjectId objectId) {
		return knownObjects.get(objectId);
	}

        @Override
        public <T extends IWorldObject> T get(WorldObjectId id, Class<T> clazz) {
            IWorldObject obj = get(id);
            if(obj == null){
                return null;
            }
            else if(clazz.isAssignableFrom(obj.getClass())){
                return (T)obj;
            } else {
                throw new ClassCastException("Object with id " + id + " is not of class " + clazz);
            }
        }
        
        

	@Override
	public Map<WorldObjectId, IWorldObject> get() {
		return knownObjects;
	}

	//
	//
	// PROTECTED (HELPED) METHODS
	//
	//
	
	/**
	 * Method that adds a new world object to the object maps. It will be called from
	 * the descendant whenever new object is encountered for the first time.
	 * <p><p>
	 * Synchronized!
	 * 
	 * @param worldObject
	 */
	protected synchronized void addWorldObject(IWorldObject worldObject) {
		knownObjects.put(worldObject.getId(), worldObject);
		for (Class cls : ClassUtils.getSubclasses(worldObject.getClass())) {
			syncWorldObjects.get(cls).put(worldObject.getId(), worldObject);
		}		
	}
	
	/**
	 * Removes world object from the world view - this will be called from the descendants
	 * of the AbstractWorldView whenever world object should disappear from the world view 
	 * (was destroyed in the world).
	 * <p><p>
	 * Synchronized!
	 * 
	 * @param worldObject
	 */
	protected synchronized void removeWorldObject(IWorldObject worldObject) {
		knownObjects.remove(worldObject.getId());
		for (Class<?> cls : ClassUtils.getSubclasses(worldObject.getClass())) {
			syncWorldObjects.get(cls).remove(worldObject.getId());
		}			
	}
	
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
	public String toString() {
		if (this == null) return "AbstractWorldView-instantiating";
		return getClass().getSimpleName();
	}
	
}
