package cz.cuni.amis.pogamut.base.agent.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import cz.cuni.amis.introspection.Folder;
import cz.cuni.amis.introspection.java.ReflectionObjectFolder;
import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.component.event.AgentEvents;
import cz.cuni.amis.pogamut.base.agent.exceptions.AgentException;
import cz.cuni.amis.pogamut.base.agent.jmx.AgentJMXComponents;
import cz.cuni.amis.pogamut.base.agent.jmx.IJMXEnabled;
import cz.cuni.amis.pogamut.base.agent.state.WaitForAgentStateChange;
import cz.cuni.amis.pogamut.base.agent.state.impl.AgentState;
import cz.cuni.amis.pogamut.base.agent.state.impl.AgentStateFailed;
import cz.cuni.amis.pogamut.base.agent.state.impl.AgentStateFailing;
import cz.cuni.amis.pogamut.base.agent.state.impl.AgentStateInstantiated;
import cz.cuni.amis.pogamut.base.agent.state.impl.AgentStatePaused;
import cz.cuni.amis.pogamut.base.agent.state.impl.AgentStatePausing;
import cz.cuni.amis.pogamut.base.agent.state.impl.AgentStateResumed;
import cz.cuni.amis.pogamut.base.agent.state.impl.AgentStateResuming;
import cz.cuni.amis.pogamut.base.agent.state.impl.AgentStateStarted;
import cz.cuni.amis.pogamut.base.agent.state.impl.AgentStateStartedPaused;
import cz.cuni.amis.pogamut.base.agent.state.impl.AgentStateStarting;
import cz.cuni.amis.pogamut.base.agent.state.impl.AgentStateStartingPaused;
import cz.cuni.amis.pogamut.base.agent.state.impl.AgentStateStopped;
import cz.cuni.amis.pogamut.base.agent.state.impl.AgentStateStopping;
import cz.cuni.amis.pogamut.base.agent.state.level0.IAgentState;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateDown;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateGoingDown;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateGoingUp;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateUp;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateFailed;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateFailing;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateInstantiated;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStatePaused;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStatePausing;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateResuming;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateRunning;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateStarting;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateStartingPaused;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateStopped;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateStopping;
import cz.cuni.amis.pogamut.base.agent.state.level3.IAgentStateStarted;
import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
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
import cz.cuni.amis.pogamut.base.component.bus.event.impl.FatalErrorEvent;
import cz.cuni.amis.pogamut.base.component.controller.ComponentController;
import cz.cuni.amis.pogamut.base.component.exception.ComponentCantPauseException;
import cz.cuni.amis.pogamut.base.component.exception.ComponentCantResumeException;
import cz.cuni.amis.pogamut.base.component.exception.ComponentCantStartException;
import cz.cuni.amis.pogamut.base.component.exception.ComponentCantStopException;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.ILifecycleBus;
import cz.cuni.amis.pogamut.base.utils.jmx.FolderToIJMXEnabledAdapter;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.utils.ExceptionToString;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.collections.HashSetClass;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;
import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.flag.ImmutableFlag;
import cz.cuni.amis.utils.flag.WaitForFlagChange;
import cz.cuni.amis.utils.token.IToken;

/**
 * Abstract agent class, provides basic interface for the agent implementing its lifecycle methods + introducing JMX.
 * <p><p>
 * Agent has fully implemented lifecycle methods such as {@link AbstractAgent#start()}, {@link AbstractAgent#startPaused()}
 * or {@link AbstractAgent#stop()}. It also listens for {@link IFatalErrorEvent} on its bus in order to terminate itself
 * in case of any errors. Also, if all components of the agent stops themselves, the agent is stopped as well.
 * If you do not like this behavior, email me to jakub.gemrot@gmail.com and I will make this optional.
 * <p><p>
 * Note that the Pogamut agent is designed to be a multicomponent beast. And by component we're meaning objects
 * that implements {@link IComponent} registers itself into {@link AbstractAgent#getEventBus()} owned by the agent
 * and adhering to the lifecycle provided either by {@link ComponentController} or 
 * {@link ILifecycleBus#addLifecycleManagement(IToken, cz.cuni.amis.pogamut.base.component.controller.IComponentControlHelper, cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies)}.
 * <p>
 * These components (if correctly configured, which all native components are) starts together with the call of {@link AbstractAgent#start()}
 * or {@link AbstractAgent#startPaused()}, as well as stopping themselves in the case of {@link AbstractAgent#stop()}.
 * <p><p>
 * This model seems to defy the OOP, in fact it is its very incarnation applying principles or "separation of concerns",
 * "modular design", whatever you would like to call it. Many native components are designed the way they interact
 * with each other only via interfaces, they do not depend on each other directly thus greatly enabling anybody to
 * hack inside any internal component of any Pogamut agent.
 * <p><p>
 * <b>JMX</b>
 * <p><p>
 * We're purposely using neither inheritance here nor decorator (or such
 * wrappers) because we need JMX to be usable by every descendant of the class
 * (so we don't want to have another JMX class branch, nor wrapper that denies
 * the inheritance by its nature).
 * <p>
 * To keep the JMX-specific methods at minimum in the agent class we're grouping them into the
 * private inner class object JMXComponents that can be accessed via getJMX()
 * method. This object maintain also a list of IJMXEnabled components that
 * should be enabled when the whole JMX feature is being enabled.
 * <p><p>
 * Usage:
 * <p>
 * <i>To start JMX on the agent:</i> getJMX().enableJMX(mBeanServer, domain)
 * <p>
 * <i>To add another JMX component to the agent:</i>
 * getJMX().addComponent(component)
 * </p>
 * 
 * @author Jimmy
 */
