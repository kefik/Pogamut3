package cz.cuni.amis.pogamut.ut2004.vip.protocol.messages;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageField;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageType;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.CSBotState;

@ControlMessageType(type="CS_BOT_STATE_CHANGED")
public class CSBotStateChanged extends CSMessage {

	@ControlMessageField(index=1)
	private UnrealId botId;
	
	@ControlMessageField(index=2)
	private String newState;
	
	public CSBotStateChanged() {
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

	public void setNewState(CSBotState newState) {
		this.newState = newState == null ? null : newState.name();
	}
	
	public CSBotState getNewStateEnum() {
		if (newState == null) return null;
		return CSBotState.valueOf(newState);
	}
	
	@Override
	public String toString() {
		return "CSBotStateChanged[botId=" + (botId == null ? "null" : botId.getStringId() + ", newState=" + newState + "]");
	}
	
}
