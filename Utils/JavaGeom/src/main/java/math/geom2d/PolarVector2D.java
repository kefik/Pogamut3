package math.geom2d;

import java.io.Serializable;

/** 2D vector specified by polar coordinates
 * <p>
 * Immutable.
 */
public class PolarVector2D implements Serializable {
    private static final long serialVersionUID = 1L;
    
    protected double rho;
    protected double theta;
    
    /** Construct from polar coordinates
     * 
     * @param rho the distance from origin
     * @param theta the counter-clockwise angle with to positive part of the X-axis in radians. Normalized to fit within <0,2*Pi).
     */
    public PolarVector2D( double rho, double theta ) {
    	this.rho = rho;
    	this.theta = normalizeTheta( theta );
    }
    
    /** @return rho, the distance from origin
     */
    public double getRho() {
    	return rho;
    }
    
    /** @return theta, the counter-clockwise angle with to positive part of the X-axis in radians <0,2*Pi)
     */
    public double getTheta() {
    	return theta;
    }
    
    Vector2D asCartesian() {
    	return Vector2D.createPolar( rho, theta );
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PolarVector2D))
            return false;
        PolarVector2D v = (PolarVector2D) obj;
        return (Math.abs(v.getRho()-rho)<Shape2D.ACCURACY && normalizeTheta(v.getTheta()-theta)<Shape2D.ACCURACY);
    }
    
    /** Normalize theta, the angle coordinate, so that the representation is unique
     * 
     * @param theta the counter-clockwise angle with to positive part of the X-axis in radians
     * @return theta normalized to fit within <0,2*Pi).
     */
    public static double normalizeTheta( double theta ) {
    	double retval = theta % (2*Math.PI);
    	if ( retval < 0 ) {
    		retval += 2*Math.PI;
    	}
    	return retval;
    }
}
