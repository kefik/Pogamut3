package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis;

import java.util.List;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;

public interface IRawNavMesh {

	List<? extends Location> getVertices();
	
	List<? extends List<Integer>> getPolygons();
}
