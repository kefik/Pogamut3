package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.bsp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshPolygon;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshVertex;

public class XyProjectionPolygonPartitioningStrategy extends XyProjectionTPolygonPartitioningStrategy<NavMeshPolygon> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	protected List<Location> getPolygonVertexLocations(NavMeshPolygon polygon) {
		ArrayList<Location> locations = Lists.newArrayList();
		
		for ( NavMeshVertex vertex : polygon.getVertices() ) {
			locations.add( vertex.getLocation() );
		}
		
		return locations;
	}
}
