package cz.cuni.amis.pogamut.base.agent.utils.runner;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.impl.AbstractAgent;
import cz.cuni.amis.pogamut.base.agent.utils.runner.test.TestAgent1Module;
import cz.cuni.amis.pogamut.base.agent.utils.runner.test.TestAgentRunner;
import cz.cuni.amis.pogamut.base.factory.guice.GuiceAgentFactory;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.utils.exception.PogamutException;

import cz.cuni.amis.tests.BaseTest;
				
public class Test10_AgentRunner_Main_AgentFailure extends BaseTest {

	static final int NUM_AGENTS = 5;
	
	int postStartHookCalled = 0;
	
	protected IAgentRunner initAgentRunner() {
		return new TestAgentRunner(new GuiceAgentFactory(new TestAgent1Module())) {
			@Override
			protected void postStartHook(AbstractAgent agent) {
				super.postStartHook(agent);
				++postStartHookCalled;
				if (postStartHookCalled == NUM_AGENTS) {
					agent.kill();
				}
			};
		}.setMain(true).setLog(new LogCategory("TestAgent1Runner").addConsoleHandler());
	}
	
	@Test
	public void test_start5Agents() {		
		
		IAgentRunner runner = initAgentRunner();
		
		int NUM_AGENTS = 5;
		
		System.out.println("Starting " + NUM_AGENTS + " agents...");
		List<AbstractAgent> agents;
		boolean exception = false;
		try {
			agents = runner.startAgents(NUM_AGENTS);
		} catch (PogamutException e) {
			exception = true;
			System.out.println("PogamutException caught (as one of the agent has failed)...");
		}
		System.out.println("Checking whether the exception has been thrown...");
		Assert.assertTrue("No exception thrown due to agent failure!", exception);
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
		Test10_AgentRunner_Main_AgentFailure test = new Test10_AgentRunner_Main_AgentFailure();
		test.test_start5Agents();		
	}
	
}
