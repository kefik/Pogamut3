package cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.messages;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.messages.TCInfoData;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;

public class TCInfoBotJoined extends TCInfoData {
	
	/**
	 * Auto-generated.
	 */
	private static final long serialVersionUID = -1637597443658215022L;

	public static final IToken MESSAGE_TYPE = Tokens.get("TCInfoBotJoined");
	
	private UnrealId botId;
	
	private int team;
	
	public TCInfoBotJoined(long requestId, long simTime) {
		super(requestId, MESSAGE_TYPE, simTime);
	}

	public UnrealId getBotId() {
		return botId;
	}

	public void setBotId(UnrealId botId) {
		this.botId = botId;
	}

	public int getTeam() {
		return team;
	}

	public void setTeam(int team) {
		this.team = team;
	}
	
	@Override
	public String toString() {
		return "TCInfoBotJoined[botId=" + (botId == null ? "NULL" : botId.getStringId()) + ", team=" + team + "]";
	}

}
