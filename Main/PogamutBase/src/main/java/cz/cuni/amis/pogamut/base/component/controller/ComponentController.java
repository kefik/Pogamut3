package cz.cuni.amis.pogamut.base.component.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.communication.mediator.IMediator;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.IComponentEvent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentEventListener;
import cz.cuni.amis.pogamut.base.component.bus.event.ComponentBusEvents;
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
import cz.cuni.amis.pogamut.base.component.bus.event.impl.FatalErrorEvent;
import cz.cuni.amis.pogamut.base.component.exception.ComponentCantPauseException;
import cz.cuni.amis.pogamut.base.component.exception.ComponentCantResumeException;
import cz.cuni.amis.pogamut.base.component.exception.ComponentCantStartException;
import cz.cuni.amis.pogamut.base.component.exception.ComponentCantStopException;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.ILifecycleBus;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;

/**
 * Provides simple way for components to start/stop automatically based on the status of objects
 * they depends on (e.g. {@link IWorldView} may start only if underlying {@link IMediator} has been started).
 * <p><p>
 * Dependents may be identified by {@link IToken} or {@link IComponent}.
 * <p><p>
 * Component controller ease the burden with starting/stopping the component in the right time and the broadcasting
 * of appropriate events and tracking its {@link ComponentState}.
 * <p><p>
 * It allows you to specify starting-dependencies of your {@link IComponentControlHelper} allowing you to define the moment
 * when the component should start/stop. Moreover - it automatically watching the {@link IComponentBus} for
 * {@link IFatalErrorEvent} killing your component when a fatal event is caught (but it will call kill only iff fatal error is 
 * produced by different component then the controlled one).
 * <p><p>
 * Additionally the controller is broadcasting starting/stopping events automatically.
 * <p><p>
 * If you wish to manually stop the component for whatever reason - call manualStop() method, it will
 * broadcast {@link IStoppingEvent} and {@link IStopeedEvent} automatically.
 * <p><p>
 * <p>
 * The controlled component goes through various states during its life-cycle.
 * <p><p>
 * Initial state of the component {@link ComponentState.RESETED}.
 * <p><p>
 * Usual life-cycle of the component is:<p>
 * INSTANTIATED (or RESETED or STOPPED) -> STARTING -> RUNNING -> [PAUSING -> PAUSED -> RESUMING -> RUNNING] -> STOPPING -> STOPPED<p>
 * Note that the component might be started again from the STOPPED state or may be stopped from PAUSED state.
 * <p><p>
 * Also the component might be started to paused state, the lifecycle is then is:<p>
 * INSTANTIATED (or RESETED or STOPPED) -> STARTING_PAUSED -> PAUSED -> RESUMING -> RUNNING -> ....
 * <p><p>
 * If {@link IFatalErrorEvent} is got or raised by the component through {@link ComponentController#fatalError}, the state is switched 
 * to -> KILLING -> KILLED and may continue only with -> RESETING -> RESETED.
 * <p><p>
 * Various corresponding methods from {@link IComponentControlHelper} are during state transitions.
 * <p><p>
 * INSTANTIATED (or RESETED or STOPPED) -> preStart() -> STARTING -> start() -> RUNNING -> prePause() -> PAUSING -> pause() -> PAUSED -> preResume() -> RESUMING -> resume() -> RUNNING -> preStop() -> STOPPING -> stop()
 * -> STOPPED
 * <p><p>
 * (similarly for start-paused) INSTANTIATED (or RESETED or STOPPED) -> preStartPaused() -> STARTING_PAUSED -> startPaused() -> PAUSED -> ...
 * <p><p>
 * If {@link IFatalErrorEvent} is got (or raised by the controlled component), 
 * the state is switched (and methods are called) ... -> KILLING -> kill() (not called in case of fatalError() method invocation) -> KILLED and
 * then it may continue only with -> RESETING -> reset() -> RESETED.
 * <p><p>
 * Furthermore corresponding {@link IComponentEvent}s are broadcast to the underlying {@link IComponentBus}. Thus the complete
 * life/event/method-cycle looks like this:
 * <p><p>
 * INSTANTIATED (or RESETED) -> STARTING -> {@link IStartingEvent} -> start() -> RUNNING -> {@link IStartedEvent} -> prePause() -> PAUSING -> {@link IPausingEvent}
 * -> pause() -> PAUSED -> {@link IPausedEvent} -> RESUMING -> {@link IResumingEvent} -> resume() -> RUNNING -> {@link IResumedEvent} -> preStop()
 * -> STOPPING -> {@link IStoppingEvent} -> stop() -> STOPPED -> {@link IStoppedEvent}
 * <p><p>
 * Last pieces of information:<p>
 * 1) INSTANTIATED (or RESETED) -> STARTING + STOPPED -> STARTING transition is triggered only if dependencies are starting / has started.
 * 2) RUNNING -> PAUSING transition is triggered whenever some dependency broadcasts {@link IPausingEvent} or {@link IPausedEvent}.<p>
 * 3) PAUSED -> RESUMING transition is triggered when all dependencies are resuming / has resumed.<p>
 * 4) RUNNING | PAUSED -> STOPPING transition is triggered whenever some dependency broadcasts {@links IStoppingEvent} or {@link IStoppedEvent} or
 *    when the stop is manually required by calling {@link ComponentController#manualStop} method.<p>
 * 5) any state -> KILLING transition is triggered whenever some component broadcasts {@link IFatalErrorEvent} or the component reports
 *    that fatal error has happened through one of {@link ComponentController}.fatalError() method.<p>
 * 6) kill() method is called only if OTHER component broadcasts {@link IFatalErrorEvent} - the kill() method is not called
 *    when the fatal error is raised manually by the component via one of fatalError() methods.<p>
 * 7) KILLED -> RESETING transition is triggered by {@link IResetEvent}<p>
 * 8) there is a specific transition (STARTING | RUNNING | PAUSING | PAUSED | RESUMING ) -> STOPPING transition that may be triggered
 *    by {@link IStoppingEvent} or {@link IStoppedEvent} or by manually calling {@link ComponentController#manualStop(String)} method
 *    from within the component.
 * 9) not mentioned transitions (in the whole javadoc) are non-existing (e.g. there is no such transition such as STARTING -> PAUSED,
 *    etc.). Note that some transition can't even happen because {@link IComponenBus} is processing one event at time.<p>
 * <p><p>
 * The comopnent life-cycle looks complex but it is driven by simple idea that we have to control the process of starting/pausing/resuming/stopping of
 * {@link IComponent}s. Hopefully it works like you would expect it to work.   
 * <p><p><p>
 * The controller is also {@link IComponent} but that is just a technical detail - whenever a fatal error happens in the logic
 * of starting/stopping/pausing/resuming/etc. of components the controller raises the fatal error under own id.
 * 
 * @author Jimmy
 */
public class ComponentController<COMPONENT extends IComponent> extends AbstractComponentControllerBase<COMPONENT> implements IComponentController<COMPONENT> {

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
	 * Stores the fatal error that triggered the system failure.
	 */
	private IFatalErrorEvent lastFatalError = null;
	
	//
	// EVENT LISTENERS
	//
	
	private IComponentEventListener<IStartingEvent> startingListener = new IComponentEventListener<IStartingEvent>() {

		@Override
		public void notify(IStartingEvent event) {
			startingEvent(event, false);
		}
		
	};
	
	private IComponentEventListener<IStartingPausedEvent> startingPausedListener = new IComponentEventListener<IStartingPausedEvent>() {

		@Override
		public void notify(IStartingPausedEvent event) {
			startingPausedEvent(event);
		}
		
	};
	
