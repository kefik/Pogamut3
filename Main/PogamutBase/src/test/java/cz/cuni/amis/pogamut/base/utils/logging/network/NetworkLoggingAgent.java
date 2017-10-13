package cz.cuni.amis.pogamut.base.utils.logging.network;

import java.util.concurrent.CountDownLatch;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AbstractAgent;
import cz.cuni.amis.pogamut.base.agent.module.IAgentLogic;
import cz.cuni.amis.pogamut.base.agent.module.LogicModule;
import cz.cuni.amis.pogamut.base.component.bus.ComponentBus;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;

public class NetworkLoggingAgent extends AbstractAgent {

	public IAgentLogic myLogic = new IAgentLogic() {

		@Override
		public long getLogicInitializeTime() {
			return 1000;
		}

		@Override
		public long getLogicShutdownTime() {
			return 1000;
		}

		@Override
		public void logic() {
			if (logicCycles == -1) {
				return;
			}
			if (logicCycles == 0) {
//				try {
//					Thread.sleep(10000); // let NetworkLogManager to send all remaining logs...
//				} catch (Exception e) {					
//				}
				new Thread(new Runnable() {
					@Override
					public void run() {
						NetworkLoggingAgent.this.stop();						
					}
				}).start();
				--logicCycles;
				return;
			}
			if (logicLatch != null) {
				try {
					logicLatch.await();
					Thread.sleep(500);
				} catch (InterruptedException e) {
					throw new PogamutInterruptedException(e, this);
				}
				logicLatch = null;
				System.out.println("[INFO] " + getComponentId().getToken() + " : About to send first logs...");
			}
			for (int i = 0; i < logsPerLogicCycle; ++i) {
				// DO NOT CHANGE 'my-log' and 'I'm alive!' ...
				NetworkLoggingAgent.this.getLogger().getCategory("my-log").finest("!!! I'm alive! FINEST - " + i);
				NetworkLoggingAgent.this.getLogger().getCategory("my-log").finer("!!! I'm alive! FINER - " + i);
				NetworkLoggingAgent.this.getLogger().getCategory("my-log").fine("!!! I'm alive! FINE - " + i);
				NetworkLoggingAgent.this.getLogger().getCategory("my-log").info("!!! I'm alive! INFO - " + i);
				NetworkLoggingAgent.this.getLogger().getCategory("my-log").warning("!!! I'm alive! WARNING - " + i);
				NetworkLoggingAgent.this.getLogger().getCategory("my-log").severe("!!! I'm alive! SEVERE - " + i);
			}
			--logicCycles;
		}

		@Override
		public void logicInitialize(LogicModule logicModule) {
			logicLatch = new CountDownLatch(1);
			logicCycles = origLogicCycles;
		}

		@Override
		public void logicShutdown() {
		}

		@Override
		public void beforeFirstLogic() {
		}

	};
	
	private LogicModule logic;

	private int logsPerLogicCycle;
	
	private int logicCycles;
	
	private CountDownLatch logicLatch = new CountDownLatch(1);

	private int origLogicCycles;
	
	public CountDownLatch getLogicLatch() {
		return logicLatch;
	}

	public NetworkLoggingAgent(IAgentId agentId, IAgentLogger logger, double logicFrequency, int logsPerLogicCycle, int logicCycles) {
		super(agentId, new ComponentBus(logger), logger);
		logger.addDefaultNetworkHandler();
		logic = new LogicModule(this, myLogic);
		logic.setLogicFrequency(logicFrequency);
		this.logsPerLogicCycle = logsPerLogicCycle;
		this.logicCycles = logicCycles;
		this.origLogicCycles = logicCycles;
	}

}