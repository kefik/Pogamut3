package cz.cuni.amis.pogamut.base.agent.module.comm;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;

public class CommEvent implements IWorldChangeEvent, IWorldEvent {

	private long simTime;

	public CommEvent() {
		this.simTime = 0;
	}
	
	public CommEvent(long simTime) {
		this.simTime = simTime;
	}
	
	@Override
	public long getSimTime() {
		return simTime;
	}

}
