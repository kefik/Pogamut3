package cz.cuni.amis.pogamut.base.communication.worldview.react;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;

public abstract class ObjectReact<OBJECT extends IWorldObject> extends ObjectEventReact<OBJECT, IWorldObjectEvent<OBJECT>> {

	public ObjectReact(Class<?> objectClass, IWorldView worldView) {
		super(objectClass, worldView);
	}
	

	public ObjectReact(Class<?> objectClass, Class<?> eventClass, IWorldView worldView) {
		super(objectClass, eventClass, worldView);
	}

	public ObjectReact(WorldObjectId objectId, IWorldView worldView) {
		super(objectId, worldView);
	}

	public ObjectReact(WorldObjectId objectId, Class<?> eventClass, IWorldView worldView) {
		super(objectId, eventClass, worldView);
	}
	
}
