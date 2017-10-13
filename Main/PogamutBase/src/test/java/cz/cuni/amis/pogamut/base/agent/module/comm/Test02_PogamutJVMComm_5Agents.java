package cz.cuni.amis.pogamut.base.agent.module.comm;

import org.junit.AfterClass;
import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateDown;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.tests.BaseTest;

public class Test02_PogamutJVMComm_5Agents extends CommTest {
	
	@AfterClass
	public static void afterClass() {
		Pogamut.getPlatform().close();
	}
	
	private void innerImpl() {
		int agentCount = 5;
		CommTestAgent[] agents = new CommTestAgent[agentCount];
		for (int i = 0; i < agents.length; ++i) {
			agents[i] = new CommTestAgent(i, (i == agents.length-1 ? 0 : i+1));
			agents[i].start();
		}

		log.info("INITIATING COMMUNICATION");
		PogamutJVMComm.getInstance().send(new CommTestEvent(0), 0);
		
		log.info("Waiting for agents to finish...");
		for (int i = 0; i < agents.length; ++i) {
			agents[i].awaitState(IAgentStateDown.class);
			if (agents[i].getTestState() != 10) {
				testFailed("Agent" + (i+1) + " did not reached state 10.");
			}
		}	
		
		log.info("Nulling agent pointers...");
		for (int i = 0; i < agents.length; ++i) {
			agents[i] = null;
		}
		agents = null;
		
		checkInstanceCount(60);
	}
	
	@Test
	public void test1() {
		innerImpl();		
		testOk();
	}
	
	@Test
	public void test2() {
		for (int j = 0; j < 5; ++j) {
			log.info("TEST " + (j+1) + " / 5");
			
			innerImpl();
		}
		
		testOk();
	}

}
