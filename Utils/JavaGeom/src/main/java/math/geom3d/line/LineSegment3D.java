/**
 * 
 */

package math.geom3d.line;

import java.util.ArrayList;
import java.util.Collection;

import math.geom3d.Box3D;
import math.geom3d.Point3D;
import math.geom3d.Shape3D;
import math.geom3d.Vector3D;
import math.geom3d.curve.ContinuousCurve3D;
import math.geom3d.curve.Curve3D;
import math.geom3d.transform.AffineTransform3D;
import math.utils.MathUtils;

/**
 * @author dlegland
 */
public class LineSegment3D implements ContinuousCurve3D {

    // ===================================================================
    // class variables
	
	private static final long serialVersionUID = 1L;
	
	protected Point3D firstPoint;
    protected Point3D lastPoint;
    
    // ===================================================================
    // constructors

    public LineSegment3D(Point3D p1, Point3D p2) {
        this.firstPoint = p1;
        this.lastPoint = p2;
    }
    
    
    public StraightLine3D getSupportingLine() {
        return new StraightLine3D( firstPoint, lastPoint );
    }
    
    public Point3D projectPoint(Point3D point) {
    	return getPoint(project(point));
    }

    // ===================================================================
    // methods implementing the Curve3D interface

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.curve.Curve3D#getContinuousCurves()
     */
    public Collection<LineSegment3D> getContinuousCurves() {
        ArrayList<LineSegment3D> array = new ArrayList<LineSegment3D>(1);
        array.add(this);
        return array;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.curve.Curve3D#getFirstPoint()
     */
    public Point3D getFirstPoint() {
        return firstPoint;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.curve.Curve3D#getLastPoint()
     */
    public Point3D getLastPoint() {
        return lastPoint;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.curve.Curve3D#getPoint(double)
     */
    public Point3D getPoint(double t) {
        return new Point3D(
    		firstPoint.getX()+(firstPoint.getX()-lastPoint.getX())*t,
    		firstPoint.getY()+(firstPoint.getY()-lastPoint.getY())*t,
    		firstPoint.getZ()+(firstPoint.getZ()-lastPoint.getZ())*t
    	);
    }
    
    @Deprecated
    public Point3D getPoint(double t, Point3D point) {
        if (point==null)
            point = new Point3D();
        t = Math.max(Math.min(t, 1), 0);
        point.setLocation(
    		firstPoint.getX()+(firstPoint.getX()-lastPoint.getX())*t,
    		firstPoint.getY()+(firstPoint.getY()-lastPoint.getY())*t,
    		firstPoint.getZ()+(firstPoint.getZ()-lastPoint.getZ())*t        		
        );
        return point;
    }

    /**
     * If point does not project on the line segment, return Double.NaN.
     * 
     * @see math.geom3d.curve.Curve3D#getPosition(math.geom3d.Point3D)
     */
    public double getPosition(Point3D point) {
        double t = this.getSupportingLine().getPosition(point);
        if (t>1)
            return Double.NaN;
        if (t<0)
            return Double.NaN;
        return t;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.curve.Curve3D#getReverseCurve()
     */
    public Curve3D getReverseCurve() {
        return new StraightLine3D(getLastPoint(), getFirstPoint());
    }

    /**
     * Returns the2 end points.
     * 
     * @see math.geom3d.curve.Curve3D#getSingularPoints()
     */
    public Collection<Point3D> getSingularPoints() {
        ArrayList<Point3D> points = new ArrayList<Point3D>(2);
        points.add(getFirstPoint());
        points.add(getLastPoint());
        return points;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.curve.Curve3D#getSubCurve(double, double)
     */
    public LineSegment3D getSubCurve(double t0, double t1) {
        t0 = Math.max(t0, 0);
        t1 = Math.min(t1, 1);
        return new LineSegment3D(getPoint(t0), getPoint(t1));
    }

    /**
     * Return 0, by definition of LineSegment.
     * 
     * @see math.geom3d.curve.Curve3D#getT0()
     */
    public double getT0() {
        return 0;
    }

    /**
     * Return 1, by definition of LineSegment.
     * 
     * @see math.geom3d.curve.Curve3D#getT1()
     */
    public double getT1() {
        return 1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.curve.Curve3D#project(math.geom3d.Point3D)
     */
    public double project(Point3D point) {
        double t = getSupportingLine().project(point);
        return Math.min(Math.max(t, 0), 1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.curve.Curve3D#transform(math.geom3d.transform.AffineTransform3D)
     */
    public Curve3D transform(AffineTransform3D trans) {
        return new LineSegment3D( firstPoint.transform(trans), lastPoint.transform(trans) );
    }

    // ===================================================================
    // methods implementing the Shape3D interface

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.Shape3D#clip(math.geom3d.Box3D)
     */
    public Shape3D clip(Box3D box) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.Shape3D#contains(math.geom3d.Point3D)
     */
    public boolean contains(Point3D point) {
        StraightLine3D line = this.getSupportingLine();
        if (!line.contains(point))
            return false;
        double t = line.getPosition(point);
        if (t<-Shape3D.ACCURACY)
            return false;
        if (t>1+Shape3D.ACCURACY)
            return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.Shape3D#getBoundingBox()
     */
    public Box3D getBoundingBox() {
        return new Box3D(
    		firstPoint.getX(), lastPoint.getX(),
    		firstPoint.getY(), lastPoint.getY(),
    		firstPoint.getZ(), lastPoint.getZ()
    	);
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.Shape3D#getDistance(math.geom3d.Point3D)
     */
    public double getDistance(Point3D point) {
        double t = this.project(point);
        return getPoint(t).getDistance(point);
    }
    
    /**
     * Returns distance of this segment from 'segment'.
     * <p><p>
     * Based on: http://softsurfer.com/Archive/algorithm_0106/ 
     * <p>
     * dist3D_Segment_to_Segment()
     * 
     * @param segment
     * @return
     */
    public double getDistance(LineSegment3D segment) {
    	LineSegment3D thisSegment = this;
        Vector3D u = new Vector3D(thisSegment.getFirstPoint(), thisSegment.getLastPoint());
        Vector3D v = new Vector3D(segment.getFirstPoint(), segment.getLastPoint());
        Vector3D w = new Vector3D(segment.getFirstPoint(), thisSegment.getFirstPoint());
        double   a = Vector3D.dotProduct(u,u);        // always >= 0
        double   b = Vector3D.dotProduct(u,v);
        double   c = Vector3D.dotProduct(v,v);        // always >= 0
        double   d = Vector3D.dotProduct(u,w);
        double   e = Vector3D.dotProduct(v,w);
        double   D = a*c - b*b;       // always >= 0
        double   sc, sN, sD = D;      // sc = sN / sD, default sD = D >= 0
        double   tc, tN, tD = D;      // tc = tN / tD, default tD = D >= 0

        // compute the line parameters of the two closest points
        if (D < MathUtils.EPSILON) { // the lines are almost parallel
            sN = 0.0;        // force using point P0 on segment S1
            sD = 1.0;        // to prevent possible division by 0.0 later
            tN = e;
            tD = c;
        }
        else {                // get the closest points on the infinite lines
            sN = (b*e - c*d);
            tN = (a*e - b*d);
            if (sN < 0.0) {       // sc < 0 => the s=0 edge is visible
                sN = 0.0;
                tN = e;
                tD = c;
            }
            else if (sN > sD) {  // sc > 1 => the s=1 edge is visible
                sN = sD;
                tN = e + b;
                tD = c;
            }
        }

        if (tN < 0.0) {           // tc < 0 => the t=0 edge is visible
            tN = 0.0;
            // recompute sc for this edge
            if (-d < 0.0)
                sN = 0.0;
            else if (-d > a)
                sN = sD;
            else {
                sN = -d;
                sD = a;
            }
        }
        else if (tN > tD) {      // tc > 1 => the t=1 edge is visible
            tN = tD;
            // recompute sc for this edge
            if ((-d + b) < 0.0)
                sN = 0;
            else if ((-d + b) > a)
                sN = sD;
            else {
                sN = (-d + b);
                sD = a;
            }
        }
        // finally do the division to get sc and tc
        sc = (Math.abs(sN) < MathUtils.EPSILON ? 0.0 : sN / sD);
        tc = (Math.abs(tN) < MathUtils.EPSILON ? 0.0 : tN / tD);

        // get the difference of the two closest points
        //       dP = w + (sc * u) - (tc * v);
        Vector3D dP = w.plus(u.times(sc).minus(v.times(tc)));  // = S1(sc) - S2(tc)

        return dP.getLength();   // return the closest distance
    }

    /**
     * Returns true, as a LineSegment3D is always bounded.
     * 
     * @see math.geom3d.Shape3D#isBounded()
     */
    public boolean isBounded() {
        return true;
    }

    /**
     * Returns false, as a LineSegment3D is never empty.
     * 
     * @see math.geom3d.Shape3D#isEmpty()
     */
    public boolean isEmpty() {
        return false;
    }

    public double getLength() {
        return getFirstPoint().getDistance( getLastPoint() );
    }
}
