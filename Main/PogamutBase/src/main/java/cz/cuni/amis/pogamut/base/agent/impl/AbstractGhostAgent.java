package cz.cuni.amis.pogamut.base.agent.impl;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.IGhostAgent;
import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.utils.NullCheck;

/**
 * Next step to embodied agents are those without the physical body
 * but with the ability to act inside the environment.
 * <p><p>
 * Example can be a world controller that is connected to the world gets all world
 * events (knows everything) and can alter the environment (sounds like a god, right? :-).
 * 
 * @author Jimmy
 */
@AgentScoped
public abstract class AbstractGhostAgent<WORLD_VIEW extends IWorldView, ACT extends IAct> 
       extends        AbstractObservingAgent<WORLD_VIEW>
       implements     IGhostAgent {
	
	private ACT act;
	
	@Inject
	public AbstractGhostAgent(IAgentId agentId, IComponentBus bus, IAgentLogger logger, WORLD_VIEW worldView, ACT act) {
		super(agentId, bus, logger, worldView);
		this.act = act;
		NullCheck.check(this.act, "act");
		addDependency(act);
	}
	
    @Override
	public ACT getAct() {
		return act; 
	}
	
}
