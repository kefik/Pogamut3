package cz.cuni.amis.pathfinding.alg.astar;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import cz.cuni.amis.pathfinding.map.IPFGoal;
import cz.cuni.amis.pathfinding.map.IPFMap;
import cz.cuni.amis.pathfinding.map.IPFMapView;
import cz.cuni.amis.pathfinding.map.IPFMapView.DefaultView;
import cz.cuni.amis.utils.Iterators;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.astar.AStarGoal;
import cz.cuni.amis.utils.astar.AStarHeuristic;
import cz.cuni.amis.utils.astar.AStarMap;
import cz.cuni.amis.utils.heap.Heap;
import cz.cuni.amis.utils.heap.ImmutableHeap;

/**
 * Implementation of generic A* algorithm, better refered to as A* Machine according to 
 * Dan Higgins, Generic A* Pathfind paper from AI Gaming Wisdom, 2002
 * <p><p>
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
 * 2) heuristic has to be correct & monotonic
 * <p>
 * First we have to specify some interfaces for A* to work<p>
 * ------------------------------------------------------<p>
 * <ol>
 * <li>{@link IPFMap} that provides the search-space notion for the A*, this implementation should be agent-agnostic, that is it should not incorporate
 * any particular limitations/abilities into it</li>
 * <li>{@link IPFMapView} that provides agent's customized view of the map (extra arc costs, node filtering, etc.)</li>
 * <li>{@link IPFGoal} that provides the heuristic function + the goal definition</li>
 * <p>                                 
 * Note about Nodes<p>
 * ----------------<p>
 * Note that we don't need to have a Node interface so you're free to have
 * any nodes you want (POJOs). But implementation of A* requires the nodes to have
 * {@link Object#hashCode()} and {@link Object#equals()} implemented correctly, which should be a good practice!
 * <p>
 * Note that also means you can't have two nodes which are equals in the map! 
 * <p>
 * Note that if you have "unique object" for every "node",
 * then the Java standard {@link Object#hashCode()} and {@link Object#equals()} implementations (pointer checking) are sufficient. 
 * <p><p>
 * Ideas behind {@link IPFMap}, {@link IPFMapView} {@link IPFGoal}<p>
 * ---------------------------------------------------------------<p>
 * Usually you will have only one world / state space representation (that is {@link IPFMap}) but you need to
 * change the cost of edges between nodes according to your agent (imagine a fish) for which you
 * search the path ({@link IPFMapView}). Finally you will need to search the space for different nodes
 * possibly using different heuristics based on the goal pursued ({@link IPFGoal}).
 * <p><p>
 * Imagine the situation with the lake (map) / human (one agent) / fish (another agent).
 * Human may swim across the lake but it's faster to run around it (so you need to give the edges between
 * water tiles an extra cost using {@link IPFMapView#getArcExtraCost(Object, Object, int)}).<p>
 * Fish can swim really fast but can't get out of the water (so you need to forbid tiles around the lake
 * and give the edges between the lakes' tiles using {@link IPFMapView#isNodeOpened(Object)}).<p>
 * Finally you may have hierarchical representation of your graph that has different arc-cost for humans
 * and fishes (taking into account the lake), thus you might need to specify different heuristic function for
 * humans and fishes using {@link IPFGoal#getEstimatedCostToGoal(Object)}. 
 * <p>
 * So the AStarMap will represent the world with the lake with default cost of the edges.
 * AStarGoal may change the edges cost / forbid some nodes completely. So you will
 * implement one goal for a human and another for a fish.
 * <p><p>
 * Note about the speed<p>
 * --------------------<p>
 * Speed of algorithm is based upon the speed of AStarOpenList and AStarCloseList.
 */
public class AStar<NODE> {
	
	/**
	 * Holds the representation of the map.
	 */
	private IPFMap<NODE> map;
	
	/**
	 * Holds the agent-specific view of the map.
	 */
	private IPFMapView<NODE> view;
	
	/**
	 * AStar configured with "map" with no agent-specific view on the map, {@link DefaultView} is used. 
	 * @param map
	 */
	public AStar(IPFMap<NODE> map) {
		this.map = map;
		this.view = new IPFMapView.DefaultView();
		NullCheck.check(this.map, "map");
	}
	
