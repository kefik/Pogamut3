package cz.cuni.amis.pogamut.base.component.bus;

import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentIdClashException;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.ILifecycleBus;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.LifecycleBus;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;

import cz.cuni.amis.tests.BaseTest;
				
public class Test09_LifecycleBus extends BaseTest {
	
	@Test
	public void test() {
		IAgentId agentId = new AgentId("Test09_LifecycleBus");
		final IAgentLogger logger = new AgentLogger(agentId);
		logger.addDefaultConsoleHandler();
		logger.setLevel(Level.ALL);
		ILifecycleBus bus = new LifecycleBus(logger);
		
		IComponent cmp1 = new IComponent() {

			@Override
			public IToken getComponentId() {
				return Tokens.get("IdClashToken");
			}

			public Logger getLog() {
				return logger.getCategory("CMP1");
			}
			
		};
		
		IComponent cmp2 = new IComponent() {

			@Override
			public IToken getComponentId() {
				return Tokens.get("IdClashToken");
			}
			
			public Logger getLog() {
				return logger.getCategory("CMP2");
			}
			
		};
		
		bus.register(cmp1);
		
		boolean exception = false;
		try {
			bus.register(cmp1);
		} catch (ComponentIdClashException e) {
			exception = true;
		}
		Assert.assertTrue("registering the same component twice should not result in an exception", !exception);
		exception = false;
		
		Assert.assertTrue("retrieved component under id " + cmp1.getComponentId().getToken() + " is not the same as " + cmp1, bus.getComponent(cmp1.getComponentId()) == cmp1);
		
		try {
			bus.register(cmp2);
		} catch (ComponentIdClashException e) {
			exception = true;
		}
		Assert.assertTrue("registering two components under the same id should throw an exception", exception);
		Assert.assertTrue("retrieved component under id " + cmp1.getComponentId().getToken() + " is not the same as " + cmp1, bus.getComponent(cmp1.getComponentId()) == cmp1);
		
		System.out.println("---/// TEST OK ///---");
	}
	
	public static void main(String[] args) {
		Test09_LifecycleBus test = new Test09_LifecycleBus();
		test.test();
	}

}
