package cz.cuni.amis.pogamut.base.agent.module;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.IEmbodiedAgent;
import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;

public class MotoricModule<AGENT extends IEmbodiedAgent> extends AgentModule<AGENT> {

	protected final IAct act;

	public MotoricModule(AGENT agent) {
		this(agent, null);
	}
	
	public MotoricModule(AGENT agent, Logger log) {
		this(agent, log, null);
	}
	
	public MotoricModule(AGENT agent, Logger log, ComponentDependencies dependencies) {
		super(agent, log, dependencies);
		this.act = agent.getAct();
	}
	
}
