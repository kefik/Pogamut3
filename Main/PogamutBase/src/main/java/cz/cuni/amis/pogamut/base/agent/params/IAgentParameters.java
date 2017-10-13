package cz.cuni.amis.pogamut.base.agent.params;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.utils.runner.IAgentRunner;

/**
 * Parent interface for all agent's parameters interfaces/implementations.
 * <p><p>
 * Note that this is the most general way for passing parameters to Java, i.e., you are allowed to pass
 * arbitrary object (possibly containing getters for more objects) that configures your agent.
 * <p><p>
 * The best (and intended) usage is to put the implementation of this interface into the module for 
 * Guice IOC which will allow you to inject those parameters to any class you want allowing you to 
 * easily pass values from the outside to any inner agent's class (it suffice to put {@link IAgentParameters}
 * as a parameter of the constructor used by Guice). 
 * 
 * @author Jimmy
 */
public interface IAgentParameters {

	/**
	 * Fills missing parameters of 'this' with values from 'defaults'.
	 * <p><p>
	 * This method is meant as a hook for {@link IAgentRunner}s that can ease the burden
	 * of instantiating&launching the agent into a specific environment.
	 * <p><p>
	 * It assigns params from 'default' only to fields (of this) that are null!
	 * 
	 * @param defaults values that should filled missing parameters
	 */
	public void assignDefaults(IAgentParameters defaults);
	
	/**
	 * Returns unique agent's id (and human-readable name) that is going to be used by the newly created agent instance.
	 * @return unique agent's id
	 */
	public IAgentId getAgentId();
	
}
