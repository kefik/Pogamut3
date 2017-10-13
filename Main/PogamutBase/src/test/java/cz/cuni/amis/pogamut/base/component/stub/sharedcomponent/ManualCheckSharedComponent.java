package cz.cuni.amis.pogamut.base.component.stub.sharedcomponent;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;
import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.IComponentEvent;
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

public class ManualCheckSharedComponent implements IComponent {

	private static int counter = 0;
	
	private Token token;

	private Queue<CheckSharedEvent> expect = new LinkedList<CheckSharedEvent>();

	private LogCategory log;

	public ManualCheckSharedComponent(IAgentLogger logger) {
		this(logger, new CheckSharedEvent[0]);
	}
	
	public ManualCheckSharedComponent(IAgentLogger logger, CheckSharedEvent... events) {
		this.token = Tokens.get("ManualCheckSharedComponent" + counter++);
		this.log = logger.getCategory(this);
		NullCheck.check(this.log, "log initialization");
		expect(events);
	}
	
	public void expect(CheckSharedEvent... events) {
		for (CheckSharedEvent event : events) { 
			expect(event);
		}
	}

	public void expect(CheckSharedEvent event) {
		expect.add(event);
	}
	
	public int getExpectSize() {
		return expect.size();
	}
	
	public void manualNotify(IAgentId agentId, IComponentEvent<?> event) {
		if (log.isLoggable(Level.INFO)) log.info("Got event: " + event);
		if (expect.size() == 0) {
			throw new RuntimeException("No event expected, but " + event.getClass() + " from " + event.getSource() + " of agetn " + agentId + " came.");
		}
		expect.poll().check(agentId, event);
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

}
