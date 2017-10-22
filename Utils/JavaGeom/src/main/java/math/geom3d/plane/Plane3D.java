/**
 * 
 */

package math.geom3d.plane;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import math.geom3d.Box3D;
import math.geom3d.Point3D;
import math.geom3d.Shape3D;
import math.geom3d.Vector3D;
import math.geom3d.transform.AffineTransform3D;

/** Plane in 3D space
 * 
 * Plane in 3D space defined by origin and two linearly independent vectors "vector1" and "vector2".
 * Defines internal 2D coordinate system. Vector1 is the X Axis. The Y axis is computed by orthogonalization of vector2 against vector1.  
 */
public class Plane3D implements Shape3D {

    private static final long serialVersionUID = 1L;
    
    protected Point3D origin;
    protected Vector3D vector1;
    protected Vector3D vector2;
    protected transient Plane3DCoordinateSubsystem coordinateSubsystem = null; // computed on demand and cached, use getter
    protected transient Vector3D normal = null; // computed on demand and cached, use getter
    
    public static final Plane3D xyPlane = new Plane3D(
        new Point3D(0, 0, 0),
        new Vector3D(0, 1, 0),
        new Vector3D(1, 0, 0)
    );

    public static final Plane3D xzPlane = new Plane3D(
        new Point3D(0, 0, 0),
        new Vector3D(1, 0, 0),
        new Vector3D(0, 0, 1)
    );

    public static final Plane3D yzPlane = new Plane3D(
        new Point3D(0, 0, 0),
        new Vector3D(0, 0, 1),
        new Vector3D(0, 1, 0)
    );
    
    /** Construct from points in plane
     * 
     * Picks 3 anchors from provided list of points, see {@link #pickAnchors(Collection)}
     * 
     * @param pointsInPlane points that lie in the constructed plane, must contain at least 3 linearly independent points
     */
    public static Plane3D createPlaneDefinedByPoints(Collection<Point3D> pointsInPlane) {
        Point3D[] anchors = pickAnchors(pointsInPlane);
        return new Plane3D( 
            anchors[0],
            new Vector3D(anchors[0],anchors[1]),
            new Vector3D(anchors[0],anchors[2]) 
        );
    }
    
    /** Construct from origin and vectors
     * 
     * @param origin point within the plane
     * @param vector1 first vector must be linearly independent of vector2
     * @param vector2 second vector must be linearly independent of vector1
     */
    public Plane3D(Point3D origin, Vector3D vector1, Vector3D vector2) {
        this.origin = origin;
        this.vector1 = vector1;
        this.vector2 = vector2;
    }
        
    /** Get origin
     * 
     * @return origin, point within the plane that is also origin of its internal coordinate system
     */
    public Point3D getOrigin() {
        return origin;
    }
    
    /** Get vector1
     * 
     * @return the first original vector used to define the plane
     */
    public Vector3D getVector1() {
        return vector1;
    }

    /** Get vector2
     * 
     * @return the second original vector used to define the plane
     */
    public Vector3D getVector2() {
        return vector2;
    }

    /** Get the normal vector
     * <p>
     * Normal is normalized.
     */
    public Vector3D getNormalVector() {
    	if ( normal == null ) {
    		normal = Vector3D.crossProduct(vector1, vector2).getOpposite().getNormalizedVector();
    	}
    	return normal;
    }
    
    /** Project a point onto plane
     * 
     * @param point point to project
     * @return point within plane that lies in direction of normal vector from input point
     */
    public Point3D project(Point3D point) {
        Vector3D planeNormal = getNormalVector();
        
        // the difference between origin of plane and the point
        Vector3D dp = new Vector3D( point, getOrigin() );
        
        // compute ratio of dot products,
        double t = Vector3D.dotProduct(planeNormal, dp);
        return new Point3D(
        		point.getX()+t*planeNormal.getX(),
        		point.getY()+t*planeNormal.getY(),
        		point.getZ()+t*planeNormal.getZ()
        );
    }

    /** Project a vector onto plane
     * 
     * See {@link #project(Point3D)}.
     */
    public Vector3D project(Vector3D vector) {
        return new Vector3D(project(new Point3D(vector)));
    }  
    
    /** Get coordinate subsystem.
     * 
     * @see {@link Plane3DCoordinateSubsystem}
     */
    public Plane3DCoordinateSubsystem getCoordinateSubsystem() {
        if (coordinateSubsystem == null) {
            coordinateSubsystem = new Plane3DCoordinateSubsystem(this);
        }
        return coordinateSubsystem;
    }
    
