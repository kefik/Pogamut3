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

/**
 * Responsible for detecting collisions in navigation. Investigates navigation information and detects if collision occurred.
 *
 * @author Bogo
 */
public class CollisionDetector {

    private boolean signalingCollision = false;

    private int counter = 0;

    private Location lastLocation = null;

    private double lastDistance = 0;

    private final double DELTA_DISTANCE = 10;
    private final double MIN_VELOCITY = 50;
    private final int SIGNAL_DELAY = 2;

    /**
     * Detects if the bot is colliding. Tests the current navigation information against stored data from previous checks.
     * 
     * @param currentLocation Current location of bot
     * @param currentVelocity Current velocity of bot
     * @param currentDistance Current distance of bot
     * @return Whether the bot is colliding
     */
    public boolean isColliding(Location currentLocation, double currentVelocity, double currentDistance) {
        boolean isColliding = false;

        if (lastLocation == null) {
            //We have no previous data, only updating state...
            isColliding = false;
        } else {
            if (Math.abs(lastDistance - currentDistance) < DELTA_DISTANCE && currentVelocity < MIN_VELOCITY && Math.abs(currentLocation.getDistance(lastLocation)) < DELTA_DISTANCE) {
                //All requirements for collision are satisfied!
                if (signalingCollision) {
                    if (++counter > SIGNAL_DELAY) {
                        //Signalling collision
                        isColliding = true;
                    }
                } else {
                    //Raising delay counter
                    signalingCollision = true;
                    ++counter;
                    isColliding = false;
                }
            } else {
                //No collision, reseting state
                signalingCollision = false;
                counter = 0;
                isColliding = false;
            }
        }

        lastLocation = currentLocation;
        lastDistance = currentDistance;
        //lastVelocity = currentVelocity;

        if (isColliding) {
            reset();
        }

        return isColliding;
    }

    /**
     * Resets the state of collision detector.
     */
    public void reset() {
        signalingCollision = false;
    }

}
