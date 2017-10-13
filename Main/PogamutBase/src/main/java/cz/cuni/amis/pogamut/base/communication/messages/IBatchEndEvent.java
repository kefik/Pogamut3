package cz.cuni.amis.pogamut.base.communication.messages;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;

public interface IBatchEndEvent extends IWorldChangeEvent{

	public static class BatchEndEventStub implements IBatchEndEvent
	{
		private long time;
		
		public BatchEndEventStub( long time )
		{
			this.time = time;
		}
		
		@Override
		public long getSimTime() {
			return time;
		}
		
	}
	
}
