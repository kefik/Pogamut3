package cz.cuni.amis.pogamut.base.agent.impl;

import java.util.logging.Level;

import junit.framework.Assert;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.MockAgent;
import cz.cuni.amis.pogamut.base.agent.module.MockModule;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateDown;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateUp;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateFailed;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateInstantiated;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStatePaused;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateRunning;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateStopped;
import cz.cuni.amis.pogamut.base.agent.state.level3.IAgentStateResumed;
import cz.cuni.amis.pogamut.base.agent.state.level3.IAgentStateStarted;
import cz.cuni.amis.pogamut.base.component.bus.ComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.FatalErrorEvent;
import cz.cuni.amis.pogamut.base.component.controller.ComponentState;
import cz.cuni.amis.pogamut.base.component.stub.component.ComponentStub;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;

import cz.cuni.amis.tests.BaseTest;
				
public class Test04_AbstractAgent_WithModule_FatalErrors extends BaseTest {

	@Test
	public void test() {
		IAgentId agentId = new AgentId("Test");
		AgentLogger logger = new AgentLogger(agentId);
		logger.setLevel(Level.ALL);
		logger.addDefaultConsoleHandler();
		IComponentBus bus = new ComponentBus(logger);
		MockAgent agent = new MockAgent(agentId, bus, logger);
		MockModule module = new MockModule(agent);
		ComponentStub stub = new ComponentStub(logger, bus);
		
		Assert.assertTrue("agent not in down-state", agent.inState(IAgentStateDown.class));
		Assert.assertTrue("agent not in instantiated-state", agent.inState(IAgentStateInstantiated.class));
		Assert.assertTrue("module not in instantiated-state", module.getState().getFlag() == ComponentState.INSTANTIATED);
		
		for (int i = 0; i < 10; ++i) {
			agent.start();
			Assert.assertTrue("agent not in up-state", agent.inState(IAgentStateUp.class));
			Assert.assertTrue("agent not in started-state", agent.inState(IAgentStateStarted.class));
			Assert.assertTrue("agent not in running-state", agent.inState(IAgentStateRunning.class));
			Assert.assertTrue("module not in running-state", module.getState().getFlag() == ComponentState.RUNNING);
			
			bus.event(new FatalErrorEvent(stub, "failure"));
			Assert.assertTrue("agent not in down-state", agent.inState(IAgentStateDown.class));
			Assert.assertTrue("agent not in stopped-state", agent.inState(IAgentStateFailed.class));
			Assert.assertTrue("module not in stopped-state", module.getState().getFlag() == ComponentState.KILLED);
		}
		
		agent.start();
		Assert.assertTrue("agent not in up-state", agent.inState(IAgentStateUp.class));
		Assert.assertTrue("agent not in started-state", agent.inState(IAgentStateStarted.class));
		Assert.assertTrue("agent not in running-state", agent.inState(IAgentStateRunning.class));
		Assert.assertTrue("module not in running-state", module.getState().getFlag() == ComponentState.RUNNING);
		
		for (int i = 0; i < 10; ++i) {
			agent.pause();
			Assert.assertTrue("agent not in up-state", agent.inState(IAgentStateUp.class));
			Assert.assertTrue("agent not in paused-state", agent.inState(IAgentStatePaused.class));
			Assert.assertTrue("module not in paused-state", module.getState().getFlag() == ComponentState.PAUSED);
			
			agent.resume();
			Assert.assertTrue("agent not in up-state", agent.inState(IAgentStateUp.class));
			Assert.assertTrue("agent not in resumed-state", agent.inState(IAgentStateResumed.class));
			Assert.assertTrue("module not in running-state", module.getState().getFlag() == ComponentState.RUNNING);
		}
		
		for (int i = 0; i < 10; ++i) {
			bus.event(new FatalErrorEvent(stub, "failure"));
			Assert.assertTrue("agent not in down-state", agent.inState(IAgentStateDown.class));
			Assert.assertTrue("agent not in stopped-state", agent.inState(IAgentStateFailed.class));
			Assert.assertTrue("module not in killed-state", module.getState().getFlag() == ComponentState.KILLED);
			
			agent.start();
			Assert.assertTrue("agent not in up-state", agent.inState(IAgentStateUp.class));
			Assert.assertTrue("agent not in started-state", agent.inState(IAgentStateStarted.class));
			Assert.assertTrue("agent not in running-state", agent.inState(IAgentStateRunning.class));
			Assert.assertTrue("module not in running-state", module.getState().getFlag() == ComponentState.RUNNING);
		}
		
		for (int i = 0; i < 10; ++i) {
			agent.pause();
			Assert.assertTrue("agent not in up-state", agent.inState(IAgentStateUp.class));
			Assert.assertTrue("agent not in paused-state", agent.inState(IAgentStatePaused.class));
			Assert.assertTrue("module not in paused-state", module.getState().getFlag() == ComponentState.PAUSED);
			
			agent.resume();
			Assert.assertTrue("agent not in up-state", agent.inState(IAgentStateUp.class));
			Assert.assertTrue("agent not in resumed-state", agent.inState(IAgentStateResumed.class));
			Assert.assertTrue("module not in running-state", module.getState().getFlag() == ComponentState.RUNNING);
			
			bus.event(new FatalErrorEvent(stub, "failure"));
			Assert.assertTrue("agent not in down-state", agent.inState(IAgentStateDown.class));
			Assert.assertTrue("agent not in stopped-state", agent.inState(IAgentStateFailed.class));
			Assert.assertTrue("module not in killed-state", module.getState().getFlag() == ComponentState.KILLED);
			
			agent.start();
			Assert.assertTrue("agent not in up-state", agent.inState(IAgentStateUp.class));
			Assert.assertTrue("agent not in started-state", agent.inState(IAgentStateStarted.class));
			Assert.assertTrue("agent not in running-state", agent.inState(IAgentStateRunning.class));
			Assert.assertTrue("module not in running-state", module.getState().getFlag() == ComponentState.RUNNING);
		}
		
		agent.stop();
		Assert.assertTrue("agent not in down-state", agent.inState(IAgentStateDown.class));
		Assert.assertTrue("agent not in stopped-state", agent.inState(IAgentStateStopped.class));
		Assert.assertTrue("module not in stopped-state", module.getState().getFlag() == ComponentState.STOPPED);
		
		System.out.println("---/// TEST OK ///---");
		
	}
	
}
