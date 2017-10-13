package cz.cuni.amis.pathfinding.alg.astar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import cz.cuni.amis.utils.heap.Heap;

/**
 * Represents result of the computation of {@link AStar#AStar.findPath(cz.cuni.amis.pathfinding.map.IPFGoal, long)}.
 * <p><p>
 * It contains results from the search as well as method for finding
 * the path from the startNode to the goalNode.
 * <p><p>
 * It contains all data structures the AStar is using during the work.
 * Everything is made public here so that AStar (during work) and you (for
 * browsing the results) may use it.
 */
public class AStarResult<NODE> implements IAStarResult<NODE> {
	
	/**
	 * Used by getPath() and filled by A* algorithm (AStar.aStar()). 
	 * Keys are nodes and values are 'parent-nodes' (from where we've come to the key node
	 * on the path from startNode to key-node).
	 */
	public HashMap<NODE, NODE> previousNode = new HashMap<NODE, NODE>();
	
	/**
	 * List of nodes which is opened -> was touched by the algorithm and are
	 * subjects of examination.
	 * 
	 * Initialized by AStar.aStar()
	 */
	public Heap<NODE> openList;
	
	/**
	 * Nodes which were examined by the algorithm.
	 * At the end of work of AStar contains all the nodes which were examined.
	 * If AStar successful -> at least contains nodes on the shortest path from
	 * startNode to goalNode.
	 */
	public Set<NODE> closeList;
	
	/**
	 * Used and filled by A* algorithm (AStar.aStar()).
	 * Here we store the real cost from the startNode to the 'key'.
	 */
	public HashMap<NODE, Integer> pathCost = new HashMap<NODE, Integer>();
	
	/**
	 * Used and filled by A* algorithm (AStar.aStar()).
	 * Here we store estimated cost of the path from 'key' to the goal.
	 */
	public HashMap<NODE, Integer> estimatedCost = new HashMap<NODE, Integer>();
	
	/**
	 * Contains the number of iterations made by A* search.
	 * One iteration means evaluating of the one node ("touching" each of the neighbours)
	 * Is 0-based ... if startGoal == goalNode or startGoal then this will be 0. 
	 */
	public long interations = 0;
	
	/**
	 * Start node of the A*.
	 */
	public NODE startNode = null;
	
	/**
	 * Node which was marked as a goalNode by AStarMap. (Note that you theoreticaly may have many
	 * goal nodes but A* searches only for the first one.)
	 * It's filled only if A* found the goalNoda! (success == true) 
	 */
	public NODE goalNode = null;
	
	/**
	 * Whether goalNode was found during the A* run.
	 * If this is true then goalNode is not null.
	 */
	public boolean success = false;
	
	/**
	 * Used by getPath() to cache the path from startNode to goalNode once it has
	 * been found.
	 */
	private List<NODE> path = null;

	@Override
	public NODE getPreviousNode(NODE node){
		if (previousNode.containsKey(node)) return previousNode.get(node);
		else                                return null;
	}	
	
	/**
	 * Assing 'previous' as an previous node for 'node' (the path from 'startNode' to 'node' goes across 'previous').
	 * @param node
	 * @param previous
	 */
	public void putPreviousNode(NODE node, NODE previous){
		previousNode.put(node, previous);
	}
	
	@Override
	public int getCostToNode(NODE node){
		if (pathCost.containsKey(node)) 
			return (pathCost.get(node)).intValue();
		else                            
			return -1;
	}
	
	/**
	 * Assing cost of the path from startNode to node.
	 * @param node
	 * @param cost
	 */
	public void putCostToNode(NODE node, Integer cost){
		pathCost.put(node, cost);
	}

	@Override
	public int getEstimatedCostToNode(NODE node){
		if (estimatedCost.containsKey(node)) 
			return estimatedCost.get(node);
		else                                 
			return -1;
	}
	
	/**
	 * Assing estimated cost of the path from startNode to goalNode through node.
	 * @param node
	 * @param cost
	 */
	public void putEstimatedCostToNode(NODE node, Integer cost){
		estimatedCost.put(node, cost);
	}

	@Override
	public List<NODE> getPath(){
		if (path != null) 
			return path;
		if (!success)
			return null;
		
		Stack<NODE> tempPath = new Stack<NODE>();		
		tempPath.push(goalNode);
		
		NODE node = goalNode;
		while (node != startNode){
			node = getPreviousNode(node);
			if (node == null)
				return null; // path doesn't exist
			tempPath.push(node);
		}

		path = new ArrayList<NODE>();
		
		while (!tempPath.empty()){
			path.add(tempPath.pop());
		}
			
		return path;
	}
	
	@Override
	public int getDistanceToGoal(){
		if (!this.success) return -1;
		return (this.pathCost.get(goalNode)).intValue();
	}

	@Override
	public boolean isSuccess() {
		return success;
	}	
	
}