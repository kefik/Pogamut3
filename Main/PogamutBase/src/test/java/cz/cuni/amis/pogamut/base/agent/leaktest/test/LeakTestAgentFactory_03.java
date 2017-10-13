package cz.cuni.amis.pogamut.base.agent.leaktest.test;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.params.IAgentParameters;
import cz.cuni.amis.pogamut.base.component.bus.ComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.factory.IAgentFactory;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.utils.exception.PogamutException;

public class LeakTestAgentFactory_03 implements IAgentFactory {

	@Override
	public IAgent newAgent(IAgentParameters agentParameters)
			throws PogamutException {
		IAgentLogger logger = new AgentLogger(agentParameters.getAgentId());
		IComponentBus bus = new ComponentBus(logger);		
		return new LeakTestAgent_03(agentParameters.getAgentId(), bus, logger);
	}

}
