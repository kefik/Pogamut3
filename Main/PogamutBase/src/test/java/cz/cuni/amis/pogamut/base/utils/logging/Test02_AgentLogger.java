package cz.cuni.amis.pogamut.base.utils.logging;

import java.util.logging.Level;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.utils.logging.stub.CheckPublisher;

import cz.cuni.amis.tests.BaseTest;
				
public class Test02_AgentLogger extends BaseTest {
	
	String[] lines = new String[] {
		"Log level set to ALL.", "Log level set to ALL.", "Hello", "Hello", "Hi!", "Hi!", "I'm", "I'm", "Jimmy.", "Jimmy.", "I am Ruda", "I am Ruda", "Bye. Bye.",  "Bye. Bye."
	};
	
	@Test
	public void test01() {
		IAgentId agentId = new AgentId("testId");
		IAgentLogger logger = new AgentLogger(agentId);
		
		LogCategory log1 = logger.getCategory("log1");
		CheckPublisher publisher = new CheckPublisher(lines);
		logger.addDefaultPublisher(publisher);
		logger.setLevel(Level.ALL);
		LogCategory log2 = logger.getCategory("log2");
		
		for (int i = 1; 2*i+1 < lines.length; ++i) {
			if (log1.isLoggable(Level.FINEST)) log1.finest(lines[2*i]);
			if (log2.isLoggable(Level.FINEST)) log2.finest(lines[2*i+1]);
		}
		
		publisher.checkExpectEmpty();
		
		System.out.println("---/// TEST OK ///---");
	}
	
	public static void main(String[] args) {
		Test02_AgentLogger test = new Test02_AgentLogger();
		test.test01();
	}

}
