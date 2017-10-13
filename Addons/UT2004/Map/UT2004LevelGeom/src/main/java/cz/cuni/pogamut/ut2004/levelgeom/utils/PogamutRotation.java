package cz.cuni.pogamut.ut2004.levelgeom.utils;

import java.beans.PropertyEditorManager;
import java.beans.PropertyEditorSupport;
import java.io.Serializable;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;

/**
 * Rotation within the world.
 * 
 * Rotation is represented as yaw, roll and pitch.
 * 
 * FIXME[js]: Add working methods and consider imports from Tuple3d.
 * 
 * @author Juraj 'Loque' Simlovic
 * @author Radek 'Black_Hand' Pibil
 */
public class PogamutRotation {

	/**
	 * Rotation representing NONE.
	 */
	public static final PogamutRotation NONE = new PogamutRotation(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
	
	/**
	 * Rotation(0,0,0);
	 */
	public static final PogamutRotation ZERO = new PogamutRotation();

	/**
	 * This here is for StoryFactory compatibility reasons. Can be removed in
	 * 2012.
	 */
	static final long serialVersionUID = -1964427510333336912L;

	static {
		// register property editor, otherwise this class won't be
		// introspectable
		PropertyEditorManager.registerEditor(PogamutRotation.class, PogamutRotation.PropertyEditor.class);
	}

	/**
	 * Property editor for Rotation. Accepts same format as PogamutLocation.
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
				return;
			} else {
				double[] d = PogamutLocation.PropertyEditor.parseNumberArray(s);
				if (d.length != 3) {
					throw new IllegalArgumentException();
				}
				setValue(new PogamutRotation(d[0], d[1], d[2]));
			}
		}
	}

	@Override
	public PogamutRotation clone() {
		return new PogamutRotation(this);
	}

	/**
	 * Rotation yaw. Yaw is rotation to the left or right. E.g. turn left. The
	 * value ranges from -32768..32767.
	 */
	public final double yaw;
	/**
	 * Rotation roll. Roll is twist of head. E.g. Tilt the head to shoulder. The
	 * value ranges from -32768..32767.
	 */
	public final double roll;
	/**
	 * Rotation pitch. Pitch is rotation up and down. E.g. look down. The value
	 * ranges from -32768..32767.
	 */
	public final double pitch;

	private Integer hashCode;

	/* ********************************************************************** */

	/**
	 * Rotation yaw. Yaw is rotation to the left or right. E.g. turn left.
	 * 
	 * @return Rotation yaw. The value ranges from -32768..32767.
	 */
	public double getYaw() {
		return yaw;
	}

	/**
	 * Rotation pitch. Pitch is rotation up and down. E.g. look down.
	 * 
	 * @return Rotation pitch. The value ranges from -32768..32767.
	 */
	public double getPitch() {
		return pitch;
	}

	/**
	 * Rotation roll. Roll is twist of head. E.g. Tilt the head to shoulder.
	 * 
	 * @return Rotation roll. The value ranges from -32768..32767.
	 */
	public double getRoll() {
		return roll;
	}

	/* ********************************************************************** */

	/**
	 * Retreives the rotation itself to implement {@link IRotable}.
	 * 
	 * @return The rotation itself (note: does not create a copy).
	 */
	public PogamutRotation getRotation() {
		return this;
	}

	/**
	 * Retreives javax.vecmath.Point3d representation of the rotation.
	 * 
	 * @return javax.vecmath.Point3d representation with x, y and z values set.
	 */
	public Point3d getPoint3d() {
		return new Point3d(pitch, yaw, roll);
	}

	/* ********************************************************************** */

	/**
	 * Creates rotation with all values set to zeroes.
	 */
	private PogamutRotation() {
		this(0,0,0);
	}

	/**
	 * Creates rotation with specified values.
	 * 
	 * @param yaw
	 *            Rotation yaw. Yaw is rotation to the left or right.
	 * @param roll
	 *            Rotation roll. Roll is twist of head. E.g. Tilt the head to
	 *            shoulder.
	 * @param pitch
	 *            Rotation pitch. Pitch is rotation up and down.
	 */
	public PogamutRotation(double pitch, double yaw, double roll) {
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
	}

	/**
	 * Copy constructor.
	 * 
	 * @param rotation
	 *            Rotation.
	 */
	public PogamutRotation(PogamutRotation rotation) {
		this(rotation.getPitch(), rotation.getYaw(), rotation.getRoll());
	}
	
	private int computeHashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(pitch);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(roll);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(yaw);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/**
	 * Pattern used to parse {@link PogamutRotation#toString()} in
	 * {@link PogamutRotation#PogamutRotation(String)}.
	 */
	public static final Pattern rotationPattern = Pattern
			.compile("\\[([-+]?[0-9]+(\\.[0-9]+){0,1})\\; ([-+]?[0-9]+(\\.[0-9]+){0,1})\\; ([-+]?[0-9]+(\\.[0-9]+){0,1})\\]");

	/**
	 * Parses the location from the "string" generated by
	 * {@link PogamutRotation#toString()}. If it fails, it throws RuntimeException.
	 * 
	 * @param string
	 */
	public PogamutRotation(String string) {
		Matcher m = rotationPattern.matcher(string);
		if (m.find()) {
			String strPitch = m.group(1);
			String strYaw = m.group(3);
			String strRoll = m.group(5);
			try {
				this.pitch = Double.parseDouble(strPitch);
			} catch (Exception e) {
				throw new RuntimeException("String '" + string
						+ "', was not matched as Rotation, because pitch-value '" + strPitch + "' is not a number.");
			}
			try {
				this.yaw = Double.parseDouble(strYaw);
			} catch (Exception e) {
				throw new RuntimeException("String '" + string + "', was not matched as Rotation, because yaw-value '"
						+ strYaw + "' is not a number.");
			}
			try {
				this.roll = Double.parseDouble(strRoll);
			} catch (Exception e) {
				throw new RuntimeException("String '" + string + "', was not matched as Rotation, because roll-value '"
						+ strRoll + "' is not a number.");
			}
		} else {
			throw new RuntimeException("String '" + string + "' was not matched as Rotation.");
		}
	}

	/**
	 * Linearly interpolates between 2 doubles with alpha as strength. That is
	 * how close from a to be the interpolation proceeds.
	 * 
	 * @param a
	 *            start
	 * @param b
	 *            target
	 * @param alpha
	 *            strength
	 * @return interpolated value
	 */
	public static final double LinearInterp(double a, double b, double alpha) {
		return a + (b - a) * alpha;
	}

	/**
	 * Logarithmically interpolates between 2 doubles with alpha as strength.
	 * That is how close from a to be the interpolation proceeds.
	 * 
	 * @param a
	 *            start
	 * @param b
	 *            target
	 * @param alpha
	 *            strength
	 * @return interpolated value
	 */
	public static final double LogInterp(double a, double b, double alpha) {
		return a + (b - a) * Math.log(1 + alpha * (Math.E - 1));
	}

	/**
	 * Exponentially interpolates between 2 doubles with alpha as strength. That
	 * is how close from a to be the interpolation proceeds.
	 * 
	 * @param a
	 *            start
	 * @param b
	 *            target
	 * @param alpha
	 *            strength
	 * @return interpolated value
	 */
	public static final double ExpInterp(double a, double b, double alpha) {
		// (0,1);(1,e)
		return a + (b - a) * (Math.exp(alpha) - 1) / (Math.E - 1);
	}

	/**
	 * Linearly interpolates between 2 rotations with alpha as strength. That is
	 * how close from a to be the interpolation proceeds. Static version.
	 * 
	 * @param a
	 *            start
	 * @param b
	 *            target
	 * @param alpha
	 *            strength
	 * @return interpolated rotation
	 */
	public static final PogamutRotation RotationLinearInterp(PogamutRotation a, PogamutRotation b, double alpha) {
		return new PogamutRotation(LinearInterp(a.pitch, b.pitch, alpha), LinearInterp(a.yaw, b.yaw, alpha), LinearInterp(
				a.roll, b.roll, alpha));
	}

	/**
	 * Linearly interpolates between 2 rotations with alpha as strength. That is
	 * how close from a to be the interpolation proceeds. Dynamic version.
	 * 
	 * @param a
	 *            start
	 * @param b
	 *            target
	 * @param alpha
	 *            strength
	 * @return interpolated rotation
	 */
	public final PogamutRotation RotationLinearInterp(PogamutRotation b, double alpha) {
		return RotationLinearInterp(this, b, alpha);
	}

	/**
	 * Logarithmically interpolates between 2 rotations with alpha as strength.
	 * That is how close from a to be the interpolation proceeds. Static
	 * version.
	 * 
	 * @param a
	 *            start
	 * @param b
	 *            target
	 * @param alpha
	 *            strength
	 * @return interpolated rotation
	 */
	public static final PogamutRotation RotationLogInterp(PogamutRotation a, PogamutRotation b, double alpha) {
		return new PogamutRotation(LogInterp(a.pitch, b.pitch, alpha), LogInterp(a.yaw, b.yaw, alpha), LogInterp(a.roll,
				b.roll, alpha));
	}

	/**
	 * Logarithmically interpolates between 2 rotations with alpha as strength.
	 * That is how close from a to be the interpolation proceeds. Dynamic
	 * version.
	 * 
	 * @param a
	 *            start
	 * @param b
	 *            target
	 * @param alpha
	 *            strength
	 * @return interpolated rotation
	 */
	public final PogamutRotation RotationLogInterp(PogamutRotation b, double alpha) {
		return RotationLogInterp(this, b, alpha);
	}

	/**
	 * Exponentially interpolates between 2 rotations with alpha as strength.
	 * That is how close from a to be the interpolation proceeds. Static
	 * version.
	 * 
	 * @param a
	 *            start
	 * @param b
	 *            target
	 * @param alpha
	 *            strength
	 * @return interpolated rotation
	 */
	public static final PogamutRotation RotationExpInterp(PogamutRotation a, PogamutRotation b, double alpha) {
		return new PogamutRotation(ExpInterp(a.pitch, b.pitch, alpha), ExpInterp(a.yaw, b.yaw, alpha), ExpInterp(a.roll,
				b.roll, alpha));
	}

	/**
	 * Exponentially interpolates between 2 rotations with alpha as strength.
	 * That is how close from a to be the interpolation proceeds. Dynamic
	 * version.
	 * 
	 * @param a
	 *            start
	 * @param b
	 *            target
	 * @param alpha
	 *            strength
	 * @return interpolated rotation
	 */
	public final PogamutRotation RotationExpInterp(PogamutRotation b, double alpha) {
		return RotationExpInterp(this, b, alpha);
	}

	/**
	 * Used for conversions in from PogamutLocation into Rotation and vice versa
	 */
	public enum Order {
		YAW_PITCH_ROLL, ROLL_PITCH_YAW, PITCH_YAW_ROLL, PITCH_ROLL_YAW, YAW_ROLL_PITCH, ROLL_YAW_PITCH;
	}

	/* ********************************************************************** */

	/**
	 * Generates a hashcode for this Rotation.
	 * 
	 */
	@Override
	public int hashCode() {
		if (hashCode == null) hashCode = computeHashCode();
		return hashCode;
	}

	/**
	 * Tells, whether this objects equals to given rotation.
	 * 
	 * @param obj
	 *            Object to be compared with.
	 * @return True, if the object is an instance of rotation has the same values of all three
	 *         corresponding values.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PogamutRotation))
			return false;
		PogamutRotation other = (PogamutRotation) obj;
		if (hashCode != other.hashCode) return false;
		if (Double.doubleToLongBits(pitch) != Double.doubleToLongBits(other.pitch))
			return false;
		if (Double.doubleToLongBits(roll) != Double.doubleToLongBits(other.roll))
			return false;
		if (Double.doubleToLongBits(yaw) != Double.doubleToLongBits(other.yaw))
			return false;
		return true;
	}

	/**
	 * Tells, whether two given rotations equal.
	 * 
	 * @param r1
	 *            First rotation to comapre.
	 * @param r2
	 *            Second rotation to comapre.
	 * @return True, if the locations have the same values of all three
	 *         corresponding coordinates.
	 */
	public static boolean equal(PogamutRotation r1, PogamutRotation r2) {
		if (r1 == null && r2 == null)
			return true;
		if (r1 == null || r2 == null)
			return false;

		return r1.equals(r2);
	}

	@Override
	public String toString() {
		return String.format(Locale.ENGLISH, "[%.2f; %.2f; %.2f]", pitch, yaw, roll);
	}

	/**
	 * Converts this Rotation into PogamutLocation. Using default order.
	 * 
	 * @return converted Rotation into PogamutLocation using yaw roll pitch order
	 */
	public PogamutLocation toLocation() {
		return toLocation(Order.PITCH_YAW_ROLL);
	}

	/**
	 * Converts this Rotation into PogamutLocation.
	 * 
	 * @param order
	 *            order of rotations should the method use
	 * @return converted Rotation into PogamutLocation
	 */
	public PogamutLocation toLocation(Order order) {
		Matrix3d yaw = constructXYRot(getYaw() / 32767 * Math.PI);
		Matrix3d roll = constructYZRot(getRoll() / 32767 * Math.PI);
		Matrix3d pitch = constructXZRot(getPitch() / 32767 * Math.PI);

		PogamutLocation res = new PogamutLocation(1, 0, 0);

		switch (order) {
		case YAW_PITCH_ROLL:
			return res.mul(yaw).mul(pitch).mul(roll);
		case ROLL_PITCH_YAW:
			return res.mul(roll).mul(pitch).mul(yaw);
		case PITCH_YAW_ROLL:
			return res.mul(pitch).mul(yaw).mul(roll);
		case PITCH_ROLL_YAW:
			return res.mul(pitch).mul(roll).mul(yaw);
		case YAW_ROLL_PITCH:
			return res.mul(yaw).mul(roll).mul(pitch);
		case ROLL_YAW_PITCH:
			return res.mul(roll).mul(yaw).mul(pitch);
		}

		return null;
	}

	/**
	 * Useful methods from Rotation->PogamutLocation conversions. Constructs rotation
	 * in YZ plane.
	 * 
	 * @param angle
	 * @return projection Matrix3d
	 */
	public static Matrix3d constructYZRot(double angle) {
		Matrix3d res = new Matrix3d();
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		res.setM00(1);
		res.setM11(cos);
		res.setM21(sin);
		res.setM12(-sin);
		res.setM22(cos);

		return res;
	}

	/**
	 * Useful methods from Rotation->PogamutLocation conversions. Constructs rotation
	 * in XZ plane.
	 * 
	 * @param angle
	 * @return projection Matrix3d
	 */
	public static Matrix3d constructXZRot(double angle) {
		Matrix3d res = new Matrix3d();
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		res.setM00(cos);
		res.setM20(-sin);
		res.setM11(1);
		res.setM02(sin);
		res.setM22(cos);

		return res;
	}

	/**
	 * Useful methods from Rotation->PogamutLocation conversions. Constructs rotation
	 * in XY plane.
	 * 
	 * @param angle
	 * @return projection Matrix3d
	 */
	public static Matrix3d constructXYRot(double angle) {
		Matrix3d res = new Matrix3d();
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		res.setM00(cos);
		res.setM10(-sin);
		res.setM01(sin);
		res.setM11(cos);
		res.setM22(1);

		return res;
	}
	
	/**
	 * Sets the yaw.
	 * 
	 * @return new rotation object
	 */
	public PogamutRotation setYaw(double yaw) {
		return new PogamutRotation(this.pitch, yaw, this.roll);
	}
		
	/**
	 * Sets the pitch.
	 * 
	 * @return new rotation object
	 */
	public PogamutRotation setPitch(double pitch) {
		return new PogamutRotation(pitch, this.yaw, this.roll);
	}
	
	/**
	 * Sets the roll.
	 * 
	 * @return new rotation object
	 */
	public PogamutRotation setRoll(double roll) {
		return new PogamutRotation(this.pitch, this.yaw, roll);
	}

//	/**
//	 * Set this rotation to values from r.
//	 * 
//	 * @param r
//	 *            rotation from which we copy data
//	 * @return this rotation after data has been set to r
//	 */
//	public Rotation setTo(Rotation r) {
//		this.yaw = r.yaw;
//		this.roll = r.roll;
//		this.pitch = r.pitch;
//
//		return this;
//	}
//
//	/**
//	 * Set this rotation to passed values.
//	 * 
//	 * @param pitch
//	 *            new pitch
//	 * @param yaw
//	 *            new yaw
//	 * @param roll
//	 *            new roll
//	 * @return this rotation after data has been set
//	 */
//	public Rotation setTo(double pitch, double yaw, double roll) {
//		this.yaw = yaw;
//		this.roll = roll;
//		this.pitch = pitch;
//
//		return this;
//	}

}
