package cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageField;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageType;

/**
 * Seeker has dwelled within restricted area for too long and was fouled out from the play.
 * @author Jimmy
 */
@ControlMessageType(type="HS_SEEKER_FOULED")
public class HSSeekerFouled extends HSMessage {
	
	@ControlMessageField(index=1)
	private UnrealId botId;
	
	public HSSeekerFouled() {
	}

	public UnrealId getBotId() {
		return botId;
	}

	public void setBotId(UnrealId botId) {
		this.botId = botId;
	}
	
	@Override
	public String toString() {
		return "HSSeekerFouled[botId=" + (botId == null ? "null" : botId.getStringId()) + "]";
	}


}
