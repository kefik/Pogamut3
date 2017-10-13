package cz.cuni.amis.pogamut.base.component.controller;

import java.util.logging.Level;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.component.bus.ComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.IComponentEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.PausedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.PausingEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.ResumedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.ResumingEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.StartedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.StartingEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.StartingPausedEvent;
import cz.cuni.amis.pogamut.base.component.stub.component.AutoCheckComponent;
import cz.cuni.amis.pogamut.base.component.stub.component.CheckEvent;
import cz.cuni.amis.pogamut.base.component.stub.component.ManualCheckComponent;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;

/**
 * Tests: start_paused/resume
 * @author Jimmy
 *
 */
import cz.cuni.amis.tests.BaseTest;
				
public class Test05_ComponentController_StartPaused extends BaseTest {
	
	@Test
	public void test() {
		IAgentId agentId = new AgentId("Test05_ComponentController_StartPaused");
		IAgentLogger logger = new AgentLogger(agentId);
		logger.addDefaultConsoleHandler();
		logger.setLevel(Level.ALL);
		IComponentBus bus = new ComponentBus(logger);
		
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
		ComponentController manualCompCtrl2 = 
			new ComponentController(
					manualComp2, emptyCtrl, bus, logger.getCategory(manualComp2.getComponentId().getToken()),
					new ComponentDependencies(ComponentDependencyType.STARTS_AFTER).add(manualComp1)
			);
		ComponentController manualCompCtrl3 = 
			new ComponentController(
					manualComp3, emptyCtrl, bus, logger.getCategory(manualComp3.getComponentId().getToken()),
					new ComponentDependencies(ComponentDependencyType.STARTS_WITH).add(manualComp2)
			);
		
		IComponentEvent[] events = new IComponentEvent[] {
				new StartingPausedEvent(manualComp0),
				new StartingPausedEvent(manualComp1),
				new PausedEvent(manualComp1),
				new StartingPausedEvent(manualComp2),
				new StartingPausedEvent(manualComp3),
				new PausedEvent(manualComp3),
				new PausedEvent(manualComp2),
				new PausedEvent(manualComp0),
				
				//////////////////////////
				
				new ResumingEvent(manualComp0),
				new ResumingEvent(manualComp1),
				new ResumedEvent(manualComp1),
				new ResumingEvent(manualComp2),
				new ResumingEvent(manualComp3),
				new ResumedEvent(manualComp3),
				new ResumedEvent(manualComp2),
				new ResumedEvent(manualComp0),
		};
		
		CheckEvent[] checkEvents = new CheckEvent[events.length];
		
		for (int i = 0; i < events.length; ++i) {
			checkEvents[i] = new CheckEvent(events[i]);
		}
		
		autoComp0.expect(checkEvents);
				
		manualComp0.manualStartPaused();
		
		manualComp0.manualResume();
			
		autoComp0.checkExpectEmpty();
		
		System.out.println("---/// TEST OK ///---");
	}

}
