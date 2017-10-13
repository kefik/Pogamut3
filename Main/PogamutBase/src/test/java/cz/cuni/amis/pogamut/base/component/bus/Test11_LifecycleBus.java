package cz.cuni.amis.pogamut.base.component.bus;

import java.util.logging.Level;

import junit.framework.Assert;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.component.bus.event.IFatalErrorEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.FatalErrorEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.StartedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.StartingEvent;
import cz.cuni.amis.pogamut.base.component.bus.exception.FatalErrorPropagatingEventException;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.ILifecycleBus;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.LifecycleBus;
import cz.cuni.amis.pogamut.base.component.stub.component.AutoCheckComponent;
import cz.cuni.amis.pogamut.base.component.stub.component.CheckEvent;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;

import cz.cuni.amis.tests.BaseTest;
				
public class Test11_LifecycleBus extends BaseTest {

	@Test
	public void test01() {
		IAgentId agentId = new AgentId("Test05_LifecycleBus");
		IAgentLogger logger = new AgentLogger(agentId);
		logger.addDefaultConsoleHandler();
		logger.setLevel(Level.ALL);
		final ILifecycleBus bus = new LifecycleBus(logger);
		
		final AutoCheckComponent comp1 = new AutoCheckComponent(logger, bus);
		
		IComponentEventListener<IComponentEvent> listener = new IComponentEventListener<IComponentEvent>() {
			@Override
			public void notify(IComponentEvent event) {
				if (event instanceof IFatalErrorEvent) return;
				bus.event(new StartedEvent(comp1));
				bus.event(new FatalErrorEvent(comp1, "You are doomed!"));
			}
		};
		bus.addEventListener(IComponentEvent.class, listener);
		
		CheckEvent[] checkEvents = new CheckEvent[]{
			new CheckEvent(new StartingEvent(comp1)),
			new CheckEvent(FatalErrorEvent.class, comp1)
		};
		
		comp1.expect(checkEvents);
		
		boolean exception = false;
		try {
			bus.event(new StartingEvent(comp1));
		} catch (FatalErrorPropagatingEventException e) {
			exception = true;
		}
		Assert.assertTrue("bus should throw an exception when fatal error happens during the propagation of the event", exception);
		Assert.assertTrue("bus should not be running after fatal error", !bus.isRunning());
		
		comp1.checkExpectEmpty();
		
		System.out.println("---/// TEST OK ///---");		
	}
	
	public static void main(String[] args) {
		Test11_LifecycleBus test = new Test11_LifecycleBus();
		test.test01();
	}
	
}
