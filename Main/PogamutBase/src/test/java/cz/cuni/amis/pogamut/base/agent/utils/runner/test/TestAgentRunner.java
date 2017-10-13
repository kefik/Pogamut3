package cz.cuni.amis.pogamut.base.agent.utils.runner.test;

import cz.cuni.amis.pogamut.base.agent.impl.AbstractAgent;
import cz.cuni.amis.pogamut.base.agent.params.IAgentParameters;
import cz.cuni.amis.pogamut.base.agent.utils.runner.impl.AgentRunner;
import cz.cuni.amis.pogamut.base.factory.IAgentFactory;

public class TestAgentRunner extends AgentRunner<AbstractAgent, TestAgentParams> {

	public TestAgentRunner(IAgentFactory factory) {
		super(factory);
	}

	@Override
	protected IAgentParameters newDefaultAgentParameters() {
		return new TestAgentParams("TestAgent", 0);
	}

}
