package cz.cuni.amis.pogamut.base.communication.worldview.object.event;

import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.utils.NullCheck;

/**
 * Default world object event implementation wrapping some object inside the event.
 * @author ik
 */
public class WorldObjectEvent<T extends IWorldObject> implements IWorldObjectEvent<T> {

	private T object;
	private long simTime;

	public WorldObjectEvent(T object, long simTime) {
		NullCheck.check(object, "object");
		this.object = object;
		this.simTime = simTime;
	}

	@Override
	public WorldObjectId getId() {
		if (object == null) return null;
		return object.getId();
	}

	@Override
	public T getObject() {
		return object;
	}
	
	@Override
	public long getSimTime() {
		return simTime;
	}
	

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[id = " + (getId() == null ? "null" : getId().getStringId()) + ", object = " + getObject() + ", time=" + getSimTime() + "]";
	}
	
}
