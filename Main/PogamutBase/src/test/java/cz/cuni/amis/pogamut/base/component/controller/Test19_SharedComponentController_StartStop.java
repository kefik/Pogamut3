package cz.cuni.amis.pogamut.base.component.controller;

import java.util.logging.Level;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
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
 * Tests: start/stop
 * @author Jimmy
 *
 */
import cz.cuni.amis.tests.BaseTest;
				
public class Test19_SharedComponentController_StartStop extends BaseTest {
	
	@Test
	public void test() {
		IAgentId agentId1 = new AgentId("Test19_Shared-Agent1");
		IAgentId agentId2 = new AgentId("Test19_Shared-Agent2");
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
		// manualComp1_0.manualStop();
		sharedComp.expectExactOrder(
			EventToString.eventToString (agentId1, manualComp1_0, StoppingEvent.class),
			// manualComp1_0 StoppingEvent is triggering...
			EventToString.eventToString (agentId1, manualComp1_1, StoppingEvent.class),
			// NOW manualComp1_1 IS SENDING StoppingEvent ... so local shared component is reacting
			//                                                with stopping
			// which is triggering sharedComp StoppingEvent on agentId1 which begins with localPreStop()
			MethodToString.localPreStop (sharedComp, agentId1),
			// after sharedComp.prestop the StoppingEvent on agentId1 is broadcast
			EventToString.eventToString (agentId1, sharedComp,    StoppingEvent.class),
			// after stopping event comes a sharedComp.localStop on agentId1
			MethodToString.localStop    (sharedComp, agentId1),
			// and there is nothing that supports sharedComp, so it is stopping globally
			MethodToString.preStop      (sharedComp),
			MethodToString.stop         (sharedComp),
			// finally broadcasting StoppedEvent of sharedComp on the agentId1 bus
			EventToString.eventToString (agentId1, sharedComp,    StoppedEvent.class),
			// NOW THE SHARED COMPONENT IS STOPPED
			// SO THE PROCESSING OF STOPPED EVENT IS GOING TO CONTINUE
			// AND OUR LISTENER RECEIVES IT
			EventToString.eventToString (agentId1, manualComp1_1, StoppedEvent.class),
			
			EventToString.eventToString (agentId1, manualComp1_0, StoppedEvent.class)
		);
		
		// manualComp2_2.manualStart();
		sharedComp.expectExactOrder(
			EventToString.eventToString (agentId2, manualComp2_2, StartingEvent.class),
			EventToString.eventToString (agentId2, manualComp2_2, StartedEvent.class),
			EventToString.eventToString (agentId2, manualComp2_3, StartingEvent.class),
			MethodToString.preStart     (sharedComp),
			MethodToString.start        (sharedComp),
			MethodToString.localPreStart(sharedComp, agentId2),
			EventToString.eventToString (agentId2, sharedComp,    StartingEvent.class),
			MethodToString.localStart   (sharedComp, agentId2),
			EventToString.eventToString (agentId2, sharedComp,    StartedEvent.class),
			EventToString.eventToString (agentId2, manualComp2_3, StartedEvent.class)			
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
		
		if (sharedComp.getController().notInState(ComponentState.RUNNING)) {
			String msg = "[ERROR] " + sharedComp.getComponentId().getToken() + " is not in state RUNNING but in state " + sharedComp.getController().getState().getFlag() + ", INVALID!";
			System.out.println(msg);
			throw new RuntimeException(msg);
		}
		
		manualComp1_0.manualStop();
		
		if (sharedComp.getController().notInState(ComponentState.STOPPED)) {
			String msg = "[ERROR] " + sharedComp.getComponentId().getToken() + " is not in state STOPPED but in state " + sharedComp.getController().getState().getFlag() + ", INVALID!";
			System.out.println(msg);
			throw new RuntimeException(msg);
		}
		
		manualComp2_2.manualStart();
		
		if (sharedComp.getController().notInState(ComponentState.RUNNING)) {
			String msg = "[ERROR] " + sharedComp.getComponentId().getToken() + " is not in state RUNNING but in state " + sharedComp.getController().getState().getFlag() + ", INVALID!";
			System.out.println(msg);
			throw new RuntimeException(msg);
		}
		
		manualComp2_2.manualStop();
		
		if (sharedComp.getController().notInState(ComponentState.STOPPED)) {
			String msg = "[ERROR] " + sharedComp.getComponentId().getToken() + " is not in state STOPPED but in state " + sharedComp.getController().getState().getFlag() + ", INVALID!";
			System.out.println(msg);
			throw new RuntimeException(msg);
		}
		
		sharedComp.checkNoMoreActivityExpected();
		
		System.out.println("---/// TEST OK ///---");
	}

}
