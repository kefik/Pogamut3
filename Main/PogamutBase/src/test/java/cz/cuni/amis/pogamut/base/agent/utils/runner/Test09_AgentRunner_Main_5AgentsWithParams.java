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
				
public class Test09_AgentRunner_Main_5AgentsWithParams extends BaseTest {

	protected IAgentRunner initAgentRunner() {
		return new TestAgentRunner(new GuiceAgentFactory(new TestAgent1Module())) {
			protected void postStartedHook(List<AbstractAgent> agents) {
				System.out.println("All agents have been started, stopping them...");
				for (AbstractAgent agent : agents) { 
					agent.stop();
				}
			};
		}.setMain(true);
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
		
		System.out.println("Check agents' parameters...");
		for (int i = 0; i < NUM_AGENTS; ++i) {
			Assert.assertTrue(((TestAgent1)agents.get(i)).getParam() == i);
		}
		System.out.println("OK");
		
		System.out.println("Checking agents stopped...");
		for (int i = 0; i < NUM_AGENTS; ++i) {
			Assert.assertTrue(agents.get(i).getState().getFlag().isState(IAgentStateDown.class));
		}
		System.out.println("OK");
						
		System.out.println("---/// TEST OK ///---");
		System.out.println("");
		System.out.println("IF THE TEST DOES NOT TERMINATE ITSELF AFTER THIS POINT - IT MEANS THAT 'main' FEATURE OF THE IAgentRunner IS BROKEN!!!");
	}

	/**
	 * Shortcut for running the test as classic Java program.
	 * @param args
	 */
	public static void main(String[] args) {
		Test09_AgentRunner_Main_5AgentsWithParams test = new Test09_AgentRunner_Main_5AgentsWithParams();
		test.test_start5AgentsWithDifferentParams();
	}
	
}
