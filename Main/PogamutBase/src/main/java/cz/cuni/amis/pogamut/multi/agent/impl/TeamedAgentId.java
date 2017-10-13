package cz.cuni.amis.pogamut.multi.agent.impl;

import java.util.UUID;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.multi.agent.ITeamId;
import cz.cuni.amis.pogamut.multi.agent.ITeamedAgentId;

@AgentScoped
public class TeamedAgentId extends AgentId implements ITeamedAgentId{
	
	private TeamId teamId;

	public TeamedAgentId() {
		super();
		this.teamId = new TeamId(new UUID(random.nextLong(), random.nextLong()).toString());
	}
	
	@Inject
	public TeamedAgentId(@Named(AGENT_NAME_DEPENDENCY) String agentName) {
		super(agentName);
	}
	
	public TeamedAgentId(String agentName, String teamId) {
		super(agentName);
		this.teamId = new TeamId(teamId);
	}

	@Override
	public ITeamId getTeamId() {
		return teamId;
	}
	
	@Override
	public String toString()
	{
		return "TeamedAgentId[" + super.getName().getFlag() + " | " + String.valueOf(teamId) + "]";
	}

	public void setTeamId(TeamId teamId) {
		this.teamId = teamId;
	}
	
}
