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
package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.formation;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.NavMeshClearanceComputer;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.NavMeshConstants;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import math.geom2d.Vector2D;

/**
 * A class able to make agents hold formations
 * @author Jakub
 */
public class Formation {
    
    // forces in formations
    public static final double FORCE_TO_TARGET = 1;
    public static final double STEP_SIZE = 75;
    // force in zero distance
    public static final double OBSTACLE_MAX_FORCE = 1.5;
    // in this distance the force is down to 0
    public static final double OBSTACLE_MAX_DISTANCE = 100;
    
    UT2004BotModuleController bot;
    
    Player leader;
    Location leaderLastSeenLocation;
    double leaderLastSeenRotation = 0;
    long leaderLastTimeSeen = 0;
    
    Vector2D direction;
    double distance;
    
    NavMeshClearanceComputer clearanceComputer;
    
    // direction is relative to leader's direction
    boolean relativeRotation = true;
    // bot turns to leader when he reaches the desired spot
    boolean turnToLeaderAtDestination = true;
    // should i see leader all the time?
    boolean watchLeader = true;
    // bot will not care about the navmesh
    boolean suicideMode = false;

    
    public Formation(UT2004BotModuleController bot) throws Exception {
        this.bot = bot;
        clearanceComputer = bot.getNavMeshModule().getClearanceComputer();
        if (clearanceComputer==null) throw new Exception("Formation cannot be instatiated because there is no navmesh.");
    }
    
    
    
    
    /**
     * Makes the bot move to the proper place
     */
    public void holdFormation() {
        
        if(leader == null) {
            System.out.println("No leader - no formation");
            return;
        }
        
        // update leader's location
        updateLeaderInfo();
        
        // what if i don't see leader?
        if(null == bot.getPlayers().getVisiblePlayer(leader.getId())) {
            if(watchLeader) {
                System.out.println("I lost the leader from sight. I look where i last saw him."); 
                bot.getMove().turnTo(leaderLastSeenLocation);
                return;
            }
        }
        
        if(leaderLastSeenLocation == null) {
            System.out.println("I dont know where my leader is..."); 
            return;
        }
        
        // get the point where we want to be
        double yaw = NavMeshConstants.transform2DVectorToRotation(direction);
        if(this.relativeRotation) yaw += leaderLastSeenRotation;
        Vector2D actualDirection = NavMeshConstants.transformRotationTo2DVector(yaw).getNormalizedVector().times( distance );

        // add vector to leaders location      
        Location targetLocaton = new  Location(
    		leaderLastSeenLocation.x + actualDirection.getX(),
    		leaderLastSeenLocation.y + actualDirection.getY(),
    		leaderLastSeenLocation.z
	    );
 
        if(bot.getInfo().atLocation(targetLocaton, STEP_SIZE/2)) {
            System.out.println("I am at the right spot.");
            if(this.turnToLeaderAtDestination) bot.getMove().turnTo(this.leaderLastSeenLocation);
            return;
        }
        
        Location currenLocation = bot.getInfo().getLocation();
        
        
        Vector2D mainForce = new Vector2D(targetLocaton.x-currenLocation.x, targetLocaton.y-currenLocation.y);
        mainForce = mainForce.getNormalizedVector().times( FORCE_TO_TARGET );
        
        
        // add ray forces
        yaw = bot.getInfo().getRotation().getYaw();
        double distance;
        double force;
        // 1. front ray
        Vector2D frontDirection = NavMeshConstants.transformRotationTo2DVector(yaw);
        distance = clearanceComputer.computeXyProjectionDistanceFromEdge( currenLocation, frontDirection, OBSTACLE_MAX_DISTANCE );
        if(0 < distance && distance < OBSTACLE_MAX_DISTANCE) {
            force = (distance / OBSTACLE_MAX_DISTANCE) * OBSTACLE_MAX_FORCE;
            Vector2D frontForce = frontDirection.getNormalizedVector().times( -force );
            mainForce = mainForce.plus(frontForce);
        }
        
        // 2. left front ray
        Vector2D leftFrontDirection = NavMeshConstants.transformRotationTo2DVector(yaw-NavMeshConstants.UTQuarterAngle/2);
        distance = clearanceComputer.computeXyProjectionDistanceFromEdge( currenLocation, leftFrontDirection, OBSTACLE_MAX_DISTANCE );
        if(0 < distance && distance < OBSTACLE_MAX_DISTANCE) {
            force = (distance / OBSTACLE_MAX_DISTANCE) * OBSTACLE_MAX_FORCE;
            Vector2D leftFrontForce = leftFrontDirection.getNormalizedVector().times( -force );
            mainForce = mainForce.plus(leftFrontForce);
        }

        // 3. right front ray
        Vector2D rightFrontDirection = NavMeshConstants.transformRotationTo2DVector(yaw+NavMeshConstants.UTQuarterAngle/2);
        distance = clearanceComputer.computeXyProjectionDistanceFromEdge( currenLocation, rightFrontDirection, OBSTACLE_MAX_DISTANCE );
        if(0 < distance && distance < OBSTACLE_MAX_DISTANCE) {
            force = (distance / OBSTACLE_MAX_DISTANCE) * OBSTACLE_MAX_FORCE;
            Vector2D rightFrontForce = rightFrontDirection.getNormalizedVector().times( -force );
            mainForce = mainForce.plus(rightFrontForce);
        }   
        
        
        
        
        // end of adding ray forces
        
        // main force now tells which direction to go. Go there by step size
        mainForce = mainForce.getNormalizedVector().times( STEP_SIZE );
        Location resultLocation = new Location(
    		currenLocation.x + mainForce.getX(),
    		currenLocation.y + mainForce.getY(),
    		currenLocation.z
    	);
        
        
        System.out.println("Moving to resultLocation " + resultLocation);
        bot.getMove().moveTo(resultLocation);
    }
    
    public void updateLeaderInfo() {
        if(bot.getPlayers().getVisiblePlayer(leader.getId())!=null) {
            System.out.println("Updating leader's location...");
            leaderLastSeenLocation = leader.getLocation();
            leaderLastSeenRotation = leader.getRotation().getYaw();
            leaderLastTimeSeen = System.nanoTime();
        } else {
            System.out.println("I cannot see the leader.");
        }
    }
    
    /**
     * How many seconds did the bot not seen his leader?
     * @return 
     */
    public int howLongLeaderNotSeen() {
        double nano = (System.nanoTime() - this.leaderLastTimeSeen);
        return (int)(nano / 1000000000);
    }
    
    /**
     * Setters 
     */
    public void setLeader(Player leader) {
        this.leader = leader;
        updateLeaderInfo();
    } 
    public void setDirection(Vector2D direction) {
        this.direction = direction;
    }     
    public void setDistance(double distance) {
        this.distance = distance;
    }
    
    /**
     * Getters
     */
    public Player getLeader() {
        return leader;
    } 
    public Vector2D getDirection() {
        return direction;
    }     
    public double getDistance() {
        return distance;
    } 
    public Location getLeaderLastSeenLocation() {
        return leaderLastSeenLocation;
    }
    public long getLeaderLastTimeSeen() {
        return leaderLastTimeSeen;
    }    
   
}