	private IComponentEventListener<IStartedEvent> startedListener = new IComponentEventListener<IStartedEvent>() {

		@Override
		public void notify(IStartedEvent event) {
			startedEvent(event);
		}
		
	};
	
	private IComponentEventListener<IPausingEvent> pausingListener = new IComponentEventListener<IPausingEvent>() {

		@Override
		public void notify(IPausingEvent event) {
			pausingEvent(event, false);
		}
		
	};
	
	private IComponentEventListener<IPausedEvent> pausedListener = new IComponentEventListener<IPausedEvent>() {

		@Override
		public void notify(IPausedEvent event) {
			pausedEvent(event);
		}
		
	};
	
	private IComponentEventListener<IResumingEvent> resumingListener = new IComponentEventListener<IResumingEvent>() {

		@Override
		public void notify(IResumingEvent event) {
			resumingEvent(event, false);
		}
		
	};
	
	private IComponentEventListener<IResumedEvent> resumedListener = new IComponentEventListener<IResumedEvent>() {

		@Override
		public void notify(IResumedEvent event) {
			resumedEvent(event);
		}
		
	};
	
	private IComponentEventListener<IStoppingEvent> stoppingListener = new IComponentEventListener<IStoppingEvent>() {

		@Override
		public void notify(IStoppingEvent event) {
			stoppingEvent(event, false);
		}
		
	};
	
	private IComponentEventListener<IStoppedEvent> stoppedListener = new IComponentEventListener<IStoppedEvent>() {

		@Override
		public void notify(IStoppedEvent event) {
			stoppedEvent(event);
		}
		
	};
	
	private IComponentEventListener<IFatalErrorEvent> fatalErrorListener = new IComponentEventListener<IFatalErrorEvent>() {

		@Override
		public void notify(IFatalErrorEvent event) {
			fatalErrorEvent(event);
		}
		
	};
	
	private IComponentEventListener<IResetEvent> resetEventListener = new IComponentEventListener<IResetEvent>() {

		@Override
		public void notify(IResetEvent event) {
			resetEvent(event);
		}
		
	};
	
	private IComponentBus bus;

	private ComponentBusEvents componentEvents;	
	
	/**
	 * If you're using {@link ILifecycleBus} (not only {@link IComponentBus}, you may create this {@link ComponentController} even after some 'dependencies' has started
	 * as {@link ILifecycleBus} allows us to retrieve current state of dependencies, so we're able to start the component during the construction
	 * if dependencies are already met.
	 * 
	 * @param component controlled component
	 * @param componentControlHelper object controlling the 'component' (contains lifecycle methods which controls the component)
	 * @param bus bus of the component
	 * @param log logger for the class
	 * @param dependencyType type of the dependency (YOU MUST KNOW THE SEMANTICS OF THIS ENUM, see {@link ComponentDependencyType})
	 * @param dependencies {@link IToken} or {@link Class} of components the 'component' depends on
	 */
	public ComponentController(COMPONENT component, IComponentControlHelper componentControlHelper, ILifecycleBus bus, Logger log, ComponentDependencyType dependencyType, Object... dependencies) {
		this(component, componentControlHelper, bus, log, new ComponentDependencies(dependencyType, dependencies));
	}
	
	/**
	 * If you're using {@link ILifecycleBus} (not only {@link IComponentBus}, you may create this {@link ComponentController} even after some 'dependencies' has started
	 * as {@link ILifecycleBus} allows us to retrieve current state of dependencies, so we're able to start the component during the construction
	 * if dependencies are already met.
	 * 
	 * @param component controlled component
	 * @param componentControlHelper object controlling the 'component' (contains lifecycle methods which controls the component)
	 * @param bus bus of the component
	 * @param log logger for the class
	 * @param dependencies dependencies of the component
	 */
	public ComponentController(COMPONENT component, IComponentControlHelper componentControlHelper, ILifecycleBus bus, Logger log, ComponentDependencies dependencies) {
		this(component, componentControlHelper, (IComponentBus)bus, log, dependencies);
	}

	/**
	 * If you use only {@link IComponentBus} (not {@link ILifecycleBus}, you must create this {@link ComponentController} before any of 'dependencies' is started
	 * as there is no way how to retrieve state of component from 'dependencies' so we will assume that all are in state {@link ComponentState#INSTANTIATED}.
	 * 
	 * @param component controlled component
	 * @param componentControlHelper object controlling the 'component' (contains lifecycle methods which controls the component)
	 * @param bus bus of the component
	 * @param log logger to be used by this class
	 * @param dependencyType type of the dependency (YOU MUST KNOW THE SEMANTICS OF THIS ENUM, see {@link ComponentDependencyType})
	 * @param dependencies {@link IToken} or {@link Class} of components the 'component' depends on
	 */
	public ComponentController(COMPONENT component, IComponentControlHelper componentControlHelper, IComponentBus bus, Logger log, ComponentDependencyType dependencyType, Object... dependencies) {
		this(component, componentControlHelper, bus, log, new ComponentDependencies(dependencyType, dependencies));
	}
	
	/**
	 * If you use only {@link IComponentBus} (not {@link ILifecycleBus}, you must create this {@link ComponentController} before any of 'dependencies' is started
	 * as there is no way how to retrieve state of component from 'dependencies' so we will assume that all are in state {@link ComponentState#INSTANTIATED}.
	 *  
	 * @param component controlled component
	 * @param componentControlHelper object controlling the 'component' (contains lifecycle methods which controls the component)
	 * @param bus bus of the component
	 * @param log logger to be used by this class
	 * @param dependencies dependencies of the component
	 */
	public ComponentController(COMPONENT component, IComponentControlHelper componentControlHelper, IComponentBus bus, Logger log, ComponentDependencies dependencies) {
		super(Tokens.get(component.getComponentId().getToken() + "-controller"), component, componentControlHelper, log);
		// FOLLOWING CODE COVERS ALSO ILifecycleBus INITIALIZATION
		
		// save private fields
		this.bus = bus;
		NullCheck.check(this.bus, "bus");
		this.dependencies = dependencies;
		NullCheck.check(this.dependencies, "dependencies");
		
		// register the component
		this.bus.register(component);
		// register the controller
		this.bus.register(this);
		
		// create event broadcasting objects
		this.componentEvents = new ComponentBusEvents(bus, this.component, log);
		
		// initialize state counts
		for (ComponentState state : ComponentState.values()) {
			stateCount.put(state, 0);
		}
		stateCount.put(ComponentState.INSTANTIATED, dependencies.getCount());
		
		// add initial dependency state
		for (IToken dependency : dependencies.getDependencies()) {
			dependencyState.put(dependency, ComponentState.INSTANTIATED);
		}
		
		// hook global listeners
		this.bus.addEventListener(IFatalErrorEvent.class, fatalErrorListener);
		this.bus.addEventListener(IResetEvent.class,      resetEventListener);
		
		synchronized(ctrlMutex) {
			// hook event listeners
			for (IToken dependency : dependencies.getDependencies()) {
				this.bus.addEventListener(IStartingEvent.class,       dependency, startingListener);
				this.bus.addEventListener(IStartingPausedEvent.class, dependency, startingPausedListener);
				this.bus.addEventListener(IStartedEvent.class,        dependency, startedListener);
				this.bus.addEventListener(IStoppingEvent.class,       dependency, stoppingListener);
				this.bus.addEventListener(IStoppedEvent.class,        dependency, stoppedListener);
				this.bus.addEventListener(IPausingEvent.class,        dependency, pausingListener);
				this.bus.addEventListener(IPausedEvent.class,         dependency, pausedListener);
				this.bus.addEventListener(IResumingEvent.class,       dependency, resumingListener);
				this.bus.addEventListener(IResumedEvent.class,        dependency, resumedListener);
			}	
		
			// if the bus is ILifecycleBus ... 
			if (bus instanceof ILifecycleBus) {
				// ... correctly initialize dependency states
				for (IToken dependency : dependencies.getDependencies()) {
					// add initial dependency state
					setDependencyState(dependency, ((ILifecycleBus)bus).getComponentState(dependency).getFlag());
				}
				
				// ... and check whether we should not start the component
				if (inState(ComponentState.INSTANTIATED, ComponentState.RESETED)) {
					// check whether we should not start the component
					if (dependencies.getType() == ComponentDependencyType.STARTS_WITH) {
						// STARTS_WITH
						if (getStateCount(ComponentState.STARTING, ComponentState.RESUMING, ComponentState.RUNNING) == dependencies.getCount()) {
							start(false);
						} else 
						if (getStateCount(ComponentState.STARTING, ComponentState.STARTING_PAUSED, ComponentState.PAUSING, ComponentState.PAUSED, ComponentState.RESUMING, ComponentState.RUNNING) == dependencies.getCount()) {
							startPaused(false);
						}
					} else {
						// STARTS_AFTER
						if (getStateCount(ComponentState.RUNNING) == dependencies.getCount()) {
							start(false);
						} else 
						if (getStateCount(ComponentState.PAUSED, ComponentState.RUNNING) == dependencies.getCount()) {
							startPaused(false);
						}
					}
				}
			}
		}
		
		if (log.isLoggable(Level.INFO)) log.info("In state " + componentState.getFlag() + ".");
	}
	
