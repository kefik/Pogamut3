package cz.cuni.amis.pogamut.base.communication.worldview.object.event;

import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;

/**
 * This event is raised by the WorldView whenever the object is removed from the world. E.g.
 * was destroyed / annihilated...
 * 
 * @author Jimmy
 */
public class WorldObjectDestroyedEvent<T extends IWorldObject> extends WorldObjectEvent<T> {

	public WorldObjectDestroyedEvent(T objectToBeDestroyed, long time) {
		super(objectToBeDestroyed, time);
	}

}
