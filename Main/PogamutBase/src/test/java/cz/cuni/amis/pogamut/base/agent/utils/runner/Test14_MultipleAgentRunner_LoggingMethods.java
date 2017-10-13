package cz.cuni.amis.pogamut.base.agent.utils.runner;

import java.util.List;
import java.util.logging.Level;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.impl.AbstractAgent;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateDown;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateUp;
import cz.cuni.amis.pogamut.base.agent.utils.runner.impl.AgentDescriptor;
import cz.cuni.amis.pogamut.base.agent.utils.runner.test.TestAgent1;
import cz.cuni.amis.pogamut.base.agent.utils.runner.test.TestAgent1Module;
import cz.cuni.amis.pogamut.base.agent.utils.runner.test.TestAgent2;
import cz.cuni.amis.pogamut.base.agent.utils.runner.test.TestAgent2Module;
import cz.cuni.amis.pogamut.base.agent.utils.runner.test.TestAgentParams;
import cz.cuni.amis.pogamut.base.agent.utils.runner.test.TestAgentRunner;
import cz.cuni.amis.pogamut.base.agent.utils.runner.test.TestMultipleAgentRunner;
import cz.cuni.amis.pogamut.base.factory.guice.GuiceAgentFactory;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;

import cz.cuni.amis.tests.BaseTest;
				
public class Test14_MultipleAgentRunner_LoggingMethods extends BaseTest {

	@AfterClass
	public static void tearDown() {
		Pogamut.getPlatform().close();
	}
	
	static final int NUM_AGENTS = 5;
	
	protected IMultipleAgentRunner initAgentRunner() {
		return new TestMultipleAgentRunner().setConsoleLogging(true).setLogLevel(Level.ALL).setConsoleLogging(true).setLog(new LogCategory("TestAgent1Runner").addConsoleHandler());
	}
	
	public IAgentDescriptor[] initDescriptors(int numAgents) {
		IAgentDescriptor[] result = new IAgentDescriptor[2];
		AgentDescriptor desc;
		
		// TestAgent1
		desc = new AgentDescriptor();		
		desc.setAgentModule(new TestAgent1Module());
		for (int i = 0; i < numAgents; ++i) {
			desc.addParams(new TestAgentParams(i));
		}		
		result[0] = desc;
		
		// TestAgent2
		desc = new AgentDescriptor();		
		desc.setAgentModule(new TestAgent2Module());
		for (int i = 0; i < numAgents; ++i) {
			desc.addParams(new TestAgentParams(i));
		}		
		result[1] = desc;
		
		return result;
	}
	
	@Test
	public void test_start5Agents() {		
		
		IMultipleAgentRunner runner = initAgentRunner();
		
		System.out.println("Starting " + NUM_AGENTS + " agents...");
		List<AbstractAgent> agents = runner.startAgents(initDescriptors(NUM_AGENTS));
		System.out.println("Checking agents classes...");		
		for (int i = 0; i < NUM_AGENTS; ++i) {
			Assert.assertTrue(agents.get(i) instanceof TestAgent1);
		}
		for (int i = NUM_AGENTS; i < 2*NUM_AGENTS; ++i) {
			Assert.assertTrue(agents.get(i) instanceof TestAgent2);
		}
		System.out.println("OK");
		
		System.out.println("Checking agents running...");
		for (int i = 0; i < 2*NUM_AGENTS; ++i) {
			Assert.assertTrue(agents.get(i).getState().getFlag().isState(IAgentStateUp.class));
		}
		System.out.println("OK");
		
		System.out.println("Checking agents' log levels...");
		for (int i = 0; i < 2*NUM_AGENTS; ++i) {
			Assert.assertTrue(agents.get(i).getLogger().getCategories().values().iterator().next().getLevel().equals(Level.ALL));
		}		
		System.out.println("OK");
		
		System.out.println("Check agents' parameters...");
		for (int i = 0; i < NUM_AGENTS; ++i) {
			Assert.assertTrue(((TestAgent1)agents.get(i)).getParam() == i);
		}
		for (int i = NUM_AGENTS; i < 2*NUM_AGENTS; ++i) {
			Assert.assertTrue(((TestAgent2)agents.get(i)).getParam() == i-NUM_AGENTS);
		}
		System.out.println("OK");
		
		System.out.println("Stopping " + (2*NUM_AGENTS) + " agents...");
		for (int i = 0; i < 2*NUM_AGENTS; ++i) {
			agents.get(i).stop();
		}
		System.out.println("Checking agents stopped...");
		for (int i = 0; i < 2*NUM_AGENTS; ++i) {
			Assert.assertTrue(agents.get(i).getState().getFlag().isState(IAgentStateDown.class));
		}
		System.out.println("OK");
				
		
		System.out.println("---/// TEST OK ///---");
	}

	/**
	 * Shortcut for running the test as classic Java program.
	 * @param args
	 */
	public static void main(String[] args) {
		Test14_MultipleAgentRunner_LoggingMethods test = new Test14_MultipleAgentRunner_LoggingMethods();
		test.test_start5Agents();
		Test14_MultipleAgentRunner_LoggingMethods.tearDown();
	}
	
}
