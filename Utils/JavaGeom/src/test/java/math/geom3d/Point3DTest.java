package math.geom3d;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Point3DTest {
	
	protected static final double bigDouble = 1.0e300;
	
	@Test
	public void distanceTest() {
		assertEquals( Math.sqrt(2), new Point3D( 1, 0, 0).getDistance( new Point3D( 0, 1, 0 ) ), Shape3D.ACCURACY );
		
		// test code preventing overflow
		assertEquals( Math.sqrt(2)*bigDouble, new Point3D( bigDouble, 0, 0).getDistance( new Point3D( 0, bigDouble, 0 ) ), Shape3D.ACCURACY );
	}
}
