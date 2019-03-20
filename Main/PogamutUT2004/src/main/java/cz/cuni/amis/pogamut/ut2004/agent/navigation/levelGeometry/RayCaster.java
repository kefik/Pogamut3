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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import math.bsp.IConstBspTree;
import math.bsp.algorithm.raycast.BspRayCaster;
import math.geom2d.Point2D;
import math.geom2d.polygon.SimplePolygon2D;
import math.geom3d.Point3D;
import math.geom3d.line.StraightLine3D;
import math.geom3d.plane.AxisAlignedPlane3D;
import math.geom3d.plane.Plane3D;
import math.geom3d.polygon.SimplePlanarPolygon3D;

/** Level geometry ray caster
 * 
 * Implementation of ray caster over BSP tree divided by axis aligned planes and containing triangles.
 */
public class RayCaster 
	extends BspRayCaster< ArrayList<Triangle>, AxisAlignedPlane3D, StraightLine3D, RayCastResult > 
	implements Serializable {
	
	private static final long serialVersionUID = 2L;
	
	public RayCaster( IConstBspTree<ArrayList<Triangle>,AxisAlignedPlane3D> tree ) {
		super(tree, false);
	}

	@Override
	public double computeSideSignedDistanceSquare(AxisAlignedPlane3D boundary, StraightLine3D ray) {
		double intersectionParametric = boundary.getLineIntersectionParametric(ray);
		if (Double.isNaN(intersectionParametric)) {
			return boundary.getSignedDistance(ray.getOrigin()) * Double.POSITIVE_INFINITY; 
		}
		double sign = Math.signum( boundary.getSignedDistance( ray.getOrigin() ) );
		if ( Double.isInfinite(intersectionParametric) || intersectionParametric < 0.0 || intersectionParametric > 1.0 ) {
			return sign * Double.POSITIVE_INFINITY;
		} else {
			return sign * ray.getOrigin().getDistanceSquare( ray.getPoint(intersectionParametric) );
		}
	}

	@Override
	public List<RayCastResult> getCollisions( StraightLine3D ray, double minDistanceSquare, double maxDistanceSquare, ArrayList<Triangle> data ) {
		LinkedList<RayCastResult> retval = new LinkedList<RayCastResult>();
		
		// now let's examine the ray's collisions with triangles
		for(Triangle triangle : data) {

			SimplePlanarPolygon3D trianglePolygon = triangle.planarPolygon;
			Plane3D trianglePlane = trianglePolygon.getPlane();
			Double intersectionParameter = ray.getPlaneIntersectionParametric(trianglePlane);
			if ( Double.isInfinite(intersectionParameter) || intersectionParameter < 0 || intersectionParameter > 1 ) {
				// no intersection or intersection in wrong direction or past the point
				continue;
			}

			Point3D intersection = ray.getPoint(intersectionParameter);
			double intersectionDistanceSquare = ray.getOrigin().getDistanceSquare(intersection);
			
			if ( intersectionDistanceSquare < minDistanceSquare || maxDistanceSquare < intersectionDistanceSquare ) {
				continue;
			}
			
			Point2D intersection2D = trianglePlane.getCoordinateSubsystem().project( intersection );
			SimplePolygon2D polygon2D = trianglePolygon.getPolygonIn2d();

			if ( !polygon2D.contains( intersection2D, triangle.signedAreaIn2dProjection ) ) {
				// intersection is outside the polygon
				continue;
			}

			Location intersectionLocation = new Location(intersection);
			retval.push( new RayCastResult( ray, intersectionLocation, trianglePlane.getNormalVector(), Math.sqrt(intersectionDistanceSquare), triangle ) );
		}

		Collections.sort( retval, hitDistanceComparator );

		return retval;
	}
	
	protected static final Comparator<RayCastResult> hitDistanceComparator = new Comparator<RayCastResult>() {
		@Override
		public int compare(RayCastResult lhs, RayCastResult rhs) {
			return Double.compare( lhs.hitDistance, rhs.hitDistance );
		}
	} ;
}
