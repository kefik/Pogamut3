package cz.cuni.amis.pogamut.base.agent.utils.runner.test;

import com.google.inject.AbstractModule;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.factory.guice.GuiceAgentModule;

public class TestAgent1Module extends GuiceAgentModule<TestAgentParams> {
	
	@Override
	protected void configureModules() {
		super.configureModules();
		addModule(new AbstractModule() {
			@Override
			protected void configure() {
				bind(IAgent.class).to(TestAgent1.class);
				bind(TestAgentParams.class).toProvider(getAgentParamsProvider());
			}
		});
	}

}
