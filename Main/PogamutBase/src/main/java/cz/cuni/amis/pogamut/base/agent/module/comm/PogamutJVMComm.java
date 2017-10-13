package cz.cuni.amis.pogamut.base.agent.module.comm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.IObservingAgent;
import cz.cuni.amis.pogamut.base.agent.impl.AbstractAgent;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentNotRunningException;
import cz.cuni.amis.pogamut.base.utils.PogamutPlatform;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.utils.ExceptionToString;
import cz.cuni.amis.utils.Tuple2;
import cz.cuni.amis.utils.maps.HashMapSet;

/**
 * Simple support for inter-agent in-single-JVM communication.
 * 
 * This object can be used for setting up simple communication via {@link IWorldView}. 
 * You can use {@link PogamutJVMComm#getInstance()} to obtain singleton-instance
 * and register your agent to some channel using {@link PogamutJVMComm#registerAgent(IObservingAgent, int)}.
 * DO NOT FORGET TO USE {@link PogamutJVMComm#unregisterAgent(IObservingAgent)} once your agent has finished its lifecycle,
 * otherwise you would likely to leak memory.
 * 
 * Any agent may send events to registered agents at any time via {@link PogamutJVMComm#send(IWorldChangeEvent, int)}
 * or {@link PogamutJVMComm#broadcast(IWorldChangeEvent)}. 
 * 
 * The object uses {@link IWorldView#notify(IWorldChangeEvent)} to propagate events to respective agents' worldviews.
 *  
 * @author Jimmy
 */
public class PogamutJVMComm {

	protected static Object instanceMutex = new Object();
	
	protected static PogamutJVMComm instance;
	
	protected static ConcurrentLinkedQueue<PogamutJVMComm> comms = new ConcurrentLinkedQueue<PogamutJVMComm>();

	/**
	 * Getter for JVM singleton.
	 * @return
	 */
	public static PogamutJVMComm getInstance() {
		if (instance == null) {
			synchronized(instanceMutex) {
				if (instance == null) {
					instance = new PogamutJVMComm();
					((LogCategory)instance.getLog()).addConsoleHandler();
					instance.getLog().setLevel(Level.INFO);
				}
			}
		}
		return instance;
	}
	
	public static void platformClose() {
		while(comms.size() > 0) {
			comms.poll().destroy();
		}
	}
	
	/**
	 * If you wish to listen on all CHANNELS you can use this constant.
	 */
	public static final int ALL_CHANNELS = -1;
	
	/**
	 * {@link AbstractAgent#getComponentId()} maps to actual 'agent' registered + number of registration for a given agent.
	 */
	protected Map<IAgentId, Tuple2<IObservingAgent, Integer>> registeredAgents = new HashMap<IAgentId, Tuple2<IObservingAgent, Integer>>();
	
	/**
	 * Agents registered for "ALL_CHANNELS".
	 */
	protected Set<IObservingAgent> allChannels = new HashSet<IObservingAgent>();
	
	/**
	 * Agents registered for respective channels.
	 */
	protected HashMapSet<Integer, IObservingAgent> channels = new HashMapSet<Integer, IObservingAgent>();
	
	/**
	 * Mutex for reading/writing channels/allChannels and sending events.
	 */
	protected ReadWriteLock lock = new ReentrantReadWriteLock(true);
	
	protected Lock readLock = lock.readLock();
	
	protected Lock writeLock = lock.writeLock();

	protected Logger log;		
	
	public PogamutJVMComm() {
		this(null);
	}
	
	/**
	 * @param log can be null, default will be provided
	 */
	public PogamutJVMComm(Logger log) {
		this.log = log;
		synchronized(comms) {
			if (this.log == null) {
				this.log = new LogCategory("AgentJVMComm" + comms.size());				
			}
			comms.add(this);
		}
	}
	
	public Logger getLog() {
		return log;
	}
	
	
	// ===========================
	// AGENT REGISTRATION TRACKING
	// ===========================
	
