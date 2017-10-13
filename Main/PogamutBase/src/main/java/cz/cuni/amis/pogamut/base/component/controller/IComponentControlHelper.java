package cz.cuni.amis.pogamut.base.component.controller;

import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.event.IFatalErrorEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.IPausingEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.IResetEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.IResumingEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.IStartingEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.IStartingPausedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.IStoppingEvent;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Provides a way to control the component.
 * <p><p>
 * We have purposefully created specific helper interface for the control of the {@link IComponent}. That's because
 * the designer of the component might want to hide its start/stop/kill method (e.g. they should not be accessible
 * by anyone). This is typical when using {@link IComponentController} to automatically start/stop the component
 * based on its dependencies.
 * <p><p>
 * So if you want to hide these methods from the public interface of your component, than create private inner
 * class inside your component and implement how the component is starting/stopping/killing itself possibly by recalling
 * private methods of the component. 
 * 
 * @author Jimmy
 */
public interface IComponentControlHelper {
	
	/**
	 * Called before the {@link IStartingEvent} of the component is broadcast.
	 * <p><p> 
	 * You may need to prepare some stuff before starting event is generated
	 * 
	 * @throws PogamutException
	 */
	public void preStart() throws PogamutException;
	
	/**
	 * Starts the component. It should throw exception, if it can not start.
	 * 
	 * @throws PogamutException
	 */
	public void start() throws PogamutException;
	
	/**
	 * Called before {@link IStartingPausedEvent} of the component is broadcast.
	 * <p><p>
	 * You may need to prepare some stuff before starting event is generated.
	 * 
	 * @throws PogamutException
	 */
	public void preStartPaused() throws PogamutException;
	
	/**
	 * Starts the component but it assumes that the component just prepares whatever data
	 * structures it needs / make connections / handshake whatever it needs with the environment / etc.
	 * <p><p>
	 * It should not let the agent to perform designers work (i.e., UT2004 bots should not start playing in the game).
	 * <p><p>
	 * After this call, the component should behave as it would have been paused with {@link IComponentControlHelper#pause()}.
	 * 
	 * @throws PogamutException
	 */
	public void startPaused() throws PogamutException;
	
	/**
	 * Called before the {@link IPausingEvent} of the component is broadcast.
	 * <p><p> 
	 * You may need to pre-clean some stuff.
	 * 
	 * @throws PogamutException
	 */
	public void prePause() throws PogamutException;
	
	/**
	 * Pauses the component.
	 * <p><p>
	 * Called whenever {@link IPausingEvent} is caught from one of the dependencies.
	 * 
	 * @throws PogamutException
	 */
	public void pause() throws PogamutException;
	
	/**
	 * Called before the {@link IResumingEvent} of the component is broadcast.
	 * <p><p> 
	 * You may need to pre-clean some stuff.
	 * 
	 * @throws PogamutException
	 */
	public void preResume() throws PogamutException;
	
	/**
	 * Resumes the component.
	 * <p><p>
	 * Called whenever {@link IResumingEvent} is caught from one of the dependencies.
	 * 
	 * @throws PogamutException
	 */
	public void resume() throws PogamutException;
	
	/**
	 * Called before the {@link IStoppingEvent} of the component is broadcast.
	 * <p><p> 
	 * You may need to pre-clean some stuff.
	 * 
	 * @throws PogamutException
	 */
	public void preStop() throws PogamutException;
	
	/**
	 * Stops the component.
	 * <p><p>
	 * It should throw an exception if the component can't be stopped.
	 * 
	 * @throws PogamutException
	 */
	public void stop() throws PogamutException;
	
	/**
	 * Kills the component in ruthless way. It must be non-blocking method.
	 * <p><p>
	 * Called whenever {@link IFatalErrorEvent} is caught.
	 * <p><p>
	 * Must not throw any exception whatsoever.
	 */
	public void kill();
	
	/**
	 * Called whenever {@link IResetEvent} is caught. It should reinitialize data structures of the
	 * component so it can be started again.
	 * <p><p>
	 * Should throw an exception in case that the component can't be reseted.
	 * 
	 * @throws PogamutException
	 */
	public void reset() throws PogamutException;

}