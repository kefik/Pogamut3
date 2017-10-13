package cz.cuni.amis.pogamut.multi.worldview;

import java.util.logging.Level;

import org.junit.Ignore;
import org.junit.Test;

import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentNotRunningException;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencyType;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.LifecycleBus;
import cz.cuni.amis.pogamut.base.component.stub.component.ComponentStub;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.multi.agent.impl.TeamedAgentId;
import cz.cuni.amis.pogamut.multi.communication.worldview.impl.BatchAwareLocalWorldView;
import cz.cuni.amis.pogamut.multi.communication.worldview.impl.BatchAwareSharedWorldView;
import cz.cuni.amis.pogamut.multi.utils.timekey.TimeKey;
import cz.cuni.amis.pogamut.multi.utils.timekey.TimeKeyManager;
import cz.cuni.amis.pogamut.multi.worldview.events.BatchBeginEventStub;
import cz.cuni.amis.pogamut.multi.worldview.events.BatchEndEventStub;
import cz.cuni.amis.pogamut.multi.worldview.stub.BatchAwareLocalWVStub;
import cz.cuni.amis.pogamut.multi.worldview.stub.BatchAwareSharedWVStub;
import cz.cuni.amis.tests.BaseTest;
import cz.cuni.amis.utils.ExceptionToString;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;
import cz.cuni.amis.utils.flag.Flag;

/**
 * NOT WORKING				
 * @author Jimmy
 */
@Ignore
public class Test06_BatchAwareWorldView_StoppingTest extends BaseTest {

	protected BatchAwareSharedWVStub createSWV() {
		return new BatchAwareSharedWVStub(log);
	}
	
	private class AgentLogicStub implements Runnable {

		private TeamedAgentId agentId;
		
		volatile private boolean shouldRun = true;
		volatile private Flag<Boolean> isRunning = new Flag<Boolean>(false);
		volatile private boolean failure = false;
		volatile private Throwable cause = null;
		
		private BatchAwareLocalWorldView localWV;

		public AgentLogicStub(BatchAwareLocalWorldView localWV) {
			agentId = (TeamedAgentId) localWV.getAgentId();
			this.localWV = localWV;
		}
		
		@Override
		public void run() {
			log.info(agentId + "-Logic: STARTING LOGIC");
			isRunning.setFlag(true);		
			try {
				while (true) {
					log.info(agentId + "-Logic: Locking worldview...");
					this.localWV.lock();
					log.info(agentId + "-Logic: Unlocking worldview...");
					this.localWV.unlock();
				}
			} catch (ComponentNotRunningException cnre) {
				// this marks the end of the agent
			} catch (Exception e) {
				cause = e;
				failure = true;
				log.severe(ExceptionToString.process(agentId + "-Logic: FAILURE.", e));				
			} finally {
				isRunning.setFlag(false);
			}
			log.info(agentId + "-Logic: END");
		}

	}
	
	@Test
	public void test() {
		log.info("Shared worldview...");
		BatchAwareSharedWorldView swv = createSWV();
		TeamedAgentId agentId = new TeamedAgentId("Agent" + 1, "RED");
		IAgentLogger logger = new AgentLogger(agentId);
		logger.setLevel(Level.ALL);
		logger.addDefaultConsoleHandler();
		LifecycleBus bus = new LifecycleBus(logger);
		ComponentStub starter = new ComponentStub(logger, bus);
		BatchAwareLocalWVStub agentWV = new BatchAwareLocalWVStub( new ComponentDependencies(ComponentDependencyType.STARTS_WITH).add(starter), bus, logger, swv, agentId);
		
		AgentLogicStub agent = new AgentLogicStub(agentWV);
		
		int iters = 500;
		int batches = 5;
		int time = 0;
		long batchLength = 5;
		
		Thread agentThread = null;
		
		try {
			for (int i = 0; i < iters; ++i) {
				log.info("---- ITERATION: " + (i+1) + " / " + iters + " ----");
				agentThread = new Thread(agent, "AgentThread");
				
				log.info("Starting agent...");
				starter.getController().manualStart("Start!");
				
				log.info("Starting agent logic thread...");
				agentThread.start();
				
				log.info("Waiting for the agent logic to start for 5 secs...");
				agent.isRunning.waitFor(5000, true);
				
				if (!agentWV.isRunning()) {
					throw new RuntimeException("BatchAwareLocalWorldView is NOT running!!!");
				}
				
				if (!agent.isRunning.getFlag()) {
					throw new RuntimeException("Agent logic thread is not running after start!!!");
				}
				
				log.info("Logic is running...");
				
				int nextEnd = time + batches;
				for (; time < nextEnd; ++time) {
					log.info("--- Batch " + (batches - (nextEnd - time - 1)) + " / " + batches);
					agentWV.notify( new BatchBeginEventStub(time));
					try {
						Thread.sleep(batchLength);
					} catch (InterruptedException e) {
						throw new PogamutInterruptedException(e, this);
					}
					agentWV.notify(new BatchEndEventStub(time));
				}
				
				log.info("Stopping agent...");
				starter.getController().manualStop("Stop!");
				
				log.info("Waiting for the agent logic to stop as well for 5 secs...");
				agent.isRunning.waitFor(5000, false);
				
				if (agentWV.isRunning()) {
					throw new RuntimeException("BatchAwareLocalWorldView is running!!!");
				}
				
				if (agent.isRunning.getFlag()) {
					throw new RuntimeException("Agent logic thread is STILL ALIVE after stop!!!");
				}
				
				if (agent.failure) {
					throw new RuntimeException("Agent has reported failure!!!");
				}
				
				log.info("Agent logic has stopped...");
			}
		} finally {
			try {
				starter.getController().manualKill("TERMINATION!");
				if (agentThread != null) {
					agentThread.interrupt();
				}
			} finally {
				try {
					TimeKeyManager.get().unlockAll();
				} catch (Exception e) {			
				}
				try {
					TimeKey.clear();
				} catch (Exception e) {			
				}
			}
		}
	}
	
	
}
