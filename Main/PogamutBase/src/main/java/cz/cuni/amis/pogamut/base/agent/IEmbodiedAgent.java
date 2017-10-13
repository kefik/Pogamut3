package cz.cuni.amis.pogamut.base.agent;

import javax.management.MXBean;

import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;

/**
 * Embodied agents are agents that are physically present in the world. May observe it as well as to 
 * act inside it. Note that {@link IEmbodiedAgent} has the same interface as {@IGhostAgent}. That's because
 * the embodied agent represent philosophically different category of objects and will usually have different
 * implementations of {@link IAct}. Ghost agents are not capable
 * to interact between themselves via virtual world (may be only by virtual worlds's simulator utility methods).
 * 
 * @author Jimmy
 */
@MXBean
public interface IEmbodiedAgent extends IObservingAgent {
	
	public IAct getAct();

}