	/**
	 * @param agent
	 * @return how many times is 'agent' registered (after increase)
	 */
	protected int incRegisteredAgent(IObservingAgent agent) {
		synchronized(registeredAgents) {
			Tuple2<IObservingAgent, Integer> record = registeredAgents.get(agent.getComponentId());
			if (record == null) {
				record = new Tuple2<IObservingAgent, Integer>(agent, 0);
				registeredAgents.put(agent.getComponentId(), record);
			}
			// record != null
			if (record.getFirst() != agent) {
				throw new RuntimeException("agent.getComponentId() clash! Under " + agent.getComponentId() + " is registered agent " + record.getFirst() + " NOT AGENT " + agent + ".");
			}
			record.setSecond(record.getSecond()+1);
			return record.getSecond();
		}
	}

	/**
	 * @param agent
	 * @return how many times is 'agent' still registered (after decrease)
	 */
	protected int decRegisteredAgent(IObservingAgent agent) {
		synchronized(registeredAgents) {
			Tuple2<IObservingAgent, Integer> record = registeredAgents.get(agent.getComponentId());
			if (record == null) {
				throw new RuntimeException("Attempt to decrease registration count for agent that is not registered, agent.getComponentId() == " + agent.getComponentId() + ".");
			}
			record.setSecond(record.getSecond()-1);
			if (record.getSecond() == 0) {
				registeredAgents.remove(agent.getComponentId());
			}
			return record.getSecond();
		}
	}
	
	// =======================
	// REGISTER AGENT
	// =======================
	
	/**
	 * Register an agent to receive events send through 'channel'. 
	 * 
	 * Use {@link PogamutJVMComm#ALL_CHANNELS} constant to listen to all channels (existing or future).
	 * 
	 * @param agent
	 * @param channel
	 */
	public void registerAgent(IObservingAgent agent, int channel) {
		if (channel < 0 && channel != ALL_CHANNELS) {
			throw new RuntimeException("channel == " + channel + " < 0, INVALID");
		}
		if (writeLock.tryLock()) {
			try {
				registerAgentUnsyncImpl(agent, channel);
			} finally {
				writeLock.unlock();
			}
		} else {
			execute(new RegisterAgent(agent, channel), true);
		}
	}
		
	protected void registerAgentSyncImpl(IObservingAgent agent, int channel) {
		writeLock.lock();
		try {
			registerAgentUnsyncImpl(agent, channel);
		} finally {
			writeLock.unlock();
		}
	}
	
	protected void registerAgentUnsyncImpl(IObservingAgent agent, int channel) {
		if (channel == ALL_CHANNELS) {
			// REGISTER FOR RECIEVING ANY MESSAGES
			if (allChannels.contains(agent)) {
				// ALREADY REGISTERED
				if (log != null && log.isLoggable(Level.WARNING)) log.warning("Agent " + agent.getComponentId() + " is already registered for ALL_CHANNELS (ignoring this request).");
				return;
			}
			allChannels.add(agent);
		} else {
			if (channels.get(channel).contains(agent)) {
				// ALREADY REGISTERED
				if (log != null && log.isLoggable(Level.WARNING)) log.warning("Agent " + agent.getComponentId() + " is already registered for channel " + channel + " (ignoring this request).");
				return;
			}
			channels.add(channel, agent);
		}
		int registerCount = incRegisteredAgent(agent);
		if (log != null && log.isLoggable(Level.INFO)) log.info("Registered " + agent.getComponentId() + " for " + (channel == ALL_CHANNELS ? "ALL_CHANNELS" : "channel " + channel) + ". Agent is registered " + registerCount + "x (in total).");
	}

	// =======================
	// IS AGENT REGISTERED
	// =======================