	//
	//
	// PUBLIC INTERFACE - IComponentControllerBase
	//
	//
	
	@Override
	public void setBroadcastingEvents(boolean broadcastingEvents) {
		super.setBroadcastingEvents(broadcastingEvents);
		componentEvents.setBroadcasting(broadcastingEvents);
	}
	
	@Override
	public IFatalErrorEvent getFatalError() {
		return lastFatalError;
	}
	
	@Override
	public void manualStart(String reason) {
		synchronized(ctrlMutex) {
			if (notInState(ComponentState.KILLED, ComponentState.STOPPED, ComponentState.RESETED, ComponentState.INSTANTIATED)) {
				if (log.isLoggable(Level.WARNING)) log.warning("Start requested, but the component is in state " + componentState.getFlag() + ", unsupported.");
			} else {
				if (log.isLoggable(Level.WARNING)) log.warning("Start requested.");
				if (inState(ComponentState.KILLED)) {
					if (log.isLoggable(Level.WARNING)) log.warning("Component is in state " + ComponentState.KILLED + ", resetting.");
					reset();
				}
				start(true);
			}
		}		
	}
	
	@Override
	public void manualStartPaused(String reason) {
		synchronized(ctrlMutex) {
			if (notInState(ComponentState.KILLED, ComponentState.STOPPED, ComponentState.RESETED, ComponentState.INSTANTIATED)) {
				if (log.isLoggable(Level.WARNING)) log.warning("Start-paused requested, but the component is in state " + componentState.getFlag() + ", unsupported.");
			} else {
				if (log.isLoggable(Level.WARNING)) log.warning("Start-paused requested.");
				if (inState(ComponentState.KILLED)) {
					if (log.isLoggable(Level.WARNING)) log.warning("Component is in state " + ComponentState.KILLED + ", resetting.");
					reset();
				}
				startPaused(true);
			}
		}		
	}
	
	@Override
	public void manualStop(String reason) {
		synchronized(ctrlMutex) {
			if (inState(ComponentState.RUNNING, ComponentState.PAUSED)) {
				if (log.isLoggable(Level.INFO)) log.info("Stop requested, reason: " + reason);
				stop(true);
			} else {
				if (log.isLoggable(Level.WARNING)) log.warning("Stop requested, but the component is in state " + componentState.getFlag() + ", unsupported.");
			}
		}
	}
	
	@Override
	public void manualKill(String reason) {
		if (log.isLoggable(Level.SEVERE)) log.severe("Kill requested, reason: " + reason);
		kill(true);		
	}
	
	@Override
	public void manualPause(String reason) {
		synchronized(ctrlMutex) {
			if (inState(ComponentState.RUNNING)) {
				if (log.isLoggable(Level.INFO)) log.info(id(component) + " pause requested, reason: " + reason);
				pause(true);
			} else {
				if (log.isLoggable(Level.WARNING)) log.warning(id(component) + " pause requested, but the component is in state " + componentState.getFlag() + ", unsupported.");
			}
		}
	}
	
	@Override
	public void manualResume(String reason) {
		synchronized(ctrlMutex) {
			if (inState(ComponentState.PAUSED)) {
				if (log.isLoggable(Level.INFO)) log.info(id(component) + " resume requested, reason: " + reason);
				resume(true);
			} else {
				if (log.isLoggable(Level.WARNING)) log.warning(id(component) + " resume requested, but the component is in state " + componentState.getFlag() + ", unsupported.");
			}
		}
	}
	
	@Override
	public void fatalError(String message) {
		fatalError(message, null);
	}
	
	@Override
	public void fatalError(String message, Throwable e) {
		// EARLY RETURN (do not dead-lock on multi fatal-errors)
		try {
			if (inState(ComponentState.KILLING, ComponentState.KILLED)) return;
		} catch (Exception e0) {
		}
		synchronized(ctrlMutex) {
			// OK, we're not in the state KILLING or KILLED ... meaning that one thread from fatal-error-threads reaches here 
			try {
				// Are we first here?
				if (inState(ComponentState.KILLING, ComponentState.KILLED)) return;
			} catch (Exception e0) {
			}
			// Yes we are ... let's mark the component to be killed soon.
			try { 
				setState(ComponentState.KILLING); 
			} catch (Exception e1) {
			}	
		}
		
		// WE ARE ALONE HERE!
		
		try {
			if (log.isLoggable(Level.SEVERE)) log.severe("Fatal error in " + id(component) + ": " + message);
		} catch (Exception e2) {
		}
		try {
			control.kill();
		} catch (Exception e3) {			
		}		
		try {
			lastFatalError = new FatalErrorEvent<IComponent>(component, message, e);
			// Important: DO NOT USE 'componentEvents' here! They may have been disabled
			this.bus.event(lastFatalError);
		} catch (Exception e5) {			
		}		
		try { 
			setState(ComponentState.KILLED); 
		} catch (Exception e6) {
		}
	}
	
	//
	//
	// PUBLIC INTERFACE - IComponentController
	//
	//
	
	@Override
	public boolean isDependent(IToken token) {
		return dependencies.isDependency(token);
	}
	
	@Override
	public boolean isDependent(IComponent component) {
		return dependencies.isDependency(component);
	}
	
	//
	//
	// IMPLEMENTATION
	//
	//
	
	/**
	 * Returns how many component we're depending on are in any of 'states'.
	 * 
	 * @param states which states are we counting
	 * @return total number of components (every counted component is in one of 'states')
	 */
	private int getStateCount(ComponentState... states) {
		int total = 0;
		for (ComponentState state : states) {
			total += stateCount.get(state);
		}
		if (total > dependencies.getCount()) {
			throw new IllegalStateException("Sum of ints from stateCount can't be greater than number of dependencies.");
		}
		return total;
	}
	
	private ComponentState getDependencyState(IComponent dependency) {
		return getDependencyState(dependency.getComponentId());
	}
	
	private ComponentState getDependencyState(IToken componentId) {
		return dependencyState.get(componentId);
	}

