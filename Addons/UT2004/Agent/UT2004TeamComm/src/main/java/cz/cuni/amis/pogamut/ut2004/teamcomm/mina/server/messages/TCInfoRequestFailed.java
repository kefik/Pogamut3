package cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.messages;

import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.messages.TCInfoData;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.messages.TCMessageData;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.messages.TCRequestData;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;

public class TCInfoRequestFailed extends TCInfoData {

	/**
	 * Auto-generated. 
	 */
	private static final long serialVersionUID = -1216744829039351923L;
	
	public static final IToken MESSAGE_TYPE = Tokens.get("TCInfoRequestFailed"); 
	
	private long requestId;
	
	private String reason;
	
	private TCInfoRequestFailureType failureType;
	
	public TCInfoRequestFailed(long requestId, long simTime) {
		super(requestId, MESSAGE_TYPE, simTime);
	}
	
	public long getRequestId() {
		return requestId;
	}

	public void setRequestId(long requestId) {
		this.requestId = requestId;
	}
	
	public TCInfoRequestFailureType getFailureType() {
		return failureType;
	}

	public void setFailureType(TCInfoRequestFailureType failureType) {
		this.failureType = failureType;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	@Override
	public String toString() {
		return "TCInfoRequestFailed[requestId=" + requestId + ", failureType=" + failureType + ", reason=" + reason + "]";
	}

}
