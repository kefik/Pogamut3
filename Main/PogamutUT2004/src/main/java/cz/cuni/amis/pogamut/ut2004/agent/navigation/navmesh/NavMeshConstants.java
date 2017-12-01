/*
 * Copyright (C) 2013 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh;

import cz.cuni.amis.pogamut.ut2004.utils.UnrealUtils;
import math.geom2d.Vector2D;

/**
 * Holds constants used for navmesh. Used statically.
 * @author Jakub Tomek
 */
public class NavMeshConstants {
    
    // angles in UT2004
    public static double UTHalfAngle = UnrealUtils.FULL_ANGLE_IN_UNREAL_DEGREES/2;
    public static double UTQuarterAngle = UTHalfAngle/2; 
      
    /**
     * When building a path, add this number to polygon Z coordinate, so that is is not on the floor (or under), but floating a little above
     */
    public static double liftPolygonLocation = 40.0;
    
    /**
     * how far from edge of navmesh should agent stay
     */
    //public static double agentRadius = 20.0;
    public static double agentRadius = 0.0;
    
    /**
     * Transgorms a 2D vector to angle. Handy for handling relative rotations.
     * Returns a value between 0 and 65536 (full angle);
     * @param vector
     * @return 
     */
    public static double transform2DVectorToRotation(Vector2D vector) {  
    	return vector.asPolarVector2D().getTheta() * UnrealUtils.ONE_RAD_IN_UNREAL_DEGREES; 
    }
    
    public static Vector2D transformRotationTo2DVector(double yaw) {
    	return Vector2D.createPolar( 1.0, UnrealUtils.ONE_UNREAD_DEGREE_IN_RAD * yaw );
    }
}