	/**
	 * Changes tracked dependency state, returns whether the state has truly changed.
	 * @param dependency
	 * @param newState
	 * @return whether the state has changed
	 */
	private boolean setDependencyState(IComponent dependency, ComponentState newState) {
		return setDependencyState(dependency.getComponentId(), newState);
	}
	
	/**
	 * Changes tracked dependency state, returns whether the state has truly changed.
	 * @param dependency
	 * @param newState
	 * @return
	 */
	private boolean setDependencyState(IToken dependency, ComponentState newState) {
		ComponentState oldState = dependencyState.get(dependency);
		if (oldState == newState) return false;
		int count = stateCount.get(oldState);
		if (count <= 0) {
			throw new IllegalStateException("There should not be a dependency in state " + oldState + ", but still...");
		}
		stateCount.put(oldState, count-1);
		dependencyState.put(dependency, newState);
		count = stateCount.get(newState);
		stateCount.put(newState, count+1);
		if (count+1 > dependencies.getCount()) {
			throw new IllegalStateException("There are too many dependencies in state " + newState + ", more than is possible...");
		}
		return true;
	}
	
	private boolean dependencyInState(IComponent dependency, ComponentState... states) {
		return dependencyInState(dependency.getComponentId(), states);
	}
	
	private boolean dependencyInState(IToken componentId, ComponentState... states) {
		ComponentState state = dependencyState.get(componentId);
		if (state == null) return false;
		for (ComponentState s : states) {
			if (state == s) return true;
		}
		return false;
	}
	
	private boolean dependencyNotInState(IComponent dependency, ComponentState... states) {
		return dependencyInState(dependency.getComponentId());
	}
	
	private boolean dependencyNotInState(IToken componentId, ComponentState... states) {
		ComponentState state = dependencyState.get(componentId);
		if (state == null) return true;
		for (ComponentState s : states) {
			if (state == s) return false;
		}
		return true;
	}	
	
	//
	//
	// LEVEL 1 (calling only UTILITY or LEVEL 2 methods)
	//   --->  these methods are first methods that are called when lifecycle events of other components are received, they process every such event
	//         and deciding what to do next based on the state of other components and controlled component (+ providing sanity checks)
	//   --->  these methods are synchronizing access to lower level methods! 
	//
	//
	
	/**
	 * @param event may be {@link IStartingEvent} or {@link IStartedEvent}
	 * @param simulating whether we're simulating the event
	 */
	private void startingEvent(IComponentEvent event, boolean simulating) {
		synchronized(ctrlMutex) {
			NullCheck.check(event, "event");
			IComponent eventComponent = event.getSource();
			
			if (!isDependent(eventComponent)) return;
			
			if (simulating) {
				if (log.isLoggable(Level.FINER)) log.finer("Simulating " + id(eventComponent) + " starting event.");
			} else {
				if (log.isLoggable(Level.FINER)) log.finer("Received " + id(eventComponent) + " starting event.");
			}
			
			// SANITY CHECKS
			if (dependencyInState(eventComponent, ComponentState.STARTING)) {
				if (log.isLoggable(Level.WARNING)) log.warning(id(eventComponent) + " is broadcasting 'starting event' twice, ignoring.");
				return;
			}
			if (!dependencyInState(eventComponent, ComponentState.INSTANTIATED, ComponentState.RESETED, ComponentState.STOPPED)) {
				throw new ComponentCantStartException(id(eventComponent) + " is broadcasting 'starting event' while it is in state " + getDependencyState(eventComponent) + ", unsupported.", log, component);
			}		
			
			// alter the dependency state
			if (setDependencyState(eventComponent, ComponentState.STARTING)) {
				if (getStateCount(ComponentState.STARTING_PAUSED, ComponentState.PAUSING, ComponentState.PAUSED) == 0) {
					// GOING TO LEVEL 2
					startingChangedByStartingEvent(event);
				} else {
					// GOING TO LEVEL 2
					startingChangedByStartingEventButOneComponentIsStartingPausedOrPausingOrPaused(event);
				}
			}
		}
	}
	
	private void startedEvent(IStartedEvent event) {
		synchronized(ctrlMutex) {
			NullCheck.check(event, "event");
			IComponent eventComponent = event.getSource();
			if (!isDependent(eventComponent)) return;
			
			if (log.isLoggable(Level.FINER)) log.finer("Received " + id(eventComponent) + " started event.");
			
			// SANITY CHECKS
			if (dependencyInState(eventComponent, ComponentState.RUNNING)) {
				if (log.isLoggable(Level.WARNING)) log.warning(id(eventComponent) + " is broadcasting 'started event' while running, ignoring.");
				return;
			}
			if (dependencyInState(eventComponent, ComponentState.INSTANTIATED, ComponentState.RESETED, ComponentState.STOPPED)) {
				if (log.isLoggable(Level.WARNING)) log.warning(id(eventComponent) + " is broadcasting 'started event' but did not broadcast 'starting event' before, ill behavior, simulating the event.");
				startingEvent(event, true);
			}			
			if (!dependencyInState(eventComponent, ComponentState.STARTING)) {
				throw new ComponentCantStartException(id(eventComponent) + " is broadcasting 'started event' while it is in state " + getDependencyState(eventComponent) + ", unsupported.", log, component);
			}

			// alter the dependency state
			if (setDependencyState(eventComponent, ComponentState.RUNNING)) {
				// GOING TO LEVEL 2
				runningChangedByStartedEvent(event);
			}
		}
	}
	
	/**
	 * @param event may be {@link IStartingPausedEvent} only
	 * @param simulating whether we're simulating the event
	 */
	private void startingPausedEvent(IComponentEvent event) {
		synchronized(ctrlMutex) {
			NullCheck.check(event, "event");
			IComponent eventComponent = event.getSource();
			
			if (!isDependent(eventComponent)) return;
			
			// SANITY CHECKS
			if (dependencyInState(eventComponent, ComponentState.STARTING)) {
				if (log.isLoggable(Level.WARNING)) log.warning(id(eventComponent) + " is broadcasting 'starting event' twice, ignoring.");
				return;
			}
			if (!dependencyInState(eventComponent, ComponentState.INSTANTIATED, ComponentState.RESETED, ComponentState.STOPPED)) {
				throw new ComponentCantStartException(id(eventComponent) + " is broadcasting 'starting event' while it is in state " + getDependencyState(eventComponent) + ", unsupported.", log, component);
			}			
			
			// alter the dependency state
			if (setDependencyState(eventComponent, ComponentState.STARTING_PAUSED)) {
				// GOING TO LEVEL 2
				startingChangedByStartingPausedEvent(event);
			}
		}
	}
	
	private void pausingEvent(IComponentEvent event, boolean simulating) {
		synchronized(ctrlMutex) {
			NullCheck.check(event, "event");
			IComponent eventComponent = event.getSource();
			if (!isDependent(eventComponent)) return;
				
			if (simulating) {
				if (log.isLoggable(Level.FINER)) log.finer("Simulating " + id(eventComponent) + " pausing event.");
			} else {
				if (log.isLoggable(Level.FINER)) log.finer("Received " + id(eventComponent) + " pausing event.");
			}
			
			// SANITY CHECKS
			if (dependencyInState(eventComponent, ComponentState.PAUSING)) {
				if (log.isLoggable(Level.WARNING)) log.warning(id(eventComponent) + " is broadcasting 'pausing event' twice, ignoring.");
				return;
			}			
			if (!dependencyInState(eventComponent, ComponentState.RUNNING)) {
				throw new ComponentCantPauseException(id(eventComponent) + " is broadcasting 'pausing event' while it is in state " + getDependencyState(eventComponent) + ", unsupported.", log, component);
			}
			
			// alter the dependency state
			if (setDependencyState(eventComponent, ComponentState.PAUSING)) {
				pausingChangedByPausingEvent(event);
			}
		}
	}

