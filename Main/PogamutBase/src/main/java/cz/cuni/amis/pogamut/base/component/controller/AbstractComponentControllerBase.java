package cz.cuni.amis.pogamut.base.component.controller;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.exception.ComponentKilledException;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.flag.ImmutableFlag;
import cz.cuni.amis.utils.flag.WaitForFlagChange;
import cz.cuni.amis.utils.flag.WaitForFlagChange.IAccept;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;

/**
 * Abstract class implementing some methods from {@link IComponentControllerBase}. 
 * <p><p>
 * Namely:
 * <ul>
 * <li>{@link AbstractComponentControllerBase#getComponent()}</li>
 * <li>{@link AbstractComponentControllerBase#getComponentControl()}</li>
 * <li>{@link AbstractComponentControllerBase#isRunning()}</li>
 * <li>{@link AbstractComponentControllerBase#isPaused()}</li>
 * <li>{@link AbstractComponentControllerBase#awaitState(ComponentState...)}</li>
 * <li>{@link AbstractComponentControllerBase#awaitState(long, ComponentState...)}</li>
 * <li>{@link AbstractComponentControllerBase#getState()}</li>
 * <li>{@link AbstractComponentControllerBase#inState(ComponentState...)}</li>
 * <li>{@link AbstractComponentControllerBase#notInState(ComponentState...)}</li>
 * </ul>
 * And all methods from {@link IComponent}.
 * <ul>
 * <li>{@link AbstractComponentControllerBase#getComponent()}</li>
 * <li>{@link AbstractComponentControllerBase#getComponentControl()}</li>
 * <li>{@link AbstractComponentControllerBase#isRunning()}</li>
 * <li>{@link AbstractComponentControllerBase#isPaused()}</li>
 * <li>{@link AbstractComponentControllerBase#awaitState(ComponentState...)}</li>
 * <li>{@link AbstractComponentControllerBase#awaitState(long, ComponentState...)}</li>
 * <li>{@link AbstractComponentControllerBase#getState()}</li>
 * <li>{@link AbstractComponentControllerBase#inState(ComponentState...)}</li>
 * <li>{@link AbstractComponentControllerBase#notInState(ComponentState...)}</li>
 * <li>
 * </ul>
 * 
 * <p><p>
 * Suitable for creating custom {@link IComponentController}s or {@link ISharedComponentController}s.
 * 
 * @author Jimmy
 */
public abstract class AbstractComponentControllerBase<COMPONENT extends IComponent> implements IComponentControllerBase<COMPONENT> {

	//
	// COMPONENT STATE AWAIT CLASS
	//
	
	/**
	 * Used for filtering states we're awaiting on.
	 */
	protected static class AwaitState implements IAccept<ComponentState> {
		
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
	// FIELDS
	//
	
	/**
	 * Unique (in context of one agent) id of this controller.
	 */
	protected IToken controllerId;

	/**
	 * Method providing means for direct control of the {@link AbstractComponentControllerBase#component}. This object is passed from the outside
	 * of the controller, usually created by the component itself.
	 */
	protected IComponentControlHelper control;
	
	/**
	 * Component controlled by this controller.
	 */
	protected COMPONENT component;

	/**
	 * Log used by the class, is never null.
	 */
	protected Logger log;

	/**
	 * State of the controlled component.
	 * <p><p>
	 * Use {@link ComponentController#setState(ComponentState)} to alter the value of the flag.
	 * <p><p>
	 * Should not be set manually, use {@link AbstractComponentControllerBase#setState(ComponentState)} instead.
	 */
	protected Flag<ComponentState> componentState = new Flag<ComponentState>(ComponentState.INSTANTIATED);

	/**
	 * Tells whether the controller sends events about the state of the
	 * component, i.e., whether it should automatically send starting/stopping
	 * events or not.
	 * <p><p>
	 * DEFAULT: TRUE (the controller is broadcasting events as default)
	 * <p><p>
	 * Exception: {@link IComponentControllerBase#fatalError(String)} and {@link IComponentControllerBase#fatalError(String, Throwable)}
	 * must always send fatal error!
	 */
	protected boolean broadcastingEvents = true;

