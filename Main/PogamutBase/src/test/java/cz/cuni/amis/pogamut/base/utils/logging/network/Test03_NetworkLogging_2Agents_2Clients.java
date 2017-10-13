package cz.cuni.amis.pogamut.base.utils.logging.network;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateDown;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.logging.NetworkLogManager;

public class Test03_NetworkLogging_2Agents_2Clients extends NetworkLoggingTest {

	@Test
	public void test() {

		try {
			
			double logicFrequency = 5;
			int logsPerCycle = 1000;
			int lifeTimeSeconds = 2;
			int logicCycles = (int)Math.ceil(logicFrequency * lifeTimeSeconds);
			int totalLogsOfOneCategory = logsPerCycle * logicCycles;
			
			NetworkLoggingAgent agent1 = createNewAgent(logicFrequency, logsPerCycle, logicCycles);
			NetworkLoggingAgent agent2 = createNewAgent(logicFrequency, logsPerCycle, logicCycles);
			
			agent1.start();			
			try {
				
				agent2.start();
				try {
				
					CheckNetworkLogClient logClient1 = 
						new CheckNetworkLogClient(
								"LogClient1-" + agent1.getName(),
								"localhost", 
								NetworkLogManager.getNetworkLogManager().getLoggerPort(), 
								agent1.getComponentId().getToken()
						);
					CheckNetworkLogClient logClient2 = 
						new CheckNetworkLogClient(
								"LogClient1-" + agent2.getName(),
								"localhost", 
								NetworkLogManager.getNetworkLogManager().getLoggerPort(), 
								agent2.getComponentId().getToken()
						);
					
					logClient1.start();
					
					try {
						try {
							logClient2.start();
//							System.out.println("[INFO]  Sleeping for 4secs to let the NetworkLogManager to catch up with logs...");
//							try {
//								Thread.sleep(4000); // let the NetworkLogManager to catch up with logs...
//							} catch (InterruptedException e) {
//							}
							System.out.println("[INFO]  Dropping agent's logic latches, starting to send logs...");
							agent1.getLogicLatch().countDown();
							agent2.getLogicLatch().countDown();
							
							agent1.awaitState(IAgentStateDown.class);
							logClient1.getRunning().waitFor(1000, false);
							logClient1.getThroughput().check();
							agent2.awaitState(IAgentStateDown.class);
							logClient2.getRunning().waitFor(1000, false);
							logClient2.getThroughput().check();
												
							checkAgent(agent1);
							checkAgent(agent2);
							checkLogClient(logClient1, totalLogsOfOneCategory);
							checkLogClient(logClient2, totalLogsOfOneCategory);
						} finally {
							if (logClient2.getRunning().getFlag()) {
								logClient2.stop();
							}
						}
					} finally {
						if (logClient1.getRunning().getFlag()) {
							logClient1.stop();
						}
					}	
				} finally {
					if (agent2.notInState(IAgentStateDown.class)) agent2.kill();
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
