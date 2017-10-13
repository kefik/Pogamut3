package cz.cuni.amis.pogamut.ut2004.teamcomm.server;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;

public class BotTCRecord<PLAYER_CONTAINER> {
	
	private UnrealId botId;
	
	private PLAYER_CONTAINER player;
		
	public BotTCRecord(UnrealId botId) {
		this.botId = botId;
	}
	
	public BotTCRecord(UnrealId botId, PLAYER_CONTAINER player) {
		this.botId = botId;
		this.player = player;
	}

	public PLAYER_CONTAINER getPlayer() {
		return player;
	}

	public void setPlayer(PLAYER_CONTAINER player) {
		this.player = player;
	}

	public UnrealId getBotId() {
		return botId;
	}
	
}
