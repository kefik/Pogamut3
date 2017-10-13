package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathPlanner.polygonPathFunnel;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.NavMeshConstants;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshBoundary;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshVertex;
import math.geom2d.line.LineSegment2D;
import math.geom3d.Point3D;
import math.geom3d.line.LineSegment3D;

/** One of the Funnel's rays.
 * <p>
 * A ray from a vantage point through a crossing of a navmesh boundary.
 * <p>
 * This class compensates for a quirk of path executor that could cause 
 * the agent to fall into a pit or bump into a wall when a waypoint is placed on the edge of walkable area.
 * This is why a crossing is used instead of a vertex of the boundary in the implementation.
 */
public class FunnelRay {
	protected ILocated vantagePoint;
	protected NavMeshBoundary boundary;
	protected NavMeshVertex vertex;
	protected int index;
	protected boolean isLeft;
	
	/** Construct the left ray of a funnel
	 * 
	 * @param vantagePoint the source of the ray
	 * @param boundary the boundary associated with the ray
	 * @param index index of the boundary in the boundary input sequence of {@link PolygonPathSmoothingFunnelAlgorithm}
	 * @param vertex a vertex of the boundary to determine on which side the crossing shall be computed.
	 * It is an error to pass a vertex not belonging to the boundary.
	 * @return constructed ray
	 */
	public static FunnelRay createLeftRay(ILocated vantagePoint, NavMeshBoundary boundary, int index, NavMeshVertex vertex ) {
		return new FunnelRay(vantagePoint, boundary, index, vertex, true );
	}
	
	/** Construct the right ray of a funnel
	 * 
	 * @param vantagePoint the source of the ray
	 * @param boundary the boundary associated with the ray
	 * @param index index of the boundary in the boundary input sequence of {@link PolygonPathSmoothingFunnelAlgorithm}
	 * @param vertex a vertex of the boundary to determine on which side the crossing shall be computed.
	 * It is an error to pass a vertex not belonging to the boundary.
	 * @return constructed ray
	 */
	public static FunnelRay createRightRay(ILocated vantagePoint, NavMeshBoundary boundary, int index, NavMeshVertex vertex ) {
		return new FunnelRay(vantagePoint, boundary, index, vertex, false );
	}
	
	/** Constructor
	 */
	protected FunnelRay( ILocated vantagePoint, NavMeshBoundary boundary, int index, NavMeshVertex vertex, boolean isLeft ) {
		assert( boundary.getSourceVertex() == vertex || boundary.getDestinationVertex() == vertex );
		this.vantagePoint = vantagePoint;
		this.boundary = boundary;
		this.vertex = vertex;
		this.index = index;
		this.isLeft = isLeft;
	}
	
	/** Get the vantage point
	 */
	public ILocated getVantagePoint() {
		return vantagePoint;
	}
	
	/** Get the crossing that determines the direction of the ray
	 * <p>
	 * Use this crossing rather than the vertex to compensate for the path executor quirk. 
	 */
	public Location getCrossing() {
        if (!getVertex().isOnWalkableAreaEdge()) {
        	return getVertex().getLocation().addZ(NavMeshConstants.liftPolygonLocation);
        } else {
        	// Path executor could accidentally bump into a wall or fall into a pit if it tried to navigate through the vertex.
        	// Compensate by shifting the crossing towards the opposite vertex.
        	
            if (boundary.asLineSegment3D().getLength() <= 2 * NavMeshConstants.agentRadius) {
                return (
                	Location.interpolate(
                		getVertex().getLocation(),
                		getOppositeVertexOfBoundary().getLocation(),
                		0.5 
                	).addZ( NavMeshConstants.liftPolygonLocation )
                );
            } else {
            	Location vectorFromVertexToOppositeVertex = getOppositeVertexOfBoundary().getLocation().sub( getVertex().getLocation() );
                return (
                	getVertex().getLocation().add( 
                		vectorFromVertexToOppositeVertex.getNormalized().scale( NavMeshConstants.agentRadius )
                	).addZ(NavMeshConstants.liftPolygonLocation)                            	
                );
            }
        }
	}
	
	/** Get the line segment from the vantage point to the crossing
	 */
	public LineSegment2D asLineSegment2D() {
		return PolygonPathSmoothingFunnelAlgorithm.xyPlaneSubsystem.project(
        	new LineSegment3D( 
        		vantagePoint.getLocation().asPoint3D(),
        		getCrossing().getLocation().asPoint3D()
        	)
        ); 
	}
	
	/** Tell whether a point is on the outside side
	 * <p>
	 * Even if this returns false, the point may still be outside of the funnel because
	 * it is on the outside side of the other ray
	 * 
	 * @param point
	 * @return whether the point is on the outside side
	 */
	public boolean isOnOutsideSide( Point3D point ) {
        double signedDistanceToRay = asLineSegment2D().getSignedDistance( PolygonPathSmoothingFunnelAlgorithm.xyPlaneSubsystem.project( point ) );
        if (signedDistanceToRay < 0) {
        	return !isLeft;
        } else {
        	return isLeft;
        }
	}

	/** Get the index of the boundary in the boundary input sequence of the {@link PolygonPathSmoothingFunnelAlgorithm}
	 */
	public int getIndex() {
		return index;
	}
	
	/** Get the vertex
	 * <p>
	 * This is protected to hide the path executor quirk compensation.
	 */
	protected NavMeshVertex getVertex() {
		return vertex;
	}
	
	/** Get the vertex on the opposite side of the boundary
	 * <p>
	 * This is protected to hide the path executor quirk compensation.
	 */
	protected NavMeshVertex getOppositeVertexOfBoundary() {
		if ( boundary.getSourceVertex() == vertex ) {
			return boundary.getDestinationVertex();
		} else {
			return boundary.getSourceVertex();
		}
	}
}
