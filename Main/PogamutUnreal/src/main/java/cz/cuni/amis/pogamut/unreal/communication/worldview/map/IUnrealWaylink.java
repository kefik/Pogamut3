package cz.cuni.amis.pogamut.unreal.communication.worldview.map;

import java.io.Serializable;

/**
 *
 * @author ik
 */
public interface IUnrealWaylink extends Serializable {

    IUnrealWaypoint getEnd();

    String getEndId();

    int getFlags();

    IUnrealWaypoint getStart();
}
