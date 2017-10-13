package cz.cuni.amis.pogamut.multi.worldview;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Ignore;
import org.junit.Test;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentNotRunningException;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencyType;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.LifecycleBus;
import cz.cuni.amis.pogamut.base.component.stub.component.ComponentStub;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.multi.agent.ITeamedAgentId;
import cz.cuni.amis.pogamut.multi.agent.impl.TeamedAgentId;
import cz.cuni.amis.pogamut.multi.communication.worldview.ILocalWorldView;
import cz.cuni.amis.pogamut.multi.communication.worldview.impl.BatchAwareLocalWorldView;
import cz.cuni.amis.pogamut.multi.communication.worldview.impl.BatchAwareSharedWorldView;
import cz.cuni.amis.pogamut.multi.utils.timekey.TimeKey;
import cz.cuni.amis.pogamut.multi.utils.timekey.TimeKeyManager;
import cz.cuni.amis.pogamut.multi.worldview.events.BatchBeginEventStub;
import cz.cuni.amis.pogamut.multi.worldview.events.BatchEndEventStub;
import cz.cuni.amis.pogamut.multi.worldview.objects.CheckInstances;
import cz.cuni.amis.pogamut.multi.worldview.objects.TestCompositeObject;
import cz.cuni.amis.pogamut.multi.worldview.objects.TestCompositeObjectMessage;
import cz.cuni.amis.pogamut.multi.worldview.stub.BatchAwareLocalWVStub;
import cz.cuni.amis.pogamut.multi.worldview.stub.BatchAwareSharedWVStub;
import cz.cuni.amis.tests.BaseTest;
import cz.cuni.amis.utils.ExceptionToString;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;
import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.flag.FlagListener;

public class BatchAwareWorldViewTest extends BaseTest {
	
	volatile private boolean SYNC_BATCHES = true;
	
	volatile private CountDownLatch batchLatch;
	
	volatile private int threadsUsingLatch;
	
	volatile private Object BATCH_LATCH_MUTEX = new Object();	
	
	protected BatchAwareSharedWVStub createSWV() {
		Logger log = Logger.getAnonymousLogger();
		log.setLevel(Level.FINE);
		return new BatchAwareSharedWVStub(log);
	}
	
	/**
	 * Generates a single batch of events on specified time for specified worldView on run();
	 * @author jimmy
	 */
	protected class EventGeneratorStub {
		private int objectsPerBatch;
		private ILocalWorldView localWV;
		private TeamedAgentId agentId;
		
		public EventGeneratorStub(int objectsPerBatch, ILocalWorldView localWV) {
			this.objectsPerBatch = objectsPerBatch;
			this.localWV = localWV;
			agentId = (TeamedAgentId) localWV.getAgentId();
		}
		
		public void generateEvents(long time) {
			log.info(agentId + "-EventGenerator: generating events [Time=" + time + "].");
			localWV.notify( new BatchBeginEventStub(time));
			
			for ( int i = 0; i < objectsPerBatch; ++i) {
				WorldObjectId id = WorldObjectId.get("TestObject["+i+"]");
				TestCompositeObject obj = 
					new TestCompositeObjectMessage(
						id, 
						time, 
						"LS:"+agentId.getTeamId().toString()+"["+i+"]"+"("+time+")",
						i+time, 
						"ShS:"+agentId.getTeamId().toString()+"["+i+"]"+"("+time+")",
						i+1000+time, 
						"StaticString["+i+"]" , (long)i
					);
				localWV.notify(obj.createUpdateEvent(time, agentId.getTeamId()));
			}
			localWV.notify(new BatchEndEventStub(time));
			log.info(agentId + "-EventGenerator: generated " + objectsPerBatch + " object-events.");
		}
		
	}
	
	/**
	 * Generates a single batch of events on specified time for specified worldView on run();
	 * @author jimmy
	 */
	protected class AgentEventGeneratorStub implements Runnable {

		private EventGeneratorStub eventGenerator;
		private long batchDuration;
		private TeamedAgentId agentId;
		
		volatile private boolean shouldRun = true;
		volatile private Flag<Boolean> isRunning = new Flag<Boolean>(false);
		volatile private boolean failure = false;
		volatile private Throwable cause = null;
		
