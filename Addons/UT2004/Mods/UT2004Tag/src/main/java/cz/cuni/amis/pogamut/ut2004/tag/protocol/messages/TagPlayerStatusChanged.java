package cz.cuni.amis.pogamut.ut2004.tag.protocol.messages;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageField;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageType;

@ControlMessageType(type="TAG_PLAYER_STATUS_CHANGED")
public class TagPlayerStatusChanged extends TagMessage {

	@ControlMessageField(index=1)
	private UnrealId botId;
	
	@ControlMessageField(index=1)
	private Boolean tagStatus;
	
	public TagPlayerStatusChanged() {
	}

	public UnrealId getBotId() {
		return botId;
	}

	public void setBotId(UnrealId botId) {
		this.botId = botId;
	}

	public Boolean getTagStatus() {
		return tagStatus;
	}

	public void setTagStatus(Boolean tagStatus) {
		this.tagStatus = tagStatus;
	}
	
}
