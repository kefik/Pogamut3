package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/** Adjacency analysis
 * 
 * Go through polygon edges and find those that are shared by two polygons.
 */
public class LineSegmentAnalysis {
	// (vertexId, vertexId) -> incidence
	protected HashMap<Integer,HashMap<Integer,Integer>> vertexIncidenceMatrix = Maps.newHashMap();  
	protected HashMap<Integer, PolygonInfo> polygonIdToInfoMap = Maps.newHashMap();
	protected HashMap<Integer, VertexInfo> vertexIdToInfoMap = Maps.newHashMap();
	
	public LineSegmentAnalysis( PolygonAnalysis polygonAnalysis ) {
		// map for remembering encountered line segments
		HashMap<LineSegment, PolygonEdgeInfo> lineSegmentToEdgeInfoMap = Maps.newHashMap();
		HashMap<LineSegment,NavMeshBoundaryInfo> lineSegmentToBoundaryMap = Maps.newHashMap();
		
		// loop through all line segments in all polygons and identify boundaries
		for ( int polygonId : polygonAnalysis.allPolygonIds ) {
			PolygonAnalysis.PolygonInfo polygonInfo = polygonAnalysis.polygonIdToInfoMap.get(polygonId);
			ArrayList<Integer> vertexIds = polygonInfo.vertexIds;
			
			for ( int vertexIndex = 0; vertexIndex < vertexIds.size(); ++vertexIndex ) {
				int vertexIdA = vertexIds.get( vertexIndex );
				int vertexIdB = vertexIds.get( (vertexIndex+1)%vertexIds.size() );
				int edgeIndex = vertexIndex;
				
				getVertexInfo(vertexIdA).edgeIds.add( polygonInfo.edgeIds.get(edgeIndex) );
				getVertexInfo(vertexIdB).edgeIds.add( polygonInfo.edgeIds.get(edgeIndex) );				
				
				incrementIncidency(vertexIdA, vertexIdB);
				
				LineSegment lineSegment;
				if ( vertexIdA < vertexIdB ) {
					lineSegment = new LineSegment(vertexIdA, vertexIdB);
				} else {
					lineSegment = new LineSegment(vertexIdB, vertexIdA);
				}
				
				PolygonEdgeInfo curEdgeInfo = new PolygonEdgeInfo( polygonId, edgeIndex );
				
				if ( lineSegmentToEdgeInfoMap.containsKey(lineSegment) ) { // LineSegment equals/hashCode is order-insensitive 
					// line segment has been previously found in another polygon, so create boundary
					assert( !lineSegmentToBoundaryMap.containsKey(lineSegment) ) : "Edge shared by three poligons.";
					PolygonEdgeInfo adjEdgeInfo = lineSegmentToEdgeInfoMap.get(lineSegment);
					NavMeshBoundaryInfo boundary = new NavMeshBoundaryInfo(
						vertexIdA, 
						vertexIdB, 
						curEdgeInfo.polygonId,
						curEdgeInfo.edgeIndex,
						adjEdgeInfo.polygonId,
						adjEdgeInfo.edgeIndex
					);
					
					lineSegmentToBoundaryMap.put(lineSegment, boundary);
					
					getPolygonInfo(curEdgeInfo.polygonId).adjPolygonIdToBoundaryInfoMap.put(adjEdgeInfo.polygonId, boundary);
					getPolygonInfo(adjEdgeInfo.polygonId).adjPolygonIdToBoundaryInfoMap.put(curEdgeInfo.polygonId, boundary);
					
					getPolygonInfo(curEdgeInfo.polygonId).edgeIndexToBoundaryInfoMap.put( curEdgeInfo.edgeIndex, boundary );
					getPolygonInfo(adjEdgeInfo.polygonId).edgeIndexToBoundaryInfoMap.put( adjEdgeInfo.edgeIndex, boundary );
				} else {
					// line segment encountered for the first time, remember where
					lineSegmentToEdgeInfoMap.put( lineSegment, curEdgeInfo );
				}
			}
		}
		
		for ( Integer vertexId : polygonAnalysis.allVertexIds ) {
			vertexIdToInfoMap.put( vertexId, new VertexInfo() );
		}
		
		for ( Integer vertexId : polygonAnalysis.allVertexIds ) {
			for (Integer incidenceCount : getIncidence(vertexId).values()) {
				assert(0 <= incidenceCount && incidenceCount <= 2);
				if (incidenceCount == 1) {
					// if vertex is internal, then all edges with that vertex must be boundaries and incidence == 2
					// therefore this one is not internal
					getVertexInfo(vertexId).isOnWalkableAreaEdge = true;
					break;
				}
			}
		}
	}
	
