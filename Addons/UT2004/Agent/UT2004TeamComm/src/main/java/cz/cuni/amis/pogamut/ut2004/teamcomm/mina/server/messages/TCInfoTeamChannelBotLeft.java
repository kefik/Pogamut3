package cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.messages;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.messages.TCInfoData;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;

public class TCInfoTeamChannelBotLeft extends TCInfoData {

	/**
	 * Auto-generated.
	 */
	private static final long serialVersionUID = 2825576951393302945L;

	public static final IToken MESSAGE_TYPE = Tokens.get("TCInfoTeamChannelBotLeft");
	
	private UnrealId botId;
	
	private int channelId;
	
	private int team;
	
	public TCInfoTeamChannelBotLeft(long requestId, long simTime) {
		super(requestId, MESSAGE_TYPE, simTime);
	}

	public UnrealId getBotId() {
		return botId;
	}

	public void setBotId(UnrealId botId) {
		this.botId = botId;
	}

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	public int getTeam() {
		return team;
	}

	public void setTeam(int team) {
		this.team = team;
	}
	
	@Override
	public String toString() {
		return "TCInfoTeamChannelBotLeft[team=" + team + ", channelId=" + channelId + ", botId=" + (botId == null ? "NULL" : botId.getStringId()) + "]";
	}

}
