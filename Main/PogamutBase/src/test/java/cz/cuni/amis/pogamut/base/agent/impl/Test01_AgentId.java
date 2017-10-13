package cz.cuni.amis.pogamut.base.agent.impl;

import junit.framework.Assert;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.tests.BaseTest;

public class Test01_AgentId extends BaseTest {
	
	@Test
	public void test01() {
		IAgentId agentId1 = new AgentId("ahoj");
		IAgentId agentId2 = new AgentId("cau");
		IAgentId agentId3 = new AgentId("ahoj");
		
		System.out.println("AgentId1: " + agentId1.getToken());
		System.out.println("AgentId2: " + agentId2.getToken());
		System.out.println("AgentId3: " + agentId3.getToken());
		
		Assert.assertTrue("agentId1 should equal to itself",        agentId1.equals(agentId1));
		Assert.assertTrue("agentId1 should not equal to null",     !agentId1.equals(null));
		Assert.assertTrue("agentId2 should not equal to agentId1", !agentId1.equals(agentId2));
		Assert.assertTrue("agentId2 should not equal to agentId3", !agentId2.equals(agentId3));
		Assert.assertTrue("agentId1 should not equal to agentId3", !agentId1.equals(agentId3));
		
		Assert.assertTrue("agentId1 name should equal to itself",             agentId1.getName().getFlag().equals(agentId1.getName().getFlag()));
		Assert.assertTrue("agentId1 name should not equal to null",          !agentId1.getName().getFlag().equals(null));
		Assert.assertTrue("agentId2 name should not equal to agentId1 name", !agentId1.getName().getFlag().equals(agentId2.getName().getFlag()));
		Assert.assertTrue("agentId2 name should not equal to agentId3",      !agentId2.getName().getFlag().equals(agentId3.getName().getFlag()));
		Assert.assertTrue("agentId1 name should equal to agentId3 name",      agentId1.getName().getFlag().equals(agentId3.getName().getFlag()));
		
		System.out.println("---/// TEST OK ///---");
 	}

	public static void main(String[] args) {
		Test01_AgentId test = new Test01_AgentId();
		test.test01();
	}
}
