package cz.cuni.amis.pogamut.ut2004.vip.protocol.messages;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageField;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageType;

/**
 * The bot {@link CSAssignVIP#getBotId()} has been assigned as VIP for this round.
 * 
 * @author Jimmy
 */
@ControlMessageType(type="CS_ASSIGN_VIP")
public class CSAssignVIP extends CSMessage {

	@ControlMessageField(index=1)
	private UnrealId botId;
	
	public CSAssignVIP() {
	}

	public UnrealId getBotId() {
		return botId;
	}

	public void setBotId(UnrealId botId) {
		this.botId = botId;
	}
	
	@Override
	public String toString() {
		return "CSAssignVIP[botId=" + (botId == null ? "null" : botId.getStringId()) + "]";
	}
	
}
