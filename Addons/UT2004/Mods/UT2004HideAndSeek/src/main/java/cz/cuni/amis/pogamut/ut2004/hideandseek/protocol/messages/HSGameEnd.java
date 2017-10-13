package cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages;

import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageType;

@ControlMessageType(type="HS_GAME_END")
public class HSGameEnd extends HSMessage {
	
	public HSGameEnd() {		
	}
	
	@Override
	public String toString() {
		return "HSGameEnd[]";
	}

}
