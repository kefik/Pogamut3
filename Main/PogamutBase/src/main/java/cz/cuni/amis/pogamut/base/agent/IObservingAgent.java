package cz.cuni.amis.pogamut.base.agent;

import javax.management.MXBean;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;

/**
 * Observing agent is agent that may watch/observe the world, but is powerless to do anything
 * inside it directly. E.g. it can be a tactical advisor for soldiers, but can not send direct orders
 * to the world's simulator.
 *  
 * @author Jimmy
 *
 */
@MXBean
public interface IObservingAgent extends IAgent {
	
	public IWorldView getWorldView();

}
