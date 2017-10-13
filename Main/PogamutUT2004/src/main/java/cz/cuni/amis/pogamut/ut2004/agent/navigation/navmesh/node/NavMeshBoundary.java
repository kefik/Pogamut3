package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node;

import java.io.Serializable;

import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.construction.IDeferredConstructor;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.construction.NodeConstructionCoordinator;
import math.geom2d.line.LineSegment2D;
import math.geom3d.line.LineSegment3D;

/** A line segment boundary between two adjacent navigation mesh polygons.
 * 
 * Immutable.
 */
public class NavMeshBoundary implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected NavMeshEdge edgeA;
	protected NavMeshEdge edgeB;
	
	public NavMeshBoundary(
			final int edgeAId,
			final int edgeBId,
			final NodeConstructionCoordinator coordinator
	) {
		coordinator.addDeferredConstructor(
			new IDeferredConstructor() {
				@Override
				public void construct() {
					 edgeA = coordinator.getEdgeById( edgeAId );
					 edgeB = coordinator.getEdgeById( edgeBId );
				}
			}
		);
	}
	
	public NavMeshEdge getEdgeA() {
		return edgeA;
	}
	
	public NavMeshEdge getEdgeB() {
		return edgeB;
	}
	
	public NavMeshVertex getSourceVertex() {
		return edgeA.getSource();
	}
	
	public NavMeshVertex getDestinationVertex() {
		return edgeA.getDestination();
	}
	
	public LineSegment2D asLineSegment2DInXyProjection() {
		return edgeA.asLineSegment2DInXyProjection();
	}
	
	public LineSegment3D asLineSegment3D() {
		return edgeA.asLineSegment3D();
	}
	
	@Override
	public String toString() {
		return "NMB( "+getSourceVertex().toString()+", "+getDestinationVertex().toString()+" )";
	}
}
