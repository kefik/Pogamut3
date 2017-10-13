package cz.cuni.amis.pogamut.base3d.worldview.object;

import java.beans.PropertyEditorManager;
import java.beans.PropertyEditorSupport;
import java.io.Serializable;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

/**
 * Velocity within the world.
 * 
 * Direction of the velocity is represented as a vector within the world's
 * coordinates. Size of the velocity is represented by length of that vector.
 * 
 * @author Juraj 'Loque' Simlovic
 */
public class Velocity implements ILocomotive, Serializable, Cloneable {
	/**
	 * Velocity representing NONE.
	 */
	public static final Velocity NONE = new Velocity(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
	
	public static final Velocity ZERO = new Velocity();

	static {
		// register property editor, otherwise this class won't be
		// introspectable
		PropertyEditorManager.registerEditor(Velocity.class, Velocity.PropertyEditor.class);
	}

	/**
	 * Property editor for Velocity. Accepts same format as Location.
	 */
	public static class PropertyEditor extends PropertyEditorSupport {

		@Override
		public String getAsText() {
			if (getValue() != null) {
				return getValue().toString();
			} else {
				return "null";
			}
		}

		@Override
		public void setAsText(String s) {
			if ("null".equals(s.trim())) {
				setValue(null);
			} else {
				double[] d = Location.PropertyEditor.parseNumberArray(s);
				if (d.length != 3) {
					throw new IllegalArgumentException();
				}
				setValue(new Velocity(d));
			}
		}
	}

	@Override
	public Velocity clone() {
		return new Velocity(this);
	}

	public Vector3d asVector3d() {
		return new Vector3d(x, y, z);
	}

	public Location asLocation() {
		return new Location(x, y, z);
	}

	/** X coordinate. */
	public final double x;
	/** Y coordinate. */
	public final double y;
	/** Z coordinate. */
	public final double z;
	
	private Integer hashCode = null;

	/* ********************************************************************** */

	/**
	 * X coordinate.
	 * 
	 * @return X coordinate.
	 */
	public double getX() {
		return x;
	}

	/**
	 * Y coordinate.
	 * 
	 * @return Y coordinate.
	 */
	public double getY() {
		return y;
	}

	/**
	 * Z coordinate.
	 * 
	 * @return Z coordinate.
	 */
	public double getZ() {
		return z;
	}

	/* ********************************************************************** */

	/**
	 * Sets the X coordinate.
	 * 
	 * @return new velocity object
	 */
	public Velocity setX(double x) {
		return new Velocity(x, this.y, this.z);
	}

	/**
	 * Sets the Y coordinate.
	 * 
	 * @return new velocity object
	 */
	public Velocity setY(double y) {
		return new Velocity(this.x, y, this.z);
	}

	/**
	 * Sets the Z coordinate.
	 * 
	 * @return new velocity object
	 */
	public Velocity setZ(double z) {
		return new Velocity(this.x, this.y, z);
	}

	/* ********************************************************************* */

	/**
	 * Tells, whether the velocity is zero.
	 * 
	 * @return True if the velocity is 0 in all directions; false otherwise.
	 */
	public boolean isZero() {
		// test all three elements
		return (x == 0) && (y == 0) && (z == 0);
	}

	/**
	 * Tells, whether the velocity is zero (with tolerance of 'epsilon').
	 * 
	 * @return True if the velocity is 0 in all directions; false otherwise.
	 */
	public boolean isZero(double epsilon) {
		// test all three elements
		return (Math.abs(x) < epsilon) && (Math.abs(y) < epsilon) && (Math.abs(z) < epsilon);
	}

	/**
	 * Tells, whether the velocity is zero in planar coordinates.
	 * 
	 * @return True if the velocity is 0 in planar directions; false otherwise.
	 */
	public boolean isPlanarZero() {
		// test two planar elements
		return (x == 0) && (y == 0);
	}

	/**
	 * Retreives size of the velocity.
	 * 
	 * @return Size of the velocity.
	 */
	public double size() {
		// calculate sqare of the size, then sqrt
		return Math.sqrt(x * x + y * y + z * z);
	}

	/**
	 * Retreives squared size of the velocity.
	 * 
	 * @return Size of the velocity to the power of 2.
	 */
	public double sizeSquare() {
		// calculate sqare of the size
		return x * x + y * y + z * z;
	}

	/* ********************************************************************* */

	/**
	 * Retreives normalized vector of the velocity.
	 * 
	 * @return Velocity normalized to the size of 1.
	 */
	public Velocity normalize() {
		// calculate reciprocal value of the size of the vector
		double d = 1 / Math.sqrt(x * x + y * y + z * z);
		// diminish all three directions by the size of the vector
		return new Velocity(x * d, y * d, z * d);
	}

	/**
	 * Negates values of all three coordinates.
	 * 
	 * @return Velocity with all three coordinates negated.
	 */
	public Velocity negate() {
		// create velocity of negative values
		return new Velocity(-x, -y, -z);
	}

	/**
	 * Converts values of all three coordinates to absolute values.
	 * 
	 * @return Velocity with all three coordinates <i>absoluted</i>.
	 */
	public Velocity absolute() {
		// create velocity of absoluted values
		return new Velocity(Math.abs(x), Math.abs(y), Math.abs(z));
	}

	/**
	 * Scales values of all three coordinates by given multiplier.
	 * 
	 * @param d
	 *            Scaling multiplier.
	 * @return Velocity with all three coordinates negated.
	 */
	public Velocity scale(double d) {
		// create velocity with scaled values
		return new Velocity(x * d, y * d, z * d);
	}

	/* ********************************************************************* */

	/**
	 * Computes dot product of this and other given velocity.
	 * 
	 * @param v
	 *            Second velocity to be computed upon.
	 * @return Dot product (scalar product) of the two velocities.
	 */
	public double dot(Velocity v) {
		// calculate dot product of the vectors
		return x * v.x + y * v.y + z * v.z;
	}

	/**
	 * Computes dot product of two given velocities.
	 * 
	 * @param v1
	 *            First velocity to be computed upon.
	 * @param v2
	 *            Second velocity to be computed upon.
	 * @return Dot product (scalar product) of the two velocities.
	 */
	public static double dot(Velocity v1, Velocity v2) {
		// calculate dot product of the vectors
		return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
	}

	/* ********************************************************************* */

	/**
	 * Computes cross product of this and other given velocity.
	 * 
	 * @param v
	 *            Second velocity to be computed upon.
	 * @return Cross product of the two velocities.
	 */
	public Velocity cross(Velocity v) {
		// calculate cross product of the vectors
		return new Velocity(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
	}

	/**
	 * Computes cross product of two given velocities.
	 * 
	 * @param v1
	 *            First velocity to be computed upon.
	 * @param v2
	 *            Second velocity to be computed upon.
	 * @return Cross product of the two velocities.
	 */
	public static Velocity cross(Velocity v1, Velocity v2) {
		// calculate cross product of the vectors
		return new Velocity(v1.y * v2.z - v1.z * v2.y, v1.z * v2.x - v1.x * v2.z, v1.x * v2.y - v1.y * v2.x);
	}

	/* ********************************************************************* */

	/**
	 * Retreives sum of this velocity and given velocity.
	 * 
	 * @param v
	 *            Velocity to by added to this velocity.
	 * @return Sum of the two velocities.
	 */
	public Velocity add(Velocity v) {
		// create sum of the velocities
		return new Velocity(x + v.x, y + v.y, z + v.z);
	}

	/**
	 * Retreives sum of two given velocities.
	 * 
	 * @param v1
	 *            First velocity to by summed.
	 * @param v2
	 *            Second velocity to by summed.
	 * @return Sum of the two velocities.
	 */
	public static Velocity add(Velocity v1, Velocity v2) {
		// create sum of the velocities
		return new Velocity(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
	}

	/**
	 * Retreives subtraction of given velocity from this velocity.
	 * 
	 * @param v
	 *            Velocity to be subtracted.
	 * @return Subtraction of the two velocities.
	 */
	public Velocity sub(Velocity v) {
		// create substraction of the velocities
		return new Velocity(x - v.x, y - v.y, z - v.z);
	}

	/**
	 * Retreives subtraction of two given velocities.
	 * 
	 * @param v1
	 *            Velocity to be subtracted from.
	 * @param v2
	 *            Velocity to be subtracted.
	 * @return Subtraction of the two velocities.
	 */
	public static Velocity sub(Velocity v1, Velocity v2) {
		// create substraction of the velocities
		return new Velocity(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
	}

	/* ********************************************************************* */

	/**
	 * Linearly interpolates between this velocity and given velocity.
	 * 
	 * @param v
	 *            Velocity to be interpolated to.
	 * @param d
	 *            Interpolation parameter.
	 * @return Linear interpolation between the two velocities.
	 */
	public Velocity interpolate(Velocity v, double d) {
		// from the other side
		double d1 = 1.0D - d;
		// create interpolation of the velocities
		return new Velocity(d1 * x + d * v.x, d1 * y + d * v.y, d1 * z + d * v.z);
	}

	/**
	 * Linearly interpolates between two given velocities.
	 * 
	 * @param v1
	 *            Velocity to be interpolated from.
	 * @param v2
	 *            Velocity to be interpolated to.
	 * @param d
	 *            Interpolation parameter.
	 * @return Linear interpolation between the two velocities.
	 */
	public static Velocity interpolate(Velocity v1, Velocity v2, double d) {
		// from the other side
		double d1 = 1.0D - d;
		// create interpolation of the velocities
		return new Velocity(d1 * v1.x + d * v2.x, d1 * v1.y + d * v2.y, d1 * v1.z + d * v2.z);
	}

	/* ********************************************************************* */

	/**
	 * Generates a hashcode for this Velocity.
	 */
	@Override
	public int hashCode() {
		if (hashCode == null) hashCode = computeHashCode();
		return hashCode;
	}

	/**
	 * Tells, whether this velocity equals to the given object.
	 * 
	 * @param obj
	 *            Object to be compared with.
	 * @return True, if object is an instance of Velocity and the velocities
	 *         have the same values of all three corresponding coordinates.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Velocity))
			return false;		
		Velocity other = (Velocity) obj;
		if (hashCode != other.hashCode) return false;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
			return false;
		return true;
	}
	/**
	 * Tells, whether two given velocities equal.
	 * 
	 * @param v1
	 *            First velocity to comapre.
	 * @param v2
	 *            Second velocity to comapre.
	 * @return True, if the velocities have the same values of all three
	 *         corresponding coordinates.
	 */
	public static boolean equal(Velocity v1, Velocity v2) {
		if (v1 == null && v2 == null)
			return true;
		if (v1 == null || v2 == null)
			return false;
		
		return v1.equals(v2);
	}

	/**
	 * Tells, whether the distance between coordinates of this velocity and
	 * given velocity is less than or equal to the given epsilon.
	 * 
	 * @param v
	 *            Velocity to comapre with.
	 * @param epsilon
	 *            Epsilon to compare with.
	 * @return True, if the distance between the velocities is less than the
	 *         epsilon, false otherwise.
	 */
	public boolean equals(Velocity v, double epsilon) {
		double d;

		// x axes distance
		d = x - v.x;
		if ((d >= 0 ? d : -d) > epsilon)
			return false;

		// y axes distance
		d = y - v.y;
		if ((d >= 0.0D ? d : -d) > epsilon)
			return false;

		// z axes distance
		d = z - v.z;
		if ((d >= 0.0D ? d : -d) > epsilon)
			return false;

		// aye, aye, sir..
		return true;
	}

	/**
	 * Tells, whether the distance between coordinates of two given velocities
	 * is less than or equal to the given epsilon.
	 * 
	 * @param v1
	 *            First velocity to comapre.
	 * @param v2
	 *            Second velocity to comapre.
	 * @param epsilon
	 *            Epsilon to compare with.
	 * @return True, if the distance between the velocities is less than the
	 *         epsilon, false otherwise.
	 */
	public static boolean equal(Velocity v1, Velocity v2, double epsilon) {
		double d;

		// x axes distance
		d = v1.x - v2.x;
		if ((d >= 0 ? d : -d) > epsilon)
			return false;

		// y axes distance
		d = v1.y - v2.y;
		if ((d >= 0.0D ? d : -d) > epsilon)
			return false;

		// z axes distance
		d = v1.z - v2.z;
		if ((d >= 0.0D ? d : -d) > epsilon)
			return false;

		// aye, aye, sir..
		return true;
	}

	/* ********************************************************************* */

	/**
	 * Projects the velocity into the (x, y) plane, i.e. removes z coordinate.
	 * 
	 * @return Aligned velocity, with z coordinate set to zero.
	 */
	public Velocity align() {
		// create aligned velocity
		return new Velocity(x, y, 0);
	}

	/**
	 * Computes sideways velocity, i.e. orthogonal velocity to projection of
	 * this velocity to (x, y) plane.
	 * 
	 * <p>
	 * Note: Ignores the z coordinate whatsoever, which is the same as
	 * projecting the velocity to the (x, y) plane. Calculates orthogonal vector
	 * to this projection. Returns vector (y, -x, 0).
	 * 
	 * @return Aligned velocity, with z coordinate set to zero.
	 */
	public Velocity sideways() {
		// create aligned velocity
		return new Velocity(y, -x, 0);
	}

	/* ********************************************************************* */

	/**
	 * Retreives the velocity itself to implement {@link ILocomotive}.
	 * 
	 * @return The velocity itself (note: does not create a copy).
	 */
	@Override
	public Velocity getVelocity() {
		return this;
	}

	/**
	 * Retreives javax.vecmath.Vector3d representation of the velocity.
	 * 
	 * @return javax.vecmath.Vector3d representation with x, y and z values set.
	 */
	public Vector3d getVector3d() {
		return new Vector3d(x, y, z);
	}

	/* ********************************************************************** */

	/**
	 * Creates velocity with all values set to zeroes.
	 */
	private Velocity() {
		this(0,0,0);
	}

	/**
	 * Creates velocity same as the the passed one.
	 * 
	 * @param velocity
	 *            original velocity that will be copied
	 */
	public Velocity(Velocity velocity) {
		this(velocity.getX(), velocity.getY(), velocity.getZ());
	}

	/**
	 * Creates velocity with specified coordinates.
	 * 
	 * @param x
	 *            X coordinate.
	 * @param y
	 *            Y coordinate.
	 * @param z
	 *            Z coordinate.
	 */
	public Velocity(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	private int computeHashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}


	/**
	 * Creates velocity with specified planar coordinates. Sets z to zero.
	 * 
	 * @param x
	 *            X coordinate.
	 * @param y
	 *            Y coordinate.
	 */
	public Velocity(double x, double y) {
		this(x,y,0);
	}

	/**
	 * Creates velocity from array of three doubles. Sets x = d[0], y = d[1] and
	 * z = d[2].
	 * 
	 * @param d
	 *            Array of (at least) three doubles to be used for creation.
	 */
	public Velocity(double d[]) {
		if (d.length >= 1)
			this.x = d[0];
		else 
			this.x = 0;
		if (d.length >= 2)
			this.y = d[1];
		else 
			this.y = 0;
		if (d.length >= 3)
			this.z = d[2];
		else
			this.z = 0;
	}
	
	/**
	 * Creates velocity from specified 3D vector.
	 * 
	 * @param v
	 *            Vector in space to be used for creation.
	 */
	public Velocity(Tuple3d v) {
		this(v.x, v.y, v.z);
	}

	/**
	 * Pattern used to parse {@link Velocity#toString()} in
	 * {@link Velocity#Velocity(String)}.
	 */
	public static final Pattern velocityPattern = Pattern
			.compile("\\[([-+]?[0-9]+(\\.[0-9]+){0,1})\\; ([-+]?[0-9]+(\\.[0-9]+){0,1})\\; ([-+]?[0-9]+(\\.[0-9]+){0,1})\\]");

	/**
	 * Parses the velocity from the "string" generated by
	 * {@link Velocity#toString()}. If it fails, it throws RuntimeException.
	 * 
	 * @param string
	 */
	public Velocity(String string) {
		Matcher m = velocityPattern.matcher(string);
		if (m.find()) {
			String strX = m.group(1);
			String strY = m.group(3);
			String strZ = m.group(5);
			try {
				this.x = Double.parseDouble(strX);
			} catch (Exception e) {
				throw new RuntimeException("String '" + string
						+ "', was not matched as Velocity, because X-coordinate '" + strX + "' is not a number.");
			}
			try {
				this.y = Double.parseDouble(strY);
			} catch (Exception e) {
				throw new RuntimeException("String '" + string
						+ "', was not matched as Velocity, because Y-coordinate '" + strY + "' is not a number.");
			}
			try {
				this.z = Double.parseDouble(strZ);
			} catch (Exception e) {
				throw new RuntimeException("String '" + string
						+ "', was not matched as Velocity, because Z-coordinate '" + strZ + "' is not a number.");
			}
		} else {
			throw new RuntimeException("String '" + string + "' was not matched as Velocity.");
		}
	}

	/* ********************************************************************** */

	@Override
	public String toString() {
		return String.format(Locale.ENGLISH, "[%.2f; %.2f; %.2f]", x, y, z);
	}

}
