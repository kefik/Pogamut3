package math.geom3d;

/** Axis enumeration
 */
public enum Axis3D {
	X,
	Y,
	Z;
	
	/** Get coordinate of the axis
	 */
	public double getCoord( Point3D point ) {
		switch (this) {
    	case X:
    		return point.getX();
    	case Y:
    		return point.getY();
    	case Z:
    		return point.getZ();
    	default:
    		throw new AssertionError("Invalid axis.");
    	}
	}

	/** Get coordinate of the axis
	 */
	public double getCoord( Vector3D vector ) {
		switch (this) {
    	case X:
    		return vector.getX();
    	case Y:
    		return vector.getY();
    	case Z:
    		return vector.getZ();
    	default:
    		throw new AssertionError("Invalid axis.");
    	}
	}
}