package cz.cuni.amis.pogamut.base.component.controller;

import java.util.logging.Level;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.FatalErrorEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.PausedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.PausingEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.ResetEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.ResumedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.ResumingEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.StartedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.StartingEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.StoppedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.StoppingEvent;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.ILifecycleBus;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.LifecycleBus;
import cz.cuni.amis.pogamut.base.component.stub.component.ManualCheckComponent;
import cz.cuni.amis.pogamut.base.component.stub.sharedcomponent.AutoCheckSharedComponent;
import cz.cuni.amis.pogamut.base.component.stub.sharedcomponent.EventToString;
import cz.cuni.amis.pogamut.base.component.stub.sharedcomponent.MethodToString;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;

/**
 * Tests: start/pause/resume/stop + fatal errors / resets / restarts
 * 
 * @author Jimmy
 */
import cz.cuni.amis.tests.BaseTest;
				
public class Test23_SharedComponentController_SimulatingFailures extends BaseTest {
	
	private void checkState(AutoCheckSharedComponent sharedComp, ComponentState state) {
		if (sharedComp.getController().notInState(state)) {
			String msg = "[ERROR] " + sharedComp.getComponentId().getToken() + " is not in state " + state + " but in state " + sharedComp.getController().getState().getFlag() + ", INVALID!";
			System.out.println(msg);
			throw new RuntimeException(msg);
		}
	}
	
