package cz.cuni.amis.pogamut.ut2004.teamcomm.mina.client.messages;

import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.messages.TCRequestData;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;

public class TCRequestDestroyChannel extends TCRequestData {
	
	/**
	 * Auto-generated 
	 */
	private static final long serialVersionUID = -2147007736675605441L;
	
	public static final IToken MESSAGE_TYPE = Tokens.get("TCRequestDestroyChannel");
	
	private int channelId;
	
	public TCRequestDestroyChannel(long simTime) {
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
		return "TCRequestDestroyChannel[channelId=" + channelId + "]";
	}
	
}
