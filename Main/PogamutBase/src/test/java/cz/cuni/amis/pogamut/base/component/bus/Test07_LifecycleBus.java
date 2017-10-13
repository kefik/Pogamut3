package cz.cuni.amis.pogamut.base.component.bus;

import java.util.logging.Level;

import junit.framework.Assert;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.FatalErrorEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.ResetEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.StartedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.StoppedEvent;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentBusNotRunningException;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.ILifecycleBus;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.LifecycleBus;
import cz.cuni.amis.pogamut.base.component.stub.component.AutoCheckComponent;
import cz.cuni.amis.pogamut.base.component.stub.component.CheckEvent;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;

import cz.cuni.amis.tests.BaseTest;
				
public class Test07_LifecycleBus extends BaseTest {

	@Test
	public void test01() {
		IAgentId agentId = new AgentId("Test07_LifecycleBus");
		IAgentLogger logger = new AgentLogger(agentId);
		logger.addDefaultConsoleHandler();
		logger.setLevel(Level.ALL);
		ILifecycleBus bus = new LifecycleBus(logger);
		
		AutoCheckComponent comp1 = new AutoCheckComponent(logger, bus);
		AutoCheckComponent comp2 = new AutoCheckComponent(logger, bus);
		
		IComponentEvent[] events = new IComponentEvent[] {
			new FatalErrorEvent(comp1, "Bad bad!"),
			new ResetEvent(bus),
			new FatalErrorEvent(comp2, "Bad bad!"),
			new ResetEvent(bus),
			new StartedEvent(comp1),
			new StartedEvent(comp2)
			
		};
		
		CheckEvent[] checkEvents = new CheckEvent[events.length];
		
		for (int i = 0; i < events.length; ++i) {
			checkEvents[i] = new CheckEvent(events[i]);
		}
		
		comp1.expect(checkEvents);
		comp2.expect(checkEvents);
		
		
		bus.event(events[0]);
		Assert.assertTrue("bus should not be running after fatal error", !bus.isRunning());
		bus.reset();
		
		bus.event(events[2]);
		Assert.assertTrue("bus should not be running after fatal error", !bus.isRunning());
		boolean exception = false;
		try {
			bus.event(new StoppedEvent(comp1));
		} catch (ComponentBusNotRunningException e) {
			exception = true;
		}
		Assert.assertTrue("bus should throw an exception when the event is sent to stopped bus", exception);
		bus.reset();
		
		bus.event(events[4]);
		bus.event(events[5]);
		
		comp1.checkExpectEmpty();
		comp2.checkExpectEmpty();
		
		System.out.println("---/// TEST OK ///---");		
	}
	
	public static void main(String[] args) {
		Test07_LifecycleBus test = new Test07_LifecycleBus();
		test.test01();
	}
	
}
