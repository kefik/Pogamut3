package cz.cuni.amis.pogamut.base.component.controller;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.event.IFatalErrorEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.IPausingEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.IResetEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.IResumingEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.IStartingEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.IStartingPausedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.IStoppingEvent;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.ILifecycleBus;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Provides a way to control the shared component.
 * <p><p>
 * We have purposefully created specific helper interface for the control of the {@link IComponent}. That's because
 * the designer of the component might want to hide its start/stop/kill method (e.g. they should not be accessible
 * by anyone). This is typical when using {@link ISharedComponentController} to automatically start/stop the component
 * based on its dependencies.
 * <p><p>
 * So if you want to hide these methods from the public interface of your component, than create private inner
 * class inside your component and implement how the component is starting/stopping/killing itself possibly by recalling
 * private methods of the component.
 * <p><p>
 * This helper is similar but quite different from the simpler {@link IComponentControlHelper} as it covers more lifecycle cases
 * which {@link ISharedComponent} has over simple {@link IComponent}. 
 * 
 * @author Jimmy
 */
public interface ISharedComponentControlHelper extends IComponentControlHelper {
	
	/**
	 * Called whenever starting dependencies of some (first) agent becomes satisfied.
	 * <p><p>
	 * Similar to {@link IComponentControlHelper#preStart()} (sort of a global version).
	 * <p><p>
	 * This method or {@link ISharedComponentControlHelper#preStartPaused()} method is called prior to
	 * any localXXX() methods are called. Which means that you are always informed that your component
	 * should start before it "accepts" starts from respective agents.
	 * <p><p>
	 * NOTE: this method does not have much meaning for {@link ISharedComponent} as method {@link ISharedComponentControlHelper#start()}
	 * is called right after ... nothing is taking place between these two calls.
	 * 
	 * @throws PogamutException
	 */
	@Override
	public void preStart() throws PogamutException;
	
	/**
	 * Called to start the component whenever starting dependencies of some (first) agent becomes satisfied.
	 * <p><p>
	 * Similar to {@link IComponentControlHelper#start()} (sort of a global version).
	 * <p><p>
	 * <p><p>
	 * This method or {@link ISharedComponentControlHelper#startPaused()} method is called prior to
	 * any localXXX() methods are called. Which means that you are always informed that your component
	 * should start before it "accepts" starts from respective agents.
	 * 
	 * @throws PogamutException
	 */
	@Override
	public void start() throws PogamutException;
	
	/**
	 * Called whenever starting dependencies of some (first) agent becomes satisfied, should start the component
	 * into paused state.
	 * <p><p>
	 * You may need to prepare some stuff before starting event is generated.
	 * <p><p>
	 * Similar to {@link IComponentControlHelper#preStartPaused()} (sort of a global version).
	 * <p><p>
	 * NOTE: this method does not have much meaning for {@link ISharedComponent} as method {@link ISharedComponentControlHelper#startPaused()}
	 * is called right after ... nothing is taking place between these two calls.
	 * 
	 * @throws PogamutException
	 */
	@Override
	public void preStartPaused() throws PogamutException;
	
	/**
	 * Starts the component whenever starting dependencies of some (first) agent becomes satisfied.
	 * But it assumes that the component just prepares whatever data
	 * structures it needs / make connections / handshake whatever it needs with the environment / etc.
	 * <p><p>
	 * Similar to {@link IComponentControlHelper#startPaused()} (sort of a global version).
	 * 
	 * @throws PogamutException
	 */
	@Override
	public void startPaused() throws PogamutException;
	
	/**
	 * Called whenever there is no running dependencies and the rest is going to be paused or is paused.
	 * <p><p> 
	 * Similar to {@link IComponentControlHelper#prePause()} (sort of a global version).
	 * <p><p>
	 * NOTE: this method does not have much meaning for {@link ISharedComponent} as method {@link ISharedComponentControlHelper#pause()}
	 * is called right after ... nothing is taking place between these two calls.
	 * 
	 * @throws PogamutException
	 */
	@Override
	public void prePause() throws PogamutException;
	
	/**
	 * Pauses the component. Called whenever there is no running dependencies and the rest is going to be paused or is paused.
	 * <p><p>
	 * Called whenever {@link IPausingEvent} is caught from one of the dependencies.
	 * <p><p>
	 * Similar to {@link IComponentControlHelper#pause()} (sort of a global version).
	 * 
	 * @throws PogamutException
	 */
	@Override
	public void pause() throws PogamutException;
	
	/**
	 * Called whenever some of paused dependencies is starting / is started.
	 * <p><p> 
	 * Similar to {@link IComponentControlHelper#preResume()} (sort of a global version).
	 * <p><p>
	 * NOTE: this method does not have much meaning for {@link ISharedComponent} as method {@link ISharedComponentControlHelper#resume()}
	 * is called right after ... nothing is taking place between these two calls.
	 * 
	 * @throws PogamutException
	 */
	@Override
	public void preResume() throws PogamutException;
	
	/**
	 * Resumes the component. Called whenever some of paused dependencies is starting / is started.
	 * <p><p>
	 * Similar to {@link IComponentControlHelper#resume()} (sort of a global version).
	 * 
	 * @throws PogamutException
	 */
	@Override
	public void resume() throws PogamutException;
	
	/**
	 * Called whenever there is no running dependencies and the rest is going to be stopped.
	 * <p><p> 
	 * Similar to {@link IComponentControlHelper#preStop()} (sort of a global version).
	 * 
	 * @throws PogamutException
	 */
	@Override
	public void preStop() throws PogamutException;
	
