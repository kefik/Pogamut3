package math.geom3d.plane;

import java.io.Serializable;

import math.geom3d.Axis3D;
import math.geom3d.Point3D;
import math.geom3d.Shape3D;
import math.geom3d.Vector3D;
import math.geom3d.line.StraightLine3D;

/** Axis aligned plane in 3D
 * 
 * Similar to {@link Plane3D}, but operations are much faster.
 */
public class AxisAlignedPlane3D implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/** Axis to which the plane is perpendicular
	 */
    public final Axis3D axis;
    
    /** Origin point on the axis
     */
    public final double origin;
    
    public AxisAlignedPlane3D( Axis3D axis, double origin ) {
    	this.axis = axis;
    	this.origin = origin;
    }
    
    /** Get coordinate matching the axis perpendicular to the plane 
     */
    public double getAxisCoord( Point3D point ) {
    	return axis.getCoord( point );
    }
    
    /** Get coordinate matching the axis perpendicular to the plane 
     */
    public double getAxisCoord( Vector3D vector ) {
    	return axis.getCoord( vector );
    }
    
    /** Compute intersection of a line with this plane.
     * 
     * @param line line to compute intersection with
     * @return the intersection or null if the line is parallel
     */
    public Point3D getLineIntersection( StraightLine3D line ) {
        double t = getLineIntersectionParametric(line);
        if ( !Double.isInfinite(t) ) {
        	return line.getPoint(t);
        } else {
        	return null;
        }
    }
    
    /** Compute intersection of a line with this plane.
     * 
     * @param line line to compute intersection with
     * @return parametric representation of the intersection in respect to the line or NaN if the line is parallel
     */
    public double getLineIntersectionParametric( StraightLine3D line ) {
    	double distance = origin-getAxisCoord(line.getOrigin());
    	double directionInverse = getAxisCoord(line.getVectorInverse());
    	if ( directionInverse > Shape3D.ACCURACY_INVERSE ) {
    		return Double.NaN;
    	}
    	return distance*directionInverse;
    }
    
    /** Convert to Plane3D
     */
    public Plane3D asPlane3D() {
    	Plane3D referencePlane = null;
    	switch (axis) {
    	case X:
    		referencePlane = Plane3D.yzPlane;
    		break;
    	case Y:
    		referencePlane = Plane3D.xzPlane;
    		break;
    	case Z:
    		referencePlane = Plane3D.xyPlane;
    		break;
    	default:
    		throw new AssertionError("Invalid axis.");
    	}
    	
    	return new Plane3D( getOriginPoint(), referencePlane.getVector1(), referencePlane.getVector2() );
    }
    
    /** Get point of origin
     * 
     * @return a point with axis coordinate equal to origin and other coordinates zeroes 
     */
    public Point3D getOriginPoint() {
    	switch (axis) {
    	case X:
    		return new Point3D( origin, 0, 0 );
    	case Y:
    		return new Point3D( 0, origin, 0 );
    	case Z:
    		return new Point3D( 0, 0, origin );
    	default:
    		throw new AssertionError("Invalid axis.");
    	}
    }
    
    /** Get signed distance from point
     * 
     * @return the axis coordinate of the point minus the plane origin.
     */
    public double getSignedDistance( Point3D point ) {
    	return getAxisCoord(point)-origin;
    }
    
    @Override
    public String toString() {
    	return "AxisAlignedPlane3D( "+axis.name()+", "+origin+" )";
    }
}