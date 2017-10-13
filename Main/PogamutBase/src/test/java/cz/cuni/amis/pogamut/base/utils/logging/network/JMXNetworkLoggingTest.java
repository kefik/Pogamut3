package cz.cuni.amis.pogamut.base.utils.logging.network;

import java.util.logging.Level;

import org.junit.BeforeClass;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateDown;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateFailed;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.NetworkLogManager;

public class JMXNetworkLoggingTest extends NetworkLoggingTest {

	private static int NUMBER = 0;
	
	protected JMXNetworkLoggingAgent createNewAgentJMX(double logicFrequency, int logsPerLogicCycle, int logicCycles) {
		IAgentId agentId = new AgentId("JMXNetworkLoggingAgent-" + (++NUMBER));
		IAgentLogger agentLogger = new AgentLogger(agentId);
        agentLogger.setLevel(Level.ALL);
		return new JMXNetworkLoggingAgent(agentId, agentLogger, logicFrequency, logsPerLogicCycle, logicCycles);
	}
		
}
