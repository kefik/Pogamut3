package cz.cuni.amis.pogamut.base.agent.impl;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.IObservingAgent;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.utils.NullCheck;

/**
 * The main difference between AbstractAgent and AbstractObservingAgent is that
 * this one has a world to observe. It's a next step towards embodied agent.
 * 
 * @author Jimmy
 * @param WorldView
 *            class of the worldview the agent is working with
 */
@AgentScoped
public abstract class AbstractObservingAgent<WORLD_VIEW extends IWorldView>
        extends AbstractAgent implements IObservingAgent {
 
    /**
     * Instance of the world view - basic agent's internal representation of the
     * world.
     * <p>
     * <p>
     * Accessible via getWorldView().
     */
    private WORLD_VIEW worldView;

    @Inject
    public AbstractObservingAgent(IAgentId agentId, IComponentBus bus, IAgentLogger logger, WORLD_VIEW worldView) {
        super(agentId, bus, logger);
        this.worldView = worldView;
        NullCheck.check(this.worldView, "worldView");
        addDependency(worldView);
    }

    /**
     * Returns abstraction for the agent's world. That can be anything the range
     * is broad ... from chess board to the UT2004 3D environment. <BR>
     * <BR>
     * The implementation may be different as the user needs.
     *
     * @return
     */
    @Override
    public WORLD_VIEW getWorldView() {
        return worldView;
    }

}