package cz.cuni.amis.pogamut.base3d.worldview.object.event;

import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectEvent;
import cz.cuni.amis.pogamut.base3d.worldview.object.IViewable;

/**
 *  * This event is raised when the object leaves field of view. WorldObjectUpdatedEvent will be also fired.
 * @author ik
 */
public class WorldObjectDisappearedEvent<T extends IViewable> extends WorldObjectEvent<T> {

    public WorldObjectDisappearedEvent(T obj, long simTime) {
        super(obj, simTime);
    }
}