public abstract class AbstractAgent implements IAgent {

    /**
     * Name of the root introspection folder.
     */
    public static final String INTROSPECTION_ROOT_NAME = "root";

    /**
     * Log category name used for {@link AbstractAgent#log}.
     */
	public static final String LOG_CATEGORY_NAME = "Agent";
    
	/**
	 * Logger that is used by the Pogamut (that means by the class hierarchy
	 * starting from AbstractAgent).
	 */
	private IAgentLogger logger = null;
	
	/**
	 * Agent's log category, goes under category name {@link AbstractAgent#LOG_CATEGORY_NAME}.
	 */
	protected LogCategory log = null;

	/**
	 * State of the agent - it is a flag so you may attach listeners to it.
	 */
	private Flag<IAgentState> agentState = new Flag<IAgentState>(new AgentStateInstantiated("Just created."));

	/**
	 * Wraps a few methods to one place so it won't plague the public method
	 * space of the agent. (Make things a bit clear...).
	 * <p><p>
	 * Contains list of IJMXEnabled components that should be enabled when the
	 * whole JMX feature of the agent is fired up.
	 * <p><p>
	 * Lazy initialization upon calling getJMX().
	 * <p><p>
	 * We're direct-accessing the field in-case we need to know whether the JMX has been already initialized or not
	 * comparing it against <i>null</i>.
	 */
	private AgentJMXComponents jmx = null;

	private Object jmxMutex = new Object();
	
	/**
	 * Contains system event bus, that is used to propagate agent-system events (usually start/stop/fatal error messages).
	 */
	private IComponentBus eventBus;
	
	/**
	 * Introspection folder of the agent.
	 */
	private Folder folder = null;
			
	/**
	 * Gateway for sending events into the event bus.
	 */
	protected AgentEvents events;
	
	/**
	 * Agent id.
	 */
	private IAgentId agentId;
	
	/**
	 * Running components of the agent.
	 */
	private Map<IToken, IComponent> runningComponents = new HashMap<IToken, IComponent>();
	
	/**
	 * Components, the agent depends on.
	 */
	private HashSetClass stopDependencyClass = new HashSetClass();
	
	/**
	 * Components' ids, the agent depends on.
	 */
	private Set<IToken> stopDependencyToken = new HashSet<IToken>();
		
	private IComponentEventListener<IStartedEvent> startedEventListener = new IComponentEventListener<IStartedEvent>() {

		@Override
		public void notify(IStartedEvent event) {
			if (event.getSource() == null) return;
			if (event.getSource() == AbstractAgent.this) return;
			synchronized(runningComponents) {
				IComponent component = runningComponents.get(event.getSource().getComponentId());
				if (component == null) {
					// ADDING NEW COMOPNENT TO THE AGENT
					if (log.isLoggable(Level.FINE)) log.fine("Component " + event.getSource() + " started.");
					componentStarted(event);
				} else {
					if (component == event.getSource()) {
						if (log.isLoggable(Level.WARNING)) log.warning("Component " + event.getSource() + " has started more than once.");
					} else {
						throw new AgentException("Component id clash, two instances of components have the same component id = " + event.getSource().getComponentId(), this);				
					}
				}
			}
		}
		
	};
	
	private IComponentEventListener<IPausedEvent> pausedEventListener = new IComponentEventListener<IPausedEvent>() {

		@Override
		public void notify(IPausedEvent event) {
			if (event.getSource() == null) return;
			if (event.getSource() == AbstractAgent.this) return;
			synchronized(runningComponents) {
				IComponent component = runningComponents.get(event.getSource().getComponentId());
				if (component == null) {
					// ADDING NEW COMOPNENT TO THE AGENT
					if (log.isLoggable(Level.FINE)) log.fine("Component " + event.getSource() + " started.");
					componentStarted(event);
				} else {
					if (component != event.getSource()) {
						throw new AgentException("Component id clash, two instances of components have the same component id = " + event.getSource().getComponentId(), this);				
					}
				}
			}
		}
		
	};
	
	private IComponentEventListener<IStoppingEvent> stoppingEventListener = new IComponentEventListener<IStoppingEvent>() {

		@Override
		public void notify(IStoppingEvent event) {
			componentStopping(event);
		}
		
	};
	
	private IComponentEventListener<IStoppedEvent> stoppedEventListener = new IComponentEventListener<IStoppedEvent>() {

		@Override
		public void notify(IStoppedEvent event) {
			synchronized(runningComponents) {
				IComponent component = runningComponents.get(event.getSource().getComponentId());
				if (component == null) {
					if (log.isLoggable(Level.WARNING)) log.warning("Component " + event.getSource() + " stopped, but it has never reported that it started.");
				} else {
					if (component == event.getSource()) {
						// REMOVING COMPONENT FROM THE AGENT
						if (log.isLoggable(Level.WARNING)) log.warning("Component " + event.getSource() + " has stopped.");
						componentStopped(event);
					} else {
						throw new AgentException("Component id clash, two instances of components have the same component id = " + event.getSource().getComponentId(), this);					
					}
				}
			}
		}
		
	};
	
	private IComponentEventListener<IFatalErrorEvent> fatalErrorEventListener = new IComponentEventListener<IFatalErrorEvent>() {

		@Override
		public void notify(IFatalErrorEvent event) {
			if (log.isLoggable(Level.SEVERE)) log.severe("Fatal error sensed: " + event);
			componentFatalError(event);
		}
		
	};
	
