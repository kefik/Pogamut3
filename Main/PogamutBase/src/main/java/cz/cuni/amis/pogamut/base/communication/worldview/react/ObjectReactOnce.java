package cz.cuni.amis.pogamut.base.communication.worldview.react;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;

public abstract class ObjectReactOnce<OBJECT extends IWorldObject> extends ObjectEventReactOnce<OBJECT, IWorldObjectEvent<OBJECT>> {

	public ObjectReactOnce(Class<?> objectClass, IWorldView worldView) {
		super(objectClass, worldView);
	}
	

	public ObjectReactOnce(Class<?> objectClass, Class<?> eventClass, IWorldView worldView) {
		super(objectClass, eventClass, worldView);
	}

	public ObjectReactOnce(WorldObjectId objectId, IWorldView worldView) {
		super(objectId, worldView);
	}

	public ObjectReactOnce(WorldObjectId objectId, Class<?> eventClass, IWorldView worldView) {
		super(objectId, eventClass, worldView);
	}
	
}
