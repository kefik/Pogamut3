package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathTracer;

import java.util.List;

import com.google.common.collect.Lists;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;

/** Ray traced path
 *
 * Immutable once computed.
 * 
 * @param <TPolygon>
 * @param <TEdge>
 */
public class RayPath<TPolygon,TEdge>{
	protected List<TPolygon> polygons = Lists.newArrayList();
	protected List<TEdge> edges = Lists.newArrayList();
	protected List<Location> intersections = Lists.newArrayList();
	
	public RayPath() {
	}

	public List<TPolygon> getPolygons() {
		return polygons;
	}

	public List<TEdge> getEdges() {
		return edges;
	}

	public List<Location> getIntersections() {
		return intersections;
	}
}