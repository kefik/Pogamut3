/**
 * 
 */

package math.geom3d.line;

import java.util.ArrayList;
import java.util.Collection;

import math.JavaGeomMath;
import math.geom3d.Box3D;
import math.geom3d.Point3D;
import math.geom3d.Shape3D;
import math.geom3d.Vector3D;
import math.geom3d.curve.ContinuousCurve3D;
import math.geom3d.curve.Curve3D;
import math.geom3d.plane.Plane3D;
import math.geom3d.transform.AffineTransform3D;

/** Straight line
 * 
 * @author dlegland
 */
public class StraightLine3D implements ContinuousCurve3D {

    private static final long serialVersionUID = 1L;
    
    protected Point3D origin;
    protected Vector3D vector;
    protected transient Vector3D vectorInverse = null; // computed on demand and cached, use getter
    
    // ===================================================================
    // Constructors

    public StraightLine3D() {
    	origin = new Point3D( 0, 0, 0 );
    	vector = new Vector3D( 1, 0, 0 );
    }

    public StraightLine3D(Point3D origin, Vector3D vector) {
        this.origin = origin;
        this.vector = vector;
    }

    /**
     * Constructs a line passing through the 2 points.
     * 
     * @param p1 the first point
     * @param p2 the second point
     */
    public StraightLine3D(Point3D p1, Point3D p2) {
        this(p1, new Vector3D(p1, p2));
    }

    public StraightLine3D(double x0, double y0, double z0, double dx,
            double dy, double dz) {
    	this.origin = new Point3D( x0, y0, z0 );
    	this.vector = new Vector3D( dx, dy, dz );
    }

    // ===================================================================
    // methods specific to StraightLine3D

    public Point3D getOrigin() {
        return origin;
    }
    
    public Point3D getExamplePoint1() {
    	return getOrigin();
    }
    
    public Point3D getExamplePoint2() {
    	return origin.plus(vector);
    }

    public Vector3D getVector() {
        return vector;
    }
    
    public Vector3D getVectorInverse() {
    	if ( vectorInverse == null ) {
    		vectorInverse = new Vector3D( 1.0/vector.getX(), 1.0/vector.getY(), 1.0/vector.getZ() );
    	}
    	return vectorInverse;
    }
    
    public StraightLine3D project(Plane3D plane) {
        return new StraightLine3D(plane.project(getExamplePoint1()), plane.project(getExamplePoint2()));
    }
    
    public Point3D projectPoint(Point3D point) {
    	return getPoint(project(point));
    }

    // ===================================================================
    // methods implementing the Shape3D interface

    @Override
    public Shape3D clip(Box3D box) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean contains(Point3D point) {
        return this.getDistance(point)<Shape3D.ACCURACY;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isBounded() {
        return false;
    }

    @Override
    public Box3D getBoundingBox() {
        Vector3D v = this.getVector();

        // line parallel to (Ox) axis
        if (JavaGeomMath.hypot(v.getY(), v.getZ())<Shape3D.ACCURACY)
            return new Box3D(
        		origin.getX(), origin.getX(),
        		Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
        		Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY
    		);

        // line parallel to (Oy) axis
        if (JavaGeomMath.hypot(v.getX(), v.getZ())<Shape3D.ACCURACY)
            return new Box3D(
        		Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
        		origin.getY(), origin.getY(),
        		Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY
    		);

        // line parallel to (Oz) axis
        if (JavaGeomMath.hypot(v.getX(), v.getY())<Shape3D.ACCURACY)
            return new Box3D(
        		Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
        		Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
        		origin.getZ(), origin.getZ()
    		);

        return new Box3D(
			Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
			Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
			Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY
		);
    }

    @Override
    public double getDistance(Point3D p) {
        Vector3D vl = this.getVector();
        Vector3D vp = new Vector3D(this.getOrigin(), p);
        return Vector3D.crossProduct(vl, vp).getNorm()/vl.getNorm();
    }

    @Override
    public StraightLine3D transform(AffineTransform3D trans) {
        return new StraightLine3D(getOrigin().transform(trans), getVector().transform(trans));
    }

    @Override
    public Point3D getFirstPoint() {
        return new Point3D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    @Override
    public Point3D getLastPoint() {
        return new Point3D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    @Override
    public Point3D getPoint(double t) {
    	return new Point3D(
    		origin.getX()+t*vector.getX(),
	        origin.getY()+t*vector.getY(),
	        origin.getZ()+t*vector.getZ()
        );
    }

    /** @deprecated Point3D shall be immutable
     */
    @Deprecated
    public Point3D getPoint(double t, Point3D point) {
        if (point==null)
            point = new Point3D();
        point.setX(origin.getX()+t*vector.getX());
        point.setY(origin.getY()+t*vector.getY());
        point.setZ(origin.getZ()+t*vector.getZ());
        return point;
    }

    @Override
    public double getPosition(Point3D point) {
        return project(point);
    }

    @Override
    public StraightLine3D getReverseCurve() {
        return new StraightLine3D(getOrigin(), getVector().getOpposite());
    }

    /**
     * Returns an empty array of Point3D.
     */
    public Collection<Point3D> getSingularPoints() {
        return new ArrayList<Point3D>(0);
    }

    @Override
    public Curve3D getSubCurve(double t0, double t1) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Returns -INFINITY;
     */
    public double getT0() {
        return Double.NEGATIVE_INFINITY;
    }

    /**
     * Returns +INFINITY;
     */
    public double getT1() {
        return Double.POSITIVE_INFINITY;
    }

    /**
     * Compute the position of the orthogonal projection of the given point on
     * this line.
     */
    public double project(Point3D point) {
        Vector3D vl = this.getVector();
        Vector3D vp = new Vector3D(this.getOrigin(), point);
        return Vector3D.dotProduct(vl, vp)/vl.getNormSq();
    }
    
    /** Compute intersection with a plane
     * 
     * @param plane plane to compute intersection with
     * @return parametric representation of the intersection or NaN
     */
    public double getPlaneIntersectionParametric(Plane3D plane) {
        // the plane normal
        Vector3D planeNormal = plane.getNormalVector();

        double normalLineDot = Vector3D.dotProduct(planeNormal, getVector());
        if ( Math.abs(normalLineDot) < Shape3D.ACCURACY ) {
        	// right angle between plane normal and line vector => line is parallel to the plane
        	return Double.NaN;
        }
        
        // the difference between origin of plane and origin of line
        Vector3D dp = new Vector3D(getOrigin(), plane.getOrigin());
        
        // compute ratio of dot products,
        return Vector3D.dotProduct(planeNormal, dp) / normalLineDot;
    }
    
    /** Compute intersection with a plane.
     * 
     * @param plane plane to compute intersection with
     * @return the intersection or null if the line is parallel
     */
    public Point3D getPlaneIntersection(Plane3D plane) {
        double t = getPlaneIntersectionParametric(plane);
        if ( !Double.isInfinite(t) && !Double.isNaN(t) ) {
        	return getPoint(t);
        } else {
        	return null;
        }
    }

    public Collection<StraightLine3D> getContinuousCurves() {
        ArrayList<StraightLine3D> array = new ArrayList<StraightLine3D>(1);
        array.add(this);
        return array;
    }
}