	private void pausedEvent(IPausedEvent event) {
		synchronized(ctrlMutex) {
			NullCheck.check(event, "event");
			IComponent eventComponent = event.getSource();
			if (!isDependent(eventComponent)) return;
			
			if (log.isLoggable(Level.FINER)) log.finer("Received " + id(eventComponent) + " paused event.");
			
			// SANITY CHECKS
			if (dependencyInState(eventComponent, ComponentState.PAUSED)) {
				if (log.isLoggable(Level.WARNING)) log.warning(id(eventComponent) + " is broadcasting 'paused event' twice, ignoring.");
				return;
			}
			if (dependencyInState(eventComponent, ComponentState.RUNNING)) {
				if (log.isLoggable(Level.WARNING)) log.warning(id(eventComponent) + " is broadcasting 'paused event' but did not broadcast 'pausing event' before, ill behavior, simulating the event.");
				pausingEvent(event, true);
			}
			if (dependencyInState(eventComponent, ComponentState.STARTING_PAUSED)) {
				// alter the dependency state				
				if (setDependencyState(eventComponent, ComponentState.PAUSED)) {
					// GOING TO LEVEL 2
					pausedChangedByPausedEventAfterStartingPaused(event);
				}
			} else 
			if (dependencyInState(eventComponent, ComponentState.PAUSING)) {
				// alter the dependency state
				if (setDependencyState(eventComponent, ComponentState.PAUSED)) {
					// GOING TO LEVEL 2
					pausedChangedByPausedEvent(event);
				}
			} else {
				throw new ComponentCantPauseException(id(eventComponent) + " is broadcasting 'paused event' while it is in state " + getDependencyState(eventComponent) + ", unsupported.", log, component);
			}			
		}
	}

	/**
	 * @param event may be {@link IStartingEvent} or {@link IStartedEvent}
	 * @param simulating whether we're simulating the event
	 */
	private void resumingEvent(IComponentEvent event, boolean simulating) {
		synchronized(ctrlMutex) {
			NullCheck.check(event, "event");
			IComponent eventComponent = event.getSource();
			if (!isDependent(eventComponent)) return;
			
			if (simulating) {
				if (log.isLoggable(Level.FINER)) log.finer("Simulating " + id(eventComponent) + " resuming event.");
			} else {
				if (log.isLoggable(Level.FINER)) log.finer("Received " + id(eventComponent) + " resuming event.");
			}
			
			// SANITY CHECKS
			if (dependencyInState(eventComponent, ComponentState.RESUMING)) {
				if (log.isLoggable(Level.WARNING)) log.warning(id(eventComponent) + " is broadcasting 'resuming event' twice, ignoring.");
				return;
			}
			if (!dependencyInState(eventComponent, ComponentState.PAUSED)) {
				throw new ComponentCantResumeException(id(eventComponent) + " is broadcasting 'resuming event' while it is in state " + getDependencyState(eventComponent) + ", unsupported.", log, component);
			}				
			
			// alter the dependency state
			if (setDependencyState(eventComponent, ComponentState.RESUMING)) {
				// GOING TO LEVEL 2
				resumingChangedByResumingEvent(event);
			}
		}
	}
	
	private void resumedEvent(IResumedEvent event) {
		synchronized(ctrlMutex) {
			NullCheck.check(event, "event");
			IComponent eventComponent = event.getSource();
			if (!isDependent(eventComponent)) return;
			
			if (log.isLoggable(Level.FINER)) log.finer("Received " + id(eventComponent) + " resumed event.");
			
			// SANITY CHECKS
			if (dependencyInState(eventComponent, ComponentState.RUNNING)) {
				if (log.isLoggable(Level.WARNING)) log.warning(id(eventComponent) + " is broadcasting 'resumed event' while running, ignoring.");
				return;
			}
			if (dependencyInState(eventComponent, ComponentState.PAUSED)) {
				if (log.isLoggable(Level.WARNING)) log.warning(id(eventComponent) + " is broadcasting 'resumed event' but did not broadcast 'resuming event' before, ill behavior, simulating the event.");
				resumingEvent(event, true);
			}
			if (!dependencyInState(eventComponent, ComponentState.RESUMING)) {
				throw new ComponentCantResumeException(id(eventComponent) + " is broadcasting 'resumed event' while it is in state " + getDependencyState(eventComponent) + ", unsupported.", log, component);
			}
			
			// alter the dependency state
			if (setDependencyState(eventComponent, ComponentState.RUNNING)) {
				// GOING TO LEVEL 2
				runningChangedByResumedEvent(event);
			}
		}
	}

	private void stoppingEvent(IComponentEvent event, boolean simulating) {
		synchronized(ctrlMutex) {
			NullCheck.check(event, "event");
			IComponent eventComponent = event.getSource();
			if (!isDependent(eventComponent)) return;
				
			if (simulating) {
				if (log.isLoggable(Level.FINER)) log.finer("Simulating " + id(eventComponent) + " stopping event.");
			} else {
				if (log.isLoggable(Level.FINER)) log.finer("Received " + id(eventComponent) + " stopping event.");
			}
			
			// SANITY CHECKS
			if (dependencyInState(eventComponent, ComponentState.STOPPING)) {
				if (log.isLoggable(Level.WARNING)) log.warning(id(eventComponent) + " is broadcasting 'stopping event' twice, ignoring.");
				return;
			}
			if (!dependencyInState(eventComponent, ComponentState.PAUSED, ComponentState.PAUSING, ComponentState.RESUMING, ComponentState.RUNNING)) {
				throw new ComponentCantStopException(id(eventComponent) + " is broadcasting 'stopping event' while it is in state " + getDependencyState(eventComponent) + ", unsupported.", log, component);				
			}
			
			// alter the dependency state
			if (setDependencyState(eventComponent, ComponentState.STOPPING)) {
				// GOING TO LEVEL 2
				stoppingChangedByStoppingEvent(event);
			}
		}
	}
	
	private void stoppedEvent(IStoppedEvent event) {
		synchronized(ctrlMutex) {
			NullCheck.check(event, "event");
			IComponent eventComponent = event.getSource();
			if (!isDependent(eventComponent)) return;
			
			if (log.isLoggable(Level.FINER)) log.finer("Received " + id(eventComponent) + " stopped event.");
			
			// SANITY CHECKS
			if (dependencyInState(eventComponent, ComponentState.STOPPED)) {
				if (log.isLoggable(Level.WARNING)) log.warning(id(eventComponent) + " is broadcasting 'stopped event' twice, ignoring.");
				return;
			}
			if (dependencyInState(eventComponent, ComponentState.PAUSED, ComponentState.PAUSING, ComponentState.RESUMING, ComponentState.RUNNING)) {
				if (log.isLoggable(Level.WARNING)) log.warning(id(eventComponent) + " is broadcasting 'stopped event' but did not broadcast 'stopping event' before, ill behavior, simulating the event.");
				stoppingEvent(event, true);				
			}
			if (!dependencyInState(eventComponent, ComponentState.STOPPING)) {
				throw new ComponentCantStopException(id(eventComponent) + " is broadcasting 'stopped event' while it is in state " + getDependencyState(eventComponent) + ", unsupported.", log, component);
			}
			
			// alter the dependency state
			if (setDependencyState(eventComponent, ComponentState.STOPPED)) {
				// GOING TO LEVEL 2
				stoppedChangedByStoppedEvent(event);
			}
		}
	}
	
