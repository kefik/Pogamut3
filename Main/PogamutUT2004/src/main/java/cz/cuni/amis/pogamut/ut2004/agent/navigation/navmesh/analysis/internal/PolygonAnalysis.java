package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.IRawNavMesh;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.bsp.XyProjectionTPolygonPartitioningStrategy;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.grounder.NavMeshDropGrounder;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.EdgeId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.PolygonId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.VertexId;
import math.bsp.BspTree;
import math.geom2d.line.StraightLine2D;
import math.geom3d.Point3D;
import math.geom3d.polygon.SimplePlanarPolygon3D;

public class PolygonAnalysis {
	public ArrayList<VertexId> allVertexIds = Lists.newArrayList();
	public ArrayList<PolygonId> allPolygonIds = Lists.newArrayList();
	public BspTree<ArrayList<PolygonId>, StraightLine2D> xyProjectionBsp;
	public HashMap<PolygonId, PolygonInfo> polygonIdToInfoMap = Maps.newHashMap();
	public HashMap<VertexId,VertexInfo> vertexIdToInfoMap = Maps.newHashMap();
	
	public PolygonAnalysis(IRawNavMesh rawNavMesh ) {
		
		for (int i = 0; i<rawNavMesh.getVertices().size(); ++i ) {
			VertexId id = new VertexId(i);
			allVertexIds.add( id );
			vertexIdToInfoMap.put( id, new VertexInfo( rawNavMesh.getVertices().get(i) ) );
		}
		
		for (int i = 0; i<rawNavMesh.getPolygons().size(); ++i ) {
			allPolygonIds.add( new PolygonId(i) );
		}

		int nextEdgeIdValue = 1;
		for ( int polygonIdValue = 0; polygonIdValue<rawNavMesh.getPolygons().size(); ++polygonIdValue ) {
			PolygonId polygonId = new PolygonId(polygonIdValue);
			List<Integer> rawPolygon = rawNavMesh.getPolygons().get(polygonIdValue);
			PolygonInfo polygonInfo = new PolygonInfo();
			polygonIdToInfoMap.put( polygonId, polygonInfo );
			
			for ( int vertexIndex = 0; vertexIndex<rawPolygon.size(); ++vertexIndex ) {
				VertexId vertexId = new VertexId( rawPolygon.get(vertexIndex) );
				VertexInfo vertexInfo = vertexIdToInfoMap.get(vertexId);
				
				// a polygon should contain a vertex only once
				assert(!vertexInfo.containingPolygonIdToVertexIndexMap.containsKey(polygonId));
				
				vertexInfo.containingPolygonIdToVertexIndexMap.put(polygonId, vertexIndex);
				
				polygonInfo.vertexIds.add(vertexId);
				polygonInfo.edgeIds.add( new EdgeId(nextEdgeIdValue++) );
			}
		}
		
		// create shapes
		
		for ( PolygonId polygonId : allPolygonIds ) {
			PolygonInfo polygonInfo = polygonIdToInfoMap.get(polygonId);
			ArrayList<Point3D> verticesAsPoint3D = Lists.newArrayList();  
			for (VertexId vertexId : polygonInfo.vertexIds ) {
				verticesAsPoint3D.add( vertexIdToInfoMap.get(vertexId).location.asPoint3D() );
			}
			polygonInfo.shape = new SimplePlanarPolygon3D(verticesAsPoint3D);
		}
		
		XyProjectionTPolygonPartitioningStrategy<PolygonId> partitioningStrategy = new XyProjectionTPolygonPartitioningStrategy<PolygonId>() {
			@Override
			protected List<Location> getPolygonVertexLocations(PolygonId polygonId) {
				ArrayList<Location> locations = Lists.newArrayList();
				for ( VertexId vertexId : polygonIdToInfoMap.get(polygonId).vertexIds ) {
					locations.add( vertexIdToInfoMap.get(vertexId).location );
				}
				return locations;
			}			
		};
		xyProjectionBsp = BspTree.make( partitioningStrategy, allPolygonIds );
	}
	
	public PolygonId getPolygonIdBelow(Location location ) {
		return NavMeshDropGrounder.getPolygonBelow(location, xyProjectionBsp, polygonIdToShapeFunction );
	}
	
	protected Function<PolygonId, SimplePlanarPolygon3D> polygonIdToShapeFunction = new Function<PolygonId, SimplePlanarPolygon3D>() {
		public SimplePlanarPolygon3D apply(PolygonId polygonId) {
			return polygonIdToInfoMap.get(polygonId).shape;
		};
	};
	
	public class VertexInfo {
		public HashMap<PolygonId,Integer> containingPolygonIdToVertexIndexMap = Maps.newHashMap();
		public Location location;
		
		public VertexInfo(Location location) {
			this.location = location;
		}
	}
	
	public class PolygonInfo {
		public SimplePlanarPolygon3D shape = null;
		public ArrayList<VertexId> vertexIds = Lists.newArrayList();
		public ArrayList<EdgeId> edgeIds = Lists.newArrayList();
		
		public PolygonInfo() {
		}
	}
}