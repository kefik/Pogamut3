package math.geom3d.plane;

import static org.junit.Assert.*;

import org.junit.Test;

import math.geom3d.Axis3D;
import math.geom3d.Point3D;
import math.geom3d.line.StraightLine3D;

public class AxisAlignedPlane3DTest {
	
	@Test
	public void testComputeSignedDistance() {
		Point3D threeOnes = new Point3D( 1.0, 1.0, 1.0 );
		Point3D minusThreeOnes = new Point3D( -1.0, -1.0, -1.0 );
		StraightLine3D diagonalLineThroughZero = new StraightLine3D( threeOnes, minusThreeOnes ); 
		for ( Axis3D axis : Axis3D.values() ) {
			AxisAlignedPlane3D plane = new AxisAlignedPlane3D( axis, 0 );
			assertEquals( "Failure for "+axis.name()+"-aligned plane.", 1.0, plane.asPlane3D().getSignedDistance( threeOnes ), 0.01 );
			assertEquals( "Failure for "+axis.name()+"-aligned plane.", 1.0, plane.getSignedDistance( threeOnes ), 0.01 );
			assertEquals( "Failure for "+axis.name()+"-aligned plane.", -1.0, plane.asPlane3D().getSignedDistance( minusThreeOnes ), 0.01 );
			assertEquals( "Failure for "+axis.name()+"-aligned plane.", -1.0, plane.getSignedDistance( minusThreeOnes ), 0.01 );
			
			assertEquals( "Intersection should be at (0,0,0).", 0.0, new Point3D(0,0,0).getDistance( plane.getLineIntersection(diagonalLineThroughZero) ), 0.01 );
		}
	}
}