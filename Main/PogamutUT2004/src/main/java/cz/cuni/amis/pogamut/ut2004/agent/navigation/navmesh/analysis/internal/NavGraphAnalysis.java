package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.inject.internal.Lists;
import com.google.inject.internal.Sets;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004EdgeChecker;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.PolygonId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.VertexId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathTracer.IPathTraceContext;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathTracer.NavMeshPathTracer;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathTracer.RayPath;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;
import math.geom2d.Point2D;
import math.geom2d.Vector2D;

public class NavGraphAnalysis {
	protected HashMap<PolygonId,ArrayList<NavPoint>> polygonIdToOffMeshPointsMap = Maps.newHashMap();
	public HashMap<NavPoint,NavPointInfo> navPointToInfoMap = Maps.newHashMap();
	public HashSet<NavPoint> offMeshNavPoints = Sets.newHashSet();
	public HashSet<NavPointNeighbourLink> offMeshNavLinks = Sets.newHashSet();

	public NavGraphAnalysis(
		Map<UnrealId, NavPoint> navGraph,
		PolygonAnalysis polygonAnalysis,
		LineSegmentAnalysis lineSegmentAnalysis
	) {
		
		// identify nav points that are positioned on navmesh polygons
		for (NavPoint navPoint : navGraph.values()) {
			PolygonId polygonId = null;
			
        	if ( !navPoint.isLiftCenter() ) { // lifts  are moving - always treat as off mesh point
        		polygonId = polygonAnalysis.getPolygonIdBelow(navPoint.getLocation());
        	}
        	
        	if ( polygonId == null ) {
        		offMeshNavPoints.add(navPoint);
        	} else { 
        		getNavPointInfo(navPoint).polygonId = polygonId;
        	}
        }
		
        // identify off-mesh edges
		for (NavPoint navPoint : navGraph.values() ) {
			
			for (NavPointNeighbourLink link : navPoint.getOutgoingEdges().values()) {
	            if ( UT2004EdgeChecker.checkLink(link) 
	            	 &&
	                 isOffMeshEdge(link, polygonAnalysis, lineSegmentAnalysis)
	            ) {
	                offMeshNavLinks.add(link);
	                offMeshNavPoints.add(link.getFromNavPoint());
	                offMeshNavPoints.add(link.getToNavPoint());
	            }
			}
		}
		
		// identify off-mesh points
		for (NavPoint navPoint : offMeshNavPoints) {
			getNavPointInfo(navPoint).isOffMeshPoint = true;
			
			PolygonId polygonId = getNavPointInfo(navPoint).polygonId;
			if (polygonId != null) {
				getOffMeshPointsByPolygonId(polygonId).add(navPoint);
			}
        }
		
		// identify off-mesh edges		
		for (NavPointNeighbourLink link : offMeshNavLinks) {
			getNavPointInfo(link.getFromNavPoint()).outgoingOffMeshEdges.add(link);
			getNavPointInfo(link.getToNavPoint()).incommingOffMeshEdges.add(link);
	    }
	}
	
	public ArrayList<NavPoint> getOffMeshPointsByPolygonId(PolygonId polygonId) {
		if (!polygonIdToOffMeshPointsMap.containsKey(polygonId)) {
			polygonIdToOffMeshPointsMap.put(polygonId, new ArrayList<NavPoint>());
		}
		
		return polygonIdToOffMeshPointsMap.get(polygonId);
	}
	
	/** Determine whether link is an off-mesh edge
	 * 
	 * @param link link to determine whether it is off-mesh (not implied by nav mesh)
	 * @return 	true if link is not implied by navmesh
	 */
	protected boolean isOffMeshEdge(NavPointNeighbourLink link, PolygonAnalysis polygonAnalysis, LineSegmentAnalysis lineSegmentAnalysis) {
		NavPoint fromNav = link.getFromNavPoint();
        NavPoint toNav = link.getToNavPoint();
        
        final PolygonId startPolygonId = getNavPointInfo(fromNav).polygonId;
        final PolygonId destinationPolygonId = getNavPointInfo(toNav).polygonId;
        
        if ( startPolygonId == null || destinationPolygonId == null ) {
        	// one of the off-mesh point is not even on navmesh polygon,
        	// so the edge cannot be implied by navmesh
        	return true;
        }

        Location direction3d = toNav.getLocation().sub(fromNav.getLocation());
        Vector2D direction = new Vector2D( direction3d.x, direction3d.y );
        
        AnalysisPathTracingContext pathTracingContext = new AnalysisPathTracingContext(polygonAnalysis, lineSegmentAnalysis);
        Predicate<RayPath<PolygonId, EdgeDescriptor>> keepTracingPredicate = new Predicate<RayPath<PolygonId, EdgeDescriptor>>() {
			@Override
			public boolean apply(RayPath<PolygonId, EdgeDescriptor> rayPath) {
				return (
					!rayPath.asPolygons().contains( destinationPolygonId )
				);
			}
		};
        RayPath<PolygonId, EdgeDescriptor> rayPath = NavMeshPathTracer.trace( 
    		startPolygonId,
    		fromNav.getLocation(),
    		direction, 
    		pathTracingContext,
    		keepTracingPredicate
        );
        return !rayPath.asPolygons().contains( destinationPolygonId );
	}
	
