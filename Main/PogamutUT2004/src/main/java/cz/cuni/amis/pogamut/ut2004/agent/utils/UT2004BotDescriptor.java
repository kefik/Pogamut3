package cz.cuni.amis.pogamut.ut2004.agent.utils;

import cz.cuni.amis.pogamut.base.agent.params.IRemoteAgentParameters;
import cz.cuni.amis.pogamut.base.agent.utils.runner.impl.AgentDescriptor;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004BotModule;

public class UT2004BotDescriptor<PARAMS extends IRemoteAgentParameters> extends AgentDescriptor<PARAMS, UT2004BotModule> {

	public UT2004BotDescriptor<PARAMS> setController(Class<? extends IUT2004BotController> controllerClass) {
		return setAgentModule(new UT2004BotModule(controllerClass));
	}
	
	@Override
	public UT2004BotDescriptor<PARAMS> setAgentModule(UT2004BotModule module) {
		super.setAgentModule(module);
		return this;
	}
	
	@Override
	public UT2004BotDescriptor<PARAMS> setAgentParameters(PARAMS[] params) {
		super.setAgentParameters(params);
		return this;
	}
	
	@Override
	public UT2004BotDescriptor<PARAMS> addParams(PARAMS... params) {
		super.addParams(params);
		return this;
	}
	
	@Override
	public UT2004BotDescriptor<PARAMS> setCount(int count) {
		super.setCount(count);
		return this;
	}
	
}