	/**
	 * Whether an 'agent' is listening on 'channel'.
	 * 
	 * Use {@link PogamutJVMComm#ALL_CHANNELS} constant to check whether an agent is listening to ALL CHANNELS (existing and future).
	 * 
	 * Potentially BLOCKING METHOD, waiting for {@link PogamutJVMComm#readLock} to be locked.
	 * 
	 * @param agent
	 * @param channel
	 * @return
	 */
	public boolean isAgentRegistered(IObservingAgent agent, int channel) {
		if (channel < 0 && channel != ALL_CHANNELS) {
			throw new RuntimeException("channel == " + channel + " < 0, INVALID");
		}
		readLock.lock();
		try {
			if (channel == ALL_CHANNELS) {
				return allChannels.contains(agent);
			} else {
				return getChannel(channel).contains(agent);
			}
		} finally {
			readLock.unlock();
		}
	}
	
	protected Set<IObservingAgent> getChannel(int channel) {
		if (channel == ALL_CHANNELS) {
			return allChannels;
		} else {
			if (channels.containsKey(channel)) {
				return channels.get(channel);
			}
			synchronized(channels) {
				return channels.get(channel);
			}
		}
	}
	
	// =============================
	// UNREGISTER AGENT FROM CHANNEL
	// =============================
	
	/**
	 * Removes agent from listening to some channels.
	 * 
	 * If you specify ALL_CHANNELS, agent is registered from ALL_CHANNELS (does not mean it is unregistered from respective channels, it
	 * is "unregister-from-broadcast-listening").
	 * 
	 * @param agent
	 * @param channel
	 */
	public void unregisterAgent(IObservingAgent agent, int channel) {		
		if (channel < 0 && channel != ALL_CHANNELS) {
			throw new RuntimeException("channel == " + channel + " < 0, INVALID");
		}
		if (writeLock.tryLock()) {
			try {
				unregisterAgentUnsyncImpl(agent, channel);
			} finally {
				writeLock.unlock();
			}
		} else {
			execute(new UnregisterAgentFromChannel(agent, channel), false);
		}
	}
	
	protected void unregisterAgentSyncImpl(IObservingAgent agent, int channel) {
		writeLock.lock();
		try {
			unregisterAgentUnsyncImpl(agent, channel);
		} finally {
			writeLock.unlock();
		}
	}

	protected void unregisterAgentUnsyncImpl(IObservingAgent agent, int channel) {
		if (channel == ALL_CHANNELS) {
			if (allChannels.remove(agent)) {
				int registerCount = decRegisteredAgent(agent);
				if (log != null && log.isLoggable(Level.INFO)) log.info("UNregistered " + agent.getComponentId() + " from ALL_CHANNELS. " + (registerCount == 0 ? "Agent is now fully UNREGISTERED." : "Agent remains registered for other channels (" + registerCount + "x in total)."));
				if (registeredAgents.size() == 0) {
					shutdown(false);
				}
			}
		} else {
			if (getChannel(channel).remove(agent)) {
				int registerCount = decRegisteredAgent(agent);
				if (log != null && log.isLoggable(Level.INFO)) log.info("UNregistered " + agent.getComponentId() + " from channel " + channel + ". " + (registerCount == 0 ? "Agent is now fully UNREGISTERED." : "Agent remains registered for other channels (" + registerCount + "x in total)."));
				if (registeredAgents.size() == 0) {
					shutdown(false);
				}
			}
		}			
	}

	// =======================
	// UNREGISTER AGENT
	// =======================
	
	/**
	 * Totally unregister the agent (all channels + ALL_CHANNELS).
	 * @param bot
	 */
	public void unregisterAgent(IObservingAgent agent) {
		if (writeLock.tryLock()) {
			try {
				unregisterAgentUnsyncImpl(agent);
			} finally {
				writeLock.unlock();
			}
		} else {
			execute(new UnregisterAgent(agent), false);
		}
		
	}
	