	private void fatalErrorEvent(IFatalErrorEvent event) {
		if (inState(ComponentState.KILLING, ComponentState.KILLED)) {
			// NOTHING TO SEE, MOVE ALONG ...
			return;
		}
		if (event.getSource() == component) {
			if (log.isLoggable(Level.FINER)) log.finer("Fatal error received from the controlled component, discarding.");
			return;
		}
		lastFatalError = event;
		if (log.isLoggable(Level.SEVERE)) log.severe("Received fatal error from " + id(event.getSource()) + ".");
		// GOING TO LEVEL 2
		componentFatalError();		
	}
	
	private void resetEvent(IResetEvent event) {
		synchronized(ctrlMutex) {
			if (log.isLoggable(Level.WARNING)) log.warning("Received reset event.");
			// GOING TO LEVEL 2
			componentReset();
		}
	}

	//
	//
	// LEVEL 2 (Called by LEVEL 1 methods and calling only LEVEL 3 methods) 
	//   --->  these methods are called whenever lifecycle state ({@link ComponentState}) of some component we're depending on changes
	//   --->  the method names are uniformely named
	//              STATE_NAME ChangedBy EVENT
	//                \                    \
	//                 \                    +- EVENT = event that the component (we're depending on) has broadcast | or other explanation for the change
	//                  \
	//                   +- STATE_NAME = state which the component (we're depending on) has switched into 
	//
	//
	
	
	//
	// dependency has changed its STATE into STARTING state
	//
	
	/**
	 * @param event may be {@link IStartingEvent} or {@link IStartingPausedEvent}
	 */
	private void startingChangedByStartingEvent(IComponentEvent event) {
		if (dependencies.getType() == ComponentDependencyType.STARTS_AFTER) {
			return;
		}
		if (getStateCount(ComponentState.STARTING, ComponentState.RESUMING, ComponentState.RUNNING) == dependencies.getCount()) {
			if (inState(ComponentState.STOPPED, ComponentState.RESETED, ComponentState.INSTANTIATED)) {
				if (log.isLoggable(Level.INFO)) log.info("All dependencies are starting/resuming/has started, starting the component.");
				// GOING TO LEVEL 3
				start(false);
			} else {
				throw new ComponentCantStartException("All dependencies are starting/resuming/has started, but can't start the component, it's in an ill state " + componentState.getFlag() + ".", log, component);
			}
		} else
		if (getStateCount(ComponentState.STARTING, ComponentState.STARTING_PAUSED, ComponentState.PAUSING, ComponentState.PAUSED, ComponentState.RESUMING, ComponentState.RUNNING) == dependencies.getCount()) {
			if (inState(ComponentState.STOPPED, ComponentState.RESETED, ComponentState.INSTANTIATED)) {
				if (log.isLoggable(Level.INFO)) log.info("All dependencies are starting/resuming/has started but there are some which are in STARTING_PAUSED/PAUSING/PAUSED state, starting-paused the component.");
				// GOING TO LEVEL 3
				startPaused(false);
			} else {
				throw new ComponentCantStartException("All dependencies are starting/resuming/has started but there are some which are in STARTING_PAUSED/PAUSING/PAUSED state, but can't starting-paused the component, it's in an ill state " + componentState.getFlag() + ".", log, component);
			}
		}
	}

	private void startingChangedByStartingEventButOneComponentIsStartingPausedOrPausingOrPaused(IComponentEvent event) {
		startingChangedByStartingEvent(event);
	}
	
	private void startingChangedByStartingPausedEvent(IComponentEvent event) {
		startingChangedByStartingEvent(event);
	}
	
	//
	// dependency has changed its STATE into RUNNING state
	//
	
	private void runningChangedByStartedEvent(IStartedEvent event) {
		// no need to assess dependencies.getType() == ComponentDependencyType.STARTS_WITH case
		// as this was already triggered by real/simulated starting event of the same component
		if (dependencies.getType() == ComponentDependencyType.STARTS_WITH) {
			return;
		}
		// we're assessing the situation dependencies.getType() == ComponentDependencyType.STARTS_AFTER
		if (getStateCount(ComponentState.RUNNING) == dependencies.getCount()) {
			if (inState(ComponentState.STOPPED, ComponentState.RESETED, ComponentState.INSTANTIATED)) {
				if (log.isLoggable(Level.INFO)) log.info("All dependencies has started/resumed, starting the component.");
				// GOING TO LEVEL 3
				start(false);
			} else {
				throw new ComponentCantStartException("All dependencies has started/resumed, but can't start the component, it's in an ill state " + componentState.getFlag() + ".", log, component);
			}
		} else 
		if (getStateCount(ComponentState.RUNNING, ComponentState.PAUSING, ComponentState.PAUSED) == dependencies.getCount()) {
			if (inState(ComponentState.STOPPED, ComponentState.RESETED, ComponentState.INSTANTIATED)) {
				if (log.isLoggable(Level.INFO)) log.info("All dependencies has started/resumed but there are some that is pausing/paused, starting-paused the component.");
				// GOING TO LEVEL 3
				startPaused(false);
			} else {
				throw new ComponentCantStartException("All dependencies has started/resumed, but there are some that is pausing/paused, but can't starting-paused the component, it's in an ill state " + componentState.getFlag() + ".", log, component);
			}
	}
	}
	
	private void runningChangedByResumedEvent(IResumedEvent event) {
		// no need to assess dependencies.getType() == ComponentDependencyType.STARTS_WITH case
		// as this was already triggered by real/simulated starting event of the same component
		if (dependencies.getType() == ComponentDependencyType.STARTS_WITH) {
			return;
		}
		// we're assessing the situation dependencies.getType() == ComponentDependencyType.STARTS_AFTER
		if (getStateCount(ComponentState.RUNNING) == dependencies.getCount()) {
			if (inState(ComponentState.STOPPED, ComponentState.RESETED, ComponentState.INSTANTIATED)) {
				if (log.isLoggable(Level.INFO)) log.info("All dependencies has started/resumed, starting the component.");
				// GOING TO LEVEL 3
				start(false);
			} else 
			if (inState(ComponentState.PAUSED)) {
				if (log.isLoggable(Level.INFO)) log.info("All dependencies has started/resumed, resuming the component.");
				// GOING TO LEVEL 3
				resume(false);
			} else {
				throw new ComponentCantStartException("All dependencies has started/resumed, but can't start/resume the component, it's in an ill state " + componentState.getFlag() + ".", log, component);
			}
		} else
		if (getStateCount(ComponentState.RUNNING, ComponentState.PAUSING, ComponentState.PAUSED) == dependencies.getCount()) {
			if (inState(ComponentState.STOPPED, ComponentState.RESETED, ComponentState.INSTANTIATED)) {
				if (log.isLoggable(Level.INFO)) log.info("All dependencies has started/resumed but some of them are pausing/paused, starting-paused the component.");
				// GOING TO LEVEL 3
				start(false);
			}
		}
	}

	//
	// dependency has changed its STATE into PAUSING state
	//
	
	private void pausingChangedByPausingEvent(IComponentEvent event) {
		if (inState(ComponentState.RUNNING)) {
			if (log.isLoggable(Level.INFO)) log.info("Dependency " + id(event.getSource()) + " is pausing, pausing the component.");
			// GOING TO LEVEL 3
			pause(false);
		} else {
			// pausing while this component is starting is solved elsewhere --> start() / startPaused() methods
		}
	}
	
	//
	// dependency has changed its STATE into PAUSED state
	//
	
	private void pausedChangedByPausedEvent(IPausedEvent event) {
		if (inState(ComponentState.RUNNING)) {
			if (log.isLoggable(Level.INFO)) log.info("Dependency " + id(event.getSource()) + " has paused, pausing the component.");
			// GOING TO LEVEL 3
			pause(false);
		} else {
			// paused while this component starting is solved elsewhere --> start() / startPaused() methods
		}
	}
	
