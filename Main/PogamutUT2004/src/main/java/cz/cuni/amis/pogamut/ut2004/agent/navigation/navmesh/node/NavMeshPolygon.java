/*
 * Copyright (C) 2013 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.NavMeshBoundaryInfo;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.construction.IDeferredConstructor;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.construction.NodeConstructionCoordinator;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.EdgeId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.PolygonId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.VertexId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import math.geom3d.Point3D;
import math.geom3d.polygon.SimplePlanarPolygon3D;

/** Navigation mesh polygon
 * 
 * Convex polygon representing a walkable surface.
 * 
 * Immutable.
 */
public class NavMeshPolygon implements Serializable, INavMeshAtom, ILocated {
	
	private static final long serialVersionUID = 1L;

	protected PolygonId id;
    
    protected ArrayList<NavMeshVertex> vertices = Lists.newArrayList();
    protected transient List<NavMeshVertex> constVertices;
    
    protected ArrayList<NavMeshEdge> edges = Lists.newArrayList();
    protected transient List<NavMeshEdge> constEdges;
    
    protected HashMap<Integer, NavMeshBoundary> edgeIndexToBoundaryMap = Maps.newHashMap();
    protected transient Map<Integer, NavMeshBoundary> constEdgeIndexToBoundaryMap;
    
    protected HashMap<NavMeshPolygon,NavMeshBoundary> adjPolygonToBoundaryMap = Maps.newHashMap();
    protected transient Map<NavMeshPolygon,NavMeshBoundary> constAdjPolygonToBoundaryMap;
    
    protected ArrayList<OffMeshPoint> offMeshPoints = Lists.newArrayList();
    protected transient List<OffMeshPoint> constOffMeshPoints;
    
    protected ArrayList<INavMeshAtom> neighbors = Lists.newArrayList();
    protected transient List<INavMeshAtom> constNeighbors;
    
    protected transient SimplePlanarPolygon3D shape = null; // computed on demand and cached, use getter
    
    /** Constructor
     * 
     * Uses deferred constructor to break reference cycles.
     */
    public NavMeshPolygon( 
    		final PolygonId id, 
    		final List<VertexId> vertexIds,
    		final List<EdgeId> edgeIds,
    		final Map<Integer, NavMeshBoundaryInfo> edgeIndexToBoundaryInfoMap,
    		final Map<PolygonId, NavMeshBoundaryInfo> adjPolygonIdToBoundaryInfoMap,
    		final List<NavPoint> offMeshNavPoints,
    		final NodeConstructionCoordinator constructionCoordinator 
    ) {
        this.id = id;
        initializeConstCollections();
		
        constructionCoordinator.addDeferredConstructor(
        	new IDeferredConstructor() {
				@Override
				public void construct() {				
					for ( VertexId vertexId : vertexIds ) {
						vertices.add( constructionCoordinator.getVertexById(vertexId) );
					}
					
					for (EdgeId edgeId : edgeIds  ) {
						edges.add( constructionCoordinator.getEdgeById( edgeId ) );
					}
					
					for ( Entry<Integer, NavMeshBoundaryInfo> entry : edgeIndexToBoundaryInfoMap.entrySet() )
					{
						edgeIndexToBoundaryMap.put(
								entry.getKey(),
								constructionCoordinator.getBoundaryByBoundaryInfo(entry.getValue()) 
						);
					}
					
					for ( Entry<PolygonId, NavMeshBoundaryInfo> entry : adjPolygonIdToBoundaryInfoMap.entrySet() ) {
						adjPolygonToBoundaryMap.put( 
							constructionCoordinator.getPolygonById( entry.getKey() ), 
							constructionCoordinator.getBoundaryByBoundaryInfo(entry.getValue())
						);
					}
										
					for ( NavPoint offMeshNavPoint : offMeshNavPoints ) {
						offMeshPoints.add( constructionCoordinator.getOffMeshPointByNavPoint(offMeshNavPoint) );
					}
					
					neighbors.addAll( adjPolygonToBoundaryMap.keySet() );
					neighbors.addAll( offMeshPoints );
				}
			}
		);
    }
    
    public PolygonId getId() {
        return id;
    }
    
    public Map<NavMeshPolygon,NavMeshBoundary> getAdjPolygonToBoundaryMap() {
    	return constAdjPolygonToBoundaryMap;
    }
    
    public NavMeshBoundary getBoundaryByAdjPolygon( NavMeshPolygon adjPolygon ) {
    	return constAdjPolygonToBoundaryMap.get(adjPolygon);
    }
    
    public List<OffMeshPoint> getOffMeshPoints() {
    	return constOffMeshPoints;
    }
    
    @Override
    public List<INavMeshAtom> getNeighbors() {
    	return constNeighbors;
    }
    
    /** Get vertices
     * 
     * @return Unmodifiable list of vertices.
     */
    public List<NavMeshVertex> getVertices() {
    	return constVertices; 
    }
    
    /** Get edges
     * 
     * @return
     */
	public List<NavMeshEdge> getEdges() {
		return constEdges;
	}
    
    /** Get boundaries by edge index map.
     * <p>
     * Edges are defined by the sequence of vertices - edge with index x is ( vertices[x], vertices[x+1] ).
     * 
     * @return immutable map
     */
    public Map<Integer, NavMeshBoundary> getEdgeIndexToBoundaryMap() {
    	return constEdgeIndexToBoundaryMap;
    }
    
    /** Get location
     * 
     * @return polygon's location (center of mass)
     */
    @Override
    public Location getLocation() {
    	return getCenter();
    }
    
    /** Get center
     * 
     * @return center of mass
     */
    public Location getCenter() {
		return new Location(getShape().getCentroid());
	}
    
    public double getDistance(Location location) {
    	return getShape().getDistance(location.asPoint3D());
    }
    
    public SimplePlanarPolygon3D getShape() {
    	if (shape == null) {
    		List<Point3D> point3dVertices = new ArrayList<Point3D>();
    		for (NavMeshVertex vertex : vertices) {
    			point3dVertices.add(vertex.getLocation().asPoint3D());
    		}
    		shape = new SimplePlanarPolygon3D(point3dVertices);
    	}
    	return shape;
    }
    
    @Override
    public int hashCode() {
    	return 23*id.getValue(); 
    }
    
	@Override
	public String toString() {
		StringBuilder retval = new StringBuilder();
		retval.append(  "NMP( "+id );
		for ( NavMeshVertex vertex : vertices ) {
			retval.append( ", "+vertex.toString() );
		}
		retval.append( " )" );
		return retval.toString();
	}
	
	protected void initializeConstCollections() {
        constVertices = Collections.unmodifiableList(vertices);
        constEdges = Collections.unmodifiableList(edges);
        constEdgeIndexToBoundaryMap = Collections.unmodifiableMap(edgeIndexToBoundaryMap);
        constAdjPolygonToBoundaryMap = Collections.unmodifiableMap(adjPolygonToBoundaryMap);
        constOffMeshPoints = Collections.unmodifiableList(offMeshPoints);
        constNeighbors = Collections.unmodifiableList(neighbors);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();

		initializeConstCollections();
	}
}
