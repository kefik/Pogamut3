package cz.cuni.amis.pogamut.ut2004.vip.protocol;

public enum CSBotState {
	
	/**
	 * Bot is VIP.
	 */
	VIP(CSBotRole.VIP),
	/**
	 * Bot is VIP and is dead => CTs lose.
	 */
	VIP_DEAD(CSBotRole.VIP),
	/**
	 * Bot is VIP and is safe => Terrorists lose.
	 */
	VIP_SAFE(CSBotRole.VIP),
	/**
	 * Bot is terrorist, it has to kill VIP.
	 */
	TERRORIST(CSBotRole.TERRORIST),
	/**
	 * Bot is terrorist, but is dead now. Needs to wait for another round.
	 */
	TERRORIST_DEAD(CSBotRole.TERRORIST),
	/**
	 * Bot is counter-terrorist, it has to guard VIP.
	 */
	COUNTER_TERRORIST(CSBotRole.COUNTER_TERRORIST),
	/**
	 * Bot is counter-terrorist, but is dead now. Needs to wait for another round.
	 */
	COUNTER_TERRORIST_DEAD(CSBotRole.COUNTER_TERRORIST);
	
	public final CSBotRole role;

	private CSBotState(CSBotRole role) {
		this.role = role;
	}
	
}
