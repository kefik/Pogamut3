package cz.cuni.amis.pogamut.ut2004.communication.messages;

import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocomotive;
import cz.cuni.amis.pogamut.base3d.worldview.object.IRotable;

/**
 * Interface unifying Self and Player messages.
 * @author ik
 */
public interface IPerson extends IWorldObject, IRotable, ILocomotive, ILocated {

    String getName();
}
