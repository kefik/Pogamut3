package cz.cuni.amis.pogamut.ut2004.teamcomm.mina.messages;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;

public class TCRequestData extends TCMessageData implements Serializable {
	
	/**
	 * Auto-generated.
	 */
	private static final long serialVersionUID = 3030116690874642115L;
	
	private static final AtomicLong REQUEST_COUNTER = new AtomicLong(0);

	private long requestId;

	public TCRequestData(IToken messageType, long simTime) {
		super(messageType, simTime);
		this.requestId = REQUEST_COUNTER.incrementAndGet();
	}
	
	public long getRequestId() {
		return requestId;
	}
	
}
