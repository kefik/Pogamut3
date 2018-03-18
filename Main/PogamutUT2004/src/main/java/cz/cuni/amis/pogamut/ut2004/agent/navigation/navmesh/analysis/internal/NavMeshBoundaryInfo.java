package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal;

import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.PolygonId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.VertexId;

/** 
 * 
 */
public class NavMeshBoundaryInfo {
	public VertexId sourceVertexId;
	public VertexId destinationVertexId;
	
	public PolygonId polygonAId;
	public int polygonAEdgeIndex;

	public PolygonId polygonBId;
	public int polygonBEdgeIndex;
	
	public NavMeshBoundaryInfo(
		VertexId sourceVertexId,
		VertexId destinationVertexId,
		PolygonId polygonAId,
		int polygonAEdgeIndex, 
		PolygonId polygonBId,
		int polygonBEdgeIndex
	) {
		this.sourceVertexId = sourceVertexId;
		this.destinationVertexId = destinationVertexId;
		this.polygonAId = polygonAId;
		this.polygonAEdgeIndex = polygonAEdgeIndex;
		this.polygonBId = polygonBId;
		this.polygonBEdgeIndex = polygonBEdgeIndex;
	}
}
