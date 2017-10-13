package cz.cuni.amis.pogamut.base.component.controller;

import java.util.logging.Level;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.PausedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.PausingEvent;
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
 * Tests: start/pause/resume/stop agent1/2 mixed.
 * @author Jimmy
 *
 */
import cz.cuni.amis.tests.BaseTest;
				
public class Test22_SharedComponentController_MixedStartPauseResumeStop extends BaseTest {
	
	private void checkState(AutoCheckSharedComponent sharedComp, ComponentState state) {
		if (sharedComp.getController().notInState(state)) {
			String msg = "[ERROR] " + sharedComp.getComponentId().getToken() + " is not in state " + state + " but in state " + sharedComp.getController().getState().getFlag() + ", INVALID!";
			System.out.println(msg);
			throw new RuntimeException(msg);
		}
	}
	
	@Test
	public void test() {
		IAgentId agentId1 = new AgentId("Test22_Shared-Agent1");
		IAgentId agentId2 = new AgentId("Test22_Shared-Agent2");
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
		
		// manualComp2_2.manualStart();
		sharedComp.expectExactOrder(
			EventToString.eventToString (agentId2, manualComp2_2, StartingEvent.class),
			EventToString.eventToString (agentId2, manualComp2_2, StartedEvent.class),
			EventToString.eventToString (agentId2, manualComp2_3, StartingEvent.class),
			MethodToString.localPreStart(sharedComp, agentId2),
			EventToString.eventToString (agentId2, sharedComp,    StartingEvent.class),
			MethodToString.localStart   (sharedComp, agentId2),
			EventToString.eventToString (agentId2, sharedComp,    StartedEvent.class),
			EventToString.eventToString (agentId2, manualComp2_3, StartedEvent.class)			
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
			// after localPause there is still manualComp2_2 running, so we're not globally pausing!
			//MethodToString.prePause     (sharedComp),
			//MethodToString.pause        (sharedComp),
			// now PausedEvent is broadcast to agentId1 bus
			EventToString.eventToString (agentId1, sharedComp,    PausedEvent.class),
			// and finally the manualComp1_1 is broadcasting that it is paused
			EventToString.eventToString (agentId1, manualComp1_1, PausedEvent.class)
		);
		
		// manualComp1_1.manualResume();
		sharedComp.expectExactOrder(
			// manual paused of manualComp1_1 is being resumed
			EventToString.eventToString (agentId1, manualComp1_1, ResumingEvent.class),
			// as we have dependency STARTS_AFTER, we will receive 
			EventToString.eventToString (agentId1, manualComp1_1, ResumedEvent.class),
			// so it is triggering the resume of sharedComp on agentId1
			// as we're still running, we won't receive global preResume/resume()
			//MethodToString.preResume     (sharedComp),
			//MethodToString.resume        (sharedComp),
			MethodToString.localPreResume(sharedComp, agentId1),
			EventToString.eventToString  (agentId1, sharedComp,   ResumingEvent.class),
			MethodToString.localResume   (sharedComp, agentId1),
			EventToString.eventToString  (agentId1, sharedComp,   ResumedEvent.class)			
		);
		
		// manualComp2_3.manualPause();
		sharedComp.expectExactOrder(
			// manual pausing of manualComp2_3 is being triggered
			EventToString.eventToString (agentId2, manualComp2_3, PausingEvent.class),
			// so it is triggering the pause of sharedComp on agentId2
			// so we're receiving sharedComp.localPrePause
			MethodToString.localPrePause(sharedComp, agentId2),
			// after prePause we're receiving the PausingEvent
			EventToString.eventToString (agentId2, sharedComp, PausingEvent.class),
			// after pausing event, we're having sharedComp.localPause(agentId2)
			MethodToString.localPause   (sharedComp, agentId2),
			// after localPause there is still manualComp1_1 who is using the component, so we're not pausing globally
			//MethodToString.prePause     (sharedComp),
			//MethodToString.pause        (sharedComp),
			// now PausedEvent is broadcast to agentId2 bus
			EventToString.eventToString (agentId2, sharedComp,    PausedEvent.class),
			// and finally the manualComp2_3 is broadcasting that it is paused
			EventToString.eventToString (agentId2, manualComp2_3, PausedEvent.class)
			
		);
		
		// manualComp2_3.manualResume();
		sharedComp.expectExactOrder(
			// manual pausing of manualComp2_3 is being resumed
			EventToString.eventToString (agentId2, manualComp2_3, ResumingEvent.class),
			// as we have dependency STARTS_WITH ... we are being resumed immediately
			// so it is triggering the resume of sharedComp on agentId2
			// as we're still running, we won't receive global preResume/resume(), only locals 
			//MethodToString.preResume     (sharedComp),
			//MethodToString.resume        (sharedComp),
			MethodToString.localPreResume(sharedComp, agentId2),
			EventToString.eventToString  (agentId2, sharedComp,   ResumingEvent.class),
			MethodToString.localResume   (sharedComp, agentId2),
			EventToString.eventToString  (agentId2, sharedComp,   ResumedEvent.class),
			EventToString.eventToString  (agentId2, manualComp2_3, ResumedEvent.class)
		);
		
		// NOW PAUSING BOTH COMPONENTS
		
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
			// after localPause there is still manualComp2_2 running, so we're not globally pausing!
			//MethodToString.prePause     (sharedComp),
			//MethodToString.pause        (sharedComp),
			// now PausedEvent is broadcast to agentId1 bus
			EventToString.eventToString (agentId1, sharedComp,    PausedEvent.class),
			// and finally the manualComp1_1 is broadcasting that it is paused
			EventToString.eventToString (agentId1, manualComp1_1, PausedEvent.class)
		);
		
