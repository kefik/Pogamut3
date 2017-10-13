package cz.cuni.amis.pogamut.base.communication.worldview.object.event;

import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;

/**
 * This event is raised by WorldView whenever the object is updated (possibly one of it's
 * fields has changed - warning it's not neccesery the object's field might be updated
 * to the same value).
 * 
 * @author Jimmy
 *
 * @param <T>
 */
public class WorldObjectUpdatedEvent<T extends IWorldObject> extends WorldObjectEvent<T>  {

	public WorldObjectUpdatedEvent(T updatedObject, long simTime) {
		super(updatedObject, simTime);
	}
	
}
