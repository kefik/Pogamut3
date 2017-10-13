package cz.cuni.amis.pogamut.base.communication.worldview.stubs;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;

public abstract class AbstractEventStub extends AbstractEntityStub implements IWorldChangeEvent, IWorldEvent {

	@Override
	public AbstractEventStub clone() {
		return (AbstractEventStub) super.clone();
	}
	
	@Override
	public long getSimTime() {
		return 0;
	}
	
}
