package cz.cuni.amis.pogamut.base3d.agent;

import cz.cuni.amis.pogamut.base.agent.IEmbodiedAgent;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocomotive;
import cz.cuni.amis.pogamut.base3d.worldview.object.IRotable;

/**
 * Interface of agent embodied in a 3D environment.
 * @author ik
 */
public interface IAgent3D extends IEmbodiedAgent, ILocated, ILocomotive, IRotable {

}
