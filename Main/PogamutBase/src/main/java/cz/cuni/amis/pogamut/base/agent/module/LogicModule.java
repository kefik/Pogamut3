package cz.cuni.amis.pogamut.base.agent.module;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.exceptions.AgentException;
import cz.cuni.amis.pogamut.base.agent.module.exception.LogicThreadAlteredException;
import cz.cuni.amis.pogamut.base.component.bus.event.EventFilter;
import cz.cuni.amis.pogamut.base.component.bus.event.IStartedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.WaitForEvent;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentNotRunningException;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentPausedException;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencyType;
import cz.cuni.amis.pogamut.base.component.controller.ComponentState;
import cz.cuni.amis.pogamut.base.component.exception.ComponentCantPauseException;
import cz.cuni.amis.pogamut.base.component.exception.ComponentCantResumeException;
import cz.cuni.amis.pogamut.base.component.exception.ComponentCantStartException;
import cz.cuni.amis.pogamut.base.component.exception.ComponentCantStopException;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.utils.Const;
import cz.cuni.amis.utils.ExceptionToString;
import cz.cuni.amis.utils.StopWatch;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;
import cz.cuni.amis.utils.flag.Flag;

@AgentScoped
public class LogicModule<AGENT extends IAgent> extends AgentModule<AGENT> {
	
	private static long THREAD_COUNTER = 0;
	
	private static final long LOGIC_WAIT_TIME_PLUS_MILLIS = 1000;
	
	/**
	 * Must be greater than 0.
	 */
	public static final long MIN_LOGIC_PERIOD_MILLIS = 1; 
	
	/**
	 * Must be greater than 0.
	 */
	public static final double MAX_LOGIC_FREQUENCY = 1000 / MIN_LOGIC_PERIOD_MILLIS;
	
	/**
	 * Must be greater than 0.
	 */
	public static final long MAX_LOGIC_PERIOD_MILLIS = 100000000;
	
	/**
	 * Must be greater than 0.
	 */
	public static final double MIN_LOGIC_FREQUENCY = 1000 / MAX_LOGIC_PERIOD_MILLIS;
	
	protected Object mutex = new Object();
	
	protected IAgentLogic logic;

	protected Thread logicThread = null;
	
	protected boolean logicShouldRun = true;
	
	protected Flag<Boolean> logicRunning = new Flag<Boolean>(false);
	
	protected Flag<Boolean> logicShouldPause = new Flag<Boolean>(false);
	
	protected Flag<Boolean> logicPaused = new Flag<Boolean>(false);
	
	protected double logicFrequency = 10;
	
	protected double logicPeriod = 100;
	
	protected long lastLogicRun = 0;

	protected Throwable logicException;

	@Inject
	public LogicModule(AGENT agent, IAgentLogic logic) {
		this(agent, logic, null, new ComponentDependencies(ComponentDependencyType.STARTS_WITH).add(agent));
	}
	
	public LogicModule(AGENT agent, IAgentLogic logic, Logger log) {
		this(agent, logic, log, new ComponentDependencies(ComponentDependencyType.STARTS_WITH).add(agent));
	}
	
	public LogicModule(AGENT agent, IAgentLogic logic, Logger log, ComponentDependencies dependencies) {
		super(agent, log, dependencies);
		this.logic = logic;
	}

	/**
	 * Unsync! Call only within synchronized(mutex) block.
	 */
	private void clearLogicRunningVars() {
		logicShouldPause.setFlag(false);
		logicPaused.setFlag(false);
		logicRunning.setFlag(false);
		logicShouldRun = true;		
	}
	
	@Override
	protected void start(boolean startPaused) throws AgentException {
		super.start(startPaused);
		synchronized(mutex) {
			if (logicThread != null) {
				if (log.isLoggable(Level.WARNING)) log.warning("Logic thread is not null! Sending interrupt and dropping the reference, possibly leaking resources.");
				logicThread.interrupt();
				logicThread = null;
			}
			long counter = THREAD_COUNTER++;
			String name = agent.getName() + "'s logic (" + counter + ")"; 
			clearLogicRunningVars();
			logicThread = new Thread(new LogicRunner("Thread " + counter), agent.getName() + " logic");
		}
		if (startPaused) {
			if (log.isLoggable(Level.WARNING)) log.fine("Starting logic thread in paused state (start paused requested).");
		}
		logicShouldPause.setFlag(startPaused);
		if (log.isLoggable(Level.FINE)) log.fine("Starting logic thread.");
		logicThread.start();
		long waitTime = logic.getLogicInitializeTime() + LOGIC_WAIT_TIME_PLUS_MILLIS;
		if (log.isLoggable(Level.INFO)) log.info("Waiting for the logic to initialize (" + waitTime + " ms).");
		Boolean result = logicRunning.waitFor(waitTime, true);
		if (!controller.inState(ComponentState.STARTING)) {
			throw new ComponentCantStartException("Woke up, module state differs. It is not " + ComponentState.STARTING + " but " + controller.getState().getFlag() + ".", log, this);			
		}
		if (result == null) {
			throw new ComponentCantStartException("Logic initialization is taking too long, did you correctly specified initialize time via getInitializeTime() method?", log, this);
		}
	}
	
