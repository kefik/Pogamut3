package math.geom3d.plane;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Random;

import math.geom3d.Point3D;
import math.geom3d.Vector3D;
import math.geom3d.line.StraightLine3D;

import org.junit.Test;

public class Plane3DTest {

    @Test
    public void testCreatePlaneDefinedByPoints() {
        ArrayList<Point3D> points = null;
        Point3D testPoint = null;
        Plane3D plane = null;
        
        // points in the XY plane
        // try confuse constructor by a linearly dependent points
        points = new ArrayList<Point3D>();
        points.add( new Point3D( 0, 0, 0) );
        points.add( new Point3D( 10, 0 , 0 ) );
        points.add( new Point3D( 20, 0 , 0 ) );
        points.add( new Point3D( 15, 1 , 0 ) );
        
        plane = Plane3D.createPlaneDefinedByPoints(points);
        
        testPoint = new Point3D(0, 10, 0);
        assertTrue("XY Plane should contain: "+testPoint+", plane: "+plane, plane.contains( testPoint ) );
        testPoint = new Point3D(10, 10, 0);
        assertTrue("XY Plane should contain: "+testPoint+", plane: "+plane, plane.contains( testPoint ) );
        testPoint = new Point3D(10, 0, 0);
        assertTrue("XY Plane should contain: "+testPoint+", plane: "+plane, plane.contains( testPoint ) );
        testPoint = new Point3D(0, 0, 1);
        assertTrue("XY Plane should not contain: "+testPoint+", plane: "+plane, !plane.contains( testPoint ) );
        
        // same as before but different first point
        points = new ArrayList<Point3D>();
        points.add( new Point3D( 10, 0 , 0 ) );
        points.add( new Point3D( 0, 0, 0) );
        points.add( new Point3D( 20, 0 , 0 ) );
        points.add( new Point3D( 10, 1 , 0 ) );
        
        plane = Plane3D.createPlaneDefinedByPoints(points);
        
        testPoint = new Point3D(0, 10, 0);
        assertTrue("XY Plane should contain: "+testPoint+", plane: "+plane, plane.contains( testPoint ) );
        testPoint = new Point3D(10, 10, 0);
        assertTrue("XY Plane should contain: "+testPoint+", plane: "+plane, plane.contains( testPoint ) );
        testPoint = new Point3D(10, 0, 0);
        assertTrue("XY Plane should contain: "+testPoint+", plane: "+plane, plane.contains( testPoint ) );
        testPoint = new Point3D(0, 0, 1);
        assertTrue("XY Plane should not contain: "+testPoint+", plane: "+plane, !plane.contains( testPoint ) );
    }
    
    @Test
    public void testProject() {
        Plane3D plane = new Plane3D( new Point3D( 0, 0, 0), new Vector3D(1, 0, 0), new Vector3D( 0, 1, 1) );
        assertEquals( new Point3D( 1, 1, 1), plane.project(new Point3D( 1, 2, 0) ) );
    }
    
    @Test
    public void testLineIntersect() {
        Plane3D plane = new Plane3D( new Point3D( 0, 0, 0), new Vector3D(1, 0, 0), new Vector3D( 0, 1, 1) );
        StraightLine3D line = new StraightLine3D( new Point3D( 1, 3, 0), new Point3D( 1, 3, 1) );
        assertEquals( new Point3D( 1, 3, 3), line.getPlaneIntersection(plane) );        
    }
    
    @Test
    public void testContains() {
        Point3D origin = new Point3D( 0, 0, 0);
        Vector3D u = new Vector3D(1, 0, 0);
        Vector3D v = new Vector3D( 0, 1, 1);
        Plane3D plane = new Plane3D( origin, u, v);
        Random random = new Random();
        
        for (int i=0; i<100; ++i) {
            Point3D testPointInPlane = origin.plus(u.times(random.nextDouble()*100).plus(v.times(random.nextDouble()*100))); 
            assertTrue( "Plane should contain "+testPointInPlane, plane.contains(testPointInPlane) );
            Point3D testPointOutsidePlane = testPointInPlane.plus( new Vector3D(0, 0.5, 0) ); 
            assertTrue( "Plane should not contain "+testPointInPlane, !plane.contains(testPointOutsidePlane) );
        }
    }
}
