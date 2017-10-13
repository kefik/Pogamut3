package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal;

/** 
 * 
 */
public class NavMeshBoundaryInfo {
	public int sourceVertexId;
	public int destinationVertexId;
	
	public int polygonAId;
	public int polygonAEdgeIndex;

	public int polygonBId;
	public int polygonBEdgeIndex;
	
	public NavMeshBoundaryInfo(
		int sourceVertexId,
		int destinationVertexId,
		int polygonAId,
		int polygonAEdgeIndex, 
		int polygonBId,
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
