package cz.cuni.amis.pogamut.base.communication.mediator;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.communication.mediator.impl.Mediator;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEventOutput;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldChangeEventInput;
import cz.cuni.amis.pogamut.base.component.bus.ComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.factory.guice.GuiceAgentModule;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.utils.flag.FlagListener;

import cz.cuni.amis.tests.BaseTest;
				
public class Test01_Mediator extends BaseTest {
	private static IMocksControl ctrl;

	private GuiceAgentModule module;
	/**
	 * Contains a mock testing correct outputs.
	 */
	private IWorldChangeEventOutput mockOutput;
	private MockMediatorOutput mediatorOutput;
	/**
	 * Contains a mock object, that generates inputs.
	 */
	private IWorldChangeEventInput mockInput;
	private MockMediatorInput mediatorInput;
	private IMediator mediator;
	private Thread mainThread;
	private RunningListener runningListener = new RunningListener();
	private String error = null;
	
	private String planFileName;
	private Class<? extends IMediator> mediatorClass = Mediator.class;
	
	private IAgentId agentId = new AgentId("MediatorTestCase");
	private IAgentLogger logger = new AgentLogger(agentId);
	private LogCategory log = logger.getCategory("Test");
	
	@Before
	public void startUp() {
		mainThread = Thread.currentThread();
		ctrl = createStrictControl();
	}

	@Test
	public void testMediator() {
		doTest();
	}

	class AnswerMatcher implements IArgumentMatcher {
		private ExpectedAnswersOfMediator expectedAnswersOfMediator;

		/**
		 * This method appends the error string to the buffer.
		 * This is called if comparison in EasyMock fails. 
		 */
		@Override
		public void appendTo(StringBuffer buffer) {
			buffer.append(error);
		}

		/**
		 * Compares generated event to expected event.
		 * @param argument generated event
		 * @return true if correct
		 */
		@Override
		public boolean matches(Object argument) {
			// gets last generated event, to be compared with output of the mediator.
			IWorldChangeEvent last = expectedAnswersOfMediator.getLastEvent();
			// we need some sort of a more serious comparison, but this should do for now.
			if ((argument instanceof IWorldChangeEvent && last.toString()
					.equals(((IWorldChangeEvent) argument).toString()))) {
				if (log.isLoggable(Level.INFO)) log.info("Argument equals to event: " + last.toString());
				return true;
			} else {
				error = "Unexpected argument received for an AnswerMatcher object: "
						+ argument.toString()
						+ " doesnt equal expected "
						+ last.toString();
				Thread.currentThread().interrupt();
				mainThread.interrupt();
				return false;
			}
		}

		public AnswerMatcher(ExpectedAnswersOfMediator expectedAnswersOfMediator) {
			this.expectedAnswersOfMediator = expectedAnswersOfMediator;
		}
	}

	public IWorldChangeEvent eqEvent(ExpectedAnswersOfMediator event) {
		EasyMock.reportMatcher(new AnswerMatcher(event));
		return null;
	}

	private class RunningListener implements FlagListener<Boolean> {
		@Override
		public void flagChanged(Boolean changedValue) {
			if (changedValue == false)
				mainThread.interrupt();
		}
	}

	public void doTest() {
		logger.addDefaultConsoleHandler();
		logger.setLevel(Level.ALL);
		IComponentBus bus = new ComponentBus(logger);
		
		// construct mocks
		mockOutput = ctrl.createMock(IWorldChangeEventOutput.class);
		mockInput = ctrl.createMock(IWorldChangeEventInput.class);
		
		mediatorOutput = new MockMediatorOutput(logger, bus, mockOutput);
		mediatorInput = new MockMediatorInput(logger, bus, mockInput);
				
		mediator = new Mediator(mediatorOutput, bus, logger);
		mediator.setConsumer(mediatorInput);
		
		if (log.isLoggable(Level.INFO)) log.info("Starting MediatorTestCase of " + mediator.getClass().getName() + ".");
				
		ExpectedAnswersOfMediator expectedAnswersOfMediator = new ExpectedAnswersOfMediator(log, mediator);

		assertTrue("Plan cannot be empty.", expectedAnswersOfMediator.getNumberOfQueuedEvents() > 0);

		ctrl.checkOrder(false);
		
		mockOutput.getEvent();
		expectLastCall().andAnswer(expectedAnswersOfMediator).atLeastOnce();

		mockInput.notify(eqEvent(expectedAnswersOfMediator));
		expectLastCall().times(expectedAnswersOfMediator.getNumberOfQueuedEvents());
		
		// replay
		ctrl.replay();
		
		// start mediator
		mediatorInput.getController().manualStart("starting test");
		mediatorOutput.getController().manualStart("starting test");
		
		Boolean result = expectedAnswersOfMediator.getEventsQueueEmpty().waitFor(10000, true);
		Assert.assertTrue("all events must be processed", result != null && result);
		
		mediatorInput.getController().manualStop("stopping the test");
		
		// verify
		ctrl.verify();
		
		System.out.println("---/// TEST OK ///---");
	}
	
}
