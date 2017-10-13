package math.geom3d.plane;

import java.util.ArrayList;

import com.google.common.collect.Lists;

import math.geom2d.Point2D;
import math.geom2d.line.LineSegment2D;
import math.geom2d.polygon.SimplePolygon2D;
import math.geom3d.Point3D;
import math.geom3d.Vector3D;
import math.geom3d.line.LineSegment3D;
import math.geom3d.polygon.SimplePlanarPolygon3D;

/** 3D plane coordinate subsystem.
 * 
 * Plane origin becomes (0, 0) in the coordinate subsystem.
 * Plane vectors (vector1, vector2) are orthonormalized to become (x unit vector, y unit vector) of the coordinate subsystem.
 */
public class Plane3DCoordinateSubsystem {

    private Plane3D plane;
    protected Vector3D xUnitVector;
    protected Vector3D yUnitVector;
    
    public Plane3DCoordinateSubsystem(Plane3D plane) {
        this.plane = plane;
        this.xUnitVector = plane.getVector1().getNormalizedVector();
        this.yUnitVector = plane.getVector2().minus(getXUnitVector().times(Vector3D.dotProduct(plane.getVector2(), xUnitVector))).getNormalizedVector();
    }
    
    /** Get X unit vector
     * 
     * @return 3D vector of 1 unit on the coordinate subsystem X axis 
     */
    public Vector3D getXUnitVector() {
           return xUnitVector;
    }
    
    /** Get Y unit vector
     * 
     * @return 3D vector of 1 unit on the coordinate subsystem Y axis 
     */
    public Vector3D getYUnitVector() {
        return yUnitVector;
    }
    
    /** Project a point to 2D coordinate subsystem of the plane
     * 
     * @param point point to project onto plane and then compute its coordinates in coordinate subsystem of the plane.
     * @return 2D coordinates in coordinate subsystem of the plane
     */
    public Point2D project(Point3D point) {
        Vector3D originOffset = new Vector3D(plane.getOrigin(), plane.project(point));
        
        return new Point2D(
            Vector3D.dotProduct(originOffset,getXUnitVector()),
            Vector3D.dotProduct(originOffset,getYUnitVector())
        );
    }
    
    /** Project polygon
     * 
     * @param polygon3d polygon in ambient space
     * @return 2D polygon in the coordinate subsystem
     */
    public SimplePolygon2D project(SimplePlanarPolygon3D polygon3d) {
        ArrayList<Point2D> vertices2d = Lists.newArrayList();
        for ( Point3D vertex3d : polygon3d.getVertices()) {
            vertices2d.add( project(vertex3d) );
        }
        return new SimplePolygon2D(vertices2d);
    }
    
    /** Project line segment
     * 
     * @param lineSegment3D line segment in ambient space
     * @return 2D line segment in the coordinate subsystem
     */
    public LineSegment2D project(LineSegment3D lineSegment3D) {
        return new LineSegment2D( project(lineSegment3D.getFirstPoint()), project(lineSegment3D.getLastPoint()) );
    }
    
    /** Get 3D coords of a point within the coordinate subsystem
     * 
     * @param point coordinates in the coordinate subsystem
     * @return point in 3D
     */
    public Point3D get(Point2D point) {
        return plane.getOrigin().plus( getXUnitVector().times(point.getX()) ).plus( getYUnitVector().times(point.getY() ) );
    }
}
