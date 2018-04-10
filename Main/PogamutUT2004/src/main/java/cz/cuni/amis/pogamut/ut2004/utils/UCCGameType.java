package cz.cuni.amis.pogamut.ut2004.utils;

public enum UCCGameType {
	
	DEATHMATCH("GameBots2004.BotDeathMatch"),
	TEAM_DEATCHMATCH("GameBots2004.BotTeamGame"),
	TEAM_CTF("GameBots2004.BotCTFGame"),
	TEAM_DOMINATION("GameBots2004.BotDoubleDomination");

	public final String gameType;

	private UCCGameType(String gameType) {
		this.gameType = gameType;		
	}
	
}