    @Override
    public Shape3D clip(Box3D box) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Point3D point) {
        Point3D proj = this.project(point);
        return (point.getDistanceSquare(proj)<(Shape3D.ACCURACY*Shape3D.ACCURACY));
    }

    @Override
    public Box3D getBoundingBox() {
        // plane parallel to XY plane
        if (Math.abs(vector1.getZ())<Shape3D.ACCURACY&&Math.abs(vector2.getZ())<Shape3D.ACCURACY)
            return new Box3D(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
                origin.getZ(), origin.getZ());

        // plane parallel to YZ plane
        if (Math.abs(vector1.getX())<Shape3D.ACCURACY&&Math.abs(vector2.getX())<Shape3D.ACCURACY)
            return new Box3D(origin.getX(), origin.getX(),
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

        // plane parallel to XZ plane
        if (Math.abs(vector1.getY())<Shape3D.ACCURACY&&Math.abs(vector2.getY())<Shape3D.ACCURACY)
            return new Box3D(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
                origin.getY(), origin.getY(),
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

        return new Box3D(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    /** Get signed distance determined by normal vector
     * 
     * @param point point to get signed distance of
     * @return positive distance if point is in direction of the normal vector
     */
    public double getSignedDistance(Point3D point) {
        Point3D projectedPoint = project(point);
        Vector3D pointOffset = new Vector3D(projectedPoint, point);
        double dotProduct = Vector3D.dotProduct(getNormalVector(), pointOffset);
        return point.getDistance(projectedPoint)*Math.signum(dotProduct);
    }
    
    @Override
    public double getDistance(Point3D point) {
        return point.getDistance(project(point));
    }

    @Override
    public boolean isBounded() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Shape3D transform(AffineTransform3D trans) {
        return new Plane3D(
            this.getOrigin().transform(trans),
            this.getVector1().transform(trans),
            this.getVector2().transform(trans)
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Plane3D) {
            Plane3D plane = (Plane3D) obj;
            
            return (
                origin.equals(plane.origin)
                &&
                vector1.equals(plane.vector1)
                &&
                vector2.equals(plane.vector2)
            );
        } else {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return "Plane3D( origin: "+origin+", vector1: "+vector1+ " vector2: "+vector2+")";
    }
    
    /** Pick anchors of a plane
     *      
     * Picks 3 anchors that define the plane well (see {@link #rateAnchors(Point3D, Point3D, Point3D)})..
     *   
     * @param pointsInPlane points in a plane
     * @return array of 3 anchors
     */
    public static Point3D[] pickAnchors(Collection<Point3D> pointsInPlane)
    {
    	if (pointsInPlane.size() == 3) {
    		return pointsInPlane.toArray(new Point3D[3]);
    	}
    	
        ArrayList<Point3D> points = new ArrayList<Point3D>(pointsInPlane);
        
        final Point3D a = points.remove(0);
        
        Collections.sort(
            points, 
            new Comparator<Point3D>(){
                @Override
                public int compare(Point3D lhs, Point3D rhs) {
                    return Double.compare( a.getDistanceSquare(lhs), a.getDistanceSquare(rhs) );
                }
            }
        );
        
        final Point3D b = points.remove(points.size()-1);
        
        Collections.sort(
            points, 
            new Comparator<Point3D>() {
                @Override
                public int compare(Point3D lhs, Point3D rhs) {
                    return Double.compare( rateAnchors(a, b, lhs), rateAnchors(a, b, rhs) );
                }
            }
        );
        
        final Point3D c = points.remove(points.size()-1);
                    
        return new Point3D[]{a, b, c};
    }
    
    /** Rate the ability of points to define a plane
     * 
     * Anchors need to be distant from each other (to avoid floating point math inaccuracies) and must be linearly independent.
     */
    protected static double rateAnchors(Point3D a, Point3D b, Point3D c) {
        Vector3D ab = new Vector3D(a, b);
        Vector3D ac = new Vector3D(a, c);
        Vector3D abn = ab.getNormalizedVector();
        Vector3D acn = ac.getNormalizedVector();
        double linearIndependencyFactor = Math.min( abn.minus(acn).getLength(), abn.plus(acn).getLength() );
        double distanceFactor = Math.min(ab.getLength(), ac.getLength());
        return distanceFactor*linearIndependencyFactor;
    }
}
