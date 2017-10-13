package cz.cuni.amis.pogamut.base3d.agent.jmx;

import cz.cuni.amis.pogamut.base.agent.jmx.*;
import cz.cuni.amis.pogamut.base.agent.jmx.adapter.IAgentMBeanAdapter;
import cz.cuni.amis.pogamut.base3d.agent.IAgent3D;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;

/**
 *
 * @author ik
 */
public interface Agent3DMBeanAdapterMBean extends IAgentMBeanAdapter {

    public Location getLocation();

    public Rotation getRotation();

    public Velocity getVelocity();
}
