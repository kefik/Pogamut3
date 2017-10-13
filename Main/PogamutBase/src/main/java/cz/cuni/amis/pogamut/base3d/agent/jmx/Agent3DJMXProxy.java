package cz.cuni.amis.pogamut.base3d.agent.jmx;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.management.MalformedObjectNameException;

import cz.cuni.amis.pogamut.base.agent.jmx.proxy.GhostAgentJMXProxy;
import cz.cuni.amis.pogamut.base3d.agent.IAgent3D;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;

/**
 *
 * @author ik
 */
public class Agent3DJMXProxy extends GhostAgentJMXProxy implements IAgent3D {

    public Agent3DJMXProxy(String agentJMXAddress) throws MalformedURLException, IOException, MalformedObjectNameException {
        super(agentJMXAddress);
    }

    @Override
    public Location getLocation() {
        return (Location) getAttributeNoException("Location");
    }

    @Override
    public Velocity getVelocity() {
        return (Velocity) getAttributeNoException("Velocity");
    }

    @Override
    public Rotation getRotation() {
        return (Rotation) getAttributeNoException("Rotation");
    }
}
