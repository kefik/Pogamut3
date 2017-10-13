package cz.cuni.amis.pogamut.base.component.lifecyclebus;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.IComponentAware;
import cz.cuni.amis.pogamut.base.component.bus.ComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.IComponentEvent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentEventListener;
import cz.cuni.amis.pogamut.base.component.bus.event.IFatalErrorEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.IPausedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.IPausingEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.IResetEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.IResumedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.IResumingEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.IStartedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.IStartingEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.IStartingPausedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.IStoppedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.IStoppingEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.ComponentBusErrorEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.FatalErrorEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.FatalErrorPropagatingEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.ResetEvent;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentBusErrorException;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentBusNotRunningException;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentIdClashException;
import cz.cuni.amis.pogamut.base.component.bus.exception.FatalErrorPropagatingEventException;
import cz.cuni.amis.pogamut.base.component.bus.exception.MoreComponentsForClassException;
import cz.cuni.amis.pogamut.base.component.bus.exception.ResetFailedException;
import cz.cuni.amis.pogamut.base.component.controller.ComponentController;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.controller.ComponentState;
import cz.cuni.amis.pogamut.base.component.controller.IComponentControlHelper;
import cz.cuni.amis.pogamut.base.component.controller.IComponentController;
import cz.cuni.amis.pogamut.base.component.exception.ComponentLifecycleManagementAlreadyRegisteredException;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.utils.ClassUtils;
import cz.cuni.amis.utils.Const;
import cz.cuni.amis.utils.ExceptionToString;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.flag.ImmutableFlag;
import cz.cuni.amis.utils.flag.WaitForFlagChange.IAccept;
import cz.cuni.amis.utils.maps.LazyMap;
import cz.cuni.amis.utils.sets.ConcurrentHashSet;
import cz.cuni.amis.utils.sets.ConcurrentLinkedHashSet;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;

/**
 * Lifecycle bus is implementing {@link ILifecycleBus} which is extending {@link IComponentBus} by implementing the knowledge of lifecycle states of various {@link IComponent}s. It watches
 * over the {@link ComponentState}s, providing information about them.
 * <p><p>
 * Too complex - not suitable for inheritance ... copy paste source code and change it manually ... as it was done for {@link ComponentBus} before.
 * <p><p>
 * For more documentation see {@link IComponentBus}.
 * <p><p>
 * For more documentation regarding lifecycle management, see {@link ComponentController} as {@link LifecycleBus} is working the same way.
 * 
 * @author Jimmy
 */
@AgentScoped
public class LifecycleBus implements ILifecycleBus, IComponentAware {
	
	/////
	//////////
	//
	// EVENT MANAGEMENT - IMPLEMENTATION OF IComponentBus INTERFACE
	//
	//////////
	/////

	
	public static final IToken COMPONENT_ID = Tokens.get("LifecycleBus");
	
	private Map<IToken, IComponent> componentsByToken = new ConcurrentHashMap<IToken, IComponent>();

	private Map<Class, Set<IComponent>> componentsByClass = new LazyMap<Class, Set<IComponent>>(new ConcurrentHashMap<Class, Set<IComponent>>()) {

		@Override
		protected Set<IComponent> create(Class key) {
			return new ConcurrentHashSet<IComponent>();
		}
		
	};

	private Map<Class, Set<IComponentEventListener>> eventListeners = new LazyMap<Class, Set<IComponentEventListener>>(new ConcurrentHashMap<Class, Set<IComponentEventListener>>()) {

		@Override
		protected Set<IComponentEventListener> create(Class key) {
			return new ConcurrentLinkedHashSet<IComponentEventListener>();
		}
				
	};
	
	private Map<Class, Map<Class, Set<IComponentEventListener>>> componentEventListeners = new LazyMap<Class, Map<Class, Set<IComponentEventListener>>>(new ConcurrentHashMap<Class, Map<Class, Set<IComponentEventListener>>>()) {

		@Override
		protected Map<Class, Set<IComponentEventListener>> create(Class key) {
			return new LazyMap<Class, Set<IComponentEventListener>>(new ConcurrentHashMap<Class, Set<IComponentEventListener>>()) {

				@Override
				protected Set<IComponentEventListener> create(Class key) {
					return new ConcurrentLinkedHashSet<IComponentEventListener>();
				}
				
			};
		}
		
	};
	
