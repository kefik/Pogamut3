package cz.cuni.amis.pogamut.ut2004.teamcomm.mina.client.messages;

import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.messages.TCRequestData;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;

public class TCRequestGetStatus extends TCRequestData {

	/**
	 * Auto-generated.
	 */
	private static final long serialVersionUID = 7332315275460923524L;
	
	public static final IToken MESSAGE_TYPE = Tokens.get("TCRequestGetStatus");

	public TCRequestGetStatus(long simTime) {
		super(MESSAGE_TYPE, simTime);
	}

	@Override
	public String toString() {
		return "TCRequestGetStatus";
	}
	
}
