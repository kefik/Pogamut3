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

import java.io.Serializable;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.construction.IDeferredConstructor;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.construction.NodeConstructionCoordinator;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;

/** Edge between two off-mesh points
 * 
 * Not to be confused with {@link NavMeshEdge}.
 * Immutable.
 */
public class OffMeshEdge implements Serializable  {

	private static final long serialVersionUID = 1L;
		
	protected UnrealId navPointToId;
	protected OffMeshPoint from;
	protected OffMeshPoint to;
    
    public OffMeshEdge( final NavPointNeighbourLink link, final NodeConstructionCoordinator constructionCoordinator) {
    	this.navPointToId = link.getId();
    	
		constructionCoordinator.addDeferredConstructor( new IDeferredConstructor() {
				@Override
				public void construct() {
					from = constructionCoordinator.getOffMeshPointByNavPoint(link.getFromNavPoint());
					to = constructionCoordinator.getOffMeshPointByNavPoint(link.getToNavPoint());
				}
			}
		);
	}

    /** Get edge source
     */
    public OffMeshPoint getFrom() {
        return from;
    }
    
    /** Get edge destination
     */
    public OffMeshPoint getTo() {
        return to;
    }    

    /** Get underlying navigation graph link
     */
     public NavPointNeighbourLink getLink() {
    	 return from.getNavPoint().getOutgoingEdges().get(navPointToId);
    }  
}
