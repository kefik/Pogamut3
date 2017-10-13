package cz.cuni.amis.pogamut.base.agent.utils.runner;

import java.util.List;
import java.util.logging.Level;

import junit.framework.Assert;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.impl.AbstractAgent;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateDown;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateUp;
import cz.cuni.amis.pogamut.base.agent.utils.runner.test.TestAgent1;
import cz.cuni.amis.pogamut.base.agent.utils.runner.test.TestAgent1Module;
import cz.cuni.amis.pogamut.base.agent.utils.runner.test.TestAgentRunner;
import cz.cuni.amis.pogamut.base.factory.guice.GuiceAgentFactory;

import cz.cuni.amis.tests.BaseTest;
				
public class Test07_AgentRunner_Main_1Agent extends BaseTest {

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
	public void test_start1Agent() {		
		IAgentRunner runner = initAgentRunner();
		
		System.out.println("Starting 1 agent...");
		IAgent agent = runner.startAgent();
		System.out.println("Checking agents classes...");		
		Assert.assertTrue(agent instanceof TestAgent1);
		System.out.println("OK");	
		
		System.out.println("Checking agent stopped...");		
		Assert.assertTrue(agent.getState().getFlag().isState(IAgentStateDown.class));
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
		Test07_AgentRunner_Main_1Agent test = new Test07_AgentRunner_Main_1Agent();
		test.test_start1Agent();

	}
	
}
