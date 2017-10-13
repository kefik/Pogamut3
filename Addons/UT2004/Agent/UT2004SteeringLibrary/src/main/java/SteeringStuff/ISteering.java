package SteeringStuff;

import SocialSteeringsBeta.RefLocation;
import SteeringProperties.SteeringProperties;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import javax.vecmath.Vector3d;


/**
 * An interface for all steerings.
 * @author Marki
 */
public interface ISteering {

    /**
     * The steering manager calls steering to compute the force of the steering in that tick (logic).
     * @param scaledActualVelocity This is the force of the last velocity, scaled by its weight. The steering can use this vector to create the decelerating force.
     * @param wantsToGoFaster The steering should set this ref parameter, whether is possible to enlarge the velocity (wantsToGoFaster=true), or not.
     * @param wantsToStop If steering want's to stop the agent, he can set this ref parameter to true.
     * @param focus If the wantsToStop is true, steering can set the focus Location, which means the orientation of the stopped agent. (Agent will rotate to this location.)
     * @return The computed steering force.
     */
    public Vector3d run(Vector3d scaledActualVelocity, RefBoolean wantsToGoFaster, RefBoolean wantsToStop, RefLocation focus);

    /**
     * The steering manager will set to the steering his steering properties. That could be set also many times.
     * @param newProperties the new steering properties
     */
    public void setProperties(SteeringProperties newProperties);
}
