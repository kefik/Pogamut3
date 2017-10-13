package cz.cuni.amis.pogamut.ut2004.tag.protocol.messages;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageField;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageType;

@ControlMessageType(type="TAG_PLAYER_IMMUNITY")
public class TagPlayerImmunity extends TagMessage {

	@ControlMessageField(index=1)
	private UnrealId botId;
	
	@ControlMessageField(index=2)
	private UnrealId immuneFromBotId;
	
	@ControlMessageField(index=1)
	private Boolean status;
	
	public TagPlayerImmunity() {
	}

	public UnrealId getBotId() {
		return botId;
	}

	public void setBotId(UnrealId botId) {
		this.botId = botId;
	}

	public UnrealId getImmuneFromBotId() {
		return immuneFromBotId;
	}

	public void setImmuneFromBotId(UnrealId immuneFromBotId) {
		this.immuneFromBotId = immuneFromBotId;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}	
			
}
