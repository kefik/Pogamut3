package cz.cuni.amis.pogamut.base.utils.logging.network;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateDown;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.logging.NetworkLogManager;

public class Test01_NetworkLogging_1Agent_1Client extends NetworkLoggingTest {

	@Test
	public void test() {

		try {
			
			double logicFrequency = 5;
			int logsPerCycle = 2000;
			int lifeTimeSeconds = 2;
			int logicCycles = (int)Math.ceil(logicFrequency * lifeTimeSeconds);
			int totalLogsOfOneCategory = logsPerCycle * logicCycles;
			
			NetworkLoggingAgent agent1 = createNewAgent(logicFrequency, logsPerCycle, logicCycles);
			
			agent1.start();
			
			try {
				
				CheckNetworkLogClient logClient1 = 
					new CheckNetworkLogClient(
							"LogClient1-" + agent1.getName(),
							"localhost", 
							NetworkLogManager.getNetworkLogManager().getLoggerPort(), 
							agent1.getComponentId().getToken()
					);
				
				logClient1.start();
				
				try {
//					System.out.println("[INFO]  Sleeping for 4secs to let the NetworkLogManager to catch up with logs...");
//					try {
//						Thread.sleep(4000); // let the NetworkLogManager to catch up with logs...
//					} catch (InterruptedException e) {
//					}
					System.out.println("[INFO]  Dropping agent logic latch, starting to send logs...");
					agent1.getLogicLatch().countDown();
					
					agent1.awaitState(IAgentStateDown.class);
					logClient1.getRunning().waitFor(1000, false);
					logClient1.getThroughput().check();
					
					checkAgent(agent1);
					checkLogClient(logClient1, totalLogsOfOneCategory);
					
				} finally {
					if (logClient1.getRunning().getFlag()) {
						logClient1.stop();
					}
				}				
			} finally {
				if (agent1.notInState(IAgentStateDown.class)) agent1.kill();
			}
			
			
			
		} finally {
			Pogamut.getPlatform().close();
		}
		
		System.out.println("---/// TEST OK ///---");
	}
	
}
