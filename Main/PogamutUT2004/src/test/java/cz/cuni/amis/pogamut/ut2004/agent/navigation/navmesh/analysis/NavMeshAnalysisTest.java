package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navgraph.NavGraph;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.NavMeshCache;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.LineSegmentAnalysis;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.NavGraphAnalysis;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.PolygonAnalysis;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.ReachabilityAnalysis;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.construction.NavMeshConstruction;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.file.RawNavMeshFile;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.PolygonId;

public class NavMeshAnalysisTest {

	protected String map = "DM-Flux2";
	
	@Test
	public void polygonAnalysisAnalysisTest() throws IOException {
		final NavGraph navGraph = new NavGraph( new File( map + ".navgraph" ) );
		Logger log = new LogCategory("NavMeshAnalysisTest");
		
		String rawNavMeshFileName = NavMeshCache.pureMeshReadDir + "/" + map + ".navmesh";
    	RawNavMeshFile rawNavMeshFile = new RawNavMeshFile( new File(rawNavMeshFileName) );
		PolygonAnalysis polygonAnalysis = new PolygonAnalysis(rawNavMeshFile);
		
		PolygonId polygonIdBelow = polygonAnalysis.getPolygonIdBelow( new Location( -405.00, -354.00, -328.00 ) );
		assertEquals( "Grounding test failed.", 350, polygonIdBelow.getValue() );
		
		LineSegmentAnalysis lineSegmentAnalysis = new LineSegmentAnalysis(polygonAnalysis);
		NavGraphAnalysis navGraphAnalysis = new NavGraphAnalysis( navGraph.navPointsById, polygonAnalysis, lineSegmentAnalysis );
		ReachabilityAnalysis reachabilityAnalysis = new ReachabilityAnalysis( polygonAnalysis, lineSegmentAnalysis, navGraphAnalysis, log );
		NavMeshConstruction navMeshConstruction = new NavMeshConstruction( navGraph.navPointsById, reachabilityAnalysis, polygonAnalysis, lineSegmentAnalysis, navGraphAnalysis );
		
		assertNotNull(navMeshConstruction);
	}
}
