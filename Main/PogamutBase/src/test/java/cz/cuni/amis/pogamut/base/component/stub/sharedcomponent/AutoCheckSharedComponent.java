package cz.cuni.amis.pogamut.base.component.stub.sharedcomponent;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.component.ISharedComponent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.IComponentEvent;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.ILifecycleBus;
import cz.cuni.amis.pogamut.base.component.stub.component.CheckEvent;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Token;
import cz.cuni.amis.utils.token.Tokens;

public class AutoCheckSharedComponent implements ISharedComponent {

	private static int counter = 0;
	
	private Token token;

	private LogCategory log;

	private AutoCheckSharedComponentController<ISharedComponent> controller;
		
	public AutoCheckSharedComponent(IAgentLogger logger) {
		this.token = Tokens.get("AutoCheckSharedComponent" + counter++);
		NullCheck.check(this.token, "token initialization");
		this.log = logger.getCategory(this);
		NullCheck.check(this.log, "log initialization");
		this.controller = new AutoCheckSharedComponentController<ISharedComponent>(this, log);
	}
	
	public AutoCheckSharedComponentController<ISharedComponent> getController() {
		return controller;
	}
	
	public boolean isShouldBeChecking() {
		return ((AutoCheckSharedComponentController)this.controller).isShouldBeChecking();
	}

	public void setShouldBeChecking(boolean shouldBeChecking) {
		((AutoCheckSharedComponentController)this.controller).setShouldBeChecking(shouldBeChecking);
	}

	
	@Override
	public void addComponentBus(IAgentId agentId, ILifecycleBus bus,
			ComponentDependencies dependencies) {
		this.controller.addComponentBus(agentId, bus, dependencies);
	}

	@Override
	public void removeComponentBus(IAgentId agentId, ILifecycleBus bus) {
		this.controller.removeComponentBus(agentId, bus);
	}

	@Override
	public IToken getComponentId() {
		return token;
	}

	public Logger getLog() {
		return log;
	}

	public AutoCheckSharedComponent expectAnyOrder(String... activity) {
		this.controller.expectAnyOrder(activity);
		return this;
	}
	
	public AutoCheckSharedComponent expectExactOrder(String... activity) {
		this.controller.expectExactOrder(activity);
		return this;
	}

	public void checkNoMoreActivityExpected() {
		this.controller.checkNoMoreActivityExpected();
	}
	
}
