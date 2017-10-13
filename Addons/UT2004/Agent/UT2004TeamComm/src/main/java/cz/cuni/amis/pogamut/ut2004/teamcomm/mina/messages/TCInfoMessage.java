package cz.cuni.amis.pogamut.ut2004.teamcomm.mina.messages;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.teamcomm.server.UT2004TCServer;

public class TCInfoMessage extends TCMessage {

	/**
	 * Auto-generated.
	 */
	private static final long serialVersionUID = 1528667480137897787L;

	public TCInfoMessage(UnrealId serverId, TCInfoData info) {
		super(serverId, TCRecipient.TC_INFO, false, info.getMessageType(), info, info.getSimTime());
	}

}
