package cz.cuni.amis.pogamut.ut2004.vip.protocol;

import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;

public enum CSBotTeam {
	
	/**
	 * Bot is playing in "terrorist" team. Always UT2004 red team, i.e. {@link AgentInfo#TEAM_RED}.
	 */
	TERRORIST(AgentInfo.TEAM_RED),
	
	/**
	 * Bot is playing in "counter terrorist" team. Always UT2004 blue team, i.e. {@link AgentInfo#TEAM_BLUE}.
	 */
	COUNTER_TERRORIST(AgentInfo.TEAM_BLUE);
	
	public final int ut2004Team;
	
	private CSBotTeam(int ut2004Team) {
		this.ut2004Team = ut2004Team;
	}
	
	public CSBotTeam getEnemyTeam() {
		return this == TERRORIST ? COUNTER_TERRORIST : TERRORIST;
	}

	public static CSBotTeam getFromUT2004Team(Integer ut2004Team) {
		for (CSBotTeam value : CSBotTeam.values()) {
			if (value.ut2004Team == ut2004Team) {
				return value;
			}
		}
		return null;
	}

}
