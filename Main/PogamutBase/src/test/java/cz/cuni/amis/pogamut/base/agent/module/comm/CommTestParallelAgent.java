package cz.cuni.amis.pogamut.base.agent.module.comm;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

import org.junit.Ignore;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.module.IAgentLogic;
import cz.cuni.amis.pogamut.base.agent.module.LogicModule;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.AnnotationListenerRegistrator;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.EventListener;
import cz.cuni.amis.utils.flag.Flag;

@Ignore
public class CommTestParallelAgent extends ObservingAgent {

	private AnnotationListenerRegistrator listenerRegistrator;

	private int channel;

	private int eventCount = 0;
	
	private int eventCountFromMe = 0;

	private int totalEvents;
	
	private int totalEventsFromMe;

	private LogicModule<IAgent> logic;

	private int sendEvents;

	private CountDownLatch latch = new CountDownLatch(1);

	private boolean eventsSent = false;

	private Flag<Boolean> allEventsReceived = new Flag<Boolean>(false);
	
	public CommTestParallelAgent(int channel, int sendEvents, int totalAgents) {
		this.channel = channel;
		this.sendEvents = sendEvents;
		this.totalEventsFromMe = 4 * sendEvents;
		this.totalEvents = (totalAgents - 1) * 8 * sendEvents + totalEventsFromMe;
		getLogger().addDefaultConsoleHandler();
		getLog().setLevel(Level.INFO);
		listenerRegistrator = new AnnotationListenerRegistrator(this, getWorldView(), getLog());
		listenerRegistrator.addListeners();
		
		this.logic = new LogicModule<IAgent>(this, new IAgentLogic() {

			@Override
			public void beforeFirstLogic() {
			}

			@Override
			public long getLogicInitializeTime() {
				return 0;
			}

			@Override
			public long getLogicShutdownTime() {
				return 0;
			}

			@Override
			public void logic() {
				CommTestParallelAgent.this.logic();
			}

			@Override
			public void logicInitialize(LogicModule logicModule) {
			}

			@Override
			public void logicShutdown() {
			}
			
		});
	}	

	@EventListener(eventClass=CommTestParallelStartEvent.class)
	public void startEvent(CommTestParallelStartEvent event) {
		if (latch.getCount() == 0) {
			// IGNORE
			return;
		}
		log.info("START EVENT RECEIVED");		
		latch.countDown();
	}
	
	@EventListener(eventClass=CommTestParallelEvent.class)
	public void commTestEventListener(CommTestParallelEvent event) {		
		if (event == null) {
			throw new RuntimeException("NULL EVENT!");
		}
		
		if (event.origin == this) {
			++eventCountFromMe;			
		}

		++eventCount;
		
		if (eventCount == totalEvents) {
			log.info("REACHED SENSED EVENTS: " + eventCount);
			allEventsReceived.setFlag(true);
		}
		
		
	}
	
	protected void logic() {
		if (eventsSent) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			return;
		}
		try {
			log.info("WAITING FOR START EVENT");
			latch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted while awaiting start.", e);
		}		
		log.info("SENDING EVENTS");
		for (int i = 0; i < sendEvents; ++i) {
			CommTestParallelEvent event = new CommTestParallelEvent(CommTestParallelAgent.this);
			PogamutJVMComm.getInstance().send(event, channel);
			PogamutJVMComm.getInstance().sendToOthers(event, channel, this);
			PogamutJVMComm.getInstance().broadcast(event);
			PogamutJVMComm.getInstance().broadcastToOthers(event, this);
		}
		log.info("ALL EVENTS SENT");
		eventsSent = true;
	}

	
	@Override
	protected void startAgent() {
		super.startAgent();
		PogamutJVMComm.getInstance().registerAgent(this, channel);
		log.info("Listening on CHANNEL " + channel);
		PogamutJVMComm.getInstance().registerAgent(this, PogamutJVMComm.ALL_CHANNELS);
		log.info("Listening on ALL_CHANNEL");
	}
	
	@Override
	protected void stopAgent() {
		super.stopAgent();
		PogamutJVMComm.getInstance().unregisterAgent(this);
		log.info("STOPPED Listening");
	}
	
	@Override
	protected void killAgent() {
		super.killAgent();
		PogamutJVMComm.getInstance().unregisterAgent(this);
		log.info("STOPPED Listening");
	}

	public int getTotalEvents() {
		return totalEvents;
	}


	public int getEventsCount() {
		return eventCount;
	}

	public int getEventCountFromMe() {
		return eventCountFromMe;
	}

	public int getTotalEventsFromMe() {
		return totalEventsFromMe;
	}

	public Flag<Boolean> getAllEventsReceived() {
		return allEventsReceived;
	}
	
}
