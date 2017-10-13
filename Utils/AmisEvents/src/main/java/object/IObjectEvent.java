package object;

import cz.cuni.amis.events.event.IEvent;

public interface IObjectEvent<OBJECT extends IObject> extends IEvent {
	
	public OBJECT getObject();
}
