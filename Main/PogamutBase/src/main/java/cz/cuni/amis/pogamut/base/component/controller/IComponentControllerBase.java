package cz.cuni.amis.pogamut.base.component.controller;

import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.event.IFatalErrorEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.IPausedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.IPausingEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.IStoppedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.IStoppingEvent;
import cz.cuni.amis.pogamut.base.component.exception.ComponentKilledException;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;
import cz.cuni.amis.utils.flag.ImmutableFlag;
import cz.cuni.amis.utils.flag.WaitForFlagChange;

/**
 * Base interface for component controllers, be it {@link IComponentController} or {@link ISharedComponentController}.
 * <p><p>
 * The component controller base interface is meant to provide a gateway for starting/stopping/pausing/resuming the component and
 * querying the component current {@link ComponentState}.
 * <p><p>
 * Use {@link AbstractComponentControllerBase} abstraction for the implementation of custom controllers.
 * 
 * @author Jimmy
 */
public interface IComponentControllerBase<COMPONENT extends IComponent> extends IComponent {

	/**
	 * Returns last fatal error event that has triggered the system failure.
	 */
	public IFatalErrorEvent getFatalError();
	
	/**
	 * Whether the component has been started, is not stopped or killed, may be paused.
	 * @return component is running
	 */
	public boolean isRunning();
	
	/**
	 * Whether the component is paused (or is pausing/resuming).
	 * @return
	 */
	public boolean isPaused();
	
	/**
	 * Returns state of the controlled component (state of the component life-cycle).
	 * <p><p>
	 * It returns flag - therefore you may use {@link WaitForFlagChange} to synchronize on the flag changes in other threads or use
	 * awaitState() method.
	 * @return immutable flag
	 */
	public ImmutableFlag<ComponentState> getState();
	
	/**
	 * Whether the component is in one of 'states'.
	 * @param states
	 * @return
	 */
	public boolean inState(ComponentState... states);
	
	/**
	 * Whether the component is not in any of 'states'.
	 * @param states
	 * @return
	 */
	public boolean notInState(ComponentState... states);
	
	/**
	 * Waits until the component reaches one of 'states' or KILLING / KILLED state is reached.
	 * <p><p>
	 * If KILLING / KILLED state is not among 'states' then reaching of this state will throw {@link ComponentKilledException} exception.
	 * <p><p>
	 * If interrupted, {@link PogamutInterruptedException} is thrown.
	 * 
	 * @param state
	 * @return reached component state
	 * @throws ComponentKilledException
	 */
	public ComponentState awaitState(ComponentState... states);
	
	/**
	 * Waits until the component reaches one of 'states' or KILLING / KILLED state is reached.
	 * <p><p>
	 * If KILLING / KILLED state is not among 'states' then reaching of this state will throw {@link ComponentKilledException} exception.
	 * <p><p>
	 * If interrupted, {@link PogamutInterruptedException} is thrown.
	 * <p><p>
	 * If times out, null is returned.
	 * 
	 * @param millis
	 * @param state
	 * @return reached component state
	 * @throws ComponentKilledException
	 */
	public ComponentState awaitState(long timeoutMillis, ComponentState... states);
	
	/**
	 * Provides the way to manually start the component.
	 */
	public void manualStart(String reason);
	
	/**
	 * Provides the way to manually start the component into paused state.
	 */
	public void manualStartPaused(String reason);
	
	/**
	 * Provides the way to stop the component (constructor of this controller).
	 * <p><p>
	 * Note that you should not use {@link IComponentControlHelper}.stop() alone to stop your component
	 * as it won't produce {@link IStoppingEvent} and {@link IStoppedEvent}.
	 * <p><p>
	 * If you require your component to stop prematurely - call this method.
	 * 
	 * @param reason why the component is stopping
	 */
	public void manualStop(String reason);
	
