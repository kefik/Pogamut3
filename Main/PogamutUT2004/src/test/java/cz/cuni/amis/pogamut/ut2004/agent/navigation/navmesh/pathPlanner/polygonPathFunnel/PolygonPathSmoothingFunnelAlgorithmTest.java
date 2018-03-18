package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathPlanner.polygonPathFunnel;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.NavMesh;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.NavMeshBuilder;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.PolygonId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.VertexId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshBoundary;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;

public class PolygonPathSmoothingFunnelAlgorithmTest {
	protected String map = "DM-Flux2";
	
	@Test
	public void testSharpTurnStop() throws IOException {
		Logger log = new LogCategory("polygonAnalysisAnalysisTest");
		
		//  1  H _________I___________J
		//     |\         |         / |
		//     |   \      |      /    |     
		//     |      \   |   /       |
		//  0  E_________\F/__________G
		//     |         | |          |
		//     | start  |   |  finish |		
		// -1  A       B     C        D
		//
		//    -1     -0.1 0 0.1       1
		
		Location start = new Location( -1, -0.5, 40 );
		Location finish = new Location( -1, 0.5, 40 );
		
		NavMeshBuilder navMeshBuilder = new NavMeshBuilder();
		
		VertexId vertexAId = navMeshBuilder.makeVertex( -1.0, -1.0, 0 );
		VertexId vertexBId = navMeshBuilder.makeVertex( -1.0, -0.1, 0 );
		VertexId vertexCId = navMeshBuilder.makeVertex( -1.0,  0.1, 0 );
		VertexId vertexDId = navMeshBuilder.makeVertex( -1.0,  1.0, 0 );
		VertexId vertexEId = navMeshBuilder.makeVertex(  0.0, -1.0, 0 );
		VertexId vertexFId = navMeshBuilder.makeVertex(  0.0,  0.0, 0 );
		VertexId vertexGId = navMeshBuilder.makeVertex(  0.0,  1.0, 0 );
		VertexId vertexHId = navMeshBuilder.makeVertex(  1.0, -1.0, 0 );
		VertexId vertexIId = navMeshBuilder.makeVertex(  1.0,  0.0, 0 );
		VertexId vertexJId = navMeshBuilder.makeVertex(  1.0,  1.0, 0 );
		
		PolygonId polygonAbfeId = navMeshBuilder.makePolygon( vertexAId, vertexBId, vertexCId, vertexDId );
		PolygonId polygonEfhId = navMeshBuilder.makePolygon( vertexEId, vertexFId, vertexHId );
		PolygonId polygonHfiId = navMeshBuilder.makePolygon( vertexHId, vertexFId, vertexIId );
		PolygonId polygonIfjId = navMeshBuilder.makePolygon( vertexIId, vertexFId, vertexJId );
		PolygonId polygonJfgId = navMeshBuilder.makePolygon( vertexJId, vertexFId, vertexGId );
		PolygonId polygonGfcdId = navMeshBuilder.makePolygon( vertexGId, vertexFId, vertexCId, vertexDId );
		
		NavMesh navMesh = new NavMesh(log);
		navMesh.load( new HashMap<UnrealId,NavPoint>(), navMeshBuilder);
		
		NavMeshBoundary boundaryEf = navMesh.getPolygonById( polygonAbfeId ).getBoundaryByAdjPolygon( navMesh.getPolygonById( polygonEfhId ) );
		NavMeshBoundary boundaryHf = navMesh.getPolygonById( polygonEfhId ).getBoundaryByAdjPolygon( navMesh.getPolygonById( polygonHfiId ) );
		NavMeshBoundary boundaryIf = navMesh.getPolygonById( polygonHfiId ).getBoundaryByAdjPolygon( navMesh.getPolygonById( polygonIfjId ) );
		NavMeshBoundary boundaryJf = navMesh.getPolygonById( polygonIfjId ).getBoundaryByAdjPolygon( navMesh.getPolygonById( polygonJfgId ) );
		NavMeshBoundary boundaryGf = navMesh.getPolygonById( polygonJfgId ).getBoundaryByAdjPolygon( navMesh.getPolygonById( polygonGfcdId ) );
		List<NavMeshBoundary> boundaries = Arrays.asList( new NavMeshBoundary[] { boundaryEf, boundaryHf, boundaryIf, boundaryJf, boundaryGf } );
		
		PolygonPathSmoothingFunnelAlgorithm.findShortestPathCrossings( start, boundaries, finish );
		assertTrue( true );
	}
}
