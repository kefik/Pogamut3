package cz.cuni.amis.pogamut.ut2004.vip.protocol.messages;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageField;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageType;

/**
 * Which location works as SAFE area for the VIP this round.
 * 
 * Announced ONLY to COUNTER-TERRORISTs (including VIP of course).
 * 
 * @author Jimmy
 */
@ControlMessageType(type="CS_SET_VIP_SAFE_AREA")
public class CSSetVIPSafeArea extends CSMessage {

	@ControlMessageField(index=1)
	private Location safeArea;
	
	public CSSetVIPSafeArea() {
	}

	public Location getSafeArea() {
		return safeArea;
	}

	public void setSafeArea(Location safeArea) {
		this.safeArea = safeArea;
	}

	@Override
	public String toString() {
		return "CSSetVIPSafeArea[safeArea=" + (safeArea == null ? "null" : safeArea.toString()) + "]";
	}
	
}