	private Map<IToken, Map<Class, Set<IComponentEventListener>>> componentNameEventListeners = new LazyMap<IToken, Map<Class, Set<IComponentEventListener>>>() {

		@Override
		protected Map<Class, Set<IComponentEventListener>> create(IToken key) {
			return new LazyMap<Class, Set<IComponentEventListener>>(new ConcurrentHashMap<Class, Set<IComponentEventListener>>()) {

				@Override
				protected Set<IComponentEventListener> create(Class key) {
					return new ConcurrentLinkedHashSet<IComponentEventListener>();
				}
				
			};
		}
		
	};
	
	/**
	 * Whether the bus is running. Dropped after IFatalError event.
	 */
	private boolean running = true;
		
	/**
	 * List of events we have to process.
	 * <p><p>
	 * It is managed only by raiseEvent() method - DO NOT MODIFY OUTSIDE IT!
	 */
	private Queue<IComponentEvent> queue = new ConcurrentLinkedQueue();

	/**
	 * Flag that is telling us whether there is an event being processed or not.
	 * <p><p>
	 * It is managed only by raiseEvent() method - DO NOT MODIFY IT FROM OUTSIDE THAT METHOD!
	 */
	private boolean queueProcessing = false;
	
	/**
	 * Used by processQueue() for the sync.
	 */
	private Object queueProcessingMutex = new Object();
	
	private LogCategory log;

	private IAgentId agentId;
	
	@Inject
	public LifecycleBus(IAgentLogger logger) {
		NullCheck.check(logger, "logger");
		this.agentId = logger.getAgentId();
		this.log = logger.getCategory(this);
		NullCheck.check(this.log, "log category returned by the logger");
		registerComponentStateListeners();
	}
	
	@Override
	public String toString() {
		return "LifecycleBus[" + agentId.getToken() + ", running=" + running + ", queue length=" + (this.queue == null ? "null" : this.queue.size()) + "]";
	}
	
	@Override
	public IComponentBus getEventBus() {
		return this;
	}
	
	@Override
	public IToken getComponentId() {
		return COMPONENT_ID;
	}
	
	public Logger getLog() {
		return log;
	}
	
	@Override
	public boolean isRunning() {
		return running;
	}
	
	@Override
	public synchronized void reset() throws ResetFailedException {
		if (log.isLoggable(Level.WARNING)) log.warning("reset() called.");
		try {
			if (running) {
				if (log.isLoggable(Level.WARNING)) log.warning(LifecycleBus.COMPONENT_ID.getToken() + " is still running, broadcasting fatal error to stop all components.");
				event(new FatalErrorEvent<IComponent>(this, "Resetting."));
			}
			if (log.isLoggable(Level.WARNING)) log.warning("Broadcasting reset event.");
			resetBus();
			innerRaiseEvent(new ResetEvent(this));
		} catch (Exception e) {
			if (e instanceof ComponentBusErrorException) {
				innerRaiseEvent(new FatalErrorEvent(this, "Reset failed.", e.getCause()));
				throw new ResetFailedException(e.getCause(), log, this);
			} else {
				innerRaiseEvent(new FatalErrorEvent(this, "Reset failed.", e));
				throw new ResetFailedException(e, log, this);
			}
		}
		if (log.isLoggable(Level.WARNING)) log.warning("Reseted, bus is running again.");
	} 
	
	private void resetBus() {
		running = true;
		queue.clear();
		queueProcessing = false;
	}
	
	//
	//
	// COMPONENTS
	//
	//
	
	@Override
	public <T> T getComponent(Class<T> cls) throws MoreComponentsForClassException {
		Set<T> components = (Set<T>) componentsByClass.get(cls);
		if (components.size() > 0) throw new MoreComponentsForClassException(cls, components, this);
		return components.iterator().next();
	}

	@Override
	public <T> Set<T> getComponents(Class<T> cls) {
		return (Set<T>) Collections.unmodifiableSet(componentsByClass.get(cls));
	}
	
