package cz.cuni.amis.pogamut.multi.worldview.events;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.utils.flag.FlagInteger;
import cz.cuni.amis.utils.flag.ImmutableFlag;

public class BatchBeginEventStub implements IWorldChangeEvent{

	private static FlagInteger instances = new FlagInteger(0);
	
	public static ImmutableFlag<Integer> getInstances() {
		return instances.getImmutable();
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		instances.decrement(1);
	}
	
	private long time;
	
	public BatchBeginEventStub( long time)
	{
		instances.increment(1);
		this.time = time;
	}
	
	@Override
	public long getSimTime() {
		return time;
	}

	@Override
	public String toString() {
		return "BatchBeginEventStub[time=" + getSimTime() + "]";
	}
	
}