		private long currentTime = 0;

		public AgentEventGeneratorStub(int objectPerBatch, long batchDuration,  ILocalWorldView localWV) {
			this.eventGenerator = new EventGeneratorStub(objectPerBatch, localWV);
			this.batchDuration = batchDuration;
			agentId = (TeamedAgentId) localWV.getAgentId();
		}
		
		@Override
		public void run() {
			log.info(agentId + "-EventGenerator: STARTING EXPORTING BATCH OF EVENTS");
			isRunning.setFlag(true);
			Boolean latchDowned = null;
			CountDownLatch lastLatch = null;
			try {
				while (shouldRun) {
					if (SYNC_BATCHES) {
						synchronized(BATCH_LATCH_MUTEX) {
							if (batchLatch == null || batchLatch.getCount() == 0) {
								log.info(agentId + "-EventGenerator: creating global batchLatch(" + threadsUsingLatch + ")");
								batchLatch = new CountDownLatch(threadsUsingLatch);
							}
							lastLatch = batchLatch;
							latchDowned = false;
						}					
					}					
					generateNext();
					if (SYNC_BATCHES) {
						long lastCount = lastLatch.getCount();
						lastLatch.countDown();
						log.info(agentId + "-EventGenerator: lastLatch.countDown() == " + (lastCount - 1) + " remains");
						latchDowned = true;
						log.info(agentId + "-EventGenerator: lastLatch.await()...");
						lastLatch.await();
					}
					log.info(agentId + "-EventGenerator: sleeping for " + batchDuration + " ms");
					try {
						Thread.sleep(batchDuration);
					} catch (InterruptedException e) {
						throw new PogamutInterruptedException(e, this);
					}
				}
			} catch (Exception e) {
				cause = e;
				if ((!(e instanceof InterruptedException)) || !SYNC_BATCHES) {
					log.severe(ExceptionToString.process(agentId + "-EventGenerator: failed to generate events.", e));
					failure = true;
				}								
			} finally {
				try {
					if (SYNC_BATCHES) {
						synchronized(BATCH_LATCH_MUTEX) {
							--threadsUsingLatch;
							if (latchDowned != null && !latchDowned) {
								if (lastLatch != null) {
									long lastCount = lastLatch.getCount();									
									lastLatch.countDown();
									log.info(agentId + "-EventGenerator: additional lastLatch.countDown() == " + (lastCount - 1) + " remains");
								}
								latchDowned = true;
							}
							if (lastLatch != batchLatch) {
								long lastCount = batchLatch.getCount();									
								batchLatch.countDown();
								log.info(agentId + "-EventGenerator: additional batchLatch.countDown() == " + (lastCount - 1) + " remains");
							}
						}
					}
				} catch (Exception e) {
					if (cause == null) cause = e;
					log.severe(ExceptionToString.process(e));
					failure = true;
				} finally {
					isRunning.setFlag(false);
				}
			}
			log.info(agentId + "-EventGenerator: END");
		}

		public void generateNext() {
			++currentTime;				
			eventGenerator.generateEvents(currentTime);
		}
		
	}
	
	/**
	 * Generates a single batch of events on specified time for specified worldView on run();
	 * @author jimmy
	 */
	protected class AgentLogicStub implements Runnable {

		private long logicDuration;
		private TeamedAgentId agentId;
		
		volatile private boolean shouldRun = true;
		volatile private Flag<Boolean> isRunning = new Flag<Boolean>(false);
		volatile private boolean failure = false;
		volatile private Throwable cause = null;
		
		private long currentTime = 0;
		private int runs;
		private int run;
		private BatchAwareLocalWorldView localWV;
		private int objectsPerBatch;
		
		public AgentLogicStub(int runs, int objectsPerBatch, long logicDuration,  BatchAwareLocalWorldView localWV) {
			this.runs = runs;
			this.objectsPerBatch = objectsPerBatch;
			this.logicDuration = logicDuration;
			agentId = (TeamedAgentId) localWV.getAgentId();
			this.localWV = localWV;
		}
		
