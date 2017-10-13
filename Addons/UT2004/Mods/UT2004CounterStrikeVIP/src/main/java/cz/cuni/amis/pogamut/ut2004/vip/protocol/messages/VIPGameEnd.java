package cz.cuni.amis.pogamut.ut2004.vip.protocol.messages;

import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageType;

@ControlMessageType(type="VIP_GAME_END")
public class VIPGameEnd extends CSMessage {
	
	public VIPGameEnd() {		
	}
	
	@Override
	public String toString() {
		return "VIPGameEnd[]";
	}

}
