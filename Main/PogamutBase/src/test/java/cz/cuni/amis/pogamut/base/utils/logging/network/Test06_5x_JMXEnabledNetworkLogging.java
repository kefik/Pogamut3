package cz.cuni.amis.pogamut.base.utils.logging.network;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.jmx.proxy.AgentJMXProxy;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateDown;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.logging.NetworkLogManager;

public class Test06_5x_JMXEnabledNetworkLogging extends JMXNetworkLoggingTest {

	@Test
	public void test() {

		try {
			
			double logicFrequency = 5;
			int logsPerCycle = 2000;
			int lifeTimeSeconds = 2;
			int logicCycles = (int)Math.ceil(logicFrequency * lifeTimeSeconds);
			int totalLogsOfOneCategory = logsPerCycle * logicCycles;
			
			JMXNetworkLoggingAgent agent = createNewAgentJMX(logicFrequency, logsPerCycle, logicCycles);
			
			AgentJMXProxy agentProxy = null;
			
			for (int i = 0; i < 5; ++i) {
				
				System.out.println("[INFO] Test " + (i+1) + " / 5");
				
				System.out.println("[INFO] Starting agent... ");
				
				agent.start();
				
				if (i == 0) {
					System.out.println("[INFO] Enabling agent JMX... ");
				
					String agentAddress = agent.getJMX().enableJMX();
					
					System.out.println("[INFO] Creating AgentJMXProxy... ");
					
					agentProxy = new AgentJMXProxy(agentAddress);
				}
				
				System.out.println("[INFO] Enabling network logging via JMX...");
		        
		        agentProxy.getLogger().addDefaultNetworkHandler();
		        
				try {
					
					System.out.println("[INFO] Creating NetworkLogClient...");
					
					CheckNetworkLogClient logClient = 
						new CheckNetworkLogClient(
								"LogClient-" + agent.getName(),
								"localhost", 
								NetworkLogManager.getNetworkLogManager().getLoggerPort(), 
								agent.getComponentId().getToken()
						);
					
					System.out.println("[INFO] Starting NetworkLogClient...");
					
					logClient.start();
					
					try {
						System.out.println("[INFO]  Dropping agent logic latch, starting to send logs...");
						agent.getLogicLatch().countDown();
						
						agent.awaitState(IAgentStateDown.class);
						logClient.getRunning().waitFor(1000, false);
						logClient.getThroughput().check();
						
						checkAgent(agent);
						checkLogClient(logClient, totalLogsOfOneCategory);
						
					} finally {
						if (logClient.getRunning().getFlag()) {
							logClient.stop();
						}
					}				
				} finally {
					if (agent.notInState(IAgentStateDown.class)) agent.kill();
				}

			}
			
			
		} finally {
			Pogamut.getPlatform().close();
		}
		
		System.out.println("---/// TEST OK ///---");
	}	
	
}