	@Override
	public void register(IComponent component) throws ComponentIdClashException {
		synchronized(componentsByToken) {
			NullCheck.check(component.getComponentId(), "component's id is null ("+ component + ")");
			if (componentsByToken.get(component.getComponentId()) != null) {
				if (componentsByToken.get(component.getComponentId()) == component) {
					return;
				} else {
					ComponentIdClashException e = new ComponentIdClashException(component.getComponentId(), log, this);
					try {
						event(new FatalErrorEvent(this, e));
					} catch (Exception e1) {
					}
					throw e;
				}
			}			
			registerComponent(component);
		}
	}
	
	@Override
	public void remove(IComponent component) {
		synchronized(componentsByToken) {
			componentsByToken.remove(component.getComponentId());
			Collection<Class> componentClasses = ClassUtils.getSubclasses(component.getClass());
			for (Class cls : componentClasses) {
				componentsByClass.get(cls).remove(component);
			}
			if (log.isLoggable(Level.INFO)) log.info(component + " of the id " + component.getComponentId().getToken() + " removed from the bus.");
		}
	}
	
	/**
	 * Called whenever new component is being registered.
	 * <p><p>
	 * Method assumes it is "synchronized" by caller.
	 * <p><p>
	 * This method also assumes there is no component-id clash with existing components! Checks
	 * has been done already inside register() method.
	 * 
	 * @param component
	 */
	private void registerComponent(IComponent component) {
		componentsByToken.put(component.getComponentId(), component);
		Collection<Class> componentClasses = ClassUtils.getSubclasses(component.getClass());
		for (Class cls : componentClasses) {
			componentsByClass.get(cls).add(component);
		}
		setComponentState(component.getComponentId(), ComponentState.INSTANTIATED);
		if (log.isLoggable(Level.INFO)) log.info(component + " registered under id " + component.getComponentId().getToken());
	}
	
	@Override
	public IComponent getComponent(IToken name) {
		return componentsByToken.get(name);
	}
	
	//
	//
	// EVENTS LISTENER
	// 
	//
	
	@Override
	public void addEventListener(Class<?> event, IComponentEventListener<?> listener) {
		NullCheck.check(event, "event");
		NullCheck.check(listener, "listener");
		eventListeners.get(event).add(listener);
	}
	
	@Override
	public void addEventListener(Class<?> event, Class<?> component, IComponentEventListener<?> listener) {
		NullCheck.check(event, "event");
		NullCheck.check(component, "comopnent");
		NullCheck.check(listener, "listener");
		componentEventListeners.get(component).get(event).add(listener);
	}

	@Override
	public void addEventListener(Class<?> event, IToken componentName, IComponentEventListener<?> listener) {
		NullCheck.check(event, "event");
		NullCheck.check(componentName, "componentName");
		NullCheck.check(listener, "listener");
		componentNameEventListeners.get(componentName).get(event).add(listener);
	}
	
	@Override
	public void addEventListener(Class<?> event, IComponent component, IComponentEventListener<?> listener) {
		NullCheck.check(component, "component");
		addEventListener(event, component.getComponentId(), listener);
	}
	
	@Override
	public boolean isListening(Class<?> event, IComponentEventListener<?> listener) {
		NullCheck.check(event, "event");
		NullCheck.check(listener, "listener");
		if (!eventListeners.containsKey(event)) return false;
		return eventListeners.get(event).contains(listener);
	}
	
	@Override
	public boolean isListening(Class<?> event, Class<?> component, IComponentEventListener<?> listener) {
		NullCheck.check(event, "event");
		NullCheck.check(component, "component");
		NullCheck.check(listener, "listener");
		if (!componentEventListeners.containsKey(component)) return false;
		Map<Class, Set<IComponentEventListener>> listeners = componentEventListeners.get(component);
		if (!listeners.containsKey(event)) return false;
		return listeners.get(event).contains(listener);
	}

	@Override
	public boolean isListening(Class<?> event, IToken componentId, IComponentEventListener<?> listener) {
		NullCheck.check(event, "event");
		NullCheck.check(componentId, "componentId");
		NullCheck.check(listener, "listener");
		if (!componentNameEventListeners.containsKey(componentId)) return false;
		Map<Class, Set<IComponentEventListener>> listeners = componentNameEventListeners.get(componentId);
		if (!listeners.containsKey(event)) return false;
		return listeners.get(event).contains(listener);
	}
	
	@Override
	public boolean isListening(Class<?> event, IComponent component, IComponentEventListener<?> listener) {
		NullCheck.check(component, "component");
		return isListening(event, component.getComponentId(), listener);
	}

