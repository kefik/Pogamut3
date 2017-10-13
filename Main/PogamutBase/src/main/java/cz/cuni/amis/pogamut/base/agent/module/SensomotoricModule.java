package cz.cuni.amis.pogamut.base.agent.module;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.IEmbodiedAgent;
import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;

public class SensomotoricModule<AGENT extends IEmbodiedAgent> extends AgentModule<AGENT> {

	protected final IWorldView worldView;
	protected final IAct act;

	public SensomotoricModule(AGENT agent) {
		this(agent, null);
	}
	
	public SensomotoricModule(AGENT agent, Logger log) {
		this(agent, log, null);
	}
	
	public SensomotoricModule(AGENT agent, Logger log, ComponentDependencies dependencies) {
		super(agent, log, dependencies);
		this.worldView = agent.getWorldView();
		this.act = agent.getAct();
	}
	
}
