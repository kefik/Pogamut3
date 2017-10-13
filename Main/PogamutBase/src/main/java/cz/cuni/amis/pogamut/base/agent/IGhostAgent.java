package cz.cuni.amis.pogamut.base.agent;

import javax.management.MXBean;

import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;

/**
 * Interface of the agent that may observe the world and can even act inside it
 * but does not have the body (notice that in case that the world contains agents-ghosts, that are
 * "physically" being somewhere, they would be {@link IEmbodiedAgent} not this {@link IGhostAgent}).
 * <p><p>
 * This would typically be "world's simulator controller".
 * 
 * @author ik
 */
@MXBean
@AgentScoped
public interface IGhostAgent extends IObservingAgent {

    /**
     * Returns an object through which we may communicate with the world. Represents
     * the agent effectors providing a low-level act() method. You have to instantiate
     * CommandObject for yourself.
     * @return
     */
    public IAct getAct();
}
