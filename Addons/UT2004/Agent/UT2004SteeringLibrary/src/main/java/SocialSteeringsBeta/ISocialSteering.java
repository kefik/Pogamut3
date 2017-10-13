package SocialSteeringsBeta;

import SteeringStuff.ISteering;
import SteeringStuff.RefBoolean;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import javax.vecmath.Vector3d;

/**
 *
 * @author petr
 */
public interface ISocialSteering extends ISteering
{
     public SteeringResult runSocial(Vector3d scaledActualVelocity, RefBoolean wantsToGoFaster, RefBoolean wantsToStop, RefLocation focus);

}
