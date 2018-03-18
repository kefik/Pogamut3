package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathPlanner.polygonPathFunnel;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.NavMesh;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.NavMeshBuilder;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.PolygonId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.VertexId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshBoundary;

public class PolygonPathSmoothingFunnelAlgorithmTest {
	protected String map = "DM-Flux2";
	
	@Test
	public void testSharpTurnStop() throws IOException {
		Logger log = new LogCategory("polygonAnalysisAnalysisTest");
		
		//  1000  H _________I___________J
		//        |\         |         / |
		//        |   \      |      /    |     
		//        |      \   |   /       |
		//  0     E_________\F/__________G
		//        |         | |          |
		//        |   start|   |finish   |		
		// -1000  A       B     C        D
		//  Y
		//   X  -1000     -100 0 100     1000
		
		Location start =  new Location( -300, -500, 40 );
		Location finish = new Location(  300, -500, 40 );
		
		NavMeshBuilder navMeshBuilder = new NavMeshBuilder();
		
		navMeshBuilder.addPlayerStart(start);
		
		VertexId vertexAId = navMeshBuilder.addVertex( -1000, -1000, 0 );
		VertexId vertexBId = navMeshBuilder.addVertex(  -100, -1000, 0 );
		VertexId vertexCId = navMeshBuilder.addVertex(   100, -1000, 0 );
		VertexId vertexDId = navMeshBuilder.addVertex(  1000, -1000, 0 );
		VertexId vertexEId = navMeshBuilder.addVertex( -1000,     0, 0 );
		VertexId vertexFId = navMeshBuilder.addVertex(     0,     0, 0 );
		VertexId vertexGId = navMeshBuilder.addVertex(  1000,     0, 0 );
		VertexId vertexHId = navMeshBuilder.addVertex( -1000,  1000, 0 );
		VertexId vertexIId = navMeshBuilder.addVertex(     0,  1000, 0 );
		VertexId vertexJId = navMeshBuilder.addVertex(  1000,  1000, 0 );
		
		PolygonId polygonAbfeId = navMeshBuilder.addPolygon( vertexAId, vertexBId, vertexFId, vertexEId );
		PolygonId polygonEfhId = navMeshBuilder.addPolygon( vertexEId, vertexFId, vertexHId );
		PolygonId polygonHfiId = navMeshBuilder.addPolygon( vertexHId, vertexFId, vertexIId );
		PolygonId polygonIfjId = navMeshBuilder.addPolygon( vertexIId, vertexFId, vertexJId );
		PolygonId polygonJfgId = navMeshBuilder.addPolygon( vertexJId, vertexFId, vertexGId );
		PolygonId polygonGfcdId = navMeshBuilder.addPolygon( vertexGId, vertexFId, vertexCId, vertexDId );
		
		NavMesh navMesh = new NavMesh(log);
		navMesh.load( navMeshBuilder.getNavGraph(), navMeshBuilder );
		
		NavMeshBoundary boundaryEf = getBoundaryByPolygonIds( navMesh, polygonAbfeId, polygonEfhId );
		NavMeshBoundary boundaryHf = getBoundaryByPolygonIds( navMesh, polygonEfhId, polygonHfiId );
		NavMeshBoundary boundaryIf = getBoundaryByPolygonIds( navMesh, polygonHfiId, polygonIfjId );
		NavMeshBoundary boundaryJf = getBoundaryByPolygonIds( navMesh, polygonIfjId, polygonJfgId );
		NavMeshBoundary boundaryGf = getBoundaryByPolygonIds( navMesh, polygonJfgId, polygonGfcdId );
		List<NavMeshBoundary> boundaries = Arrays.asList( new NavMeshBoundary[] { boundaryEf, boundaryHf, boundaryIf, boundaryJf, boundaryGf } );
		
		List<ILocated> path = PolygonPathSmoothingFunnelAlgorithm.findShortestPathCrossings( start, boundaries, finish );
		assertTrue( path.size() == 1 && path.get(0).getLocation().equals( new Location( 0, 0, 40 ) ) ); // this is with NavMeshConstants.agentRadius == 0.0
	}
	
	NavMeshBoundary getBoundaryByPolygonIds( NavMesh navMesh, PolygonId polygonA, PolygonId polygonB ) {
		return navMesh.getPolygonById( polygonA ).getBoundaryByAdjPolygon( navMesh.getPolygonById( polygonB ) );
	}
}
