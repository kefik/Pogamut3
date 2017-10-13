package cz.cuni.amis.pogamut.base.agent.utils.runner.test;

import java.util.concurrent.CountDownLatch;

import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.agent.params.impl.AgentParameters;

public class TestAgentParams extends AgentParameters {
	
	private int param;

	public TestAgentParams(int param) {
		this.param = param;
	}

	public TestAgentParams(String agentName, int param) {
		super();
		setAgentId(new AgentId(agentName));
		param = param;
	}

	public int getParam() {
		return param;
	}

}
