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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.construction.IDeferredConstructor;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.construction.NodeConstructionCoordinator;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.PolygonId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.internal.NavMeshNavGraphGlue;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;

/** Off-mesh navigation point
 * 
 * A source or a destination of a navigation edge between non-adjacent navigation mesh polygons. 
 */
public class OffMeshPoint implements ILocated, INavMeshAtom, Serializable {
    
	private static final long serialVersionUID = 1L;

	protected NavMeshNavGraphGlue navGraphGlue;
	
	protected UnrealId navPointId; // custom serialization
    protected NavMeshPolygon polygon;
    
    protected ArrayList<OffMeshEdge> outgoingEdges = new ArrayList<OffMeshEdge>();
    protected transient List<OffMeshEdge> constOutgoingEdges;
    protected ArrayList<OffMeshEdge> incomingEdges = new ArrayList<OffMeshEdge>();
    protected transient List<OffMeshEdge> constIncomingEdges;  
    protected ArrayList<INavMeshAtom> neighbors = new ArrayList<INavMeshAtom>();
    protected transient List<INavMeshAtom> constNeighbors;
    
    public OffMeshPoint(
    		NavMeshNavGraphGlue navGraphGlue,
    		final NavPoint navPoint,
    		final PolygonId polygonId,
    		final Collection<NavPointNeighbourLink> outgoingNavLinks,
    		final Collection<NavPointNeighbourLink> incomingNavLinks,
    		final NodeConstructionCoordinator constructionCoordinator
    ) {
    	this.navGraphGlue = navGraphGlue;
        this.navPointId = navPoint.getId();
        initializeConstCollections();
        
        constructionCoordinator.addDeferredConstructor( new IDeferredConstructor() {
				@Override
				public void construct() {
					if ( polygonId != null ) {
						polygon = constructionCoordinator.getPolygonById( polygonId );
						neighbors.add(polygon);
					} else {
						polygon = null;
					}
															
					for ( NavPointNeighbourLink link : outgoingNavLinks ) {
						outgoingEdges.add( constructionCoordinator.getOffMeshEdgeByNavLink(link) );
						neighbors.add( constructionCoordinator.getOffMeshPointByNavPoint( link.getToNavPoint() ) );
					}
					
					for ( NavPointNeighbourLink link : incomingNavLinks ) {
						incomingEdges.add( constructionCoordinator.getOffMeshEdgeByNavLink(link) );
					}
				}
			}
        );
    }
    
    /** Get underlying navigation graph point
     */
    public NavPoint getNavPoint() {
    	return navGraphGlue.getNavPoint( navPointId );
    }
    
    /** Get polygon containing this off-mesh point
     */
    public NavMeshPolygon getPolygon() {
        return polygon;
    } 

    /** Get outgoing edges
     */
    public List<OffMeshEdge> getOutgoingEdges() {
        return constOutgoingEdges;
    } 

    /** Get incoming edges
     */
    public List<OffMeshEdge> getIncomingEdges() {
        return constIncomingEdges;
    }

    @Override
    public Location getLocation() {
        return getNavPoint().getLocation();
    }

    @Override
    public List<INavMeshAtom> getNeighbors() {
    	return constNeighbors;
    }
    
    @Override
    public String toString() {
    	return "OFP( "+getNavPoint().toString()+" )";
    }
    
    protected void initializeConstCollections() {
        constOutgoingEdges = Collections.unmodifiableList(outgoingEdges);
        constIncomingEdges = Collections.unmodifiableList(incomingEdges);  
        constNeighbors = Collections.unmodifiableList(neighbors);
	}
    
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();

		initializeConstCollections();
	}
}
