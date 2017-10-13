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
package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathPlanner.AStar;

import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.INavMeshAtom;

/** A* algorithm node
 * 
 * @author Jakub
 */
public class NavMeshAStarNode {
    private INavMeshAtom atom;
    private NavMeshAStarNode previous;
    private double costFromStart;
    private double estimatedCostToTarget;
    
    /** Constructor
     * 
     * @param previous previous A* node
     * @param atom NavMesh atom associated with the constructed node
     * @param target
     */
    public NavMeshAStarNode( NavMeshAStarNode previous, INavMeshAtom atom, double costFromStart, double estimatedCostToTarget ) {
        
        this.previous = previous;
        this.atom = atom;
        this.costFromStart = costFromStart;
        this.estimatedCostToTarget = estimatedCostToTarget;
    }
    
    public double getCostFromStart() {
        return costFromStart;
    }
    
    public double getEstimatedCostFromNode() {
        return estimatedCostToTarget;
    }
    
    public double getEstimatedTotalCost() {
        return costFromStart + estimatedCostToTarget;
    }
    
    public INavMeshAtom getAtom() {
        return atom;
    }
    
    public NavMeshAStarNode getPrevious() {
        return previous;
    }
}
