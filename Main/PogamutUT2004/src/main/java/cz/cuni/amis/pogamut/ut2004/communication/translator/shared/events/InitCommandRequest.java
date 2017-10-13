package cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events;

import cz.cuni.amis.pogamut.base.communication.translator.event.WorldEventIdentityWrapper;

public class InitCommandRequest extends TranslatorEvent {

    public InitCommandRequest(long simTime) {
		super(simTime);
	}

	@Override
	public String toString() {
		return "InitCommandRequest[GameBots2004 are waiting for the agent to send INIT command to spawn the bot inside UT2004]";
	}
	
}