	private void pausedChangedByPausedEventAfterStartingPaused(IPausedEvent event) {
		// no need to assess dependencies.getType() == ComponentDependencyType.STARTS_WITH case
		// as this was already triggered by real/simulated starting event of the same component
		if (dependencies.getType() == ComponentDependencyType.STARTS_WITH) {
			return;
		}
		// we're assessing the situation dependencies.getType() == ComponentDependencyType.STARTS_AFTER
		if (getStateCount(ComponentState.RUNNING, ComponentState.PAUSED) == dependencies.getCount()) {
			if (inState(ComponentState.STOPPED, ComponentState.RESETED, ComponentState.INSTANTIATED)) {
				if (log.isLoggable(Level.INFO)) log.info("All dependencies has started/started into paused/are paused, starting-paused the component.");
				// GOING TO LEVEL 3
				startPaused(false);
			} else {
				throw new ComponentCantStartException("All dependencies has started/started into paused/are paused, but can't starting-paused the component, it's in an ill state " + componentState.getFlag() + ".", log, component);
			}
		}		
	}
	
	//
	// dependency has changed its STATE into RESUMING state
	//
	
	private void resumingChangedByResumingEvent(IComponentEvent event) {
		if (dependencies.getType() == ComponentDependencyType.STARTS_AFTER) {
			return;
		}
		// we're assessing the situation dependencies.getType() == ComponentDependencyType.STARTS_WITH
		if (getStateCount(ComponentState.STARTING, ComponentState.RESUMING, ComponentState.RUNNING) == dependencies.getCount()) {
			if (inState(ComponentState.STOPPED, ComponentState.RESETED, ComponentState.INSTANTIATED)) {
				if (log.isLoggable(Level.INFO)) log.info("All dependencies has started/resumed, starting the component.");
				// GOING TO LEVEL 3
				start(false);
			} else 
			if (inState(ComponentState.PAUSED)) {
				if (log.isLoggable(Level.INFO)) log.info("All dependencies are starting/resuming/has started, resuming the component.");
				// GOING TO LEVEL 3
				resume(false);
			} else {
				throw new ComponentCantResumeException("All dependencies are starting/resuming/has started, but can't resume the component, it's in an ill state " + componentState.getFlag() + ".", log, component);
			}
		} else 
		if (getStateCount(ComponentState.STARTING, ComponentState.RESUMING, ComponentState.RUNNING, ComponentState.STARTING_PAUSED, ComponentState.PAUSING, ComponentState.PAUSED) == dependencies.getCount()) {
			if (inState(ComponentState.STOPPED, ComponentState.RESETED, ComponentState.INSTANTIATED)) {
				if (log.isLoggable(Level.INFO)) log.info("All dependencies are starting/resuming/has started but there are some which are in STARTING_PAUSED/PAUSING/PAUSED state, starting-paused the component.");
				// GOING TO LEVEL 3
				startPaused(false);
			}
		}
	}

	//
	// dependency has changed its STATE into STOPPING state
	//
	
	private void stoppingChangedByStoppingEvent(IComponentEvent event) {
		if (inState(ComponentState.PAUSED, ComponentState.PAUSING, ComponentState.RESUMING, ComponentState.RUNNING)) {
			if (log.isLoggable(Level.INFO)) log.info("Dependency " + id(event.getSource()) + " is stopping, stopping the component.");
			// GOING TO LEVEL 3
			stop(false);
		} else {
			// stopping while this component is starting is solved elsewhere --> start() / startPaused() methods
		}
	}

	//
	// dependency has changed its STATE into STOPPED state
	//
	
	private void stoppedChangedByStoppedEvent(IStoppedEvent event) {
		if (inState(ComponentState.PAUSED, ComponentState.PAUSING, ComponentState.RESUMING, ComponentState.RUNNING)) {
			if (log.isLoggable(Level.INFO)) log.info("Dependency " + id(event.getSource()) + " is stopping, stopping the component.");
			// GOING TO LEVEL 3
			stop(false);
		} else {
			// stopped while this component is starting is solved elsewhere --> start() / startPaused() methods
		}
	}
	
	//
	// some component has broadcast FATAL ERROR
	//
	
	private void componentFatalError() {
		// WARNING: We may not be under ctrlMutex here!!!
		switch(componentState.getFlag()) {
		case INSTANTIATED:
			if (log.isLoggable(Level.WARNING)) log.warning("Component is in instantiated state, won't call kill().");
			return;
		case RESETED:
			if (log.isLoggable(Level.WARNING)) log.warning("Component is resetted, won't call kill().");
			return;
		case KILLED:
			if (log.isLoggable(Level.WARNING)) log.warning("Component has been already killed, won't call kill().");
			return;
		}
		// GOING TO LEVEL 3
		kill(false);
	}
	
	//
	// RESET event received
	//
	
	private void componentReset() {
		switch(componentState.getFlag()) {
		case INSTANTIATED:
			if (log.isLoggable(Level.WARNING)) log.warning("Component is in instantiated state, won't call reset().");
			return;
		case RESETED:
			if (log.isLoggable(Level.WARNING)) log.warning("Component is resetted, won't call reset().");
			return;
		case KILLED:
			// GOING TO LEVEL 3
			reset();
			return;
		default:
			if (log.isLoggable(Level.WARNING)) log.warning("Reset event received but the component has not been killed! Current state is " + componentState.getFlag() + ", killing the component first!");
			// GOING TO LEVEL 3			
			kill(false);
			if (log.isLoggable(Level.WARNING)) log.warning("And than, resetting it!");
			// GOING TO LEVEL 3
			reset();
			return;
		}
	}
	
	//
	//
	// LEVEL 3 (Called by LEVEL 2 / manualXXX() methods and calling only UTILITY METHODS) 
	//   -- methods are not checking whether they can perform requested operation! 
	//
	//
	
	private void start(boolean manual) {
		setState(ComponentState.STARTING);
		if (log.isLoggable(Level.FINE)) log.fine("Calling " + id(component) + ".preStart().");
		control.preStart();
		if (log.isLoggable(Level.FINE)) log.fine("Sending " + id(component) + " starting event (transactional).");
		componentEvents.startingTransactional();
		
		if (log.isLoggable(Level.FINE)) log.fine(id(component) + " starting event (transactional) sent.");

		if (!manual) {
			// not manual?
			// --> check the dependencies state count again as somebody may have stopped/paused
			if (dependencies.getType() == ComponentDependencyType.STARTS_WITH) {
				// STARTS_WITH case
				if (getStateCount(ComponentState.STARTING, ComponentState.RESUMING, ComponentState.RUNNING) == dependencies.getCount()) {
					// all is OK!					
				} else
				if (getStateCount(ComponentState.STARTING, ComponentState.RESUMING, ComponentState.RUNNING, ComponentState.PAUSING, ComponentState.PAUSED) == dependencies.getCount()) {
					// some component we've been depending on is pausing / has paused
					fatalError("Some components is pausing / has paused after starting-event of " + id(component) + ", unsupported state!");
					return;
				} else {
					// some components has stopped
					fatalError("Some components has stopped after starting-event of " + id(component) + ", unsupported state!");
					return;
				}
			} else {
				// STARTS_AFTER case
				if (getStateCount(ComponentState.RUNNING) == dependencies.getCount()) {
					// all is OK!					
				} else
				if (getStateCount(ComponentState.RUNNING, ComponentState.PAUSING, ComponentState.PAUSED) == dependencies.getCount()) {
					// some component we've been depending on is pausing / has paused
					fatalError("Some components is pausing / has paused after starting-event of " + id(component) + ", unsupported state!");
					return;
				} else {
					// some components has stopped
					fatalError("Some components has stopped after starting-event of " + id(component) + ", unsupported state!");
					return;
				}
			}
		}
		
		if (log.isLoggable(Level.FINE)) log.fine("Calling " + id(component) + ".start().");
		control.start();
		if (log.isLoggable(Level.INFO)) log.info(id(component) + ".start()ed.");
		
		setState(ComponentState.RUNNING);
		if (log.isLoggable(Level.FINE)) log.fine("Sending " + id(component) + " started event (transactional).");
		componentEvents.startedTransactional();
	}
	
