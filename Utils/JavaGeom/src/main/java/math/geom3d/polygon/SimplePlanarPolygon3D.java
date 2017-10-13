package math.geom3d.polygon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import math.geom2d.Point2D;
import math.geom2d.polygon.SimplePolygon2D;
import math.geom3d.Point3D;
import math.geom3d.plane.Plane3D;

/** Simple planar polygon in 3D space
 */
public class SimplePlanarPolygon3D {

    protected List<Point3D> vertices;
    protected transient SimplePolygon2D polygonIn2d = null; // computed on demand and cached, use getter
    protected transient Plane3D plane = null; // computed on demand and cached, use getter
    
    /** Construct planar polygon in 3D from vertices
     * 
     * @param vertices polygons vertices, must form a planar polygon
     */
    public SimplePlanarPolygon3D(Collection<Point3D> vertices) {
        assert(vertices.size() >= 3);
        this.vertices = Collections.unmodifiableList(new ArrayList<Point3D>(vertices));
    }
    
    /** Get vertices
     * 
     * Read only.
     */
    public List<Point3D> getVertices() {
        return vertices;
    }
    
    /** Get plane of the polygon
     * <p>
     * All polygon vertices are guaranteed to lie within the plane,
     * but the specific origin, vector1, vector2 and coordinate subsystem derived from those
     * are chosen arbitrarily. 
     */
    public Plane3D getPlane() {
        if (plane == null) {    
            plane = Plane3D.createPlaneDefinedByPoints(vertices);
        }
        
        return plane;
    }
    
    /** Get 2D projection of the polygon
     * 
     * Vertex coordinates are determined by the coordinate subsystem of the plane of the polygon. See {@link #getPlane()}.
     */
    public SimplePolygon2D getPolygonIn2d() {
        if (polygonIn2d == null) {
            polygonIn2d = getPlane().getCoordinateSubsystem().project( this );
        }
        return polygonIn2d;
    }
    
    /** Get centroid
     */
    public Point3D getCentroid() {
        return getPlane().getCoordinateSubsystem().get(getPolygonIn2d().getCentroid());
    }
    
    /** Get area of the polygon
     */
    public double getArea() {
        return getPolygonIn2d().getArea();
    }
    
    /** Project point
     * 
     * @param point point to project
     * @return closest point within the shape
     */
    public Point3D project(Point3D point) {
        Point2D point2d = getPlane().getCoordinateSubsystem().project(point);
        Point2D closest2d = getPolygonIn2d().project(point2d);
        return getPlane().getCoordinateSubsystem().get(closest2d);
    }
    
    /** Get distance to a point
     */
    public double getDistance(Point3D from) {
        return project(from).getDistance(from);
    }
    
    @Override
    public String toString() {
    	String retval = "SimplePlanarPolygon3D( ";
    	for (Point3D vertex : vertices) {
    		if ( retval.length() > 49-26) {
    			retval += ", ";
    		}
    		retval += vertex.toString();
    	}
    	return retval+" )";
    }
}
