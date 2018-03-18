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
package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.IRawNavMesh;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.NavMeshAnalysis;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.INavMeshAtom;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.PolygonId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.VertexId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshPolygon;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshVertex;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.OffMeshPoint;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.internal.NavMeshNavGraphGlue;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import math.bsp.IConstBspTree;
import math.geom2d.line.StraightLine2D;

/**
 * Class storing NavMesh data structures.
 *
 * Controlled from {@link NavMeshModule}.
 *
 * @author Jakub Tomek
 * @author Jakub Gemrot aka Jimmy
 * @author David Holan
 */
public class NavMesh implements Serializable {
	
	private static final long serialVersionUID = 1L;

	protected transient Logger log;
    
	protected NavMeshNavGraphGlue navGraphGlue = null;
    protected Map<VertexId, NavMeshVertex> vertices = null;
    protected Map<PolygonId, NavMeshPolygon> polygons = null;
    protected Set<OffMeshPoint> offMeshPoints = null;
    protected Set<INavMeshAtom> atoms = null;
    protected transient Map<NavPoint,OffMeshPoint> navPointToOffMeshPointMap = null; // constructed from offMeshPoints on demand, use getter
    protected IConstBspTree<ArrayList<NavMeshPolygon>, StraightLine2D> xyProjectionBsp = null;
    
    public NavMesh(Logger log) {
        this.log = log;
        if (this.log == null) {
            this.log = new LogCategory("NavMesh");
        }
    }
        
    public void setLog(Logger log) {
    	this.log = log;
    }
    
	public boolean isLoaded() {
		return polygons != null;
	}
    
    /** Get all navmesh polygons
     */
    public Collection<NavMeshPolygon> getPolygons() {
        return polygons.values();
    }
    
	/** Get map from polygon ID to polygon
     */
    public Map<PolygonId, NavMeshPolygon> getIdToPolygonMap() {
        return polygons;
    }
    
    /** Get polygon by ID
     */
    public NavMeshPolygon getPolygonById(PolygonId id) {
    	return polygons.get(id);
    }

    /** Get all vertices of navmesh polygons
     */
    public Collection<NavMeshVertex> getVertices() {
        return vertices.values();
    }
    
    /** Get map from vertex ID to vertex
     */
    public Map<VertexId, NavMeshVertex> getIdToVertexMap() {
        return vertices;
    }
    
    /** Get vertex by ID
     */
    public NavMeshVertex getVertexById( VertexId id ) {
    	return vertices.get(id);
    }
    
    /** Get all {@link OffMeshPoint}s
     */
    public Collection<OffMeshPoint> getOffMeshPoints() {
        return offMeshPoints;
    }
    
    /** Get a collection of all navmesh polygons and off-mesh points.
     */
    public Set<INavMeshAtom> getAtoms() {
    	return Collections.unmodifiableSet( atoms );
    }
    
    public OffMeshPoint getOffMeshPointsByNavPoint(NavPoint navPoint) {
    	return getNavPointToOffMeshPointMap().get(navPoint.getId());
	}
    
	public IConstBspTree<ArrayList<NavMeshPolygon>, StraightLine2D> getXyProjectionBsp() {
		return xyProjectionBsp;
	}
	
	protected Map<NavPoint,OffMeshPoint> getNavPointToOffMeshPointMap() {
    	if ( navPointToOffMeshPointMap == null ) {
    		navPointToOffMeshPointMap = Maps.newHashMap();
	    	for ( OffMeshPoint offMeshPoint : offMeshPoints ) {
	    		navPointToOffMeshPointMap.put( offMeshPoint.getNavPoint(), offMeshPoint );
	    	}
	    	navPointToOffMeshPointMap = Collections.unmodifiableMap( navPointToOffMeshPointMap );
    	}
		return navPointToOffMeshPointMap;
	}
    
    protected void setNavGraph( Function<UnrealId, NavPoint> navGraphView ) {
    	navGraphGlue.setNavGraph(navGraphView);
    }
    
    /** Load NavMesh from raw data
     * 
     * @param navGraph navigation graph
     * @param rawNavMesh raw navmesh data
     */
    public void load( Map<UnrealId, NavPoint> navGraph, IRawNavMesh rawNavMesh ) throws IOException {
    	NavMeshAnalysis navMeshAnalysis = new NavMeshAnalysis( rawNavMesh, navGraph, log );
    	navGraphGlue = navMeshAnalysis.getNavGraphGlue();
    	
    	vertices = Maps.newHashMap();
    	for ( NavMeshVertex vertex :  navMeshAnalysis.getVertices() ) {
    		vertices.put( vertex.getId(), vertex );
    	}
    	vertices = Collections.unmodifiableMap( vertices );
    	
    	polygons = Maps.newHashMap();
    	for ( NavMeshPolygon polygon : navMeshAnalysis.getPolygons() ) {
    		polygons.put( polygon.getId(),  polygon );
    	}
    	polygons = Collections.unmodifiableMap( polygons );
    	
    	offMeshPoints = Collections.unmodifiableSet( navMeshAnalysis.getOffMeshPoints() );
    	
    	atoms = new HashSet<INavMeshAtom>();
    	atoms.addAll( polygons.values() );
    	atoms.addAll( offMeshPoints );
    	atoms = Collections.unmodifiableSet( atoms );
    	
    	xyProjectionBsp = navMeshAnalysis.getXyProjectionBsp();
    }
    
    protected void copyFrom( NavMesh navMesh ) {
    	navGraphGlue = navMesh.navGraphGlue;
        vertices = navMesh.vertices;
        polygons = navMesh.polygons;
        offMeshPoints = navMesh.offMeshPoints;
        atoms = navMesh.atoms;
        if ( navMesh.navPointToOffMeshPointMap != null ) {
        	navPointToOffMeshPointMap = navMesh.navPointToOffMeshPointMap;
        } else {
        	navPointToOffMeshPointMap = null;
        }
        xyProjectionBsp = navMesh.xyProjectionBsp;
    }
    
    /** Reseting {@link NavMesh} instance, completely "unloading" the NavMesh.
     * 
     * Called from {@link NavMeshModule}.
     */
    protected void clear() {
        log.warning("NavMesh has been cleared...");
        
        navGraphGlue = null;
        vertices = null;
        polygons = null;
        offMeshPoints = null;
        atoms = null;
        navPointToOffMeshPointMap = null;
        xyProjectionBsp = null;
    }
}
