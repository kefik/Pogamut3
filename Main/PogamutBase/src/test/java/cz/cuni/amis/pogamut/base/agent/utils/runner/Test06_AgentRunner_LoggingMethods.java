package cz.cuni.amis.pogamut.base.agent.utils.runner;

import java.util.List;
import java.util.logging.Level;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.impl.AbstractAgent;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateDown;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateUp;
import cz.cuni.amis.pogamut.base.agent.utils.runner.test.TestAgent1;
import cz.cuni.amis.pogamut.base.agent.utils.runner.test.TestAgent1Module;
import cz.cuni.amis.pogamut.base.agent.utils.runner.test.TestAgentParams;
import cz.cuni.amis.pogamut.base.agent.utils.runner.test.TestAgentRunner;
import cz.cuni.amis.pogamut.base.factory.guice.GuiceAgentFactory;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;

import cz.cuni.amis.tests.BaseTest;
				
public class Test06_AgentRunner_LoggingMethods extends BaseTest {

	@AfterClass
	public static void tearDown() {
		Pogamut.getPlatform().close();
	}
	
	protected IAgentRunner initAgentRunner() {
		return new TestAgentRunner(new GuiceAgentFactory(new TestAgent1Module())).setConsoleLogging(true).setLogLevel(Level.ALL).setConsoleLogging(true).setLog(new LogCategory("TestAgent1Runner").addConsoleHandler());
	}
		
	@Test
	public void test_start1Agent() {		
		IAgentRunner runner = initAgentRunner();
		
		System.out.println("Starting 1 agent...");
		IAgent agent = runner.startAgent();
		System.out.println("Checking agents classes...");		
		Assert.assertTrue(agent instanceof TestAgent1);
		System.out.println("OK");
		
		System.out.println("Checking agents running...");
		Assert.assertTrue(agent.getState().getFlag().isState(IAgentStateUp.class));
		System.out.println("OK");
		
		System.out.println("Checking agent log level...");
		Assert.assertTrue(agent.getLogger().getCategories().values().iterator().next().getLevel().equals(Level.ALL));
		System.out.println("OK");		

		System.out.println("Stopping 1 agent...");
		agent.stop();
		System.out.println("Checking agent stopped...");
		Assert.assertTrue(agent.getState().getFlag().isState(IAgentStateDown.class));
		System.out.println("OK");
		
		System.out.println("---/// TEST OK ///---");
	}
	
	@Test
	public void test_start5Agents() {		
		
		IAgentRunner runner = initAgentRunner();
		
		int NUM_AGENTS = 5;
		
		System.out.println("Starting " + NUM_AGENTS + " agents...");
		List<AbstractAgent> agents = runner.startAgents(NUM_AGENTS);
		System.out.println("Checking agents classes...");		
		for (int i = 0; i < NUM_AGENTS; ++i) {
			Assert.assertTrue(agents.get(i) instanceof TestAgent1);
		}
		System.out.println("OK");
		
		System.out.println("Checking agents running...");
		for (int i = 0; i < NUM_AGENTS; ++i) {
			Assert.assertTrue(agents.get(i).getState().getFlag().isState(IAgentStateUp.class));
		}
		System.out.println("OK");
		
		System.out.println("Checking agents' log levels...");
		for (int i = 0; i < NUM_AGENTS; ++i) {
			Assert.assertTrue(agents.get(i).getLogger().getCategories().values().iterator().next().getLevel().equals(Level.ALL));
		}
		System.out.println("OK");
		
		System.out.println("Stopping " + NUM_AGENTS + " agents...");
		for (int i = 0; i < NUM_AGENTS; ++i) {
			agents.get(i).stop();
		}
		System.out.println("Checking agents stopped...");
		for (int i = 0; i < NUM_AGENTS; ++i) {
			Assert.assertTrue(agents.get(i).getState().getFlag().isState(IAgentStateDown.class));
		}
		System.out.println("OK");
				
		System.out.println("---/// TEST OK ///---");
	}
	
	@Test
	public void test_start5AgentsWithDifferentParams() {		
		
		IAgentRunner runner = initAgentRunner();
		
		int NUM_AGENTS = 5;
		
		System.out.println("Starting " + NUM_AGENTS + " agents with different parameters...");
		
		TestAgentParams[] params = new TestAgentParams[NUM_AGENTS];
		for (int i = 0; i < NUM_AGENTS; ++i) {
			params[i] = new TestAgentParams(i);
		}		
		List<AbstractAgent> agents = runner.startAgents(params);		
		System.out.println("Checking agents classes...");		
		for (int i = 0; i < NUM_AGENTS; ++i) {
			Assert.assertTrue(agents.get(i) instanceof TestAgent1);
		}
		System.out.println("OK");
		
		System.out.println("Checking agents running...");
		for (int i = 0; i < NUM_AGENTS; ++i) {
			Assert.assertTrue(agents.get(i).getState().getFlag().isState(IAgentStateUp.class));
		}
		System.out.println("OK");
		
		System.out.println("Check agents' parameters...");
		for (int i = 0; i < NUM_AGENTS; ++i) {
			Assert.assertTrue(((TestAgent1)agents.get(i)).getParam() == i);
		}
		System.out.println("OK");
		
		System.out.println("Checking agents' log levels...");
		for (int i = 0; i < NUM_AGENTS; ++i) {
			Assert.assertTrue(agents.get(i).getLogger().getCategories().values().iterator().next().getLevel().equals(Level.ALL));
		}
		System.out.println("OK");
		
		System.out.println("Stopping " + NUM_AGENTS + " agents...");
		for (int i = 0; i < NUM_AGENTS; ++i) {
			agents.get(i).stop();
		}		
		System.out.println("Checking agents stopped...");
		for (int i = 0; i < NUM_AGENTS; ++i) {
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
		Test06_AgentRunner_LoggingMethods test = new Test06_AgentRunner_LoggingMethods();
		test.test_start1Agent();
		test.test_start5Agents();
		test.test_start5AgentsWithDifferentParams();
		Test06_AgentRunner_LoggingMethods.tearDown();
	}
	
}
