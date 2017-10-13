package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh;

import com.google.common.base.Predicate;

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

    /** Compute 2D distance from nav mesh edge in XY projection
     * 
     * Returns how far one has to walk from specified location in specified direction to reach edge of nav mesh.
     * 
     * @param location location to walk from
     * @param direction direction of walk in XY projection
     * @param maxDistance maxDistance to walk and return
     * @param maxDropDistance max downward distance of navmesh from the location 
     * @return 0 if location is not on navmesh, distance otherwise
     */
	public double computeXyProjectionDistanceFromEdge(final Location location, Vector2D direction, final double maxDistance, double maxDropDistance) {
    	assert( maxDistance > 0 );
        
        NavMeshPolygon sourcePolygon = dropGrounder.getPolygonBelow(location, maxDropDistance);
        if (sourcePolygon == null) {
            return 0;
        }
        
        Predicate<RayPath<NavMeshPolygon,NavMeshEdge>> keepTracingPredicate = new Predicate<RayPath<NavMeshPolygon,NavMeshEdge>>() {
			@Override
			public boolean apply(RayPath<NavMeshPolygon, NavMeshEdge> rayPath) {
				return rayPath.getIntersections().get( rayPath.getIntersections().size()-1 ).getDistance(location) < maxDistance;
			}
        };
        
        RayPath<NavMeshPolygon,NavMeshEdge> rayPath = NavMeshPathTracer.trace(
    		sourcePolygon,
    		location,
    		direction,
    		keepTracingPredicate
        );
        
        return Math.min( 
    		rayPath.getIntersections().get( rayPath.getIntersections().size()-1 ).getDistance(location), 
    		maxDistance
        );
    }
	
	/** Compute 2D distance from nav mesh edge in XY projection
     * 
     * Returns how far one has to walk from specified location in specified direction to reach edge of nav mesh.
     * 
     * @param location location to walk from
     * @param vector direction of walk in XY projection
     * @param maxDistance maxDistance to walk and return
     * @return 0 if location is not on navmesh, distance otherwise
     */
	public double computeXyProjectionDistanceFromEdge(Location location, Vector2D vector, double maxDistance) {
		return computeXyProjectionDistanceFromEdge(location, vector, maxDistance, NavMeshDropGrounder.DEFAULT_GROUND_DISTANCE);
	}
	
    /** Compute 2D distance from nav mesh edge in XY projection
     * 
     * Returns how far one has to walk from specified location in specified direction to reach edge of nav mesh.
     * 
     * @param location location to walk from
     * @param vector direction of walk in XY projection
     * @return 0 if location is not on navmesh, distance otherwise
     */
    public double computeXyProjectionDistanceFromEdge(Location location, Vector2D vector) {
        return computeXyProjectionDistanceFromEdge(location, vector, Double.POSITIVE_INFINITY, NavMeshDropGrounder.DEFAULT_GROUND_DISTANCE);
    }
}
