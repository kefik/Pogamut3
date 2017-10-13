package cz.cuni.amis.pogamut.ut2004.vip.protocol.messages;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageField;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageType;

/**
 * VIP has been killed.
 * @author Jimmy
 */
@ControlMessageType(type="CS_VIP_KILLED")
public class CSVIPKilled extends CSMessage {

	@ControlMessageField(index=1)
	private UnrealId vipId;
	
	@ControlMessageField(index=2)
	private UnrealId killerId;
	
	public CSVIPKilled() {
	}

	public UnrealId getVipId() {
		return vipId;
	}

	public void setVipId(UnrealId vipId) {
		this.vipId = vipId;
	}

	public UnrealId getKillerId() {
		return killerId;
	}

	public void setKillerId(UnrealId killerId) {
		this.killerId = killerId;
	}

	@Override
	public String toString() {
		return "CSVIPKilled[vipId=" + (vipId == null ? "null" : vipId.getStringId()) + ",killerId=" + (killerId == null ? "null" : killerId.getStringId()) + "]";
	}
	
}
