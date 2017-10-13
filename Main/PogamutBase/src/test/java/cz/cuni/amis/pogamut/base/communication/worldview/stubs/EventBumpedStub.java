package cz.cuni.amis.pogamut.base.communication.worldview.stubs;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;

public class EventBumpedStub extends AbstractEventStub {

	private int location;

	public EventBumpedStub(int location) {
		this.location = location;
	}

	@Override
	public EventBumpedStub clone() {
		return (EventBumpedStub) super.clone();
	}

	public int getLocation() {
		return location;
	}

}