	@Override
	public void removeEventListener(Class<?> event, IComponentEventListener<?> listener) {
		NullCheck.check(event, "event");
		NullCheck.check(listener, "listener");
		if (!eventListeners.containsKey(event)) return;
		eventListeners.get(event).remove(listener);
	}

	@Override
	public void removeEventListener(Class<?> event, Class<?> component,	IComponentEventListener<?> listener) {
		NullCheck.check(event, "event");
		NullCheck.check(component, "component");
		NullCheck.check(listener, "listener");
		if (!componentEventListeners.containsKey(component)) return;
		Map<Class, Set<IComponentEventListener>> listeners = componentEventListeners.get(component);
		if (!listeners.containsKey(event)) return;
		listeners.get(event).remove(listener);
	}

	@Override
	public void removeEventListener(Class<?> event, IToken componentId, IComponentEventListener<?> listener) {
		NullCheck.check(event, "event");
		NullCheck.check(componentId, "componentId");
		NullCheck.check(listener, "listener");
		if (!componentNameEventListeners.containsKey(componentId)) return;
		Map<Class, Set<IComponentEventListener>> listeners = componentNameEventListeners.get(componentId);
		if (!listeners.containsKey(event)) return;
		listeners.get(event).remove(listener);
	}
	
	@Override
	public void removeEventListener(Class<?> event, IComponent component, IComponentEventListener<?> listener) {
		NullCheck.check(component, "component");
		removeEventListener(event, component.getComponentId(), listener);
	}
	
	//
	//
	// EVENT PROCESSING
	//
	//
		
	/**
	 * Helper method used ONLY FROM innerRaiseEvent. DO NOT USE OUTSIDE THAT METHOD!
	 * <p><p>
	 * Notifies only if isRunning().
	 * 
	 * @param event
	 */
	private void notifyListenersA(IComponentEvent event) {
		Collection<Class> eventClasses = ClassUtils.getSubclasses(event.getClass());
		for (Class eventClass : eventClasses) {
			if (!eventListeners.containsKey(eventClass)) continue;
			for (IComponentEventListener listener : eventListeners.get(eventClass)) {
				if (!isRunning()) return;
				listener.notify(event);
			}
		}
	}

	/**
	 * Helper method used ONLY FROM innerRaiseEvent. DO NOT USE OUTSIDE THAT METHOD!
	 * <p><p>
	 * Notifies only if isRunning().
	 * 
	 * @param event
	 */
	private void notifyListenersB(IComponentEvent event) {
		Collection<Class> componentClasses = ClassUtils.getSubclasses(event.getSource().getClass());
		Collection<Class> eventClasses = ClassUtils.getSubclasses(event.getClass());
		for (Class componentClass : componentClasses) {
			if (!componentEventListeners.containsKey(componentClass)) continue;
			Map<Class, Set<IComponentEventListener>> listeners = componentEventListeners.get(componentClass);
			for (Class eventClass : eventClasses) {
				if (!listeners.containsKey(eventClass)) continue;
				for (IComponentEventListener listener : listeners.get(eventClass)) {
					if (!isRunning()) return;
					listener.notify(event);
				}
			}
		}
	}
	
	/**
	 * Helper method used ONLY FROM innerRaiseEvent. DO NOT USE OUTSIDE THAT METHOD!
	 * <p><p>
	 * Notifies only if isRunning().
	 * 
	 * @param event
	 */
	private void notifyListenersC(IComponentEvent event) {
		if (!componentNameEventListeners.containsKey(event.getSource().getComponentId())) return;
		Map<Class, Set<IComponentEventListener>> listeners = componentNameEventListeners.get(event.getSource().getComponentId());
		Collection<Class> eventClasses = ClassUtils.getSubclasses(event.getClass());
		for (Class eventClass : eventClasses) {
			if (!listeners.containsKey(eventClass)) continue;
			for (IComponentEventListener listener : listeners.get(eventClass)) {
				if (!isRunning()) return;
				listener.notify(event);
			}
		}
	}

