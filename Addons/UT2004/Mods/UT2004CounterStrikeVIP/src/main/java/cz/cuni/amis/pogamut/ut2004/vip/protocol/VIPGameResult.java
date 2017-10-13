package cz.cuni.amis.pogamut.ut2004.vip.protocol;

public enum VIPGameResult {

	COUNTER_TERRORISTS_WIN(CSBotTeam.COUNTER_TERRORIST),
	TERRORISTS_WIN(CSBotTeam.TERRORIST),
	DRAW(null);
	
	public final CSBotTeam team;
	
	private VIPGameResult(CSBotTeam team) {
		this.team = team;
	}
	
}