		@Override
		public void run() {
			log.info(agentId + "-Logic: STARTING LOGIC");
			isRunning.setFlag(true);		
			try {
				run = 0;
				while (run < runs && shouldRun) {					
				    consumeNext(); // increments 'run'
				}
			} catch (ComponentNotRunningException cnre) {
				// this marks the end of the agent
			} catch (Exception e) {
				cause = e;
				failure = true;
				log.severe(ExceptionToString.process(agentId + "-Logic[Run=" + run + "/" + runs + "]: FAILURE.", e));				
			} finally {
				isRunning.setFlag(false);
			}
			log.info(agentId + "-Logic: END");
		}

		public void consumeNext() throws InterruptedException {
			++run;
			log.info(agentId + "-Logic[Run=" + run + "/" + runs + "]: LOGIC ITERATION");
			log.info(agentId + "-Logic[Run=" + run + "/" + runs + "]: Locking worldview...");
			localWV.lock();
			
			try {
				currentTime = localWV.getCurrentTimeKey().getTime();
				log.info(agentId + "-Logic[Run=" + run + "/" + runs + ", Time=" + currentTime + "]: Worldview locked.");
				
				for ( int i = 0; i < objectsPerBatch; ++i) {
					WorldObjectId objectId = WorldObjectId.get("TestObject["+i+"]");
					TestCompositeObject obj = (TestCompositeObject) localWV.get(objectId);
					assertTrue(
						agentId + "-Logic[Run=" + run + "/" + runs + ", Time=" + currentTime + "]: " + objectId + " must exist! But is null...", 
						obj != null
					);
					
					long sharedLong = i+1000+currentTime;
					String sharedString = "ShS:"+agentId.getTeamId().toString()+"["+i+"]"+"("+currentTime+")";
					
					long localLong = i + currentTime;
					String localString = "LS:"+agentId.getTeamId().toString()+"["+i+"]"+"("+currentTime+")";
										
					long staticLong = i;
					String staticString = "StaticString["+i+"]";

//					log.info(agentId + "-Logic[Run=" + run + "/" + runs + ", Time=" + currentTime + "]: checking fields of " +  obj);
//					log.info(agentId + "-Logic[Run=" + run + "/" + runs + ", Time=" + currentTime + "]: Expecting SharedString = " + sharedString);
//					log.info(agentId + "-Logic[Run=" + run + "/" + runs + ", Time=" + currentTime + "]: Expecting SharedLong   = " + sharedLong);
//					log.info(agentId + "-Logic[Run=" + run + "/" + runs + ", Time=" + currentTime + "]: Expecting LocalString  = " + localString);
//					log.info(agentId + "-Logic[Run=" + run + "/" + runs + ", Time=" + currentTime + "]: Expecting LocalLong    = " + localLong);
//					log.info(agentId + "-Logic[Run=" + run + "/" + runs + ", Time=" + currentTime + "]: Expecting StaticString = " + staticString);
//					log.info(agentId + "-Logic[Run=" + run + "/" + runs + ", Time=" + currentTime + "]: Expecting StaticLong   = " + staticLong);

					assertTrue(
							agentId + "-Logic[Run=" + run + "/" + runs + ", Time=" + currentTime + "]: SharedString fail!" 
							+ NEW_LINE + "        Expected: " + sharedString + ", "
							+ NEW_LINE + "        Got:      " + obj.getSharedString()
							+ NEW_LINE + "        On:       " + obj,
							obj.getSharedString().equals(sharedString)
						);
					
					assertTrue(
						agentId + "-Logic[Run=" + run + "/" + runs + ", Time=" + currentTime + "]: SharedLong fail!"
						+ NEW_LINE + "        Expected: " + sharedLong + ", "
						+ NEW_LINE + "        Got:      " + obj.getSharedLong()
						+ NEW_LINE + "        On:       " + obj,
						obj.getSharedLong() == sharedLong
					);
						
					assertTrue(
							agentId + "-Logic[Run=" + run + "/" + runs + ", Time=" + currentTime + "]: LocalString fail!"
							+ NEW_LINE + "        Expected: " + localString + ", "
							+ NEW_LINE + "        Got:      " + obj.getLocalString()
							+ NEW_LINE + "        On:       " + obj,
							obj.getLocalString().equals(localString)
						);
					
					assertTrue(
						agentId + "-Logic[Run=" + run + "/" + runs + ", Time=" + currentTime + "]: LocalLong fail!"
						+ NEW_LINE + "        Expected: " + localLong + ", "
						+ NEW_LINE + "        Got:      " + obj.getLocalLong()
						+ NEW_LINE + "        On:       " + obj,	
						obj.getLocalLong() == i + currentTime			
					);
					
					assertTrue(
							agentId + "-Logic[Run=" + run + "/" + runs + ", Time=" + currentTime + "]: StaticString fail!"
							+ NEW_LINE + "        Expected: " + staticString + ", "
							+ NEW_LINE + "        Got:      " + obj.getStaticString()
							+ NEW_LINE + "        On:       " + obj,
							obj.getStaticString().equals(staticString)
						);
					
					assertTrue(
						agentId + "-Logic[Run=" + run + "/" + runs + ", Time=" + currentTime + "]: StaticLong fail!"
						+ NEW_LINE + "        Expected: " + staticLong + ", "
						+ NEW_LINE + "        Got:      " + obj.getStaticLong()
						+ NEW_LINE + "        On:       " + obj,
						obj.getStaticLong() == staticLong
					);
				}	
				
				log.info(agentId + "-Logic: Simulating logic " + logicDuration + " ms...");
				try {
					Thread.sleep(logicDuration);
				} catch (InterruptedException e) {
					throw new PogamutInterruptedException(e, this);
				}
			} finally {
				localWV.unlock();
			}
		}
		
	}
	
