package cz.cuni.amis.pogamut.base.component.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.ISharedComponent;
import cz.cuni.amis.pogamut.base.component.bus.event.IFatalErrorEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.FatalErrorEvent;
import cz.cuni.amis.pogamut.base.component.exception.ComponentCantPauseException;
import cz.cuni.amis.pogamut.base.component.exception.ComponentCantResumeException;
import cz.cuni.amis.pogamut.base.component.exception.ComponentCantStartException;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.ILifecycleBus;
import cz.cuni.amis.utils.ExceptionToString;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.FlagListener;
import cz.cuni.amis.utils.token.IToken;

/**
 * Shared component controller is meant to provide management of lifecycle methods of the {@link ISharedComponent} 
 * while providing methods that has to be implemented by any {@link ISharedComponent}.
 * <p><p>
 * You can't actualy create {@link ISharedComponent} by extending this class, you have to create your own class implementing
 * {@link ISharedComponent} interface and instantiate this class (wrap it) for yourself (and delegete all calls to methods
 * from {@link ISharedComponent} to this class).
 * <p><p>
 * This decision has been deliberately made in order to allow anybody to create their shared components out of blue and add 
 * lifecycle-management later on using this object.
 * <p><p>
 * To get the impression of what this class is doing, first read javadoc for {@link ComponentController}. Read? Good! So this
 * object does something similar as {@link ComponentController} but not for simple {@link IComponent} but for {@link ISharedComponent} which
 * means that we're dealing with the component that is shared by multiple agent instances, i.e., component that will be active on
 * multiple {@link ILifecycleBus}es. Which means that we must auto-start/stop controlled component differently than {@link ComponentController} is
 * doing.
 * <p><p>
 * The component should be started when any agent using it is starting and should be stop when the last agent that is using it is stopping. Similar 
 * things applies for pause/resume. Simple to say, harder to perform. Nevertheless, the implementation is here ;-)
 * 
 * @author Jimmy
 */
public class SharedComponentController<COMPONENT extends ISharedComponent> extends AbstractComponentControllerBase<COMPONENT> implements ISharedComponentController<COMPONENT> {

	protected class ComponentStateListener implements FlagListener<ComponentState> {

		protected IAgentId agentId;

		public ComponentStateListener(IAgentId agentId) {
			this.agentId = agentId;
		}
		
		public IAgentId getAgentId() {
			return agentId;
		}

		@Override
		public void flagChanged(ComponentState changedValue) {
			stateChanged(agentId, changedValue);
		}
		
	}
	
	/**
	 * Used as {@link IComponentControlHelper} that is passed to every {@link ILifecycleBus#addLifecycleManagement(IComponent, IComponentControlHelper, ComponentDependencies)}
	 * sensing decisions of ordinary {@link ComponentController} that signalizes when the component should be started for a given bus.
	 * <p><p>
	 * All lifecycle methods are just recalling {@link SharedComponentControl} signalizeXXX() methods where we truly decide whether the component
	 * should start/stop, etc.
	 * 
	 * @author Jimmy
	 */
	protected class LocalController {

		protected ComponentStateListener listener;
		
		protected IComponentController controller;
		
		public LocalController(IAgentId agentId, IComponentController controller) {
			NullCheck.check(agentId, "agentId");
			this.listener = new ComponentStateListener(agentId);
			this.controller = controller;
			NullCheck.check(this.controller, "controller");
			this.controller.getState().addListener(listener);
		}
		
		public IAgentId getAgentId() {
			return listener.getAgentId();
		}

		public void destroy() {
			this.controller.getState().removeListener(listener);
		}

	}
	
	protected class ControlHelper implements IComponentControlHelper {

		protected IAgentId agentId;
		
		public ControlHelper(IAgentId agentId) {
			this.agentId = agentId;
		}
		
		@Override
		public void preStart() throws PogamutException {
			localPreStart(agentId);
		}
		
		@Override
		public void start() throws PogamutException {
			localStart(agentId);
		}

		@Override
		public void preStartPaused() throws PogamutException {
			localPreStartPaused(agentId);
		}

		@Override
		public void startPaused() throws PogamutException {
			localStartPaused(agentId);
		}

		@Override
		public void prePause() throws PogamutException {
			localPrePause(agentId);			
		}
		
		@Override
		public void pause() throws PogamutException {
			localPause(agentId);
		}

		@Override
		public void preResume() throws PogamutException {
			localPreResume(agentId);
		}
		
		@Override
		public void resume() throws PogamutException {
			localResume(agentId);
		}
		
		@Override
		public void preStop() throws PogamutException {
			localPreStop(agentId);
		}

		@Override
		public void stop() throws PogamutException {
			localStop(agentId);
		}
		
		@Override
		public void kill() {
			localKill(agentId);
		}

		@Override
		public void reset() throws PogamutException {
			localReset(agentId);
		}
		
	}
	
	/**
	 * Mutex that is synchronizing access to internal data structures of the controller.
	 */
	protected Object ctrlMutex = new Object();
	
	/**
	 * Control helpers that are used to signalizes starting/stopping of the controlled {@link AbstractComponentControllerBase#component} inside
	 * bus of respective agents. 
	 */
	protected Map<IAgentId, ControlHelper> localControlHelpers = new HashMap<IAgentId, ControlHelper>();
	
	/**
	 * Controllers that are used to store {@link IComponentController} provided by respective {@link ILifecycleBus#addLifecycleManagement(IComponent, IComponentControlHelper, ComponentDependencies)}
	 * of agents using the component.
	 */
	protected Map<IAgentId, LocalController> localControllers = new HashMap<IAgentId, LocalController>();
	
