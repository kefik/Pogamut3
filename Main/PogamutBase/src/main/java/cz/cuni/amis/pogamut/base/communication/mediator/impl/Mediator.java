package cz.cuni.amis.pogamut.base.communication.mediator.impl;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.communication.exception.CommunicationException;
import cz.cuni.amis.pogamut.base.communication.mediator.IMediator;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEventOutput;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldChangeEventInput;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentNotRunningException;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentPausedException;
import cz.cuni.amis.pogamut.base.component.controller.ComponentControlHelper;
import cz.cuni.amis.pogamut.base.component.controller.ComponentController;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencyType;
import cz.cuni.amis.pogamut.base.component.controller.IComponentControlHelper;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.token.Token;
import cz.cuni.amis.utils.token.Tokens;

/**
 * This class should wrap the reading thread that continuously reads 
 * {@link IWorldChangeEvent} from the {@link IWorldChangeEventOutput}
 * passing them to the {@IWorldEventInput} without any delay.
 * <p><p>
 * Note that the mediator **NEEDS** {@link Mediator#setConsumer(IWorldChangeEventInput)} called in order to be started
 * by the {@link IComponentBus}.
 * <p><p>
 * Ignores {@link IComponentControlHelper#startPaused()}, performs {@link IComponentControlHelper#start()} in both start cases.
 */
@AgentScoped
public class Mediator implements IMediator {
	
	public static final Token COMPONENT_ID = Tokens.get("Mediator");

	/**
	 * Name prefix for the worker thread and for the logs.
	 */
	public static final String WORKER_THREAD_NAME_PREFIX = "MediatorWorker";

	/**
	 * Worker instance - it implements Runnable interface and is continuously
	 * reading messages from the connection object and passing them to the
	 * receiver.
	 */
	protected Worker worker = null;
	
	/**
	 * Thread of the worker.
	 */
	protected Thread workerThread = null;

	/**
	 * Mutex for start synchronization.
	 */
	protected Object threadMutex = new Object();

	/**
	 * Log category for the mediator (platform log).
	 */
	private LogCategory log = null;

	/**
	 * Used to get events from the world.
	 */
	private IWorldChangeEventOutput producer;
	
	/**
	 * Who events get passed to.
	 */
	private IWorldChangeEventInput consumer;
	
	private ComponentController controller;

	private IComponentBus eventBus;

	private IAgentId agentId;

	/**
	 * The object in passed to the constructor (IWorldEventOutput) is world
	 * event producer.
	 * <p><p>
	 * The mediator will read events from this producer and pass them to the
	 * IWorldEventInput specified during the start() of the mediator.
	 * <p><p>
	 * Note that the mediator **NEEDS** {@link Mediator#setConsumer(IWorldChangeEventInput)} called in order to be started
	 * by the {@link IComponentBus}.
	 * 
	 * @param connection
	 * @param messageParser
	 * @param commandSerializer
	 * @throws CommunicationException
	 */
	@Inject
	public Mediator(IWorldChangeEventOutput producer, IComponentBus bus, IAgentLogger logger) {
		this.agentId = logger.getAgentId();
		this.log = logger.getCategory(getComponentId().getToken());
		this.producer = producer;
		this.eventBus = bus;
	}
	
	private IComponentControlHelper control = new ComponentControlHelper() {
		
		@Override
		public void stop() throws PogamutException {
			Worker w = worker;
			if (w != null) {
				w.stop();
			}
		}
		
		@Override
		public void startPaused() {
			start();
		}
		
		@Override
		public void start() throws PogamutException {
			if (workerThread != null) {
				if (log.isLoggable(Level.WARNING)) log.warning("Mediator worker thread already exists, leaking resources?");
			}
			synchronized (threadMutex) {
				if (log.isLoggable(Level.FINER)) log.finer("Starting mediator thread " + WORKER_THREAD_NAME_PREFIX + ".");
				worker = new Worker();
				workerThread = new Thread(worker, agentId.getName().getFlag() + " mediator");
				workerThread.start();
			}
		}
		
		@Override
		public void kill() {
			Worker w = worker;
			if (w != null) w.kill();
			Thread thread = workerThread;
			if (thread != null) thread.interrupt();
			worker = null;
			workerThread = null;
		}
		
		public void pause() {
			Worker w = worker;
			if (w != null) {
				w.pause();
			}	
		}
		
		public void resume() {
			Worker w = worker;
			if (w != null) {
				w.resume();
			}	
		}
		
		@Override
		public void reset() {
			worker = null;
			workerThread = null;
		}
		
	};
	
	@Override
	public Token getComponentId() {
		return COMPONENT_ID;
	}
	
	public LogCategory getLog() {
		return log;
	}
	
	@Override
	public void setConsumer(IWorldChangeEventInput consumer) {
		this.consumer = consumer;
		this.controller = new ComponentController(this, control, eventBus, log, ComponentDependencyType.STARTS_AFTER, producer, consumer);
	}
	
	@Override
	public String toString() {
		if (this == null) return "Mediator";
		return getClass().getSimpleName() + "[producer=" + producer + ", consumer=" + consumer +"]";
	}


	private class Worker implements Runnable {

