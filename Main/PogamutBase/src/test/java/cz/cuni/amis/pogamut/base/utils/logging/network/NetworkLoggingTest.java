package cz.cuni.amis.pogamut.base.utils.logging.network;

import java.util.logging.Level;

import org.junit.BeforeClass;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AbstractAgent;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateDown;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateFailed;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.NetworkLogManager;

public class NetworkLoggingTest {

	private static int NUMBER = 0;
	
	@BeforeClass
	public static void setUp() {
		NetworkLogManager.getLog().setLevel(Level.FINER);
	}
	
	protected NetworkLoggingAgent createNewAgent(double logicFrequency, int logsPerLogicCycle, int logicCycles) {
		IAgentId agentId = new AgentId("NetworkLoggingAgent-" + (++NUMBER));
		IAgentLogger agentLogger = new AgentLogger(agentId);
        agentLogger.setLevel(Level.ALL);
		return new NetworkLoggingAgent(agentId, agentLogger, logicFrequency, logsPerLogicCycle, logicCycles);
	}
	
	protected void failure(String failure) {
		System.out.println(failure);
		throw new RuntimeException(failure);
	}
	
	protected void checkAgent(AbstractAgent agent) {
		System.out.println("[INFO]  Checking agent " + agent.getName());
		
		if (agent.notInState(IAgentStateDown.class)) {
			failure("[ERROR] Agent " + agent.getComponentId().getToken() + " is still running!");
		}
		
		if (agent.inState(IAgentStateFailed.class)) {
			failure("[ERROR] Agent " + agent.getComponentId().getToken() + " failed!");			
		}	
		
		System.out.println("[OK]    " + agent.getName() + " OK!");
	}
	
	protected void checkLogClient(CheckNetworkLogClient logClient, int totalLogsOfOneCategory) {
		System.out.println("[INFO]  Checking network log client: " + logClient.getName());
		System.out.println("[INFO]  Total throughput: " + logClient.getThroughput().getCheckThroughput() + " bytes / s");
		
		
		if (logClient.getConnected().getFlag() == true) {
			logClient.stop();
			failure("[ERROR] Network log client still connected!");
		}
		
		if (logClient.getRunning().getFlag() == true) {
			logClient.stop();
			failure("[ERROR] Network log client still running!");
		}
		
		logClient.checkLogNumber(totalLogsOfOneCategory);
		System.out.println("[OK]    All log records received as anticipated (total " + (6*totalLogsOfOneCategory) + ")!");
		
		if (logClient.getThroughput().getCheckThroughput() < 500000) {
			failure("[ERROR] Throughput of the logging is " + logClient.getThroughput().getCheckThroughput() + " B / s < 0.5 MB/s !");
		}
		
		if (logClient.isException()) {
			System.out.println("[ERROR] Exception occured during reading logs: " + logClient.getException().getMessage());
			throw new RuntimeException("[ERROR] Exception occured during reading logs: " + logClient.getException().getMessage(), logClient.getException());
		}
		
		System.out.println("[OK]    Throughput is OK!");
		System.out.println("[OK]    " + logClient.getName() + " OK!");
	}
	
	
}
