package cz.cuni.amis.pogamut.base.component.stub.component;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;
import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.IComponentEvent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentEventListener;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Token;
import cz.cuni.amis.utils.token.Tokens;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AutoCheckComponent implements IComponent {

	private static int counter = 0;
	
	private Token token;

	private List<CheckEvent> expect = new LinkedList<CheckEvent>();

	private IComponentBus bus;
	
	private LogCategory log;
	
	private boolean exactOrder = true;

	private IComponentEventListener<IComponentEvent> listener = new IComponentEventListener<IComponentEvent>() {

		@Override
		public void notify(IComponentEvent event) {
			if (log.isLoggable(Level.INFO)) log.info("Got event: " + event);
			if (expect.size() == 0) {
				throw new RuntimeException("No event expected, but " + event.getClass() + " from " + event.getSource() + " came.");
			}
			if (exactOrder) {
				expect.get(0).check(event);
				expect.remove(0);
			} else {
				for (int i = 0; i < expect.size(); ++i) {
					if (expect.get(i).checkNoException(event)) {
						expect.remove(i);
						return;
					}
				}
				Assert.fail("Did not expect event of class " + event.getClass() + " from " + event.getSource() + ".");
			}
		}
		
	};

	public AutoCheckComponent(IAgentLogger logger, IComponentBus bus) {
		this(logger, bus, new CheckEvent[0]);
	}
	
	public AutoCheckComponent(IAgentLogger logger, IComponentBus bus, CheckEvent... events) {
		this.token = Tokens.get("AutoCheckComponent" + counter++);
		this.log = logger.getCategory(this);
		NullCheck.check(this.log, "log initialization");
		expect(events);
		this.bus = bus;
		this.bus.addEventListener(IComponentEvent.class, listener);
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

	@Override
	public IToken getComponentId() {
		return token;
	}
	
	public Logger getLog() {
		return log;
	}
	
	public boolean isExactOrder() {
		return exactOrder;
	}

	public void setExactOrder(boolean exactOrder) {
		this.exactOrder = exactOrder;
	}

	@Override
	public String toString() {
		return getComponentId().getToken();
	}

	public void checkExpectEmpty() {
		Assert.assertTrue("should not expect any events", getExpectSize() == 0);
		if (log.isLoggable(Level.INFO)) log.info("Does not expecting any more events.");
	}

}