	protected void unregisterAgentSyncImpl(IObservingAgent agent) {
		writeLock.lock();
		try {
			unregisterAgentUnsyncImpl(agent);
		} finally {
			writeLock.unlock();
		}
	}
	
	protected void unregisterAgentUnsyncImpl(IObservingAgent agent) {
		synchronized(channels) {
			for (Integer channel : channels.keySet()) {
				unregisterAgentUnsyncImpl(agent, channel);
			}
		}
		unregisterAgentUnsyncImpl(agent, ALL_CHANNELS);
	}

	// =======================
	// SEND EVENT
	// =======================
	
	/**
	 * Send 'event' to 'channel'.
	 * 
	 * Note that this version does not have "SENDER", thus it may notify even the SENDER with 'event'
	 * if it is subscribed to channel 'channel'.
	 * 
	 * If you wish to "sendToAllOthers" use {@link PogamutJVMComm#sendToOthers(IWorldChangeEvent, int, IObservingAgent)}.
	 * 
	 * @param event
	 * @param channel
	 */
	public void send(IWorldChangeEvent event, int channel) {
		execute(new Send(event, channel), false);
	}
	
	/**
	 * Send 'event' to 'channel' but does not notify 'sender'.
	 * 
	 * Note that 'sender' (that should be you) won't receive the 'event'.
	 * 
	 * @param event
	 * @param channel
	 */
	public void sendToOthers(IWorldChangeEvent event, int channel, IObservingAgent sender) {
		execute(new SendToOthers(event, channel, sender), false);
	}	

	protected void sendSyncImpl(IWorldChangeEvent event, int channel) {
		readLock.lock();
		try {
			sendUnsyncImpl(event, channel);
		} finally {
			readLock.unlock();
		}
	}
	
	protected void sendToOthersSyncImpl(IWorldChangeEvent event, int channel, IObservingAgent sender) {
		readLock.lock();
		try {
			sendToOthersUnsyncImpl(event, channel, sender);
		} finally {
			readLock.unlock();
		}
	}
	
	protected void sendUnsyncImpl(IWorldChangeEvent event, int channel) {
		if (channel == ALL_CHANNELS) {
			broadcastUnsyncImpl(event);
		} else {
			for (IObservingAgent agent : getChannel(channel)) {
				sendToAgentUnsyncImpl(agent, event);
			}
			for (IObservingAgent agent : allChannels) {
				sendToAgentUnsyncImpl(agent, event);
			}			
		}
	}
	
	protected void sendToOthersUnsyncImpl(IWorldChangeEvent event, int channel, IObservingAgent sender) {
		if (channel == ALL_CHANNELS) {
			broadcastToOthersUnsyncImpl(event, sender);
		} else {
			for (IObservingAgent agent : getChannel(channel)) {
				if (agent != sender) {
					sendToAgentUnsyncImpl(agent, event);
				}
			}
			for (IObservingAgent agent : allChannels) {
				if (agent != sender) {
					sendToAgentUnsyncImpl(agent, event);
				}
			}			
		}
	}
	
	protected void sendToAgentUnsyncImpl(IObservingAgent agent, IWorldChangeEvent event) {
		try {
			if (log != null && log.isLoggable(Level.FINE))log.fine(event + " -> " + agent.getComponentId());
			agent.getWorldView().notify(event);
		} catch (ComponentNotRunningException e1) {
			// NOTHING TO SEE, move along...	
			if (log != null && log.isLoggable(Level.WARNING)) log.warning("Agent " + agent.getComponentId() + " is not running, did not receive: " + event);
		} catch (Exception e2) {
			if (log != null && log.isLoggable(Level.WARNING)) log.warning(ExceptionToString.process("Agent " + agent.getComponentId() + " failed to process " + event + ".", e2));
		}
	}
	
	// =======================
	// BROADCAST EVENT
	// =======================

