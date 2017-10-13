package cz.cuni.amis.utils.astar;

import java.util.Collection;
import java.util.Iterator;

import cz.cuni.amis.utils.SafeEquals;

/**
 * ========================================================
 * This file holds implementation of generic A* algorithm,
 * better refered to as A* Machine according to 
 * Dan Higgins, Generic A* Pathfind, AI Gaming Wisdom, 2002
 * ========================================================
 * <p><p>
 * 
 * What is A*<p>
 * ----------<p>
 * A* is space-search algorithm using a custom-built heuristic. It's an improved
 * version of well-known Dijkstra algorithm which is used to find the shortest
 * path in weighted graphs. Instead of picking the node with the smallest path
 * from the start node it chooses node which seems to be on the shortest path
 * to the goal node (and this guess is based on provided heuristic).
 * <p><p>
 * Note<p>
 * ----<p>
 * Insted of weights we speak about cost of the edges. 
 * <p><p>
 * Limitation of A*<p>
 * ----------------<p>
 * 1) A* doesn't work over graphs with negative edge costs.<p>
 * 2) heuristic has to be correct -> it has to be lower estimation of the cost to
 *    the goal node (in 2D, 3D an euklidian metric will do the job).<p>
 * <p>
 * First we have to specify some interfaces for A*<p>
 * -----------------------------------------------<p>
 * we will need a few things: <p>
 *                            Open List Class<p> 
 *                            Close List Class<p>
 *                            Goal (which can tell us about extra costs, when work is done, etc)<p>
 *                            Map  (which tells us about travel cost between nodes and can return
 *                                  node's neighbours)<p>
 * <p>                                 
 * Note about Nodes<p>
 * ----------------<p>
 * Note that we don't need to have a Node interface so you're free to have
 * any nodes you want (POJOs). But implementation of A* requires the nodes to have
 * hashCode() and equals() implemented correctly, which should be a good practice!
 * (Note that also means you can't have two nodes which are equals in the map!)
 * <p><p>
 * Idea behind AStarGoal / AStarMap<p>
 * --------------------------------<p>
 * Usually you will have only one world / state space representation but you need to
 * change the cost of edges between nodes according to let say creature for which you
 * search the path.
 * <p><p>
 * Imagine the situation with the lake / human / fish.
 * Human may swim across the lake but it's faster to run around it (so you need to give the edges between
 * water tiles an extra cost).<p>
 * Fish can swim really fast but can't get out of the water (so you need to forbid tiles around the lake
 * and give the edges between the lakes' tiles an negative extra cost).<p>
 * <p>
 * So the AStarMap will represent the world with the lake with default cost of the edges.
 * AStarGoal may change the edges cost / forbid some nodes completely. So you will
 * implement one goal for a human and another for a fish.
 * <p><p>
 * Note about the speed<p>
 * --------------------<p>
 * Speed of algorithm is based upon the speed of AStarOpenList and AStarCloseList.
 * 
 * <p><p>
 * Use amis-path-finding library instead, see svn://artemis.ms.mff.cuni.cz/pogamut/trunk/project/Utils/AmisPathFinding
 */
@Deprecated
public class AStar<NODE> {
		
