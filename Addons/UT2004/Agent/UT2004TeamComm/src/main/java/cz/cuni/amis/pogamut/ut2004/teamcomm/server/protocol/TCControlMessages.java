package cz.cuni.amis.pogamut.ut2004.teamcomm.server.protocol;

import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessages;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ICustomControlMessage;
import cz.cuni.amis.pogamut.ut2004.teamcomm.server.protocol.messages.TCControlServerAlive;

public class TCControlMessages extends ControlMessages {
	
	@SuppressWarnings("unchecked")
	public static final Class<? extends ICustomControlMessage>[] messages = 
		new Class[] {
			TCControlServerAlive.class
		}
	;
	
	public TCControlMessages() {
		register(messages);
	}

}
