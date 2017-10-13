package cz.cuni.amis.pogamut.multi.agent.impl;

import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.multi.agent.ITeamId;

public class AgentTeamedId extends AgentId {
	
	protected ITeamId teamId;
	
	ITeamId getTeamId() { return teamId; };
}
