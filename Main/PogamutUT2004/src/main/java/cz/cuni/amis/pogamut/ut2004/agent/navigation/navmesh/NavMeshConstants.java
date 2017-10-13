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

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.old.OldOffMeshEdge;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;
import cz.cuni.amis.pogamut.ut2004.server.impl.UT2004Server;
import cz.cuni.amis.pogamut.ut2004.utils.LinkFlag;
import javax.vecmath.Vector2d;

/**
 * Holds constants used for navmesh. Used statically.
 * @author Jakub Tomek
 */
public class NavMeshConstants {
    // storage directories
    public static String pureMeshReadDir = "navmesh"; 
    public static String processedMeshDir = "navmesh";
    public static String pureLevelGeometryReadDir = "map"; 
    public static String processedLevelGeometryDir = "map";    
    
    // polygon BSP tree params
    public static int stopSplittingNumberOfPolygons = 1;
    public static int maxNumberOfPolygonsToTry = 10;
    public static double maxAllowedSplitFactor = 1.0;
    
    // level geometry BSP tree params
    public static int stopSplittingNumberOfTriangles = 20;
    public static double stopSplittingSizeOfOneBlock = 40.0;
    public static double maxAllowedCrossFactor = 0.6;
    
    // angles in UT2004
    public static double UTFullAngle = 65536;
    public static double UTHalfAngle = UTFullAngle/2;
    public static double UTQuarterAngle = UTHalfAngle/2; 
    
    // forces in formations
    public static double ForceToTarget = 1;
    public static double StepSize = 75;
    // force in zero distance
    public static double obstacleMaxForce = 1.5;
    // in this distance the force is down to 0
    public static double obstacleMaxDistance = 100;
    
    /**
     * how far above or bellow a polygon a bot can stand and still be considered to be standing on it
     */
    public static double maxDistanceBotPolygon = 90.0;
    
    /**
     * When building a path, add this number to polygon Z coordinate, so that is is not on the floor (or under), but floating a little above
     */
    public static double liftPolygonLocation = 40.0;
    
    /**
     * how far from edge of navmesh should agent stay
     */
    //public static double agentRadius = 20.0;
    public static double agentRadius = 0.0;
    
    
    
    public static Location getColorForOffMeshConnection(OldOffMeshEdge oe, UT2004Server server) {
        
        NavPoint from = server.getWorldView().get(oe.getFrom().getNavPointId(), NavPoint.class);
        NavPoint to = server.getWorldView().get(oe.getTo().getNavPointId(), NavPoint.class);
        
        if (from == null) return new Location(255,255,100);
        if (to == null) return new Location(255,255,100);

        //lift is blue
        if(from.isLiftCenter() || to.isLiftCenter()) return new Location(0, 0, 255);
        // teleporter is violet
        if(from.isTeleporter() && to.isTeleporter()) return new Location(150, 0, 255);
        // return new Location(0, 180, 64);
        
        NavPointNeighbourLink link = from.getOutgoingEdges().get(oe.getLinkId());
        
        if (link == null) return new Location(255,255,100);
        
        int linkFlags = link.getFlags();        
        if ((linkFlags & LinkFlag.DOOR.get()) > 0) {}
        if ((linkFlags & LinkFlag.FLY.get()) > 0) {return new Location(255, 0, 0);}
        if ((linkFlags & LinkFlag.FORCED.get()) > 0) {return new Location(255, 170, 255);}      
        if ((linkFlags & LinkFlag.LADDER.get()) > 0) {return new Location(255, 0, 0);}
        if ((linkFlags & LinkFlag.PLAYERONLY.get()) > 0) {return new Location(255, 0, 0);}
        if ((linkFlags & LinkFlag.PROSCRIBED.get()) > 0) {return new Location(255, 0, 0);}
        if ((linkFlags & LinkFlag.SPECIAL.get()) > 0) {return new Location(255, 0, 255);}
        if ((linkFlags & LinkFlag.SWIM.get()) > 0) {return new Location(255, 0, 0);}
        if ((linkFlags & LinkFlag.WALK.get()) > 0) {}
        // JUMP is light green
        if ((linkFlags & LinkFlag.JUMP.get()) > 0) {return new Location(100, 255, 255);}
        // default
        return new Location(255,255,100);
    }
    
    /**
     * Transgorms a 2D vector to angle. Handy for handling relative rotations.
     * Returns a value between 0 and 65536 (full angle);
     * @param vector
     * @return 
     */
    public static double transform2DVectorToRotation(Vector2d vector) {
    
        double yaw;
        double x = vector.x;
        double y = vector.y;
        
        if(x==0) {
            if(y>=0) yaw = UTQuarterAngle;
            else yaw = 3*UTQuarterAngle;
        }
        else {
            if(y==0) {
                if(x>=0) yaw = 0;
                else yaw = 2*UTQuarterAngle;
            }
            else {
                // neither x nor y are 0
                // turning right 0-180 degrees
                if(y>0) {
                    // 0-90
                    if(x>0) {
                        yaw = 0*UTQuarterAngle + Math.atan(y/x)/(2*Math.PI)*UTFullAngle;
                    }
                    // 90-180
                    else {
                        yaw = 1*UTQuarterAngle + Math.atan(-x/y)/(2*Math.PI)*UTFullAngle;
                    }
                }
                // 180-360
                else {
                    // 180-270
                    if(x<0) {
                        yaw = 2*UTQuarterAngle + Math.atan(-y/-x)/(2*Math.PI)*UTFullAngle;
                    }
                    // 270-360
                    else {
                        yaw = 3*UTQuarterAngle + Math.atan(x/-y)/(2*Math.PI)*UTFullAngle;
                    }    
                }
            }
        }        
        return yaw;      
    }

    public static Vector2d transformRotationTo2DVector(double yaw) {
        
            Vector2d direction = null;
        
            // transorm it to 0..UTFullAngle
            while(yaw < 0) yaw += UTFullAngle;
            yaw = yaw % UTFullAngle;
            // transform it to radians
            double yawRad = yaw / UTFullAngle * 2*Math.PI;
            
            // now we will transform the angle back to vector2d
            if(yaw % UTQuarterAngle == 0) {
                if(yaw==0*UTQuarterAngle) direction = new Vector2d(1,0);
                if(yaw==1*UTQuarterAngle) direction = new Vector2d(0,1);
                if(yaw==2*UTQuarterAngle) direction = new Vector2d(-1,0);
                if(yaw==3*UTQuarterAngle) direction = new Vector2d(0,-1);            
            }
            else {
                if(yaw < 2*UTQuarterAngle) {
                    if(yaw < 1*UTQuarterAngle) {
                        direction = new Vector2d(1, Math.tan(yawRad));
                    }
                    else {
                        direction = new Vector2d(-Math.tan(yawRad-Math.PI/2), 1);
                    }
                }
                else {
                    if(yaw < 3*UTQuarterAngle) {
                        direction = new Vector2d(-1, Math.tan(yawRad-Math.PI));
                    }
                    else {
                        direction = new Vector2d(-Math.tan(yawRad-3*Math.PI/2), -1);
                    }
                }
            } 
            // now we have the direction on x,y.
            // we must add back the z direction
            direction.normalize(); 
            return direction;
    }
    
}
