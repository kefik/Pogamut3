package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.construction.IDeferredConstructor;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.construction.NodeConstructionCoordinator;

/** Vertex of a nav mesh polygon
 * 
 * Immutable.
 */
public class NavMeshVertex implements Serializable, ILocated {
	
	private static final long serialVersionUID = 1L;
	
	protected int id;
	protected Location location;
	
	protected HashMap<NavMeshPolygon,Integer> polygonToVertexIndexMap = Maps.newHashMap();
	protected transient Map<NavMeshPolygon,Integer> constPolygonToVertexIndexMap;
	
	protected ArrayList<NavMeshEdge> edges = Lists.newArrayList();
	protected transient List<NavMeshEdge> constEdges;
	
	protected boolean isOnWalkableAreaEdge;
	
	public NavMeshVertex(
			final int id,
			Location location,
			final Map<Integer,Integer> polygonIdToVertexIndexMap,
			final List<Integer> edgeIds,
			boolean isOnWalkableAreaEdge,
			final NodeConstructionCoordinator constructionCoordinator
	) {
		this.id = id;
		this.location = location;
		this.isOnWalkableAreaEdge = isOnWalkableAreaEdge;
		initializeConstCollections();
		
		constructionCoordinator.addDeferredConstructor(
			new IDeferredConstructor() {
				@Override
				public void construct() {
					for ( Entry<Integer, Integer> entry : polygonIdToVertexIndexMap.entrySet() ) {
						polygonToVertexIndexMap.put(
							constructionCoordinator.getPolygonById( entry.getKey() ),
							entry.getValue()
						);
					}
					
					for ( Integer edgeId : edgeIds ) {
						edges.add( constructionCoordinator.getEdgeById(edgeId) );
					}
				}
			}
		);
	}
	
	/** Get ID
	 */
	public int getId() {
		return id;
	}
	
	@Override
	public Location getLocation() {
		return location;
	}
	
	/** Get a map of containing polygon to index of this vertex in the polygon
	 * 
	 * @return unmodifiable map
	 */
	public Map<NavMeshPolygon,Integer> getPolygonToVertexIndexMap() {
		return constPolygonToVertexIndexMap;
	}
	
	/** Get a list of edges containing this vertex
	 * 
	 * @return unmodifiable list
	 */
	public List<NavMeshEdge> getEdges() {
		return constEdges;
	}
	
	public boolean isOnWalkableAreaEdge() {
		return isOnWalkableAreaEdge;
	}
	
	@Override
	public String toString() {
		return "NMV( "+id+", "+location.toString()+" )";
	}
	
    protected void initializeConstCollections() {
    	constPolygonToVertexIndexMap = Collections.unmodifiableMap(polygonToVertexIndexMap);
    	constEdges = Collections.unmodifiableList( edges );
	}
    
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();

		initializeConstCollections();
	}
}
