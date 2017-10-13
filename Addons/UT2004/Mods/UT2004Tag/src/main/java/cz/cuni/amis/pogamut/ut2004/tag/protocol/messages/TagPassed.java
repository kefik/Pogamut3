package cz.cuni.amis.pogamut.ut2004.tag.protocol.messages;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageField;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageType;

@ControlMessageType(type="TAG_PASSED")
public class TagPassed extends TagMessage {
	
	@ControlMessageField(index=1)
	private UnrealId fromBotId;
	
	@ControlMessageField(index=2)
	private UnrealId toBotId;
	
	public TagPassed() {		
	}

	/**
	 * WHO has passed the tagged (this bot has a tag, but touched another bot)
	 * <p><p>
	 * 
	 * May be null, in that case it is the SERVER who is assigning the tag.
	 * 
	 * @return
	 */
	public UnrealId getFromBotId() {
		return fromBotId;
	}

	public void setFromBotId(UnrealId fromBotId) {
		this.fromBotId = fromBotId;
	}

	/**
	 * WHO has been (received) the tag (this bot now switchis to IS-TAGGED state)
	 * <p><p>
	 * 
	 * May be null, in that case no new bot gets tagged (SERVER removed tag from {@link #getFromBotId()} bot).
	 * 
	 * @return
	 */
	public UnrealId getToBotId() {
		return toBotId;
	}

	public void setToBotId(UnrealId toBotId) {
		this.toBotId = toBotId;
	}
	
}
