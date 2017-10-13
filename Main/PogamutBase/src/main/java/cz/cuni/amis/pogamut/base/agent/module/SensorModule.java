package cz.cuni.amis.pogamut.base.agent.module;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.IObservingAgent;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;

public class SensorModule<AGENT extends IObservingAgent> extends AgentModule<AGENT> {

	protected final IWorldView worldView;

	public SensorModule(AGENT agent) {
		this(agent, null);
	}
	
	public SensorModule(AGENT agent, Logger log) {
		this(agent, log, null);
	}
	
	public SensorModule(AGENT agent, Logger log, ComponentDependencies dependencies) {
		super(agent, log, dependencies);
		this.worldView = agent.getWorldView();
	}

}
