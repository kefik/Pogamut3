package cz.cuni.amis.pogamut.base.agent.module.comm;

import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateDown;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.tests.BaseTest;

@Ignore
public class ParallelTest extends CommTest {

	@AfterClass
	public static void afterClass() {
		Pogamut.getPlatform().close();
	}
		
	protected void innerImpl(int EVENTS_COUNT, int AGENTS_COUNT, int CHANNEL) {
		CommTestParallelAgent[] agents = new CommTestParallelAgent[AGENTS_COUNT];
		for (int i = 0; i < agents.length; ++i) {
			agents[i] = new CommTestParallelAgent(CHANNEL, EVENTS_COUNT, AGENTS_COUNT);
			agents[i].start();
		}

		log.info("INITIATING COMMUNICATION");
		PogamutJVMComm.getInstance().broadcast(new CommTestParallelStartEvent());
		
		try {
			for (int i = 0; i < agents.length; ++i) {
				agents[i].getAllEventsReceived().waitFor(1000 * AGENTS_COUNT * EVENTS_COUNT, true);
				//agents[i].getAllEventsReceived().waitFor(true);
			}
			try {
				log.info("Waiting for 200ms in order to let possible-error-event-propagation to happen.");
				Thread.sleep(200);
			} catch (InterruptedException e) {
			}
			log.info("STOPPING AGENTS...");
			for (int i = 0; i < agents.length; ++i) {
				agents[i].stop();
			}
			log.info("CHECKING AGENTS...");
			for (int i = 0; i < agents.length; ++i) {
				if (agents[i].getTotalEvents() != agents[i].getEventsCount()) {
					testFailed("Agent" + (i+1) + " did not received correct number of events. Expected: " + agents[i].getTotalEvents() + ", got: " + agents[i].getEventsCount() + ".");
				}
				if (agents[i].getTotalEventsFromMe() != agents[i].getEventCountFromMe()) {
					testFailed("Agent" + (i+1) + " did not received correct number of events from itself. Expected: " + agents[i].getTotalEventsFromMe() + ", got: " + agents[i].getEventCountFromMe() + ".");
				}
				log.info("Agent " + (i+1) + " / " + AGENTS_COUNT + " received all events OK.");
			}

			log.info("NULLING AGENT POINTERS...");
			for (int i = 0; i < agents.length; ++i) {
				agents[i] = null;
			}	
			agents = null;
			
			checkInstanceCount(12 * AGENTS_COUNT);
			
			log.info("ALL AGENTS OK!");
		} finally {
			if (agents != null) {
				for (int i = 0; i < agents.length; ++i) {
					if (agents[i].notInState(IAgentStateDown.class)) {
						try {
							agents[i].kill();
						} catch (Exception e) {						
						}
					}
				}
			}
		}
	}
	
}