		// manualComp2_3.manualPause();
		sharedComp.expectExactOrder(
			// manual pausing of manualComp2_3 is being triggered
			EventToString.eventToString (agentId2, manualComp2_3, PausingEvent.class),
			// so it is triggering the pause of sharedComp on agentId2
			// so we're receiving sharedComp.localPrePause
			MethodToString.localPrePause(sharedComp, agentId2),
			// after prePause we're receiving the PausingEvent
			EventToString.eventToString (agentId2, sharedComp, PausingEvent.class),
			// after pausing event, we're having sharedComp.localPause(agentId2)
			MethodToString.localPause   (sharedComp, agentId2),
			// after localPause there is noone who is using the component, so we're globally pausing the component
			MethodToString.prePause     (sharedComp),
			MethodToString.pause        (sharedComp),
			// now PausedEvent is broadcast to agentId2 bus
			EventToString.eventToString (agentId2, sharedComp,    PausedEvent.class),
			// and finally the manualComp2_3 is broadcasting that it is paused
			EventToString.eventToString (agentId2, manualComp2_3, PausedEvent.class)
		);
		
		// NOW THE COMPONENT IS PAUSED -> resuming 
		
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
		
		// manualComp2_3.manualResume();
		sharedComp.expectExactOrder(
			// manual pausing of manualComp2_3 is being resumed
			EventToString.eventToString (agentId2, manualComp2_3, ResumingEvent.class),
			// as we have dependency STARTS_WITH ... we are being resumed immediately
			// so it is triggering the resume of sharedComp on agentId2
			// as we're still running, we won't receive global preResume/resume(), only locals 
			//MethodToString.preResume     (sharedComp),
			//MethodToString.resume        (sharedComp),
			MethodToString.localPreResume(sharedComp, agentId2),
			EventToString.eventToString  (agentId2, sharedComp,   ResumingEvent.class),
			MethodToString.localResume   (sharedComp, agentId2),
			EventToString.eventToString  (agentId2, sharedComp,   ResumedEvent.class),
			EventToString.eventToString  (agentId2, manualComp2_3, ResumedEvent.class)
		);
		
		// PAUSING BOTH COMPONENTS IN SWITCHED ORDER
		
