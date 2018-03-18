package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.construction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.NavMeshBoundaryInfo;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshBoundary;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshEdge;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshPolygon;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshVertex;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.OffMeshEdge;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.OffMeshPoint;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.EdgeId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.PolygonId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.VertexId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;

public class NodeConstructionCoordinator {
	protected ArrayList<IDeferredConstructor> deferredConstructors;
	
	protected HashMap<PolygonId, NavMeshPolygon> polygonIdToPolygonMap;
	protected HashMap<VertexId, NavMeshVertex> vertexIdToVertexMap;
	protected HashMap<EdgeId, NavMeshEdge> polygonEdgeIdToEdgeMap;
	protected HashMap<NavMeshBoundaryInfo, NavMeshBoundary> boundaryInfoToBoundaryMap;
	protected HashMap<NavPoint, OffMeshPoint> offMeshNavPointToOffMeshPointMap;
	protected HashMap<NavPointNeighbourLink, OffMeshEdge> offMeshNavLinkToOffMeshEdgeMap;
	
	
	public NodeConstructionCoordinator() {
		deferredConstructors = Lists.newArrayList();
		
		polygonIdToPolygonMap = Maps.newHashMap();
		vertexIdToVertexMap = Maps.newHashMap();
		polygonEdgeIdToEdgeMap = Maps.newHashMap();
		boundaryInfoToBoundaryMap = Maps.newHashMap();
		offMeshNavPointToOffMeshPointMap = Maps.newHashMap();
		offMeshNavLinkToOffMeshEdgeMap = Maps.newHashMap();
	}
	
	// deferred constructor registry
			
	public void addDeferredConstructor(IDeferredConstructor deferredConstructor) {
		deferredConstructors.add(deferredConstructor);
		
	}
	
	public void runDeferredConstructors() {
		for (IDeferredConstructor deferredConstructor : deferredConstructors) {
			deferredConstructor.construct();
		}
		deferredConstructors.clear();
	}
	
	// stub registry
	
	public NavMeshVertex getVertexById(VertexId vertexId) {
		assert( vertexIdToVertexMap.containsKey( vertexId ) );
		return vertexIdToVertexMap.get(vertexId);
	}

	public NavMeshPolygon getPolygonById(PolygonId polygonId) {
		assert( polygonIdToPolygonMap.containsKey( polygonId ) );
		return polygonIdToPolygonMap.get(polygonId);
	}

	public NavMeshEdge getEdgeById(EdgeId edgeId) {
		assert( polygonEdgeIdToEdgeMap.containsKey( edgeId ) );
		return polygonEdgeIdToEdgeMap.get( edgeId );
	}
	
	public NavMeshBoundary getBoundaryByBoundaryInfo( NavMeshBoundaryInfo boundaryInfo ) {
		assert( boundaryInfoToBoundaryMap.containsKey( boundaryInfo ) );
		return boundaryInfoToBoundaryMap.get( boundaryInfo );
	}

	public OffMeshPoint getOffMeshPointByNavPoint(NavPoint offMeshNavPoint) {
		assert( offMeshNavPointToOffMeshPointMap.containsKey( offMeshNavPoint ) );
		return offMeshNavPointToOffMeshPointMap.get(offMeshNavPoint);
	}

	public OffMeshEdge getOffMeshEdgeByNavLink(NavPointNeighbourLink link) {
		assert( offMeshNavLinkToOffMeshEdgeMap.containsKey( link ) );
		return offMeshNavLinkToOffMeshEdgeMap.get(link);
	}

	public void addVertex( NavMeshVertex vertex ) {
		vertexIdToVertexMap.put( vertex.getId(), vertex);
	}

	public void addPolygon( NavMeshPolygon polygon ) {
		polygonIdToPolygonMap.put( polygon.getId(), polygon );
	}
	
	public void addPolygonEdge( NavMeshEdge edge ) {
		polygonEdgeIdToEdgeMap.put( edge.getId(), edge );
	}
	
	public void addBoundary( NavMeshBoundaryInfo boundaryInfo, NavMeshBoundary boundary ) {
		boundaryInfoToBoundaryMap.put( boundaryInfo, boundary );
	}

	public void addOffMeshPoint( OffMeshPoint offMeshPoint ) {
		offMeshNavPointToOffMeshPointMap.put( offMeshPoint.getNavPoint(), offMeshPoint);
	}

	public void addOffMeshEdge( NavPointNeighbourLink link, OffMeshEdge edge ) {
		offMeshNavLinkToOffMeshEdgeMap.put( link, edge );
	}
	
	// output
	
	public HashSet<NavMeshPolygon> getPolygons() {
		return Sets.newHashSet( polygonIdToPolygonMap.values() );
	}

	public HashSet<NavMeshVertex> getVertices() {
		return Sets.newHashSet( vertexIdToVertexMap.values() );
	}

	public HashSet<OffMeshPoint> getOffMeshPoints() {
		return Sets.newHashSet( offMeshNavPointToOffMeshPointMap.values() );
	}
}