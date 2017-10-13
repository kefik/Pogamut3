package cz.cuni.amis.pogamut.base.agent.state.level0;

import java.io.Serializable;

public interface IAgentState extends Serializable {

	/**
	 * Provides convenient method for testing whether the agent is in one of 'states'.
	 * <p><p>
	 * Returns whether this state is one of the 'states' (using instanceof).
	 * @param states
	 * @return
	 */
	public boolean isState(Class... states);
	
	/**
	 * Provides convenient method for testing whether the agent is not in any of 'states'.
	 * <p><p>
	 * Returns whether this state is not any of the 'states' (using instanceof).
	 * @param states
	 * @return
	 */
	public boolean isNotState(Class... states);
	
	/**
	 * Additional information about the state - this is inlcuded to hashCode() and equals() methods.
	 * @return
	 */
	public String getDescription();
	
}
