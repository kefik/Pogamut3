package cz.cuni.amis.pogamut.multi.agent.impl;

import cz.cuni.amis.pogamut.base.utils.guice.AgentTeamScoped;
import cz.cuni.amis.pogamut.multi.agent.ITeamId;
import cz.cuni.amis.utils.token.Token;
import cz.cuni.amis.utils.token.Tokens;

@AgentTeamScoped
public class TeamId implements ITeamId {

	private Token token;

	public TeamId(String teamIdentifier) {
		this.token = Tokens.get(teamIdentifier);
	}
	
	@Override
	public int hashCode() {
		return token.hashCode();
	}
	
	@Override
	public String toString()
	{
		return token.getToken();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof TeamId)) return false;
		return token.equals(((TeamId)obj).token);
	}
	
}
