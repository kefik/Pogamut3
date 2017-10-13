package cz.cuni.amis.pogamut.base.agent.module.comm;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.IObservingAgent;
import cz.cuni.amis.pogamut.base.agent.impl.AbstractAgent;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.impl.EventDrivenWorldView;
import cz.cuni.amis.pogamut.base.component.bus.ComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencyType;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.flag.FlagInteger;

public class ObservingAgent extends AbstractAgent implements IObservingAgent {
	
	public static FlagInteger instanceCount = new FlagInteger(0);
	
	private static int id = 0;
	private static AgentLogger logger;
	private static AgentId agentId;
	private IWorldView worldView;

	public ObservingAgent() {
		super(agentId = new AgentId("ObservingAgent" + (++id)), new ComponentBus(logger = new AgentLogger(agentId)), logger);
		this.worldView = new EventDrivenWorldView(new ComponentDependencies(ComponentDependencyType.STARTS_WITH, this.getComponentId()), getEventBus(), getLogger());
		NullCheck.check(this.worldView, "worldView");
		instanceCount.increment(1);
	}

	@Override
	public IWorldView getWorldView() {
		return worldView;
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		instanceCount.decrement(1);
	}

}