	/**
	 * This map holds the currently desired {@link SharedComponentController#component} state inside the agent's bus.
	 */
	protected Map<IAgentId, ComponentState> componentStates = new HashMap<IAgentId, ComponentState>();
	
	
	/**
	 * Map tracking count of states of dependencies.
	 */
	protected Map<ComponentState, Integer> componentStateCount = new HashMap<ComponentState, Integer>();

	/**
	 * Last fatal error sensed.
	 */
	protected IFatalErrorEvent fatalError;
	
	/**
	 * Default constructor. Nothing fancy...
	 * 
	 * @param component
	 * @param componentControl
	 * @param log
	 */
	public SharedComponentController(COMPONENT component, ISharedComponentControlHelper componentControl, Logger log) {
		super(component, componentControl, log);		
		
		// set initiali component state count
		for(ComponentState state : ComponentState.values()) {
			componentStateCount.put(state, 0);
		}
	}
	
	//
	//
	// PUBLIC INTERFACE - ISharedComponentController
	//
	//
	
	@Override
	public ISharedComponentControlHelper getComponentControl() {
		return (ISharedComponentControlHelper) super.getComponentControl();
	}
	
	@Override
	public void addComponentBus(IAgentId agentId, ILifecycleBus bus, ComponentDependencies dependencies) {
		synchronized(ctrlMutex) {
			if (isUsedBy(agentId)) {
				throw new PogamutException(id(component) + "[" + getState().getFlag() + "] is already a member of the " + agentId.getToken() + " lifecycle bus!", this);
			}			
			ControlHelper helper = new ControlHelper(agentId);
			localControlHelpers.put(agentId, helper);
			IComponentController<COMPONENT> controller = bus.addLifecycleManagement(component, helper, dependencies);
			LocalController localController = new LocalController(agentId, controller);
			localControllers.put(agentId, localController);
			// alter the state count
			setState(agentId, controller.getState().getFlag());
		}
	}
	
	@Override
	public void removeComponentBus(IAgentId agentId, ILifecycleBus bus) {
		synchronized(ctrlMutex) {
			if (!isUsedBy(agentId)) {
				// the component was not registered for this agent
				if (log.isLoggable(Level.WARNING)) log.warning(id(component) + "[" + getState().getFlag() + "] is not registered for agent " + agentId.getToken() + ", can't perform removeComponentBus().");
				return;
			}
			
			// remove the lifecycle management for the component of respective bus
			bus.removeLifecycleManagement(component);
			
			// drop the control helper
			localControllers.remove(agentId);
			
			// unregister from the bus
			bus.remove(component);
			bus.remove(this);
			
			// decrease the state count
			setState(agentId, null);
		}
	}
	
	@Override
	public boolean isUsedBy(IAgentId agentId) {
		return localControllers.containsKey(agentId);
	}
	
	@Override
	public boolean isDependent(IAgentId agentId, IToken componentId) {
		LocalController helper = localControllers.get(agentId);
		if (helper == null) {
			if (log.isLoggable(Level.WARNING)) log.warning(id(component) + "[" + getState().getFlag() + "] is not registered for agent " + agentId.getToken() + ", can't perform isDependent(" + componentId.getToken() + ").");
			return false;
		}
		return helper.controller.isDependent(componentId);
	}

	@Override
	public boolean isDependent(IAgentId agentId, IComponent component) {
		LocalController helper = localControllers.get(agentId);
		if (helper == null) {
			if (log.isLoggable(Level.WARNING)) log.warning(id(component) + "[" + getState().getFlag() + "] is not registered for agent " + agentId.getToken() + ", can't perform isDependent(" + id(component) + ").");
			return false;
		}
		return helper.controller.isDependent(component);
	}

	@Override
	public void fatalError(String message) {
		kill(null, message, null);
	}

	@Override
	public void fatalError(String message, Throwable e) {
		kill(null, message, e);
	}

	@Override
	public IFatalErrorEvent getFatalError() {
		return fatalError;
	}
	
	@Override
	public void manualStart(String reason) {
		synchronized(ctrlMutex) {
			if (notInState(ComponentState.INSTANTIATED, ComponentState.RESETED, ComponentState.STOPPED)) {
				throw new ComponentCantStartException(id(component) + " in state " + getState().getFlag() + ", can't manual start (" + reason + ")!", this);
			}
			start();
		}
	}

	@Override
	public void manualStartPaused(String reason) {
		synchronized(ctrlMutex) {
			if (notInState(ComponentState.INSTANTIATED, ComponentState.RESETED, ComponentState.STOPPED)) {
				throw new ComponentCantStartException(id(component) + "[" + getState().getFlag() + "], can't manual start paused (reason: " + reason + ")!", this);
			}
			startPaused();
		}
	}

	@Override
	public void manualPause(String reason) {
		synchronized(ctrlMutex) {
			if (notInState(ComponentState.RUNNING)) {
				throw new ComponentCantStartException(id(component) + "[" + getState().getFlag() + "], can't manual pause (reason: " + reason + ")!", this);
			}
			startPaused();
		}
	}

	@Override
	public void manualResume(String reason) {
		synchronized(ctrlMutex) {
			if (notInState(ComponentState.PAUSED)) {
				throw new ComponentCantStartException(id(component) + "[" + getState().getFlag() + "], can't manual reason (reason: " + reason + ")!", this);
			}
			resume();
		}
	}

	@Override
	public void manualStop(String reason) {
		synchronized(ctrlMutex) {
			if (notInState(ComponentState.PAUSED, ComponentState.RUNNING)) {
				throw new ComponentCantStartException(id(component) + "[" + getState().getFlag() + "], can't manual reason (reason: " + reason + ")!", this);
			}
			stop();
		}
	}

