/*
 * Copyright (C) 2014 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
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
package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathfollowing;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;

/**
 * Contains precomputed information about coming jump. If it is even jumpable, minimal and maximal take off point, landing target and navigation target of current link.
 *
 * @author Bogo
 */
public class JumpBoundaries {

    private boolean jumpable;

    private Location takeOffMin;
    private Location takeOffMax;
    private Location takeoffEdgeDirection;

    private Location landingTarget;
    
    private Location navigTarget;

    private Location targetEdgeDirection;
    private NavPointNeighbourLink link;

    /**
     * Constructor for jumpable link.
     * 
     * @param link
     * @param takeOffMin
     * @param takeOffMax
     * @param takeoffEdgeDirection
     * @param target
     * @param targetEdgeDirection
     * @param navigTarget 
     */
    public JumpBoundaries(NavPointNeighbourLink link, Location takeOffMin, Location takeOffMax, Location takeoffEdgeDirection, Location target, Location targetEdgeDirection, Location navigTarget) {
        this.link = link;
        this.takeOffMin = takeOffMin;
        this.takeOffMax = takeOffMax;
        this.takeoffEdgeDirection = takeoffEdgeDirection;
        this.landingTarget = target;
        this.targetEdgeDirection = targetEdgeDirection;
        this.navigTarget = navigTarget;
        this.jumpable = true;
    }

    /**
     * Constructor for not jumpable link.
     * 
     * @param link 
     */
    public JumpBoundaries(NavPointNeighbourLink link) {
        this.link = link;
        this.jumpable = false;
    }

    /**
     * Get link this boundaries are for.
     * @return 
     */
    public NavPointNeighbourLink getLink() {
        return link;
    }

    /**
     * Get link this boundaries are for.
     * @param link 
     */
    public void setLink(NavPointNeighbourLink link) {
        this.link = link;
    }

    /**
     * If is jumpable.
     * @return 
     */
    public boolean isJumpable() {
        return jumpable;
    }

    /**
     * Set jumpable.
     * @param jumpable 
     */
    public void setJumpable(boolean jumpable) {
        this.jumpable = jumpable;
    }

    /**
     * Get earliest take off point.
     * @return 
     */
    public Location getTakeOffMin() {
        return takeOffMin;
    }

    /**
     * Set earliest take off point.
     * @param takeOffMin 
     */
    public void setTakeOffMin(Location takeOffMin) {
        this.takeOffMin = takeOffMin;
    }

    /**
     * Get latest take off point.
     * @return 
     */
    public Location getTakeOffMax() {
        return takeOffMax;
    }

    /**
     * Set latest take off point.
     * @param takeOffMax 
     */
    public void setTakeOffMax(Location takeOffMax) {
        this.takeOffMax = takeOffMax;
    }

    /**
     * Get landing target.
     * @return 
     */
    public Location getLandingTarget() {
        return landingTarget;
    }

    /**
     * Set landing target.
     * @param landingTarget 
     */
    public void setLandingTarget(Location landingTarget) {
        this.landingTarget = landingTarget;
    }

    /**
     * If the given location is between take off boundaries.
     * 
     * @param botLocation
     * @return 
     */
    boolean isInBoundaries(Location botLocation) {
        if (!jumpable) {
            return false;
        }

        if (takeOffMin.equals(takeOffMax, 1.0)) {
            //We have only the point, we are out of NavMesh
            return botLocation.getDistance(takeOffMin) < 20;
        }

        //TODO: Improve || inform about passing max boundary
        return botLocation.getDistance(takeOffMax) + botLocation.getDistance(takeOffMin) <= 2 * takeOffMin.getDistance(takeOffMax);
    }

    /**
     * If the given location is past take off boundaries.
     * 
     * @param botLocation
     * @return 
     */
    boolean isPastBoundaries(Location botLocation) {
        if (jumpable) {
            return botLocation.getDistance(landingTarget) < takeOffMax.getDistance(landingTarget);
        } else {
            return false;
        }
    }

    /**
     * If the jump is up and not fall.
     * 
     * @return 
     */
    public boolean isJumpUp() {
        return jumpable && landingTarget.z - takeOffMax.z > 45;
    }

    /**
     * Get direction of the mesh edge the landing target lies on.
     * 
     * @return 
     */
    public Location getTargetEdgeDirection() {
        return targetEdgeDirection;
    }

    /**
     * Get direction of the mesh edge the take off point lies on.
     * 
     * @return 
     */
    public Location getTakeoffEdgeDirection() {
        return takeoffEdgeDirection;
    }

    /**
     * Get target of current link.
     * 
     * @return 
     */
    public Location getNavigTarget() {
        return navigTarget;
    }

    /**
     * If the bot can land later than at landing target without problem detected by mesh.
     * 
     * @param distanceLater
     * @return 
     */
    boolean canLandLater(double distanceLater) {
        return landingTarget.getDistance2D(navigTarget) > distanceLater;
    }

}
