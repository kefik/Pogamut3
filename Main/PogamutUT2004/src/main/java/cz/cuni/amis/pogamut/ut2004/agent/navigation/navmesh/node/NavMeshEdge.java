package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node;

import java.io.Serializable;
import java.util.List;

import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.NavMeshBoundaryInfo;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.construction.IDeferredConstructor;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.construction.NodeConstructionCoordinator;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.EdgeId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.PolygonId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.VertexId;
import math.geom2d.line.LineSegment2D;
import math.geom3d.line.LineSegment3D;

/** Edge of a nav mesh polygon
 * 
 * Not to be confused with {@link OffMeshEdge}.
 * Immutable.
 */
public class NavMeshEdge implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected EdgeId id;
	protected int index;
	protected NavMeshPolygon polygon;
	protected NavMeshVertex source;
	protected NavMeshVertex destination;
	protected NavMeshBoundary boundary;
	
	public NavMeshEdge(
			EdgeId id,
			int index,
			final PolygonId polygonId,
			final VertexId sourceVertexId,
			final VertexId destinationVertexId,
			final NavMeshBoundaryInfo boundaryInfo,
			final NodeConstructionCoordinator coordinator
	) {
		this.id = id;
		this.index = index;
		
		coordinator.addDeferredConstructor(
			new IDeferredConstructor() {
				@Override
				public void construct() {
					
					polygon = coordinator.getPolygonById(polygonId);
					source = coordinator.getVertexById( sourceVertexId );
					destination = coordinator.getVertexById( destinationVertexId );
					
					if ( boundaryInfo != null ) {
						boundary = coordinator.getBoundaryByBoundaryInfo(boundaryInfo);
					} else {
						boundary = null;
					}
					
				}
			}
		);
	}
	
	public NavMeshPolygon getPolygon() {
		return polygon;
	}

	public NavMeshVertex getSource() {
		return source;
	}

	public NavMeshVertex getDestination() {
		return destination;
	}
	
	/** Get the index of the edge among the edges of the polygon
	 */
	public int getIndex() {
		return index;
	}
	
	/** Get the boundary that uses this edge, or null if there is none
	 */
	public NavMeshBoundary getBoundary() {
		return boundary;
	}
	
	/** Get adjacent edge (edge sharing same vertices of the polygon adjacent to this edge), or null if there is none 
	 */
	public NavMeshEdge getAdjacentEdge() {
		if ( boundary != null ) {
			NavMeshEdge edgeA = boundary.getEdgeA();
			NavMeshEdge edgeB = boundary.getEdgeB();
			
			if ( edgeA != this ) {
				return edgeA;
			} else {
				return edgeB;
			}
		} else {
			return null;
		}
	}
	
	/** Get polygon adjacent to this edge, or null if there is none 
	 */
	public NavMeshPolygon getAdjacentPolygon() {
		if ( boundary != null ) {
			return getAdjacentEdge().getPolygon();
		} else {
			return null;
		}
	}
	
	/** Get next edge in the polygon (the one sharing the destination vertex)
	 */
	public NavMeshEdge getNextEdge() {
		List<NavMeshEdge> edges = getPolygon().getEdges(); 
		return edges.get( (index + 1) % edges.size() );
	}
	
	/** Get previous edge in the polygon (the one sharing the source vertex)
	 */
	public NavMeshEdge getPreviousEdge() {
		List<NavMeshEdge> edges = getPolygon().getEdges(); 
		return edges.get( (index - 1 + edges.size()) % edges.size() );
	}
	
	public LineSegment2D asLineSegment2DInXyProjection() {
		return new LineSegment2D( 
			source.getLocation().getX(),
			source.getLocation().getY(),
			destination.getLocation().getX(),
			destination.getLocation().getY()
		);
	}
	
	public LineSegment3D asLineSegment3D() {
		return new LineSegment3D(
			source.getLocation().asPoint3D(),
			destination.getLocation().asPoint3D()
		);
	}
	
	public EdgeId getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return "NME( "+id+", "+source+", "+destination+" )";
	}
}