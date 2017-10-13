package cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.messages;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.messages.TCInfoData;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;

public class TCInfoTeamChannelDestroyed extends TCInfoData {
	
	/**
	 * Auto-generated.
	 */
	private static final long serialVersionUID = -3520694361069320854L;

	public static final IToken MESSAGE_TYPE = Tokens.get("TCInfoTeamChannelDestroyed");
	
	/**
	 * Always the 'creator' of the team channel.
	 */
	private UnrealId destroyer;
	
	private int channelId;
	
	public TCInfoTeamChannelDestroyed(long requestId, long simTime) {
		super(requestId, MESSAGE_TYPE, simTime);
	}

	public UnrealId getDestroyer() {
		return destroyer;
	}

	public void setDestroyer(UnrealId destroyer) {
		this.destroyer = destroyer;
	}

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	@Override
	public String toString() {
		return "TCInfoTeamChannelDestroyed[channelId=" + channelId + ", destroyer=" + (destroyer == null ? "NULL" : destroyer.getStringId()) + "]";
	}

	

}
