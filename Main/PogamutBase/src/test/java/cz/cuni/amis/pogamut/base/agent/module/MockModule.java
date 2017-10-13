package cz.cuni.amis.pogamut.base.agent.module;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.component.controller.ComponentController;

public class MockModule extends AgentModule {

	public MockModule(IAgent agent) {
		super(agent);
	}
	
	public ComponentController getController() {
		return controller;
	}

}
