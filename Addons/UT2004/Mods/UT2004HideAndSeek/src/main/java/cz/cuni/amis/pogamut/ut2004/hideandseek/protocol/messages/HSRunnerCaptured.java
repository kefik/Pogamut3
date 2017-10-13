package cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageField;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageType;

/**
 * Runner has been captured by the seeker.
 * @author Jimmy
 */
@ControlMessageType(type="HS_RUNNER_CAPTURED")
public class HSRunnerCaptured extends HSMessage {

	@ControlMessageField(index=1)
	private UnrealId botId;
	
	public HSRunnerCaptured() {
	}

	public UnrealId getBotId() {
		return botId;
	}

	public void setBotId(UnrealId botId) {
		this.botId = botId;
	}
	
	@Override
	public String toString() {
		return "HSRunnerCaptures[botId=" + (botId == null ? "null" : botId.getStringId()) + "]";
	}

	
}
