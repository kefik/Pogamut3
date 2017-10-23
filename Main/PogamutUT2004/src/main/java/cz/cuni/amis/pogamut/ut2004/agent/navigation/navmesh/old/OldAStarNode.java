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
 * class used in algorithm A* in pathfinding in NavMesh
 * @author Jakub
 */
@Deprecated
public class OldAStarNode {
    private OldINavMeshAtom atom;
    private OldAStarNode from;
    private List<OldAStarNode> followers;
    private double distanceFromStart;
    private double estimatedDistanceToTarget;
    private double estimatedTotalDistance;
    
    public OldAStarNode(OldAStarNode from, OldINavMeshAtom atom, OldNavMesh mesh, OldINavMeshAtom start, OldINavMeshAtom target) {
        
        this.from = from;
        this.atom = atom;
        followers = new ArrayList<OldAStarNode>();
        
        // count distance from start
        if(from == null) {
            distanceFromStart = 0;
        }
        else {
            distanceFromStart = from.getDistanceFromStart() + mesh.getDistance(from.atom, atom);
        }
        
        // count distance to end
        estimatedDistanceToTarget = mesh.getDistance(atom, target);
        
        // count total distance
        estimatedTotalDistance = distanceFromStart + estimatedDistanceToTarget;   
    }
    
    public double getDistanceFromStart() {
        return distanceFromStart;
    }
    public double getEstimatedDistanceToTarget() {
        return estimatedDistanceToTarget;
    }
    public double getEstimatedTotalDistance() {
        return estimatedTotalDistance;
    }
    public OldINavMeshAtom getAtom() {
        return atom;
    }
    public OldAStarNode getFrom() {
        return from;
    } 
    public List<OldAStarNode> getFollowers() {
        return followers;
    }
}
