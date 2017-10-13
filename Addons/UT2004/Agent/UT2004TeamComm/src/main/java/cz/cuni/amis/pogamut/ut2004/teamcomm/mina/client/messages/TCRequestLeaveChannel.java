package cz.cuni.amis.pogamut.ut2004.teamcomm.mina.client.messages;

import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.messages.TCRequestData;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;

public class TCRequestLeaveChannel extends TCRequestData {
	
	/**
	 * Auto-generated.
	 */
	private static final long serialVersionUID = 2747943792761435817L;

	public static final IToken MESSAGE_TYPE = Tokens.get("TCRequestLeaveChannel");
	
	private int channelId;

	public TCRequestLeaveChannel(long simTime) {
		super(MESSAGE_TYPE, simTime);
	}

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}
	
	@Override
	public String toString() {
		return "TCRequestLeaveChannel[channelId=" + channelId + "]";
	}
	
}