	@Test
	public void test() {
		IAgentId agentId1 = new AgentId("Test23_Shared-Agent1");
		IAgentId agentId2 = new AgentId("Test23_Shared-Agent2");
		IAgentLogger logger1 = new AgentLogger(agentId1);
		IAgentLogger logger2 = new AgentLogger(agentId2);
		logger1.addDefaultConsoleHandler();
		logger1.setLevel(Level.ALL);
		logger2.addDefaultConsoleHandler();
		logger2.setLevel(Level.ALL);
		ILifecycleBus bus1 = new LifecycleBus(logger1);
		ILifecycleBus bus2 = new LifecycleBus(logger2);
		
		// needs to be first, to receive all events as the first component
		AutoCheckSharedComponent sharedComp = new AutoCheckSharedComponent(logger1);		
		
		ManualCheckComponent manualComp1_0 = new ManualCheckComponent(logger1, bus1);
		ManualCheckComponent manualComp1_1 = new ManualCheckComponent(logger1, bus1);
		
		ManualCheckComponent manualComp2_2 = new ManualCheckComponent(logger2, bus2);
		ManualCheckComponent manualComp2_3 = new ManualCheckComponent(logger2, bus2);
		
		ComponentControlHelper emptyCtrl = new ComponentControlHelper();
		
		ComponentController manualCompCtrl1_0 = 
			new ComponentController(
					manualComp1_1, emptyCtrl, bus1, logger1.getCategory(manualComp1_1.getComponentId().getToken()),
					new ComponentDependencies(ComponentDependencyType.STARTS_WITH).add(manualComp1_0)
			);
		ComponentController manualCompCtrl2 = 
			new ComponentController(
					manualComp2_3, emptyCtrl, bus2, logger2.getCategory(manualComp2_3.getComponentId().getToken()),
					new ComponentDependencies(ComponentDependencyType.STARTS_AFTER).add(manualComp2_2)
			);
		
		sharedComp.addComponentBus(agentId1, bus1, new ComponentDependencies(ComponentDependencyType.STARTS_AFTER).add(manualComp1_1));
		sharedComp.addComponentBus(agentId2, bus2, new ComponentDependencies(ComponentDependencyType.STARTS_WITH).add(manualComp2_3));

		//
		// 1st batch
		//
		
		// manualComp1_0.manualStart();
		sharedComp.expectExactOrder(
			EventToString.eventToString (agentId1, manualComp1_0, StartingEvent.class),
			EventToString.eventToString (agentId1, manualComp1_1, StartingEvent.class),
			EventToString.eventToString (agentId1, manualComp1_1, StartedEvent.class),
			MethodToString.preStart     (sharedComp),
			MethodToString.start        (sharedComp),
			MethodToString.localPreStart(sharedComp, agentId1),
			EventToString.eventToString (agentId1, sharedComp,    StartingEvent.class),
			MethodToString.localStart   (sharedComp, agentId1),
			EventToString.eventToString (agentId1, sharedComp,    StartedEvent.class),
			EventToString.eventToString (agentId1, manualComp1_0, StartedEvent.class)
		);

		// manualComp1_0.manualFatalError();
		sharedComp.expectExactOrder(
			// fatal error on agentId1 will result in killing sharedComp on agentId1
			MethodToString.localKill    (sharedComp, agentId1),
			// this will lead to broadcasting FatalErrorEvent into all other buses (== agentId2)
			// which will use component controller on agentId2, that will automatically call fatalError
			MethodToString.localKill    (sharedComp, agentId2),
			// so we will just sense broadcast fatal error
			EventToString.eventToString (agentId2, sharedComp, FatalErrorEvent.class),
			// finally we will global kill sharedComp
			MethodToString.kill         (sharedComp),
			// and finish the processing of FatalErrorEvent that triggered that all
			EventToString.eventToString (agentId1, manualComp1_0, FatalErrorEvent.class)
		);
		
		// manualComp1_0.manualReset();
		sharedComp.expectExactOrder(
			// first a global reset will be invoked
			MethodToString.reset        (sharedComp),	
			// than local reset
			MethodToString.localReset   (sharedComp, agentId1),			
			// and finally the reset event will be sensed
			EventToString.eventToString (agentId1, bus1, ResetEvent.class)
		);
		
		// manualComp2_2.manualReset();
		sharedComp.expectExactOrder(
			// manual reset is resetting the component
			MethodToString.localReset   (sharedComp, agentId2),
			// we will sense only reset event as agentId2 was never started before
			EventToString.eventToString (agentId2, bus2, ResetEvent.class)
		);
		
		//
		// 2nd batch
		//
		
		// manualComp1_0.manualStart();
		sharedComp.expectExactOrder(
			EventToString.eventToString (agentId1, manualComp1_0, StartingEvent.class),
			EventToString.eventToString (agentId1, manualComp1_1, StartingEvent.class),
			EventToString.eventToString (agentId1, manualComp1_1, StartedEvent.class),
			MethodToString.preStart     (sharedComp),
			MethodToString.start        (sharedComp),
			MethodToString.localPreStart(sharedComp, agentId1),
			EventToString.eventToString (agentId1, sharedComp,    StartingEvent.class),
			MethodToString.localStart   (sharedComp, agentId1),
			EventToString.eventToString (agentId1, sharedComp,    StartedEvent.class),
			EventToString.eventToString (agentId1, manualComp1_0, StartedEvent.class)
		);
		
		// manualComp1_1.manualPause();
		sharedComp.expectExactOrder(
			// manual pausing of manualComp1_1 is being triggered
			EventToString.eventToString (agentId1, manualComp1_1, PausingEvent.class),
			// so it is triggering the pause of sharedComp on agentId1
			// so we're receiving sharedComp.localPrePause
			MethodToString.localPrePause(sharedComp, agentId1),
			// after prePause we're receiving the PausingEvent
			EventToString.eventToString (agentId1, sharedComp, PausingEvent.class),
			// after pausing event, we're having sharedComp.localPause(agentId1)
			MethodToString.localPause   (sharedComp, agentId1),
			// after localPause there is noone who is using the component, so we're globally pausing the component
			MethodToString.prePause     (sharedComp),
			MethodToString.pause        (sharedComp),
			// now PausedEvent is broadcast to agentId1 bus
			EventToString.eventToString (agentId1, sharedComp,    PausedEvent.class),
			// and finally the manualComp1_1 is broadcasting that it is paused
			EventToString.eventToString (agentId1, manualComp1_1, PausedEvent.class)
		);
		
		// manualComp1_0.manualFatalError();
		sharedComp.expectExactOrder(
			// fatal error on agentId1 will result in killing sharedComp on agentId1
			MethodToString.localKill    (sharedComp, agentId1),
			// this will lead to broadcastin FatalErrorEvent into all other buses (== agentId2)
			// so the localKill should happen for agentId2 as well, but it won't as component on agentId2
			// has never been started
			//MethodToString.localKill    (sharedComp, agentId2),
			// so we will just sense broadcast fatal error
			EventToString.eventToString (agentId2, sharedComp, FatalErrorEvent.class),
			// finally we will global kill sharedComp
			MethodToString.kill         (sharedComp),
			// and finish the processing of FatalErrorEvent that triggered that all
			EventToString.eventToString (agentId1, manualComp1_0, FatalErrorEvent.class)
		);
		
		// manualComp1_0.manualReset();
		sharedComp.expectExactOrder(
			// first a global reset will be invoked
			MethodToString.reset        (sharedComp),	
			// than local reset
			MethodToString.localReset   (sharedComp, agentId1),			
			// and finally the reset event will be sensed
			EventToString.eventToString (agentId1, bus1, ResetEvent.class)
		);
		
		// manualComp2_2.manualReset();
		sharedComp.expectExactOrder(
			// manual reset is resetting the component
			MethodToString.localReset   (sharedComp, agentId2),
			// we will sense only reset event as agentId2 was never started before
			EventToString.eventToString (agentId2, bus2, ResetEvent.class)
		);
		
		//
		// 3rd batch
		//
		
		// manualComp1_0.manualStart();
		sharedComp.expectExactOrder(
			EventToString.eventToString (agentId1, manualComp1_0, StartingEvent.class),
			EventToString.eventToString (agentId1, manualComp1_1, StartingEvent.class),
			EventToString.eventToString (agentId1, manualComp1_1, StartedEvent.class),
			MethodToString.preStart     (sharedComp),
			MethodToString.start        (sharedComp),
			MethodToString.localPreStart(sharedComp, agentId1),
			EventToString.eventToString (agentId1, sharedComp,    StartingEvent.class),
			MethodToString.localStart   (sharedComp, agentId1),
			EventToString.eventToString (agentId1, sharedComp,    StartedEvent.class),
			EventToString.eventToString (agentId1, manualComp1_0, StartedEvent.class)
		);
		
		// manualComp1_1.manualPause();
		sharedComp.expectExactOrder(
			// manual pausing of manualComp1_1 is being triggered
			EventToString.eventToString (agentId1, manualComp1_1, PausingEvent.class),
			// so it is triggering the pause of sharedComp on agentId1
			// so we're receiving sharedComp.localPrePause
			MethodToString.localPrePause(sharedComp, agentId1),
			// after prePause we're receiving the PausingEvent
			EventToString.eventToString (agentId1, sharedComp, PausingEvent.class),
			// after pausing event, we're having sharedComp.localPause(agentId1)
			MethodToString.localPause   (sharedComp, agentId1),
			// after localPause there is noone who is using the component, so we're globally pausing the component
			MethodToString.prePause     (sharedComp),
			MethodToString.pause        (sharedComp),
			// now PausedEvent is broadcast to agentId1 bus
			EventToString.eventToString (agentId1, sharedComp,    PausedEvent.class),
			// and finally the manualComp1_1 is broadcasting that it is paused
			EventToString.eventToString (agentId1, manualComp1_1, PausedEvent.class)
		);
		
		// manualComp1_1.manualResume();
		sharedComp.expectExactOrder(
			// manual pausing of manualComp1_1 is being resumed
			EventToString.eventToString (agentId1, manualComp1_1, ResumingEvent.class),
			// as we have dependency STARTS_AFTER, we will receive 
			EventToString.eventToString (agentId1, manualComp1_1, ResumedEvent.class),
			// so it is triggering the resume of sharedComp on agentId1
			// so we're receiving sharedComp.preResume ...
			MethodToString.preResume     (sharedComp),
			MethodToString.resume        (sharedComp),
			MethodToString.localPreResume(sharedComp, agentId1),
			EventToString.eventToString  (agentId1, sharedComp,   ResumingEvent.class),
			MethodToString.localResume   (sharedComp, agentId1),
			EventToString.eventToString  (agentId1, sharedComp,   ResumedEvent.class)			
		);
		
		// manualComp1_0.manualFatalError();
		sharedComp.expectExactOrder(
			// fatal error on agentId1 will result in killing sharedComp on agentId1
			MethodToString.localKill    (sharedComp, agentId1),
			// this will lead to broadcastin FatalErrorEvent into all other buses (== agentId2)
			// so the localKill should happen for agentId2 as well, but it won't as component on agentId2
			// has never been started
			//MethodToString.localKill    (sharedComp, agentId2),
			// so we will just sense broadcast fatal error
			EventToString.eventToString (agentId2, sharedComp, FatalErrorEvent.class),
			// finally we will global kill sharedComp
			MethodToString.kill         (sharedComp),
			// and finish the processing of FatalErrorEvent that triggered that all
			EventToString.eventToString (agentId1, manualComp1_0, FatalErrorEvent.class)
		);
		
		// manualComp1_0.manualReset();
		sharedComp.expectExactOrder(
			// first a global reset will be invoked
			MethodToString.reset        (sharedComp),	
			// than local reset
			MethodToString.localReset   (sharedComp, agentId1),			
			// and finally the reset event will be sensed
			EventToString.eventToString (agentId1, bus1, ResetEvent.class)
		);
		
		// manualComp2_2.manualReset();
		sharedComp.expectExactOrder(
			// manual reset is resetting the component
			MethodToString.localReset   (sharedComp, agentId2),
			// we will sense only reset event as agentId2 was never started before
			EventToString.eventToString (agentId2, bus2, ResetEvent.class)
		);
		
		//
		// 4th batch  
		//
		
		// manualComp1_0.manualStart();
		sharedComp.expectExactOrder(
			EventToString.eventToString (agentId1, manualComp1_0, StartingEvent.class),
			EventToString.eventToString (agentId1, manualComp1_1, StartingEvent.class),
			EventToString.eventToString (agentId1, manualComp1_1, StartedEvent.class),
			MethodToString.preStart     (sharedComp),
			MethodToString.start        (sharedComp),
			MethodToString.localPreStart(sharedComp, agentId1),
			EventToString.eventToString (agentId1, sharedComp,    StartingEvent.class),
			MethodToString.localStart   (sharedComp, agentId1),
			EventToString.eventToString (agentId1, sharedComp,    StartedEvent.class),
			EventToString.eventToString (agentId1, manualComp1_0, StartedEvent.class)
		);
		
		// manualComp1_1.manualPause();
		sharedComp.expectExactOrder(
			// manual pausing of manualComp1_1 is being triggered
			EventToString.eventToString (agentId1, manualComp1_1, PausingEvent.class),
			// so it is triggering the pause of sharedComp on agentId1
			// so we're receiving sharedComp.localPrePause
			MethodToString.localPrePause(sharedComp, agentId1),
			// after prePause we're receiving the PausingEvent
			EventToString.eventToString (agentId1, sharedComp, PausingEvent.class),
			// after pausing event, we're having sharedComp.localPause(agentId1)
			MethodToString.localPause   (sharedComp, agentId1),
			// after localPause there is noone who is using the component, so we're globally pausing the component
			MethodToString.prePause     (sharedComp),
			MethodToString.pause        (sharedComp),
			// now PausedEvent is broadcast to agentId1 bus
			EventToString.eventToString (agentId1, sharedComp,    PausedEvent.class),
			// and finally the manualComp1_1 is broadcasting that it is paused
			EventToString.eventToString (agentId1, manualComp1_1, PausedEvent.class)
		);
		
		// manualComp1_1.manualResume();
		sharedComp.expectExactOrder(
			// manual pausing of manualComp1_1 is being resumed
			EventToString.eventToString (agentId1, manualComp1_1, ResumingEvent.class),
			// as we have dependency STARTS_AFTER, we will receive 
			EventToString.eventToString (agentId1, manualComp1_1, ResumedEvent.class),
			// so it is triggering the resume of sharedComp on agentId1
			// so we're receiving sharedComp.preResume ...
			MethodToString.preResume     (sharedComp),
			MethodToString.resume        (sharedComp),
			MethodToString.localPreResume(sharedComp, agentId1),
			EventToString.eventToString  (agentId1, sharedComp,   ResumingEvent.class),
			MethodToString.localResume   (sharedComp, agentId1),
			EventToString.eventToString  (agentId1, sharedComp,   ResumedEvent.class)			
		);
		
		// manualComp1_0.manualStop();
		sharedComp.expectExactOrder(
			EventToString.eventToString (agentId1, manualComp1_0, StoppingEvent.class),
			EventToString.eventToString (agentId1, manualComp1_1, StoppingEvent.class),
			MethodToString.localPreStop (sharedComp, agentId1),
			EventToString.eventToString (agentId1, sharedComp,    StoppingEvent.class),
			MethodToString.localStop    (sharedComp, agentId1),
			
			// IN FACT ... EventToString.eventToString (agentId1, manualComp1_1, StoppedEvent.class)
			//             HAPPENS FIRST, BUT AS REACTION TO THIS EVENT, SHARED COMPONENT WILL BE STOPPED
			//             IMPORTANT IS ... THAT localStop() HAPPENS PRIOR TO global stop
			MethodToString.preStop      (sharedComp),
			MethodToString.stop         (sharedComp),
			EventToString.eventToString (agentId1, sharedComp,    StoppedEvent.class),
			// NOW THE SHARED COMPONENT IS STOPPED
			// SO THE PROCESSING OF STOPPED EVENT IS GOING TO CONTINUE
			// AND OUR LISTENER RECEIVES IT
			EventToString.eventToString (agentId1, manualComp1_1, StoppedEvent.class),
			
			EventToString.eventToString (agentId1, manualComp1_0, StoppedEvent.class)
		);
		
//		// manualComp2_2.manualStart();
//		sharedComp.expectExactOrder(
//			EventToString.eventToString (agentId2, manualComp2_2, StartingEvent.class),
//			EventToString.eventToString (agentId2, manualComp2_2, StartedEvent.class),
//			EventToString.eventToString (agentId2, manualComp2_3, StartingEvent.class),
//			MethodToString.preStart     (sharedComp),
//			MethodToString.start        (sharedComp),
//			MethodToString.localPreStart(sharedComp, agentId2),
//			EventToString.eventToString (agentId2, sharedComp,    StartingEvent.class),
//			MethodToString.localStart   (sharedComp, agentId2),
//			EventToString.eventToString (agentId2, sharedComp,    StartedEvent.class),
//			EventToString.eventToString (agentId2, manualComp2_3, StartedEvent.class)			
//		);
//		
//		// manualComp2_3.manualPause();
//		sharedComp.expectExactOrder(
//			// manual pausing of manualComp2_3 is being triggered
//			EventToString.eventToString (agentId2, manualComp2_3, PausingEvent.class),
//			// so it is triggering the pause of sharedComp on agentId2
//			// so we're receiving sharedComp.localPrePause
//			MethodToString.localPrePause(sharedComp, agentId2),
//			// after prePause we're receiving the PausingEvent
//			EventToString.eventToString (agentId2, sharedComp, PausingEvent.class),
//			// after pausing event, we're having sharedComp.localPause(agentId2)
//			MethodToString.localPause   (sharedComp, agentId2),
//			// after localPause there is noone who is using the component, so we're globally pausing the component
//			MethodToString.prePause     (sharedComp),
//			MethodToString.pause        (sharedComp),
//			// now PausedEvent is broadcast to agentId2 bus
//			EventToString.eventToString (agentId2, sharedComp,    PausedEvent.class),
//			// and finally the manualComp2_3 is broadcasting that it is paused
//			EventToString.eventToString (agentId2, manualComp2_3, PausedEvent.class)
//			
//		);
//		
//		// manualComp2_3.manualResume();
//		sharedComp.expectExactOrder(
//			// manual pausing of manualComp2_3 is being resumed
//			EventToString.eventToString (agentId2, manualComp2_3, ResumingEvent.class),
//			// as we have dependency STARTS_WITH ... we are being resumed immediately
//			// so it is triggering the resume of sharedComp on agentId2
//			// so we're receiving sharedComp.preResume ...
//			MethodToString.preResume     (sharedComp),
//			MethodToString.resume        (sharedComp),
//			MethodToString.localPreResume(sharedComp, agentId2),
//			EventToString.eventToString  (agentId2, sharedComp,   ResumingEvent.class),
//			MethodToString.localResume   (sharedComp, agentId2),
//			EventToString.eventToString  (agentId2, sharedComp,   ResumedEvent.class),
//			EventToString.eventToString (agentId2, manualComp2_3, ResumedEvent.class)
//		);
//		
//		// manualComp2_2.manualStop();
//		sharedComp.expectExactOrder(
//			EventToString.eventToString (agentId2, manualComp2_2, StoppingEvent.class),
//			EventToString.eventToString (agentId2, manualComp2_3, StoppingEvent.class),
//			MethodToString.localPreStop (sharedComp, agentId2),
//			EventToString.eventToString (agentId2, sharedComp,    StoppingEvent.class),
//			MethodToString.localStop    (sharedComp, agentId2),
//			
//			// IN FACT ... EventToString.eventToString (agentId2, manualComp2_3, StoppedEvent.class)
//			//             HAPPENS FIRST, BUT AS REACTION TO THIS EVENT, SHARED COMPONENT WILL BE STOPPED
//			//             IMPORTANT IS ... THAT localStop() HAPPENS PRIOR TO global stop
//			MethodToString.preStop      (sharedComp),
//			MethodToString.stop         (sharedComp),
//			EventToString.eventToString (agentId2, sharedComp,    StoppedEvent.class),
//			// NOW THE SHARED COMPONENT IS STOPPED
//			// SO THE PROCESSING OF STOPPED EVENT IS GOING TO CONTINUE
//			// AND OUR LISTENER RECEIVES IT
//			EventToString.eventToString (agentId2, manualComp2_3, StoppedEvent.class),
//			
//			EventToString.eventToString (agentId2, manualComp2_2, StoppedEvent.class)
//		);
		
		//
		// STARTING/STOPPING DEPENDENCIES + SIMULATE FAILURES
		//
		
		// 1st batch
		
		manualComp1_0.manualStart();
		manualComp1_0.manualFatalError();
		manualComp1_0.manualReset();
		manualComp2_2.manualReset();
		
		// 2nd batch
		
		manualComp1_0.manualStart();
		manualComp1_1.manualPause();
		manualComp1_0.manualFatalError();
		manualComp1_0.manualReset();
		manualComp2_2.manualReset();		
		
		// 3rd batch
		
		manualComp1_0.manualStart();
		manualComp1_1.manualPause();
		manualComp1_1.manualResume();
		manualComp1_0.manualFatalError();
		manualComp1_0.manualReset();
		manualComp2_2.manualReset();
		
		// 4th batch
		
		manualComp1_0.manualStart();
		manualComp1_1.manualPause();
		manualComp1_1.manualResume();
		manualComp1_0.manualStop();
		
		
		
		
		
		
		sharedComp.checkNoMoreActivityExpected();
		
		System.out.println("---/// TEST OK ///---");
	}

}
