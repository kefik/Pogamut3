package math;

public class JavaGeomMath {

	/** Compute hypotenuse
	 * <p>
	 * Same as {@link Math#hypot(double, double)}, only much faster for values that are not huge. Falls back to {@link Math#hypot(double, double)} if values are actually huge.
	 */
	public static double hypot( double a, double b ) {
     	double maxCoordDifference = Math.max( Math.abs(a), Math.abs(b) );
    	
        if (maxCoordDifference > 1.0e150 ) {
        	// difference square might overflow, use the safe method
        	return Math.hypot( a, b );
        } else {
        	// squares will fit fine, use the fast method
        	return Math.sqrt( a*a+b*b );
        }
	}
	
	/** Compute vector length
	 */
	public static double computeLength( double x, double y, double z ) {
		double maxCoordDifference = Math.max( 
			Math.abs(x),
			Math.max( Math.abs(y), Math.abs(z) )
		);
    	
        if (maxCoordDifference > 1.0e150 ) {
        	// difference square might overflow, use the safe method
        	return hypot( hypot(x, y), z );
        } else {
        	// squares will fit fine, use the fast method
        	return Math.sqrt( x*x+y*y+z*z );
        }
	}
}
