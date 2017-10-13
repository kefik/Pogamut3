package cz.cuni.amis.pogamut.ut2004multi.communication.worldview.stubs;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;
import cz.cuni.amis.pogamut.base.communication.exception.CommunicationException;
import cz.cuni.amis.pogamut.base.communication.mediator.IMediator;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldChangeEventInput;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.utils.Job;
import cz.cuni.amis.utils.collections.MyCollections;
import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.flag.ImmutableFlag;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MediatorStub implements IMediator {
	
	private static int num = 0;
	
	private Flag<Boolean> running = new Flag<Boolean>(false);
	private BlockingQueue<IWorldChangeEvent> eventsQueue = new LinkedBlockingQueue<IWorldChangeEvent>();
	private LogCategory log;
	private IToken token;
	private IWorldChangeEventInput consumer;

	public MediatorStub(IAgentLogger log, IWorldChangeEvent[] events) {
		pushEvent(events);
		this.token = Tokens.get("MediatorStub" + (++num));
		this.log = log.getCategory(this);
	}

	public MediatorStub(IAgentLogger logger) {
		this.token = Tokens.get("MediatorStub" + (++num));
		this.log = logger.getCategory(this);
	}
	
	public int getEventQueueLength() {
		return eventsQueue.size();
	}

	public void pushEvent(IWorldChangeEvent event) {
		eventsQueue.add(event);
	}

	public void pushEvent(IWorldChangeEvent[] events) {
		eventsQueue.addAll(MyCollections.toList(events));
	}

	public void clearEventsQueue() {
		if (log.isLoggable(Level.WARNING)) log.warning("clearing events queue");
		eventsQueue.clear();
	}

	public ImmutableFlag<Boolean> getRunning() {
		return running.getImmutable();
	}
	
	public void setConsumer(IWorldChangeEventInput consumer) {
		this.consumer = consumer;
	}
	
	public IToken getComponentId() {
		return token;
	}
	
	public LogCategory getLog() {
		return log;
	}

	public void kill() {
		running.setFlag(false);
	}

	public void start() throws CommunicationException {
		running.setFlag(true);
		new Job() {

			@Override
			protected void job() throws Exception {
				try {
					while (eventsQueue.size() > 0) {
						if (!running.getFlag()) {
							if (log.isLoggable(Level.WARNING)) log.warning("Me: Stop requested, stopping mediator.");
							break;
						}
						IWorldChangeEvent event = eventsQueue.poll(100, TimeUnit.MILLISECONDS);
						if (!running.getFlag()) {
							if (log.isLoggable(Level.WARNING)) log.warning("Me: Stop requested, stopping mediator.");
							break;
						}
						if (event == null) continue;
						if (log.isLoggable(Level.INFO)) log.info("event " + event);
						consumer.notify(event);
					}
					stop();
					clearEventsQueue();
				} catch (Exception e) {
					e.printStackTrace();
					Assert.fail("WorldView failed to process event...");
				}
			}

		}.startJob();
	}

	public void stop() {
		running.setFlag(false);
	}

}
