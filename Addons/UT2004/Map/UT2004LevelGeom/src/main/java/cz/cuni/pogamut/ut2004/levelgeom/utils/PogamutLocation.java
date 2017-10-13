package cz.cuni.pogamut.ut2004.levelgeom.utils;

import java.beans.PropertyEditorManager;
import java.beans.PropertyEditorSupport;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

import math.geom3d.Point3D;

/**
 * Location within the world.
 * 
 * Location is represented as a point within the world's coordinates.
 * 
 * @author Juraj 'Loque' Simlovic
 * @author Radek 'Black_Hand' Pibil
 */
public class PogamutLocation {

	/**
	 * Location representing NONE.
	 */
	public static final PogamutLocation NONE = new PogamutLocation(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
	
	/**
	 * Location(0,0,0).
	 */
	public static final PogamutLocation ZERO = new PogamutLocation();

	/**
	 * This here is for StoryFactory compatibility reasons. Can be removed in
	 * 2012.
	 */
	static final long serialVersionUID = -7001866845605943889L;

	/**
	 * We have to register PropertyEditor for Location, without it, Location
	 * won't be introspectable.
	 */
	static {
		PropertyEditorManager.registerEditor(PogamutLocation.class, PogamutLocation.PropertyEditor.class);
	}

	/**
	 * PropertyEditor for class Location.
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
				double[] d = PogamutLocation.PropertyEditor.parseNumberArray(s);
				if (d.length != 3) {
					throw new IllegalArgumentException();
				}
				setValue(new PogamutLocation(d));
			}
		}

		public static double[] parseNumberArray(String s) {
			s = s.trim();
			// if string has brackets, remove them
			if ((s.startsWith("[") && s.endsWith("]")) || (s.startsWith("(") && s.endsWith(")"))) {
				s = s.substring(1, s.length() - 1);
			}
			// now we expect num , num, num
			StringTokenizer st = new StringTokenizer(s, ";");

			// try to parse numbers and set the new value
			try {
				double[] d = new double[st.countTokens()];
				for (int i = 0; i < d.length; ++i) {
					d[i] = Double.parseDouble(st.nextToken());
				}
				return d;
			} catch (NumberFormatException ex) {
				throw new IllegalArgumentException(ex);
			}
		}

		@Override
		public boolean supportsCustomEditor() {
			return false;
		}
	}

	@Override
	public PogamutLocation clone() {
		return new PogamutLocation(this);
	}

	public Vector3d asVector3d() {
		return new Vector3d(x, y, z);
	}

	public Point3d asPoint3d() {
		return new Point3d(x, y, z);
	}

	public Point3D asPoint3D() {
		return new Point3D(x, y, z);
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
	 * Retreives sum of this location and given location.
	 * 
	 * @param l
	 *            Location to be added to this location.
	 * @return Sum of the two locations.
	 */
	public PogamutLocation add(PogamutLocation l) {
		// create sum of the locations
		return new PogamutLocation(x + l.x, y + l.y, z + l.z);
	}

	/**
	 * Retreives sum of two given locations.
	 * 
	 * @param l1
	 *            First location to be summed.
	 * @param l2
	 *            Second location to be summed.
	 * @return Sum of the two locations.
	 */
	public static PogamutLocation add(PogamutLocation l1, PogamutLocation l2) {
		// create sum of the locations
		return new PogamutLocation(l1.x + l2.x, l1.y + l2.y, l1.z + l2.z);
	}

	/**
	 * Retreives subtraction of given location from this location.
	 * 
	 * @param l
	 *            Location to be subtracted.
	 * @return Subtraction of the two locations.
	 */
	public PogamutLocation sub(PogamutLocation l) {
		// create substraction of the locations
		return new PogamutLocation(x - l.x, y - l.y, z - l.z);
	}

	/**
	 * Retreives subtraction of two given locations.
	 * 
	 * @param l1
	 *            Location to be subtracted from.
	 * @param l2
	 *            Location to be subtracted.
	 * @return Subtraction of the two locations.
	 */
	public static PogamutLocation sub(PogamutLocation l1, PogamutLocation l2) {
		// create substraction of the locations
		return new PogamutLocation(l1.x - l2.x, l1.y - l2.y, l1.z - l2.z);
	}

	/* ********************************************************************* */

	/**
	 * Scales values of all three coordinates by given multiplier.
	 * 
	 * @param d
	 *            Scaling multiplier.
	 * @return Location with all three coordinates negated.
	 */
	public PogamutLocation scale(double d) {
		// create location with scaled values
		return new PogamutLocation(x * d, y * d, z * d);
	}

	/* ********************************************************************* */

	/**
	 * Lineary interpolates between this location and given location.
	 * 
	 * @param l
	 *            Location to be interpolated to.
	 * @param d
	 *            Interpolation parameter.
	 * @return Linear interpolation between the two locations.
	 */
	public PogamutLocation interpolate(PogamutLocation l, double d) {
		// from the other side
		double d1 = 1.0D - d;
		// create interpolation of the locations
		return new PogamutLocation(d1 * x + d * l.x, d1 * y + d * l.y, d1 * z + d * l.z);
	}

	/**
	 * Linearly interpolates between two given locations.
	 * 
	 * @param l1
	 *            Location to be interpolated from.
	 * @param l2
	 *            Location to be interpolated to.
	 * @param d
	 *            Interpolation parameter.
	 * @return Linear interpolation between the two locations.
	 */
	public static PogamutLocation interpolate(PogamutLocation l1, PogamutLocation l2, double d) {
		// from the other side
		double d1 = 1.0D - d;
		// create interpolation of the locations
		return new PogamutLocation(d1 * l1.x + d * l2.x, d1 * l1.y + d * l2.y, d1 * l1.z + d * l2.z);
	}

	/* ********************************************************************** */

	/**
	 * Generates a hashCode for this Location.
	 * 
	 * @return the hashcode for this Location.
	 */

	@Override
	public int hashCode() {		
		if (hashCode == null) hashCode = computeHashCode();
		return hashCode;
	}

	/**
	 * Tells, whether this location equals to given object.
	 * 
	 * @param obj
	 *            Object to be compared with.
	 * @return True, if the object is a Location and has has the same values of
	 *         all three corresponding coordinates.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PogamutLocation other = (PogamutLocation) obj;
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
	 * Tells, whether two given locations equal.
	 * 
	 * @param l1
	 *            First location to compare.
	 * @param l2
	 *            Second location to compare.
	 * @return True, if the locations has the same values of all three
	 *         corresponding coordinates.
	 */
	public static boolean equal(PogamutLocation l1, PogamutLocation l2) {
		if (l1 == null && l2 == null)
			return true;
		if (l1 == null || l2 == null)
			return false;

		return l1.equals(l2);
	}

	/**
	 * Tells, whether the distance between coordinates of this location and
	 * given location is less than or equal to the given epsilon.
	 * 
	 * @param l
	 *            Location to compare with.
	 * @param epsilon
	 *            Epsilon to compare with.
	 * @return True, if the distance between the locations is less than the
	 *         epsilon, false otherwise.
	 */
	public boolean equals(PogamutLocation l, double epsilon) {
		if (l == null)
			return false;

		double d;

		// x axes distance
		d = x - l.x;
		if ((d >= 0 ? d : -d) > epsilon)
			return false;

		// y axes distance
		d = y - l.y;
		if ((d >= 0.0D ? d : -d) > epsilon)
			return false;

		// z axes distance
		d = z - l.z;
		if ((d >= 0.0D ? d : -d) > epsilon)
			return false;

		// aye, aye, sir..
		return true;
	}

	/**
	 * Tells, whether the distance between coordinates of two given locations is
	 * less than or equal to the given epsilon.
	 * 
	 * @param l1
	 *            First location to compare.
	 * @param l2
	 *            Second location to compare.
	 * @param epsilon
	 *            Epsilon to compare with.
	 * @return True, if the distance between the locations is less than the
	 *         epsilon, false otherwise.
	 */
	public static boolean equal(PogamutLocation l1, PogamutLocation l2, double epsilon) {
		double d;

		// x axes distance
		d = l1.x - l2.x;
		if ((d >= 0 ? d : -d) > epsilon)
			return false;

		// y axes distance
		d = l1.y - l2.y;
		if ((d >= 0.0D ? d : -d) > epsilon)
			return false;

		// z axes distance
		d = l1.z - l2.z;
		if ((d >= 0.0D ? d : -d) > epsilon)
			return false;

		// aye, aye, sir..
		return true;
	}

	/* ********************************************************************** */

	/**
	 * Calculates average of all 'locations'. If locations.size() == 0, returns
	 * null.
	 * 
	 * @param locations
	 * @return average location
	 */
	public static PogamutLocation getAverage(Collection<PogamutLocation> locations) {
		if (locations.size() == 0)
			return null;
		Iterator<PogamutLocation> iter = locations.iterator();
		PogamutLocation result = new PogamutLocation(iter.next());
		while (iter.hasNext()) {
			result.add(iter.next());
		}
		return result.scale(1 / locations.size());
	}

	/* ********************************************************************** */

	/**
	 * Calculates the distance between this and given location.
	 * 
	 * @param l
	 *            Location to be calculated the distance to.
	 * @return Euclidean distance between the two locations.
	 */
	public double getDistance(PogamutLocation l) {
		double dx = l.x - x;
		double dy = l.y - y;
		double dz = l.z - z;
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	/**
	 * Calculates the distance between this and given location (ignoring 'z'
	 * coordinate).
	 * 
	 * @param l
	 *            Location to be calculated the distance to.
	 * @return Euclidean distance between the two locations.
	 */
	public double getDistance2D(PogamutLocation l) {
		double dx = l.x - x;
		double dy = l.y - y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	/**
	 * Calculates the distance between two given locations.
	 * 
	 * @param l1
	 *            Location to be calculated the distance from.
	 * @param l2
	 *            Location to be calculated the distance to.
	 * @return Euclidean distance between the two locations.
	 */
	public static double getDistance(PogamutLocation l1, PogamutLocation l2) {
		double dx = l2.x - l1.x;
		double dy = l2.y - l1.y;
		double dz = l2.z - l1.z;
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	/**
	 * Returns difference between z-coords (this.z - location.z).
	 * 
	 * @param location
	 * @return z-difference
	 */
	public double getDistanceZ(PogamutLocation location) {
		return z - location.z;
	}

	/**
	 * Calculates the distance between two given locations (ignoring 'z'
	 * coordinate).
	 * 
	 * @param l1
	 *            Location to be calculated the distance from.
	 * @param l2
	 *            Location to be calculated the distance to.
	 * @return Euclidean distance between the two locations.
	 */
	public static double getDistance2D(PogamutLocation l1, PogamutLocation l2) {
		double dx = l2.x - l1.x;
		double dy = l2.y - l1.y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	/**
	 * Calculates the square of the distance between this and given location.
	 * 
	 * @param l
	 *            Location to be calculated the distance to.
	 * @return Square of the euclidean distance between the two locations.
	 */
	public double getDistanceSquare(PogamutLocation l) {
		double dx = l.x - x;
		double dy = l.y - y;
		double dz = l.z - z;
		return dx * dx + dy * dy + dz * dz;
	}

	/**
	 * Calculates the square of the distance between two given locations.
	 * 
	 * @param l1
	 *            Location to be calculated the distance from.
	 * @param l2
	 *            Location to be calculated the distance to.
	 * @return Square of the euclidean distance between the two locations.
	 */
	public static double getDistanceSquare(PogamutLocation l1, PogamutLocation l2) {
		double dx = l2.x - l1.x;
		double dy = l2.y - l1.y;
		double dz = l2.z - l1.z;
		return dx * dx + dy * dy + dz * dz;
	}

	/**
	 * Calculates the Manhattan distance between this and given location.
	 * 
	 * @param l
	 *            Location to be calculated the distance to.
	 * @return Manhattan (i.e. 1-norm) distance between the two locations.
	 */
	public double getDistanceL1(PogamutLocation l) {
		double dx = Math.abs(l.x - x);
		double dy = Math.abs(l.y - y);
		double dz = Math.abs(l.z - z);
		return dx + dy + dz;
	}

	/**
	 * Calculates the Manhattan distance between two given locations.
	 * 
	 * @param l1
	 *            Location to be calculated the distance from.
	 * @param l2
	 *            Location to be calculated the distance to.
	 * @return Manhattan (i.e. 1-norm) distance between the two locations.
	 */
	public static double getDistanceL1(PogamutLocation l1, PogamutLocation l2) {
		double dx = Math.abs(l2.x - l1.x);
		double dy = Math.abs(l2.y - l1.y);
		double dz = Math.abs(l2.z - l1.z);
		return dx + dy + dz;
	}

	/**
	 * Calculates the Chebyshev distance between this and given location.
	 * 
	 * @param l
	 *            Location to be calculated the distance to.
	 * @return Chebyshev (i.e. infinity-norm) distance between the two
	 *         locations.
	 */
	public double getDistanceLinf(PogamutLocation l) {
		double dx = Math.abs(l.x - x);
		double dy = Math.abs(l.y - y);
		double dz = Math.abs(l.z - z);
		return Math.max(Math.max(dx, dy), dz);
	}

	/**
	 * Calculates the Chebyshev distance between two given locations.
	 * 
	 * @param l1
	 *            Location to be calculated the distance from.
	 * @param l2
	 *            Location to be calculated the distance to.
	 * @return Chebyshev (i.e. infinity-norm) distance between the two
	 *         locations.
	 */
	public static double getDistanceLinf(PogamutLocation l1, PogamutLocation l2) {
		double dx = Math.abs(l2.x - l1.x);
		double dy = Math.abs(l2.y - l1.y);
		double dz = Math.abs(l2.z - l1.z);
		return Math.max(Math.max(dx, dy), dz);
	}

	/**
	 * Calculates the distance between this and given location after being
	 * projected to the (x,y) plane.
	 * 
	 * @param l
	 *            Location to be calculated the distance to.
	 * @return Plane-projected distance between the two locations.
	 */
	public double getDistancePlane(PogamutLocation l) {
		double dx = l.x - x;
		double dy = l.y - y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	/**
	 * Calculates the distance between two given locations after being projected
	 * to the (x,y) plane.
	 * 
	 * @param l1
	 *            Location to be calculated the distance from.
	 * @param l2
	 *            Location to be calculated the distance to.
	 * @return Plane-projected distance between the two locations.
	 */
	public static double getDistancePlane(PogamutLocation l1, PogamutLocation l2) {
		double dx = l2.x - l1.x;
		double dy = l2.y - l1.y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	/* ********************************************************************** */

	/**
	 * Retreives the location itself to implement {@link ILocated}.
	 * 
	 * @return The location itself (note: does not create a copy).
	 */
	public PogamutLocation getLocation() {
		return this;
	}

	/**
	 * Retreives javax.vecmath.Point3d representation of the location.
	 * 
	 * @return javax.vecmath.Point3d representation with x, y and z values set.
	 */
	public Point3d getPoint3d() {
		return new Point3d(x, y, z);
	}

	/* ********************************************************************** */

	/**
	 * Creates location with all values set to zeroes.
	 */
	private PogamutLocation() {
		this(0,0,0);
	}

	/**
	 * Creates location with specified coordinates.
	 * 
	 * @param x
	 *            X coordinate.
	 * @param y
	 *            Y coordinate.
	 * @param z
	 *            Z coordinate.
	 */
	public PogamutLocation(double x, double y, double z) {
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
	 * Creates location with specified planar coordinates. Sets z to zero.
	 * 
	 * @param x
	 *            X coordinate.
	 * @param y
	 *            Y coordinate.
	 */
	public PogamutLocation(double x, double y) {
		this(x,y,0);		
	}

	/**
	 * Copy constructor
	 * 
	 * @param source
	 *            Location to copy
	 */
	public PogamutLocation(PogamutLocation source) {
		this(source.getX(), source.getY(), source.getZ());
	}

	/**
	 * Pattern used to parse {@link PogamutLocation#toString()} in
	 * {@link PogamutLocation#PogamutLocation(String)}.
	 */
	public static final Pattern locationPattern = Pattern
			.compile("\\[([-+]?[0-9]+(\\.[0-9]+){0,1})\\; ([-+]?[0-9]+(\\.[0-9]+){0,1})\\; ([-+]?[0-9]+(\\.[0-9]+){0,1})\\]");

	/**
	 * Zero delta for {@link PogamutLocation#equals(PogamutLocation)}.
	 */
	public static final double DISTANCE_ZERO = 0.000000001;

	/**
	 * Parses the location from the "string" generated by
	 * {@link PogamutLocation#toString()}. If it fails, it throws RuntimeException.
	 * 
	 * @param string
	 */
	public PogamutLocation(String string) {
		Matcher m = locationPattern.matcher(string);
		if (m.find()) {
			String strX = m.group(1);
			String strY = m.group(3);
			String strZ = m.group(5);
			try {
				this.x = Double.parseDouble(strX);
			} catch (Exception e) {
				throw new RuntimeException("String '" + string
						+ "', was not matched as Location, because X-coordinate '" + strX + "' is not a number.");
			}
			try {
				this.y = Double.parseDouble(strY);
			} catch (Exception e) {
				throw new RuntimeException("String '" + string
						+ "', was not matched as Location, because Y-coordinate '" + strY + "' is not a number.");
			}
			try {
				this.z = Double.parseDouble(strZ);
			} catch (Exception e) {
				throw new RuntimeException("String '" + string
						+ "', was not matched as Location, because Z-coordinate '" + strZ + "' is not a number.");
			}
		} else {
			throw new RuntimeException("String '" + string + "' was not matched as Location.");
		}
	}

	/**
	 * Creates location from array of three doubles. Sets x = d[0], y = d[1] and
	 * z = d[2].
	 * 
	 * @param d
	 *            Array of doubles to be used for creation.
	 */
	public PogamutLocation(double d[]) {
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
	 * Creates location from array of three doubles. Sets x = f[0], y = f[1] and
	 * z = f[2].
	 * 
	 * @param f
	 *            Array of to be used for creation.
	 */
	public PogamutLocation(float f[]) {
		if (f.length >= 1)
			this.x = f[0];
		else
			this.x = 0;
		if (f.length >= 2)
			this.y = f[1];
		else
			this.y = 0;
		if (f.length >= 3)
			this.z = f[2];
		else
			this.z = 0;
	}

	/**
	 * Creates location from specified 3D point.
	 * 
	 * @param p
	 *            Point in space to be used for creation.
	 */
	public PogamutLocation(Tuple3d p) {
		this(p.x, p.y, p.z);
	}

	/**
	 * Creates location from specified 3D point.
	 * 
	 * @param p
	 *            Point in space to be used for creation.
	 */
	public PogamutLocation(Point3D p) {
		this(p.getX(), p.getY(), p.getZ());
	}

	/**
	 * Calculates dot product of this Location and Location b
	 * 
	 * @param b
	 *            Location to dot with
	 * @return dot product of this and b
	 */
	public double dot(PogamutLocation b) {
		return x * b.getX() + y * b.getY() + z * b.getZ();
	}

	/**
	 * Calculates dot product of this Location and Location b in 2D (x,y coord
	 * only)
	 * 
	 * @param b
	 *            Location to dot with
	 * @return dot product of this and b
	 */
	public double dot2D(PogamutLocation b) {
		return x * b.getX() + y * b.getY();
	}

	/**
	 * Calculates cross product of this Location and Lcoations b
	 * 
	 * @param b
	 *            Location to cross with
	 * @return cross product of this and b
	 */
	public PogamutLocation cross(PogamutLocation b) {
		return new PogamutLocation(y * b.getZ() - z * b.getY(), z * b.getX() - x * b.getZ(), x * b.getY() - y * b.getX());
	}

	/**
	 * Converts Location into PogamutRotation. Since location is only a single vector,
	 * roll is ommited.
	 * 
	 * @param order
	 *            tells resulting rotation, which rotation order should it
	 *            represent
	 * @return resulting rotation
	 */
	public PogamutRotation getRotation(PogamutRotation.Order order) {
		PogamutLocation this_normalized = getNormalized();
		double yaw = 0d, pitch = 0d;
		switch (order) {
		case YAW_PITCH_ROLL:
		case ROLL_YAW_PITCH:
		case YAW_ROLL_PITCH:
			yaw = Math.atan2(this_normalized.getY(), Math.sqrt(1 - this_normalized.getY() * this_normalized.getY()));

			pitch = Math.atan2(this_normalized.getZ(), this_normalized.getX());
			break;
		case PITCH_YAW_ROLL:
		case PITCH_ROLL_YAW:
		case ROLL_PITCH_YAW:
			pitch = Math.atan2(Math.sqrt(1 - this_normalized.getZ() * this_normalized.getZ()), this_normalized.getZ());
			yaw = Math.atan2(this_normalized.getX(), this_normalized.getY());
			break;
		}
		return new PogamutRotation(pitch / Math.PI * 32768 - 1, yaw / Math.PI * 32768 - 1, 0);
	}

	/**
	 * WIP not completed yet. Use only in case roll yaw pitch order is used.
	 * Converts this location into rotation required to be applied on (1,0,0) in
	 * order achieve this location. Difference from getRotation is that in this
	 * case, quaternion like rotation chaining is achieved. That means that if
	 * you take rotation (x, y, z) then result would be same as applying
	 * rotation (x, 0, 0) on (1, 0, 0) - identity - then (0, y, 0) on the
	 * result, but like it was identity. Then again (0, 0, z) on the result like
	 * it was identity. Thus it works in similar vein as quaternion
	 * multiplication.
	 * 
	 * @param order
	 * @return
	 */
	public PogamutRotation getQuatLikeRotationSeq(PogamutRotation.Order order) {
		PogamutLocation projected = new PogamutLocation(1, 0, 0);

		double yaw = 0d, pitch = 0d;
		switch (order) {
		case ROLL_YAW_PITCH:
			yaw = Math.atan2(getY(), getX());
			projected = projected.mul(PogamutRotation.constructXYRot(yaw));

			pitch = Math.atan2(getZ(), new PogamutLocation(getX(), getY(), 0).dot(projected));

			return new PogamutRotation(pitch / Math.PI * 32768 - 1, yaw / Math.PI * 32768 - 1, 0);
		}
		return PogamutRotation.ZERO;
	}

	/**
	 * Normalizes this Location
	 * 
	 * @return normalized
	 */
	public PogamutLocation getNormalized() {
		return scale(1 / getLength());
	}

	/**
	 * Calculates length of Location
	 * 
	 * @return length
	 */
	public double getLength() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	/**
	 * Projects this Location (vector) using matrix from parameter
	 * 
	 * @param matrix
	 *            projection matrix
	 * @return resulting Location
	 */
	public PogamutLocation mul(Matrix3d matrix) {
		PogamutLocation res = new PogamutLocation(
							matrix.getM00() * x + matrix.getM10() * y + matrix.getM20() * z,
							matrix.getM01() * x + matrix.getM11() * y + matrix.getM21() * z,
							matrix.getM02() * x + matrix.getM12() * y + matrix.getM22() * z
				       );

		return res;
	}

	/**
	 * Calculates inverse Location
	 * 
	 * @return new inverted Location
	 */
	public PogamutLocation invert() {
		return new PogamutLocation(-x, -y, -z);
	}

	/**
	 * Sets the X coordinate.
	 * 
	 * @return new location object
	 */
	public PogamutLocation setX(double x) {
		return new PogamutLocation(x, this.y, this.z);
	}
		
	/**
	 * Sets the Y coordinate.
	 * 
	 * @return new location object
	 */
	public PogamutLocation setY(double y) {
		return new PogamutLocation(this.x, y, this.z);
	}
	
	/**
	 * Sets the Z coordinate.
	 * 
	 * @return new location object
	 */
	public PogamutLocation setZ(double z) {
		return new PogamutLocation(this.x, this.y, z);
	}
	
	/**
	 * Adds to the X coordinate.
	 * 
	 * @return new location object
	 */
	public PogamutLocation addX(double x) {
		return new PogamutLocation(this.x + x, this.y, this.z);
	}
		
	/**
	 * Adds to the Y coordinate.
	 * 
	 * @return new location object
	 */
	public PogamutLocation addY(double y) {
		return new PogamutLocation(this.x, this.y + y, this.z);
	}
	
	/**
	 * Adds to the Z coordinate.
	 * 
	 * @return new location object
	 */
	public PogamutLocation addZ(double z) {
		return new PogamutLocation(this.x, this.y, this.z + z);
	}
	
	/**
	 * Scales the X coordinate.
	 * 
	 * @return new location object
	 */
	public PogamutLocation scaleX(double x) {
		return new PogamutLocation(this.x * x, this.y, this.z);
	}
		
	/**
	 * Scales the Y coordinate.
	 * 
	 * @return new location object
	 */
	public PogamutLocation scaleY(double y) {
		return new PogamutLocation(this.x, this.y * y, this.z);
	}
	
	/**
	 * Scales the Z coordinate.
	 * 
	 * @return new location object
	 */
	public PogamutLocation scaleZ(double z) {
		return new PogamutLocation(this.x, this.y, this.z * z);
	}
	
//	/**
//	 * Set content of this location from passed tocation.
//	 * 
//	 * @return this with newly set value
//	 */
//	public Location setTo(Location l) {
//		this.x = l.x;
//		this.y = l.y;
//		this.z = l.z;
//
//		return this;
//	}
//
//	/**
//	 * Set content of this location from passed data.
//	 * 
//	 * @return this with newly set value
//	 */
//	public Location setTo(double x, double y, double z) {
//		this.x = x;
//		this.y = y;
//		this.z = z;
//
//		return this;
//	}

	/* ********************************************************************** */

	@Override
	public String toString() {
		return String.format(Locale.ENGLISH, "[%.2f; %.2f; %.2f]", x, y, z);
	}

	public PogamutLocation scaleXYZ(double x2, double y2, double z2) {
		return new PogamutLocation(x * x2, y * y2, z * z2);
	}

}