	@Override
	public void manualKill(String reason) {
		synchronized(ctrlMutex) {
			if (inState(ComponentState.KILLING, ComponentState.KILLED)) {
				return;
			}
			kill(null, reason, null);
		}
	}
	
	//
	//
	// UTILITY METHODS
	//
	//
	
	/**
	 * Return how many components are in one of 'states'
	 * 
	 * @param states
	 * @return number of components in one of 'states'
	 */
	public int getStateCount(ComponentState... states) {
		if (states == null) return 0;
		if (states.length == 0) return 0;
		if (states.length == 1) return componentStateCount.get(states[0]);
		int count = 0;
		for (ComponentState state : states) {
			count += this.componentStateCount.get(state);
		}
		return count;
	}
	
	/**
	 * Checks sanity of the 'state' count 'newCount'
	 * @param newCount
	 * @param state
	 */
	protected void checkStateCount(int newCount, ComponentState state) {
		if (newCount < 0) throw new PogamutException(id(component) + "[" + getState().getFlag() + "] attempt to change state count of " + state + " to " + newCount + ", invalid.", this);
		if (newCount > componentStates.size()) throw new PogamutException(id(component) + "[" + getState().getFlag() + "] attempt to change state count of " + state + " to " + newCount + " > " + componentStates.size() + " = all-component-states state count, invalid.", this);
	}
	
	/**
	 * Changes the count (by 'change') of the 'state', DOES NOT TRIGGER {@link SharedComponentController#componentStateCountChanged()}.
	 * @param state
	 */
	protected int alterStateCount(ComponentState state, int change) {
		Integer newCount = componentStateCount.get(state) + change;
		checkStateCount(newCount, state);
		componentStateCount.put(state, newCount);
		return newCount;
	}
	
	/**
	 * Increases (+1) count of the 'state', DOES NOT TRIGGER {@link SharedComponentController#componentStateCountChanged()}.
	 * @param state
	 */
	protected int increaseStateCount(ComponentState state) {
		return alterStateCount(state, 1);
	}
	
	
	/**
	 * Increases count (+n) of the 'state', DOES NOT TRIGGER {@link SharedComponentController#componentStateCountChanged()}.
	 * @param state
	 */
	protected int increaseStateCount(ComponentState state, int n) {
		return alterStateCount(state, n);
	}

	/**
	 * Decreases (-1) count of the 'state', DOES NOT TRIGGER {@link SharedComponentController#componentStateCountChanged()}.
	 * @param state
	 */
	protected int decreaseStateCount(ComponentState state) {
		return alterStateCount(state, -1);
	}
	
	/**
	 * Decreases (-n) count of the 'state', DOES NOT TRIGGER {@link SharedComponentController#componentStateCountChanged()}.
	 * @param state
	 */
	protected int decreaseStateCount(ComponentState state, int n) {
		return alterStateCount(state, -n);
	}
	
	//
	//
	// LEVEL 1.A - SIGNALS FROM ComponentControllers(s)
	//   -- SYNCHRONIZED METHODS
	//
	
