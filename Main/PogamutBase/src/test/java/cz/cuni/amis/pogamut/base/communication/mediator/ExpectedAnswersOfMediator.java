package cz.cuni.amis.pogamut.base.communication.mediator;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;

import org.easymock.IAnswer;

import cz.cuni.amis.pogamut.base.communication.mediator.testevents.StubBeginMessage;
import cz.cuni.amis.pogamut.base.communication.mediator.testevents.StubBombInfo;
import cz.cuni.amis.pogamut.base.communication.mediator.testevents.StubBotDamaged;
import cz.cuni.amis.pogamut.base.communication.mediator.testevents.StubBumped;
import cz.cuni.amis.pogamut.base.communication.mediator.testevents.StubEndMessage;
import cz.cuni.amis.pogamut.base.communication.mediator.testevents.StubGameInfo;
import cz.cuni.amis.pogamut.base.communication.mediator.testevents.StubHearPickup;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.flag.ImmutableFlag;
import java.util.logging.Level;

public class ExpectedAnswersOfMediator implements IAnswer<IWorldChangeEvent> {
	private LinkedList<IWorldChangeEvent> eventsQueue = new LinkedList<IWorldChangeEvent>();
	private IWorldChangeEvent lastEvent;
	private LogCategory logger;
	private IMediator testedMediator;
	
	private Flag<Boolean> eventsQueueEmpty = new Flag<Boolean>(true);
	
	public ExpectedAnswersOfMediator(LogCategory logger, IMediator testedMediator
//			,String planFileName
			) {
		
		assertTrue("Passed logger cannot be empty.", logger != null);
//		assertTrue("planFileName has to contain the name of the plan to execute.",
//				planFileName != null && !planFileName.isEmpty());
		
		this.logger = logger;

		eventsQueue.add(new StubBeginMessage());
		eventsQueue.add(new StubBombInfo());
		eventsQueue.add(new StubBotDamaged());
		eventsQueue.add(new StubBumped());
		eventsQueue.add(new StubEndMessage());
		eventsQueue.add(new StubGameInfo());
		eventsQueue.add(new StubHearPickup());
		eventsQueueEmpty.setFlag(false);
		
		this.logger = logger;
		this.testedMediator = testedMediator;
//		deserializeTest(planFileName);
	}
	
	public ImmutableFlag<Boolean> getEventsQueueEmpty() {
		return eventsQueueEmpty.getImmutable();
	}
	
	/**
	 * Returns next event to be processed.
	 * @return next event 
	 */
	@Override
	public IWorldChangeEvent answer() throws Throwable {
		if (eventsQueue.size() > 0) {
			lastEvent = eventsQueue.pop();
			if (logger.isLoggable(Level.INFO)) logger.info("Returned event: " + lastEvent.toString());
		}
		else {
			eventsQueueEmpty.setFlag(true);
			lastEvent = null;
			if (logger.isLoggable(Level.INFO)) logger.info("Event queue empty");
			Thread.currentThread().interrupt();
		}		
		return lastEvent;
	}
	
	/**
	 * Returns last event that was passed for processing.
	 * @return last event
	 */
	public IWorldChangeEvent getLastEvent() {
		return lastEvent;
	}
	
	public int getNumberOfQueuedEvents() {
		return eventsQueue.size();
	}
	
	public LinkedList<IWorldChangeEvent> getList() {
		return eventsQueue;
	}
}