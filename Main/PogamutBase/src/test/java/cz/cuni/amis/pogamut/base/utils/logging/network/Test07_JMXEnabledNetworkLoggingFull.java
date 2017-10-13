package cz.cuni.amis.pogamut.base.utils.logging.network;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.jmx.proxy.AgentJMXProxy;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateDown;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.logging.NetworkLogManager;

public class Test07_JMXEnabledNetworkLoggingFull extends JMXNetworkLoggingTest {

	@Test
	public void test() {

		try {
			
			double logicFrequency = 5;
			int logsPerCycle = 2000;
			int lifeTimeSeconds = 2;
			int logicCycles = (int)Math.ceil(logicFrequency * lifeTimeSeconds);
			int totalLogsOfOneCategory = logsPerCycle * logicCycles;
			
			JMXNetworkLoggingAgent agent = createNewAgentJMX(logicFrequency, logsPerCycle, logicCycles);
			
			System.out.println("[INFO] Starting agent... ");
			
			agent.start();
			
			System.out.println("[INFO] Enabling agent JMX... ");
			
			String agentAddress = agent.getJMX().enableJMX();	
			
			System.out.println("[INFO] Creating AgentJMXProxy... ");
			
	        AgentJMXProxy agentProxy = new AgentJMXProxy(agentAddress);
	        
	        System.out.println("[INFO] Enabling network logging via JMX...");
	        
	        agentProxy.getLogger().addDefaultNetworkHandler();
	        
	        System.out.println("[INFO] Obtaining host:port address of the network logger...");
	        
	        String host = agentProxy.getLogger().getNetworkLoggerHost();
	        Integer port = agentProxy.getLogger().getNetworkLoggerPort();
	        
	        if (host == null) {
	        	System.out.println("[ERROR] HOST is NULL!! agentProxy.getLogger().getNetworkLoggerHost() failed!");
	        	throw new RuntimeException("[SEVERE] HOST is NULL!! agentProxy.getLogger().getNetworkLoggerHost() failed!");
	        }
	        if (port == null) {
	        	System.out.println("[ERROR] PORT is NULL!! agentProxy.getLogger().getNetworkLoggerPort() failed!");
	        	throw new RuntimeException("[ERROR] PORT is NULL!! agentProxy.getLogger().getNetworkLoggerPort() failed!");
	        }
	        if (port <= 0) {
	        	System.out.println("[ERROR] PORT <= 0!! agentProxy.getLogger().getNetworkLoggerPort() failed!");
	        	throw new RuntimeException("[ERROR] PORT <= 0!! agentProxy.getLogger().getNetworkLoggerPort() failed!");
	        }
	        
	        System.out.println("[INFO] Network logging is running at " + host + ":" + port + " ...");
	        
			try {
				
				System.out.println("[INFO] Creating NetworkLogClient...");
				
				CheckNetworkLogClient logClient1 = 
					new CheckNetworkLogClient(
							"LogClient1-" + agent.getName(),
							host,
							port,
							agent.getComponentId().getToken()
					);
				
				System.out.println("[INFO] starting NetworkLogClient...");
				
				logClient1.start();
				
				try {
					System.out.println("[INFO]  Dropping agent logic latch, starting to send logs...");
					agent.getLogicLatch().countDown();
					
					agent.awaitState(IAgentStateDown.class);
					logClient1.getRunning().waitFor(1000, false);
					logClient1.getThroughput().check();
					
					checkAgent(agent);
					checkLogClient(logClient1, totalLogsOfOneCategory);
					
				} finally {
					if (logClient1.getRunning().getFlag()) {
						logClient1.stop();
					}
				}				
			} finally {
				if (agent.notInState(IAgentStateDown.class)) agent.kill();
			}
			
			
			
		} finally {
			Pogamut.getPlatform().close();
		}
		
		System.out.println("---/// TEST OK ///---");
	}	
	
}
