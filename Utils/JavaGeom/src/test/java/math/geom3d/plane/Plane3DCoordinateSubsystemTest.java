package math.geom3d.plane;

import static org.junit.Assert.*;

import math.geom2d.Point2D;
import math.geom3d.Point3D;
import math.geom3d.Vector3D;

import org.junit.Test;

public class Plane3DCoordinateSubsystemTest {
    
    @Test
    public void testGetUnitVector() {
        Plane3D plane = new Plane3D( new Point3D(0, 0, 0), new Vector3D(10, 0, 0), new Vector3D(71421, 15, 15) );
        assertEquals( new Vector3D(1, 0, 0), plane.getCoordinateSubsystem().getXUnitVector() );
        assertEquals( new Vector3D(0, 1.0/Math.sqrt(2),  1.0/Math.sqrt(2)), plane.getCoordinateSubsystem().getYUnitVector() );
    }
    
    @Test
    public void testProject() {
        Plane3D plane = new Plane3D( new Point3D(0, 0, 0), new Vector3D(10, 0, 0), new Vector3D(71421, 15, 15) );
        assertEquals( new Point2D(1,1), plane.getCoordinateSubsystem().project( new Point3D(1, 1.0/Math.sqrt(2),  1.0/Math.sqrt(2))) );
    }
    
    @Test
    public void testGet() {
        Plane3D plane = new Plane3D( new Point3D(0, 0, 0), new Vector3D(10, 0, 0), new Vector3D(71421, 15, 15) );
        assertEquals(  new Point3D(1, 1.0/Math.sqrt(2),  1.0/Math.sqrt(2)), plane.getCoordinateSubsystem().get( new Point2D(1, 1) ) );
    }
}
