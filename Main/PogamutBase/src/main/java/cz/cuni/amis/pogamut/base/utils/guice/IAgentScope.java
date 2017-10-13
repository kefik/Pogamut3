package cz.cuni.amis.pogamut.base.utils.guice;

import com.google.inject.Scope;

/**
 * Scope that treats all {@link AgentScoped} classes as singletons.
 * @author Jimmy
 *
 */
public interface IAgentScope extends Scope {

	/**
	 * Release all {@link AgentScoped} objects that the scope is holding.
	 */
	public void clearScope();
	
}
