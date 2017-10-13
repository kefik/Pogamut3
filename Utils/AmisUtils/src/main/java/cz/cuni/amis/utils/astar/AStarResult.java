package cz.cuni.amis.utils.astar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

/**
 * This class is returned by AStar.aStar().
 * It contains results from the search as well as method for
 * finding
 *  the path from the startNode to the goalNode.
 * 
 * It contains all data structures the AStar is using during the work.
 * Everything is made public here so that AStar (during work) and you (for
 * browsing the results) may use it.
 * 
 * <p><p>
 * Use amis-path-finding library instead, see svn://artemis.ms.mff.cuni.cz/pogamut/trunk/project/Utils/AmisPathFinding
 */
@Deprecated
public class AStarResult<NODE> {
	
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
	public Collection<NODE> openList;
	
	/**
	 * Nodes which were examined by the algoritm.
	 * At the end of work of AStar contains all the nodes which were examined.
	 * If AStar successful -> at least contains nodes on the shortest path from
	 * startNode to goalNode.
	 */
	public Collection<NODE> closeList = new HashSet<NODE>();
	
	/**
	 * Used and filled by A* algorithm (AStar.aStar()).
	 * Here we store the real cost from the startNode to the 'key'.
	 */
	public HashMap<NODE, Integer> pathCost = new HashMap<NODE, Integer>();
	
	/**
	 * Used and filled by A* alorithm (AStar.aStar()).
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
	
	/**
	 * Used by getPath() method when creating a list of nodes (the path) from startNode
	 * to goalNode.
	 * @param node
	 * @return previous node of supplied node | null
	 */
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
	
	/**
	 * Returns cost of the path from startNode to node if the node was touched
	 * by A* algorithm (if A* was successful, then this always contains the goalNode
	 * and every node on the path).
	 * 
	 * If node wasn't touched by A* algorithm, then it returns -1.
	 * 
	 * @param node
	 * @return cost of the path from startNode to node
	 */
	public int getCostToNode(NODE node){
		if (pathCost.containsKey(node)) 
			return ((Integer)pathCost.get(node)).intValue();
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
	
	/**
	 * Returns estimated cost of the path from startNode to goal through node.
	 * If the node was touched by A* algorithm then it has this value stored here
	 * (if A* was successful, then this always contains the goalNode
	 * and every node on the path).
	 * 
	 * If node wasn't touched by A* algorithm, then it returns -1.
	 * 
	 * @param node
	 * @return cost of the path from startNode to node
	 */
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
	
	/**
	 * Returns the path from startNode to goalNode. (Don't change it as it's cached,
	 * if you want to alter it - then copy it :-)
	 * 
	 * First item is startNode and the last item is goalNode.
	 * If startNode == goalNode then it contains only one item.
	 * For each index ... path[index] has neighbour path[index+1].
	 * 
	 * If the path doesn't exist - returns null.
	 */
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
	
	/**
	 * If the AStar succeeded then it returns the distance to the goal.
	 * Returns -1 otherwise.
	 * @return distance | -1
	 */
	public int getDistanceToGoal(){
		if (!this.success) return -1;
		return ((Integer) this.pathCost.get(goalNode)).intValue();
	}
}