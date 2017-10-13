/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.pogamut.base.agent.jmx.adapter;

import javax.management.MXBean;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import cz.cuni.amis.pogamut.base.agent.exceptions.AgentException;
import cz.cuni.amis.pogamut.base.agent.state.level0.IAgentState;
import cz.cuni.amis.pogamut.base.component.bus.event.IFatalErrorEvent;

/**
 * Interface for the adapter of the Agent MBean.
 * 
 * @author Ik
 */
@MXBean
public interface IAgentMBeanAdapter {
	
	/**
	 * Returns JMX object name of the MBean.
	 * @param domain jmx domain
	 * @return name under which the MBean should be exported
	 * @throws MalformedObjectNameException
	 */
	public ObjectName getObjectName(String domain) throws MalformedObjectNameException;
	
	/**
	 * Returns id of the agent - unique across the JVMs.
	 * @return
	 */
	public String getComponentId();
		
	/**
     * Returns human readable name. getDisplayName is set by agent and doesn't have
     * to be unique, while getName is machine assigned and is unique.
     */
    public String getName();

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
	public IAgentState getState();
	
	/**
	 * Attempt to launch the agent. If it does not throw an exception, agent has been successfully started, also
	 * the state of the agent state is changed into Running state.
	 * 
	 * @throws AgentException
	 */
	public void start() throws AgentException;
	
	/**
	 * This should pause the the agent. If it does not throw an exception, agent has been successfully started,
	 * also the state of the agent state is changed into Paused state.
	 * <BR><BR>
	 * If your agent can't be paused, throw OperationNotSupportedException.
	 * 
	 * @throws AgentException
	 */
	public void pause() throws AgentException;
	
	/**
	 * This should resume the logic of the agent. If it does not throw an exception, agent has been successfully resumed,
	 * also the state of the agent state is changed into Running state.
	 * <BR><BR>
	 * If your agent can't be paused therefore can't be resumed,
	 * throw OperationNotSupportedException.
	 * 
	 * @throws AgentException
	 */
	public void resume() throws AgentException;
	
	/**
	 * Attempt to stop the agent, usually meaning dropping all running flags and see whether
	 * it will stop automatically. This method may be blocking. If it does not throw the exception,
	 * the agent has been successfully stopped, also the state of the agent is changed into End state.
	 * <p><p>
	 * If the stop can not complete - it must automatically call kill() method.
	 */
	public void stop() throws AgentException;
	
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
	public void kill();

	
}