	/**
	 * Initialize the controller. This constructor is auto-creating generic {@link AbstractComponentControllerBase#controllerId}
	 * (adding suffix "-controller" to the {@link IComponent#getComponentId()}.
	 * 
	 * @param component
	 * @param componentControlHelper
	 * @param log
	 */
	public AbstractComponentControllerBase(COMPONENT component, IComponentControlHelper componentControlHelper, Logger log) {
		// save arguments
		this.log = log;
		NullCheck.check(this.log, "log");
		this.component = component;		
		NullCheck.check(this.component, "component");
		this.control = componentControlHelper;
		NullCheck.check(this.control, "componentControlHelper");	
		
		// create default ID
		this.controllerId = Tokens.get(component.getComponentId().getToken() + "-controller");
	}
	
	/**
	 * Initialize controller with specific componentControllerId.
	 * 
	 * @param componentControllerId
	 * @param component
	 * @param componentControlHelper
	 * @param log
	 */
	public AbstractComponentControllerBase(IToken componentControllerId, COMPONENT component, IComponentControlHelper componentControlHelper, Logger log) {
		this(component, componentControlHelper, log);
		this.controllerId = componentControllerId;
		NullCheck.check(controllerId, "componentControllerId");
	}
	
	//
	//
	// PUBLIC INTERFACE - IComponent
	//
	//
	
	@Override
	public String toString() {
		if (this == null) return "AbstractComponentControllerBase";
		if (getComponentId() == null) return this.getClass().getSimpleName();
		return this.getClass().getSimpleName()+ "[" + getComponentId().getToken() + "]";
	}
	
	@Override
	public IToken getComponentId() {
		return controllerId;
	}
	
	public Logger getLog() {
		return log;
	}
	
	@Override
	public COMPONENT getComponent() {
		return component;
	}
	
	//
	//
	// PUBLIC INTERFACE - IComponentControllerBase
	//
	//
	
	@Override
	public boolean isBroadcastingEvents() {
		return broadcastingEvents;
	}
	
	@Override
	public void setBroadcastingEvents(boolean broadcastingEvents) {
		this.broadcastingEvents = broadcastingEvents;
	}

	@Override
	public IComponentControlHelper getComponentControl() {
		return control;
	}
	
	@Override
	public boolean isRunning() {
		return inState(
				  ComponentState.PAUSING, ComponentState.PAUSED,
				  ComponentState.RESUMING, ComponentState.RUNNING
			   );
	}
	
	@Override
	public boolean isPaused() {
		return inState(
				  ComponentState.STARTING_PAUSED, ComponentState.PAUSED, ComponentState.PAUSING, ComponentState.RESUMING
			   );
	}
	
	@Override
	public ImmutableFlag<ComponentState> getState() {
		return componentState.getImmutable();
	}
	
	@Override
	public boolean inState(ComponentState... states) {
		return ComponentState.inside(componentState.getFlag(), states);
	}
	
	@Override
	public boolean notInState(ComponentState... states) {
		return ComponentState.notInside(componentState.getFlag(), states);
	}
	
	@Override
	public ComponentState awaitState(ComponentState... states) throws ComponentKilledException {
		ComponentState resultState;
		resultState = new WaitForFlagChange<ComponentState>(componentState, new AwaitState(states)).await();
		if (ComponentState.inside(resultState, ComponentState.KILLING, ComponentState.KILLED) &&
		    !ComponentState.partOf(states, ComponentState.KILLING, ComponentState.KILLED)) {
			throw new ComponentKilledException(component, this);	
		}
		return resultState;
	}
	
	@Override
	public ComponentState awaitState(long timeoutMillis, ComponentState... states) throws ComponentKilledException {
		ComponentState resultState;
		resultState = new WaitForFlagChange<ComponentState>(componentState, new AwaitState(states)).await(timeoutMillis, TimeUnit.MILLISECONDS);
		if (ComponentState.inside(resultState, ComponentState.KILLING, ComponentState.KILLED) &&
		    !ComponentState.partOf(states, ComponentState.KILLING, ComponentState.KILLED)) {
			throw new ComponentKilledException(component, this);	
		}
		return resultState;
	}
	
	//
	//
	// UTILITY METHODS
	//
	//
	
	/**
	 * Returns component id or null.
	 * @param component
	 * @return
	 */
	protected String id(IComponent component) {
		if (component == null) return "null";
		return component.getComponentId().getToken();
	}
	
	/**
	 * Changes the {@link AbstractComponentControllerBase#componentState} to desired state.
	 * @param state
	 */
	protected void setState(ComponentState state) {
		if (state == this.componentState.getFlag()) return;
		if (log.isLoggable(Level.FINEST)) log.finest("Switching to " + state + ".");
		this.componentState.setFlag(state);
		if (log.isLoggable(Level.INFO)) log.info("In state " + state + ".");
	}
	
}
