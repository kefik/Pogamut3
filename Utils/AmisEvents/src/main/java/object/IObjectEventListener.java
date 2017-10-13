package object;

import cz.cuni.amis.events.event.IEventListener;

public interface IObjectEventListener<OBJECT extends IObject> extends IEventListener<IObjectEvent<OBJECT>>{	
}
