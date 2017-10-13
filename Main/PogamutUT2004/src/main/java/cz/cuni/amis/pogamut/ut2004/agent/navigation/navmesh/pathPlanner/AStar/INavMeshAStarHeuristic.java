package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathPlanner.AStar;

import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.INavMeshAtom;

/** A heuristic usable by the A* algorithm
 * 
 */
public interface INavMeshAStarHeuristic {

	/** Create a new A* node extending the A* star graph
	 * 
	 * @param node A* node to extend the graph from
	 * @param adjacentAtom atom to extend A* graph to
	 * @param destinationAtom destination atom
	 * @return A* node extending the A* graph to the adjacent atom
	 */
	public NavMeshAStarNode extend( NavMeshAStarNode node, INavMeshAtom adjacentAtom, INavMeshAtom destinationAtom );
}