	/**
	 * Provides the way to kill the component (constructor of this controller).
	 * <p><p>
	 * Note that you should not use {@link IComponentControlHelper}.kill() alone to stop your component
	 * as it won't produce {@link IFatalErrorEvent}.
	 * <p><p>
	 * If you require your component to stop prematurely - call this method.
	 * 
	 * @param reason why the component is stopping
	 */
	public void manualKill(String reason);
	
	/**
	 * Provides the way to pause the component (constructor of this controller).
	 * <p><p>
	 * Note that you should not use {@link IComponentControlHelper}.pause() alone to stop your component
	 * as it won't produce {@link IPausingEvent} and {@link IPausedEvent}.
	 * 
	 * @param reason why the component is pausing
	 */
	public void manualPause(String reason);
	
	/**
	 * Provides the way to pause the component (constructor of this controller).
	 * <p><p>
	 * Note that you should not use {@link IComponentControlHelper}.pause() alone to stop your component
	 * as it won't produce {@link IPausingEvent} and {@link IPausedEvent}.
	 * 
	 * @param reason why the component is pausing
	 */
	public void manualResume(String reason);
	
	/**
	 * Broadcasts fatal error with controlled component as source.
	 * <p><p>
	 * Sets state to KILLING, broadcasts {@link IFatalErrorEvent} and then sets the state to KILLED.
	 * <p><p> 
	 * <b>WARNING:</b> Note that the ComponentController assumes that you will kill your component
	 * yourself before or after you call this method. Therefore the components kill() method won't be called. That's because
	 * whenever fatal error occurs, the component is in undefined state and you have to decide what to do based
	 * on that fatal error. 
	 *  
	 * @param message
	 */
	public void fatalError(String message);
	
	/**
	 * Broadcasts fatal error with controlled component as source.
	 * <p><p>
	 * Sets state to KILLING, broadcasts {@link IFatalErrorEvent} and then sets the state to KILLED.
	 * <p><p> 
	 * <b>WARNING:</b> Note that the ComponentController assumes that you will kill your component
	 * yourself before or after you call this method. Therefore the components kill() method won't be called. That's because
	 * whenever fatal error occurs, the component is in undefined state and you have to decide what to do based
	 * on that fatal error.
	 * 
	 * @param message
	 * @param e
	 */
	public void fatalError(String message, Throwable e);
	
	/**
	 * Returns controlled component instance.
	 * @return controlled component
	 */
	public COMPONENT getComponent();
	
	/**
	 * Returns component control with lifecycle methods of the component controlled by this instance. I.e., methods that are
	 * directly controlling the component {@link IComponentController#getComponent()}.
	 * <p><p>
	 * IT IS DISCOURAGED TO USE METHODS OF THE {@link IComponentControlHelper} DIRECTLY! IT DEFIES THE PURPOSE OF THE CONTROLLER TOTALLY
	 * AND THE CONTROLLER WILL PROBABLY WON'T COPE WITH SUCH BEHAVIOR.
	 * <p><p>
	 * But what the hell, if it solves your problem, go ahead ;-) 
	 * 
	 * @return
	 */
	public IComponentControlHelper getComponentControl();
	
	/**
	 * Tells whether the controller sends events about the state of the component, i.e., whether it should automatically send
	 * starting/stopping events or not.
	 * <p><p>
	 * DEFAULT: TRUE (the controller is broadcasting events as default)
	 * <p><p>
	 * Exception: {@link IComponentControllerBase#fatalError(String)} and {@link IComponentControllerBase#fatalError(String, Throwable)}
	 * must always send fatal error!
	 * 
	 * @return
	 */
	public boolean isBroadcastingEvents();
	
	/**
	 * Enables (== true) / Disables (== false) sending events about the state of the component, i.e., whether it should automatically send
	 * starting/stopping events or not.
	 * <p><p>
	 * Exception: {@link IComponentControllerBase#fatalError(String)} and {@link IComponentControllerBase#fatalError(String, Throwable)}
	 * must always send fatal error!
	 * 
	 * @param broadcastEvents Enables (== true) / Disables (== false)
	 */
	public void setBroadcastingEvents(boolean broadcastEvents);
	
}
