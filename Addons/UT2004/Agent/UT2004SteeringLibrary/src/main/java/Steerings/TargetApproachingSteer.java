package Steerings;

import SocialSteeringsBeta.RefLocation;
import SteeringStuff.SteeringManager;
import SteeringStuff.RefBoolean;
import SteeringProperties.SteeringProperties;
import SteeringProperties.TargetApproachingProperties;
import SteeringProperties.Target_packet;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import javax.vecmath.Vector3d;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import SteeringStuff.ISteering;
import java.util.ArrayList;
import javax.vecmath.Tuple3d;


/**
 * Using this steering the bot can approach one or more targets.
 *
 * @author Marki
 */
public class TargetApproachingSteer implements ISteering {

    /** This steering needs botself. */
    private UT2004Bot botself;

    private ArrayList<Target_packet> targets = new ArrayList<Target_packet>();

    private static int NEARLY_THERE_DISTANCE = 150;

    /**
     * @param bot Instance of the steered bot.
     */
    public TargetApproachingSteer(UT2004Bot bot) {
        botself = bot;
    }

    /** When called, the bot starts steering, when possible, he get's nearer the target location. */
    @Override
    public Vector3d run(Vector3d scaledActualVelocity, RefBoolean wantsToGoFaster, RefBoolean wantsToStop, RefLocation focus)
    {
        // Supposed velocity in the next tick of logic, after applying various steering forces to the bot.
        Vector3d nextVelocity = new Vector3d(0,0,0);

        for(Target_packet tp : targets) {

            /** ISteering properties: target location - bot approaches this location. */
            Location targetLocation = tp.getTargetLocation();

            // A vector from the bot to the target location.
            Vector3d vectorToTarget = new Vector3d(targetLocation.x - botself.getLocation().x, targetLocation.y - botself.getLocation().y, 0);

            double distFromTarget = vectorToTarget.length();

            /** ISteering properties: target gravity - a parameter meaning how attracted the bot is to his target location. */
            int attractiveForce = tp.getAttractiveForce(distFromTarget);
            
            if (distFromTarget < NEARLY_THERE_DISTANCE) {
                wantsToStop.setValue(true);
                //if (SteeringManager.DEBUG) System.out.println("We reached the target");
            } else {
                vectorToTarget.normalize();
                vectorToTarget.scale(attractiveForce);
                nextVelocity.add((Tuple3d) vectorToTarget);
            }
        }
        wantsToGoFaster.setValue(true);
        return nextVelocity;
    }

    public void setProperties(SteeringProperties newProperties) {
        ArrayList<Target_packet> al = ((TargetApproachingProperties)newProperties).getTargets();
        targets.clear();
        for(Target_packet tp : al) {
            targets.add(new Target_packet(tp.getTargetLocation(), tp.getForce_packet()));
        }
    }

    public TargetApproachingProperties getProperties() {
        TargetApproachingProperties properties = new TargetApproachingProperties();
        properties.setTargets(targets);
        return properties;
    }

}