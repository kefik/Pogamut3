package cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events;

import cz.cuni.amis.pogamut.base.communication.translator.event.WorldEventIdentityWrapper;

public class TranslatorEvent extends WorldEventIdentityWrapper {
	
	private long simTime;

	public TranslatorEvent(long simTime) {
		this.simTime = simTime;
	}

	@Override
	public long getSimTime() {
		return simTime;
	}
	
}