	private IComponentEventListener<IResetEvent> resetEventListener = new IComponentEventListener<IResetEvent>() {

		@Override
		public void notify(IResetEvent event) {
			resetEvent(event);
		}
		
	};
	
	/**
	 * Introspection folder with properties and other subfolders obtained from
	 * this agent.
	 * @param agentId unique id of the agent
	 * @param eventBus agent's event bus system
	 * @param logger agent's logger, used to obtain {@link AgentName} instance
	 */
	public AbstractAgent(IAgentId agentId, IComponentBus eventBus, IAgentLogger logger) {
		this.logger = logger;
		NullCheck.check(this.logger, "logger");
		this.agentId = agentId;
		NullCheck.check(this.agentId, "agentId");
		this.eventBus = eventBus;
		NullCheck.check(this.eventBus, "eventBus");
		this.log = this.logger.getCategory(LOG_CATEGORY_NAME);
		NullCheck.check(this.log, "logger.getCategory()");
		
		if (log.isLoggable(Level.INFO)) log.info("Initializing " + getClass().getSimpleName() + ", id: " + this.agentId.getToken());
		
		this.events = new AgentEvents(this.eventBus, this, log);
		
		this.eventBus.addEventListener(IStartedEvent.class,    startedEventListener);
		this.eventBus.addEventListener(IPausedEvent.class,     pausedEventListener);
		this.eventBus.addEventListener(IStoppingEvent.class,   stoppingEventListener);
		this.eventBus.addEventListener(IStoppedEvent.class,    stoppedEventListener);
		this.eventBus.addEventListener(IFatalErrorEvent.class, fatalErrorEventListener);
		this.eventBus.addEventListener(IResetEvent.class,      resetEventListener);
	}	
	