	@Override
	public void stop() {
		super.stop();
		if (Thread.currentThread() == this.logicThread) {
			inThreadStopping();
		} else {			
			logicShouldRun = false;
			logicShouldPause.setFlag(false);
			long waitTime = (long)logicPeriod + LOGIC_WAIT_TIME_PLUS_MILLIS;
			if (log.isLoggable(Level.INFO)) log.info("Waiting for the logic to stop (" + waitTime + " ms).");
			Boolean result = logicRunning.waitFor(waitTime, false);
			if (result == null) {
				throw new ComponentCantStopException("Logic thread is still running! Is your logic too cpu-demanding?", log, this);
			}
		}
	}
	
	@Override
	protected void kill() {
		super.kill();
		if (Thread.currentThread() == this.logicThread) {
			inThreadKilling();
		} else {
			logicShouldRun = false;
			logicShouldPause.setFlag(false);
			synchronized(mutex) {
				if (logicThread == null) return;
			}
			long waitTime = (long)logicPeriod + LOGIC_WAIT_TIME_PLUS_MILLIS;
			if (log.isLoggable(Level.INFO)) log.info("Waiting for the logic to stop (" + waitTime + " ms).");
			Boolean result = logicRunning.waitFor(waitTime, false);
			synchronized(mutex) {
				if (logicThread == null) return;
				if (result == null) if (log.isLoggable(Level.WARNING)) log.warning("Logic thread is still running, sending interrupt.");
				else return;
				logicThread.interrupt();
			}
			if (log.isLoggable(Level.INFO)) log.info("Waiting for the logic to stop (" + waitTime + " ms).");
			result = logicRunning.waitFor(waitTime, false);
			synchronized(mutex) {
				if (logicThread == null) return;
				if (result == null) if (log.isLoggable(Level.WARNING)) log.warning("Logic thread is still running, is your logic too much cpu demanding?");
			}
		}
	}

	@Override
	protected void pause() {
		super.pause();
		if (Thread.currentThread() == logicThread) {
			inThreadPausing();
		} else {
			logicShouldPause.setFlag(true);
			long waitTime = (long)logicPeriod + LOGIC_WAIT_TIME_PLUS_MILLIS;
			if (log.isLoggable(Level.INFO)) log.info("Waiting for the logic to pause (" + waitTime + " ms).");
			Boolean result = logicPaused.waitFor(waitTime, true);
			if (result == null) {
				throw new ComponentCantPauseException("Logic is still running, is your logic cpu demanding too much?", log, this);
			}
		}
	}

	@Override
	protected void resume() {
		super.resume();
		if (Thread.currentThread() == logicThread) {
			inThreadResuming();
		} else {
			logicShouldPause.setFlag(false);
			long waitTime = (long)logicPeriod + LOGIC_WAIT_TIME_PLUS_MILLIS;
			if (log.isLoggable(Level.INFO)) log.info("Waiting for the logic to resume (" + waitTime + " ms).");
			Boolean result = logicPaused.waitFor(waitTime, false);
			if (result == null) {
				throw new ComponentCantResumeException("Logic did not resumed.", log, this);
			}
		}
	}
	
	protected void inThreadStopping() {
		inThreadWarning("Stopping", "stopped", "stop");
		logicShouldRun = false;
		logicShouldPause.setFlag(false);
	}
	
	protected void inThreadKilling() {
		inThreadWarning("Killing", "killed", "kill");
		logicShouldRun = false;
		logicShouldPause.setFlag(false);
		logicThread.interrupt();
	}
	
	protected void inThreadPausing() {
		inThreadWarning("Pausing", "paused", "pause");
		logicShouldPause.setFlag(true);
	}
	
	protected void inThreadResuming() {
		inThreadWarning("Resuming", "resumed", "resume");
		logicShouldPause.setFlag(false);
	}
	
