package cz.cuni.amis.utils.astar;

import java.util.Collection;

/**
 * This class represents the search space for A* algorithm
 * 1) we need to know which neighbours the node has
 * 2) we need to know the travel cost between two nodes (edge cost)
 * 
 * <p><p>
 * Use amis-path-finding library instead, see svn://artemis.ms.mff.cuni.cz/pogamut/trunk/project/Utils/AmisPathFinding
 */
@Deprecated
public interface AStarMap<NODE> {
	
	/**
	 * General cost of having this node at your path. This allows you to say how every node appeals to the agent, 
	 * it may specify "this is a cool node, try to get it on your path" (negative cost) or "this is neutral node"
	 * (zero cost) or "this is a bad node to have on your path" (positive cost).
	 * 
	 * @param node
	 * @return
	 */
	public int getNodeCost(NODE node);
	
	/**
	 * Should return the distance from nodeFrom to nodeTo
     * You can be sure that nodeTo is among the neighbours of nodeFrom.
	 * @param nodeFrom
	 * @param nodeTo
	 * @return cost of an edge
	 */
	public int getEdgeCost(NODE nodeFrom, NODE nodeTo);
	
	/**
	 * This should return a collection of nodes which are connected to this one.
	 */
	public Collection<NODE> getNodeNeighbours(NODE node);
}
