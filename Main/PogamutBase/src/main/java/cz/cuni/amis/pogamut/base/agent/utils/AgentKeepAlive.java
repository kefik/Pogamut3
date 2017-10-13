package cz.cuni.amis.pogamut.base.agent.utils;


import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.state.WaitForAgentStateChange;
import cz.cuni.amis.pogamut.base.agent.state.level0.IAgentState;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateDown;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateGoingUp;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateUp;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.utils.ExceptionToString;
import cz.cuni.amis.utils.NullCheck;
import java.util.logging.Level;

public class AgentKeepAlive {
	
	private Object keepAliveMutex = new Object();
	
	private LogCategory log;
	
	private KeepAlive keepAlive = null;
	
	private boolean firstStart = false;
	
	private IAgent agent;
	
	private boolean running = false;
	
	private long reconnectMillis;

	public AgentKeepAlive(IAgent agent, long reconnectWaitMillis) {
		this.agent = agent;
		NullCheck.check(this.agent, "agent");
		this.log = agent.getLogger().getCategory(getClass().getSimpleName());
		NullCheck.check(this.log, "log initialization");
		this.reconnectMillis = reconnectWaitMillis;
	}

	public void start() {
		synchronized(keepAliveMutex) {
			if (running) return;
			IAgentState state = agent.getState().getFlag();
			firstStart = true;
			keepAlive = new KeepAlive();
		}
	}
	
	public void stop() {
		synchronized(keepAliveMutex) {
			if (keepAlive != null) {
				keepAlive.stop();
				keepAlive = null;
			}
		}
	}
	
	public Long getNextRestart() {
		synchronized(keepAliveMutex) {
			if (!running) return null;
			return keepAlive.getNextRestart();
		}
	}
	
	public boolean isRunning() {
		return running;
	}
			
	private class KeepAlive implements Runnable {
		
		private Thread reconnectThread = null;
		
		private boolean shouldRun = true;
	
		private boolean shouldReconnect = true;
		
		private long sleepMillis = -1;
		
		public KeepAlive() {
			this.reconnectThread = new Thread(this, agent.getName() + " reconnector");
			this.reconnectThread.start();
		}
		

		public void stop() {
			if (log.isLoggable(Level.WARNING)) log.warning("Stopping KeepAlive.");
			shouldReconnect = false;
			shouldRun = false;
			reconnectThread.interrupt();			
		}
		
		public Long getNextRestart() {
			synchronized(keepAliveMutex) {
				if (sleepMillis < 0) return null;
				return System.currentTimeMillis() - sleepMillis - reconnectMillis;
			}
		}

		@Override
		public void run() {
			Boolean result = null;
			while (shouldRun && !Thread.currentThread().isInterrupted()) {
				if (shouldReconnect) {			
					try {
						if (!firstStart) {
							synchronized(keepAliveMutex) {
								sleepMillis = System.currentTimeMillis();
							}
							if (log.isLoggable(Level.FINER)) log.finer("Next reconnect attempt in " + reconnectMillis + " ms.");
							Thread.sleep(reconnectMillis);
						}
						if (!shouldReconnect) break;
						try {
							IAgentState state = agent.getState().getFlag();
							if (state instanceof IAgentStateUp || state instanceof IAgentStateGoingUp) {
								shouldReconnect = false;
								continue;
							}
							if (firstStart) {
								if (log.isLoggable(Level.INFO)) log.info("Starting agent.");
								firstStart = false;
							} else {
								if (log.isLoggable(Level.INFO)) log.info("Restarting agent.");
							}
							
							agent.start();
							if (log.isLoggable(Level.INFO)) log.info("Agent started.");
						} catch (Exception e1) {
							if (!shouldReconnect) break;
							if (log.isLoggable(Level.INFO)) log.info("Agent did not start, killing agent (cleaning up).");
							try {														
								agent.kill();
							} catch (Exception e2) {
							}
						}
					} catch (Exception e) {
						if (!shouldReconnect) break;
						if (log.isLoggable(Level.SEVERE)) log.severe(ExceptionToString.process("Unhandled exception, stopping " + AgentKeepAlive.this.getClass().getSimpleName() + ".", e));
						break;
					}	
				}
				// if we reach here - the agent is running
				try {
					new WaitForAgentStateChange(agent.getState(), IAgentStateDown.class).await();
				} catch (Exception e) {					
				}
				shouldReconnect = agent.getState().getFlag() instanceof IAgentStateDown;				
			}
			reconnectThread = null;
			synchronized(keepAliveMutex) {
				if (keepAlive == this) {
					running = false;
					keepAlive = null;
				}
			}
			if (log.isLoggable(Level.WARNING)) log.warning("Stopped.");
		}	
	}
	
}
