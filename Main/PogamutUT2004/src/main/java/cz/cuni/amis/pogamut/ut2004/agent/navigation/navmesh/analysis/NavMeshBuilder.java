package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.PolygonId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.VertexId;

/** Builder for experiments and tests with simple nav meshes
 */
public class NavMeshBuilder implements IRawNavMesh {

	protected ArrayList<Location> vertices = Lists.newArrayList();
	protected ArrayList<ArrayList<Integer>> polygons = Lists.newArrayList(); // polygons as lists of indices into vertices

	public NavMeshBuilder() {
	}
	
	public VertexId makeVertex( Location location ) {
		vertices.add( location );
		return new VertexId( vertices.size()-1 );
	}
	
	public VertexId makeVertex( double x, double y, double z ) {
		return makeVertex( new Location( x, y, z ) );
	}
	
	public PolygonId makePolygon( List<VertexId> vertexIds ) {
		ArrayList<Integer> vertexIdsAsIntengers = Lists.newArrayList();
		for ( VertexId vertex : vertexIds ) {
			vertexIdsAsIntengers.add( vertex.getValue() );
		}
		polygons.add( vertexIdsAsIntengers );
		return new PolygonId( polygons.size()-1 );
	}
	
	public PolygonId makePolygon( VertexId vertex1Id, VertexId vertex2Id, VertexId vertex3Id, VertexId... moreVertex1Ids ) {
		List<VertexId> vertices = Arrays.asList( new VertexId[]{ vertex1Id, vertex2Id, vertex3Id } );
		for ( VertexId vertex : moreVertex1Ids ) {
			vertices.add( vertex );
		}
		return makePolygon( vertices );
	}
	
	@Override
	public List<? extends Location> getVertices() {
		return Collections.unmodifiableList( vertices );
	}

	@Override
	public List<? extends List<Integer>> getPolygons() {
		return Collections.unmodifiableList( polygons );
	}
}
