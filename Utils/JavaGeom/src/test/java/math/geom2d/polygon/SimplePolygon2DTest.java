package math.geom2d.polygon;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import math.geom2d.Point2D;

import org.junit.Test;

public class SimplePolygon2DTest {

    static ArrayList<Point2D> triangleOriginXYVertices;
    static ArrayList<Point2D> triangleOriginYXVertices;
    static ArrayList<Point2D> triangleBandOriginXYVertices;
    
    static {
        triangleOriginXYVertices = new ArrayList<Point2D>();
        triangleOriginXYVertices.add( new Point2D(0, 0) );
        triangleOriginXYVertices.add( new Point2D(100, 0) );
        triangleOriginXYVertices.add( new Point2D(0, 100) );
        
        triangleOriginYXVertices = new ArrayList<Point2D>( triangleOriginXYVertices);
        Collections.reverse(triangleOriginYXVertices);
        
        triangleBandOriginXYVertices = new ArrayList<Point2D>( triangleOriginXYVertices);
        // add inner edges
        triangleBandOriginXYVertices.add( new Point2D( 25, 25) );
        triangleBandOriginXYVertices.add( new Point2D( 75, 25) );
        triangleBandOriginXYVertices.add( new Point2D( 25, 75) );
        triangleBandOriginXYVertices.add( new Point2D( 25, 25) );
    }
    
    @Test
    public void testWindingAndContainment() {
        testWindingAndContainmentOfPolygon(triangleOriginYXVertices, 25, 25, -1, true );
        testWindingAndContainmentOfPolygon(triangleOriginYXVertices, 0, 0, -1, true );
        testWindingAndContainmentOfPolygon(triangleOriginYXVertices, -1, 0, 0, false );
        testWindingAndContainmentOfPolygon(triangleOriginYXVertices, 0, -1, 0, false );
        testWindingAndContainmentOfPolygon(triangleOriginYXVertices, 100, 1, 0, false );
        testWindingAndContainmentOfPolygon(triangleOriginYXVertices, 1, 100, 0, false );
        testWindingAndContainmentOfPolygon(triangleOriginYXVertices, 50, 50, -1, true );
        testWindingAndContainmentOfPolygon(triangleOriginXYVertices, 25, 25, 1, true );
        testWindingAndContainmentOfPolygon(triangleOriginXYVertices, -1, 0, 0, false );
        testWindingAndContainmentOfPolygon(triangleOriginXYVertices, 0, -1, 0, false );
        testWindingAndContainmentOfPolygon(triangleOriginXYVertices, 0, 0, 0, true );
        testWindingAndContainmentOfPolygon(triangleOriginXYVertices, 100, 1, 0, false );
        testWindingAndContainmentOfPolygon(triangleOriginXYVertices, 1, 100, 0, false );
        testWindingAndContainmentOfPolygon(triangleOriginXYVertices, 50, 50, 0, true );
        testWindingAndContainmentOfPolygon(triangleBandOriginXYVertices, 30, 30, 2, false );
        testWindingAndContainmentOfPolygon(triangleBandOriginXYVertices, 30, 15, 1, true );        
    }
    
    protected void testWindingAndContainmentOfPolygon(Collection<Point2D> vertices, double x, double y, int expectedWindingNumber, boolean expectedIsContained) {
        SimplePolygon2D polygon = new SimplePolygon2D(vertices);
        assertEquals( expectedWindingNumber, polygon.getWindingNumber( x, y ) );
        assertEquals( expectedIsContained, polygon.contains( x, y ) || polygon.getBoundary().contains( x, y ) );

    }
}