	public NavPointInfo getNavPointInfo(NavPoint navPoint) {
		if (!navPointToInfoMap.containsKey(navPoint)) {
			navPointToInfoMap.put( navPoint, new NavPointInfo() );
		}
		return navPointToInfoMap.get(navPoint);
	}
	
	public static class NavPointInfo {
		public PolygonId polygonId = null;
		public boolean isOffMeshPoint = false;
		public HashSet<NavPointNeighbourLink> outgoingOffMeshEdges = Sets.newHashSet();
		public HashSet<NavPointNeighbourLink> incommingOffMeshEdges = Sets.newHashSet();
	}
	
	protected class EdgeDescriptor {
		protected PolygonId polygonId;
		protected int edgeIndex;
		
		public EdgeDescriptor(PolygonId polygonId, int edgeIndex) {
			super();
			this.polygonId = polygonId;
			this.edgeIndex = edgeIndex;
		}

		public PolygonId getPolygonId() {
			return polygonId;
		}

		public int getEdgeIndex() {
			return edgeIndex;
		}
	}
	
	protected class AnalysisPathTracingContext implements IPathTraceContext<PolygonId, EdgeDescriptor> {
		protected PolygonAnalysis polygonAnalysis;
		protected LineSegmentAnalysis lineSegmentAnalysis;
		
		public AnalysisPathTracingContext(
				PolygonAnalysis polygonAnalysis,
				LineSegmentAnalysis lineSegmentAnalysis
		) {
			this.polygonAnalysis = polygonAnalysis;
			this.lineSegmentAnalysis = lineSegmentAnalysis;
		}
		
		@Override
		public List<EdgeDescriptor> getEdges(PolygonId polygonId) {
			ArrayList<EdgeDescriptor> retval = Lists.newArrayList();
			for (int i=0; i<polygonAnalysis.polygonIdToInfoMap.get(polygonId).vertexIds.size(); ++i) {
				retval.add( new EdgeDescriptor( polygonId, i) );
			}
			return retval;
		}

		@Override
		public Location getSourceVertex(EdgeDescriptor edge) {
			PolygonAnalysis.PolygonInfo polygonInfo = polygonAnalysis.polygonIdToInfoMap.get(edge.getPolygonId());
			VertexId vertexId = polygonInfo.vertexIds.get(edge.getEdgeIndex());
			return polygonAnalysis.vertexIdToInfoMap.get(vertexId).location;
		}

		@Override
		public Location getDestinationVertex(EdgeDescriptor edge) {
			PolygonAnalysis.PolygonInfo polygonInfo = polygonAnalysis.polygonIdToInfoMap.get(edge.getPolygonId());
			VertexId vertexId = polygonInfo.vertexIds.get( (edge.getEdgeIndex()+1)%polygonInfo.vertexIds.size() );
			return polygonAnalysis.vertexIdToInfoMap.get(vertexId).location;
		}

		@Override
		public PolygonId getAdjacentPolygonByEdge(PolygonId polygonId, EdgeDescriptor edge) {
			NavMeshBoundaryInfo boundaryInfo = lineSegmentAnalysis.getPolygonInfo(polygonId).edgeIndexToBoundaryInfoMap.get(edge.edgeIndex);
			
			if ( boundaryInfo == null ) {
				return null;
			}
			
			if ( ! boundaryInfo.polygonAId.equals( polygonId ) ) {
				return boundaryInfo.polygonAId;
			} else {
				return boundaryInfo.polygonBId;
			}
		}

		@Override
		public Point2D project(Location location) {
			return new Point2D(location.getX(), location.getY());
		}
		
	}
}