	protected class TeamAgentStub {
		
		private ITeamedAgentId agentId;
		private BatchAwareLocalWorldView agentWV;		
		private ComponentStub starter;
		private AgentEventGeneratorStub eventGenerator;
		private AgentLogicStub logic;
		
		public TeamAgentStub(
				int agentNum, 
				BatchAwareSharedWorldView swv,
				int logicCycles,
				long logicDuration,
				int objectsPerBatch,
				long batchDuration
		) {
			agentId = new TeamedAgentId("Agent" + agentNum, "RED");
			IAgentLogger log = new AgentLogger(agentId);
			log.setLevel(Level.FINE);
			log.addDefaultConsoleHandler();
			LifecycleBus bus = new LifecycleBus(log);
			starter = new ComponentStub(log,bus);
			agentWV = new BatchAwareLocalWVStub( new ComponentDependencies(ComponentDependencyType.STARTS_WITH).add(starter), bus, log, swv, agentId);
			
			this.eventGenerator = new AgentEventGeneratorStub(objectsPerBatch, batchDuration, agentWV);
			this.logic = new AgentLogicStub(logicCycles, objectsPerBatch, logicDuration, agentWV);
		}
		
		public ITeamedAgentId getAgentId() {
			return agentId;
		}

		public BatchAwareLocalWorldView getLocalWV() {
			return agentWV;
		}
		
		//////////////////////////
		
		private Object eventMutex = new Object();
		
		private Thread eventThread;		
		private Thread logicThread;
		
		volatile private Flag<Boolean> isRunning = new Flag<Boolean>(false);
		
		volatile private boolean failure = false;
		volatile private Throwable cause = null;
		
		private FlagListener<Boolean> eventListener = new FlagListener<Boolean>() {

			@Override
			public void flagChanged(Boolean changedValue) {
				if (!changedValue) {
					if (isRunning.getFlag() == true && eventGenerator.failure) {
						if (cause == null) {
							cause = eventGenerator.cause;
						}
						failure = true;
					}
					kill();
				}
			}

		};
		
		private FlagListener<Boolean> logicListener = new FlagListener<Boolean>() {

			@Override
			public void flagChanged(Boolean changedValue) {
				if (!changedValue) {
					if (logic.failure) {
						if (cause == null) {
							cause = logic.cause;
						}
						failure = true;
					}
					kill();
				}
			}

		};
		
		
		
