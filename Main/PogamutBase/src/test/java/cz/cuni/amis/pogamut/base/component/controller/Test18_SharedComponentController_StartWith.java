package cz.cuni.amis.pogamut.base.component.controller;

import java.util.logging.Level;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.StartedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.StartingEvent;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.ILifecycleBus;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.LifecycleBus;
import cz.cuni.amis.pogamut.base.component.stub.component.ManualCheckComponent;
import cz.cuni.amis.pogamut.base.component.stub.sharedcomponent.AutoCheckSharedComponent;
import cz.cuni.amis.pogamut.base.component.stub.sharedcomponent.EventToString;
import cz.cuni.amis.pogamut.base.component.stub.sharedcomponent.MethodToString;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;

/**
 * Tests: start
 * @author Jimmy
 *
 */
import cz.cuni.amis.tests.BaseTest;
				
public class Test18_SharedComponentController_StartWith extends BaseTest {
	
	@Test
	public void test() {
		IAgentId agentId1 = new AgentId("Test18_Shared-Agent1");
		IAgentId agentId2 = new AgentId("Test18_Shared-Agent2");
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
				
		manualComp2_2.manualStart();
		
		sharedComp.checkNoMoreActivityExpected();
		
		if (sharedComp.getController().notInState(ComponentState.RUNNING)) {
			String msg = "[ERROR] " + sharedComp.getComponentId().getToken() + " is not in state RUNNING but in state " + sharedComp.getController().getState().getFlag() + ", INVALID!";
			System.out.println(msg);
			throw new RuntimeException(msg);
		}
		
		System.out.println("---/// TEST OK ///---");
	}

}
