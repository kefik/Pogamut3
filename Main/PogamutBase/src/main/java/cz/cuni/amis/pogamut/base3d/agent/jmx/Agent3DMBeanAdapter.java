package cz.cuni.amis.pogamut.base3d.agent.jmx;

import cz.cuni.amis.pogamut.base.agent.jmx.*;
import cz.cuni.amis.pogamut.base.agent.jmx.adapter.AgentMBeanAdapter;
import cz.cuni.amis.pogamut.base3d.agent.IAgent3D;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

/**
 *
 * @author ik
 */
public class Agent3DMBeanAdapter<T extends IAgent3D> extends AgentMBeanAdapter<T> implements Agent3DMBeanAdapterMBean {

    public Agent3DMBeanAdapter(T agent, ObjectName objectName, MBeanServer mbs) throws MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
        super(agent, objectName, mbs);
    }

    @Override
    public Location getLocation() {
        return getAgent().getLocation();
    }

    @Override
    public Rotation getRotation() {
        return getAgent().getRotation();
    }

    @Override
    public Velocity getVelocity() {
        return getAgent().getVelocity();
    }

}
