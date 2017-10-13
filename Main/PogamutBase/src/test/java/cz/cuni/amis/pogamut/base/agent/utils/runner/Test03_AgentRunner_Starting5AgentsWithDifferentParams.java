package cz.cuni.amis.pogamut.base.agent.utils.runner;

import java.util.List;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.impl.AbstractAgent;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateDown;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateUp;
import cz.cuni.amis.pogamut.base.agent.utils.runner.test.TestAgent1;
import cz.cuni.amis.pogamut.base.agent.utils.runner.test.TestAgent1Module;
import cz.cuni.amis.pogamut.base.agent.utils.runner.test.TestAgentParams;
import cz.cuni.amis.pogamut.base.agent.utils.runner.test.TestAgentRunner;
import cz.cuni.amis.pogamut.base.factory.guice.GuiceAgentFactory;
import cz.cuni.amis.pogamut.base.utils.Pogamut;

import cz.cuni.amis.tests.BaseTest;
				
public class Test03_AgentRunner_Starting5AgentsWithDifferentParams extends BaseTest {

	@AfterClass
	public static void tearDown() {
		Pogamut.getPlatform().close();
	}
	
	static final int NUM_AGENTS = 5; 
	
	@Test
	public void test_start5AgentsWithDifferentParams() {		
		
		TestAgentRunner runner = new TestAgentRunner(new GuiceAgentFactory(new TestAgent1Module()));
		
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
		Test03_AgentRunner_Starting5AgentsWithDifferentParams test = new Test03_AgentRunner_Starting5AgentsWithDifferentParams();
		test.test_start5AgentsWithDifferentParams();
		Test03_AgentRunner_Starting5AgentsWithDifferentParams.tearDown();
	}
	
}
