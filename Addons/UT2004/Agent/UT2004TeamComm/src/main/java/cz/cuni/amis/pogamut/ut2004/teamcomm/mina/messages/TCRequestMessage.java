package cz.cuni.amis.pogamut.ut2004.teamcomm.mina.messages;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;

public class TCRequestMessage extends TCMessage {

	/**
	 * Auto-generated.
	 */
	private static final long serialVersionUID = -8649552145716268590L;

	public TCRequestMessage(UnrealId source, TCRequestData request) {
		super(source, TCRecipient.TC_REQUEST, false, request.getMessageType(), request, request.getSimTime());
	}

}