	/** Get incidence
	 * 
	 * @return how many times line segment (vertexA, vertexB) poses as polygon edge
	 */
	public Integer getIncidency(int vertexAId, int vertexBId) {
		HashMap<Integer,Integer> incidencyOfA = getIncidence(vertexAId);
		if (!incidencyOfA.containsKey(vertexBId)) {
			return 0;
		} else {
			return incidencyOfA.get(vertexBId);
		}
	}
	
	protected void incrementIncidency(int vertexIdA, int vertexIdB) {
		int previousIncidency = getIncidency(vertexIdA, vertexIdB);
		getIncidence(vertexIdA).put(vertexIdB, previousIncidency+1);
		getIncidence(vertexIdB).put(vertexIdA, previousIncidency+1);
	}
	
	public HashMap<Integer,Integer> getIncidence(int vertexId) {
		if (!vertexIncidenceMatrix.containsKey(vertexId)) {
			vertexIncidenceMatrix.put(vertexId, new HashMap<Integer,Integer>());
		}
		return vertexIncidenceMatrix.get(vertexId);
	}

	public PolygonInfo getPolygonInfo(int polygonId) {
		if ( !polygonIdToInfoMap.containsKey(polygonId) ) {
			polygonIdToInfoMap.put(polygonId, new PolygonInfo());
		}
		return polygonIdToInfoMap.get(polygonId);
	}
	
	public VertexInfo getVertexInfo( int vertexId ) {
		if ( !vertexIdToInfoMap.containsKey(vertexId) ) {
			vertexIdToInfoMap.put(vertexId, new VertexInfo());
		}
		return vertexIdToInfoMap.get(vertexId);		
	}
	
	public static class PolygonInfo {
		public HashMap<Integer, NavMeshBoundaryInfo> edgeIndexToBoundaryInfoMap = Maps.newHashMap();
		public HashMap<Integer, NavMeshBoundaryInfo> adjPolygonIdToBoundaryInfoMap = Maps.newHashMap();
	}
	
	public static class VertexInfo {
		public boolean isOnWalkableAreaEdge = false;
		public ArrayList<Integer> edgeIds = Lists.newArrayList();
	}
	
	protected static class PolygonEdgeInfo {
		public int polygonId;
		public int edgeIndex;
		
		public PolygonEdgeInfo( int polygonId, int edgeIndex ) {
			this.polygonId = polygonId;
			this.edgeIndex = edgeIndex;
		}
	}
	
	protected static class LineSegment {
		protected int vertexAId;
		protected int vertexBId;
		
		public LineSegment(int vertexAId, int vertexBId) {
			this.vertexAId = vertexAId;
			this.vertexBId = vertexBId;
		}
		
		public int getVertexAId() {
			return vertexAId;
		}
		
		public int getVertexBId() {
			return vertexBId;
		}
		
		@Override
		public int hashCode() {
			return vertexAId+2*vertexBId;
		}
		
		@Override
		public boolean equals(Object other) {
			if (other instanceof LineSegment) {
				LineSegment otherLineSegment = (LineSegment) other;
				return (
					vertexAId == otherLineSegment.vertexAId
					&&
					vertexBId == otherLineSegment.vertexBId
				); 
			}
			
			return false;
		}
	}
}