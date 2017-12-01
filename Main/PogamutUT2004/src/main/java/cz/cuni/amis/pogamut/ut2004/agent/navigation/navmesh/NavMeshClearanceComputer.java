package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.grounder.NavMeshDropGrounder;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshEdge;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshPolygon;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathTracer.NavMeshPathTracer;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathTracer.RayPath;
import math.geom2d.Vector2D;

/** Navmesh Clearance computer 
 * 
 * Algorithms to figure out how much maneuvering space there is on the navmesh. 
 */
public class NavMeshClearanceComputer {
	
	protected NavMeshDropGrounder dropGrounder;
	
	public NavMeshClearanceComputer(NavMeshDropGrounder dropGrounder) {
		this.dropGrounder = dropGrounder;
	}

	/** Find edge of navmesh in a specific 2D direction
     * 
     * Returns location of navmesh edge found by walking from specified location to specified direction until navmesh ends.
     * 
     * @param start location to walk from
     * @param direction direction of walk in XY projection
     * @param maxDistance maxDistance to walk
     * @param maxDropDistance max downward distance of navmesh from the location 
     * @return start if start is not on navmesh, null if edge is not found within maxDistance or the actual location of the edge
     */
	public ClearanceLimit findEdge(final Location start, Vector2D direction, final double maxDistance, double maxDropDistance) {
    	assert( maxDistance > 0 );
        
        NavMeshPolygon sourcePolygon = dropGrounder.getPolygonBelow(start, maxDropDistance);
        if (sourcePolygon == null) {
            return new ClearanceLimit( null, start );
        }
        
        Predicate<RayPath<NavMeshPolygon,NavMeshEdge>> keepTracingPredicate = new Predicate<RayPath<NavMeshPolygon,NavMeshEdge>>() {
			@Override
			public boolean apply(RayPath<NavMeshPolygon, NavMeshEdge> rayPath) {
				return Iterables.getLast( rayPath.getSteps() ).getIntersection().getDistance(start) < maxDistance;
			}
        };
        
        RayPath<NavMeshPolygon,NavMeshEdge> rayPath = NavMeshPathTracer.trace(
    		sourcePolygon,
    		start,
    		direction,
    		keepTracingPredicate
        );
        
        RayPath<NavMeshPolygon,NavMeshEdge>.PathStep lastStep = Iterables.getLast( rayPath.getSteps() );
        if ( lastStep.getPolygon() == null ) {
        	return new ClearanceLimit( lastStep.getEdge(), lastStep.getIntersection() );
        } else {
        	// we did not actually find an edge after traversing max distance
        	return null;
        }
    }
	
	/** Find edge of navmesh in a specific 2D direction
     * 
     * Returns location of navmesh edge found by walking from specified location to specified direction until navmesh ends.
     * 
     * @param start location to walk from
     * @param direction direction of walk in XY projection
     * @return start if start is not on navmesh, null if edge is not found within maxDistance or the actual location of the edge
     */
	public ClearanceLimit findEdge(final Location start, Vector2D direction ) {
		return findEdge( start, direction, Double.POSITIVE_INFINITY, NavMeshDropGrounder.DEFAULT_GROUND_DISTANCE );
	}
	
    /** Compute 2D distance from nav mesh edge in XY projection
     * 
     * Returns how far one has to walk from specified location in specified direction to reach edge of nav mesh.
     * 
     * @param start location to walk from
     * @param direction direction of walk in XY projection
     * @param maxDistance maxDistance to walk and return
     * @param maxDropDistance max downward distance of navmesh from the location 
     * @return 0 if start is not on navmesh, distance otherwise
     */
	public double computeXyProjectionDistanceFromEdge(final Location start, Vector2D direction, final double maxDistance, double maxDropDistance) {
    	assert( maxDistance > 0 );
        
    	Location clearanceLimitLocation = findEdge( start, direction, maxDistance, maxDropDistance ).getLocation();
    	
    	if ( clearanceLimitLocation == start ) { // findEdge returns reference to start if start is not on navmesh
    		return 0; // start is not on navmesh
    	} else if ( clearanceLimitLocation == null ) {
    		return maxDistance;
    	} else {
    		return Math.min( 
    			start.getDistance(clearanceLimitLocation), 
      			maxDistance
    		);
    	}
    }
	
	/** Compute 2D distance from nav mesh edge in XY projection
     * 
     * Returns how far one has to walk from specified location in specified direction to reach edge of nav mesh.
     * 
     * @param start location to walk from
     * @param vector direction of walk in XY projection
     * @param maxDistance maxDistance to walk and return
     * @return 0 if start is not on navmesh, distance otherwise
     */
	public double computeXyProjectionDistanceFromEdge(Location start, Vector2D vector, double maxDistance) {
		return computeXyProjectionDistanceFromEdge(start, vector, maxDistance, NavMeshDropGrounder.DEFAULT_GROUND_DISTANCE);
	}
	
    /** Compute 2D distance from nav mesh edge in XY projection
     * 
     * Returns how far one has to walk from specified location in specified direction to reach edge of nav mesh.
     * 
     * @param start location to walk from
     * @param vector direction of walk in XY projection
     * @return 0 if start is not on navmesh, distance otherwise
     */
    public double computeXyProjectionDistanceFromEdge(Location start, Vector2D vector) {
        return computeXyProjectionDistanceFromEdge(start, vector, Double.POSITIVE_INFINITY, NavMeshDropGrounder.DEFAULT_GROUND_DISTANCE);
    }
    
    public static class ClearanceLimit {
    	protected NavMeshEdge edge;
    	protected Location location;
    	
		public ClearanceLimit(NavMeshEdge edge, Location location) {
			super();
			this.edge = edge;
			this.location = location;
		}
		
		public NavMeshEdge getEdge() {
			return edge;
		}
		
		public Location getLocation() {
			return location;
		}
    }
}
