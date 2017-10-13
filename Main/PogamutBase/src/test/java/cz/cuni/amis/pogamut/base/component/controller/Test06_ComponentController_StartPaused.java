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
import cz.cuni.amis.pogamut.base.component.bus.event.impl.StoppedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.StoppingEvent;
import cz.cuni.amis.pogamut.base.component.stub.component.AutoCheckComponent;
import cz.cuni.amis.pogamut.base.component.stub.component.CheckEvent;
import cz.cuni.amis.pogamut.base.component.stub.component.ManualCheckComponent;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;

/**
 * Tests starting_paused/resuming/stopping/starting_paused...
 * @author Jimmy
 */
import cz.cuni.amis.tests.BaseTest;
				
public class Test06_ComponentController_StartPaused extends BaseTest {
	
	@Test
	public void test() {
		IAgentId agentId = new AgentId("Test06_ComponentController_StartPaused");
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
		
		IComponentEvent[] events = new IComponentEvent[] {
				new StartingPausedEvent(manualComp0),
				new StartingPausedEvent(manualComp1),
				new PausedEvent(manualComp1),
				new StartingPausedEvent(manualComp2),
				new PausedEvent(manualComp2),
				new PausedEvent(manualComp0),	
				
				new ResumingEvent(manualComp0),
				new ResumingEvent(manualComp1),
				new ResumedEvent(manualComp1),
				new ResumingEvent(manualComp2),
				new ResumedEvent(manualComp2),
				new ResumedEvent(manualComp0),	
				
				new StoppingEvent(manualComp0),
				new StoppingEvent(manualComp1),
				new StoppingEvent(manualComp2),
				new StoppedEvent(manualComp2),
				new StoppedEvent(manualComp1),
				new StoppedEvent(manualComp0),
				
				//////////////////////////////
				
				new StartingPausedEvent(manualComp0),
				new StartingPausedEvent(manualComp1),
				new PausedEvent(manualComp1),
				new StartingPausedEvent(manualComp2),
				new PausedEvent(manualComp2),
				new PausedEvent(manualComp0),	
				
				new ResumingEvent(manualComp0),
				new ResumingEvent(manualComp1),
				new ResumedEvent(manualComp1),
				new ResumingEvent(manualComp2),
				new ResumedEvent(manualComp2),
				new ResumedEvent(manualComp0),	
				
				new StoppingEvent(manualComp0),
				new StoppingEvent(manualComp1),
				new StoppingEvent(manualComp2),
				new StoppedEvent(manualComp2),
				new StoppedEvent(manualComp1),
				new StoppedEvent(manualComp0),
				
				//////////////////////////////
				
				new StartingPausedEvent(manualComp0),
				new StartingPausedEvent(manualComp1),
				new PausedEvent(manualComp1),
				new StartingPausedEvent(manualComp2),
				new PausedEvent(manualComp2),
				new PausedEvent(manualComp0),	
				
				new ResumingEvent(manualComp0),
				new ResumingEvent(manualComp1),
				new ResumedEvent(manualComp1),
				new ResumingEvent(manualComp2),
				new ResumedEvent(manualComp2),
				new ResumedEvent(manualComp0),	
				
				new StoppingEvent(manualComp0),
				new StoppingEvent(manualComp1),
				new StoppingEvent(manualComp2),
				new StoppedEvent(manualComp2),
				new StoppedEvent(manualComp1),
				new StoppedEvent(manualComp0),
				
				//////////////////////////////				
		};
		
		CheckEvent[] checkEvents = new CheckEvent[events.length];
		
		for (int i = 0; i < events.length; ++i) {
			checkEvents[i] = new CheckEvent(events[i]);
		}
		
		autoComp0.expect(checkEvents);
		
		///////////////////////////////////////////
		
		manualComp0.manualStartPaused();
		
		manualComp0.manualResume();
		
		manualComp0.manualStop();
		
		///////////////////////////////////////////
		
		manualComp0.manualStartPaused();
		
		manualComp0.manualResume();
		
		manualComp0.manualStop();
		
		///////////////////////////////////////////
		
		manualComp0.manualStartPaused();
		
		manualComp0.manualResume();
		
		manualComp0.manualStop();
		
		///////////////////////////////////////////
		
		autoComp0.checkExpectEmpty();
		
		System.out.println("---/// TEST OK ///---");
	}

}
