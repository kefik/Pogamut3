package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.bsp.XyProjectionTPolygonPartitioningStrategy;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.file.RawNavMeshFile;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.grounder.NavMeshDropGrounder;
import math.bsp.BspTree;
import math.geom2d.line.StraightLine2D;
import math.geom3d.Point3D;
import math.geom3d.polygon.SimplePlanarPolygon3D;

public class PolygonAnalysis {
	public ArrayList<Integer> allVertexIds = Lists.newArrayList();
	public ArrayList<Integer> allPolygonIds = Lists.newArrayList();
	public BspTree<ArrayList<Integer>, StraightLine2D> xyProjectionBsp;
	public HashMap<Integer, PolygonInfo> polygonIdToInfoMap = Maps.newHashMap();
	public HashMap<Integer,VertexInfo> vertexIdToInfoMap = Maps.newHashMap();
	
	public PolygonAnalysis(RawNavMeshFile rawNavMesh ) {		
		for (int i = 0; i<rawNavMesh.vertices.size(); ++i ) {
			allVertexIds.add(i);
			vertexIdToInfoMap.put(i, new VertexInfo( rawNavMesh.vertices.get(i) ) );
		}
		
		for (int i = 0; i<rawNavMesh.polygons.size(); ++i ) {
			allPolygonIds.add(i);
		}

		int nextEdgeId = 1;
		for ( int polygonId = 0; polygonId<rawNavMesh.polygons.size(); ++polygonId ) {
			ArrayList<Integer> rawPolygon = rawNavMesh.polygons.get(polygonId);
			PolygonInfo polygonInfo = new PolygonInfo();
			polygonIdToInfoMap.put( polygonId, polygonInfo );
			
			for ( int vertexIndex = 0; vertexIndex<rawPolygon.size(); ++vertexIndex ) {
				int vertexId = rawPolygon.get(vertexIndex);
				VertexInfo vertexInfo = vertexIdToInfoMap.get(vertexId);
				
				// a polygon should contain a vertex only once
				assert(!vertexInfo.containingPolygonIdToVertexIndexMap.containsKey(polygonId));
				
				vertexInfo.containingPolygonIdToVertexIndexMap.put(polygonId, vertexIndex);
				
				polygonInfo.vertexIds.add(vertexId);
				polygonInfo.edgeIds.add( nextEdgeId++ );
			}
		}
		
		// create shapes
		
		for ( Integer polygonId : allPolygonIds ) {
			PolygonInfo polygonInfo = polygonIdToInfoMap.get(polygonId);
			ArrayList<Point3D> verticesAsPoint3D = Lists.newArrayList();  
			for (Integer vertexId : polygonInfo.vertexIds ) {
				verticesAsPoint3D.add( vertexIdToInfoMap.get(vertexId).location.asPoint3D() );
			}
			polygonInfo.shape = new SimplePlanarPolygon3D(verticesAsPoint3D);
		}
		
		XyProjectionTPolygonPartitioningStrategy<Integer> partitioningStrategy = new XyProjectionTPolygonPartitioningStrategy<Integer>() {
			@Override
			protected List<Location> getPolygonVertexLocations(Integer polygonId) {
				ArrayList<Location> locations = Lists.newArrayList();
				for ( Integer vertexId : polygonIdToInfoMap.get(polygonId).vertexIds ) {
					locations.add( vertexIdToInfoMap.get(vertexId).location );
				}
				return locations;
			}			
		};
		xyProjectionBsp = BspTree.make( partitioningStrategy, allPolygonIds );
	}
	
	public Integer getPolygonIdBelow(Location location ) {
		return NavMeshDropGrounder.getPolygonBelow(location, xyProjectionBsp, polygonIdToShapeFunction );
	}
	
	protected Function<Integer, SimplePlanarPolygon3D> polygonIdToShapeFunction = new Function<Integer, SimplePlanarPolygon3D>() {
		public SimplePlanarPolygon3D apply(Integer polygonId) {
			return polygonIdToInfoMap.get(polygonId).shape;
		};
	};
	
	public class VertexInfo {
		public HashMap<Integer,Integer> containingPolygonIdToVertexIndexMap = Maps.newHashMap();
		public Location location;
		
		public VertexInfo(Location location) {
			this.location = location;
		}
	}
	
	public class PolygonInfo {
		public SimplePlanarPolygon3D shape = null;
		public ArrayList<Integer> vertexIds = Lists.newArrayList();
		public ArrayList<Integer> edgeIds = Lists.newArrayList();
		
		public PolygonInfo() {
		}
	}
}