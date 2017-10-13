/**
 * File: 	ContinuousCirculinearCurve2D.java
 * Project: javaGeom
 * 
 * Distributed under the LGPL License.
 *
 * Created: 11 mai 09
 */
package math.geom2d.circulinear;

import java.util.Collection;

import math.geom2d.Box2D;
import math.geom2d.curve.Curve2D;
import math.geom2d.curve.CurveSet2D;
import math.geom2d.domain.ContinuousOrientedCurve2D;
import math.geom2d.transform.CircleInversion2D;


/**
 * A tagging interface defining a circulinear curve which is continuous.
 * @author dlegland
 * @deprecated replaced by CirculinearContinuousCurve2D
 */
@Deprecated
public interface ContinuousCirculinearCurve2D extends CirculinearCurve2D,
		ContinuousOrientedCurve2D {
    // ===================================================================
    // redefines declaration of CirculinearCurve2D interfaces

	public Curve2D getParallel(double d);
	public Curve2D transform(CircleInversion2D inv);
	
	// ===================================================================
    // redefines declaration of some parent interfaces

	/**
     * Returns a set of circulinear elements, which are basis for circulinear
     * curves.
     */
    public abstract Collection<? extends CirculinearElement2D> getSmoothPieces();

    public CurveSet2D clip(Box2D box);
	public Curve2D getSubCurve(double t0, double t1);
	public Curve2D getReverseCurve();
}
