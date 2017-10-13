package cz.cuni.amis.pogamut.base.agent.leaktest.test;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AbstractAgent;
import cz.cuni.amis.pogamut.base.agent.module.IAgentLogic;
import cz.cuni.amis.pogamut.base.agent.module.LogicModule;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.utils.flag.FlagInteger;
import cz.cuni.amis.utils.flag.ImmutableFlag;

public class LeakTestAgent_02 extends AbstractAgent {

	private static FlagInteger instances = new FlagInteger(0);
	
	public static ImmutableFlag<Integer> getInstances() {
		return instances.getImmutable();
	}

	private LogicModule logicModule;
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		instances.decrement(1);
	}
	
	@SuppressWarnings("unchecked")
	@Inject
	public LeakTestAgent_02(IAgentId agentId, IComponentBus eventBus, IAgentLogger logger) {
		super(agentId, eventBus, logger);
		instances.increment(1);
		this.logicModule = new LogicModule(this, new IAgentLogic() {

			@Override
			public long getLogicInitializeTime() {
				return 0;
			}

			@Override
			public long getLogicShutdownTime() {
				return 0;
			}

			@Override
			public void logic() {
				System.out.println("Logic...");
			}

			@Override
			public void logicInitialize(LogicModule logicModule) {
			}

			@Override
			public void logicShutdown() {
			}

			@Override
			public void beforeFirstLogic() {
			}
			
		});
	}
	

}
