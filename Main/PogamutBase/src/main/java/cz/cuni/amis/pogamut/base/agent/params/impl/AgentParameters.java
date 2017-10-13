package cz.cuni.amis.pogamut.base.agent.params.impl;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.agent.params.IAgentParameters;
import cz.cuni.amis.pogamut.base.agent.utils.runner.IAgentRunner;
import cz.cuni.amis.pogamut.base.agent.utils.runner.IMultipleAgentRunner;
import cz.cuni.amis.pogamut.base.communication.connection.IWorldConnectionAddress;

/**
 * Agent parameters are meant to provide run-time parameters needed by various agents.
 * <p><p>
 * If you need to populate the parameters after instantiation, use setter available in this
 * class: {@link AgentParameters#setAgentId(IAgentId)}
 * <p><p>
 * NOTE: all {@link IAgentParameters} implementors are usually used together with {@link IAgentRunner} or {@link IMultipleAgentRunner}
 * which usually contains sensible default params, therefore there is no need to set all parameters
 * into newly created ones as runners will supply them via {@link IAgentParameters#assignDefaults(IAgentParameters)}.
 * 
 * @author Jimmy
 */
public class AgentParameters implements IAgentParameters {

	private IAgentId agentId;

	public AgentParameters() {
		this.agentId = null;
	}
		
	@Override
	public IAgentId getAgentId() {
		return agentId;
	}
	
	/**
	 * Sets agent id into the parameters.
	 * <p><p>
	 * WARNING: Note that you should not mess with 'setters' in different threads as they
	 * are non-thread-safe and may interrupt horrible agent instantiations with such behavior.
	 * @param agentId
	 * @return this instance
	 */
	public AgentParameters setAgentId(IAgentId agentId) {
		this.agentId = agentId;
		return this;
	}

	@Override
	public void assignDefaults(IAgentParameters defaults) {
		if (agentId == null) agentId = defaults.getAgentId();
	}

}
