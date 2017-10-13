package cz.cuni.amis.pogamut.base.agent.state.impl;

import java.io.Serializable;

import cz.cuni.amis.pogamut.base.agent.state.level0.IAgentState;
import cz.cuni.amis.utils.SafeEquals;

/**
 * Wrapper for the AgentState adding additional String information that provides
 * more details about the agent state.
 * <p><p>
 * WARNING: equals() defined according to it'description as well only!
 * <p><p>
 * Note that we can not assume which state the agent may find itself in, so we will use a Java object hierarchy to represent
 * it. We provide a set of interfaces that may extend each other to provide the information about the state, e.g.
 * the state may be only {@IAgentUp} but we may create also interface {@IAgentRunning} that extends {@IAgentUp} thus
 * allowing other objects to know, that {@IAgentRunning} means that agent is healthy because that is what {@IAgentUp} state
 * declares.  
 * 
 * @author Jimmy
 */
public abstract class AgentState implements IAgentState, Serializable {
	
	private String description;

	public AgentState(String description) {
		this.description = description;
	}
	
	@Override
	public boolean isState(Class... states) {
		for (Class state : states) {
			if (state.isAssignableFrom(this.getClass())) return true;
		}
		return false;
	}
	
	@Override
	public boolean isNotState(Class... states) {
		for (Class state : states) {
			if (state.isAssignableFrom(this.getClass())) return false;
		}
		return true;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof AgentState)) return false; 
		AgentState state = (AgentState)o;
		return SafeEquals.equals(description, state.description);
	}

	public String getDescription() {
		return description;
	}
	
	@Override
	public String toString() {
		if (this == null) return "AgentState-instantiating";
		return getClass().getSimpleName()+"[" + description + "]";
	}
	
}