	private void startPaused(boolean manual) {
		setState(ComponentState.STARTING_PAUSED);
		if (log.isLoggable(Level.FINE)) log.fine("Calling " + id(component) + ".preStartPaused().");
		control.preStartPaused();
		if (log.isLoggable(Level.FINE)) log.fine("Sending " + id(component) + " starting-paused event (transactional).");
		componentEvents.startingPausedTransactional();
		
		if (log.isLoggable(Level.FINE)) log.fine(id(component) + " starting-paused event (transactional) sent.");

		if (!manual) {
			// not manual?
			// --> check the dependencies state count again as somebody may have stopped/paused
			if (dependencies.getType() == ComponentDependencyType.STARTS_WITH) {
				// STARTS_WITH case
				if (getStateCount(ComponentState.STARTING, ComponentState.RESUMING, ComponentState.RUNNING) == dependencies.getCount()) {
					// all components is starting/is resuming/running, some components that was pausing/has been paused is now resuming/resumed
					fatalError("Some components is resuming / has resumed after starting-paused-event of " + id(component) + ", unsupported state!");
					return;
				} else
				if (getStateCount(ComponentState.STARTING, ComponentState.STARTING_PAUSED, ComponentState.RESUMING, ComponentState.RUNNING, ComponentState.PAUSING, ComponentState.PAUSED) == dependencies.getCount()) {
					// all is OK!					
				} else {
					// some components has stopped
					fatalError("Some components has stopped after starting-paused-event of " + id(component) + ", unsupported state!");
					return;
				}
			} else {
				// STARTS_AFTER case
				if (getStateCount(ComponentState.RUNNING) == dependencies.getCount()) {
					// all components is starting/is resuming/running, some components that was pausing/has been paused is now resuming/resumed
					fatalError("Some components is resuming / has resumed after starting-paused-event of " + id(component) + ", unsupported state!");
					return;
				} else
				if (getStateCount(ComponentState.RUNNING, ComponentState.PAUSING, ComponentState.PAUSED) == dependencies.getCount()) {
					// all is OK!					
				} else {
					// some components has stopped
					fatalError("Some components has stopped after starting-paused-event of " + id(component) + ", unsupported state!");
					return;
				}
			}
		}
		
		if (log.isLoggable(Level.FINE)) log.fine("Calling " + id(component) + ".startPaused().");
		control.startPaused();
		if (log.isLoggable(Level.INFO)) log.info(id(component) + ".startPaused()ed.");
		
		setState(ComponentState.PAUSED);
		if (log.isLoggable(Level.FINE)) log.fine("Sending " + id(component) + " paused event (transactional).");
		componentEvents.pausedTransactional();		
	}
	
	/**
	 * @param manual whether the method has been called from manualStop() method
	 */
	private void pause(boolean manual) {
		setState(ComponentState.PAUSING);
		if (log.isLoggable(Level.FINE)) log.fine("Calling " + id(component) + ".prePause().");
		control.prePause();
		if (log.isLoggable(Level.FINE)) log.fine("Sending " + id(component) + " pausing event (transactional).");
		componentEvents.pausingTransactional();
		if (log.isLoggable(Level.FINE)) log.fine("Calling " + id(component) + ".pause().");
		control.pause();
		if (log.isLoggable(Level.INFO)) log.info(id(component) + ".paused()ed.");
		setState(ComponentState.PAUSED);
		if (log.isLoggable(Level.FINE)) log.fine("Sending " + id(component) + " paused event (transactional).");
		componentEvents.pausedTransactional();
	}
	
	/**
	 * @param manual whether the method has been called from manualStop() method
	 */
	private void resume(boolean manual) {
		setState(ComponentState.RESUMING);
		if (log.isLoggable(Level.FINE)) log.fine("Calling " + id(component) + ".preResume().");
		control.preResume();
		if (log.isLoggable(Level.FINE)) log.fine("Sending " + id(component) + " resuming event (transactional).");
		componentEvents.resumingTransactional();
		if (log.isLoggable(Level.FINE)) log.fine("Calling " + id(component) + ".resume().");
		control.resume();
		if (log.isLoggable(Level.INFO)) log.info(id(component) + ".resum()ed.");
		setState(ComponentState.RUNNING);
		if (log.isLoggable(Level.FINE)) log.fine("Sending " + id(component) + " resumed event (transactional).");
		componentEvents.resumedTransactional();
	}

	/**
	 * @param manual whether the method has been called from manualStop() method
	 */
	private void stop(boolean manual) {
		setState(ComponentState.STOPPING);
		if (log.isLoggable(Level.FINE)) log.fine("Calling " + id(component) + ".preStop().");
		control.preStop();
		if (log.isLoggable(Level.FINE)) log.fine("Sending " + id(component) + " stopping event (transactional).");
		componentEvents.stoppingTransactional();
		if (log.isLoggable(Level.FINE)) log.fine("Calling " + id(component) + ".stop().");
		control.stop();
		if (log.isLoggable(Level.INFO)) log.info(id(component) + ".stop()ed.");
		setState(ComponentState.STOPPED);
		componentEvents.stoppedTransactional();
	}
	
	/**
	 * Will eat all exceptions...
	 * @param manual
	 */
	private void kill(boolean manual) {
		if (inState(ComponentState.KILLING, ComponentState.KILLED)) {
			// NOTHING TO SEE, MOVE ALONG
			return;
		}
		
		synchronized(ctrlMutex) {
			if (inState(ComponentState.KILLING, ComponentState.KILLED)) {
				// NOTHING TO SEE, MOVE ALONG
				return;
			}
			try {
				setState(ComponentState.KILLING);
			} catch (Exception e) {
			}
		}
		
		// WE'RE ALONE HERE!
		
		try {
			if (log.isLoggable(Level.WARNING)) log.warning("Calling " + id(component) + ".kill().");
		} catch (Exception e) {
		}
		try {
			control.kill();
		} catch(Exception e) {			
		}
		try {
			if (log.isLoggable(Level.SEVERE)) log.severe(id(component) + ".kill()ed.");
		} catch (Exception e) {
		}
		try {
			setState(ComponentState.KILLED);
		} catch (Exception e) {
		}
	}
	
	private void reset() {
		setState(ComponentState.RESETTING);
		if (log.isLoggable(Level.FINE)) log.fine("Reseting " + id(component) + "'s controller.");
		resetController();
		if (log.isLoggable(Level.INFO)) log.info(id(component) + "'s controller reseted.");
		if (log.isLoggable(Level.FINE)) log.fine("Calling " + id(component) + ".reset().");
		control.reset();
		if (log.isLoggable(Level.INFO)) log.info(id(component) + ".reset()ed.");
		setState(ComponentState.RESETED);
	}
	
	private void resetController() {
		for (IToken dependency : dependencies.getDependencies()) {
			dependencyState.put(dependency, ComponentState.RESETED);
		}
		for (ComponentState state : ComponentState.values()) {
			stateCount.put(state, 0);
		}
		stateCount.put(ComponentState.RESETED, dependencies.getCount());
		lastFatalError = null;
	}

}