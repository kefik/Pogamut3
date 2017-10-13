package cz.cuni.amis.pogamut.ut2004.vip.protocol;

public enum CSBotRole {

	/**
	 * Bot is VIP. It needs to escape by reaching a certain location.
	 */
	VIP(CSBotTeam.COUNTER_TERRORIST),
	
	/**
	 * Bot is Terrorist. It needs to kill VIP.
	 */
	TERRORIST(CSBotTeam.TERRORIST),
	
	/**
	 * Bot is Counter-terrorist. It needs to guard VIP.
	 */
	COUNTER_TERRORIST(CSBotTeam.COUNTER_TERRORIST);
	
	public final CSBotTeam team;
	
	private CSBotRole(CSBotTeam team) {
		this.team = team;
	}
}
