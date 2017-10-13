package cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events;

import cz.cuni.amis.pogamut.base.communication.translator.event.WorldEventIdentityWrapper;

public class ReadyCommandRequest extends TranslatorEvent {

    public ReadyCommandRequest(long simTime) {
		super(simTime);
	}

	@Override
    public String toString() {
        return "ReadyCommandRequest('connection successful, GameBots2004 are waiting for the READY command to begin handshake')";
    }
}