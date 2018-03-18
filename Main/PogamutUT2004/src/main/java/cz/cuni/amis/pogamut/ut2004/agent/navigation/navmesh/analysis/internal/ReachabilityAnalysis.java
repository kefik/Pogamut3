package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

import com.google.common.collect.Maps;
import com.google.inject.internal.Sets;

import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.PolygonId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.VertexId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;

public class ReachabilityAnalysis {

	public HashSet<PolygonId> reachablePolygons = Sets.newHashSet();
	public HashSet<VertexId> reachableVertices = Sets.newHashSet();
	public HashSet<NavMeshBoundaryInfo> reachableBoundaries = Sets.newHashSet();
	public HashSet<NavPoint> reachableOffMeshNavPoints = Sets.newHashSet();
	public HashSet<NavPointNeighbourLink> reachableOffMeshNavLinks = Sets.newHashSet();
	public HashMap<VertexId,HashSet<PolygonId>> vertexIdToContainingPolygonsMap = Maps.newHashMap();
		
	/**
     * Some polygons cannot be reached we find them with the help of navigation
     * graph Definition: 1. Any polygon with navigation point is reachable 2.
     * Any polygon sharing edge with a reachable polygon is also reachable.
     */
    public ReachabilityAnalysis(
    	PolygonAnalysis polygonAnalysis,
    	LineSegmentAnalysis lineSegmentAnalysis,
    	NavGraphAnalysis navGraphAnalysis,
    	Logger log
    ) {
        if (navGraphAnalysis.navPointToInfoMap.values().isEmpty()) {
            log.warning("There are no navpoints present within the worldview, could not analyze reachability.");
            return;
        }
        
        // navigation graph is expected to be sane, so all off-mesh nav points are reachable
        reachableOffMeshNavPoints.addAll( navGraphAnalysis.offMeshNavPoints );
        reachableOffMeshNavLinks.addAll( navGraphAnalysis.offMeshNavLinks );
        
        for (NavGraphAnalysis.NavPointInfo navPointInfo : navGraphAnalysis.navPointToInfoMap.values()) {
            recursivelyMarkAsReachable( navPointInfo.polygonId, polygonAnalysis, lineSegmentAnalysis );       		
        }
        
        log.info("Reachability analysis: There are " + reachablePolygons.size() + " reachable polygons containing " + reachableVertices.size() + " vertices.");
        
        // create vertex ID to containing polygon map without unreachable polygons
        for (VertexId reachableVertexId : reachableVertices ) {
        	HashSet<PolygonId> containingPolygonIds = Sets.newHashSet();
        	containingPolygonIds.addAll( polygonAnalysis.vertexIdToInfoMap.get(reachableVertexId).containingPolygonIdToVertexIndexMap.keySet() );
        	containingPolygonIds.retainAll(reachablePolygons);
        	vertexIdToContainingPolygonsMap.put( reachableVertexId, containingPolygonIds );
        }
    }
    
    /** Recursively mark as reachable
     *
     * @param polygonId ID of a reachable polygon or null
     */
    protected void recursivelyMarkAsReachable(
    	PolygonId polygonId,
    	PolygonAnalysis polygonAnalysis,
    	LineSegmentAnalysis lineSegmentAnalysis
    ) {
        if (polygonId == null
        	|| 
        	reachablePolygons.contains(polygonId) // already recursively marked 
        ) {
            return;
        }
        
        reachablePolygons.add(polygonId);
        reachableVertices.addAll( polygonAnalysis.polygonIdToInfoMap.get(polygonId).vertexIds );
        reachableBoundaries.addAll( lineSegmentAnalysis.polygonIdToInfoMap.get(polygonId).edgeIndexToBoundaryInfoMap.values() );
        
        for ( PolygonId adjacentPolygonId : lineSegmentAnalysis.getPolygonInfo(polygonId).adjPolygonIdToBoundaryInfoMap.keySet() ) {
        	recursivelyMarkAsReachable( adjacentPolygonId, polygonAnalysis, lineSegmentAnalysis );
        }
    }
}