	/**
	 * Helper method used ONLY FROM innerRaiseEvent. DO NOT USE OUTSIDE THAT METHOD!
	 * <p><p>
	 * Notification is done safe-way ... every listener is notified even if an exception happens in one of them.
	 * <p><p>
	 * Notifies also if is not running (used for propagation of {@link IFatalErrorEvent}.
	 * 
	 * @param event
	 */
	private void notifyListenersA_Safe(IComponentEvent event) {
		Collection<Class> eventClasses = ClassUtils.getSubclasses(event.getClass());
		for (Class eventClass : eventClasses) {
			if (!eventListeners.containsKey(eventClass)) continue;
			for (IComponentEventListener listener : eventListeners.get(eventClass)) {
				try {
					listener.notify(event);
				} catch (Exception e) {
					if (log.isLoggable(Level.WARNING)) log.warning(ExceptionToString.process("Exception happened during notification of event " + event + " on listener " + listener + ".", e));
				}
			}
		}
	}

	/**
	 * Helper method used ONLY FROM innerRaiseEvent. DO NOT USE OUTSIDE THAT METHOD!
	 * <p><p>
	 * Notification is done safe-way ... every listener is notified even if an exception happens in one of them.
	 * <p><p>
	 * Notifies also if is not running (used for propagation of {@link IFatalErrorEvent}.
	 * 
	 * @param event
	 */
	private void notifyListenersB_Safe(IComponentEvent event) {
		Collection<Class> componentClasses = ClassUtils.getSubclasses(event.getSource().getClass());
		Collection<Class> eventClasses = ClassUtils.getSubclasses(event.getClass());
		for (Class componentClass : componentClasses) {
			if (!componentEventListeners.containsKey(componentClass)) continue;
			Map<Class, Set<IComponentEventListener>> listeners = componentEventListeners.get(componentClass);
			for (Class eventClass : eventClasses) {
				if (!listeners.containsKey(eventClass)) continue;
				for (IComponentEventListener listener : listeners.get(eventClass)) {
					try {
						listener.notify(event);
					} catch (Exception e) {
						if (log.isLoggable(Level.WARNING)) log.warning(ExceptionToString.process("Exception happened during notification of event " + event + " on listener " + listener + ".", e));
					}
				}
			}
		}
	}
	