	/**
	 * AStar configured with "map" and agent-specific view on the map, if "view" is null, {@link DefaultView} is going to be used. 
	 * @param map
	 * @param view may be null
	 */
	public AStar(IPFMap<NODE> map, IPFMapView<NODE> view) {
		this.map = map;
		this.view = view;
		NullCheck.check(this.map, "map");
		if (this.view == null) {
			this.view = new IPFMapView.DefaultView();
		}
	}
		
	/**
	 * Map abstraction the AStar is working with.
	 * @return
	 */
	public IPFMap<NODE> getMap() {
		return map;
	}

	/**
	 * Sets map abstraction into the AStar.
	 * @param map
	 */
	public synchronized void setMap(IPFMap<NODE> map) {
		this.map = map;
	}

	/**
	 * Returns agent-specific map view for the map.
	 * @return
	 */
	public IPFMapView<NODE> getMapView() {
		return view;
	}

	/**
	 * Sets agent-specific map view for the map. 
	 * @param mapView
	 */
	public synchronized void setMapView(IPFMapView<NODE> mapView) {
		this.view = mapView;
	}
	
	////////////////////////////////////////////////////////////
	// AStar runtime variables - cleared after aStar() finishes.
	////////////////////////////////////////////////////////////
	
	private IPFGoal<NODE> goal = null;
	private long iterationsMax = 0;
	private AStarResult<NODE> result = null;

