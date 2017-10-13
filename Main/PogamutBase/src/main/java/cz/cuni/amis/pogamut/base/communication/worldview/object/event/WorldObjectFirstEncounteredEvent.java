package cz.cuni.amis.pogamut.base.communication.worldview.object.event;

import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;

/**
 * This event is raised by WorldView whenever new object appears in the worldview. E.g. the agent sees it
 * for the first time.
 *
 * @author Jimmy
 *
 * @param <T>
 */
public class WorldObjectFirstEncounteredEvent<T extends IWorldObject> extends WorldObjectEvent<T> {

    public WorldObjectFirstEncounteredEvent(T appearedObject, long simTime) {
        super(appearedObject, simTime);
    }
}
