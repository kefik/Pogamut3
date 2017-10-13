package cz.cuni.amis.pogamut.base.component.stub.component;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;
import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.IComponentEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.FatalErrorEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.PausedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.PausingEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.ResumedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.ResumingEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.StartedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.StartingEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.StartingPausedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.StoppedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.StoppingEvent;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Token;
import cz.cuni.amis.utils.token.Tokens;

public class ManualCheckComponent implements IComponent {

	private static int counter = 0;
	
	private Token token;

	private Queue<CheckEvent> expect = new LinkedList<CheckEvent>();

	private IComponentBus bus;

	private LogCategory log;

	public ManualCheckComponent(IAgentLogger logger, IComponentBus bus) {
		this(logger, bus, new CheckEvent[0]);
	}
	
	public ManualCheckComponent(IAgentLogger logger, IComponentBus bus, CheckEvent... events) {
		this.token = Tokens.get("ManualCheckComponent" + counter++);
		this.log = logger.getCategory(this);
		NullCheck.check(this.log, "log initialization");
		expect(events);
		this.bus = bus;
		this.bus.register(this);
	}
	
	public void expect(CheckEvent... events) {
		for (CheckEvent event : events) { 
			expect(event);
		}
	}

	public void expect(CheckEvent event) {
		expect.add(event);
	}
	
	public int getExpectSize() {
		return expect.size();
	}
	
	public void manualNotify(IComponentEvent<?> event) {
		if (log.isLoggable(Level.INFO)) log.info("Got event: " + event);
		if (expect.size() == 0) {
			throw new RuntimeException("No event expected, but " + event.getClass() + " from " + event.getSource() + " came.");
		}
		expect.poll().check(event);
	}

	@Override
	public IToken getComponentId() {
		return token;
	}
	
	public Logger getLog() {
		return log;
	}
	
	@Override
	public String toString() {
		return getComponentId().getToken();
	}

	public void checkExpectEmpty() {
		Assert.assertTrue("should not expect any events", getExpectSize() == 0);
		if (log.isLoggable(Level.INFO)) log.info("Does not expecting any new events.");
	}
	
	public void manualStart() {
		bus.event(new StartingEvent(this));
		bus.event(new StartedEvent(this));
	}
	
	public void manualStartPaused() {
		bus.event(new StartingPausedEvent(this));
		bus.event(new PausedEvent(this));
	}
	
	public void manualPause() {
		bus.event(new PausingEvent(this));
		bus.event(new PausedEvent(this));
	}

	public void manualStop() {
		bus.event(new StoppingEvent(this));
		bus.event(new StoppedEvent(this));
	}

	public void manualResume() {
		bus.event(new ResumingEvent(this));
		bus.event(new ResumedEvent(this));
	}

	public void manualFatalError() {
		bus.event(new FatalErrorEvent<IComponent>(this, "SIMULATED FAILURE!"));
	}
	
	public void manualReset() {
		bus.reset();
	}

}
