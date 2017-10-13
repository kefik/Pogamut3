package cz.cuni.amis.pogamut.base.agent.utils.runner;

import java.util.List;

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
import cz.cuni.amis.utils.exception.PogamutException;

import cz.cuni.amis.tests.BaseTest;
				
public class Test04_AgentRunner_UtilityMethods extends BaseTest {

	@AfterClass
	public static void tearDown() {
		Pogamut.getPlatform().close();
	}
	
	protected int preInitHookCalled = 0;
	protected int preStartHookCalled = 0;
	protected int postStartHookCalled = 0;
	protected int postStartedHookCalled = 0;
	
	protected TestAgentRunner initAgentRunner() {
		preInitHookCalled = 0;
		preStartHookCalled = 0;
		postStartHookCalled = 0;
		postStartedHookCalled = 0;
		return new TestAgentRunner(new GuiceAgentFactory(new TestAgent1Module())) {
			@Override
			protected void preInitHook() throws PogamutException {
				super.preInitHook();
				if (preInitHookCalled != 0) throw new RuntimeException("preInitHook() called repeatedly!");
				if (preStartHookCalled != 0) throw new RuntimeException("preInitHook() called after some preStartHook were called!");
				if (postStartHookCalled != 0) throw new RuntimeException("preInitHook() called after some postStartHook were called!");
				if (postStartedHookCalled != 0) throw new RuntimeException("preInitHook() called after postStartedHookCalled was called!");				
				++preInitHookCalled;
			}
			@Override
			protected void preStartHook(AbstractAgent agent) throws PogamutException {
				super.preStartHook(agent);
				if (agent == null) throw new RuntimeException("preStartHook called with param agent==null!");
				if (preInitHookCalled == 0) throw new RuntimeException("preStartHook() called but no preInitHook was called!");
				if (preInitHookCalled != 1) throw new RuntimeException("preStartHook() called when more than one preInitHook was called!");
				if (postStartedHookCalled != 0) throw new RuntimeException("preStartHook() called after postStartedHookCalled was called!");
				++preStartHookCalled;
			}
			@Override
			protected void postStartHook(AbstractAgent agent) throws PogamutException {
				super.postStartHook(agent);
				++postStartHookCalled;
				if (agent == null) throw new RuntimeException("postStartHook called with param agent==null!");
				if (preInitHookCalled == 0) throw new RuntimeException("postStartHook() called but no preInitHook was called!");
				if (preInitHookCalled != 1) throw new RuntimeException("postStartHook() called when more than one preInitHook was called!");
				if (preStartHookCalled != postStartHookCalled) throw new RuntimeException("preStartHook num. of calls != postStartHook num of calls!");
				if (postStartedHookCalled != 0) throw new RuntimeException("postStartHook() called after postStartedHookCalled was called!");
			}
			@Override
			protected void postStartedHook(List<AbstractAgent> agents) {
				super.postStartedHook(agents);
				for (int i = 0; i < agents.size(); ++i) {
					if (agents.get(i) == null) throw new RuntimeException("postStartedHook called with param agents[" + i + "] == null!");
					if (preInitHookCalled == 0) throw new RuntimeException("postStartedHook() called but no preInitHook was called!");
					if (preInitHookCalled != 1) throw new RuntimeException("postStartedHook() called when more than one preInitHook was called!");
					if (postStartedHookCalled != 0) throw new RuntimeException("postStartedHook() called repeatedly!");
					if (preStartHookCalled != postStartHookCalled) throw new RuntimeException("preStartHook num. of calls != postStartHook num of calls!");					
				}
				++postStartedHookCalled;
			}			
		};
	}
	
	protected void checkAgentRunnerCalls(int numAgents) {
		System.out.println("Checking agent runner calls...");
		if (preInitHookCalled == 0) throw new RuntimeException("preInitHook() was not called at all!");
		if (preInitHookCalled != 1) throw new RuntimeException("preInitHook() was called more than once!");
		if (preStartHookCalled != numAgents) throw new RuntimeException(numAgents + " started, but preStartHook was called only " + preStartHookCalled +"x!");
		if (postStartHookCalled != numAgents) throw new RuntimeException(numAgents + " started, but postStartHook was called only " + postStartHookCalled +"x!");
		if (preStartHookCalled != postStartHookCalled) throw new RuntimeException("preStartHook num. of calls != postStartHook num of calls!");
		if (postStartedHookCalled == 0) throw new RuntimeException("postStartedHook() was not called at all!");
		if (postStartedHookCalled != 1) throw new RuntimeException("postStartedHook() was called more than once!");
		System.out.println("OK");
	}
	
	@Test
	public void test_start1Agent() {		
		IAgentRunner runner = initAgentRunner();
		
		System.out.println("Starting 1 agent...");
		IAgent agent = runner.startAgent();
		System.out.println("Checking agent class...");		
		Assert.assertTrue(agent instanceof TestAgent1);
		System.out.println("OK");
		
		System.out.println("Checking agent running...");
		Assert.assertTrue(agent.getState().getFlag().isState(IAgentStateUp.class));
		System.out.println("OK");
			
		System.out.println("Stopping 1 agent...");
		agent.stop();
		System.out.println("Checking agent stopped...");
		Assert.assertTrue(agent.getState().getFlag().isState(IAgentStateDown.class));
		System.out.println("OK");
				
		checkAgentRunnerCalls(1);
		
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
		
		System.out.println("Stopping " + NUM_AGENTS + " agents...");
		for (int i = 0; i < NUM_AGENTS; ++i) {
			agents.get(i).stop();
		}
		System.out.println("Checking agents stopped...");
		for (int i = 0; i < NUM_AGENTS; ++i) {
			Assert.assertTrue(agents.get(i).getState().getFlag().isState(IAgentStateDown.class));
		}
		System.out.println("OK");
				
		checkAgentRunnerCalls(NUM_AGENTS);
		
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
				
		System.out.println("Stopping " + NUM_AGENTS + " agents...");
		for (int i = 0; i < NUM_AGENTS; ++i) {
			agents.get(i).stop();
		}		
		System.out.println("Checking agents stopped...");
		for (int i = 0; i < NUM_AGENTS; ++i) {
			Assert.assertTrue(agents.get(i).getState().getFlag().isState(IAgentStateDown.class));
		}
		System.out.println("OK");
		
		checkAgentRunnerCalls(NUM_AGENTS);
		
		System.out.println("---/// TEST OK ///---");
	}

	/**
	 * Shortcut for running the test as classic Java program.
	 * @param args
	 */
	public static void main(String[] args) {
		Test04_AgentRunner_UtilityMethods test = new Test04_AgentRunner_UtilityMethods();
		test.test_start1Agent();
		test.test_start5Agents();
		test.test_start5AgentsWithDifferentParams();
		Test04_AgentRunner_UtilityMethods.tearDown();
	}
	
}
