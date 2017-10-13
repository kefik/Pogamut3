package cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageField;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageType;

/**
 * Runner has been found within the restricted area at the beginning of the game and was fauled out from the play.
 * @author Jimmy
 */
@ControlMessageType(type="HS_RUNNER_FAULED")
public class HSRunnerFouled extends HSMessage {
	
	@ControlMessageField(index=1)
	private UnrealId botId;
	
	public HSRunnerFouled() {
	}

	public UnrealId getBotId() {
		return botId;
	}

	public void setBotId(UnrealId botId) {
		this.botId = botId;
	}
	
	@Override
	public String toString() {
		return "HSRunnerFouled[botId=" + (botId == null ? "null" : botId.getStringId()) + "]";
	}
	
}
