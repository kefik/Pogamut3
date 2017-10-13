package cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageField;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageType;

/**
 * The bot {@link HSAssignSeeker#getBotId()} has been assigned as seeker for this round.
 * 
 * @author Jimmy
 */
@ControlMessageType(type="HS_ASSIGN_SEEKER")
public class HSAssignSeeker extends HSMessage {

	@ControlMessageField(index=1)
	private UnrealId botId;
	
	public HSAssignSeeker() {
	}

	public UnrealId getBotId() {
		return botId;
	}

	public void setBotId(UnrealId botId) {
		this.botId = botId;
	}
	
	@Override
	public String toString() {
		return "HSAssignSeeker[botId=" + (botId == null ? "null" : botId.getStringId()) + "]";
	}
	
}
