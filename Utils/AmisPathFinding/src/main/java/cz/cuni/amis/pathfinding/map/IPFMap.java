package cz.cuni.amis.pathfinding.map;

import java.util.Collection;
import java.util.HashMap;

/**
 * This class represents the discrete search space for path-finding algorithms for games. It conceptualize the map/location/environment of the game for
 * the purpose of planners as finite graph whose nodes are easily distinguishable from each others (it is suitable for NavigationGraphs using discrete navigation
 * points, but it is not suitable for GOAP planners for strategic games such as Defcon).
 * <p><p>
 * The map is perceived as oriented graph that does not have "multi-edges" (it is not a multigraph).
 * <p><p>
 * Every environment must at least provide:
 * <ol>
 * <li>function that can compute neighbors of every node ({@link IPFMap#getNeighbors(Object)})</li>
 * <li>function that provides "cost" of the node (i.e., what is the cost of having the node in the path) ({@link IPFMap#getNodeCost(Object)})</li>
 * <li>function that provides "cost" of the arc (arc == directed edge, i.e., what is the cost of traveling through edge between two nodes in the graph) ({@link IPFMap#getArcCost(Object)})</li>
 * </ol>
 * <p><p>
 * Note that the interface is parameterized by "NODE" which might be arbitrary object, that is you may use POJOs here. But be careful
 * as algorithms using the map usually assumes that {@link Object#hashCode()} and {@link Object#equals(Object)} are correctly implemented
 * for them (i.e., they may use the nodes as keys inside {@link HashMap}s).
 * <p><p>
 * Note that the implementation of such interface should not provide any "view hacks", i.e., means for suppressing the presence of some nodes/arc of the graph.
 * Such tweaks should be implemented using {@link IPFMapView}.
 * <p><p>
 * Note that this interface is suitable for "exploratory" algorithms such as A-Star, that is, algorithms which gradually search the space given some "seed" (starting point).
 * If you wish to use "grand-scale" algorithms such as Floyd-Warshall (where you have to know number of all nodes in advance), use {@link IPFKnownMap}.
 * 
 * @param NODE
 */
public interface IPFMap<NODE> {
	
	/**
	 * This should return a collection of nodes which are connected to this one by some arc (== oriented edge).
	 * I.e., return collection of nodes that are directly accessible from "node".
	 * <p><p>
	 * "node" MUST NOT BE PART OF THE COLLECTION!
	 * <p><p>
	 * Returned collection must not contain multiple references to a single neighbor (multi-graph is forbidden).
	 * 
	 * @param node
	 * @return all neighbors of the node (arc exists between 'node' and every node inside returned collection)
	 */
	public Collection<NODE> getNeighbors(NODE node);
	
	/**
	 * General cost of having this node at your path. This allows you to say how much each node appeals to the agent, 
	 * it may specify "this is a cool node, try to get it on your path" (negative cost) or "this is neutral node"
	 * (zero cost) or "this is a bad node to have on your path" (positive cost).
	 * <p><p>
	 * This might be highly dependent on the agent so the default implementation will probably just return "0".
	 * 
	 * @param node
	 * @return cost of the node
	 */
	public int getNodeCost(NODE node);
	
	/**
	 * Should return the cost of traveling from "nodeFrom" to "nodeTo".
     * You can be sure that "nodeTo" is among the neighbors of "nodeFrom" as they were returned by 
     * {@link IPFMap#getNeighbors(Object)} or from {@link IPFMapView#getExtraNeighbors(Object)}. 
     * <p><p>
     * Note that notion of "cost" might be highly dependent on the agent, thus it may have the sense to provide it
     * only a general distance between "nodeFrom" and "nodeTo".
     * <p><p>
     * The method can be also perceived as having name "getDistance".
     * 
	 * @param nodeFrom
	 * @param nodeTo
	 * @return cost of an arc
	 */
	public int getArcCost(NODE nodeFrom, NODE nodeTo);
	
}