	/**
	 * Helper method used ONLY FROM innerRaiseEvent. DO NOT USE OUTSIDE THAT METHOD!
	 * <p><p>
	 * Notification is done safe-way ... every listener is notified even if an exception happens in one of them.
	 * <p><p>
	 * Notifies also if is not running (used for propagation of {@link IFatalErrorEvent}.
	 * 
	 * @param event
	 */
	private void notifyListenersC_Safe(IComponentEvent event) {
		if (!componentNameEventListeners.containsKey(event.getSource().getComponentId())) return;
		Map<Class, Set<IComponentEventListener>> listeners = componentNameEventListeners.get(event.getSource().getComponentId());
		Collection<Class> eventClasses = ClassUtils.getSubclasses(event.getClass());
		for (Class eventClass : eventClasses) {
			if (!listeners.containsKey(eventClass)) continue;
			for (IComponentEventListener listener : listeners.get(eventClass)) {
				try {
					listener.notify(event);
				} catch (Exception e) {
					if (log.isLoggable(Level.WARNING)) log.warning(ExceptionToString.process("Exception happened during notification of event " + event + " on listener " + listener + ".", e));
				}
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
	private void innerRaiseEvent(IComponentEvent event) {		
		if (event instanceof IFatalErrorEvent) {
			if (log.isLoggable(Level.SEVERE)) log.severe("Fatal error happenned - component bus is stopping." + Const.NEW_LINE + ((IFatalErrorEvent)event).getSummary());
			queue.clear();
			running = false;
			notifyListenersA_Safe(event);
			notifyListenersB_Safe(event);
			notifyListenersC_Safe(event);
			return;
		} else {
			if (log.isLoggable(Level.FINE)) log.fine("Notifying " + event);
		}
		if (!isRunning()) return;
		notifyListenersA(event);
		if (!isRunning()) return;
		notifyListenersB(event);
		if (!isRunning()) return;
		notifyListenersC(event);
	}
	
	/**
	 * Process new IWorldEvent - DO NOT CALL SEPARATELY - must be called only from raiseEvent(),
	 * that forbids recursion of its calls.
	 * <p><p>
	 * Contains the sequence in which the listeners are informed about the event.
	 * <p><p>
	 * Notification is done safe-way ... every listener is notified even if an exception happens in one of them.
	 * 
	 * @param event
	 */
	private void innerRaiseEvent_Safe(IComponentEvent event) {
		if (event instanceof IFatalErrorEvent) {
			if (log.isLoggable(Level.SEVERE)) log.severe("Fatal error happenned - component bus is stopping." + Const.NEW_LINE + ((IFatalErrorEvent)event).getSummary());
			queue.clear();
			running = false;
		} else {
			if (log.isLoggable(Level.FINE)) log.fine("Notifying (safe) " + event);
		}
		notifyListenersA_Safe(event);
		notifyListenersB_Safe(event);
		notifyListenersC_Safe(event);
	}
	
	@Override
	public synchronized boolean event(IComponentEvent event) throws ComponentBusNotRunningException, ComponentBusErrorException, FatalErrorPropagatingEventException {
		// method is synchronized - only one thread inside at given time
		
		NullCheck.check(event, "event");
		if (event instanceof IResetEvent) throw new IllegalArgumentException("you can't broadcast reset event this way, use reset() instead");
			
		if (!isRunning()) {			
			if (event instanceof IFatalErrorEvent) {
				try {
					if (log.isLoggable(Level.WARNING)) log.warning("Component bus is not running, ignoring fatal error event from " + event.getSource() + ".");
				} catch (Exception e) {
				}
				return false;
			}
			throw new ComponentBusNotRunningException(event, log, this);
		}
		
		// is it fatal error event?
		if (event instanceof IFatalErrorEvent) {
			if (!isRunning()) {
				try {
					if (log.isLoggable(Level.WARNING)) log.warning("Component bus is not running, ignoring fatal error event from " + event.getSource() + ".");
				} catch (Exception e) {
				}
				return false;
			}
			// yes -> process it immediately
			innerRaiseEvent(event);
			return false;
		}
		
		// is this method recursively called? 
		if (queueProcessing) {
			// yes it is -> that means the previous event has not been
			// processed! ... store this event and allows the previous one
			// to be fully processed (e.g. postpone raising this event)
			queue.add(event);
			return false;
		}
		
		// check the queue consistency
		if (queue.size() > 0) {
			ComponentBusErrorException e = new ComponentBusErrorException("Previous events has not been fully processed! ComponenBus fatal error.", event, this);
			innerRaiseEvent_Safe(
				new ComponentBusErrorEvent(this, e)
			);
			throw e;
		}
		
		// add the event to the queue
		queue.add(event);
		// start processing the event
		processQueue();		
		
		// event has been processed
		return true;
	}
	
	@Override
	public synchronized void eventTransactional(IComponentEvent event) throws ComponentBusNotRunningException, ComponentBusErrorException, FatalErrorPropagatingEventException {
		// method is synchronized - only one thread inside at given time
		
		NullCheck.check(event, "event");
		if (event instanceof IResetEvent) throw new IllegalArgumentException("you can't broadcast reset event this way, use reset() instead");
		
		if (!isRunning()) {
			if (event instanceof IFatalErrorEvent) {
				try {
					if (log.isLoggable(Level.WARNING)) log.warning("Component bus is not running, ignoring fatal error event from " + event.getSource() + ".");
				} catch (Exception e) {
				}
				return;
			}
			throw new ComponentBusNotRunningException(event, log, this);
		}
		
		if (!queueProcessing) {
			// the method is not being called in the context of another event, redirect
			event(event);
			return;
		}
		 
		// process event - we're in transactional mode == immediately propagate the event
		innerRaiseEvent(event);
	}
	
	/**
	 * Must be synchronized beforehand!
	 */
	private void processQueue() throws FatalErrorPropagatingEventException, ComponentBusErrorException {
		// save the 'queueProcessing' state (whether we should drop the queue processing flag or not at the end of this method)
		boolean dropQueueProcessing = !queueProcessing;
		queueProcessing = true;
		IComponentEvent event = null;
		while(queue.size() != 0) {
			// yes we do -> do it!
			try {
			event = queue.poll();
			} catch (Exception e) {
				ComponentBusErrorException e1 = new ComponentBusErrorException("Can't poll next event.", e, this);
				innerRaiseEvent_Safe(
					new ComponentBusErrorEvent(this, e)
				);
				throw e1;
			}
			try {
				innerRaiseEvent(event);
			} catch (FatalErrorPropagatingEventException e1) {
				throw e1;
			} catch (ComponentBusErrorException e2) {
				throw e2;
			} catch (Exception e3) {
				innerRaiseEvent_Safe(
					new FatalErrorPropagatingEvent<LifecycleBus>(this, "Exception happened during the event propagation.", e3, event)
				);
				queueProcessing = false;
				throw new FatalErrorPropagatingEventException(event, e3, this);
			}
		}
		if (!isRunning()) {
			// fatal error has happened
			if (log.isLoggable(Level.SEVERE)) log.severe("Stopped.");
			if (event != null && (!(event instanceof IFatalErrorEvent))) {
				// last event we have been processing was not "fatal error"
				throw new FatalErrorPropagatingEventException(event, this);
			}
		}
		// if we should drop the queue processing...
		if (dropQueueProcessing) {
			// so be it...
			queueProcessing = false;
		}
	}
	
	/////
	//////////
	//
	// LIFECYCLE MANAGEMENT - IMPLEMENTATION OF ILifecycleBus INTERFACE
	//
	//////////
	/////
	
	/**
	 * Mutex used for synchronization.
	 */
	private Object ctrlMutex = new Object();
	
	/**
	 * Provided dependencies of the controlled component.
	 */
	private ComponentDependencies dependencies;
	
	/**
	 * Map tracking the states of dependencies.
	 */
	private Map<IToken, ComponentState> dependencyState = new HashMap<IToken, ComponentState>();
	
	/**
	 * Map tracking count of states of dependencies.
	 */
	private Map<ComponentState, Integer> stateCount = new HashMap<ComponentState, Integer>();
	
	/**
	 * State of the controlled component.
	 * <p><p>
	 * Use {@link ComponentController#setState(ComponentState)} to alter the value of the flag.
	 */
	private Flag<ComponentState> componentState = new Flag<ComponentState>(ComponentState.INSTANTIATED);
	
	/**
	 * Stores the fatal error that triggered the system failure.
	 */
	private IFatalErrorEvent lastFatalError = null;
	
	//
	// COMPONENT STATE AWAIT CLASS
	//
	
	private static class AwaitState implements IAccept<ComponentState> {
		
		Set<ComponentState> awaiting = new HashSet<ComponentState>();
		
		public AwaitState(ComponentState... states) {
			for (ComponentState state : states) {
				awaiting.add(state);
			}
		}

		@Override
		public boolean accept(ComponentState flagValue) {
			return awaiting.contains(flagValue);
		}
		
	};
		
	//
	// EVENT LISTENERS
	//
	
	private IComponentEventListener<IStartingEvent> startingListener = new IComponentEventListener<IStartingEvent>() {

		@Override
		public void notify(IStartingEvent event) {
			setComponentState(event.getSource().getComponentId(), ComponentState.STARTING);
		}
		
	};
	
	private IComponentEventListener<IStartingPausedEvent> startingPausedListener = new IComponentEventListener<IStartingPausedEvent>() {

		@Override
		public void notify(IStartingPausedEvent event) {
			setComponentState(event.getSource().getComponentId(), ComponentState.STARTING_PAUSED);
		}
		
	};
	
	private IComponentEventListener<IStartedEvent> startedListener = new IComponentEventListener<IStartedEvent>() {

		@Override
		public void notify(IStartedEvent event) {
			setComponentState(event.getSource().getComponentId(), ComponentState.RUNNING);
		}
		
	};
	
	private IComponentEventListener<IPausingEvent> pausingListener = new IComponentEventListener<IPausingEvent>() {

		@Override
		public void notify(IPausingEvent event) {
			setComponentState(event.getSource().getComponentId(), ComponentState.PAUSING);
		}
		
	};
	
	private IComponentEventListener<IPausedEvent> pausedListener = new IComponentEventListener<IPausedEvent>() {

		@Override
		public void notify(IPausedEvent event) {
			setComponentState(event.getSource().getComponentId(), ComponentState.PAUSED);
		}
		
	};
	
	private IComponentEventListener<IResumingEvent> resumingListener = new IComponentEventListener<IResumingEvent>() {

		@Override
		public void notify(IResumingEvent event) {
			setComponentState(event.getSource().getComponentId(), ComponentState.RESUMING);
		}
		
	};
	
	private IComponentEventListener<IResumedEvent> resumedListener = new IComponentEventListener<IResumedEvent>() {

		@Override
		public void notify(IResumedEvent event) {
			setComponentState(event.getSource().getComponentId(), ComponentState.RUNNING);
		}
		
	};
	
	private IComponentEventListener<IStoppingEvent> stoppingListener = new IComponentEventListener<IStoppingEvent>() {

		@Override
		public void notify(IStoppingEvent event) {
			setComponentState(event.getSource().getComponentId(), ComponentState.STOPPING);
		}
		
	};
	
	private IComponentEventListener<IStoppedEvent> stoppedListener = new IComponentEventListener<IStoppedEvent>() {

		@Override
		public void notify(IStoppedEvent event) {
			setComponentState(event.getSource().getComponentId(), ComponentState.STOPPED);
		}
		
	};
	
	private IComponentEventListener<IFatalErrorEvent> fatalErrorListener = new IComponentEventListener<IFatalErrorEvent>() {

		@Override
		public void notify(IFatalErrorEvent event) {
			setComponentStates(ComponentState.KILLED);
		}
		
	};
	
	private IComponentEventListener<IResetEvent> resetEventListener = new IComponentEventListener<IResetEvent>() {

		@Override
		public void notify(IResetEvent event) {
			setComponentStates(ComponentState.RESETED);
		}
		
	};
	
	/**
	 * Contains {@link ComponentControl} with lifecycle methods of various components identified by their ID.
	 */
	private Map<IToken, ComponentController> controls = new HashMap<IToken, ComponentController>();
	
	/**
	 * Map holdin {@link Flag}s with the current state of the component.
	 */
	private Map<IToken, Flag<ComponentState>> componentStates = new LazyMap<IToken, Flag<ComponentState>>() {

		@Override
		protected Flag<ComponentState> create(IToken key) {
			return new Flag<ComponentState>(ComponentState.INSTANTIATED);
		}
		
	};

	private void registerComponentStateListeners() {
		this.addEventListener(IStartingEvent.class,       startingListener);
		this.addEventListener(IStartingPausedEvent.class, startingPausedListener);
		this.addEventListener(IStartedEvent.class,        startedListener);
		this.addEventListener(IStoppingEvent.class,       stoppingListener);
		this.addEventListener(IStoppedEvent.class,        stoppedListener);
		this.addEventListener(IPausingEvent.class,        pausingListener);
		this.addEventListener(IPausedEvent.class,         pausedListener);
		this.addEventListener(IResumingEvent.class,       resumingListener);
		this.addEventListener(IResumedEvent.class,        resumedListener);
	}
	
	/**
	 * Sets new component state for component of 'componentId'.
	 * @param componentId
	 * @param newState
	 */
	private void setComponentState(IToken componentId, ComponentState newState) {
		componentStates.get(componentId).setFlag(newState);
	}
	
	/**
	 * Sets all component states to 'newState'.
	 * @param newState
	 */
	private void setComponentStates(ComponentState newState) {
		for(Flag<ComponentState> state : componentStates.values()) {
			state.setFlag(newState);
		}
	}
	
	@Override
	public <T extends IComponent> IComponentController<T> addLifecycleManagement(T component, IComponentControlHelper lifecyleMethods, ComponentDependencies componentDependencies) throws ComponentLifecycleManagementAlreadyRegisteredException {
		synchronized(controls) {
			if (controls.containsKey(component.getComponentId())) throw new ComponentLifecycleManagementAlreadyRegisteredException("Lifecycle already registered at " + this + " for component " + component.getComponentId() + ".", this);
			ComponentController controller = new ComponentController(component, lifecyleMethods, this, getLog(), componentDependencies);
			controls.put(component.getComponentId(), controller);
			return controller;
		}
	}

	@Override
	public ImmutableFlag<ComponentState> getComponentState(IToken componentId) {
		return componentStates.get(componentId).getImmutable();
	}

	@Override
	public ImmutableFlag<ComponentState> getComponentState(Class<? extends IComponent> cls) throws MoreComponentsForClassException {
		IComponent component = getComponent(cls);
		if (component == null) return null;
		return getComponentState(component.getComponentId());
	}

	@Override
	public void removeLifecycleManagement(IComponent component) {
		synchronized(controls) {
			controls.remove(component.getComponentId());
		}
	}

}

