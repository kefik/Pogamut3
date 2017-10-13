package cz.cuni.amis.pogamut.ut2004.vip.protocol.messages;

import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageType;

/**
 * Announcing that the round has ended.
 * <p><p>
 * VIP killed / VIP escaped / all terrorists dead.
 * <p><p>
 * Sent after last {@link CSRoundState} update is broadcast to mark the round end allowing clients to resolve the round.
 * 
 * @author Jimmy
 */
@ControlMessageType(type="CS_ROUND_END")
public class CSRoundEnd extends CSMessage {
	
	public CSRoundEnd() {
	}

	@Override
	public String toString() {
		return "CSRoundEnd[]";
	}
}
