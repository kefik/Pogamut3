package cz.cuni.amis.pogamut.ut2004.vip.protocol.messages;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageField;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageType;

/**
 * VIP has reached the safe alive.
 * @author Jimmy
 */
/**
 * @author Jimmy
 *
 */
@ControlMessageType(type="CS_VIP_SAFE")
public class CSVIPSafe extends CSMessage {
	
	@ControlMessageField(index=1)
	private UnrealId vipId;
	
	public CSVIPSafe() {
	}

	public UnrealId getVipId() {
		return vipId;
	}

	public void setVipId(UnrealId vipId) {
		this.vipId = vipId;
	}

	@Override
	public String toString() {
		return "HSRunnerSafe[botId=" + (vipId == null ? "null" : vipId.getStringId()) + "]";
	}


}
