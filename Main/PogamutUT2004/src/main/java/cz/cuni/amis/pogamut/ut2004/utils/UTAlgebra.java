package cz.cuni.amis.pogamut.ut2004.utils;

import cz.cuni.amis.pogamut.base.utils.math.A;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;

/**
 *
 * @author Jimmy
 */
public class UTAlgebra {

    /**
     * Returns degrees!
     * @param agentLocation
     * @param agentRotation
     * @param object
     * @return
     */
    public static double lineOfSightAngle(Location agentLocation, Rotation agentRotation, Location object) {
        return A.lineOfSightAngle(agentLocation, UnrealUtils.unrealDegreeToRad(agentRotation.getRoll()), object);
    }
}
