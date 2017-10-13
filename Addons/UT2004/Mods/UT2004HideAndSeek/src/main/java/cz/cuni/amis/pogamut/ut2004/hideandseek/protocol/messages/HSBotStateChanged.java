package cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageField;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageType;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.HSBotState;

@ControlMessageType(type="HS_BOT_STATE_CHANGED")
public class HSBotStateChanged extends HSMessage {

	@ControlMessageField(index=1)
	private UnrealId botId;
	
	@ControlMessageField(index=2)
	private String newState;
	
	public HSBotStateChanged() {
	}

	public UnrealId getBotId() {
		return botId;
	}

	public void setBotId(UnrealId botId) {
		this.botId = botId;
	}

	public String getNewState() {
		return newState;
	}

	public void setNewState(HSBotState newState) {
		this.newState = newState == null ? null : newState.name();
	}
	
	public HSBotState getNewStateEnum() {
		if (newState == null) return null;
		return HSBotState.valueOf(newState);
	}
	
	@Override
	public String toString() {
		return "HSBotStateChanged[botId=" + (botId == null ? "null" : botId.getStringId() + ", newState=" + newState + "]");
	}
	
}