		// manualComp2_3.manualPause();
		sharedComp.expectExactOrder(
			// manual pausing of manualComp2_3 is being triggered
			EventToString.eventToString (agentId2, manualComp2_3, PausingEvent.class),
			// so it is triggering the pause of sharedComp on agentId2
			// so we're receiving sharedComp.localPrePause
			MethodToString.localPrePause(sharedComp, agentId2),
			// after prePause we're receiving the PausingEvent
			EventToString.eventToString (agentId2, sharedComp, PausingEvent.class),
			// after pausing event, we're having sharedComp.localPause(agentId2)
			MethodToString.localPause   (sharedComp, agentId2),
			// after localPause there is still manualComp1_1 who is using the component, so we're not pausing globally
			//MethodToString.prePause     (sharedComp),
			//MethodToString.pause        (sharedComp),
			// now PausedEvent is broadcast to agentId2 bus
			EventToString.eventToString (agentId2, sharedComp,    PausedEvent.class),
			// and finally the manualComp2_3 is broadcasting that it is paused
			EventToString.eventToString (agentId2, manualComp2_3, PausedEvent.class)
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
		
		// manualComp2_3.manualResume();
		sharedComp.expectExactOrder(
			// manual pausing of manualComp2_3 is being resumed
			EventToString.eventToString (agentId2, manualComp2_3, ResumingEvent.class),
			// as we have dependency STARTS_WITH ... we are being resumed immediately
			// so it is triggering the resume of sharedComp on agentId2
			// so we're receiving sharedComp.preResume ...
			MethodToString.preResume     (sharedComp),
			MethodToString.resume        (sharedComp),
			MethodToString.localPreResume(sharedComp, agentId2),
			EventToString.eventToString  (agentId2, sharedComp,   ResumingEvent.class),
			MethodToString.localResume   (sharedComp, agentId2),
			EventToString.eventToString  (agentId2, sharedComp,   ResumedEvent.class),
			EventToString.eventToString (agentId2, manualComp2_3, ResumedEvent.class)
		);
		
		// manualComp1_1.manualResume();
		sharedComp.expectExactOrder(
			// manual paused of manualComp1_1 is being resumed
			EventToString.eventToString (agentId1, manualComp1_1, ResumingEvent.class),
			// as we have dependency STARTS_AFTER, we will receive 
			EventToString.eventToString (agentId1, manualComp1_1, ResumedEvent.class),
			// so it is triggering the resume of sharedComp on agentId1
			// as we're still running, we won't receive global preResume/resume()
			//MethodToString.preResume     (sharedComp),
			//MethodToString.resume        (sharedComp),
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
			// no global stopping, there are still agent agentId2 using the component
			//MethodToString.preStop      (sharedComp),
			//MethodToString.stop         (sharedComp),
			EventToString.eventToString (agentId1, sharedComp,    StoppedEvent.class),
			EventToString.eventToString (agentId1, manualComp1_1, StoppedEvent.class),			
			EventToString.eventToString (agentId1, manualComp1_0, StoppedEvent.class)
		);
		
		// manualComp1_0.manualStart();
		sharedComp.expectExactOrder(
			EventToString.eventToString (agentId1, manualComp1_0, StartingEvent.class),
			EventToString.eventToString (agentId1, manualComp1_1, StartingEvent.class),
			EventToString.eventToString (agentId1, manualComp1_1, StartedEvent.class),
			// we're still running, no global preStart/start()
			//MethodToString.preStart     (sharedComp),
			//MethodToString.start        (sharedComp),
			MethodToString.localPreStart(sharedComp, agentId1),
			EventToString.eventToString (agentId1, sharedComp,    StartingEvent.class),
			MethodToString.localStart   (sharedComp, agentId1),
			EventToString.eventToString (agentId1, sharedComp,    StartedEvent.class),
			EventToString.eventToString (agentId1, manualComp1_0, StartedEvent.class)
		);
		
		// manualComp2_2.manualStop();
		sharedComp.expectExactOrder(
			EventToString.eventToString (agentId2, manualComp2_2, StoppingEvent.class),
			EventToString.eventToString (agentId2, manualComp2_3, StoppingEvent.class),
			MethodToString.localPreStop (sharedComp, agentId2),
			EventToString.eventToString (agentId2, sharedComp,    StoppingEvent.class),
			MethodToString.localStop    (sharedComp, agentId2),
			// agentId1 is still using the component, no globall preStop/stop() is called
			//MethodToString.preStop      (sharedComp),
			//MethodToString.stop         (sharedComp),
			EventToString.eventToString (agentId2, sharedComp,    StoppedEvent.class),
			EventToString.eventToString (agentId2, manualComp2_3, StoppedEvent.class),
			EventToString.eventToString (agentId2, manualComp2_2, StoppedEvent.class)
		);

		// manualComp2_2.manualStart();
		sharedComp.expectExactOrder(
			EventToString.eventToString (agentId2, manualComp2_2, StartingEvent.class),
			EventToString.eventToString (agentId2, manualComp2_2, StartedEvent.class),
			EventToString.eventToString (agentId2, manualComp2_3, StartingEvent.class),
			// we're still running, no global preStart/start() is called, only locals
			//MethodToString.preStart     (sharedComp),
			//MethodToString.start        (sharedComp),
			MethodToString.localPreStart(sharedComp, agentId2),
			EventToString.eventToString (agentId2, sharedComp,    StartingEvent.class),
			MethodToString.localStart   (sharedComp, agentId2),
			EventToString.eventToString (agentId2, sharedComp,    StartedEvent.class),
			EventToString.eventToString (agentId2, manualComp2_3, StartedEvent.class)			
		);
		
		// STOPPING BOTH AGENTS -> COMPONENT IS GOING TO BE STOPPED
		
		// manualComp1_0.manualStop();
		sharedComp.expectExactOrder(
			EventToString.eventToString (agentId1, manualComp1_0, StoppingEvent.class),
			EventToString.eventToString (agentId1, manualComp1_1, StoppingEvent.class),
			MethodToString.localPreStop (sharedComp, agentId1),
			EventToString.eventToString (agentId1, sharedComp,    StoppingEvent.class),
			MethodToString.localStop    (sharedComp, agentId1),
			// no global stopping, there are still agent agentId2 using the component
			//MethodToString.preStop      (sharedComp),
			//MethodToString.stop         (sharedComp),
			EventToString.eventToString (agentId1, sharedComp,    StoppedEvent.class),
			EventToString.eventToString (agentId1, manualComp1_1, StoppedEvent.class),			
			EventToString.eventToString (agentId1, manualComp1_0, StoppedEvent.class)
		);
		
		// manualComp2_2.manualStop();
		sharedComp.expectExactOrder(
			EventToString.eventToString (agentId2, manualComp2_2, StoppingEvent.class),
			EventToString.eventToString (agentId2, manualComp2_3, StoppingEvent.class),
			MethodToString.localPreStop (sharedComp, agentId2),
			EventToString.eventToString (agentId2, sharedComp,    StoppingEvent.class),
			MethodToString.localStop    (sharedComp, agentId2),
			
			// IN FACT ... EventToString.eventToString (agentId2, manualComp2_3, StoppedEvent.class)
			//             HAPPENS FIRST, BUT AS REACTION TO THIS EVENT, SHARED COMPONENT WILL BE STOPPED
			//             IMPORTANT IS ... THAT localStop() HAPPENS PRIOR TO global stop
			MethodToString.preStop      (sharedComp),
			MethodToString.stop         (sharedComp),
			EventToString.eventToString (agentId2, sharedComp,    StoppedEvent.class),
			// NOW THE SHARED COMPONENT IS STOPPED
			// SO THE PROCESSING OF STOPPED EVENT IS GOING TO CONTINUE
			// AND OUR LISTENER RECEIVES IT
			EventToString.eventToString (agentId2, manualComp2_3, StoppedEvent.class),
			
			EventToString.eventToString (agentId2, manualComp2_2, StoppedEvent.class)
		);
				
		//
		// STARTING/STOPPING DEPENDENCIES
		//
		
		manualComp1_0.manualStart();
		
		checkState(sharedComp, ComponentState.RUNNING);
		
		manualComp2_2.manualStart();
		
		checkState(sharedComp, ComponentState.RUNNING);
		
		manualComp1_1.manualPause();
		
		checkState(sharedComp, ComponentState.RUNNING);
		
		manualComp1_1.manualResume();
		
		checkState(sharedComp, ComponentState.RUNNING);
		
		manualComp2_3.manualPause();
		
		checkState(sharedComp, ComponentState.RUNNING);
		
		manualComp2_3.manualResume();
		
		checkState(sharedComp, ComponentState.RUNNING);
		
		manualComp1_1.manualPause();
		
		checkState(sharedComp, ComponentState.RUNNING);

		manualComp2_3.manualPause();
		
		checkState(sharedComp, ComponentState.PAUSED);
		
		manualComp1_1.manualResume();
		
		checkState(sharedComp, ComponentState.RUNNING);
		
		manualComp2_3.manualResume();
		
		checkState(sharedComp, ComponentState.RUNNING);
		
		manualComp2_3.manualPause();
		
		checkState(sharedComp, ComponentState.RUNNING);
		
		manualComp1_1.manualPause();
		
		checkState(sharedComp, ComponentState.PAUSED);

		manualComp2_3.manualResume();
		
		checkState(sharedComp, ComponentState.RUNNING);
		
		manualComp1_1.manualResume();
		
		checkState(sharedComp, ComponentState.RUNNING);

		manualComp1_0.manualStop();
		
		checkState(sharedComp, ComponentState.RUNNING);
		
		manualComp1_0.manualStart();
		
		checkState(sharedComp, ComponentState.RUNNING);

		manualComp2_2.manualStop();
		
		checkState(sharedComp, ComponentState.RUNNING);
		
		manualComp2_2.manualStart();
		
		checkState(sharedComp, ComponentState.RUNNING);
		
		manualComp1_0.manualStop();
		
		checkState(sharedComp, ComponentState.RUNNING);
		
		manualComp2_2.manualStop();		
		
		checkState(sharedComp, ComponentState.STOPPED);
		
		sharedComp.checkNoMoreActivityExpected();
		
		System.out.println("---/// TEST OK ///---");
	}

}
