package cz.cuni.amis.pogamut.base.agent.impl;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.IEmbodiedAgent;
import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.utils.NullCheck;

@AgentScoped
public abstract class AbstractEmbodiedAgent<WORLD_VIEW extends IWorldView, ACT extends IAct> extends AbstractGhostAgent<WORLD_VIEW, ACT> implements IEmbodiedAgent {
	
	private ACT act;

	@Inject
	public AbstractEmbodiedAgent(IAgentId agentId, IComponentBus bus, IAgentLogger logger, WORLD_VIEW worldView, ACT act) {
		super(agentId, bus, logger, worldView, act);
		this.act = act;
		NullCheck.check(this.act, "act");
		addDependency(act);
	}
	
	@Override
	public ACT getAct() {
		return act;
	}

}
