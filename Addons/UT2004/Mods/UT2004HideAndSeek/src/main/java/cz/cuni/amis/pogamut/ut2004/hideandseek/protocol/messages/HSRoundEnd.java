package cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages;

import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageType;

/**
 * Announcing that the round has ended.
 * <p><p>
 * All RUNNERS that were neither FOULED nor CAPTURED are counted as "SURVIVED".
 * <p><p>
 * Sent after last {@link HSRoundState} update is broadcast to mark the round end allowing clients to resolve the round.
 * 
 * @author Jimmy
 */
@ControlMessageType(type="HS_ROUND_END")
public class HSRoundEnd extends HSMessage {
	
	public HSRoundEnd() {
	}

	@Override
	public String toString() {
		return "HSRoundEnd[]";
	}
}