	/**
	 * Method performing an AStar search over graph defined inside {@link IPFMap} starting from 'start' node driving
	 * itself towards goal that is described by {@link AStarGoal}. Note that {@link AStarGoal} also contains a heuristic {@link AStarHeuristic}.
	 * <p><p>
	 * {@link AStarMap} provides informations about node neighbours and edge costs,
	 * while {@link AStarGoal} contains the definition of goal node and extra cost / extra info
	 * about map nodes.
	 * <p><p>
	 * You may also specify maxIterations - "how long the A* should search" equals
	 * to number of evaluated nodes. If it's < 0 then A* will run until the 'goal' is found
	 * all nodes are evaluated and there is nowhere to search. If it is == 0, the A* won't even start!
	 * 
	 * @param map
	 * @param start
	 * @param goal
	 * @param iterationsMax maximum of iterations to be made by algorithm during the search (zero or negative number == infinite)
	 */
	public synchronized AStarResult<NODE> findPath(IPFGoal<NODE> goal, long iterationsMax) {
		
		// NOTE: values of the estimated cost is maintained in AStarResult
		// AS HEAP: we're using Heap with AStarHeapComparator which is
		//          using data from AStarResult.estimatedCost ...
		//          that means you have to first alter AStarResult.estimatedCost
		//          before adding / decreasing key in AStarHeap
		
		this.goal = goal;
		this.iterationsMax = iterationsMax;
		
		NODE start = goal.getStart();
		this.result = new AStarResult<NODE>();
				
		result.openList = new Heap<NODE>(new AStarHeapComparator<NODE>(result.estimatedCost), 64);
		result.closeList = new HashSet<NODE>();
		Collection<NODE> close = result.closeList;
		
		goal.setCloseList(Collections.unmodifiableSet(result.closeList));
		goal.setOpenList(new ImmutableHeap(result.openList));
				
		result.startNode = start;
		
		result.putCostToNode(result.startNode, 0);
		result.putEstimatedCostToNode(result.startNode, goal.getEstimatedCostToGoal(result.startNode));
		result.openList.add(result.startNode);		
		
		NODE node, nextNode;
		Collection<NODE> neighbors;
		Collection<NODE> extraNeighbors;
		Iterator<NODE> nodeIter;
		int nodePathCost, nextNodePathCost, travelCost, extraCost, nodeCost, nodeExtraCost, estimatedPathCost, newNextNodePathCost;
		
		while ((!result.openList.empty()) && 
			   ((this.iterationsMax <= 0) || (result.interations < iterationsMax))
			  ){
			++result.interations; // new iteration begin
			
			node = result.openList.getMin();
			
			if (node == null){ // failure
				result.success = false;
				break;
			}
			
			result.openList.deleteMin();			
			
            if (goal.isGoalReached(node)){ // we've reached the goal HURRAY!
            	result.goalNode = node;
            	result.success = true;
            	break;
            }
            
            nodePathCost = result.getCostToNode(node);
            
            neighbors = map.getNeighbors(node);
            extraNeighbors = view.getExtraNeighbors(node, neighbors);
            nodeIter = new Iterators<NODE>(
            				neighbors == null ? null : neighbors.iterator(), 
            				extraNeighbors == null ? null : extraNeighbors.iterator()
            		   );
            
            while (nodeIter.hasNext()){
           		// iterate over all of the neighbors node
            	nextNode = nodeIter.next();
            	if (nextNode == null) continue;
			    // and evaluate them one by one
            	
            	if (!view.isNodeOpened(nextNode)){  // stepping to this node is forbidden, skip it
            		continue;
            	}
            	if (!view.isArcOpened(node, nextNode)) { // travelling through this arc is forbidden, skip it
            		continue;
            	}
            	
            	travelCost = map.getArcCost(node, nextNode);
            	extraCost = view.getArcExtraCost(node, nextNode, travelCost);
            	
            	nodeCost = map.getNodeCost(nextNode);
            	nodeExtraCost = view.getNodeExtraCost(nextNode, nodeCost);
            	
            	nextNodePathCost = result.getCostToNode(nextNode);
            	if (nextNodePathCost == -1){ 
            		// we've never touched nextNode
            		nextNodePathCost = nodePathCost + travelCost + extraCost + nodeCost + nodeExtraCost;
            		if (nextNodePathCost < 0) nextNodePathCost = 0;
            		result.putCostToNode(nextNode, nextNodePathCost);
            		result.putPreviousNode(nextNode, node);
            		
            		estimatedPathCost = nextNodePathCost + goal.getEstimatedCostToGoal(nextNode);
            		result.putEstimatedCostToNode(nextNode, estimatedPathCost);
            		
            		result.openList.add(nextNode);
            		continue;
            	} else {                     
            		// we've already touched the nextNode                     
            		newNextNodePathCost = nodePathCost + travelCost + extraCost + nodeCost + nodeExtraCost;
            		if (newNextNodePathCost < 0) newNextNodePathCost = 0;
            		if (newNextNodePathCost < nextNodePathCost){            			
            			estimatedPathCost = newNextNodePathCost + goal.getEstimatedCostToGoal(nextNode);
            			result.putCostToNode(nextNode, newNextNodePathCost);
            			result.putEstimatedCostToNode(nextNode, estimatedPathCost);
            			result.putPreviousNode(nextNode, node);
            			if (close.contains(nextNode)){
            				close.remove(nextNode);            				
            				result.openList.add(nextNode);
            			} else 
            				if (result.openList.contains(nextNode)){
            					result.openList.decreaseKey(node);
            				} else {
            					result.openList.add(nextNode);
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
	 * Method performing an AStar search over graph defined inside {@link IPFMap} starting from 'start' node driving
	 * itself towards goal that is described by {@link IPFGoal}. Note that {@link IPFGoal} also contains a heuristic function.
	 * <p><p>
	 * {@link IPFMap} provides informations about node neighbours and edge costs,
	 * while {@link IPFGoal} contains the definition of goal node and extra cost / extra info
	 * about map nodes.
	 * <p><p>
	 * Does not have any cap on the number of evaluated nodes. Will run until the 'goal' is found
	 * all nodes are evaluated and there is nowhere to search.
	 * 
	 * @param goal defines START-NODE + GOAL-NODE
	 * @param mapView use custom {@link IPFMapView}
	 */
	public synchronized AStarResult<NODE> findPath(IPFGoal<NODE> goal, IPFMapView<NODE> mapView) {
		IPFMapView<NODE> oldView = view;
		this.view = mapView;
		AStarResult<NODE> result = findPath(goal, 0);
		this.view = oldView;
		return result;
	}

	/**
	 * Method performing an AStar search over graph defined inside {@link IPFMap} starting from 'start' node driving
	 * itself towards goal that is described by {@link IPFGoal}. Note that {@link IPFGoal} also contains a heuristic function.
	 * <p><p>
	 * {@link IPFMap} provides informations about node neighbours and edge costs,
	 * while {@link IPFGoal} contains the definition of goal node and extra cost / extra info
	 * about map nodes.
	 * <p><p>
	 * Does not have any cap on the number of evaluated nodes. Will run until the 'goal' is found
	 * all nodes are evaluated and there is nowhere to search.
	 * 
	 * @param goal defines START-NODE + GOAL-NODE
	 */
	public synchronized AStarResult<NODE> findPath(IPFGoal<NODE> goal) {
		return findPath(goal, 0);
	}
	
}