	private void inThreadWarning(String str1, String str2, String str3) {
		String warning = "In-Logic-Thread " + str1 + " happens. This occurs whenever the LogicModule is being " + str2 + " from within its own thread. While this may proceed as you have expected, it is unsupported operation with uncertain result." + Const.NEW_LINE 
				  + "It is adviced to perform the troubling operation in different thread, e.g.:" + Const.NEW_LINE
				  + "    new Thread(new Runnable() {" + Const.NEW_LINE 
				  + "        @Override" + Const.NEW_LINE
				  + "        public void run() {" + Const.NEW_LINE 
				  + "            // do something that happens to " + str3 + " the logic module //" + Const.NEW_LINE
				  + "        }" + Const.NEW_LINE
				  + "    }).start();"
				  + Const.NEW_LINE
				  + "Stacktrace:"
				  + Const.NEW_LINE
				  + ExceptionToString.getCurrentStackTrace();
		
		if (log.isLoggable(Level.WARNING)) log.warning(warning);
		else if (log.isLoggable(Level.SEVERE)) log.severe(warning);
	}
	
	/**
	 * Called right before the {@link LogicModule#logic}.doLogic() is called.
	 */
	protected void beforeLogic(String threadName) {
	}

	/**
	 * Called right after the {@link LogicModule#logic}.doLogic() is called.
	 */
	protected void afterLogic(String threadName) {
	}
	
	/**
	 * Called whenever some exception is thrown inside {@link LogicRunner}.
	 * @param e
	 */
	protected void afterLogicException(String threadName, Throwable e) {
		logicException = e;
	}
	
	/**
	 * Controls whether the {@link LogicModule#logic}.logic() will be called.
	 * <p><p>
	 * If logic is running & is not paused you may use this to fine control the moments when the logic should execute.
	 * <p><p>
	 * Returns 'true' as default.
	 * 
	 * @return whether the logic should be executing 
	 */
	protected boolean shouldExecuteLogic() {
		return true;
	}
	
	/**
	 * Called before the {@link IAgentLogic#logic()} is periodically called - allows you to sleep the logic until the rest of the agent is ready.
	 */
	protected void logicLatch(String threadName) {		
	}
	
	public double getLogicPeriod() {
		return logicPeriod;
	}
	
	/**
	 * 
	 * @return
	 * 			the throwable that caused the logic to crash. 
	 */
	public Throwable getLogicException(){
		return logicException;
	}
	
	public double getLogicFrequency() {
		return logicFrequency;
	}
	
	public void setMinLogicFrequency() {
		this.logicPeriod = MAX_LOGIC_PERIOD_MILLIS;
		this.logicFrequency = MIN_LOGIC_FREQUENCY;
	}
	
	public void setMaxLogicFrequency() {
		this.logicPeriod = MIN_LOGIC_PERIOD_MILLIS;
		this.logicFrequency = MAX_LOGIC_FREQUENCY;
	}

	public void setLogicFrequency(double frequency) {
		this.logicFrequency = frequency;
		if (this.logicFrequency <= MIN_LOGIC_FREQUENCY) {
			this.logicPeriod = MAX_LOGIC_PERIOD_MILLIS;
			this.logicFrequency = MIN_LOGIC_FREQUENCY;
		} else 
		if (this.logicFrequency > MAX_LOGIC_FREQUENCY){
			this.logicPeriod = MIN_LOGIC_PERIOD_MILLIS;
			this.logicFrequency = MAX_LOGIC_FREQUENCY;
		} else {
			this.logicPeriod = 1000 / frequency;
			this.logicFrequency = frequency;
		}
	}

	private class LogicRunner implements Runnable {

		private String name;
		
		private WaitForEvent startedEvent = new WaitForEvent(eventBus, new EventFilter(IStartedEvent.class, getComponentId()));

		private boolean firstLogic = true;

		public LogicRunner(String name) {
			if (name == null) name = "unnamed";
			this.name = name;
		}
		
