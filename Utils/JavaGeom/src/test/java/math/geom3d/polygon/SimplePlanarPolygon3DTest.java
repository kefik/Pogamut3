package math.geom3d.polygon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Random;

import math.geom2d.Point2D;
import math.geom2d.polygon.SimplePolygon2D;
import math.geom3d.Point3D;
import math.geom3d.Vector3D;
import math.geom3d.transform.AffineTransform3D;

import org.junit.Test;

public class SimplePlanarPolygon3DTest {

    //rectangle in (1,0,0)X(0,1,1) plane
    static SimplePlanarPolygon3D rectangle = null;
    static {
        ArrayList<Point3D> vertices = new ArrayList<Point3D>();
        vertices.add( new Point3D(-1,-1,-1) );
        vertices.add( new Point3D( 1,-1,-1) );
        vertices.add( new Point3D( 1, 1, 1) );
        vertices.add( new Point3D(-1, 1, 1) );
        rectangle = new SimplePlanarPolygon3D( vertices );
    }
    
    @Test
    public void testGetArea() {
        assertEquals( 2*Math.sqrt(8), rectangle.getArea(), 0.001 );
    }
    
    @Test
    public void testGetCentroid() {
        assertEquals( new Point3D( 0, 0, 0 ), rectangle.getCentroid() );
        
        // create random polygon and determine centroid
        
        // transformation to randomly warp polygon into 3D space
        AffineTransform3D transformation = new AffineTransform3D();
        transformation = transformation.concatenate( AffineTransform3D.createRotationOx(1) );
        transformation = transformation.concatenate( AffineTransform3D.createRotationOy(2) );
        transformation = transformation.concatenate( AffineTransform3D.createRotationOz(0.5) );
        transformation = transformation.concatenate( AffineTransform3D.createTranslation( new Vector3D(2, 3, 5) ) );

        Random random = new Random();

        ArrayList<Point2D> verticesIn2d = new ArrayList<Point2D>();
        ArrayList<Point3D> verticesIn3d = new ArrayList<Point3D>();
        for (int i=0; i<3; ++i) {
            double x = 100*random.nextDouble();
            double y = 100*random.nextDouble();
            Point2D vertexIn2d = new Point2D(x,y); 
            verticesIn2d.add( vertexIn2d );
            Point3D vertexIn3d = transformation.transformPoint(new Point3D(x,y,0)); 
            verticesIn3d.add( vertexIn3d );
        }

        SimplePolygon2D polygonIn2d = new SimplePolygon2D(verticesIn2d);

        SimplePlanarPolygon3D polygon = new SimplePlanarPolygon3D(verticesIn3d);
        Point3D computedCentroid = polygon.getCentroid(); // this is the centroid point to test
        Vector3D offsetSum = new Vector3D();
        
        // randomly pick point inside the polygon, over time it should average to centroid
        long randomLocationLimit = 50000;
        for (int i=0;i<randomLocationLimit;++i)
        {
            Point3D randomPointInsidePolygon3D = null;
            do
            {
                double x = 100*random.nextDouble();
                double y = 100*random.nextDouble();
                Point2D randomPointInsidePolygon2D = new Point2D(x,y);
                if ( polygonIn2d.contains(randomPointInsidePolygon2D) ) 
                {
                    randomPointInsidePolygon3D = transformation.transformPoint(new Point3D(x,y,0));                  
                }
            } while ( randomPointInsidePolygon3D == null );

            offsetSum = offsetSum.plus(new Vector3D(computedCentroid, randomPointInsidePolygon3D));
            
            if (i%(randomLocationLimit/50) == 0) {
                System.out.println(""+i+" iterations out of "+randomLocationLimit+".");
            }
        }
        
        Vector3D averageLocationOffset = offsetSum.times(1.0/randomLocationLimit);
        assertTrue("Probabistically computed centroid deviated by more than 1.0, deviation: "+averageLocationOffset.getLength(), averageLocationOffset.getLength() < 1.0);    
    }
    
    @Test
    public void testProject() {
        assertEquals( new Point3D( 0, 0, 0), rectangle.project( new Point3D(0, 1, -1) ) );
        assertEquals( new Point3D( 0, 0, 0), rectangle.project( new Point3D(0, 0, 0) ) );
        assertEquals( new Point3D( 1, 0, 0), rectangle.project( new Point3D(2, 0, 0) ) );
    }
    
    @Test
    public void testGetDistance() {
        assertEquals( Math.sqrt(2), rectangle.getDistance( new Point3D(0, 1, -1) ) , 0.001 );
        assertEquals( 0, rectangle.getDistance( new Point3D(0, 0, 0) ) , 0.001 );
        assertEquals( 1, rectangle.getDistance( new Point3D(2, 0, 0) ) , 0.001 );
    }
}