		public void start() {			
			synchronized(eventMutex) {
				if (isRunning.getFlag()) return;

				starter.getController().manualStart("Test");
				
				eventGenerator.shouldRun = true;
				logic.shouldRun = true;
				
				eventThread = new Thread(eventGenerator, agentId.getToken() + "-EventGeneratorThread");
				logicThread = new Thread(logic, agentId.getToken() + "-LogicThread");
				
				eventGenerator.isRunning.addListener(eventListener);
				logic.isRunning.addListener(logicListener);
			
				isRunning.setFlag(true);
				failure = false;
				eventThread.start();
				logicThread.start();
			}
		}
		
		
		boolean killing = false;
		
		public void kill() {
			if (!isRunning.getFlag()) {
				return;
			}
			synchronized(eventMutex) {
				if (killing) {
					return;
				}	
				killing = true;
			}
				
			try {
				log.info(agentId + ": Shutting down...");
			
				eventGenerator.shouldRun = false;
				if (failure) {
					eventGenerator.isRunning.removeListener(eventListener);
					eventThread.interrupt();
				} else {
					log.info(agentId + ": Waiting 2 secs for EventGenerator shutdown...");
					eventGenerator.isRunning.waitFor(2000, false);
					eventGenerator.isRunning.removeListener(eventListener);
					if (eventGenerator.isRunning.getFlag()) {
						if (!SYNC_BATCHES) {
							log.severe(agentId + ": FAILURE! EventGenerator still running after 2secs!");
							failure = true;
						}
						eventThread.interrupt();
					} else {
						log.info(agentId + ": EventGenerator shut down.");
					}
				}
				
				logic.shouldRun = false;
				if (failure) {
					logic.isRunning.removeListener(logicListener);
					logicThread.interrupt();
				} else {
					log.info(agentId + ": Waiting 2 secs for Logic shutdown...");
					logic.isRunning.waitFor(2000, false);
					logic.isRunning.removeListener(logicListener);
					if (logic.isRunning.getFlag()) {
						log.severe(agentId + ": FAILURE! Logic still running!");
						failure = true;
						logicThread.interrupt();
					}else {
						log.info(agentId + ": Logic shut down.");
					}
				}
			
				if (!failure) {
					try {
						log.info(agentId + ": Generating additional batch.");
						eventGenerator.generateNext();							
						log.info(agentId + ": Consuming additionally generated batches with another logic-iteration.");
						logic.consumeNext();
					} catch (Exception e) {
						log.severe(ExceptionToString.process("Failed!", e));
						failure = true;
					}
				}
				
				starter.getController().manualStop("Stop");	
			} finally {
				try {
					isRunning.setFlag(false);
				} finally {
					killing = false;
				}
			}
		}

	}
	
	private class AgentRunnerStub {
		
		private TeamAgentStub[] agents;
		
		private FlagListener<Boolean>[] listeners;
		
		private Object eventMutex = new Object();
		
		private CountDownLatch latch;

		private boolean failure = false;
		private Throwable cause = null;
		
		private Flag<Boolean> isRunning = new Flag<Boolean>(false);
		
		public AgentRunnerStub(int agentNum, BatchAwareSharedWorldView swv, int logicCycles, long logicDuration, int objectsPerBatch, long batchDuration) {
			agents = new TeamAgentStub[agentNum];
			listeners = new FlagListener[agentNum];
			latch = new CountDownLatch(agentNum);
			
			for (int i = 0; i < agents.length; ++i) {
				final int curAgent = i;
				agents[i] = new TeamAgentStub(i, swv, logicCycles, logicDuration, objectsPerBatch, batchDuration);
				listeners[i] = new FlagListener<Boolean>() {
					@Override
					public void flagChanged(Boolean changedValue) {
						if (!changedValue) {
							if (agents[curAgent].failure) {
								failure(curAgent);
							} else {
								latch.countDown();
							}
						}
					}					
				};
				agents[i].isRunning.addListener(listeners[i]);
			}
		}
		
