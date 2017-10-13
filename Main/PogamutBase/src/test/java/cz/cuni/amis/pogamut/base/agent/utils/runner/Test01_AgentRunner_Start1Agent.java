package cz.cuni.amis.pogamut.base.agent.utils.runner;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateDown;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateUp;
import cz.cuni.amis.pogamut.base.agent.utils.runner.test.TestAgent1;
import cz.cuni.amis.pogamut.base.agent.utils.runner.test.TestAgent1Module;
import cz.cuni.amis.pogamut.base.agent.utils.runner.test.TestAgentRunner;
import cz.cuni.amis.pogamut.base.factory.guice.GuiceAgentFactory;
import cz.cuni.amis.pogamut.base.utils.Pogamut;

import cz.cuni.amis.tests.BaseTest;
				
public class Test01_AgentRunner_Start1Agent extends BaseTest {

	@AfterClass
	public static void tearDown() {
		Pogamut.getPlatform().close();
	}
	
	@Test
	public void test_start1Agent() {		
		TestAgentRunner runner = new TestAgentRunner(new GuiceAgentFactory(new TestAgent1Module()));
		
		System.out.println("Starting 1 agent...");		
		TestAgent1 agent = (TestAgent1) runner.startAgent();
		
		System.out.println("Checking agent running...");		
		Assert.assertTrue(agent.getState().getFlag().isState(IAgentStateUp.class));
		System.out.println("OK");
		
		System.out.println("Stopping agent...");
		agent.stop();
		System.out.println("Checking agent stopped...");
		Assert.assertTrue(agent.getState().getFlag().isState(IAgentStateDown.class));
		System.out.println("OK");
				
		
		System.out.println("---/// TEST OK ///---");
	}

	/**
	 * Shortcut for running the test as classic Java program.
	 * @param args
	 */
	public static void main(String[] args) {
		Test01_AgentRunner_Start1Agent test = new Test01_AgentRunner_Start1Agent();
		test.test_start1Agent();
		Test01_AgentRunner_Start1Agent.tearDown();
	}
	
}
