package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.inject.internal.Maps;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.PolygonId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.VertexId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;

/** Builder for experiments and tests with simple nav meshes
 */
public class NavMeshBuilder implements IRawNavMesh {

	protected ArrayList<Location> vertices = Lists.newArrayList();
	protected ArrayList<ArrayList<Integer>> polygons = Lists.newArrayList(); // polygons as lists of indices into vertices
	protected HashMap<UnrealId, NavPoint> navGraph = Maps.newHashMap();
	
	public NavMeshBuilder() {
	}
	
	public VertexId addVertex( Location location ) {
		vertices.add( location );
		return new VertexId( vertices.size()-1 );
	}
	
	public VertexId addVertex( double x, double y, double z ) {
		return addVertex( new Location( x, y, z ) );
	}
	
	public PolygonId addPolygon( List<VertexId> vertexIds ) {
		ArrayList<Integer> vertexIdsAsIntengers = Lists.newArrayList();
		for ( VertexId vertex : vertexIds ) {
			vertexIdsAsIntengers.add( vertex.getValue() );
		}
		polygons.add( vertexIdsAsIntengers );
		return new PolygonId( polygons.size()-1 );
	}
	
	public PolygonId addPolygon( VertexId vertex1Id, VertexId vertex2Id, VertexId vertex3Id, VertexId... moreVertex1Ids ) {
		List<VertexId> vertices = new ArrayList<VertexId>( Arrays.asList( new VertexId[]{ vertex1Id, vertex2Id, vertex3Id } ) );
		for ( VertexId vertex : moreVertex1Ids ) {
			vertices.add( vertex );
		}
		return addPolygon( vertices );
	}
	
	public NavPoint addPlayerStart( Location location ) {
		NavPoint startNavPoint = new NavPointMessage(
			UnrealId.get("PlayerStart_"+navGraph.values().size()),
			location,
			new Velocity(0,0,0),
			true,
			null,
			null,
			false,
			false,
			null,
			null,
			false,
			false,
			false,
			true,
			0,
			false,
			0,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			new Rotation(0,0,0),
			false,
			false,
			null,
			new HashMap<UnrealId,NavPointNeighbourLink>(),
			new HashMap<UnrealId,NavPointNeighbourLink>(),
			null
		);
	
		navGraph.put( startNavPoint.getId(), startNavPoint );
		return startNavPoint;
	}
	
	public Map<UnrealId, NavPoint> getNavGraph() {
		return Collections.unmodifiableMap( navGraph );
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
