package cz.cuni.amis.pogamut.base.component.stub.sharedcomponent;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.component.ISharedComponent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.controller.ComponentController;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencyType;
import cz.cuni.amis.pogamut.base.component.controller.ISharedComponentController;
import cz.cuni.amis.pogamut.base.component.controller.SharedComponentControlHelper;
import cz.cuni.amis.pogamut.base.component.controller.SharedComponentController;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.ILifecycleBus;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Token;
import cz.cuni.amis.utils.token.Tokens;

public class SharedComponentStub implements ISharedComponent {

	private static int num = 0;
	
	private ISharedComponentController<ISharedComponent> controller;

	private Token token;

	private LogCategory log;

	public SharedComponentStub(IAgentLogger logger, IComponentBus bus) {
		this.token = Tokens.get("SharedComponentStub" + (++num));
		this.log = logger.getCategory(this);
		this.controller = new SharedComponentController<ISharedComponent>(this, new SharedComponentControlHelper(), log);
	}
		
	@Override
	public IToken getComponentId() {
		return token;
	}
	
	public Logger getLog() {
		return log;
	}
	
	public ISharedComponentController<ISharedComponent> getController() {
		return controller;
	}
	
	@Override
	public void addComponentBus(IAgentId agentId, ILifecycleBus bus, ComponentDependencies dependencies) {
		controller.addComponentBus(agentId, bus, dependencies);
	}

	@Override
	public void removeComponentBus(IAgentId agentId, ILifecycleBus bus) {
		controller.removeComponentBus(agentId, bus);
	}

}
