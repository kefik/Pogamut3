package cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.messages;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.client.TCMinaClient.RequestFuture;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.messages.TCRequestMessage;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Wrapper for {@link TCInfoRequestFailed} in order to be usable with {@link RequestFuture#computationException(Exception)}.
 * @author Jimmy
 */
public class TCInfoRequestFailedException extends PogamutException {
	
	/**
	 * Auto-generated.
	 */
	private static final long serialVersionUID = 3626755789607062972L;
	
	private TCInfoRequestFailed failureDetails;
	private TCRequestMessage request;
	
	public TCInfoRequestFailedException(TCRequestMessage request, TCInfoRequestFailed failureDetails, Logger logger, Object origin) {
		super("Failed to process request " + String.valueOf(request) + ", error " + (failureDetails == null ? "NULL" : failureDetails.getFailureType()) + ", reason: " + (failureDetails == null ? "NULL" : failureDetails.getReason()), logger, origin);
		this.request = request;
		this.failureDetails = failureDetails;
	}

	public TCInfoRequestFailed getFailureDetails() {
		return failureDetails;
	}

	public void setFailureDetails(TCInfoRequestFailed failureDetails) {
		this.failureDetails = failureDetails;
	}

	public TCRequestMessage getRequest() {
		return request;
	}

	public void setRequest(TCRequestMessage request) {
		this.request = request;
	}

}
