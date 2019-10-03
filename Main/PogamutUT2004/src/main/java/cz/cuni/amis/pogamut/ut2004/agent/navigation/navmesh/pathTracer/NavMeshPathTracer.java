package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathTracer;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshEdge;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshPolygon;
import math.geom2d.Point2D;
import math.geom2d.Vector2D;
import math.geom2d.line.LineSegment2D;
import math.geom2d.line.Ray2D;
import math.geom3d.Point3D;
import math.geom3d.line.LineSegment3D;

/** Ray-tracer for navigation mesh paths
 * 
 * Performs ray-tracing in 2D projection of navigation mesh.
 */
public class NavMeshPathTracer {


	/** Trace path (navmesh specialization)
	 * 
	 * Walks from start in a straight line and record the path (visited polygons, edges and crossings).
	 * 
	 * @param startPolygon polygon where the tracing starts from
	 * @param start exact location within the start polygon where the tracing starts from
	 * @param direction2d direction to go 
	 * @param keepGoingCondition condition that stops the algorithm when evaluated to false, such as distance limit 
	 * @return recorded path
	 */
	public static RayPath<NavMeshPolygon, NavMeshEdge> trace(
			NavMeshPolygon startPolygon,
			Location start,
			Vector2D direction2d,
			Predicate<RayPath<NavMeshPolygon,NavMeshEdge>> keepGoingCondition
	) {
		return trace(startPolygon, start, direction2d, XyProjectionNavMeshPathTraceContext.getInstance(), keepGoingCondition);
	}
	
	/** Trace path (generic version)
	 * 
	 * Walks from start in a straight line and record the path (visited polygons, edges and crossings).
	 * 
	 * @param startPolygon polygon where the tracing starts from
	 * @param start exact location within the start polygon where the tracing starts from
	 * @param direction2d direction to go 
	 * @param context implementation of operations needed by the algorithm
	 * @param keepGoingCondition condition that stops the algorithm when evaluated to false, such as distance limit 
	 * @return recorded path
	 */
	public static <TPolygon,TEdge> RayPath<TPolygon,TEdge> trace(
			TPolygon startPolygon,
			Location start,
			Vector2D direction2d,
			IPathTraceContext<TPolygon,TEdge> context,
			Predicate<RayPath<TPolygon,TEdge>> keepGoingCondition
	) {
		RayPath<TPolygon,TEdge> rayPath = new RayPath<TPolygon,TEdge>(startPolygon);
		
		Point2D start2d = context.project(start);
		Ray2D ray = new Ray2D( start2d, start2d.plus(direction2d) );
		
		TPolygon currentPolygon = startPolygon;
        TPolygon previousPolygon = null; // prevent searching backwards
        do {        	       	
            // Find intersection in with edge. There must be 2, front and back. Grab the front one.
        	TEdge intersectingEdge = null;
        	LineSegment2D intersectingEdge2d = null;
        	Point2D intersection2d = null;
            for (TEdge edge : context.getEdges(currentPolygon)) {
            	if (previousPolygon != null && previousPolygon.equals( context.getAdjacentPolygonByEdge(currentPolygon, edge) ) ) {
            		// skip this edge, it leads back
            		continue;
            	}
            	
            	LineSegment2D edge2d = new LineSegment2D(
                	context.project(context.getSourceVertex(edge)),
                	context.project(context.getDestinationVertex(edge))
                );
                
            	intersection2d = ray.getIntersection( edge2d );
                if ( intersection2d != null) {
                    intersectingEdge = edge;
                    intersectingEdge2d = edge2d;
                    break;
                } 
            }
            
            if (intersectingEdge == null) {
            	// MATH MAY FAIL, edge cases...
            	// => RETURN "hit"
            	if (rayPath.getSteps().size() == 0) return rayPath;
            	RayPath<TPolygon,TEdge>.PathStep lastStep = Iterables.getLast( rayPath.getSteps() );
            	if (lastStep == null) return rayPath;
            	lastStep.polygon = null;
            	return rayPath;
            }
            
            assert( intersectingEdge != null );
            assert( intersectingEdge2d != null );
            assert( intersection2d != null );
            
            LineSegment3D edge3d = new LineSegment3D(
            	context.getSourceVertex(intersectingEdge).asPoint3D(),
            	context.getDestinationVertex(intersectingEdge).asPoint3D()
            );
            
            // STRANGE, the following line is not working, theoretically sound...
            //Location intersection = new Location( edge3d.getPoint( intersectingEdge2d.project(intersection2d) ) );
            // REPLACING BY OWN IMPLEMENTATION
            Location edge3DDir = context.getDestinationVertex(intersectingEdge).sub(context.getSourceVertex(intersectingEdge));
            Location intersection = context.getSourceVertex(intersectingEdge).add(edge3DDir.scale(intersectingEdge2d.project(intersection2d)));
            
            previousPolygon = currentPolygon;
            currentPolygon = context.getAdjacentPolygonByEdge( currentPolygon, intersectingEdge );
            
            rayPath.addStep( intersection, intersectingEdge, currentPolygon );
        } while ( currentPolygon != null && keepGoingCondition.apply(rayPath) );
        
        return rayPath;
	}
}
