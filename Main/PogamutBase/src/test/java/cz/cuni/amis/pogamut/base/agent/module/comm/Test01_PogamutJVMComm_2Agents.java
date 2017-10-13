package cz.cuni.amis.pogamut.base.agent.module.comm;

import org.junit.AfterClass;
import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateDown;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.tests.BaseTest;

public class Test01_PogamutJVMComm_2Agents extends CommTest {
	
	@AfterClass
	public static void afterClass() {
		Pogamut.getPlatform().close();
	}
	
	private void innerImpl() {
		CommTestAgent agent1 = new CommTestAgent(0, 1);
		CommTestAgent agent2 = new CommTestAgent(1, 0);
	
		agent1.start();
		agent2.start();
		
		log.info("INITIATING COMMUNICATION");
		PogamutJVMComm.getInstance().send(new CommTestEvent(0), 0);
		
		log.info("Waiting for agents to finish...");
		agent1.awaitState(IAgentStateDown.class);
		agent2.awaitState(IAgentStateDown.class);
		
		if (agent1.getTestState() != 10) {
			testFailed("Agent1 did not reached state 10");
		}
		if (agent2.getTestState() != 10) {
			testFailed("Agent2 did not reached state 10");
		}
		
		log.info("Nulling agent pointers...");
		agent1 = null;
		agent2 = null;
		
		checkInstanceCount(60);
	}	

	@Test
	public void test1() {
		innerImpl();
		
		testOk();
	}
	
	@Test
	public void test2() {
		for (int i = 0; i < 5; ++i) {
			log.info("TEST " + (i+1) + " / 5");
			
			innerImpl();
		}
		
		testOk();
	}

}