		@Override
		public void run() {
			if (log.isLoggable(Level.WARNING)) log.warning(name + ": Thread started.");
			
			if (log.isLoggable(Level.FINE)) log.fine(name + ": Initializing logic.");
			StopWatch logicWatch = new StopWatch();
			logic.logicInitialize(LogicModule.this);
			if (log.isLoggable(Level.INFO)) log.info(name + ": Logic initialized (" + logicWatch.stopStr() + ").");
			
			synchronized(mutex) {
				// alter logicRunning only if inside valid thread, otherwise, stop the thread.
				if (logicThread != Thread.currentThread()) {
					if (log.isLoggable(Level.SEVERE)) log.severe(name + ": Logic thread altered! Shutdown not called!");
					return;
				}
				logicRunning.setFlag(true);
			}
			
			// preallocation
			long sleepTime = 0;
			
			try {
				if (log.isLoggable(Level.FINER)) log.finer(name + ": waiting for the logic module started event.");
				startedEvent.await();
				if (log.isLoggable(Level.FINER)) log.finer(name + ": logic module started event received.");
				
				synchronized(mutex) {
					if (logicThread != Thread.currentThread()) {
						throw new LogicThreadAlteredException(name, log, LogicModule.this);
					}
				}
				
				logicLatch(name);
				
				synchronized(mutex) {
					if (logicThread != Thread.currentThread()) {
						throw new LogicThreadAlteredException(name, log, LogicModule.this);
					}
				}
				
				while(!Thread.currentThread().isInterrupted() && // the thread was not interrupted 
					  logicShouldRun                             // and the logic should be running 
				) {
					if (logicThread != Thread.currentThread()) throw new LogicThreadAlteredException(name, log, LogicModule.this);
					
					if (logicShouldPause.getFlag()) {
						synchronized(mutex) {
							if (logicThread != Thread.currentThread()) throw new LogicThreadAlteredException(name, log, LogicModule.this);
							logicPaused.setFlag(true);
						}
						if (log.isLoggable(Level.WARNING)) log.warning(name + ": Logic paused.");
						logicShouldPause.waitFor(false);
						synchronized(mutex) {
							if (logicThread != Thread.currentThread()) throw new LogicThreadAlteredException(name, log, LogicModule.this);
							logicPaused.setFlag(false);
						}
						if (log.isLoggable(Level.WARNING)) log.warning(name + ": Logic resumed.");
					}
					
					sleepTime = (long)logicPeriod - (System.currentTimeMillis() - lastLogicRun);
					if (sleepTime > 0) {
						if (log.isLoggable(Level.FINER)) log.finer(name + ": Sleeping for " + sleepTime + " ms.");
						Thread.sleep(sleepTime);
					}
					
					if (logicThread != Thread.currentThread()) throw new LogicThreadAlteredException(name, log, LogicModule.this);
					
					try {
						if (log.isLoggable(Level.FINER)) log.finer(name + ": Logic iteration.");
						logicWatch.start();
						beforeLogic(name);
						try {
							lastLogicRun = System.currentTimeMillis();
							if (shouldExecuteLogic()) {
								if (firstLogic) {
									logic.beforeFirstLogic();
									firstLogic = false;
								}
								logic.logic();
							} else {
								if (log.isLoggable(Level.INFO)) log.info(name + ": Logic should not run now...");
							}
						} catch (Exception e1) {
							try {
								afterLogicException(name, e1);
							} catch (Exception e2) {
								if (log.isLoggable(Level.SEVERE)) log.severe(ExceptionToString.process(name + ": afterLogicException() exception.", e2));
							}
							throw e1;
						}
						afterLogic(name);
						if (log.isLoggable(Level.FINE)) log.fine(name + ": Logic iteration finished (" + logicWatch.stopStr() + ").");
					} catch (ComponentPausedException e) {
						if (log.isLoggable(Level.INFO)) log.info(name + ": pausing the thread, received ComponentPausedException from " + e.getOrigin() + ".");
						logicShouldPause.setFlag(true);
					}
					
				}
				
			} catch (LogicThreadAlteredException e1) {
				if (log.isLoggable(Level.WARNING)) log.warning(name + ": Logic thread has been altered, this one is not executed anymore.");
				// the logic is not being executed in this thread anymore...
				return;
			} catch (InterruptedException e2) {
				if (log.isLoggable(Level.WARNING)) log.warning(name + ": Interrupted!");
			} catch (PogamutInterruptedException e3) {
				if (log.isLoggable(Level.WARNING)) log.warning(name + ": Interrupted!");
			} catch (ComponentNotRunningException e5) {
				log.log(log.getLevel(), name + ": stopping the thread, received ComponentNotRunningException from " + e5.getOrigin() + ".");
			} catch (Exception e4) {
				controller.fatalError(name + ": Logic iteration exception.", e4);
			} finally {
				synchronized(mutex) {
					if (logicThread != Thread.currentThread()) {
						return;
					}
					try {
						if (log.isLoggable(Level.FINE)) log.fine(name + ": Shutting down the logic.");
						logicWatch.start();
						logic.logicShutdown();
						if (log.isLoggable(Level.INFO)) log.info(name + ": Logic shutdown (" + logicWatch.stopStr() + ").");
					} catch (Exception e) {
						controller.fatalError(name + ": Logic shutdown exception.", e);
					} finally {
						if (logicThread == Thread.currentThread()) {
							clearLogicRunningVars();
							logicThread = null;
						}
						if (log.isLoggable(Level.WARNING)) log.warning(name + ": Logic thread stopped.");
					}
				}
			}
		}
		
	}

}
