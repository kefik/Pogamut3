/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.pogamut.base3d.worldview.object.event;

import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectEvent;
import cz.cuni.amis.pogamut.base3d.worldview.object.IViewable;

/**
 * This event is raised when the object becomes visible. WorldObjectUpdated event will be also fired.
 * @author ik
 */
public class WorldObjectAppearedEvent<T extends IViewable> extends WorldObjectEvent<T> {

    public WorldObjectAppearedEvent(T o, long simTime) {
        super(o, simTime);
    }
}