		private volatile CountDownLatch stopLatch = new CountDownLatch(1);
		
		/**
		 * Simple flag that is telling us whether the Worker should run.
		 */
		private volatile boolean shouldRun = true;
		
		private volatile Flag<Boolean> shouldPause = new Flag<Boolean>(false);
		
		private volatile boolean running = false;
		
		private volatile boolean exceptionExpected = false;
		
		private Thread myThread;
		
		public void pause() {
			shouldPause.setFlag(true);
		}
		
		public void resume() {
			shouldPause.setFlag(false);
		}
		
		/**
		 * Drops the shouldRun flag.
		 */
		public void stop() {
			this.shouldRun = false;
			this.shouldPause.setFlag(false);
			exceptionExpected = true;
			myThread.interrupt();
		}

		/**
		 * Drops the shouldRun flag, waits for 200ms and then interrupts the
		 * thread in hope it helps.
		 */
		public void kill() {
			if (!running) return;
			this.shouldRun = false;
			this.shouldPause.setFlag(false);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
			}
			exceptionExpected = true;
			myThread.interrupt();
		}
		
		/**
		 * Contains main while cycle that is continuously reading messages from
		 * the connection (using parser), notifying listeners and then passing
		 * them to the message receiver.
		 */
		@Override
		public void run() {
			myThread = Thread.currentThread();
			
			// set the running flag, we've been started
			running = true;

			// notify that gateway started
			logWorker(Level.INFO, "Started.");

			try {
				IWorldChangeEventInput currentConsumer;
				IWorldChangeEventOutput currentProducer;
				IWorldChangeEvent worldEvent = null;

				while (shouldRun && !myThread.isInterrupted()) {
					
					if (shouldPause.getFlag()) {
						logWorker(Level.INFO, "Paused.");
						shouldPause.waitFor(false);
						logWorker(Level.INFO, "Resumed.");
					}
					
					// are we alive?
					if (!shouldRun || myThread.isInterrupted()) {
						break;
					}
					
					currentConsumer = consumer;
					if (currentConsumer == null) {
						running = false;
						if (!exceptionExpected) {
							controller.fatalError("Event consumer lost (is null).");
						}
						break;
					}
					
					currentProducer = producer;
					if (currentProducer == null) {
						running = false;
						if (!exceptionExpected) {
							controller.fatalError("Event producer lost (is null).");
						}
						break;
					}
					
					// do we have cached event?
					if (worldEvent == null) {
						try {
							// following call may block
							worldEvent = producer.getEvent();
							logWorker(Level.FINEST, "received - " + String.valueOf(worldEvent), worldEvent);
						} catch (ComponentPausedException cpe) {
							logWorker(Level.INFO, "Producer is paused, pausing mediator.");
							shouldPause.setFlag(true);
							continue;
						} catch (ComponentNotRunningException nre) {
							logWorker(Level.WARNING, "Producer is not running, stopping the mediator worker.");
							running = false;
							break;
						} catch (Exception ce) {
							running = false;
							if (!exceptionExpected) {
								controller.fatalError(WORKER_THREAD_NAME_PREFIX + ": Producer exception.", ce);
							} else {
								logWorker(Level.FINE, "Producer exception expected, caught: " + ce); 
							}
							break;
						}
					}
					
					// are we alive?
					if (!shouldRun || myThread.isInterrupted()) {
						break;
					}
					
					// yes we are, continue
					try {
						currentConsumer.notify(worldEvent);
					} catch (ComponentPausedException e) {
						logWorker(Level.INFO, "Consumer is paused, pausing mediator.");
						shouldPause.setFlag(true);
						continue;
					} catch (ComponentNotRunningException e) {
						logWorker(Level.WARNING, "Consumer is not running, stopping mediator worker.");
						running = false;
						break;
					} catch (Exception e) {						
						running = false;
						if (!exceptionExpected) {
							controller.fatalError(WORKER_THREAD_NAME_PREFIX + ": Consumer exception.", e);
						} else {
							logWorker(Level.FINE, "Consumer exception expected, caught: " + e); 
						}
						break;
					}
					
					worldEvent = null;
				}
			} catch (Exception e) {
				running = false;
				if (!exceptionExpected) {
					controller.fatalError(WORKER_THREAD_NAME_PREFIX + ": Exception.", e);
				} else {
					logWorker(Level.FINE, "Exception expected, caught: " + e);
				}
			}
			
			try {
				stopLatch.countDown();
			} finally {
				// clean after yourself
				shouldRun = false;			
				running = false;
				
				synchronized(threadMutex) {
					if (workerThread == myThread) {
						worker = null; 
						workerThread = null;
					}
				}
				
				logWorker(Level.WARNING, "Stopped.");
			}
		}

		private void logWorker(Level level, String message) {
			log.log(level, WORKER_THREAD_NAME_PREFIX + ": " + message);
		}

		private void logWorker(Level level, String message, Object obj) {
			if (obj == null)
				log.log(level, WORKER_THREAD_NAME_PREFIX + ": " + message);
			else
				log.log(level, WORKER_THREAD_NAME_PREFIX + ": " + message, obj);
		}

	}

}