	@Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (!(other instanceof AbstractAgent)) return false;
        AbstractAgent otherAgent = (AbstractAgent) other;
        return this.agentId.equals(otherAgent.getComponentId());
    }
    
    @Override
	public int hashCode() {
    	return this.agentId.hashCode();
    }

	////
	//
	// INTERFACE METHOD IMPLEMENTATIONS
	//
	////
	
    @Override
	public IAgentLogger getLogger() {
		return this.logger;
	}

    @Override
	public ImmutableFlag<IAgentState> getState() {
		return agentState.getImmutable();
	}
    
    /**
     * Returns true if the agent is in one of 'states'.
     * @param states
     * @return
     */
    public boolean inState(Class<?>... states) {
    	Class<?> agentState = getState().getFlag().getClass();
    	for (Class<?> state : states) {
    		if (state.isAssignableFrom(agentState)) return true;
    	}
    	return false;
    }
    
    /**
     * Returns true if the agent is not in any of 'states'.
     * @param states
     * @return
     */
    public boolean notInState(Class<?>... states) {
    	Class<?> agentState = getState().getFlag().getClass();
    	for (Class<?> state : states) {
    		if (state.isAssignableFrom(agentState)) return false;
    	}
    	return true;
    }
    
    @Override
	public IComponentBus getEventBus() {
		return eventBus;
	}
    
    @Override
    public String getName() {
    	return agentId.getName().getFlag();
    }
	
	@Override
	public IAgentId getComponentId() {
		return agentId;
	}
	
	/**
	 * Returns log category of the agent, used by agent lifecycle management methods itself.
	 */
	public LogCategory getLog() {
		return log;
	}
		
	private void startFailedTest() {
		if (inState(IAgentStateFailed.class)) {
			throw new ComponentCantStartException("Agent has been killed during initialization.", log, this);
		}
	}

	private boolean startMethodCalled = false;
	
	/**
	 * Starts the agent.
	 * <p><p>
	 * 1) switches state to {@link IAgentStateStarting}<p>
	 * 2) broadcasts {@link IStartingEvent} (transactional)<p>
	 * 3) calls startAgent()<p>
	 * 3) broadcasts {@link IStartedEvent} (transactional)<p>
	 * 4) switches state to {@link IAgentStateStarted}<p>
	 * <p><p>
	 * Every agent component that wants to start together with the agent should do so during (2 or 3) and broadcast eventTransactionl({@link IStartedEvent} / {@link IPausedEvent}).
	 * <p><p>
	 * Prevents recursion.
	 * 
	 * @throws ComponentCantStartException
	 */
	@Override
	public final synchronized void start() throws ComponentCantStartException {
		if (startMethodCalled) return;
	
		if (inState(IAgentStateGoingUp.class, IAgentStateUp.class)) return;
		if (notInState(IAgentStateDown.class)) {
			throw new ComponentCantStartException("Agent can't start, it is in wrong state (" + getState().getFlag() + ") stop() or kill() agent before start()ing.", log, this);
		}
		
		startMethodCalled = true;
		try {
			if (log.isLoggable(Level.WARNING)) log.warning("Starting agent " + getComponentId().getToken());
	    	if (jmx != null) {
	    		// reregister JMX components again
	    		getJMX().registerJMX();
	    	}
			if (!eventBus.isRunning()) {
				if (log.isLoggable(Level.WARNING)) log.warning("Event bus is not running, resetting.");
				eventBus.reset();
				if (!eventBus.isRunning()) { 
					throw new ComponentCantStartException("Event bus reset()ed but it's still not running.", log, this);
				}
			}
			setState(new AgentStateStarting("Sending 'starting' event."));
			startFailedTest();
			events.startingTransactional();
			startFailedTest();
			setState(new AgentStateStarting("Calling startAgent()."));
			startFailedTest();
			startAgent();
			startFailedTest();
			setState(new AgentStateStarting("Sending 'started' event."));
			startFailedTest();
			events.startedTransactional();
			startFailedTest();
			setState(new AgentStateStarted("Agent has started."));
			startFailedTest();
			if (log.isLoggable(Level.INFO)) log.info(runningComponents.size() + " component" + (runningComponents.size() > 1 ? "s" : "") + " has started along with the agent.");
		} catch (Exception e) {
			if (!inState(IAgentStateFailed.class) && !events.fatalError("Can't start the agent", e)) {
				componentFatalError(new FatalErrorEvent(this, "agent's fatal error not propagated"));
			}
			if (e instanceof ComponentCantStartException) throw (ComponentCantStartException)e;
			throw new ComponentCantStartException("Can't start: " + e.getMessage(), e, log, this);
		} finally {
			startMethodCalled = false;
		}
	}
	
	/**
	 * Starts the agent into paused state.
	 * <p><p>
	 * 1) switches state to {@link IAgentStateStartingPaused}<p>
	 * 2) broadcasts {@link IStartingPausedEvent} (transactional)<p>
	 * 3) calls startAgent()<p>
	 * 3) broadcasts {@link IPausedEvent} (transactional)<p>
	 * 4) switches state to {@link IAgentStatePaused}<p>
	 * <p><p>
	 * Every agent component that wants to start together with the agent should do so during (2 or 3) and broadcast eventTransactionl({@link IStartedEvent} / {@link IPausedEvent}).
	 * <p><p>
	 * Prevents recursion.
	 * 
	 * @throws ComponentCantStartException
	 */
	@Override
	public final synchronized void startPaused() throws ComponentCantStartException {
		if (startMethodCalled) return;
	
		if (inState(IAgentStateGoingUp.class, IAgentStateUp.class)) return;
		if (notInState(IAgentStateDown.class)) {
			throw new ComponentCantStartException("Agent can't start, it is in wrong state (" + getState().getFlag() + ") stop() or kill() agent before start()ing.", log, this);
		}
		
		startMethodCalled = true;
		try {
			if (log.isLoggable(Level.WARNING)) log.warning("Starting-paused agent " + getComponentId().getToken());
	    	if (jmx != null) {
	    		// reregister JMX components again
	    		getJMX().registerJMX();
	    	}
			if (!eventBus.isRunning()) {
				if (log.isLoggable(Level.WARNING)) log.warning("Event bus is not running, resetting.");
				eventBus.reset();
				if (!eventBus.isRunning()) { 
					throw new ComponentCantStartException("Event bus reset()ed but it's still not running.", log, this);
				}
			}
			setState(new AgentStateStartingPaused("Sending 'starting-paused' event."));
			startFailedTest();
			events.startingPausedTransactional();
			startFailedTest();
			setState(new AgentStateStartingPaused("Calling startPausedAgent()."));
			startFailedTest();
			startPausedAgent();
			startFailedTest();
			setState(new AgentStateStartingPaused("Sending 'paused' event."));
			startFailedTest();
			events.pausedTransactional();
			startFailedTest();
			setState(new AgentStateStartedPaused("Agent has started into paused state."));
			startFailedTest();
			if (log.isLoggable(Level.INFO)) log.info(runningComponents.size() + " component" + (runningComponents.size() > 1 ? "s" : "") + " has started along with the agent.");
		} catch (Exception e) {
			if (!inState(IAgentStateFailed.class) && !events.fatalError("Can't start-paused the agent", e)) {
				componentFatalError(new FatalErrorEvent(this, "agent's fatal error not propagated"));
			}
			if (e instanceof ComponentCantStartException) throw (ComponentCantStartException)e;
			throw new ComponentCantStartException("Can't start-paused: " + e.getMessage(), e, log, this);
		} finally {
			startMethodCalled = false;
		}
	}
	
	/**
	 * Called before any {@link AgentEvents#stopping()} event is broadcast. Hook that allows you to implement just-before-death stuff
	 * before any of agent component actually dies.
	 */
	protected void preStopAgent() {		
	}
	
	/**
	 * Stops the agent.
	 * <p><p>
	 * 1) switches state to {@link IAgentStateStopping}<p>
	 * 2) broadcasts {@link IStoppingEvent} (transactional)<p>
	 * 3) calls stopAgent()<p>
	 * 4) checks whether all components has stopped, if not - performs kill().
	 * 5) broadcasts {@link IStoppedEvent} (transactional)<p>
	 * 6) switches state to {@link IAgentStateStopped}<p>
	 * <p>
	 * Every agent's component that is still running must stop itself during (2) by broadcasting 'eventTransactional({@link IStoppedEvent})'.
	 * <p><p>
	 * If there will be some component that remains started after the propagation of {@link IStoppingEvent} the method
	 * will perform kill() method (counts as fatal error) and throws {@link AgentException}.
	 * <p><p>
	 * Prevents recursion.
	 * 
	 * @throws ComponentCantStopException
	 */
	@Override
	public final synchronized void stop() throws ComponentCantStopException {
		if (stopMethodCalled) return;
		
		if (inState(IAgentStateGoingDown.class, IAgentStateDown.class, IAgentStateInstantiated.class)) return;
		if (inState(IAgentStateGoingUp.class)) {
			throw new ComponentCantStopException("stop() requested during initialization, kill() the agent.", log, this);
		}
		if (notInState(IAgentStateUp.class)) {
			throw new ComponentCantStopException("Agent can't stop, it is in wrong state (" + getState().getFlag() + "), call start() first.", log, this);
		}
		
		stopMethodCalled = true;
		try {
			if (log.isLoggable(Level.WARNING)) log.warning("Stopping agent " + getComponentId().getToken());
			setState(new AgentStateStopping("stop() requested, calling preStopAgent()"));
			preStopAgent();
			setState(new AgentStateStopping("stop() requested, sending 'stopping' event"));
			events.stoppingTransactional();
			setState(new AgentStateStopping("Calling stopAgent()."));
			stopAgent();
			if (runningComponents.size() > 1) {
				StringBuffer sb = new StringBuffer();
				boolean first = true;
				for (IComponent component : runningComponents.values()) {
					if (component == this) continue;
					if (first) first = false;
					else sb.append(", ");
					sb.append(component.getComponentId().getToken());
					sb.append(":");
					sb.append(component);
				}
				ComponentCantStopException e = new ComponentCantStopException("Not all components has stopped along with the agent - components that did not send stopped event (id:toString): " + sb.toString(), log, this); 
				if (!events.fatalError(e)) {
					componentFatalError(new FatalErrorEvent(this, "agent's fatal error not propagated"));
				}
				throw e;
			}
			setState(new AgentStateStopping("Sending 'stopped' event."));
			events.stoppedTransactional();
			setState(new AgentStateStopped("Agent stopped."));
			if (jmx != null) {
				getJMX().unregisterJMX();
			}
			getLogger().removeDefaultNetworkHandler();
		} catch (Exception e) {
			if (!events.fatalError("Can't stop the agent.", e)) {
				componentFatalError(new FatalErrorEvent(this, "agent's fatal error not propagated"));
			}
			if (e instanceof ComponentCantStopException) throw ((ComponentCantStopException)e);
			throw new ComponentCantStopException("Can't stop.", e, log, this);
		} finally {
			stopMethodCalled = false;
			System.gc();
		}
	}
	
	private boolean stopMethodCalled = false;
	
	/**
	 * Called before any {@link AgentEvents#fatalError(String)} event is broadcast. Hook that allows you to implement just-before-death stuff
	 * before any of agent component actually dies.
	 */
	protected void preKillAgent() {		
	}
	
	/**
	 * Method that requests the agent to be killed - this counts as fatal error as well.
	 * <p><p>
	 * 1) switches state to {@link IAgentStateFailed}<p>
	 * 2) broadcast {@link IFatalErrorEvent}<p>
	 * 3) calls killAgent()<p>
	 * <p><p>
	 * This should be used to ultimately kill the agent in a ruthless way - components will usually perform some dirty tricks
	 * to kill themselves.
	 * <p><p>
	 * Prevents recursion.
	 * <p><p>
	 * <b>NEVER THROWS EXCEPTION</b>
	 */
	@Override
	public final synchronized void kill() {
		if (killMethodCalled) return;
		killMethodCalled = true;
		try {
			if (inState(IAgentStateFailing.class, IAgentStateFailed.class)) return; 
			try {
				if (log.isLoggable(Level.SEVERE)) log.severe("Killing agent " + getComponentId().getToken());
			} finally {
				try {
					preKillAgent();
				} finally {
					try {
						setState(new AgentStateFailing("kill() requested, sending fatal error event."));
					} finally {
						try {
							events.fatalError("agent kill() requested");
						} finally {
							try {
								setState(new AgentStateFailing("Calling killAgent()."));
							} finally {
								try {
									innerKillAgent();
								} finally {
									// CODE AFTER THIS POINT MUST BE PROPAGATED TO componentFatalError() as WELL!!!
									try {
										setState(new AgentStateFailed("Agent killed."));								
									} finally {
										if (jmx != null) {
											getJMX().unregisterJMX();
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			if (e instanceof PogamutException) {
				((PogamutException) e).logExceptionOnce(log);
			} else {
				if (log.isLoggable(Level.SEVERE)) log.severe(ExceptionToString.process(e));
			}
		} finally { 
			killMethodCalled = false;
			System.gc();
		}
	}
	
	private boolean killMethodCalled = false;
		
	/**
	 * Pauses the agent - working only if the agent is in {@link IAgentStateRunning}
	 * <p><p>
	 * 1) switches state to {@link IAgentStatePausing}
	 * 2) broadcasts {@link IPausingEvent} (transactional)<p>
	 * 3) calls pauseAgent()<p>
	 * 4) broadcasts {@link IPausedEvent} (transactional)<p>
	 * 5) switches state to {@link IAgentPaused}
	 * <p><p>
	 * Prevents recursion.
	 * 
	 * @throws ComponentCantPauseException
	 */
	@Override
	public final synchronized void pause() throws ComponentCantPauseException {
		if (pauseMethodCalled) return;
		
		if (inState(IAgentStatePaused.class)) return;
		if (notInState(IAgentStateRunning.class)) {
			throw new ComponentCantPauseException("Agent can't pause, it is not in the running state but " + getState().getFlag() + ".", log, this);
		}
		
		pauseMethodCalled = true;
		try {
			setState(new AgentStatePausing("Sending 'pausing' event."));
			events.pausingTransactional();
			setState(new AgentStatePausing("Calling pauseAgent()."));
			pauseAgent();
			setState(new AgentStatePausing("Sending 'paused' event."));
			events.pausedTransactional();
			setState(new AgentStatePaused("Agent paused."));
		} catch (Exception e) {			
			if (!events.fatalError("Can't pause the agent", e)) {
				componentFatalError(new FatalErrorEvent(this, "agent's fatal error not propagated"));
			}
			if (e instanceof ComponentCantPauseException) throw ((ComponentCantPauseException)e);
			throw new ComponentCantPauseException("Can't pause.", e, log, this);
		} finally {
			pauseMethodCalled = false;
		}
	}
	
	private boolean pauseMethodCalled = false;
	
	/**
	 * Resumes the agent - working only if the agent is in {@link IAgentStatePaused}
	 * <p><p>
	 * 1) switches state to {@link IAgentStateResuming}<p>
	 * 2) broadcasts {@link IResumingEvent} (transactional)<p>
	 * 3) calls resumeAgent()<p>
	 * 4) broadcasts {@link IResumedEvent} (transactional)<p>
	 * 5) switches state to {@link IAgentStateRunning}
	 * <p><p>
	 * Prevents recursion.
	 * 
	 * @throws ComponentCantResumeException
	 */
	@Override
	public final synchronized void resume() throws ComponentCantResumeException {
		if (resumeMethodCalled) return;
		
		if (inState(IAgentStateRunning.class)) return;
		if (notInState(IAgentStatePaused.class)) {
			throw new ComponentCantResumeException("Agent can't resume, it is not in the paused state but " + getState().getFlag() + ".", log, this);				
		}
		
		resumeMethodCalled = true;
		try {
			setState(new AgentStateResuming("resume() requested, sending 'resuming' event."));
			events.resumingTransactional();
			setState(new AgentStateResuming("Calling resumeAgent()."));
			resumeAgent();
			setState(new AgentStateResuming("Sending 'resumed' event."));
			events.resumedTransactional();
			setState(new AgentStateResumed("Agent resumed."));
		} catch (Exception e) {			
			if (!events.fatalError("Can't resume the agent.", e)) {
				componentFatalError(new FatalErrorEvent(this, "agent's fatal error not propagated"));
			}
			if (e instanceof ComponentCantResumeException) throw ((ComponentCantResumeException)e);
			throw new ComponentCantResumeException("Can't resume.", e, log, this);
		} finally {
			resumeMethodCalled = false;
		}
	}
	
	private boolean resumeMethodCalled = false;
	
	////
	//
	// NEW PUBLIC METHODS
	//
	////
	
	/**
	 * Returns support class for the JMX feature of the agent. You may use it to
	 * register new JMX components of the agent or for enabling of the whole
	 * feature.
	 * 
	 * @return
	 */
	public final AgentJMXComponents getJMX() {
		if (jmx == null) {
			synchronized(jmxMutex) {
				if (jmx == null) {
					jmx = createAgentJMX();
					addJMXComponents();
				}
			}
		}
		return jmx;
	}

    /**
     * This method is designed to wait for the agent to reach state 'awaitAgentState' (usually used with {@link IAgentStateUp}.
     * <p><p>
     * The call on this method will blocks until this instance of agent switches to the desired state.
     * Nevertheless - if the agent switches itself to the {@link IAgentStateDown} state, it returns null.
     * (if it is not the state you are awaiting for of course). If you find this unsuitable, use {@link WaitForAgentStateChange} directly.
     * 
     * @param awaitAgentState
     * 
     * @throws AgentException
     */
    public IAgentState awaitState(final Class awaitAgentState) throws AgentException {
    	IAgentState state = getState().getFlag();
    	if (awaitAgentState.isAssignableFrom(state.getClass())) return state;
		state = new WaitForFlagChange<IAgentState>(agentState, new WaitForFlagChange.IAccept<IAgentState>() {

			@Override
			public boolean accept(IAgentState flagValue) {
				return awaitAgentState.isAssignableFrom(flagValue.getClass()) || flagValue instanceof IAgentStateDown;
			}
			
		}).await();    		
   		if (awaitAgentState.isAssignableFrom(state.getClass())) return state;
    	if (state instanceof IAgentStateDown) return null;
    	throw new PogamutException("Agent is in unexpected state, not IAgentStateUp nor IAgentStateDown but " + state + ".", log, this);
    }
    
    /**
     * This method is designed to wait for the agent's initialization until till 'timeoutMillis'.
     * <p><p>
     * The call on this method will blocks until this instance of agent switches to the {@link IAgentStateUp}.
     * Nevertheless - if the agent switches itself to the {@link IAgentStateDown} state, it returns null.  If you find this unsuitable, use {@link WaitForAgentStateChange} directly.
     * <p><p>
     * The method also returns null in the case of timeout.
	 *
     * @param awaitAgentState 
     * @param timeoutMillis how long we should wait for the agent to ini
     * 
     * @throws AgentException 
     * @throws PogamutInterruptedException
     */
    public IAgentState awaitState(final Class awaitAgentState, long timeoutMillis) throws AgentException {
    	IAgentState state = getState().getFlag();
    	if (awaitAgentState.isAssignableFrom(state.getClass())) return state;
		state = new WaitForFlagChange<IAgentState>(agentState, new WaitForFlagChange.IAccept<IAgentState>() {

			@Override
			public boolean accept(IAgentState flagValue) {
				return awaitAgentState.isAssignableFrom(flagValue.getClass()) || flagValue instanceof IAgentStateDown;
			}
			
		}).await(timeoutMillis, TimeUnit.MILLISECONDS);    		
   		if (state == null) {
   			return null;
   		}
   		if (awaitAgentState.isAssignableFrom(state.getClass())) return state;
    	if (state instanceof IAgentStateDown) return null;
    	throw new PogamutException("Agent is in unexpected state, not IAgentStateUp nor IAgentStateDown but " + state + ".", log, this);
    }

    @Override
    final public Folder getIntrospection() {
    	 if(folder == null) {
             folder = createIntrospection();
         }
        return folder;
    }

    /**
     * Create introspection root object.
     * @return
     */
    protected Folder createIntrospection() {
        return new ReflectionObjectFolder(INTROSPECTION_ROOT_NAME, this);
    }
    
    ////
    //
    // PROTECTED - AGENT CONTROL METHODS
    //
    ////
    
    /**
     * Called during start() method - override to provide custom starting behavior of the agent.
     * <p><p>
     * <b>WARNING:</b> DO NOT CALL ON YOUR OWN, ALWAYS USE PUBLIC INTERFACE (start()), but that should not be needed!
     * <p><p>
	 * If you override this method <b>don't forget</b> to call
	 * <i>super.startAgent()</i> as the first method.
     */
    protected void startAgent() {
    }
    
    /**
     * Called during startPaused() method - override to provide custom starting-paused behavior of the agent.
     * <p><p>
     * <b>WARNING:</b> DO NOT CALL ON YOUR OWN, ALWAYS USE PUBLIC INTERFACE (startPaused()), but that should not be needed!
     * <p><p>
	 * If you override this method <b>don't forget</b> to call
	 * <i>super.startPausedAgent()</i> as the first method.
     */
    protected void startPausedAgent() {
    }
    
    /**
     * Called during stop() method - override to provide custom stopping behavior of the agent.
     * <p><p>
     * <b>WARNING:</b> DO NOT CALL ON YOUR OWN, ALWAYS USE PUBLIC INTERFACE (stop()).
     * <p><p>
	 * If you override this method <b>don't forget</b> to call
	 * <i>super.stopAgent()</i> as the first method.
     */
    protected void stopAgent() {    	
    }
    
    /**
     * Called during kill() method - override to provide custom ruthless stopping (killing) behavior of the agent.
     * <p><p>
     * <b>WARNING:</b> DO NOT CALL ON YOUR OWN, ALWAYS USE PUBLIC INTERFACE (kill()).
     * <p><p>
	 * If you override this method <b>don't forget</b> to call
	 * <i>super.killAgent()</i> as the first method.
     */
    protected void killAgent() {    	
    }
    
    /**
     * This method is called to do some clean-up before actual {@link AbstractAgent#killAgent()} method is called.
     */
    private void innerKillAgent() {
    	try {
        	runningComponents.clear();          	
    	} finally {
    		killAgent();
    	}
    }
    
    /**
     * Called during pause() method - override to provide custom pausing behavior of the agent.
     * <p><p>
     * <b>WARNING:</b> DO NOT CALL ON YOUR OWN, ALWAYS USE PUBLIC INTERFACE (pause()).
     * <p><p>
	 * If you override this method <b>don't forget</b> to call
	 * <i>super.pauseAgent()</i> as the first method.
     */
    protected void pauseAgent() {
    }
    
    /**
     * Called during resume() method - override to provide custom resuming behavior of the agent.
     * <p><p>
     * <b>WARNING:</b> DO NOT CALL ON YOUR OWN, ALWAYS USE PUBLIC INTERFACE (resume()).
     * <p><p>
	 * If you override this method <b>don't forget</b> to call
	 * <i>super.resumeAgent()</i> as the first method.
     */
    protected void resumeAgent() {
    }
    
    /**
     * Called whenever the {@link IComponentBus} broadcast {@link IResetEvent} to reset all agent's components as well
     * as an agent. Clean up your private data structure, get ready to be started again.
     * <p><p>
     * <b>WARNING:</b> DO NOT CALL ON YOUR OWN, CALLED FROM THE {@link AbstractAgent#resetEvent(IResetEvent)} AUTOMATICALLY.
     * <p><p>
	 * If you override this method <b>don't forget</b> to call
	 * <i>super.resetAgent()</i> as the first method.
     */
    protected void resetAgent() {
    	
    }
    
    ////
    //
    //  PROTECTED - EVENTS HANDLING METHODS
    //
    ////
    
	/**
	 * Called whenever some component that was not started before broadcasts {@link IStartedEvent}
	 * <p><p>
	 * If you override this method <b>don't forget</b> to call
	 * <i>super.componentStarted(event)</i> as the first method.
	 * 
	 * @param event
	 */
	protected synchronized void componentStarted(IStartedEvent event) {
		// ADD COMPONENT INTO AGENT'S COMPONENT
		synchronized(runningComponents) {
			if (runningComponents.containsKey(event.getSource().getComponentId())) return;
			runningComponents.put(event.getSource().getComponentId(), event.getSource());
		}
		// PROBE JMX
		if (event.getSource() instanceof IJMXEnabled) {
			synchronized(getJMX()) {
				jmx.addComponent((IJMXEnabled) event.getSource());
			}
		}
	}
	
	/**
	 * Called whenever some component that was not started before broadcasts {@link IPausedEvent}
	 * <p><p>
	 * If you override this method <b>don't forget</b> to call
	 * <i>super.componentStarted(event)</i> as the first method.
	 * 
	 * @param event
	 */
	protected synchronized void componentStarted(IPausedEvent event) {
		// ADD COMPONENT INTO AGENT'S COMPONENT
		synchronized(runningComponents) {
			if (runningComponents.containsKey(event.getSource().getComponentId())) return;
			runningComponents.put(event.getSource().getComponentId(), event.getSource());
		}
		// PROBE JMX
		if (event.getSource() instanceof IJMXEnabled) {
			synchronized(getJMX()) {
				jmx.addComponent((IJMXEnabled) event.getSource());
			}
		}
	}

	/**
	 * Called whenever some component broadcasts {@link IStoppingEvent}
	 * <p><p>
	 * If you override this method <b>don't forget</b> to call
	 * <i>super.componentStopping(event)</i> as the first method.
	 * 
	 * @param event
	 */
	protected synchronized void componentStopping(IStoppingEvent event) {
		if (stopMethodCalled) return;
		synchronized(stopDependencyToken) {
			if (stopDependencyToken.contains(event.getSource().getComponentId())) {
				if (log.isLoggable(Level.WARNING)) log.warning("Component " + event.getSource().getComponentId().getToken() + " that the agent depends on is stopping, stopping agent as well.");
				stop();
				return;
			}
		}
		synchronized(stopDependencyClass) {
			Class dependency = stopDependencyClass.containsClass(event.getSource().getClass());
			if (dependency != null) {
				if (log.isLoggable(Level.WARNING)) log.warning("Component of class " + dependency.getSimpleName() + " (id: " + event.getSource().getComponentId().getToken() + ") that tghe agent depends on is stopping, stopping agent as well.");
				stop();
				return;
			}
		}
	}
	
	/**
	 * Called whenever component that was running broadcasts {@link IStoppedEvent}.
	 * <p><p>
	 * If you override this method <b>don't forget</b> to call
	 * <i>super.componentStopped(event)</i> as the first method.
	 * 
	 * @param event
	 */
	protected synchronized void componentStopped(IStoppedEvent event) {
		// REMOVE FROM RUNNING
		synchronized(runningComponents) {
			runningComponents.remove(event.getSource().getComponentId());
			if (runningComponents.size() == 0) {
				if (log.isLoggable(Level.WARNING)) log.warning("All agent's components has stopped. Stopping agent as well.");
				stop();
			}
		}
	}
	
	/**
	 * Called whenever some comopnent broadcasts {@link IFatalErrorEvent}.
	 * <p><p>
	 * If you override this method <b>don't forget</b> to call
	 * <i>super.fatalError(event)</i> as the first method.
	 * 
	 * @param event
	 */
	protected void componentFatalError(IFatalErrorEvent event) {
		if (inState(IAgentStateFailing.class)) return; // prevent recursion
		try {
			setState(new AgentStateFailing(event.getMessage() + ", calling killAgent()."));
		} finally {
			try {
				preKillAgent();
			} finally {
				try {
					innerKillAgent();
				} finally {
					// CODE AFTER THIS POINT MUST BE PROPAGETED TO kill() AS WELL !!!
					try {
						setState(new AgentStateFailed(event.getMessage()));								
					} finally {
						if (jmx != null) {
							getJMX().unregisterJMX();
						}
					}				
				}
			}
		}
	}
	
	protected void resetEvent(IResetEvent event) {
		resetAgent();
	}
	
	////
	//
	// PROTECTED - AGENT INNER METHODS
	// 
	////
	
	protected AgentJMXComponents createAgentJMX() {
		return new AgentJMXComponents(this);
	}
    
    protected void addDependency(IComponent component) {
    	NullCheck.check(component, "component");
    	addDependency(component.getComponentId());
    }
    
    protected void addDependency(Class componentClass) {
    	NullCheck.check(componentClass, "componentClass");
    	synchronized(stopDependencyClass) {
    		stopDependencyClass.add(componentClass);
    	}
    }
    
    protected void addDependency(IToken componentId) {
    	NullCheck.check(componentId, "componentId");
    	synchronized(stopDependencyToken) {
    		stopDependencyToken.add(componentId);
    	}
    }
	
	/**
	 * Sets the state of the agent ... note that the flag is private field so
	 * you can't change it directly.
	 * 
	 * @param state
	 */
	protected void setState(AgentState state) {		
		synchronized(agentState) {
			if (log.isLoggable(Level.FINER)) log.finer("Agent state is going to be switched to: " + state.toString());
			this.agentState.setFlag(state);
			if (log.isLoggable(Level.INFO)) log.info("Agent state switched to: " + state.toString());
		}		
	}
	
    /**
	 * Called when AgentJMX (field jmx) is instantiated to populate it with
	 * agent's JMX enabled components.
	 * <p><p>
	 * Currently two components are added:
	 * <ol>
	 * <li>agent's logger</li>
	 * <li>agent's introspection</li>
	 * </ol>
	 * <p><p>
	 * If you override this method <b>don't forget</b> to call
	 * <i>super.addJMXComponents()</i> as the first method.
	 * <p><p>
	 * Note that you don't need to override this method to introduce new jmx components if and only if:<p>
	 * 1) you do not need the component before the agent starts up<p>
	 * 2) your JMX component is also an {@link IComponent} that starts together with the agent<p>
	 * If (2) holds, the component will be added to the 'jmx' by the agent automatically and if jmx is already started
	 * it will start it as well. 
	 */
	protected void addJMXComponents() {
		jmx.addComponent(logger);
		jmx.addComponent(new FolderToIJMXEnabledAdapter(getIntrospection()));
	}
	
	@Override
	public String toString() {
		if (this == null) return "AbstractAgent[constructing]";
		else return getClass().getSimpleName() + "[" + getName() + "]";
	}
    
}
