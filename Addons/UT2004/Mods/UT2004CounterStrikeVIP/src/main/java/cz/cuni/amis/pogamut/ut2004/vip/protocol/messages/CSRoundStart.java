package cz.cuni.amis.pogamut.ut2004.vip.protocol.messages;

import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageType;

/**
 * Announcing new round is about to start.
 * <p><p>
 * Sent before new {@link CSRoundState} is sent, allows clients to clean-up their data structures for new round.
 * 
 * @author Jimmy
 *
 */
@ControlMessageType(type="CS_ROUND_START")
public class CSRoundStart extends CSMessage {
	
	public CSRoundStart() {
	}
	
	@Override
	public String toString() {
		return "CSRoundStart[]";
	}


}
