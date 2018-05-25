package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.construction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.LineSegmentAnalysis;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.NavGraphAnalysis;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.NavMeshBoundaryInfo;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.PolygonAnalysis;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.ReachabilityAnalysis;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.bsp.XyProjectionPolygonPartitioningStrategy;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshBoundary;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshEdge;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshPolygon;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshVertex;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.OffMeshEdge;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.OffMeshPoint;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.internal.NavMeshNavGraphGlue;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;
import math.bsp.BspTree;
import math.geom2d.line.StraightLine2D;

/** Nav mesh construction
 * 
 * Takes results of analysis and constructs the object model of navmesh.
 */
public class NavMeshConstruction {
	
	protected NavMeshNavGraphGlue navGraphGlue;
	protected HashSet<NavMeshPolygon> polygons;
	protected HashSet<NavMeshVertex> vertices;
	protected HashSet<OffMeshPoint> offMeshPoints;
	protected BspTree<ArrayList<NavMeshPolygon>, StraightLine2D> xyProjectionBsp;
	
	public NavMeshConstruction(
		final Map<UnrealId, NavPoint> navGraph,
		final ReachabilityAnalysis reachabilityAnalysis,
		final PolygonAnalysis polygonAnalysis,
		final LineSegmentAnalysis lineSegmentAnalysis,
		final NavGraphAnalysis navGraphAnalysis
	) {
		// create coordinator 
		
		NodeConstructionCoordinator coordinator = new NodeConstructionCoordinator();
		
		// construct objects
		
		navGraphGlue = new NavMeshNavGraphGlue( new Function<UnrealId, NavPoint>() {
			@Override
			public NavPoint apply(UnrealId input) {
				return navGraph.get(input);
			}}
		);
		
		for ( int reachablePolygonId : reachabilityAnalysis.reachablePolygons ) {
			PolygonAnalysis.PolygonInfo paPolygonInfo = polygonAnalysis.polygonIdToInfoMap.get(reachablePolygonId);
			LineSegmentAnalysis.PolygonInfo lsaPolygonInfo = lineSegmentAnalysis.getPolygonInfo(reachablePolygonId);
			
			
			ArrayList<Integer> edgeIds = Lists.newArrayList();
			for ( int vertexIndex = 0; vertexIndex < paPolygonInfo.vertexIds.size(); ++vertexIndex ) {
				int edgeIndex = vertexIndex; // these two are equal
				
				NavMeshEdge edge = new NavMeshEdge(
					paPolygonInfo.edgeIds.get( edgeIndex ),
					edgeIndex,
					reachablePolygonId,
					paPolygonInfo.vertexIds.get( vertexIndex ),
					paPolygonInfo.vertexIds.get( (vertexIndex+1)%paPolygonInfo.vertexIds.size() ),
					lsaPolygonInfo.edgeIndexToBoundaryInfoMap.get(edgeIndex),
					coordinator
				);
				coordinator.addPolygonEdge( edge );
				edgeIds.add( edge.getId() );
			}
			
			coordinator.addPolygon( 
				new NavMeshPolygon(
					reachablePolygonId,
					paPolygonInfo.vertexIds,
					edgeIds,
					lsaPolygonInfo.edgeIndexToBoundaryInfoMap,
					lsaPolygonInfo.adjPolygonIdToBoundaryInfoMap,
					navGraphAnalysis.getOffMeshPointsByPolygonId(reachablePolygonId),
					coordinator 
				) 
			);
		}
		
		for ( Integer reachableVertexId : reachabilityAnalysis.reachableVertices ) {
			PolygonAnalysis.VertexInfo paVertexInfo = polygonAnalysis.vertexIdToInfoMap.get(reachableVertexId);
			LineSegmentAnalysis.VertexInfo lsaVertexInfo = lineSegmentAnalysis.getVertexInfo(reachableVertexId);

			HashMap<Integer,Integer> polygonIdToVertexIndexMap = Maps.newHashMap();
			for ( Entry<Integer, Integer> entry : paVertexInfo.containingPolygonIdToVertexIndexMap.entrySet() ) {
				polygonIdToVertexIndexMap.put( entry.getKey(), entry.getValue() );
			}
			
			coordinator.addVertex( 
				new NavMeshVertex(
					reachableVertexId,
					paVertexInfo.location,
					polygonIdToVertexIndexMap,
					lsaVertexInfo.edgeIds,
					lsaVertexInfo.isOnWalkableAreaEdge,
					coordinator 
				)
			);
		}
		
		for ( NavMeshBoundaryInfo boundaryInfo : reachabilityAnalysis.reachableBoundaries ) {
			coordinator.addBoundary(
				boundaryInfo,
				new NavMeshBoundary(
					polygonAnalysis.polygonIdToInfoMap.get( boundaryInfo.polygonAId ).edgeIds.get( boundaryInfo.polygonAEdgeIndex ),
					polygonAnalysis.polygonIdToInfoMap.get( boundaryInfo.polygonBId ).edgeIds.get( boundaryInfo.polygonBEdgeIndex ),
					coordinator
				)
			);
		}
		
		for ( NavPoint offMeshNavPoint : reachabilityAnalysis.reachableOffMeshNavPoints ) {
			NavGraphAnalysis.NavPointInfo navPointInfo = navGraphAnalysis.getNavPointInfo(offMeshNavPoint);
			
			coordinator.addOffMeshPoint(
				new OffMeshPoint(
					navGraphGlue,
					offMeshNavPoint,
					navPointInfo.polygonId,
					navPointInfo.outgoingOffMeshEdges,
					navPointInfo.incommingOffMeshEdges,
					coordinator
				)
			);
		}
		
		for ( NavPointNeighbourLink offMeshNavLink : reachabilityAnalysis.reachableOffMeshNavLinks ) {
			coordinator.addOffMeshEdge(
				offMeshNavLink,
				new OffMeshEdge( offMeshNavLink, coordinator )
			);
		}
		
		coordinator.runDeferredConstructors();
		
		polygons = coordinator.getPolygons();
		vertices = coordinator.getVertices();
		offMeshPoints = coordinator.getOffMeshPoints();
		
		XyProjectionPolygonPartitioningStrategy partitioningStrategy = new XyProjectionPolygonPartitioningStrategy();
		
		xyProjectionBsp = BspTree.make(
				partitioningStrategy,
			new ArrayList<NavMeshPolygon>( polygons )
		);
		
		partitioningStrategy.clearCache();
	}
	
    public Set<NavMeshPolygon> getPolygons() {
		return Collections.unmodifiableSet( polygons );
	}
	
	public Set<NavMeshVertex> getVertices() {
		return Collections.unmodifiableSet( vertices );
	}
	
	public Set<OffMeshPoint> getOffMeshPoints() {
		return Collections.unmodifiableSet( offMeshPoints );
	}
	
	public BspTree<ArrayList<NavMeshPolygon>, StraightLine2D> getXyProjectionBsp() {
		return xyProjectionBsp;
	}

	public NavMeshNavGraphGlue getNavGraphGlue() {
		return navGraphGlue;
	}
}
