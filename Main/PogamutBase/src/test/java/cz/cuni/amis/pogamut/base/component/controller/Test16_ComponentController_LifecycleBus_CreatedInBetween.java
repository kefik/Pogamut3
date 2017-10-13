package cz.cuni.amis.pogamut.base.component.controller;

import java.util.logging.Level;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.component.bus.IComponentEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.PausedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.PausingEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.ResumedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.ResumingEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.StartedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.StartingEvent;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.ILifecycleBus;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.LifecycleBus;
import cz.cuni.amis.pogamut.base.component.stub.component.AutoCheckComponent;
import cz.cuni.amis.pogamut.base.component.stub.component.CheckEvent;
import cz.cuni.amis.pogamut.base.component.stub.component.ManualCheckComponent;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;

/**
 * Tests: start/pause/resume with LifecycleBus
 * @author Jimmy
 */
import cz.cuni.amis.tests.BaseTest;
				
public class Test16_ComponentController_LifecycleBus_CreatedInBetween extends BaseTest {
	
	@Test
	public void test() {
		IAgentId agentId = new AgentId("Test16_ComponentController_LifecycleBus_CreatedInBetween");
		IAgentLogger logger = new AgentLogger(agentId);
		logger.addDefaultConsoleHandler();
		logger.setLevel(Level.ALL);
		ILifecycleBus bus = new LifecycleBus(logger);
		
		// needs to be first, to receive all events as the first component
		AutoCheckComponent autoComp0 = new AutoCheckComponent(logger, bus);
		
		ComponentControlHelper emptyCtrl = new ComponentControlHelper() {
		};
		
		ManualCheckComponent manualComp0 = new ManualCheckComponent(logger, bus);
		ManualCheckComponent manualComp1 = new ManualCheckComponent(logger, bus);
		ManualCheckComponent manualComp2 = new ManualCheckComponent(logger, bus);
		ManualCheckComponent manualComp3 = new ManualCheckComponent(logger, bus);
		
		ComponentController manualCompCtrl1 = 
			new ComponentController(
					manualComp1, emptyCtrl, bus, logger.getCategory(manualComp1.getComponentId().getToken()),
					new ComponentDependencies(ComponentDependencyType.STARTS_WITH).add(manualComp0)
			);
		
		IComponentEvent[] events = new IComponentEvent[] {
				new StartingEvent(manualComp0),
				new StartingEvent(manualComp1),
				new StartedEvent(manualComp1),
				new StartedEvent(manualComp0),
				new StartingEvent(manualComp2),
				new StartingEvent(manualComp3),
				new StartedEvent(manualComp3),
				new StartedEvent(manualComp2),
				new PausingEvent(manualComp3),
				new PausedEvent(manualComp3),
				new ResumingEvent(manualComp3),
				new ResumedEvent(manualComp3),
				new PausingEvent(manualComp0),
				new PausingEvent(manualComp1),
				new PausingEvent(manualComp2),
				new PausingEvent(manualComp3),
				new PausedEvent(manualComp3),
				new PausedEvent(manualComp2),
				new PausedEvent(manualComp1),
				new PausedEvent(manualComp0)
		};
		
		CheckEvent[] checkEvents = new CheckEvent[events.length];
		
		for (int i = 0; i < events.length; ++i) {
			checkEvents[i] = new CheckEvent(events[i]);
		}
		
		autoComp0.expect(checkEvents);
				
		manualComp0.manualStart();
		
		ComponentController manualCompCtrl3 = 
			new ComponentController(
					manualComp3, emptyCtrl, bus, logger.getCategory(manualComp3.getComponentId().getToken()),
					new ComponentDependencies(ComponentDependencyType.STARTS_WITH).add(manualComp2)
			);		
		ComponentController manualCompCtrl2 = 
			new ComponentController(
					manualComp2, emptyCtrl, bus, logger.getCategory(manualComp2.getComponentId().getToken()),
					new ComponentDependencies(ComponentDependencyType.STARTS_AFTER).add(manualComp1)
			);
		
		manualCompCtrl3.manualPause("testing");
		manualCompCtrl3.manualResume("testing");
		
		manualComp0.manualPause();
		
		autoComp0.checkExpectEmpty();
		
		System.out.println("---/// TEST OK ///---");
	}

}
