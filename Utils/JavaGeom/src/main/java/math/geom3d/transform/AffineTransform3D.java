/* file : AffineTransform3D.java
 * 
 * Project : geometry
 *
 * ===========================================
 * 
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or (at
 * your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY, without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. if not, write to :
 * The Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 * 
 * Created on 27 nov. 2005
 *
 */

package math.geom3d.transform;

import java.util.ArrayList;
import java.util.Collection;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import math.geom3d.Point3D;
import math.geom3d.Shape3D;
import math.geom3d.Vector3D;

/**
 * @author dlegland
 */
public class AffineTransform3D implements Bijection3D {

    // TODO make the class immutable

    /** Transformation matrix
     * 
     * Last column is translation. Point3D is treated as (x, y, z, 1).
     * Last row is (0, 0, 0, 1)
     */
    protected Matrix4d matrix; 
    
    static final Matrix4d identity;
    static {
        identity = new Matrix4d();
        identity.setIdentity();
    }
    
    // ===================================================================
    // public static methods

    public static AffineTransform3D createTranslation(Vector3D vec) {
        return createTranslation(vec.getX(), vec.getY(), vec.getZ());
    }

    public static AffineTransform3D createTranslation(double x, double y, double z) {
        return new AffineTransform3D(1, 0, 0, x, 0, 1, 0, y, 0, 0, 1, z);
    }

    public static AffineTransform3D createRotationOx(double theta) {
        Matrix3d matrix3d = new Matrix3d();
        matrix3d.rotX(theta);
        Matrix4d matrix4d = new Matrix4d();
        matrix4d.set(matrix3d);
        return new AffineTransform3D(matrix4d);
    }

    public static AffineTransform3D createRotationOy(double theta) {
        Matrix3d matrix3d = new Matrix3d();
        matrix3d.rotY(theta);
        Matrix4d matrix4d = new Matrix4d();
        matrix4d.set(matrix3d);
        return new AffineTransform3D(matrix4d);
    }

    public static AffineTransform3D createRotationOz(double theta) {
        Matrix3d matrix3d = new Matrix3d();
        matrix3d.rotZ(theta);
        Matrix4d matrix4d = new Matrix4d();
        matrix4d.set(matrix3d);
        return new AffineTransform3D(matrix4d);
    }

    public static AffineTransform3D createScaling(double s) {
        return createScaling(s, s, s);
    }
    
    public static AffineTransform3D createScaling(Vector3D scalingVector) {
        return createScaling(scalingVector.getX(), scalingVector.getY(), scalingVector.getZ());
    }
    
    public static AffineTransform3D createScaling(double sx, double sy, double sz) {
        return new AffineTransform3D(sx, 0, 0, 0, 0, sy, 0, 0, 0, 0, sz, 0);
    }

    // ===================================================================
    // constructors

    /** Creates a new affine transform3D set to identity */
    public AffineTransform3D() {
        matrix = new Matrix4d();
        matrix.setIdentity();
    }

    public AffineTransform3D(double[] coeficients) {
        matrix = new Matrix4d();  	
        if (coeficients.length==9) {
            matrix.set(new Matrix3d(coeficients));
        } else if (coeficients.length==12) {
            double[] extendedCoeficients = new double[16];
            for (int i=0; i<coeficients.length; ++i) {
                extendedCoeficients[i] = coeficients[i];
            }
            extendedCoeficients[12] = 0;
            extendedCoeficients[13] = 0;
            extendedCoeficients[14] = 0;
            extendedCoeficients[15] = 1;
            matrix.set(extendedCoeficients);
        }
    }

    public AffineTransform3D(
        double xx, double yx, double zx, double tx,
        double xy, double yy, double zy, double ty,
        double xz, double yz, double zz, double tz
    ) {
        matrix = new Matrix4d();
        matrix.m00 = xx;
        matrix.m01 = yx;
        matrix.m02 = zx;
        matrix.m03 = tx;
        matrix.m10 = xy;
        matrix.m11 = yy;
        matrix.m12 = zy;
        matrix.m13 = ty;
        matrix.m20 = xz;
        matrix.m21 = yz;
        matrix.m22 = zz;
        matrix.m23 = tz;
        matrix.m30 = 0;
        matrix.m31 = 0;
        matrix.m32 = 0;
        matrix.m33 = 1;
    }

    // protected, because the last row could be malformed and matrix could be later modified from outside
    protected AffineTransform3D(Matrix4d matrix) {
        this.matrix = matrix;
    }

    // ===================================================================
    // accessors
    
