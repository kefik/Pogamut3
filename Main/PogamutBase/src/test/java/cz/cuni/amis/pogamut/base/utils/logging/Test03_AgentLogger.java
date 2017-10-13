package cz.cuni.amis.pogamut.base.utils.logging;

import java.util.logging.Level;

import junit.framework.Assert;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.utils.logging.stub.CheckPublisher;

import cz.cuni.amis.tests.BaseTest;
				
public class Test03_AgentLogger extends BaseTest {
	
	String[] lines = new String[] {
		"Log level set to ALL.", "Hello", "Hi!", "I'm", "Jimmy.", "I am Ruda", "Bye. Bye."
	};
	
	@Test
	public void test01() {
		IAgentId agentId = new AgentId("testId");
		IAgentLogger logger = new AgentLogger(agentId);
		
		LogCategory log1 = logger.getCategory("log1");
		CheckPublisher publisher = new CheckPublisher(lines);
		logger.addDefaultPublisher(publisher);
		logger.setLevel(Level.ALL);
		LogCategory log2 = logger.getCategory("log1");
		
		Assert.assertTrue("log1 should be the same as log2, they have the same name", log1 == log2);
		
		for (int i = 1; i < lines.length; ++i) {
			if (i % 2 == 0) {
				if (log1.isLoggable(Level.FINEST)) log1.finest(lines[i]);
			} else {
				if (log2.isLoggable(Level.FINEST)) log2.finest(lines[i]);
			}
		}
		
		publisher.checkExpectEmpty();
		
		System.out.println("---/// TEST OK ///---");
	}
	
	public static void main(String[] args) {
		Test03_AgentLogger test = new Test03_AgentLogger();
		test.test01();
	}

}
