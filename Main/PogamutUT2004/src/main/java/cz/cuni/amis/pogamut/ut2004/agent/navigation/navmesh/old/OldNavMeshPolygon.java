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
package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.old;
//old navmesh

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of INavMeshAtom for polygons
 * @author Jakub
 */
@Deprecated
public class OldNavMeshPolygon implements OldINavMeshAtom {
   private int pId;
   
   public OldNavMeshPolygon(int pId) {
	   assert(pId >= 0);
       this.pId = pId;
   }
   
   public int getPolygonId() {
       return pId;
   }

    @Override
    public List<OldINavMeshAtom> getNeighbours(OldNavMesh mesh) {
       List<OldINavMeshAtom> neighbours = new ArrayList<OldINavMeshAtom>(); 
       
       // add all nearby polygons
       List<Integer> pn = mesh.getNeighbourIdsToPolygon(pId);
       for(Integer i : pn) {
           neighbours.add(new OldNavMeshPolygon(i));
       }
       
       // add all offmesh points on this polgon
       List<OldOffMeshPoint> ops = mesh.getOffMeshPointsOnPolygon(pId);
       for(OldOffMeshPoint op : ops) {
           neighbours.add(op);
       }
       
       return neighbours; 
    }

    /**
     * Compares ids of polygons and returns true if they are the same
     * returns false if p is point
     * @param p
     * @return 
     */
	@Override
	public boolean equals(OldINavMeshAtom atom) {
		return equals((Object) atom);
	}
	
    @Override
    public boolean equals(Object other) {
        if (other instanceof OldNavMeshPolygon) {
            OldNavMeshPolygon p = (OldNavMeshPolygon) other;
            return (p.getPolygonId()==pId);
        } else {
        	return false;
        }
    }
    
    @Override
    public int hashCode() {
    	return pId; 
    }


}
