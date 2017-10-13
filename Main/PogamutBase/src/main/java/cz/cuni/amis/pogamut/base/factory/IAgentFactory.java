package cz.cuni.amis.pogamut.base.factory;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.params.IAgentParameters;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * General factory that instantiates the agent according to the passed parameters. Note that every factory may require
 * different {@link IAgentParameters} descendants/implementors according to the agent type and target agent environment.
 * @author Jimmy
 *
 * @param <AGENT> type of the agent the factory is producing
 * @param <PARAMS> type of the parameters that the agent is configured with
 */
public interface IAgentFactory<AGENT extends IAgent, PARAMS extends IAgentParameters> {
	
	/**
	 * Factory method - it creates an agent with 'agentParameters'
	 * <p><p>
	 * <b>DOES NOT START THE AGENT!</b>
	 *  
	 * @param agentParameters
	 * @return new agent instance configured with 'agentParameters'
	 * @throws PogamutException
	 */
	public AGENT newAgent(PARAMS agentParameters) throws PogamutException;

}
