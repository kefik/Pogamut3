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

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.NavMeshAnalysis;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.file.RawNavMeshFile;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.INavMeshAtom;
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

    public static String pureMeshReadDir = "navmesh"; 
    public static String processedMeshDir = "navmesh";
    
	private static final long serialVersionUID = 1L;

	protected transient Logger log;
    
	protected NavMeshNavGraphGlue navGraphGlue = null;
    protected Set<NavMeshVertex> vertices = null;
    protected Set<NavMeshPolygon> polygons = null;
    protected Set<OffMeshPoint> offMeshPoints = null;
    protected Set<INavMeshAtom> atoms = null;
    protected transient HashMap<NavPoint,OffMeshPoint> navPointToOffMeshPointMap = null; // constructed from offMeshPoints on demand, use getter
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
    public Set<NavMeshPolygon> getPolygons() {
        return Collections.unmodifiableSet( polygons );
    }

    /** Get all vertices of navmesh polygons
     */
    public Set<NavMeshVertex> getVertices() {
        return Collections.unmodifiableSet( vertices );
    }
    
    /** Get all {@link OffMeshPoint}s
     */
    public Set<OffMeshPoint> getOffMeshPoints() {
        return Collections.unmodifiableSet( offMeshPoints );
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
	
	protected HashMap<NavPoint,OffMeshPoint> getNavPointToOffMeshPointMap() {
    	if ( navPointToOffMeshPointMap == null ) {
    		navPointToOffMeshPointMap = Maps.newHashMap();
	    	for ( OffMeshPoint offMeshPoint : offMeshPoints ) {
	    		navPointToOffMeshPointMap.put( offMeshPoint.getNavPoint(), offMeshPoint );
	    	}
    	}
		return navPointToOffMeshPointMap;
	}
    
    protected void setNavGraph( Function<UnrealId, NavPoint> navGraphView ) {
    	navGraphGlue.setNavGraph(navGraphView);
    }
    
    /** Load NavMesh
     * 
     * @param world map world view
     * @param mapName 
     * @throws IOException if file cannot be read 
     */
    protected void load( Map<UnrealId, NavPoint> navGraph, String mapName ) throws IOException {
    	String rawNavMeshFileName = pureMeshReadDir + "/" + mapName + ".navmesh";
    	RawNavMeshFile rawNavMeshFile = new RawNavMeshFile( new File(rawNavMeshFileName) );
    	NavMeshAnalysis navMeshAnalysis = new NavMeshAnalysis( rawNavMeshFile, navGraph, log );
    	navGraphGlue = navMeshAnalysis.getNavGraphGlue();
    	vertices = navMeshAnalysis.getVertices();
    	polygons = navMeshAnalysis.getPolygons();
    	offMeshPoints = navMeshAnalysis.getOffMeshPoints();
    	atoms = new HashSet<INavMeshAtom>();
    	atoms.addAll( polygons );
    	atoms.addAll( offMeshPoints );
    	xyProjectionBsp = navMeshAnalysis.getXyProjectionBsp();
    }
    
    protected void copyFrom( NavMesh navMesh ) {
    	navGraphGlue = navMesh.navGraphGlue;
        vertices = new HashSet<NavMeshVertex>( navMesh.vertices );
        polygons = new HashSet<NavMeshPolygon>( navMesh.polygons );
        offMeshPoints = new HashSet<OffMeshPoint>( navMesh.offMeshPoints );
        atoms = new HashSet<INavMeshAtom>( navMesh.atoms );
        if ( navMesh.navPointToOffMeshPointMap != null ) {
        	navPointToOffMeshPointMap = new HashMap<NavPoint, OffMeshPoint>( navMesh.navPointToOffMeshPointMap );
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
