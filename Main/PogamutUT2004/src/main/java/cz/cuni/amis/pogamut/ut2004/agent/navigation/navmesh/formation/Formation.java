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
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.NavMeshConstants;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.old.OldNavMesh;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.utils.exception.PogamutException;
import javax.vecmath.Vector2d;

/**
 * A class able to make agents hold formations
 * @author Jakub
 */
public class Formation {
    
    
    UT2004BotModuleController bot;
    
    Player leader;
    Location leaderLastSeenLocation;
    double leaderLastSeenRotation = 0;
    long leaderLastTimeSeen = 0;
    
    Vector2d direction;
    double distance;
    
    OldNavMesh navmesh;
    
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
        navmesh = bot.getNavMeshModule().getNavMesh();
        if(navmesh==null) throw new Exception("Formation cannot be instatiated because there is no navmesh.");
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
        Vector2d actualDirection = NavMeshConstants.transformRotationTo2DVector(yaw);
        // stretch the vector to proper length
        actualDirection.normalize();
        actualDirection.x *= distance;
        actualDirection.y *= distance;
        // add vector to leaders location
        double x = leaderLastSeenLocation.x + actualDirection.x;
        double y = leaderLastSeenLocation.y + actualDirection.y;
        double z = leaderLastSeenLocation.z;
        
        Location targetLocaton = new  Location(x,y,z);
 
        if(bot.getInfo().atLocation(targetLocaton, NavMeshConstants.StepSize/2)) {
            System.out.println("I am at the right spot.");
            if(this.turnToLeaderAtDestination) bot.getMove().turnTo(this.leaderLastSeenLocation);
            return;
        }
        
        Location currenLocation = bot.getInfo().getLocation();
        
        
        Vector2d mainForce = new Vector2d(targetLocaton.x-currenLocation.x, targetLocaton.y-currenLocation.y);
        mainForce.normalize();
        mainForce.x *= NavMeshConstants.ForceToTarget; 
        mainForce.y *= NavMeshConstants.ForceToTarget;
         
        
        // add ray forces
        yaw = bot.getInfo().getRotation().getYaw();
        double distance;
        double force;
        // 1. front ray
        Vector2d frontDirection = NavMeshConstants.transformRotationTo2DVector(yaw);
        distance = navmesh.getDistanceFromEdge(currenLocation, frontDirection, NavMeshConstants.obstacleMaxDistance);
        if(0 < distance && distance < NavMeshConstants.obstacleMaxDistance) {
            force = (distance / NavMeshConstants.obstacleMaxDistance) * NavMeshConstants.obstacleMaxForce;
            Vector2d frontForce = (Vector2d) frontDirection.clone();
            frontForce.negate();
            frontForce.normalize();
            frontForce.x *= force;
            frontForce.y *= force;
            mainForce.add(frontForce);
        }
        
        // 2. left front ray
        Vector2d leftFrontDirection = NavMeshConstants.transformRotationTo2DVector(yaw-NavMeshConstants.UTQuarterAngle/2);
        distance = navmesh.getDistanceFromEdge(currenLocation, leftFrontDirection, NavMeshConstants.obstacleMaxDistance);
        if(0 < distance && distance < NavMeshConstants.obstacleMaxDistance) {
            force = (distance / NavMeshConstants.obstacleMaxDistance) * NavMeshConstants.obstacleMaxForce;
            Vector2d leftFrontForce = (Vector2d) leftFrontDirection.clone();
            leftFrontForce.negate();
            leftFrontForce.normalize();
            leftFrontForce.x *= force;
            leftFrontForce.y *= force;
            mainForce.add(leftFrontForce);
        }

        // 3. right front ray
        Vector2d rightFrontDirection = NavMeshConstants.transformRotationTo2DVector(yaw+NavMeshConstants.UTQuarterAngle/2);
        distance = navmesh.getDistanceFromEdge(currenLocation, rightFrontDirection, NavMeshConstants.obstacleMaxDistance);
        if(0 < distance && distance < NavMeshConstants.obstacleMaxDistance) {
            force = (distance / NavMeshConstants.obstacleMaxDistance) * NavMeshConstants.obstacleMaxForce;
            Vector2d rightFrontForce = (Vector2d) rightFrontDirection.clone();
            rightFrontForce.negate();
            rightFrontForce.normalize();
            rightFrontForce.x *= force;
            rightFrontForce.y *= force;
            mainForce.add(rightFrontForce);
        }   
        
        
        
        
        // end of adding ray forces
        
        // main force now tells which direction to go. Go there by step size
        mainForce.normalize();
        mainForce.x *= NavMeshConstants.StepSize; 
        mainForce.y *= NavMeshConstants.StepSize;
        x = currenLocation.x + mainForce.x;
        y = currenLocation.y + mainForce.y;
        z = currenLocation.z;
        Location resultLocation = new Location(x,y,z);
        
        
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
    public void setDirection(Vector2d direction) {
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
    public Vector2d getDirection() {
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
