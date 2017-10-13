package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathPlanner.polygonPathFunnel;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshBoundary;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshVertex;
import math.geom2d.Point2D;
import math.geom2d.line.LineSegment2D;
import math.geom3d.Point3D;
import math.geom3d.line.LineSegment3D;

/** A polygon path funnel
 * <p>
 * Funnel is an angle comprised of two rays and the gateway in between - a line-segment connecting the two rays.
 * In addition each ray is related to a boundary between two navmesh polygons.
 * <p>
 * This implementation compensates for a quirk of path executor that could cause 
 * the agent to fall into a pit or bump into a wall when a waypoint is placed on the edge of walkable area.
 */
public class Funnel {
    	protected FunnelRay leftRay;
    	protected FunnelRay rightRay;
    	
    	/** Direct constructor
    	 */
    	public Funnel( FunnelRay leftRay, FunnelRay rightRay ) {
    		assert( leftRay.getVantagePoint() == rightRay.getVantagePoint() );
    		
    		this.leftRay = leftRay;
    		this.rightRay = rightRay;
    	}
    	
    	/** Create a funnel matching a boundary
    	 * 
    	 * @param vantagePoint vantage (source) point of the funnel
    	 * @param boundary Boundary that defines left and right rays of the funnel. 
    	 * @param index index of the boundary in the boundary list
    	 * @return a constructed funnel
    	 */
    	public static Funnel createFromBoundary( ILocated vantagePoint, NavMeshBoundary boundary, int index ) {
    		
            NavMeshVertex boundarySourceVertex = boundary.getSourceVertex();
            NavMeshVertex boundaryDestinationVertex = boundary.getDestinationVertex();
            LineSegment2D gateway = PolygonPathSmoothingFunnelAlgorithm.xyPlaneSubsystem.project( boundary.asLineSegment3D() );
            
    		FunnelRay leftRay = null;
            FunnelRay rightRay = null;
            
            Point2D vantagePoint2D = PolygonPathSmoothingFunnelAlgorithm.xyPlaneSubsystem.project( vantagePoint.getLocation().asPoint3D() );
            if ( gateway.getSignedDistance( vantagePoint2D ) < 0) { // identify left and right vertex
            	 // source v. = left, dest v. = right
            	leftRay = FunnelRay.createLeftRay( vantagePoint, boundary, index, boundarySourceVertex );
            	rightRay = FunnelRay.createRightRay( vantagePoint, boundary, index, boundaryDestinationVertex );
            } else {
            	 // source v. = right, dest v. = left
            	leftRay = FunnelRay.createLeftRay( vantagePoint, boundary, index, boundaryDestinationVertex );
            	rightRay = FunnelRay.createRightRay( vantagePoint, boundary, index, boundarySourceVertex );
            }
            
            return new Funnel( leftRay, rightRay );
    	}
    	
    	/** Get the left ray
    	 */
    	public FunnelRay getLeftRay() {
    		return leftRay;
    	}
    	
    	/** Get the right ray
    	 */
    	public FunnelRay getRightRay() {
    		return rightRay;
    	}
    	
    	/** Get the vantage point
    	 */
    	public ILocated getVantagePoint() {
    		return leftRay.getVantagePoint();
    	}
    	
    	/** Determine in which zone a point is located
    	 * <p>
    	 * The result is determined by projecting the problem into the XY-plane.
    	 */
    	public FunnelZone determineZone(Point3D point) {
    		
			if (leftRay.isOnOutsideSide(point)){
				return FunnelZone.OUTSIDE_LEFT;
			} else if (rightRay.isOnOutsideSide(point)) {
				return FunnelZone.OUTSIDE_RIGHT;
			} else {
				return FunnelZone.INSIDE;
			}
    	}
    	
    	/** Get the gateway between the two rays
    	 */
		public LineSegment3D getGateway() {
			return new LineSegment3D(
				leftRay.getCrossing().getLocation().asPoint3D(),
				rightRay.getCrossing().getLocation().asPoint3D()
			);
		}
    }