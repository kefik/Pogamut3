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

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;

import java.util.List;

/**
 * Interface for navmesh atom. It can be either Polygon or off-mesh point
 * is used in pathfinng algorithms as an abstraction of polygon/point
 * @author Jakub
 */
@Deprecated
public interface OldINavMeshAtom {
    
    /**
     * Gets a list of all neighbousrs of this atom in navmesh. That includes both polygons and offmesh points.
     * @return 
     */
    public List<OldINavMeshAtom> getNeighbours(OldNavMesh mesh);
    
    /**
     * Compares atoms if they are the same (same class, same polygon/point)
     * @param atom
     * @return 
     */
    public boolean equals(OldINavMeshAtom atom);
    
}
