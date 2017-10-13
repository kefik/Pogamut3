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
 * Holds information about a point on the border of the mesh and about the mesh
 * edge it lies on.
 *
 * @author Bogo
 */
public class BorderPoint {

    private Location point;
    private Location direction;

    /**
     * Constructs border point.
     *
     * @param point Point on the border of mesh
     * @param direction Direction of the edge of mesh
     */
    public BorderPoint(Location point, Location direction) {
        this.point = point;
        this.direction = direction;
    }

    /**
     * Gets location of border point.
     *
     * @return
     */
    public Location getPoint() {
        return point;
    }

    /**
     * Gets direction of the border edge.
     *
     * @return
     */
    public Location getDirection() {
        return direction;
    }

    /**
     * Sets location of the border point.
     *
     * @param point
     */
    public void setPoint(Location point) {
        this.point = point;
    }

    /**
     * Sets direction of the border edge.
     *
     * @param direction
     */
    public void setDirection(Location direction) {
        this.direction = direction;
    }

}