	/**
	 * Method performing an AStar search over graph defined inside {@link AStarMap} starting from 'start' node driving
	 * itself towards goal that is described by {@link AStarGoal}. Note that {@link AStarGoal} also contains a heuristic {@link AStarHeuristic}.
	 * <p><p>
	 * {@link AStarMap} provides informations about node neighbours and edge costs,
	 * while {@link AStarGoal} contains the definition of goal node and extra cost / extra info
	 * about map nodes.
	 * <p><p>
	 * You may also specify maxIterations - "how long the A* should search" equals
	 * to number of evaluated nodes. If it's 0 then A* won't even start! If it is < 0 it will run until the 'goal' is found
	 * all nodes are evaluated.
	 * 
	 * @param map
	 * @param start
	 * @param goal
	 * @param iterationsMax maximum of iterations to be made by algorithm during the search (negative number == infinite)
	 */
	public static <NODE> AStarResult<NODE> aStar(AStarMap<NODE> map, NODE start, AStarGoal<NODE> goal, long iterationsMax){
		
		// NOTE: values of the estimated cost is maintained in AStarResult
		// AS HEAP: we're using AStarHeap with AStarHeapComparator which is
		//          using data from AStarResult.estimatedCost ...
		//          that means you have to first alter AStarResult.estimatedCost
		//          before adding / decreasing key in AStarHeap
		
		AStarResult<NODE> result = new AStarResult<NODE>();
		
		AStarHeap<NODE> open = new AStarHeap<NODE>(new AStarHeapComparator<NODE>(result.estimatedCost), 64);
		result.openList = open;
		Collection<NODE> close = result.closeList;
		
		goal.setCloseList(result.closeList);
		goal.setOpenList(result.openList);
				
		result.startNode = start;
		
		result.putCostToNode(result.startNode, 0);
		result.putEstimatedCostToNode(result.startNode, goal.getEstimatedDistanceToGoal(result.startNode));
		open.add(result.startNode);		
		
		NODE node, nextNode;
		Collection<NODE> neighbours;
		Iterator<NODE> nodeIter;
		int nodePathCost, nextNodePathCost, travelCost, extraCost, estimatedPathCost,
		     newNextNodePathCost;
		
		while ((!open.empty()) && 
			   ((iterationsMax <= 0) || (result.interations < iterationsMax))
			  ){
			++result.interations; // new iteratrion begin
			
			node = open.getMin();
			
			if (node == null){ // failure
				result.success = false;
				break;
			}
			
			open.deleteMin();			
			
            if (goal.isGoalReached(node)){ // we've reached the goal HURRAY!
            	result.goalNode = node;
            	result.success = true;
            	break;
            }
            
            nodePathCost = result.getCostToNode(node);
            
            neighbours = map.getNodeNeighbours(node);
            nodeIter = neighbours.iterator();
            
            while (nodeIter.hasNext()){
            	nextNode = nodeIter.next();
           		// iterate over all of the neighbours node
			    // and evaluate them one by one
            	
            	if (!goal.isNodeOpened(nextNode)){  // stepping to this node is forbidden, skip it
            		continue;
            	}
            	
            	travelCost = map.getEdgeCost(node, nextNode);
            	extraCost = goal.getExtraCost(node, nextNode);
            	
            	nextNodePathCost = result.getCostToNode(nextNode);
            	if (nextNodePathCost == -1){ 
            		// we've never touched nextNode
            		nextNodePathCost = nodePathCost + travelCost + extraCost;
            		if (nextNodePathCost < 0) nextNodePathCost = 0;
            		result.putCostToNode(nextNode, nextNodePathCost);
            		result.putPreviousNode(nextNode, node);
            		
            		estimatedPathCost = nextNodePathCost + goal.getEstimatedDistanceToGoal(nextNode);
            		result.putEstimatedCostToNode(nextNode, estimatedPathCost);
            		
            		open.add(nextNode);
            		continue;
            	} else {                     
            		// we've already touched the nextNode                     
            		newNextNodePathCost = nodePathCost + travelCost + extraCost;
            		if (newNextNodePathCost < 0) newNextNodePathCost = 0;
            		if (newNextNodePathCost < nextNodePathCost){            			
            			estimatedPathCost = newNextNodePathCost + goal.getEstimatedDistanceToGoal(nextNode);
            			result.putCostToNode(nextNode, newNextNodePathCost);
            			result.putEstimatedCostToNode(nextNode, estimatedPathCost);
            			result.putPreviousNode(nextNode, node);
            			if (close.contains(nextNode)){
            				close.remove(nextNode);            				
            				open.add(nextNode);
            			} else 
            				if (open.contains(nextNode)){
            					open.decreaseKey(node);
            				} else {
            					open.add(nextNode);
            				}
            		}
                	// if estimatedCost is higher or equal, we don't have to take any actions
            		continue;
            	}
            }            
            close.add(node);
		}
		
		return result;
	}

	/**
	 * Method performing an AStar search over graph defined inside {@link AStarMap} starting from 'start' node driving
	 * itself towards goal that is described by {@link AStarGoal}. Note that {@link AStarGoal} also contains a heuristic {@link AStarHeuristic}.
	 * <p><p>
	 * {@link AStarMap} provides informations about node neighbours and edge costs,
	 * while {@link AStarGoal} contains the definition of goal node and extra cost / extra info
	 * about map nodes.
	 * <p><p>
	 * This method performs 'unbounded' AStar search, i.e., it does not limit a number of iterations the algorithm will perform.
	 * 
	 * @param <NODE>
	 * @param map
	 * @param start
	 * @param goal
	 * @return
	 */
	public static <NODE> AStarResult<NODE> aStar(final AStarMap<NODE> map, final NODE start, final AStarGoal<NODE> goal) {
		return aStar(map, start, goal, -1);
	}
	
	/**
	 * Method performing an AStar search over graph defined inside {@link AStarMap} starting from 'start' node driving
	 * itself towards 'goal' using heuristic and extra costs defined by {@link AStarEvaluator}.
	 * <p><p>
	 * {@link AStarMap} provides informations about node neighbours and edge costs,
	 * while {@link AStarEvaluator} contains definition of the heuristic + extra edge info about map nodes.
	 * <p><p>
	 * You may also specify maxIterations - "how long the A* should search" equals
	 * to number of evaluated nodes. If it's 0 then A* won't even start! If it is < 0 it will run until the 'goal' is found
	 * all nodes are evaluated.
	 * <p><p>
	 * <b>WARNING</b>: Class that is used for NODE must have correctly defined {@link Object#equals(Object)} because it will be used
	 * to recognized whether the current evaluated node is the same as the goal or not!
	 * 
	 * @param <NODE>
	 * @param map
	 * @param evaluator
	 * @param start
	 * @param goal
	 * @param maxIterations maximum of iterations to be made by algorithm during the search (negative number == infinite)
	 * @return
	 */
	public static <NODE> AStarResult<NODE> aStar(final AStarMap<NODE> map, final AStarEvaluator<NODE> evaluator, final NODE start, final NODE goal, int maxIterations) {
		return
			aStar(
				map, 
				start, 
					new AStarGoal<NODE>() {
			
						@Override
						public boolean isGoalReached(NODE actualNode) {
							return SafeEquals.equals(actualNode, goal);
						}
			
						@Override
						public void setCloseList(Collection<NODE> closeList) {
							// NOT NEEDED
						}
			
						@Override
						public void setOpenList(Collection<NODE> openList) {
							// NOT NEEDED
						}
			
						@Override
						public int getExtraCost(NODE nodeFrom, NODE nodeTo) {
							return evaluator.getExtraCost(nodeFrom, nodeTo);
						}
			
						@Override
						public boolean isNodeOpened(NODE node) {
							return evaluator.isNodeOpened(node);
						}
			
						@Override
						public int getEstimatedDistanceToGoal(NODE node) {
							return evaluator.getEstimatedDistanceToGoal(node);
						}
					
					},
				maxIterations
		);
	}
		
