package cz.cuni.amis.pogamut.base.agent.utils.runner;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.impl.AbstractAgent;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateDown;
import cz.cuni.amis.pogamut.base.agent.utils.runner.impl.AgentDescriptor;
import cz.cuni.amis.pogamut.base.agent.utils.runner.test.TestAgent1;
import cz.cuni.amis.pogamut.base.agent.utils.runner.test.TestAgent1Module;
import cz.cuni.amis.pogamut.base.agent.utils.runner.test.TestAgent2;
import cz.cuni.amis.pogamut.base.agent.utils.runner.test.TestAgent2Module;
import cz.cuni.amis.pogamut.base.agent.utils.runner.test.TestAgentParams;
import cz.cuni.amis.pogamut.base.agent.utils.runner.test.TestMultipleAgentRunner;
import cz.cuni.amis.utils.exception.PogamutException;

import cz.cuni.amis.tests.BaseTest;
				
public class Test16_MultipleAgentRunner_Main_AgentFailure extends BaseTest {

	static final int NUM_AGENTS = 5;
	
	protected IMultipleAgentRunner initAgentRunner() {
		return new TestMultipleAgentRunner() {
			private int postStartHookCalled;

			@Override
			protected void postStartHook(AbstractAgent agent) {
				super.postStartHook(agent);
				++postStartHookCalled;
				if (postStartHookCalled == 2*NUM_AGENTS) {
					agent.kill();
				}
			};	
		}.setMain(true);
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
		List<AbstractAgent> agents;
		boolean exception = false;
		try {
			agents = runner.startAgents(initDescriptors(NUM_AGENTS));
		} catch (PogamutException e) {
			exception = true;
			System.out.println("PogamutException caught (as one of the agent has failed)...");
		}
		System.out.println("Checking whether the exception has been thrown...");
		Assert.assertTrue("No exception thrown due to agent failure!", exception);
		System.out.println("OK");
				
		
		System.out.println("---/// TEST OK ///---");
		System.out.println("");
		System.out.println("IF THE TEST DOES NOT TERMINATE ITSELF AFTER THIS POINT - IT MEANS THAT 'main' FEATURE OF THE IMultipleAgentRunner IS BROKEN!!!");
	}

	/**
	 * Shortcut for running the test as classic Java program.
	 * @param args
	 */
	public static void main(String[] args) {
		Test16_MultipleAgentRunner_Main_AgentFailure test = new Test16_MultipleAgentRunner_Main_AgentFailure();
		test.test_start5Agents();
	}
	
}