	/**
	 * Broadcast 'event' to all channels == all listening agents.
	 * 
	 * Note that this version does not have "SENDER", thus it may notify even the SENDER with 'event'
	 * if it is subscribed to any / all channels.
	 * 
	 * If you wish to "broadcastToAllOthers" use {@link PogamutJVMComm#broadcastToOthers(IWorldChangeEvent, IObservingAgent)}.
	 * 
	 * @param event
	 */
	public void broadcast(IWorldChangeEvent event) {
		execute(new Broadcast(event), false);
	}
	
	/**
	 * Broadcast 'event' to all channels == all listening agents.
	 * 
	 * Note that 'sender' (that should be you) won't receive the 'event'.
	 * 
	 * @param event
	 * @param sender 
	 */
	public void broadcastToOthers(IWorldChangeEvent event, IObservingAgent sender) {
		if (sender == null) {
			if (log != null && log.isLoggable(Level.WARNING)) log.warning("broadcast(event, null) called, sender unspecified");
			broadcast(event);
		} else {
			execute(new BroadcastToOthers(event, sender), false);
		}
	}
	
	protected void broadcastSyncImpl(IWorldChangeEvent event) {
		readLock.lock();
		try {
			broadcastUnsyncImpl(event);
		} finally {
			readLock.unlock();
		}
	}
	
	protected void broadcastToOthersSyncImpl(IWorldChangeEvent event, IObservingAgent sender) {
		readLock.lock();
		try {
			broadcastToOthersUnsyncImpl(event, sender);
		} finally {
			readLock.unlock();
		}
	}
	
	protected void broadcastUnsyncImpl(IWorldChangeEvent event) {
		List<Integer> existingChannels;
		synchronized(channels) {
			 existingChannels = new ArrayList<Integer>(channels.keySet());
		}
		for (Integer channel : existingChannels) {
			for (IObservingAgent agent : getChannel(channel)) {
				sendToAgentUnsyncImpl(agent, event);
			}
		}
		for (IObservingAgent agent : allChannels) {
			sendToAgentUnsyncImpl(agent, event);
		}		
	}
	
	protected void broadcastToOthersUnsyncImpl(IWorldChangeEvent event, IObservingAgent sender) {
		List<Integer> existingChannels;
		synchronized(channels) {
			 existingChannels = new ArrayList<Integer>(channels.keySet());
		}
		for (Integer channel : existingChannels) {
			for (IObservingAgent agent : getChannel(channel)) {
				if (agent != sender) {
					sendToAgentUnsyncImpl(agent, event);
				}
			}
		}
		for (IObservingAgent agent : allChannels) {
			if (agent != sender) {
				sendToAgentUnsyncImpl(agent, event);
			}
		}		
	}
	
	// =======================
	// CLEANUP
	// =======================
	
	/**
	 * UTILITY METHOD FOR DESTROYING THE COMMUNICATION.
	 * 
	 * Call this method if you want the instance to be safely GC()ed.
	 * 
	 * Note that {@link PogamutPlatform#close()} will call this for you on {@link PogamutJVMComm#getInstance()}.
	 */
	public void destroy() {
		try {
			try {
				shutdown(true);
			} finally {
				try {
					synchronized(channels) {
						channels.clear();
					}
				} finally {
					try {
						synchronized(allChannels) {
							allChannels.clear();
						}
					} finally {
						synchronized(registeredAgents) {
							registeredAgents.clear();
						}					
					}
				}
			}
		} catch (Exception e) {
			if (log != null && log.isLoggable(Level.WARNING)) log.warning(ExceptionToString.process("Failed to fully PogamutJVMComm.destroy().", e));
		}
	}
	
	// =======================
	// JOB PROCESSING
	// =======================
	
	protected Object executorMutex = new Object();
	
	protected ThreadPoolExecutor executor = null;
	
	protected Object numberOfRegisterAgentPendingMutex = new Object();
	
	protected int numberOfRegisterAgentPending = 0;
	
