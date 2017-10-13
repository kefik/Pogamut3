package cz.cuni.amis.pogamut.multi.agent;

import cz.cuni.amis.pogamut.base.agent.IAgentId;

/**
 * Interface for agentId with a team.
 * @author srlok
 *
 */
public interface ITeamedAgentId extends IAgentId {
	
	/**
	 * Returns the teamId associated with this agentId.
	 * @return
	 */
	ITeamId getTeamId();
	
}
