package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.LineSegmentAnalysis;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.NavGraphAnalysis;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.PolygonAnalysis;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.ReachabilityAnalysis;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.construction.NavMeshConstruction;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshPolygon;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshVertex;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.OffMeshPoint;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.internal.NavMeshNavGraphGlue;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import math.bsp.BspTree;
import math.geom2d.line.StraightLine2D;

public class NavMeshAnalysis {

	protected NavMeshConstruction navMeshConstruction;
	
	public NavMeshAnalysis( IRawNavMesh rawNavMesh, Map<UnrealId, NavPoint> navGraph, Logger log) {
		
		// analysis
		
		PolygonAnalysis polygonAnalysis = new PolygonAnalysis(rawNavMesh);
		LineSegmentAnalysis lineSegmentAnalysis = new LineSegmentAnalysis(polygonAnalysis);
		NavGraphAnalysis navGraphAnalysis = new NavGraphAnalysis( navGraph, polygonAnalysis, lineSegmentAnalysis );
		ReachabilityAnalysis reachabilityAnalysis = new ReachabilityAnalysis( polygonAnalysis, lineSegmentAnalysis, navGraphAnalysis, log );
		navMeshConstruction = new NavMeshConstruction( navGraph, reachabilityAnalysis, polygonAnalysis, lineSegmentAnalysis, navGraphAnalysis );
	}

    public Set<NavMeshPolygon> getPolygons() {
		return navMeshConstruction.getPolygons();
	}
	
	public Set<NavMeshVertex> getVertices() {
		return navMeshConstruction.getVertices();
	}
	
	public Set<OffMeshPoint> getOffMeshPoints() {
		return navMeshConstruction.getOffMeshPoints();
	}
	
	public BspTree<ArrayList<NavMeshPolygon>, StraightLine2D> getXyProjectionBsp() {
		return navMeshConstruction.getXyProjectionBsp();
	}

	public NavMeshNavGraphGlue getNavGraphGlue() {
		return navMeshConstruction.getNavGraphGlue();
	}
}
