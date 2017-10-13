package cz.cuni.amis.pogamut.ut2004.teamcomm.mina.messages;

import java.io.Serializable;

import cz.cuni.amis.utils.token.IToken;

public class TCInfoData extends TCMessageData implements Serializable {

	/**
	 * Auto-generated.
	 */
	private static final long serialVersionUID = 8887031521002499499L;
	
	private long requestId;

	public TCInfoData(long requestId, IToken messageType, long simTime) {
		super(messageType, simTime);
		this.requestId = requestId;
	}
	
	public long getRequestId() {
		return requestId;
	}
	
	@Override
	public String toString() {
		return "TCInfoData[requestId=" + requestId + "]";
	}
	
}
