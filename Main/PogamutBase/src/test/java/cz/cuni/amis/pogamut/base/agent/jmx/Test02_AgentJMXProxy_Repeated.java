/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.pogamut.base.agent.jmx;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.management.ObjectInstance;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.MockAgent;
import cz.cuni.amis.pogamut.base.agent.impl.AbstractAgent;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.agent.jmx.proxy.AgentJMXProxy;
import cz.cuni.amis.pogamut.base.agent.state.level0.IAgentState;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStatePaused;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStatePausing;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateResuming;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateStarting;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateStopped;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateStopping;
import cz.cuni.amis.pogamut.base.agent.state.level3.IAgentStateResumed;
import cz.cuni.amis.pogamut.base.agent.state.level3.IAgentStateStarted;
import cz.cuni.amis.pogamut.base.component.bus.ComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.utils.flag.FlagListener;
import cz.cuni.amis.utils.flag.WaitForFlagChange;
import cz.cuni.amis.utils.flag.WaitForFlagChange.IAccept;
import cz.cuni.amis.utils.token.IToken;

/**
 * Exports agent as MBean and then test functionality of this MBean.
 * @author Ik
 */
import cz.cuni.amis.tests.BaseTest;
				
public class Test02_AgentJMXProxy_Repeated extends BaseTest {

    public Test02_AgentJMXProxy_Repeated() {
    }

    AbstractAgent agent = null;
    ObjectInstance agentObjectInstance = null;
    AgentJMXProxy agentProxy = null;
    IToken token;
    
    Queue<Class> expectingStateChanges = new LinkedList<Class>();
	private String agentAddress;

    @Before
    public void setUp() {
    	IAgentId agentId = new AgentId("Test01_AgentJMXProxy");
    	token = agentId;
		IAgentLogger logger = new AgentLogger(agentId);
		logger.addDefaultConsoleHandler();
		logger.setLevel(Level.ALL);
		IComponentBus bus = new ComponentBus(logger);
   
        agent = new MockAgent(agentId, bus, logger);
        
        agentAddress = agent.getJMX().enableJMX();
    }

    @After
    public void tearDown() {
    	Pogamut.getPlatform().close();
    }

    /**
     * Test of getAgentState method from class AgentMBeanAdapter.
     */
    @Test
    public void testAgentNotifications() {
    	final int repetitions = 5; 	
    	
    	FlagListener<IAgentState> stateListener = new FlagListener<IAgentState>() {
			@Override
			public void flagChanged(IAgentState changedValue) {
				Class cls = expectingStateChanges.peek();
				if (cls.isAssignableFrom(changedValue.getClass())) {
					System.out.println("CONSUMING " + cls);
					expectingStateChanges.poll();
				}
			}
		};
		
		agentProxy = new AgentJMXProxy(agentAddress);
		
		Assert.assertTrue("name should be Test01_AgentJMXProxy", agentProxy.getName().equals("Test01_AgentJMXProxy"));
		Assert.assertTrue("token should be " + token.getToken(), token.getToken().equals(agentProxy.getComponentId().getToken()));

		agentProxy.getState().addListener(stateListener);
				
		for (int i = 0; i < repetitions; ++i) {
			if (i == 0) {
				expectingStateChanges.add(IAgentStateStarting.class);
				expectingStateChanges.add(IAgentStateStarted.class);
				agent.start();
			} else {
				agent.start();
				agentProxy = new AgentJMXProxy(agentAddress);				
				Assert.assertTrue("name should be Test01_AgentJMXProxy", agentProxy.getName().equals("Test01_AgentJMXProxy"));
				Assert.assertTrue("token should be " + token.getToken(), token.getToken().equals(agentProxy.getComponentId().getToken()));
				agentProxy.getState().addListener(stateListener);
			}
			
	    	expectingStateChanges.add(IAgentStatePausing.class);
	    	expectingStateChanges.add(IAgentStatePaused.class);
	    	expectingStateChanges.add(IAgentStateResuming.class);
	    	expectingStateChanges.add(IAgentStateResumed.class);
	    	expectingStateChanges.add(IAgentStatePausing.class);
	    	expectingStateChanges.add(IAgentStatePaused.class);
	    	expectingStateChanges.add(IAgentStateResuming.class);
	    	expectingStateChanges.add(IAgentStateResumed.class);
	    	expectingStateChanges.add(IAgentStateStopping.class);
	    	expectingStateChanges.add(IAgentStateStopped.class);

	    	agent.pause();
			agent.resume();
			agent.pause();
			agent.resume();
			agent.stop();
			
			IAgentState result = new WaitForFlagChange<IAgentState>(agentProxy.getState(), new IAccept<IAgentState>() {

				@Override
				public boolean accept(IAgentState flagValue) {
					return flagValue instanceof IAgentStateStopped;
				}
				
			}).await(10000, TimeUnit.MILLISECONDS);
			
			if (result == null || !(result instanceof IAgentStateStopped)) {
				Assert.fail("Failed to sense " + (i+1) + "th IAgentStateStopped in 10 secs.");
			}
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				throw new RuntimeException("Interrupted while asleep...", e);
			}
			
			if (expectingStateChanges.size() > 0) {
				System.out.println((i+1) + ". agent should have gone through all expected states, remaining state changes " + expectingStateChanges.size() + ".");
				Assert.assertEquals((i+1) + ". agent should have gone through all expected states, remaining state changes " + expectingStateChanges.size() + ".",
						 			expectingStateChanges.size(), 0);
			}
		}		
		
		System.out.println("---/// TEST OK ///---");
    }
    
    public static void main(String[] args) throws Exception {
		Test02_AgentJMXProxy_Repeated test = new Test02_AgentJMXProxy_Repeated();
		test.setUp();
		test.testAgentNotifications();
		test.tearDown();
	}

}
