/**
 * 
 */

package math.geom3d;

import javax.vecmath.Vector3d;

import math.JavaGeomMath;
import math.geom3d.transform.AffineTransform3D;

/**
 * Define a vector in 3 Dimensions.
 * <p>
 * Immutable.
 */
public class Vector3D {

    // ===================================================================
    // class variables

    protected double x = 1;
    protected double y = 0;
    protected double z = 0;

    // ===================================================================
    // static methods

    /**
     * Computes the dot product of the two vectors, defined by :
     * <p>
     * <code> x1*x2 + y1*y2 + z1*z2</code>
     * <p>
     * Dot product is zero if the vectors defined by the 2 vectors are
     * orthogonal. It is positive if vectors are in the same direction, and
     * negative if they are in opposite direction.
     */
    public final static double dotProduct(Vector3D v1, Vector3D v2) {
        return v1.getX()*v2.getX()+v1.getY()*v2.getY()+v1.getZ()*v2.getZ();
    }

    /**
     * Computes the cross product of the two vectors. Cross product is zero for
     * colinear vectors. It is positive if angle between vector 1 and vector 2
     * is comprised between 0 and PI, and negative otherwise.
     */
    public final static Vector3D crossProduct(Vector3D v1, Vector3D v2) {
        return new Vector3D(v1.y*v2.z-v1.z*v2.y, v1.z*v2.x-v1.x*v2.z, v1.x*v2.y
                -v1.y*v2.x);
    }

    /**
     * test if the two vectors are colinear
     * 
     * @return true if the vectors are colinear
     */
    public final static boolean isColinear(Vector3D v1, Vector3D v2) {
        return Vector3D.crossProduct(v1.getNormalizedVector(),
                v2.getNormalizedVector()).getNorm()<Shape3D.ACCURACY;
    }

    /**
     * test if the two vectors are orthogonal
     * 
     * @return true if the vectors are orthogonal
     */
    public final static boolean isOrthogonal(Vector3D v1, Vector3D v2) {
        return Vector3D.dotProduct(v1.getNormalizedVector(), v2
                .getNormalizedVector())<Shape3D.ACCURACY;
    }

    // ===================================================================
    // constructors

    /** Constructs a new Vector3D initialized with x=1, y=0 and z=0. */
    public Vector3D() {
        this(1, 0, 0);
    }

    /** Base constructor, using coordinates in each direction. */
    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3D(Vector3d vector) {
        this(vector.getX(), vector.getY(), vector.getZ());
    }
    
    /**
     * Construct a new vector between origin and a 3D point.
     */
    public Vector3D(Point3D point) {
        this(point.getX(), point.getY(), point.getZ());
    }

    /**
     * construct a new vector between two points, the vector is leading from the 'point1' to 'point2'.
     */
    public Vector3D(Point3D point1, Point3D point2) {
        this(point2.getX()-point1.getX(), point2.getY()-point1.getY(), point2.getZ()-point1.getZ());
    }

    // ===================================================================
    // inner fields management

    public double getX() {
        return x;
    }

    /** @deprecated Point3D shall be immutable
     */
    @Deprecated
    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    /** @deprecated Point3D shall be immutable
     */
    @Deprecated
    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    /** @deprecated Point3D shall be immutable
     */
    @Deprecated
    public void setZ(double z) {
        this.z = z;
    }

    /** @deprecated Point3D shall be immutable
     */
    @Deprecated
    public void setVector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // ===================================================================
    // basic arithmetic on vectors

    /**
     * Return the sum of current vector with vector given as parameter. Inner
     * fields are not modified.
     */
    public Vector3D plus(Vector3D v) {
        return new Vector3D(x+v.x, y+v.y, z+v.z);
    }

    /**
     * Return the subtraction of current vector with vector given as parameter.
     * Inner fields are not modified.
     */
    public Vector3D minus(Vector3D v) {
        return new Vector3D(x-v.x, y-v.y, z-v.z);
    }

    /**
     * Multiplies this vector by a constant.
     */
    public Vector3D times(double k) {
        return new Vector3D(k*x, k*y, k*z);
    }

    // ===================================================================
    // general operations on vectors

    /**
     * Returns the opposite vector v2 of this, such that the sum of this and v2
     * equals the null vector.
     * 
     * @return the vector opposite to <code>this</code>.
     */
    public Vector3D getOpposite() {
        return new Vector3D(-x, -y, -z);
    }

    /**
     * Computes the norm of the vector
     * 
     * @return the euclidean norm of the vector
     */
    public double getNorm() {
        return JavaGeomMath.computeLength( x, y, z);
    }
    
    /**
     * Alias for {@link Vector3D#getNorm()}.
     * @return
     */
    public double getLength() {
    	return getNorm();
    }

    /**
     * Computes the square of the norm of the vector. This avoids to compute the
     * square root.
     * 
     * @return the euclidean norm of the vector
     */
    public double getNormSq() {
        return x*x+y*y+z*z;
    }

    /**
     * Normalizes the vector, such that its norms becomes 1.
     *
     * @deprecated Point3D shall be immutable
     */
    @Deprecated
    public void normalize() {
        double r = this.getNorm();
        this.x = this.x/r;
        this.y = this.y/r;
        this.z = this.z/r;
    }

    /**
     * Returns the vector with same direction as this one, but with norm equal
     * to 1.
     */
    public Vector3D getNormalizedVector() {
        double r = this.getNorm();
        return new Vector3D(this.x/r, this.y/r, this.z/r);
    }

    /**
     * Transform the vector, by using only the first 4 parameters of the
     * transform. Translation of a vector returns the same vector.
     * 
     * @param trans an affine transform
     * @return the transformed vector.
     */
    public Vector3D transform(AffineTransform3D trans) {
        return new Vector3D(trans.transformPoint(new Point3D(this)));
    }

    // ===================================================================
    // methods implementing Object interface

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vector3D))
            return false;

        Vector3D v = (Vector3D) obj;
        if (Math.abs(x-v.x)>Shape3D.ACCURACY)
            return false;
        if (Math.abs(y-v.y)>Shape3D.ACCURACY)
            return false;
        if (Math.abs(z-v.z)>Shape3D.ACCURACY)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Vector3D( "+x+", "+y+", "+z+")";
    }
    
	public Vector3d asVector3d() {
		return new Vector3d( x, y, z);
	}
}
