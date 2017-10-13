package cz.cuni.amis.pogamut.base.agent.utils.runner.impl;

import java.util.ArrayList;
import java.util.List;

import cz.cuni.amis.pogamut.base.agent.params.IAgentParameters;
import cz.cuni.amis.pogamut.base.agent.utils.runner.IAgentDescriptor;
import cz.cuni.amis.pogamut.base.agent.utils.runner.IMultipleAgentRunner;
import cz.cuni.amis.pogamut.base.factory.guice.GuiceAgentModule;

/**
 * Base implementation of the {@link IAgentDescriptor}. It specify which agents and how many should
 * be instantiated and which parameters they should be given. 
 * <p><p>
 * See the interface ({@link IAgentDescriptor}) for more info.
 * <p><p>
 * Have checked the javadoc for {@link IAgentDescriptor}? Good. Now you might be wondering, about number
 * of agents that are going to be instantiated. Here are the scenarios again:
 * <ol>
 * <li>You will specify only number of agents via {@link AgentDescriptor#setCount(int)} - in this case, the {@link IMultipleAgentRunner} 
 * will instantiate specified number of agents with default parameters</li>
 * <li>You will specify only parameters of the agents via either {@link AgentDescriptor#setAgentParameters(IAgentParameters[])} or {@link AgentDescriptor#addParams(IAgentParameters...)} - 
 * in this case, the {@link IMultipleAgentRunner} will instantiate the same number of agents as there are parameters</li>
 * <li>You will specify both, count and parameters - in this case it depends on the relation between set count and number of parameters<p>
 * If count > number of parameters - the {@link IMultipleAgentRunner} will instantiate 'count' of agents and first 'number of parameters' will have custom params as specified, rest will have defaults<p>
 * If count == number of parameters - same as the case 2<p>
 * If count < number of parameters - the {@link IMultipleAgentRunner} will instancitate 'count' of agents using first 'count' of 'parameters' passed
 * </li> 
 * </ol>
 * The exact number of agents that will be instantiated can be obtained from the {@link AgentDescriptor#getCount()} that
 * not necessarily match the number you will pass into it through {@link AgentDescriptor#setCount(int)} because of described
 * possibilities.
 * 
 * @author Jimmy
 *
 * @param <PARAMS>
 * @param <MODULE>
 */
public class AgentDescriptor<PARAMS extends IAgentParameters, MODULE extends GuiceAgentModule> implements IAgentDescriptor<PARAMS, MODULE>{

	private MODULE module = null;
	private List<PARAMS> params = new ArrayList<PARAMS>();
	private int count = -1;
	
	@Override
	public MODULE getAgentModule() {
		return module;
	}

	/**
	 * Sets agent module to be used for the instantiation of the agent.
	 * <p><p>
	 * For more info see {@link IAgentDescriptor#getAgentModule()}.
	 * 
	 * @param module
	 * @return this instance
	 */
	public AgentDescriptor<PARAMS, MODULE> setAgentModule(MODULE module) {
		this.module = module;
		return this;
	}
	
	@Override
	public PARAMS[] getAgentParameters() {
		return (PARAMS[]) params.toArray(new IAgentParameters[0]);
	}
	
	/**
	 * Clears all the params stored within {@link AgentDescriptor} and assigns 'params'.
	 * <p><p>
	 * For more info see {@link IAgentDescriptor#getAgentParameters()}.
	 *  
	 * @param params
	 * @return
	 */
	public AgentDescriptor<PARAMS, MODULE> setAgentParameters(PARAMS[] params) {
		this.params.clear();
		if (params == null) return this;
		for (PARAMS param : params) {
			this.params.add(param);
		}
		return this;
	}
	
	/**
	 * Adds parameters for another agents. Note that the number of parameters must be the same as the count
	 * of agents specified via {@link AgentDescriptor#setCount(int)}.
	 * <p><p>
	 * For more info see {@link IAgentDescriptor#getAgentParameters()}.
	 * 
	 * @param params
	 * @return this instance
	 */
	public AgentDescriptor<PARAMS, MODULE> addParams(PARAMS... params) {
		if (params == null) return this;
		for (PARAMS param : params) {
			this.params.add(param);
		}
		return this;
	}
	
	/**
	 * Returns number of agents to be instantiated using current {@link AgentDescriptor#getAgentModule()}.
	 * <p><p> 
     * If {@link AgentDescriptor#setCount(int)} is called with positive int param, that this will return the passed
     * count. Otherwise it returns the {@link AgentDescriptor#params}.size(). I.e. the number of parameters you've added via {@link AgentDescriptor#addParams(IAgentParameters)} or set
	 * via {@link AgentDescriptor#setAgentParameters(IAgentParameters[])}.
	 * <p><p>
	 * For more info see {@link IAgentDescriptor#getCount()} and javadoc for {@link IAgentDescriptor} and {@link AgentDescriptor}.
	 */
	@Override
	public int getCount() {
		if (count == -1) return params.size();
		if (count > params.size()) return count;
		if (count == params.size()) return count;
		// count < params.size()
		return count;
	}
	
	/**
	 * Set number of agents to be instantiated.
	 * <p><p>
	 * For more info see {@link IAgentDescriptor#getCount()}.
	 * 
	 * @param count
	 * @return
	 */
	public AgentDescriptor<PARAMS, MODULE> setCount(int count) {
		this.count = count;
		return this;
	}

}
