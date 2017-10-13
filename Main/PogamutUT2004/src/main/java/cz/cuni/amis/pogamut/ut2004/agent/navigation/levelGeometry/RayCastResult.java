/*
 * Copyright (C) 2016 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
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
package cz.cuni.amis.pogamut.ut2004.agent.navigation.levelGeometry;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import math.geom3d.Vector3D;
import math.geom3d.line.StraightLine3D;

/** Ray cast result
 * 
 * Immutable.
 */
public class RayCastResult {

	public final StraightLine3D ray;
	public final Location hitLocation;
	public final Vector3D hitNormal;
	public final double hitDistance;
	public final Triangle hitTriangle;
	
	public RayCastResult( StraightLine3D ray, Location hitLocation, Vector3D hitNormal, double hitDistance, Triangle hitTriangle ) {
		this.ray = ray;
		this.hitLocation = hitLocation;
		this.hitNormal = hitNormal;
		this.hitDistance = hitDistance;
		this.hitTriangle = hitTriangle;
	}

	public boolean isHit() {
		return hitLocation != null;
	}		
}
