package cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.messages;

import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.messages.TCInfoData;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.model.TCChannel;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;

public class TCInfoTeamChannelCreated extends TCInfoData {
	
	/**
	 * Auto-generated.
	 */
	private static final long serialVersionUID = -8179443685424761127L;

	public static final IToken MESSAGE_TYPE = Tokens.get("TCInfoTeamChannelCreated");
	
	private TCChannel channel;
	
	public TCInfoTeamChannelCreated(long requestId, long simTime) {
		super(requestId, MESSAGE_TYPE, simTime);
	}

	public TCChannel getChannel() {
		return channel;
	}

	public void setChannel(TCChannel channel) {
		this.channel = channel;
	}
	
	@Override
	public String toString() {
		return "TCInfoTeamChannelCreated[channel=" + channel + "]";
	}

}