	/**
	 * Method performing an AStar search over graph defined inside {@link AStarMap} starting from 'start' node driving
	 * itself towards 'goal' using heuristic and extra costs defined by {@link AStarEvaluator}.
	 * <p><p>
	 * {@link AStarMap} provides informations about node neighbours and edge costs,
	 * while {@link AStarEvaluator} contains definition of the heuristic + extra edge info about map nodes.
	 * <p><p>
	 * This method performs 'unbounded' AStar search, i.e., it does not limit a number of iterations the algorithm will perform.
	 * <p><p>
	 * <b>WARNING</b>: Class that is used for NODE must have correctly defined {@link Object#equals(Object)} because it will be used
	 * to recognized whether the current evaluated node is the same as the goal or not!
	 * 
	 * @param <NODE>
	 * @param map
	 * @param evaluator
	 * @param start
	 * @param goal
	 * @return
	 */
	public static <NODE> AStarResult<NODE> aStar(final AStarMap<NODE> map, final AStarEvaluator<NODE> evaluator, final NODE start, final NODE goal) {
		return aStar(map, evaluator, start, goal, -1);
	}
	
	/**
	 * Method performing an AStar search over graph defined inside {@link AStarMap} starting from 'start' node driving
	 * itself towards 'goal' using heuristic defined by {@link AStarHeuristic}.
	 * <p><p>
	 * {@link AStarMap} provides informations about node neighbours and edge costs,
	 * while {@link AStarHeuristic} contains definition of the heuristic.
	 * <p><p>
	 * You may also specify maxIterations - "how long the A* should search" equals
	 * to number of evaluated nodes. If it's 0 then A* won't even start! If it is < 0 it will run until the 'goal' is found
	 * all nodes are evaluated.
	 * <p><p>
	 * <b>WARNING</b>: Class that is used for NODE must have correctly defined {@link Object#equals(Object)} because it will be used
	 * to recognized whether the current evaluated node is the same as the goal or not!
	 * 
	 * @param <NODE>
	 * @param map
	 * @param evaluator
	 * @param start
	 * @param goal
	 * @param maxIterations
	 * @return
	 */
	public static <NODE> AStarResult<NODE> aStar(final AStarMap<NODE> map, final AStarHeuristic<NODE> heuristic, final NODE start, final NODE goal, int maxIterations) {
		return aStar(map, start, new AStarGoal<NODE>() {

			@Override
			public boolean isGoalReached(NODE actualNode) {
				return SafeEquals.equals(actualNode, goal);
			}

			@Override
			public void setCloseList(Collection<NODE> closeList) {
				// NOT NEEDED
			}

			@Override
			public void setOpenList(Collection<NODE> openList) {
				// NOT NEEDED
			}

			@Override
			public int getExtraCost(NODE nodeFrom, NODE nodeTo) {
				return 0;
			}

			@Override
			public boolean isNodeOpened(NODE node) {
				return true;
			}

			@Override
			public int getEstimatedDistanceToGoal(NODE node) {
				return heuristic.getEstimatedDistanceToGoal(node);
			}
			
		},
		maxIterations);
	}
		
	/**
	 * Method performing an AStar search over graph defined inside {@link AStarMap} starting from 'start' node driving
	 * itself towards 'goal' using heuristic defined by {@link AStarHeuristic}.
	 * <p><p>
	 * {@link AStarMap} provides informations about node neighbours and edge costs,
	 * while {@link AStarHeuristic} contains definition of the heuristic.
	 * <p><p>
	 * This method performs 'unbounded' AStar search, i.e., it does not limit a number of iterations the algorithm will perform.
	 * <p><p>
	 * <b>WARNING</b>: Class that is used for NODE must have correctly defined {@link Object#equals(Object)} because it will be used
	 * to recognized whether the current evaluated node is the same as the goal or not!
	 * 
	 * @param <NODE>
	 * @param map
	 * @param heuristic
	 * @param start
	 * @param goal
	 * @return
	 */
	public static <NODE> AStarResult<NODE> aStar(final AStarMap<NODE> map, final AStarHeuristic<NODE> heuristic, final NODE start, final NODE goal) {
		return aStar(map, heuristic, start, goal, -1);
	}

}