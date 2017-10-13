package cz.cuni.amis.pogamut.multi.communication.messages;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;

/**
 * This event is sent by SharedWorldView to waiting LocalWorldViews after a batch has been fully
 * processed by the shared WV. After this event is recieved, the shared and static data can be considered consistent for the
 * time carried by the event.
 * @author srlok
 *
 */
public class SharedBatchFinishedEvent implements IWorldChangeEvent {
	
	private long time;
	
	public SharedBatchFinishedEvent( long time )
	{
		this.time = time;
	}

	@Override
	public long getSimTime() {
		return time;
	}
	
	@Override
	public String toString() {
		return "SharedBatchFinishedEvent[time=" + getSimTime() + "]";
	}
	
}
