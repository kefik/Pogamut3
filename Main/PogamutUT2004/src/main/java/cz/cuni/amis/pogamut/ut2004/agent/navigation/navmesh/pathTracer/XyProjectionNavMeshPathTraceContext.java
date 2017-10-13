package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathTracer;

import java.util.List;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshEdge;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshPolygon;
import math.geom2d.Point2D;

public class XyProjectionNavMeshPathTraceContext implements IPathTraceContext<NavMeshPolygon, NavMeshEdge> {

	protected static XyProjectionNavMeshPathTraceContext instance = new XyProjectionNavMeshPathTraceContext();
	
	public static XyProjectionNavMeshPathTraceContext getInstance() {
		return instance;
	}
		
	@Override
	public List<NavMeshEdge> getEdges(NavMeshPolygon polygon) {
		return polygon.getEdges();
	}

	@Override
	public Location getSourceVertex(NavMeshEdge edge) {
		return edge.getSource().getLocation();
	}

	@Override
	public Location getDestinationVertex(NavMeshEdge edge) {
		return edge.getDestination().getLocation();
	}

	@Override
	public NavMeshPolygon getAdjacentPolygonByEdge(NavMeshPolygon polygon, NavMeshEdge edge) {
		return edge.getAdjacentPolygon();
	}

	@Override
	public Point2D project(Location location) {
		return new Point2D( location.getX(), location.getY() );
	}
}
