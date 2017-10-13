package cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageField;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageType;

/**
 * Runner has reached the safe are before the seeker thus making itself safe.
 * @author Jimmy
 */
@ControlMessageType(type="HS_BOT_SAFE")
public class HSRunnerSafe extends HSMessage {
	
	@ControlMessageField(index=1)
	private UnrealId botId;
	
	public HSRunnerSafe() {
	}

	public UnrealId getBotId() {
		return botId;
	}

	public void setBotId(UnrealId botId) {
		this.botId = botId;
	}
	
	@Override
	public String toString() {
		return "HSRunnerSafe[botId=" + (botId == null ? "null" : botId.getStringId()) + "]";
	}


}
