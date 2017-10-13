package cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages;

import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageType;

/**
 * Announcing new round is about to start.
 * <p><p>
 * Sent before new {@link HSRoundState} is sent, allows clients to clean-up their data structures for new round.
 * 
 * @author Jimmy
 *
 */
@ControlMessageType(type="HS_ROUND_START")
public class HSRoundStart extends HSMessage {
	
	public HSRoundStart() {
	}
	
	@Override
	public String toString() {
		return "HSRoundStart[]";
	}


}
