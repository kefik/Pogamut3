package cz.cuni.amis.pogamut.base.agent;

import javax.management.MXBean;

import cz.cuni.amis.introspection.Folder;
import cz.cuni.amis.pogamut.base.agent.state.level0.IAgentState;
import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.IComponentAware;
import cz.cuni.amis.pogamut.base.component.IControllable;
import cz.cuni.amis.pogamut.base.component.bus.ComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.event.IFatalErrorEvent;
import cz.cuni.amis.pogamut.base.component.exception.ComponentCantPauseException;
import cz.cuni.amis.pogamut.base.component.exception.ComponentCantResumeException;
import cz.cuni.amis.pogamut.base.component.exception.ComponentCantStartException;
import cz.cuni.amis.pogamut.base.component.exception.ComponentCantStopException;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.utils.flag.ImmutableFlag;

/**
 * MXBean interface - serves the purpose only to JMX, you should always derive your agent from
 * at least AbstractAgent, even though it's not enforced right now it may be in the future!
 * <p><p>
 * The key component of the agent is EventBus. The agent is propagating system events like
 * "state of the agent has changed", "agent is starting", "agent is running", "agent name has changed", 
 * etc. See {@link ComponentBus} for information how the system is working - notice that the best
 * way how to see what events are propagated - attach a listener to "ISystemClass.class" into the event bus
 * and log all events that are being broadcasted.
 * 
 * @author Jimmy
 */
@MXBean
	
public interface IAgent extends IControllable, IComponent, IComponentAware {
	/**
	 * Returns human-readable agent's name.
	 * <p><p>
	 * Do not use as unique id of the agent:<p>
	 * 1) the name might change during the life of agent<p>
	 * 2) we do not ensure it's unique
	 * <p>
	 * Use getComponentId().getToken() instead!
	 * <p><p>
	 * Use getComponentId().getName().setFlag() to change the name of the agent.
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Returns agent id - contains also a human-readable name that can be changed
	 * @return
	 */
	public IAgentId getComponentId();

	/**
	 * Returns AgentLogger for the instance allowing creating new log categories
	 * or adding new handlers to them.
	 * 
	 * @return
	 */
	public IAgentLogger getLogger();
	
	/**
	 * Returns the state of the agent (whether it's running / dead / etc.).
	 * <p><p>
	 * Note that the type AgentState wraps two things: 
	 * <ul>
	 * <li>AgentStateType that describes the type of the state (init, running, paused, etc.)</li>
	 * <li>String description of the state</li>
	 * </ul>
	 *  
	 * @return
	 */
	public ImmutableFlag<IAgentState> getState();
	
	/**
     * Returns folder with introspection information. Useful for showing agent model
     * variables and parameters.
     * @return Folder with introspection info
     */
    public Folder getIntrospection();
	
	/**
	 * Attempt to launch the agent. If it does not throw an exception, agent has been successfully started, also
	 * the state of the agent state is changed into Running state.
	 * <p><p>
	 * This method is not suitable for simultaneous start of multiple agents that should start working together in the environment.
	 * (I.e., during tournaments of agents when you need to synchronize their start in the environment.) In such cases
	 * use {@link IAgent#startPaused()} and then multiple threads+barrier to execute {@link IAgent#resume()} of all agents at once.
	 * 
	 * @throws ComponentCantStartException
	 */
	@Override
	public void start() throws ComponentCantStartException;
	
	
	/**
	 * Attempt to launch the agent. If it does not throw an exception, agent has been successfully started (paused).
	 * <p><p>
	 * In contrast with {@link IAgent#start()} this method will initialize the agent inside the environment but pauses
	 * it after the start (i.e., its reasoning should not run, the action should not do any decisions).
	 * <p><p>
	 * To fully start the agent, you need to {@link IAgent#resume()} it.
	 * <p><p>
	 * It is designed to provide safe synchronization of multiple agent simulations when you need to start the reasoning
	 * of agents synchronously.
	 * 
	 * @throws ComponentCantStartException
	 */
	public void startPaused() throws ComponentCantStartException;
	
	/**
	 * This should pause the the agent. If it does not throw an exception, agent has been successfully started,
	 * also the state of the agent state is changed into Paused state.
	 * <BR><BR>
	 * If your agent can't be paused, throw OperationNotSupportedException.
	 * 
	 * @throws ComponentCantPauseException
	 */
	public void pause() throws ComponentCantPauseException;
	
	/**
	 * This should resume the logic of the agent. If it does not throw an exception, agent has been successfully resumed,
	 * also the state of the agent state is changed into Running state.
	 * <BR><BR>
	 * If your agent can't be paused therefore can't be resumed,
	 * throw OperationNotSupportedException.
	 * 
	 * @throws ComponentCantResumeException
	 */
	public void resume() throws ComponentCantResumeException;
	
	/**
	 * Attempt to stop the agent, usually meaning dropping all running flags and see whether
	 * it will stop automatically. This method may be blocking. If it does not throw the exception,
	 * the agent has been successfully stopped, also the state of the agent is changed into End state.
	 * <p><p>
	 * If the stop can not complete - it must automatically call kill() method.
	 * 
	 * @throws ComponentCantStopException
	 */
	@Override
	public void stop() throws ComponentCantStopException;
	
	/**
	 * Stops the agent (unconditionally), closing whatever connection it may have, this
	 * method must be non-blocking + interrupting all the communication, logic or whatever
	 * threads the agent may have.
	 * <p><p>
	 * After calling kill() method, the only method that may be called is getState() to examine state of the agent.
	 * <p><p>
	 * This also equals to "exception happened outside the agent" and "{@link IFatalErrorEvent} should be propagated inside
	 * the agent"
	 */
	@Override
	public void kill();

}