		private void failure(int agentNum) {
			synchronized(eventMutex) {
				if (failure) return;
				if (cause == null) cause = agents[agentNum].cause;
				log.severe("Agent[" + agentNum + "] FAILED!");
				failure = true;
			}
			try {
				for (TeamAgentStub agent : agents) {
					if (agent != null) {
						try {
							agent.kill();
						} catch (Exception e) {
							log.severe(ExceptionToString.process("Exception killing " + agent.agentId, e));
						}
					}
				}
			} finally {
				while (latch.getCount() > 0) latch.countDown();
			}
		}
		
		private void failure(String message) {
			synchronized(eventMutex) {
				if (failure) return;
				if (cause == null) {
					cause = new RuntimeException(message);
				}
				log.severe("AgentRunner FAILED!");
				failure = true;
			}
			try {
				for (TeamAgentStub agent : agents) {
					if (agent != null) {
						try {
							agent.kill();
						} catch (Exception e) {
							log.severe(ExceptionToString.process("Exception killing " + agent.agentId, e));
						}
					}
				}
			} finally {
				while (latch.getCount() > 0) latch.countDown();
			}
		}
		
		public synchronized void run(long timeout) {
			try {
				isRunning.setFlag(true);
				synchronized(eventMutex) {
					for (int i = 0; i < agents.length; ++i) {
						agents[i].start();
					}		
				}
				try {
					latch.await(timeout, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					log.severe("INTERRUPTED!");
					failure = true;
					if (cause == null) {
						cause = new PogamutInterruptedException(e, this);
						throw (PogamutInterruptedException)cause;
					} else {
						throw new PogamutException("Interrupted!", cause, this);
					}
				}
				if (latch.getCount() > 0) {
					failure("TIMEOUT!");
				}
			} catch (Exception e) {
				failure("RUNNER EXCEPTION!");
			} finally {
				int i = 0;
				for (TeamAgentStub agent : agents) {
					if (agent != null && listeners[i] != null) {
						agent.isRunning.removeListener(listeners[i]);
					}
					if (agent != null) {
						agent.kill();
					}
					++i;
				}
				isRunning.setFlag(false);
			}
		}

		public void destroy() {
			for (int i = 0; i < agents.length; ++i) {
				TeamAgentStub agent = agents[i];
				if (agent != null) {
					//agent.destroy();
					agents[i] = null;
				}				
			}
		}
		
	}
	
	public synchronized void runTest(int agents, int logicCycles, long logicDuration, int objectsPerBatch, long batchDuration, long timeoutMillis, boolean syncBatches) {
	
		try {
			log.info("======== TEST ========");
		
			log.info("Agents:            " + agents);
			log.info("Logic cycles:      " + logicCycles);
			log.info("Logic duration:    " + logicDuration + " ms");
			log.info("Objects per batch: " + objectsPerBatch + " ms");
			log.info("Batch duration:    " + batchDuration + " ms");
			log.info("Sync batches:      " + syncBatches);
			log.info("-------- INIT --------");
			
			log.info("Test params...");
			SYNC_BATCHES = syncBatches;
			threadsUsingLatch = 1*agents; // CountDownLatch is used by 1xEventGenerator thread ... thus we need 1*agents
			
			log.info("Shared worldview...");
			BatchAwareSharedWorldView swv = createSWV();
			log.info("Runner...");
			AgentRunnerStub runner = new AgentRunnerStub(agents, swv, logicCycles, logicDuration, objectsPerBatch, batchDuration);
			
			log.info("------ TESTING! ------");
			runner.run(timeoutMillis);
					
			if (runner.failure) {
				if (runner.cause != null) {
					throw new PogamutException("TEST FAILED", runner.cause, log, this);				
				} else {
					throw new PogamutException("TEST FAILED", log, this);
				}
			}
			
			runner.destroy();
			runner = null;
			swv = null;
			
			CheckInstances.waitGCTotal(agents);
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
		
		// ADDITIONAL CHECKS
		testOk();
	}
	
	@Ignore
	@Test
	public void exampleTest() {
		runTest(
			1,       // int agents, 
			10,      // int logicCycles, 
			100,     // long logicDuration, 
			10,      // int objectsPerBatch, 
			50,      // long batchDuration, 
			10000000,// long timeoutMillis
			true     // SYNC BATCHES
		);
	}
	
}
