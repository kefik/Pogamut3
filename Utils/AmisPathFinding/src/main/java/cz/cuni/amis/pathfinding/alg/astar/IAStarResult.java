package cz.cuni.amis.pathfinding.alg.astar;

import java.util.List;

/**
 * This interface is returned by {@link AStar#AStar.findPath(cz.cuni.amis.pathfinding.map.IPFGoal, long)}.
 * It contains results from the search as well as method for finding the path from the startNode to the goalNode.
 */
public interface IAStarResult<NODE> {
	
	/**
	 * Previous node in the path to the goal node.
	 * @param node
	 * @return previous node of supplied node | null
	 */
	public NODE getPreviousNode(NODE node);
		
	/**
	 * Returns cost of the path from startNode to node if the node was touched
	 * by A* algorithm (if A* was successful, then this always contains the goalNode
	 * and every node on the path at least).
	 * <p><p>
	 * If node wasn't touched by A* algorithm, then it returns -1.
	 * 
	 * @param node
	 * @return cost of the path from startNode to node
	 */
	public int getCostToNode(NODE node);
	
	/**
	 * Returns estimated cost of the path from startNode to goal through node.
	 * If the node was touched by A* algorithm then it has this value stored here
	 * (if A* was successful, then this always contains the goalNode and every node on the path at least).
	 * <p><p>
	 * If node wasn't touched by A* algorithm, then it returns -1.
	 * 
	 * @param node
	 * @return cost of the path from startNode to node
	 */
	public int getEstimatedCostToNode(NODE node);
		
	/**
	 * Returns the path from startNode to goalNode. (Don't change it as it's cached,
	 * if you want to alter it - then copy it :-)
	 * <p><p>
	 * First item is startNode and the last item is goalNode.
	 * If startNode == goalNode then it contains only one item.
	 * For each index ... path[index] has neighbor path[index+1].
	 * <p><p>
	 * If the path doesn't exist - returns null.
	 * 
	 * @return path
	 */
	public List<NODE> getPath();
	
	/**
	 * If the AStar succeeded then it returns the distance to the goal from the start node.
	 * <p><p>
	 * Returns -1 otherwise.
	 * 
	 * @return distance | -1
	 */
	public int getDistanceToGoal();

	/**
	 * Whether this result represents the success, i.e., path from start to goal node has been found.
	 * @return
	 */
	public boolean isSuccess();
	
}