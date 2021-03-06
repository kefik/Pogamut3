package cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageField;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageType;

/**
 * Runner has survived the round without being captured or reaching safe-area.
 * 
 * @author Jimmy
 */
@ControlMessageType(type="HS_RUNNER_SURVIVED")
public class HSRunnerSurvived extends HSMessage {
	
	@ControlMessageField(index=1)
	private UnrealId botId;
	
	public HSRunnerSurvived() {
	}

	public UnrealId getBotId() {
		return botId;
	}

	public void setBotId(UnrealId botId) {
		this.botId = botId;
	}
	
	@Override
	public String toString() {
		return "HSRunnerSurvived[botId=" + (botId == null ? "null" : botId.getStringId()) + "]";
	}

	

}
