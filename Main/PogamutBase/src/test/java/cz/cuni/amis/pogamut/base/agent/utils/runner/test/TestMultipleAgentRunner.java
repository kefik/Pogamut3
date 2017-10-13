package cz.cuni.amis.pogamut.base.agent.utils.runner.test;

import cz.cuni.amis.pogamut.base.agent.impl.AbstractAgent;
import cz.cuni.amis.pogamut.base.agent.params.IAgentParameters;
import cz.cuni.amis.pogamut.base.agent.params.impl.AgentParameters;
import cz.cuni.amis.pogamut.base.agent.utils.runner.impl.MultipleAgentRunner;
import cz.cuni.amis.pogamut.base.factory.IAgentFactory;
import cz.cuni.amis.pogamut.base.factory.guice.GuiceAgentFactory;
import cz.cuni.amis.pogamut.base.factory.guice.GuiceAgentModule;

public class TestMultipleAgentRunner extends MultipleAgentRunner<AbstractAgent, TestAgentParams, GuiceAgentModule> {

	@Override
	protected IAgentFactory newAgentFactory(GuiceAgentModule agentModule) {
		return new GuiceAgentFactory<AbstractAgent, AgentParameters>(agentModule);
	}

	@Override
	protected IAgentParameters newDefaultAgentParameters() {
		return new TestAgentParams("TestAgent", 0);
	}

}