	/**
	 * Stops the component. Called whenever there is no running dependencies and the rest is going to be stopped.
	 * <p><p>
	 * Similar to {@link IComponentControlHelper#stop()} (sort of a global version).
	 * <p><p>
	 * NOTE: this method does not have much meaning for {@link ISharedComponent} as method {@link ISharedComponentControlHelper#stop()}
	 * is called right after ... nothing is taking place between these two calls.
	 * 
	 * @throws PogamutException
	 */
	@Override
	public void stop() throws PogamutException;
	
	/**
	 * Kills the component in ruthless way. It must be non-blocking method.
	 * <p><p>
	 * Called whenever {@link IFatalErrorEvent} is caught in any agent's bus.
	 * <p><p>
	 * Must not throw any exception whatsoever.
	 * <p><p>
	 * Similar to {@link IComponentControlHelper#kill()} (sort of a global version).
	 */
	@Override
	public void kill();
	
	/**
	 * Called whenever {@link IResetEvent} is caught in any of stopped bus. 
	 * It should reinitialize data structures of the
	 * component so it can be started again.
	 * <p><p>
	 * Should throw an exception in case that the component can't be reseted.
	 * <p><p>
	 * Similar to {@link IComponentControlHelper#reset()} (sort of a global version).
	 * 
	 * @throws PogamutException
	 */
	@Override
	public void reset() throws PogamutException;
	
	/**
	 * Called before the {@link IStartingEvent} of the component is broadcast into {@link ILifecycleBus} of
	 * the agent identified by 'agentId'.
	 * <p><p> 
	 * You may need to prepare some stuff before starting event is generated
	 * 
	 * @param agentId
	 * @throws PogamutException
	 */
	public void localPreStart(IAgentId agentId) throws PogamutException;
	
	/**
	 * The component is being started inside the {@link ILifecycleBus} of the agent identified by 'agentId'.
	 * It should throw exception, if it can not start for the particular agent.
	 * 
	 * @throws PogamutException
	 */
	public void localStart(IAgentId agentId) throws PogamutException;
	
	/**
	 * Called before {@link IStartingPausedEvent} of the component is broadcast into {@link ILifecycleBus} of
	 * the agent identified by 'agentId'.
	 * <p><p>
	 * You may need to prepare some stuff before starting event is generated.
	 * 
	 * @param agentId
	 * @throws PogamutException
	 */
	public void localPreStartPaused(IAgentId agentId) throws PogamutException;
	
	/**
	 * Starts the component for the agent identified by 'agentId' but it assumes that the component just prepares whatever data
	 * structures it needs / make connections / handshake whatever it needs with the environment / etc.
	 * <p><p>
	 * It should not let the agent to perform designers work (i.e., UT2004 bots should not start playing in the game).
	 * <p><p>
	 * After this call, the component should behave as it would have been paused with {@link IComponentControlHelper#pause()}.
	 * 
	 * @param agentId
	 * @throws PogamutException
	 */
	public void localStartPaused(IAgentId agentId) throws PogamutException;
	
	/**
	 * Called before the {@link IPausingEvent} of the component is broadcast into {@link ILifecycleBus} of
	 * the agent identified by 'agentId'.
	 * <p><p> 
	 * You may need to pre-clean some stuff.
	 * 
	 * @param agentId 
	 * @throws PogamutException
	 */
	public void localPrePause(IAgentId agentId) throws PogamutException;
	
	/**
	 * Pauses the component for agent identified by 'agentId'.
	 * <p><p>
	 * Called whenever {@link IPausingEvent} is caught from one of the dependencies of the given agent.
	 *  
	 * @param agentId
	 * @throws PogamutException
	 */
	public void localPause(IAgentId agentId) throws PogamutException;
	
	/**
	 * Called before the {@link IResumingEvent} of the component is broadcast into {@link ILifecycleBus} of
	 * the agent identified by 'agentId'.
	 * <p><p> 
	 * You may need to pre-clean some stuff.
	 *  
	 * @param agentId
	 * @throws PogamutException
	 */
	public void localPreResume(IAgentId agentId) throws PogamutException;
	
	/**
	 * Resumes the component for the agent identified by 'agentId'.
	 * <p><p>
	 * Called whenever {@link IPausingEvent} is caught from one of the dependencies of the given agent.
	 *  
	 * @param agentId
	 * @throws PogamutException
	 */
	public void localResume(IAgentId agentId) throws PogamutException;
	
	/**
	 * Called before the {@link IStoppingEvent} of the component is broadcast into {@link ILifecycleBus} of
	 * the agent identified by 'agentId'.
	 * <p><p> 
	 * You may need to pre-clean some stuff.
	 *  
	 * @param agentId
	 * @throws PogamutException
	 */
	public void localPreStop(IAgentId agentId) throws PogamutException;
	
	/**
	 * Stops the component for the agent identified by 'agentId'. 
	 * <p><p>
	 * It should throw an exception if the component can't be stopped for the given agent.
	 *  
	 * @param agentId
	 * @throws PogamutException
	 */
	public void localStop(IAgentId agentId) throws PogamutException;
	
	/**
	 * Kills the component for the agent identified by 'agentId'. It must be non-blocking method.
	 * <p><p>
	 * Called whenever {@link IFatalErrorEvent} is caught for a given agent.
	 * <p><p>
	 * Must not throw any exception whatsoever.
	 * 
	 * @param agentId
	 */
	public void localKill(IAgentId agentId);
	
	/**
	 * Called whenever {@link IResetEvent} is caught at the {@link ILifecycleBus} of the agent identified by 'agentId'.
	 * <p><p>
	 * It should reinitialize data structures of the
	 * component so it can be usable by the given agent again.
	 * <p><p>
	 * Should throw an exception in case that the component can't be reseted for a given agent.
	 * 
	 * @param agentId
	 * @throws PogamutException
	 */
	public void localReset(IAgentId agentId);
	
}
