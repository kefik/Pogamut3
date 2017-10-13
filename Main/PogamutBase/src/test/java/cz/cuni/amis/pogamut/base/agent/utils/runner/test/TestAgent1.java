package cz.cuni.amis.pogamut.base.agent.utils.runner.test;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.agent.impl.AbstractAgent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;

public class TestAgent1 extends AbstractAgent {

	private TestAgentParams params;

	@Inject
	public TestAgent1(TestAgentParams params, IComponentBus eventBus, IAgentLogger logger) {
		super(params.getAgentId(), eventBus, logger);
		this.params = params;
	}

	public int getParam() {
		return params.getParam();
	}

}
