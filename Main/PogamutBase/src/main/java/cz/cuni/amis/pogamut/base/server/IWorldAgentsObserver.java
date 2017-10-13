package cz.cuni.amis.pogamut.base.server;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.utils.collections.ObservableCollection;

/** 
 * Provides functionality for observing GaviaLib based agents connected to the world.
 * @author Ik
 */
public interface IWorldAgentsObserver<A extends IAgent> {

    /**
     * Instances of the IAgent interface returned might not be the agents themselves
     * as they can run on remote machines. In this case it will be proxy objects 
     * that can control those agents.
     * 
     * @return List of all Pogamut based agents in the world.
     */
    ObservableCollection<A> getAgents();
    
}