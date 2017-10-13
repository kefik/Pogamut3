package cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.messages;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.messages.TCInfoData;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;

public class TCInfoBotLeft extends TCInfoData {

	/**
	 * Auto-generated.
	 */
	private static final long serialVersionUID = -4350697544161725191L;

	public static final IToken MESSAGE_TYPE = Tokens.get("TCInfoBotLeft");
	
	private UnrealId botId;
	
	private int team;
	
	public TCInfoBotLeft(long requestId, long simTime) {
		super(requestId, MESSAGE_TYPE, simTime);
	}

	public UnrealId getBotId() {
		return botId;
	}

	/**
	 * Who has left the TC server.
	 * 
	 * May be 'null' in case that it is uknown (due to internal TCMinaServer error ... should not happen at all...).
	 * 
	 * @param botId
	 */
	public void setBotId(UnrealId botId) {
		this.botId = botId;
	}

	/**
	 * May be 'negative' in case that the team is unknown (due to internal TCMinaServer error ... should not happen at all...).
	 * @return
	 */
	public int getTeam() {
		return team;
	}

	public void setTeam(int team) {
		this.team = team;
	}
	
	@Override
	public String toString() {
		return "TCInfoBotLeft[botId=" + (botId == null ? "NULL" : botId.getStringId()) + ", team=" + team + "]";
	}

}
