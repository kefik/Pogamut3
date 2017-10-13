package cz.cuni.amis.pogamut.multi.communication.worldview.object.event;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;

/**
 * Class for buffering raised events in local worldViews
 * @author srlok
 *
 */
public class DummyObjectEvent {
	
	public DummyObjectEvent( WorldObjectId objectId, EventType eventType, long eventTime)
	{
		this.eventType = eventType;
		this.objectId = objectId;
		this.time = eventTime;
	}
	
	/**
	 * Enumerates all common object event types
	 * @author srlok
	 *
	 */
	public enum EventType
	{
		APPEARED,
		DESTROYED,
		DISAPPEARED,
		FIRST_ENCOUNTERED,
		UPDATED
	}
	
	private WorldObjectId objectId = null;
	private EventType eventType = null;
	private long time = 0;
	
	public WorldObjectId getObjectId()
	{
		return this.objectId;
	}
	
	public EventType getType()
	{
		return this.eventType;
	}
	
	public void incTime()
	{
		this.time++;
	}

	public long getTime()
	{
		return this.time;
	}
}