	protected void localPreStart(IAgentId agentId) {		
		synchronized(ctrlMutex) {
			if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] processing localPreStart(" + agentId + ")");
			switch(getState().getFlag()) {
			case INSTANTIATED:
			case STOPPED:
			case RESETED:
				if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] must start!");
				start();
				break;			
			case PAUSED:
				if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] must resume!");
				resume();
				break;
			case STARTING:
			case STARTING_PAUSED:
			case PAUSING:
			case RESUMING:
			case STOPPING:
			case KILLING:
			case RESETTING:
				// can't happen ...
				throw new ComponentCantStartException(id(component) + "[" + getState().getFlag() + "] STATE INVALID AT THIS POINT!", this);				
			case RUNNING:
				// nothing to do
				break;				
			case KILLED:
				if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] must start! But is in killed state, must be reset()ed first in order to start!");
				reset(); // first reset the component
				if (notInState(ComponentState.RESETED)) throw new PogamutException(id(component) + "[" + getState().getFlag() + "] reset has failed, could not resolve the state change.", this);
				start(); // start it
				break;
			}
			if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] calling componentControlHelper.localPreStart(" + agentId + ")");
			getComponentControl().localPreStart(agentId);
		}
	}

	protected void localStart(IAgentId agentId) {		
		synchronized(ctrlMutex) {
			if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] processing localStart(" + agentId + ")");
			switch(getState().getFlag()) {
			case INSTANTIATED:
			case STOPPED:
			case RESETED:
				if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] must start!");
				start();
				break;			
			case PAUSED:
				if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] must resume!");
				resume();
				break;
			case RUNNING:
				// nothing to do
				break;				
			case STARTING:
			case STARTING_PAUSED:
			case PAUSING:
			case RESUMING:
			case STOPPING:
			case KILLING:
			case RESETTING:
				// can't happen ...
				throw new ComponentCantStartException(id(component) + "[" + getState().getFlag() + "] STATE INVALID AT THIS POINT!", this);
				
			case KILLED:
				if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] must start! But is in killed state, must be reset()ed first in order to start!");
				reset(); // first reset the component
				if (notInState(ComponentState.RESETED)) throw new PogamutException(id(component) + "[" + getState().getFlag() + "] reset has failed, could not resolve the state change.", this);
				start(); // start it
				break;
			}
			if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] calling componentControlHelper.localStart(" + agentId + ")");
			getComponentControl().localStart(agentId);
		}
	}

	protected void localPreStartPaused(IAgentId agentId) {		
		synchronized(ctrlMutex) {
			if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] processing localPreStartPaused(" + agentId + ")");
			switch(getState().getFlag()) {
			case INSTANTIATED:
			case STOPPED:
			case RESETED:
				if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] must start-paused!");
				startPaused();
				break;			
			case RUNNING:
			case PAUSED:
				// nothing to do
				break;				
			case STARTING:
			case STARTING_PAUSED:
			case PAUSING:
			case RESUMING:
			case STOPPING:
			case KILLING:
			case RESETTING:
				// can't happen ...
				throw new ComponentCantStartException(id(component) + "[" + getState().getFlag() + "] STATE INVALID AT THIS POINT!", this);				
			case KILLED:
				if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] must start-paused! But is in killed state, must be reset()ed first in order to start-paused!");
				reset(); // first reset the component
				if (notInState(ComponentState.RESETED)) throw new PogamutException(id(component) + "[" + getState().getFlag() + "] reset has failed, could not resolve the state change.", this);
				startPaused(); // start it in paused state
				break;
			}
			if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] calling componentControlHelper.localPreStartPaused(" + agentId + ")");
			getComponentControl().localPreStartPaused(agentId);
		}	
	}

	protected void localStartPaused(IAgentId agentId) {		
		synchronized(ctrlMutex) {
			if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] processing localStartPaused(" + agentId + ")");
			switch(getState().getFlag()) {
			case INSTANTIATED:
			case STOPPED:
			case RESETED:
				if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] must start-paused!");
				startPaused();
				break;			
			case RUNNING:
			case PAUSED:
				// nothing to do
				break;				
			case STARTING:
			case STARTING_PAUSED:
			case PAUSING:
			case RESUMING:
			case STOPPING:
			case KILLING:
			case RESETTING:
				// can't happen ...
				throw new ComponentCantStartException(id(component) + "[" + getState().getFlag() + "] STATE INVALID AT THIS POINT!", this);				
			case KILLED:
				if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] must start-paused! But is in killed state, must be reset()ed first in order to start-paused!");
				reset();       // reset the component first
				if (notInState(ComponentState.RESETED)) throw new PogamutException(id(component) + "[" + getState().getFlag() + "] reset has failed, could not resolve the state change.", this);
				startPaused(); // start the component
				break;
			}
			if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] calling componentControlHelper.localStartPaused(" + agentId + ")");
			getComponentControl().localStartPaused(agentId);
		}	
	}

	protected void localPrePause(IAgentId agentId) {		
		synchronized(ctrlMutex) {
			if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] processing localPrePause(" + agentId + ")");
			if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] calling componentControlHelper.localPrePause(" + agentId + ")");
			getComponentControl().localPrePause(agentId);
			switch(getState().getFlag()) {
			case INSTANTIATED:
			case STOPPED:
			case RESETED:
				if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] must start-paused!");
				startPaused();
				break;			
			case RUNNING:
				// nothing to do ...
				// ... wait for the localPause(agentId)
				break;
			case PAUSED:
				// nothing to do
				break;				
			case STARTING:
			case STARTING_PAUSED:
			case PAUSING:
			case RESUMING:
			case STOPPING:
			case KILLING:
			case RESETTING:
				// can't happen ...
				throw new ComponentCantPauseException(id(component) + "[" + getState().getFlag() + "] STATE INVALID AT THIS POINT!", this);				
			case KILLED:
				if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] must start-paused! But is in killed state, must be reset()ed first in order to start-paused!");
				reset();       // reset the component first
				if (notInState(ComponentState.RESETED)) throw new PogamutException(id(component) + "[" + getState().getFlag() + "] reset has failed, could not resolve the state change.", this);
				startPaused(); // start the component
				break;
			}			
		}	
	}

	protected void localPause(IAgentId agentId) {		
		synchronized(ctrlMutex) {
			if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] processing localPreStart(" + agentId + ")");
			if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] calling componentControlHelper.localPause(" + agentId + ")");
			getComponentControl().localPause(agentId);
			switch(getState().getFlag()) {
			case INSTANTIATED:
			case STOPPED:
			case RESETED:
				if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] must start-paused!");
				startPaused();
				break;			
			case RUNNING:
				if (getStateCount(ComponentState.STARTING, ComponentState.RUNNING, ComponentState.RESUMING) == 0) {
					if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] there are no more starting/running/resuming local component states, must pause!");
					// there is not a single agent that would need to utilize the component
					pause();
				}
				break;
			case PAUSED:
				// nothing to do
				break;				
			case STARTING:
			case STARTING_PAUSED:
			case PAUSING:
			case RESUMING:
			case STOPPING:
			case KILLING:
			case RESETTING:
				// can't happen ...
				throw new ComponentCantPauseException(id(component) + "[" + getState().getFlag() + "] STATE INVALID AT THIS POINT!", this);				
			case KILLED:
				if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] must start-paused! But is in killed state, must be reset()ed first in order to start-paused!");
				reset();       // reset the component first
				if (notInState(ComponentState.RESETED)) throw new PogamutException(id(component) + "[" + getState().getFlag() + "] reset has failed, could not resolve the state change.", this);
				startPaused(); // start the component
				break;
			}			
		}	
	}

	protected void localPreResume(IAgentId agentId) {		
		synchronized(ctrlMutex) {
			if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] processing localPreResume(" + agentId + ")");
			switch(getState().getFlag()) {
			case INSTANTIATED:
			case STOPPED:
			case RESETED:
				if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] must start!");
				start();
				break;			
			case RUNNING:
				// nothing to do
				break;
			case PAUSED:
				if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] must resume!");
				resume();
				break;				
			case STARTING:
			case STARTING_PAUSED:
			case PAUSING:
			case RESUMING:
			case STOPPING:
			case KILLING:
			case RESETTING:
				// can't happen ...
				throw new ComponentCantResumeException(id(component) + "[" + getState().getFlag() + "] STATE INVALID AT THIS POINT!", this);				
			case KILLED:
				if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] must star! But is in killed state, must be reset()ed first in order to start!");
				reset();       // reset the component first
				if (notInState(ComponentState.RESETED)) throw new PogamutException(id(component) + "[" + getState().getFlag() + "] reset has failed, could not resolve the state change.", this);
				start(); // start the component
				break;
			}
			if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] calling componentControlHelper.localPreResume(" + agentId + ")");
			getComponentControl().localPreResume(agentId);
		}	
	}

	protected void localResume(IAgentId agentId) {		
		synchronized(ctrlMutex) {
			if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] processing localResume(" + agentId + ")");
			switch(getState().getFlag()) {
			case INSTANTIATED:
			case STOPPED:
			case RESETED:
				if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] must start!");
				start();
				break;			
			case RUNNING:
				// nothing to do
				break;
			case PAUSED:
				if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] must resume!");
				resume();
				break;				
			case STARTING:
			case STARTING_PAUSED:
			case PAUSING:
			case RESUMING:
			case STOPPING:
			case KILLING:
			case RESETTING:
				// can't happen ...
				throw new ComponentCantResumeException(id(component) + "[" + getState().getFlag() + "] STATE INVALID AT THIS POINT!", this);				
			case KILLED:
				if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] must start! But is in killed state, must be reset()ed first in order to evaluate the state!");
				reset();       // reset the component first
				if (notInState(ComponentState.RESETED)) throw new PogamutException(id(component) + "[" + getState().getFlag() + "] reset has failed, could not resolve the state change.", this);
				start(); // start the component
				break;
			}
			if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] calling componentControlHelper.localResume(" + agentId + ")");
			getComponentControl().localResume(agentId);
		}	
	}

	protected void localPreStop(IAgentId agentId) {		
		synchronized(ctrlMutex) {
			if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] processing localPreStop(" + agentId + ")");
			if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] calling componentControlHelper.localPreStop(" + agentId + ")");
			getComponentControl().localPreStop(agentId);
			switch(getState().getFlag()) {
			case INSTANTIATED:
			case STOPPED:
			case RESETED:
				// nothing to do
				break;			
			case RUNNING:
			case PAUSED:
				if (getStateCount(ComponentState.STARTING, ComponentState.STARTING_PAUSED, ComponentState.RUNNING, ComponentState.PAUSING, ComponentState.PAUSED, ComponentState.RESUMING, ComponentState.STOPPING) == 0) {
					if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] there are no more running/paused/...  local component states, must stop!");
					// there is not a single agent that would need to utilize the component
					stop();
				}
				break;				
			case STARTING:
			case STARTING_PAUSED:
			case PAUSING:
			case RESUMING:
			case STOPPING:
			case KILLING:
			case RESETTING:
				// can't happen ...
				throw new ComponentCantStartException(id(component) + "[" + getState().getFlag() + "] STATE INVALID AT THIS POINT!", this);				
			case KILLED:
				// nothing to do
				break;
			}			
		}	
	}

	protected void localStop(IAgentId agentId) {		
		synchronized(ctrlMutex) {
			if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] processing localStop(" + agentId + ")");
			if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] calling componentControlHelper.localPreStop(" + agentId + ")");
			getComponentControl().localStop(agentId);
			switch(getState().getFlag()) {
			case INSTANTIATED:
			case STOPPED:
			case RESETED:
				// nothing to do
				break;			
			case RUNNING:
			case PAUSED:
				if (getStateCount(ComponentState.STARTING, ComponentState.STARTING_PAUSED, ComponentState.RUNNING, ComponentState.PAUSING, ComponentState.PAUSED, ComponentState.RESUMING, ComponentState.STOPPING) == 0) {
					if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] there are no more running/paused/...  local component states, must stop!");
					// there is not a single agent that would need to utilize the component
					stop();
				}
				break;				
			case STARTING:
			case STARTING_PAUSED:
			case PAUSING:
			case RESUMING:
			case STOPPING:
			case KILLING:
			case RESETTING:
				// can't happen ...
				throw new ComponentCantStartException(id(component) + "[" + getState().getFlag() + "] STATE INVALID AT THIS POINT!", this);				
			case KILLED:
				// nothing to do
				break;
			}
		}	
	}

	protected void localKill(IAgentId agentId) {		
		synchronized(ctrlMutex) {
			if (log != null && log.isLoggable(Level.WARNING)) log.warning(id(component) + "[" + getState().getFlag() + "] processing localKill(" + agentId + ")");
			if (log != null && log.isLoggable(Level.WARNING)) log.warning(id(component) + "[" + getState().getFlag() + "] calling componentControlHelper.localKill(" + agentId + ")");
			getComponentControl().localKill(agentId);
			switch(getState().getFlag()) {
			case KILLING:
			case KILLED:
				// nothing to do move along
				break;
			default:
				if (log != null && log.isLoggable(Level.WARNING)) log.warning(id(component) + "[" + getState().getFlag() + "] fatal error has happened inside agent " + agentId + ", killing the component!");
				// hups, fatal error has happened in agentId
				kill(agentId, null, null);
				break;			
			}
		}	
	}

	protected void localReset(IAgentId agentId) {		
		synchronized(ctrlMutex) {
			if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] processing localReset(" + agentId + ")");
			if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] calling componentControlHelper.localReset(" + agentId + ")");
			if (inState(ComponentState.KILLED)) {
				reset();
				if (notInState(ComponentState.RESETED)) throw new PogamutException(id(component) + "[" + getState().getFlag() + "] reset has failed, could not resolve the state change.", this);
			}
			getComponentControl().localReset(agentId);
			switch(getState().getFlag()) {
			case RESETED:
				// expected, nothing to do...
				break;
			case KILLING:
				// can't happen ...
				throw new ComponentCantStartException(id(component) + "[" + getState().getFlag() + "] STATE INVALID AT THIS POINT!", this);
			default:
			case INSTANTIATED:
			case STOPPED:
			case RUNNING:
			case PAUSED:
			case STARTING:
			case STARTING_PAUSED:
			case PAUSING:
			case RESUMING:
			case STOPPING:		
			case RESETTING:
				// can't happen ...
				throw new ComponentCantStartException(id(component) + "[" + getState().getFlag() + "] component was not killed before, state is " + getState().getFlag() + ", INVALID!", this);
			}
		}	
	}
	
	//
	//
	// LEVEL 1.B - STATE CHANGES FROM ComponentController(s)
	//   -- SYNCHRONIZED METHODS
	//

	/**
	 * Signal that the component in the bus of agent identified by 'agentId' has changed into 'changedValue'.
	 */
	protected void stateChanged(IAgentId agentId, ComponentState changedValue) {
		synchronized(ctrlMutex) {
			// POSSIBLY GOING TO LEVEL 2
			setState(agentId, changedValue);
		}
	}
	
	/**
	 * Changes the state of the component for given agentId, triggers {@link SharedComponentController#componentStateCountChanged()}.
	 * 
	 * @param agentId
	 * @param newState may be null (== agentId has been deleted)
	 */
	protected void setState(IAgentId agentId, ComponentState newState) {
		synchronized(ctrlMutex) {
			// POSSIBLY GOING TO LEVEL 2
			setState(agentId, componentStates.get(agentId), newState);
		}
	}
	
	/**
	 * Changes the state of the component for given agentId, triggers {@link SharedComponentController#componentStateCountChanged()}.
	 * 
	 * @param agentId
	 * @param oldState may be null (== there was no previous state stored), MUST BE CORRECT!
	 * @param newState may be null (== agentId has been deleted)
	 */
	protected void setState(IAgentId agentId, ComponentState oldState, ComponentState newState) {
		synchronized(ctrlMutex) {
			if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] got local state change event: " + agentId + " " + oldState + " -> " + newState);
			if (newState == null) {
				oldState = componentStates.remove(agentId);
			} else {
				oldState = componentStates.put(agentId, newState);
			}
			if (oldState == newState) return;
			if (oldState == null) {
				increaseStateCount(newState);
			} else {
				decreaseStateCount(oldState);
				if (newState != null) {
					increaseStateCount(newState);
				}
			}
			// GOING TO LEVEL 2
			componentStateCountChanged(agentId, oldState, newState);
			if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] /PROCESSED/ got local state change event: " + agentId + " " + oldState + " -> " + newState);
		}
	}


	//
	//
	// LEVEL 2 - CHANGES TO THE COMPONENT STATE COUNTS
	//   -- UNSYNCHRONIZED METHODS
	//
	
	protected void componentStateCountChanged(IAgentId origin, ComponentState oldState, ComponentState newState) {
		if (oldState == null) {
			// component has been registered to new agent
			newAgentIsUsingTheComponent(origin, newState);
		} else
		if (newState == null) {
			// component has been removed from the agent
			agentStoppedUsingTheComponent(origin, oldState);
		} else {
			componentStateChanged(origin, oldState, newState);
		}
	}
	
	protected void newAgentIsUsingTheComponent(IAgentId agentId, ComponentState state) {
		if (log != null && log.isLoggable(Level.FINE)) log.fine(id(component) + "[" + getState().getFlag() + "] has started to be used by " + agentId);
		// check whether the component should not be started / resumed
		switch(getState().getFlag()) {
		case INSTANTIATED:
		case STOPPED:
		case RESETED:
			if (getStateCount(ComponentState.STARTING, ComponentState.RUNNING, ComponentState.RESUMING) > 0) {
				if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] number of starting/running/resuming of local component states is greater than 0, must start!");
				// there are agent wishing to use the component
				start();
			} else 
			if (getStateCount(ComponentState.STARTING_PAUSED, ComponentState.PAUSING, ComponentState.PAUSED) > 0) {
				if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] number of starting_paused/pausing/paused of local component states is greater than 0, must start paused!");
				// there are agent wishing to use the component but currently are paused
				startPaused();
			}	
			break;

		case RUNNING:
			// nothing to do
			break;
			
		case PAUSED:
			if (getStateCount(ComponentState.STARTING, ComponentState.RUNNING, ComponentState.RESUMING) > 0) {
				if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] number of starting/resuming/running of local component states is greater than 0, must resume!");
				// there are agent wishing to use the component
				resume();
			}
			break;
			
		case STARTING:
		case STARTING_PAUSED:
		case RESUMING:
		case PAUSING:
		case STOPPING:		
		case RESETTING:
			// can't happen ...
			throw new ComponentCantStartException(id(component) + "[" + getState().getFlag() + "] STATE INVALID AT THIS POINT!", this);
			
		case KILLING:
			// can't happen ...
			throw new ComponentCantStartException(id(component) + "[" + getState().getFlag() + "] STATE INVALID AT THIS POINT!", this);
			
		case KILLED:
			if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] must star! But is in killed state, must be reset()ed first in order to start!");
			reset();
			if (notInState(ComponentState.RESETED)) throw new PogamutException(id(component) + "[" + getState().getFlag() + "] reset has failed, could not resolve the state change.", this);
			newAgentIsUsingTheComponent(agentId, state); // evaluate the state again
			break;
		}
	}
	
	protected void agentStoppedUsingTheComponent(IAgentId agentId, ComponentState oldState) {
		// check whether the component should not be stopped / paused
		switch(getState().getFlag()) {
		case INSTANTIATED:
		case STOPPED:
		case RESETED:
			// nothing to do	
			break;

		case RUNNING:
			if (getStateCount(ComponentState.STARTING, ComponentState.RUNNING, ComponentState.RESUMING) == 0) {
				// there is nobody that wants to activelly use the agent
				if (getStateCount(ComponentState.STARTING_PAUSED, ComponentState.PAUSED) > 0) {
					if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] there are only starting-paused/paused component, must pause!");
					// there are agent wishing to use the component but are currently paused
					pause();
				} else
				if (getStateCount(ComponentState.PAUSING) == 0) {
					// nobody is actively using the component nor is paused
					if (getStateCount(ComponentState.STOPPING) == 0) {
						if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] there are no running/paused component, must stop!");
						// and is event not stopping
						stop();
					}
				}				 
			} // else, there are still somebody who wants to use the component, keep it running!
			break;
			
		case PAUSED:
			if (getStateCount(ComponentState.STARTING, ComponentState.RUNNING, ComponentState.RESUMING) == 0) {
				// there is nobody that wants to activelly use the agent
				if (getStateCount(ComponentState.STARTING_PAUSED, ComponentState.PAUSING, ComponentState.PAUSED) > 0) {
					// nothing to do
				} else {
					// nobody is actively using the component nor is paused
					if (getStateCount(ComponentState.STOPPING) == 0) {
						if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] there are no running/paused component, must stop!");
						// and is event not stopping
						stop();
					}
				}				 
			} else {
				// can't happen ...
				throw new ComponentCantStartException(id(component) + "[" + getState().getFlag() + "] STATE INVALID AT THIS POINT!", this);
			}
			break;
			
		case STARTING:
		case STARTING_PAUSED:
		case RESUMING:
		case PAUSING:
		case STOPPING:		
		case RESETTING:
			// can't happen ...
			throw new ComponentCantStartException(id(component) + "[" + getState().getFlag() + "] STATE INVALID AT THIS POINT!", this);
			
		case KILLING:
			// can't happen ...
			throw new ComponentCantStartException(id(component) + "[" + getState().getFlag() + "] STATE INVALID AT THIS POINT!", this);
			
		case KILLED:
			// nothing to do
			break;
		}
	}

	protected void componentStateChanged(IAgentId origin, ComponentState oldState, ComponentState newState) {
		// should we be killed?
		if (newState == ComponentState.KILLED) {
			if (inState(ComponentState.KILLED)) {
				if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] local component has been switched to killed, but we are killed too!");
				return;
			}
			if (inState(ComponentState.KILLING)) {
				// the component is being currently killed ... we've got here because fatal error has been broadcast to all other buses
				return;
			}
			// we have to kill the component
			if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] local component has been switched to killed, must kill!");
			kill(origin, null, null);			
		}
		if (getStateCount(ComponentState.KILLING) > 0) {
			if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] there is a local component in KILLING state, awaiting its KILLED to tear down the whole system!");
			return;
		}
		// we should not be killed
		// check whether the component should not be stopped / paused / started / resumed
		switch(getState().getFlag()) {
		case INSTANTIATED:
		case STOPPED:
		case RESETED:
			if (getStateCount(ComponentState.RUNNING) > 0) {				
				// this means we have screwed up!
				throw new PogamutException(id(component) + "[" + getState().getFlag() + "] the component should have been already running!", this);
			}
			if (getStateCount(ComponentState.PAUSED) > 0) {
				// this means we have screwed up!
				throw new PogamutException(id(component) + "[" + getState().getFlag() + "] the component should have been already running/paused!", this);
			}
			if (getStateCount(ComponentState.STARTING, ComponentState.RESUMING) > 0) {
				if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] number of starting/resuming of local component states is greater than 0, must start!");
				// there are agent wishing to use the component
				start();
			} else 
			if (getStateCount(ComponentState.STARTING_PAUSED, ComponentState.PAUSING) > 0) {
				if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] number of starting-paused/pausing of local component states is greater than 0, must start!");
				// there are agent wishing to use the component but currently are paused
				startPaused();
			}		
			break;

		case RUNNING:			
			if (getStateCount(ComponentState.STARTING, ComponentState.RUNNING, ComponentState.RESUMING) == 0) {
				if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] number of starting/resuming/running of local component states is zero!");
				// there is nobody that wants to activelly use the agent
				if (getStateCount(ComponentState.STARTING_PAUSED, ComponentState.PAUSED) > 0) {
					if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] number of starting-paused/paused of local component states is greater than 0, there are still agents using the component!");
					// there are agent wishing to use the component but are currently paused
					if (getStateCount(ComponentState.PAUSING) == 0) {
						if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] all local components in paused state, must pause!");
						pause();
					}
				} else 
				if (getStateCount(ComponentState.PAUSING) == 0) {
					if (getStateCount(ComponentState.STOPPING) == 0) {
						if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] number of running/paused of local component states is 0, must stop!");
						// nobody is actively using the component nor is paused
						stop();
					}
				}				 
			} // else, there are still somebody who wants to use the component, keep it running!
			break;
			
		case PAUSED:
			if (getStateCount(ComponentState.STARTING, ComponentState.RUNNING, ComponentState.RESUMING) == 0) {
				// there is nobody that wants to activelly use the agent
				if (getStateCount(ComponentState.STARTING_PAUSED, ComponentState.PAUSING, ComponentState.PAUSED) > 0) {
					// nothing to do!
				} else {
					if (getStateCount(ComponentState.STOPPING) == 0) {
						if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] number of running/paused of local component states is 0, must stop!");
						// nobody is actively using the component nor is paused
						stop();
					}
				}				 
			} else {
				if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] number of starting/resuming/running of local component states is > 0, must resume!");
				resume();
			}
			break;
			
		case STARTING:
		case STARTING_PAUSED:
		case RESUMING:
		case PAUSING:
		case STOPPING:		
		case RESETTING:
			// can't happen ...
			throw new ComponentCantStartException(id(component) + "[" + getState().getFlag() + "] STATE INVALID AT THIS POINT!", this);
			
		case KILLING:
			// can't happen ...
			throw new ComponentCantStartException(id(component) + "[" + getState().getFlag() + "] STATE INVALID AT THIS POINT!", this);
			
		case KILLED:
			reset(); // reset the component
			if (notInState(ComponentState.RESETED)) throw new PogamutException(id(component) + "[" + getState().getFlag() + "] reset has failed, could not resolve the state change.", this);
			componentStateChanged(origin, oldState, newState); // reevaluate the state again
			break;
		}
	}

	
	//
	//
	// LEVEL 4 - START / STOP
	//   -- UNSYNCHRONIZED METHODS!
	//
	
	
	protected void start() {
		setState(ComponentState.STARTING);
		if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] calling preStart()");
		getComponentControl().preStart();
		if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] calling start()");
		getComponentControl().start();
		setState(ComponentState.RUNNING);
	}
	
	protected void startPaused() {
		setState(ComponentState.STARTING_PAUSED);
		if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] calling preStartPaused()");
		getComponentControl().preStartPaused();
		if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] calling startPaused()");
		getComponentControl().startPaused();
		setState(ComponentState.PAUSED);
	}
	
	protected void pause() {
		setState(ComponentState.PAUSING);
		if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] calling prePause()");
		getComponentControl().prePause();
		if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] calling pause()");
		getComponentControl().pause();
		setState(ComponentState.PAUSED);
	}
	
	protected void resume() {
		setState(ComponentState.RESUMING);
		if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] calling preResume()");
		getComponentControl().preResume();
		if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] calling resume()");
		getComponentControl().resume();
		setState(ComponentState.RUNNING);
	}
	
	protected void stop() {
		setState(ComponentState.STOPPING);
		if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] calling preStop()");
		getComponentControl().preStop();
		if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] calling stop()");
		getComponentControl().stop();
		setState(ComponentState.STOPPED);
	}
	
	boolean killing = false;
	
	/**
	 * AgentId has broadcast {@link IFatalErrorEvent}, tear down the whole system!
	 * @param agentId may be null
	 */
	protected void kill(IAgentId agentId, String message, Throwable cause) {
		if (killing) return;
		killing = true;
		try {
			try {
				setState(ComponentState.KILLING);
			} catch (Exception e) {
				if (log.isLoggable(Level.SEVERE)) log.severe(ExceptionToString.process(id(component) + "[" + getState().getFlag() + "] could not set component state to KILLING.", e));
			}
			
			String msg = null;
			try {
				msg = 	
					id(component) + "[" + getState().getFlag() + "] " + 
					(agentId == null ? 
						"General component failure."
						: 	"Agent " + agentId + " has failed, tearing down the whole team.")
					+ 
					(message != null ? "Reason: " + message : "");
				for (LocalController ctrl : localControllers.values()) {
					if (ctrl.getAgentId().equals(agentId)) continue; // do not broadcast fatal error to the bus which triggers the KILL
					try {
						ctrl.controller.fatalError(msg, cause);
					} catch (PogamutException pe) {
						pe.logExceptionOnce(log);
					} catch (Exception e) {
						if (log.isLoggable(Level.SEVERE)) log.severe(ExceptionToString.process(e));
					}
				}
			} catch (Exception e) {
				if (log.isLoggable(Level.SEVERE)) log.severe(ExceptionToString.process(id(component) + "[" + getState().getFlag() + "] failed to broadcast fatal error to all other buses.", e));
			}
			
			try {
				if (log != null && log.isLoggable(Level.FINER)) log.finer(id(component) + "[" + getState().getFlag() + "] calling kill()");
			} catch (Exception e) {
			}
			
			try {
				getComponentControl().kill();
			} catch (Exception e) {
				if (log.isLoggable(Level.SEVERE)) log.severe(ExceptionToString.process(id(component) + "[" + getState().getFlag() + "] could not kill the component.", e));
			}
			
			
			if (agentId == null) {
				fatalError = new FatalErrorEvent(this, msg, cause);
			} else {
				fatalError = localControllers.get(agentId).controller.getFatalError();
			}
		} finally {
			try {
				setState(ComponentState.KILLED);
			} catch (Exception e) {
				if (log.isLoggable(Level.SEVERE)) log.severe(ExceptionToString.process(id(component) + "[" + getState().getFlag() + "] could not set component state to KILLED.", e));
			} finally {
				killing = false;
			}
		}
		
	}

	protected void reset() {
		setState(ComponentState.RESETTING);
		getComponentControl().reset();
		setState(ComponentState.RESETED);
	}
	
}