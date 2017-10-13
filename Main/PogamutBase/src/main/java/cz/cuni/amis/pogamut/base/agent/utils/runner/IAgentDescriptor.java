package cz.cuni.amis.pogamut.base.agent.utils.runner;

import cz.cuni.amis.pogamut.base.agent.params.IAgentParameters;
import cz.cuni.amis.pogamut.base.agent.utils.runner.impl.AgentDescriptor;
import cz.cuni.amis.pogamut.base.factory.guice.GuiceAgentFactory;
import cz.cuni.amis.pogamut.base.factory.guice.GuiceAgentModule;

/**
 * This interface describes everything that is needed to instantiate and start of the agent.
 * <p><p>
 * It describes three things:
 * <ol>
 * <li>Guice module that contains a definition (bindings) for the factory, so we know how to instantiate the agent ({@link IAgentDescriptor#getAgentModule()})</li>
 * <li>number of the agents that should be instantiated/started ({@link IAgentDescriptor#getCount()})</li>
 * <li>respective parameters that should be passed to the agents during construction ({@link IAgentDescriptor#getAgentParameters()})</li> 
 * </ol>
 * <p><p>
 * Used by implementors of {@link IMultipleAgentRunner}. It allows you to start different agents (of different classes)
 * at once.
 * <p><p>
 * Note that the {@link IAgentDescriptor#getCount()} does not need to match the {@link IAgentDescriptor#getAgentParameters()}.length.
 * There are three scenarios (according to the relation between those two numbers) and the number of agents the {@link IMultipleAgentRunner} will instantiate:
 * <ol>
 * <li>{@link IAgentDescriptor#getCount()} > {@link IAgentDescriptor#getAgentParameters()}.length - in this case, the {@link IMultipleAgentRunner} will instantiate specified number of agents with default parameters</li>
 * <li>{@link IAgentDescriptor#getCount()} == {@link IAgentDescriptor#getAgentParameters()}.length - in this case, the {@link IMultipleAgentRunner} will instantiate the same number of agents as there are parameters</li>
 * <li>{@link IAgentDescriptor#getCount()} < {@link IAgentDescriptor#getAgentParameters()}.length - the {@link IMultipleAgentRunner} will instantiate 'count' of agents and first 'number of parameters' will have custom params as specified, rest will have defaults</li>
 * </ol>
 * 
 * @author Jimmy
 *
 * @param <PARAMS>
 * @param <MODULE>
 */
public interface IAgentDescriptor<PARAMS extends IAgentParameters, MODULE extends GuiceAgentModule> {
	
	/**
	 * Agent module that contains bindings for the classes, used by {@link GuiceAgentFactory} (or concretely by the Guice)
	 * to instantiate the agent.
	 * 
	 * @return agent's module
	 */
	public MODULE getAgentModule();
	
	/**
	 * How many instances this object describes, i.e., how many agents should be instantiated using {@link IAgentDescriptor#getAgentModule()}.
	 * @return number of agents to instantiate
	 */
	public int getCount();
	
	/**
	 * Respective parameters of the agents. The length of the array must be the same as {@link IAgentDescriptor#getCount()}, i.e.,
	 * there must be a separate parameter instance for every agent).
	 * <p><p>
	 * Note that some parameters may be null as {@link IMultipleAgentRunner} implementation can provide
	 * default parameters for agents (usually).
	 * 
	 * @return
	 */
	public PARAMS[] getAgentParameters();	
	
}
