package cz.cuni.amis.pogamut.base.utils.logging.network;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AbstractAgent;
import cz.cuni.amis.pogamut.base.agent.module.IAgentLogic;
import cz.cuni.amis.pogamut.base.agent.module.LogicModule;
import cz.cuni.amis.pogamut.base.component.bus.ComponentBus;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;

public class JMXNetworkLoggingAgent extends AbstractAgent {

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
						JMXNetworkLoggingAgent.this.stop();						
					}
				}).start();
				--logicCycles;
				return;
			}
			if (logicLatch != null) {
				try {
					logicLatch.await();
					logicLatch = null;
					System.out.println("(" + JMXNetworkLoggingAgent.this.getComponentId().getToken() + ") [INFO] Logic latched dropped, waiting 1 sec to let the NetworkLogManger to catch the NetworkLogClient connection...");
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					throw new PogamutInterruptedException(e, this);
				}
				System.out.println("(" + JMXNetworkLoggingAgent.this.getComponentId().getToken() + ") [INFO] About to send first logs...");
			}
			for (int i = 0; i < logsPerLogicCycle; ++i) {
				// DO NOT CHANGE 'my-log' and 'I'm alive!' ...
				JMXNetworkLoggingAgent.this.getLogger().getCategory("my-log").finest("!!! (" + JMXNetworkLoggingAgent.this.getComponentId().getToken() + ") I'm alive! FINEST - " + i);
				JMXNetworkLoggingAgent.this.getLogger().getCategory("my-log").finer("!!! (" + JMXNetworkLoggingAgent.this.getComponentId().getToken() + ") I'm alive! FINER - " + i);
				JMXNetworkLoggingAgent.this.getLogger().getCategory("my-log").fine("!!! (" + JMXNetworkLoggingAgent.this.getComponentId().getToken() + ") I'm alive! FINE - " + i);
				JMXNetworkLoggingAgent.this.getLogger().getCategory("my-log").info("!!! (" + JMXNetworkLoggingAgent.this.getComponentId().getToken() + ") I'm alive! INFO - " + i);
				JMXNetworkLoggingAgent.this.getLogger().getCategory("my-log").warning("!!! (" + JMXNetworkLoggingAgent.this.getComponentId().getToken() + ") I'm alive! WARNING - " + i);
				JMXNetworkLoggingAgent.this.getLogger().getCategory("my-log").severe("!!! (" + JMXNetworkLoggingAgent.this.getComponentId().getToken() + ") I'm alive! SEVERE - " + i);
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
	
	private CountDownLatch logicLatch;

	private int origLogicCycles;
	
	public CountDownLatch getLogicLatch() {
		return logicLatch;
	}

	public JMXNetworkLoggingAgent(IAgentId agentId, IAgentLogger logger, double logicFrequency, int logsPerLogicCycle, int logicCycles) {
		super(agentId, new ComponentBus(logger), logger);
		logic = new LogicModule(this, myLogic);
		logic.setLogicFrequency(logicFrequency);
		log.setLevel(Level.ALL);
		this.logsPerLogicCycle = logsPerLogicCycle;
		this.logicCycles = logicCycles;
		this.origLogicCycles = logicCycles;
	}

}