package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathTracer;

import java.util.List;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import math.geom2d.Point2D;

/** Path trace context
 * 
 * Interface of operations needed by path trace algorithm.
 */
public interface IPathTraceContext<TPolygon,TEdge> {
	List<TEdge> getEdges(TPolygon polygon);
	Location getSourceVertex(TEdge edge);
	Location getDestinationVertex(TEdge edge);
	TPolygon getAdjacentPolygonByEdge(TPolygon polygon, TEdge edge);
	Point2D project(Location location);
}