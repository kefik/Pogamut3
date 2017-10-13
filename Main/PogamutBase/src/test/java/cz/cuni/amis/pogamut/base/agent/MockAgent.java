/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.base.agent;

import cz.cuni.amis.pogamut.base.agent.impl.AbstractAgent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;

/**
 * Changes states when proper methods are invoked.
 * @author Ik
 */
public class MockAgent extends AbstractAgent {

	public MockAgent(IAgentId agentId, IComponentBus bus, IAgentLogger logger) {
		super(agentId, bus, logger);
	}

}
