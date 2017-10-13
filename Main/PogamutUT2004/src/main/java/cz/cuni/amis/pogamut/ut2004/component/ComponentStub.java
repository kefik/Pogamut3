package cz.cuni.amis.pogamut.ut2004.component;

import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.controller.ComponentControlHelper;
import cz.cuni.amis.pogamut.base.component.controller.ComponentController;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencyType;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Token;
import cz.cuni.amis.utils.token.Tokens;

public class ComponentStub implements IComponent {

	private static int num = 0;
	
	private ComponentController controller;

	private Token token;

	private LogCategory log;

	public ComponentStub(IAgentLogger logger, IComponentBus bus) {
		this.token = Tokens.get("ComponentStub" + (++num));
		this.log = logger.getCategory(this);
		this.controller = new ComponentController(this, new ComponentControlHelper(), bus, log, ComponentDependencyType.STARTS_WITH);
	}
	
	@Override
	public IToken getComponentId() {
		return token;
	}
	
	public ComponentController getController() {
		return controller;
	}

}