    public boolean isIdentity() {  	 
        return matrix.equals( identity );
    }

    /** Get matrix representing the transformation
     */
    public Matrix4d getMatrix() {
        return new Matrix4d(matrix);
    }
    
    /**
     * Computes the inverse affine transform.
     */
    public AffineTransform3D getInverseTransform() {
        Matrix4d invertedMatrix = new Matrix4d(matrix);
        invertedMatrix.invert();
        return new AffineTransform3D(invertedMatrix);
    }

    // ===================================================================
    // mutators

    /**
     * @deprecated AffineTransform3d is immutable (0.6.3)
     */
    @Deprecated
    public void setTransform(
        double n00, double n01, double n02, double n03,
        double n10, double n11, double n12, double n13,
        double n20, double n21, double n22, double n23
    ) {
        matrix.m00 = n00;
        matrix.m01 = n01;
        matrix.m02 = n02;
        matrix.m03 = n03;
        matrix.m10 = n10;
        matrix.m11 = n11;
        matrix.m12 = n12;
        matrix.m13 = n13;
        matrix.m20 = n20;
        matrix.m21 = n21;
        matrix.m22 = n22;
        matrix.m23 = n23;
    }

    /**
     * @deprecated AffineTransform3d is immutable (0.6.3)
     */
    @Deprecated
    public void setTransform(AffineTransform3D trans) {
        matrix.set(trans.matrix);
    }

    /**
     * @deprecated AffineTransform3d is immutable (0.6.3)
     */
    @Deprecated
    public void setToIdentity() {
        matrix.setIdentity();
    }

    /** Concatenate transformations
     * 
     * @param second Transformation which is intended to be applied second in the resulting concatenated transformation. 
     * @return concatentated transformation
     */
    public AffineTransform3D concatenate(AffineTransform3D second) {
        Matrix4d concatenatedMatrix = new Matrix4d(second.matrix);
        concatenatedMatrix.mul(matrix);
        return new AffineTransform3D(concatenatedMatrix);
    }

    /**
     * @deprecated shapes are responsible of their transform (0.6.3)
     */
    @Deprecated
    public Shape3D transform(Shape3D shape) {
        return shape.transform(this);
    }

    public ArrayList<Point3D> transformPoints(Collection<Point3D> points) {
        ArrayList<Point3D> retval = new ArrayList<Point3D>(points.size());
        
        for ( Point3D point : points ) {
            retval.add(transformPoint(point));
        }
        
        return retval;
    }

    public Point3D transformPoint(Point3D point) {
        javax.vecmath.Point3d retval = new javax.vecmath.Point3d(point.getX(), point.getY(), point.getZ());
        matrix.transform(retval);
        return new Point3D(retval);
    }
    
    public Vector3D transformVector(Vector3D vector) {
    	return new Vector3D( transformPoint( new Point3D(vector) ) );
    }

    /**
     * Compares two transforms. Returns true if all inner fields are equal up to
     * the precision given by Shape3D.ACCURACY.
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof AffineTransform3D) {
            Matrix4d otherMatrix = ((AffineTransform3D) other).matrix;
            return (
                Math.abs(matrix.m00 - otherMatrix.m00)<= Shape3D.ACCURACY
                &&
                Math.abs(matrix.m01 - otherMatrix.m01)<= Shape3D.ACCURACY
                &&
                Math.abs(matrix.m02 - otherMatrix.m02)<= Shape3D.ACCURACY
                &&
                Math.abs(matrix.m03 - otherMatrix.m03)<= Shape3D.ACCURACY
                &&
                Math.abs(matrix.m10 - otherMatrix.m10)<= Shape3D.ACCURACY
                &&
                Math.abs(matrix.m11 - otherMatrix.m11)<= Shape3D.ACCURACY
                &&
                Math.abs(matrix.m12 - otherMatrix.m12)<= Shape3D.ACCURACY
                &&
                Math.abs(matrix.m13 - otherMatrix.m13)<= Shape3D.ACCURACY
                &&
                Math.abs(matrix.m20 - otherMatrix.m20)<= Shape3D.ACCURACY
                &&
                Math.abs(matrix.m21 - otherMatrix.m21)<= Shape3D.ACCURACY
                &&
                Math.abs(matrix.m22 - otherMatrix.m22)<= Shape3D.ACCURACY
                &&
                Math.abs(matrix.m23 - otherMatrix.m23)<= Shape3D.ACCURACY
                // rest should be ( 0, 0, 0, 1 ) in both matrices
            );
        } else {
            return false;
        }
    }
}
