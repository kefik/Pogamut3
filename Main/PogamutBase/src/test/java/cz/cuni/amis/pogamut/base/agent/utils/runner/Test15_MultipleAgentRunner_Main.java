package cz.cuni.amis.pogamut.base.agent.utils.runner;

import java.util.List;
import java.util.logging.Level;

import junit.framework.Assert;

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
import cz.cuni.amis.pogamut.base.agent.utils.runner.test.TestMultipleAgentRunner;

import cz.cuni.amis.tests.BaseTest;
				
public class Test15_MultipleAgentRunner_Main extends BaseTest {

	static final int NUM_AGENTS = 5;
	
	protected IMultipleAgentRunner initAgentRunner() {
		return new TestMultipleAgentRunner() {
			protected void postStartedHook(List<AbstractAgent> agents) {
				System.out.println("All agents have been started, stopping them...");
				for (AbstractAgent agent : agents) { 
					agent.stop();
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
		List<AbstractAgent> agents = runner.startAgents(initDescriptors(NUM_AGENTS));
		System.out.println("Checking agents classes...");		
		for (int i = 0; i < NUM_AGENTS; ++i) {
			Assert.assertTrue(agents.get(i) instanceof TestAgent1);
		}
		for (int i = NUM_AGENTS; i < 2*NUM_AGENTS; ++i) {
			Assert.assertTrue(agents.get(i) instanceof TestAgent2);
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
		
		System.out.println("Checking agents stopped...");
		for (int i = 0; i < 2*NUM_AGENTS; ++i) {
			Assert.assertTrue(agents.get(i).getState().getFlag().isState(IAgentStateDown.class));
		}
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
		Test15_MultipleAgentRunner_Main test = new Test15_MultipleAgentRunner_Main();
		test.test_start5Agents();
	}
	
}