	protected void execute(Runnable job, boolean forceStart) {
		if (executor == null) {
			synchronized(executorMutex) {
				if (executor == null) {
					if (forceStart || registeredAgents.size() > 0) {
						if (log != null && log.isLoggable(Level.INFO)) log.info("Starting thread pool executor.");
						executor = new ThreadPoolExecutor(1, 20, 10000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
					}
				}
			}
		}
		if (executor == null) return;
		executor.execute(job);
	}
	
	protected void shutdown(boolean forced) {
		if (executor != null) {
			synchronized(executorMutex) {
				if (executor != null) {
					if (!forced) {
						synchronized(numberOfRegisterAgentPendingMutex) {
							if (numberOfRegisterAgentPending > 0) {
								if (log != null && log.isLoggable(Level.INFO)) log.info("Won't shutdown thread pool executor, there are unprocessed agent-registration jobs in queue and shutdown is NOT forced.");
								return;
							}
						}
					}
					if (log != null && log.isLoggable(Level.INFO)) log.info("Shutting down thread pool executor.");
					try {
						executor.shutdownNow();
					} catch (Exception e) {
						if (log != null && log.isLoggable(Level.WARNING)) log.warning(ExceptionToString.process("Error shutting down thread pool executor.", e));
					}
					executor = null;
				}
			}
		}
	}
	
	protected class RegisterAgent implements Runnable {

		protected IObservingAgent agent;
		protected int channel;
		
		public RegisterAgent(IObservingAgent agent, int channel) {
			synchronized(numberOfRegisterAgentPendingMutex) {
				++numberOfRegisterAgentPending;
			}
			this.agent = agent;
			this.channel = channel;
		}
		
		@Override
		public void run() {
			try {
				registerAgentSyncImpl(agent, channel);
			} finally {
				synchronized(numberOfRegisterAgentPendingMutex) {
					--numberOfRegisterAgentPending;
				}
			}
		}
		
	}
	
	protected class UnregisterAgentFromChannel implements Runnable {

		protected IObservingAgent agent;
		protected int channel;
		
		public UnregisterAgentFromChannel(IObservingAgent agent, int channel) {
			this.agent = agent;
			this.channel = channel;
		}
		
		@Override
		public void run() {
			unregisterAgentSyncImpl(agent, channel);
		}
		
	}
	
	protected class UnregisterAgent implements Runnable {

		protected IObservingAgent agent;
		
		public UnregisterAgent(IObservingAgent agent) {
			this.agent = agent;
		}
		
		@Override
		public void run() {
			unregisterAgentSyncImpl(agent);
		}
		
	}
	
	protected class Send implements Runnable {

		protected IWorldChangeEvent event;
		protected int channel;

		public Send(IWorldChangeEvent event, int channel) {
			this.event = event;
			this.channel = channel;
		}
		
		@Override
		public void run() {
			sendSyncImpl(event, channel);
		}
		
	}
	
	protected class SendToOthers implements Runnable {

		protected IWorldChangeEvent event;
		protected int channel;
		protected IObservingAgent sender;

		public SendToOthers(IWorldChangeEvent event, int channel, IObservingAgent sender) {
			this.event = event;
			this.channel = channel;
			this.sender = sender;
		}
		
		@Override
		public void run() {
			sendToOthersSyncImpl(event, channel, sender);
		}
		
	}
	
	protected class Broadcast implements Runnable {

		protected IWorldChangeEvent event;
		
		public Broadcast(IWorldChangeEvent event) {
			this.event = event;
		}
		
		@Override
		public void run() {
			broadcastSyncImpl(event);
		}
		
	}
	
	protected class BroadcastToOthers implements Runnable {

		protected IWorldChangeEvent event;
		protected IObservingAgent sender;

		public BroadcastToOthers(IWorldChangeEvent event, IObservingAgent sender) {
			this.event = event;
			this.sender = sender;
		}
		
		@Override
		public void run() {
			broadcastToOthersSyncImpl(event, sender);
		}
		
	}

}
