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

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import math.geom3d.Axis3D;
import math.geom3d.plane.AxisAlignedPlane3D;

/** Space a BSP node represents
 * 
 *  Used during BSP tree construction to compute SAH.
 */
public class NodeSpace implements Serializable {
    	private static final long serialVersionUID = 1L;
    	
    	// parameters of the space part
        public Location max;
        public Location min;    	
        
        public NodeSpace( double minX, double maxX, double minY, double maxY, double minZ, double maxZ ) {
        	this.min = new Location( minX, minY, minZ );
        	this.max = new Location( maxX, maxY, maxZ );
        }
        
        public NodeSpace splitOffNegative(AxisAlignedPlane3D plane) {
        	return new NodeSpace(
        		min.x, (plane.axis == Axis3D.X ? plane.origin : max.x),
    			min.y, (plane.axis == Axis3D.Y ? plane.origin : max.y),
    			min.z, (plane.axis == Axis3D.Z ? plane.origin : max.z)
    		);
        }
        
        public NodeSpace splitOffPositive(AxisAlignedPlane3D plane) {
        	return new NodeSpace(
        		(plane.axis == Axis3D.X ? plane.origin : min.x),  max.x,
        		(plane.axis == Axis3D.Y ? plane.origin : min.y),  max.y,
        		(plane.axis == Axis3D.Z ? plane.origin : min.z),  max.z
        	);
        }

		public void expand(Location vertex) {
            // update the boundaries
			min = new Location(
				Math.min( vertex.x-1, min.x),
				Math.min( vertex.y-1, min.y),
				Math.min( vertex.z-1, min.z)
		    );
			max = new Location(
				Math.max( vertex.x+1, max.x),
				Math.max( vertex.y+1, max.y),
				Math.max( vertex.z+1, max.z)
			);
		}
		
		public boolean contains(Location location) {
			return (
				min.x-0.1 <= location.x && location.x <= max.x+0.1 &&
				min.y-0.1 <= location.y && location.y <= max.y+0.1 &&
				min.z-0.1 <= location.z && location.z <= max.z+0.1
	        );
		}
		
		public double getSurfaceArea() {
			return (
				(
					(max.x-min.x)*(max.y-min.y)
					+
					(max.x-min.x)*(max.z-min.z)
					+
					(max.y-min.y)*(max.z-min.z)
				)
				*
				2
			);
		}
		
		@Override
		public String toString() {
			return (
				"x( " + min.x + ", " + max.x + " ), " + 
				"y( " + min.y + ", " + max.y + " ), " +
				"z( " + min.z + ", " + max.z + " )"
			);
		}	
    }