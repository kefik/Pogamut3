package cz.cuni.amis.pogamut.base.agent.leaktest.test;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AbstractAgent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.utils.flag.FlagInteger;
import cz.cuni.amis.utils.flag.ImmutableFlag;

public class LeakTestAgent_03 extends AbstractAgent {

	private static FlagInteger instances = new FlagInteger(0);
	
	public static ImmutableFlag<Integer> getInstances() {
		return instances.getImmutable();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		instances.decrement(1);
	}
	
	@SuppressWarnings("unchecked")
	@Inject
	public LeakTestAgent_03(IAgentId agentId, IComponentBus eventBus, IAgentLogger logger) {
		super(agentId, eventBus, logger);
		instances.increment(1);
	}
	
	@Override
	protected void startAgent() {
		super.startAgent();
		getJMX().enableJMX();
	}

}
