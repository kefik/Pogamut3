package cz.cuni.amis.pathfinding.alg.floydwarshall;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pathfinding.map.IPFKnownMap;
import cz.cuni.amis.pathfinding.map.IPFKnownMapView;
import cz.cuni.amis.pathfinding.map.IPFMap;
import cz.cuni.amis.pathfinding.map.IPFMapView;
import cz.cuni.amis.pathfinding.map.IPFMapView.DefaultView;
import cz.cuni.amis.utils.Iterators;
import cz.cuni.amis.utils.NullCheck;

/**
 * Floyd-Warshall algorithm for precomputing all-possible paths within the {@link IPFKnownMap}.
 * <p><p>
 * It precomputes all the paths inside the environment using Floyd-Warshall
 * algorithm (time: O(n^3)) in the form of matrix.
 * <p><p> 
 * Use {@link FloydWarshall#isReachable(Object, Object)}, {@link FloydWarshall#getPathCost(Object, Object)}
 * and {@link FloydWarshall#getPath(Object, Object)} to obtain information about computed paths.
 * the info about the path.
 * <p><p>
 * {@link FloydWarshall#getPath(Object, Object)} is caching retrieved paths using {@link SoftReference}s.
 * <p><p>
 * If needed you may call {@link FloydWarshall#compute()} to recompute paths, this call is needed if you set new map / agent view
 * using {@link FloydWarshall#setMap(IPFKnownMap)} or {@link FloydWarshall#setMapView(IPFMapView)}.
 * <p><p>
 * Based upon the implementation from Martin Krulis with his kind consent, thank you! Even though it was heavily recreated by now ;-)
 * <p><p>
 * NOTE: requires O(map.nodes.size^2) of memory! So be careful.
 * <p><p>
 * NOTE: you should read javadocs for {@link IPFMap}, {@link IPFKnownMap} and {@link IPFMapView} to understand how you can separate 
 * your map representation from various agent "views on the map" (i.e., how to provide customized path finding 
 * for different agents, e.g., fish vs. human)
 * 
 * @author Jimmy
 */
public class FloydWarshall<NODE>  {
	
	/**
	 * Class describing cell inside the FloydWarshall matrix holding additional informations relating to the path between two
	 * nodes.
	 * <p><p>
	 * These nodes are stored under "indices" inside {@link FloydWarshall#pathMatrix}.
	 * 
	 * @author Jimmy
	 *
	 * @param <N>
	 */
	public static class PathMatrixNode<N> {

		private int cost = Integer.MAX_VALUE;
		private Integer viaNode = null;
		private SoftReference<List<N>> path = null;

		/**
		 * Doesn't leading anywhere
		 */
		public PathMatrixNode() {
		}

		public PathMatrixNode(int cost) {
			this.cost = cost;
		}
		
		/**
		 * Returns aprox. number of bytes used by this class (for 32-bit Java, might be as twice as much for 64-bit!) including
		 * currently cached path.
		 * @return
		 */
		public int getBytesAprox() {
			List<N> path = (this.path != null ? this.path.get() : null);
			return 4 + 8 + (path == null ? 4 : 4 + path.size() * 4); 
		}
		
		/**
		 * Returns aprox. number of bytes used by this class (for 32-bit Java, might be as twice as much for 64-bit!) EXCLUDING
		 * currently cached path.
		 * 
		 * @return
		 */
		public int getBytesAproxWithoutPath() {
			List<N> path = (this.path != null ? this.path.get() : null);
			return 4 + 8 + 4; 
		}

		/**
		 * Returns the cost of the path between nodes, if the path does not exist, returns {@link Integer#MAX_VALUE}.
		 * @return
		 */
		public int getPathCost() {
			return cost;
		}

		/**
		 * Sets the cost of the path between nodes.
		 * @param cost
		 */
		public void setPathCost(int cost) {
			this.cost = cost;
		}

		/**
		 * Returns the node you have to travel through.
		 * @return
		 */
		public Integer getViaNode() {
			return viaNode;
		}

		/**
		 * Sets the node you have to travel through.
		 * @param indice
		 */
		public void setViaNode(Integer indice) {
			this.viaNode = indice;
		}

		/**
		 * Returns the full path between nodes.
		 * <p><p>
		 * WARNING: this is cached path! Might return null even though such path exists! Use {@link FloydWarshall#getPath(Object, Object)}
		 * to obtain the result in every case.
		 * @return
		 */
		public List<N> getPath() {
			return path == null ? null : path.get();
		}

		/**
		 * Sets the full path between nodes, computed as the last step of {@link FloydWarshall#performFloydWarshall(List)}. Such path
		 * is going to be stored using {@link SoftReference} (cached) and might be freed by GC if heap runs dry.
		 * @param path
		 */
		public void setPath(List<N> path) {
			this.path = new SoftReference<List<N>>(path);
		}

	}
	
	/**
	 * Map used for the computation of paths.
	 */
	private IPFKnownMap<NODE> map;

	/**
	 * Agent's custom view of the map.
	 */
	private IPFKnownMapView<NODE> view;

	/**
	 * Logger used by this object.
	 */
	private Logger log = null;
	
	/**
	 * Map converting nodes to their corresponding indices inside {@link FloydWarshall#pathMatrix}.
	 */
	private Map<NODE, Integer> nodesIndices;

	/**
	 * Mapping indices (inside {@link FloydWarshall#pathMatrix}) to nodes.
	 */
	private Map<Integer, NODE> indicesNodes;

	/**
	 * FloydWarshall's matrix of distances & paths.
	 */
	private PathMatrixNode<NODE>[][] pathMatrix;
	
	// ===========
	// CONSTRUCTOR
	// ===========

	/**
	 * FloydWarshall configured with "map" with no agent-specific view on the map, {@link DefaultView} is used.
	 * <p><p>
	 * {@link FloydWarshall#compute()} method is immediately called from within this constructor.
	 *  
	 * @param map
	 */
	public FloydWarshall(IPFKnownMap<NODE> map) {
		this.map = map;
		this.view = new IPFKnownMapView.DefaultView();
		NullCheck.check(this.map, "map");
		compute();
	}
	
	/**
	 * FloydWarshall configured with "map" and agent-specific view on the map, if "view" is null, {@link DefaultView} is going to be used.
	 * <p><p>
	 * {@link FloydWarshall#compute()} method is immediately called from within this constructor.
	 *  
	 * @param map
	 * @param view may be null
	 */
	public FloydWarshall(IPFKnownMap<NODE> map, IPFKnownMapView<NODE> view) {
		this.map = map;
		this.view = view;
		NullCheck.check(this.map, "map");
		if (this.view == null) {
			this.view = new IPFKnownMapView.DefaultView();
		}
		compute();
	}
	
	/**
	 * FloydWarshall configured with "map" with no agent-specific view on the map, {@link DefaultView} is used.
	 * <p><p>
	 * {@link FloydWarshall#compute()} method is immediately called from within this constructor.
	 *  
	 * @param map
	 * @param log may be null
	 */
	public FloydWarshall(IPFKnownMap<NODE> map, Logger log) {
		this.map = map;
		this.view = new IPFKnownMapView.DefaultView();
		NullCheck.check(this.map, "map");
		this.log = log;
		compute();
	}
	
	/**
	 * FloydWarshall configured with "map" and agent-specific view on the map, if "view" is null, {@link DefaultView} is going to be used.
	 * <p><p>
	 * {@link FloydWarshall#compute()} method is immediately called from within this constructor.
	 *  
	 * @param map
	 * @param view may be null
	 * @param log may be null
	 */
	public FloydWarshall(IPFKnownMap<NODE> map, IPFKnownMapView<NODE> view, Logger log) {
		this.map = map;
		this.view = view;
		NullCheck.check(this.map, "map");
		this.log = log;
		if (this.view == null) {
			if (log != null && log.isLoggable(Level.WARNING)) log.warning("No view specified! IPFKnownMapView.DefaultView is going to be used.");
			this.view = new IPFKnownMapView.DefaultView();
		}		
		compute();
	}
	
	/**
	 * Returns logger used by this object.
	 * @return
	 */
	public Logger getLog() {
		return log;
	}

	/**
	 * Sets logger used by this object.
	 * @param log
	 */
	public void setLog(Logger log) {
		this.log = log;
	}

	/**
	 * Map abstraction the FloydWarshall is working with.
	 * @return
	 */
	public IPFKnownMap<NODE> getMap() {
		return map;
	}

	/**
	 * Sets map abstraction into the FloydWarshall.
	 * @param map
	 */
	public synchronized void setMap(IPFKnownMap<NODE> map) {
		this.map = map;
	}

	/**
	 * Returns agent-specific map view for the map.
	 * @return
	 */
	public IPFKnownMapView<NODE> getMapView() {
		return view;
	}

	/**
	 * Sets agent-specific map view for the map. 
	 * @param mapView
	 */
	public synchronized void setMapView(IPFKnownMapView<NODE> mapView) {
		this.view = mapView;
	}
	
	/**
	 * Force FloydWarshall to refresh its path matrix, useful if you modify the map or view using {@link FloydWarshall#setMap(IPFKnownMap)}
	 * or {@link FloydWarshall#setMapView(IPFKnownMapView)}.
	 * <p><p>
	 * Called automatically from constructors!
	 */
	public synchronized void compute() {
		if (log != null && log.isLoggable(Level.FINE)) log.fine(this + ": Computing paths...");
		
		List<NODE> nodes = new ArrayList<NODE>();
		
		Collection<NODE> mapNodes = map.getNodes();
		if (mapNodes != null) {
			Iterator<NODE> iter = mapNodes.iterator();
			while (iter.hasNext()) {
				NODE node = iter.next();
				if (view.isNodeOpened(node)) {
					// we can use it
					nodes.add(node);
				}
			}
		}
		
		Collection<NODE> extraNodes = view.getExtraNodes(mapNodes);
		if (extraNodes != null) {
			Iterator<NODE> iter = extraNodes.iterator();
			while (iter.hasNext()) {
				NODE node = iter.next();
				if (view.isNodeOpened(node)) {
					// we can use it
					nodes.add(node);
				}
			}
		}
		
		performFloydWarshall(nodes);
		if (log != null && log.isLoggable(Level.FINE)) log.fine(this + ": Paths computed!");
	}
	
	public synchronized void cacheAllPaths() {
		int size = pathMatrix.length;
		// Construct paths + log unreachable paths.		
		if (log != null && log.isLoggable(Level.FINE)) log.fine(this + ": Caching all paths O(" + size + "^3)...");
		long time = System.currentTimeMillis();
		int count = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (pathMatrix[i][j].getPathCost() == Integer.MAX_VALUE) {
					// WE'RE PURPOSEFULLY TESTING "FINER" LEVEL HERE!
					if (log != null && log.isLoggable(Level.FINER)) log.warning("Unreachable path from " + indicesNodes.get(i) + " -> " + indicesNodes.get(j));
					count++;
				} else {
					// path exists ... retrieve it
					pathMatrix[i][j].setPath(retrievePath(i, j));
				}
			}
		}
		if (log != null && log.isLoggable(Level.FINE)) log.fine(this + ": Paths cached (" + (System.currentTimeMillis() - time) + " ms).");
	}
	
	//////////////////////////////////////
	// FloydWarshall algorithms & variable 
	//////////////////////////////////////

	
	/**
	 * Perform FloydWarshall over the list of nodes initializing {@link FloydWarshall#nodesIndices}, {@link FloydWarshall#pathMatrix}.
	 * @param nodes
	 */
	@SuppressWarnings("unchecked")
	private void performFloydWarshall(List<NODE> nodes) {
		if (log != null && log.isLoggable(Level.FINE)) log.fine(this + ": Running Floyd-Warshall algorithm...");
		long start = System.currentTimeMillis();

		// prepares data structures
		int size = nodes.size();
		nodesIndices = new HashMap<NODE, Integer>(size);
		indicesNodes = new HashMap<Integer, NODE>(size);
		pathMatrix = new PathMatrixNode[size][size];

		// Initialize navPoint indices mapping.
		for (int i = 0; i < nodes.size(); ++i) {
			nodesIndices.put(nodes.get(i), i);
			indicesNodes.put(i, nodes.get(i));
		}

		// Initialize distance matrix.
		if (log != null && log.isLoggable(Level.FINE)) log.fine(this + ": Initializing matrix...");
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				pathMatrix[i][j] = new PathMatrixNode<NODE>((i == j) ? 0 : Integer.MAX_VALUE);
			}
		}

		// Set initial arc costs into distance matrix.
		if (log != null && log.isLoggable(Level.FINE)) log.fine(this + ": Setting initial arc costs...");
		for (int i = 0; i < size; i++) {
			NODE node1 = nodes.get(i);
			if (!view.isNodeOpened(node1)) continue; // forbidden node
			
			Collection<NODE> neighbors = map.getNeighbors(node1);
			Collection<NODE> extraNeighbors = view.getExtraNeighbors(node1, neighbors);
			Iterator<NODE> iterator = 
				new Iterators<NODE>(
					neighbors == null ? null : neighbors.iterator(), 
					extraNeighbors == null ? null : extraNeighbors.iterator()
				);
			
			while(iterator.hasNext()) {
				NODE node2 = iterator.next();
				if (!view.isNodeOpened(node2)) continue;       // forbidden node
				if (!view.isArcOpened(node1, node2)) continue; // forbidden arc
				int j = nodesIndices.get(node2);
				
				int arcCost = map.getArcCost(node1, node2);
				arcCost += view.getArcExtraCost(node1, node2, arcCost);
				int nodeCost = map.getNodeCost(node2);
				nodeCost += view.getNodeExtraCost(node2, nodeCost);
				
				pathMatrix[i][j].setPathCost(arcCost+nodeCost);
			}
		}

		// Perform Floyd-Warshall.
		if (log != null && log.isLoggable(Level.FINE)) log.fine(this + ": Computing path-matrix O(" + size + "^3)...");
		for (int k = 0; k < size; k++) {
			for (int i = 0; i < size; i++) {
				NODE node1 = nodes.get(i);
				NODE node2 = nodes.get(k);
				for (int j = 0; j < size; j++) {
					NODE node3 = nodes.get(j);
					int newLen =
						pathMatrix[i][k].getPathCost() == Integer.MAX_VALUE ?
								Integer.MAX_VALUE
							:	(pathMatrix[k][j].getPathCost() == Integer.MAX_VALUE) ? 
									Integer.MAX_VALUE
								:	pathMatrix[i][k].getPathCost() + pathMatrix[k][j].getPathCost();
					if (pathMatrix[i][j].getPathCost() > newLen) {
						pathMatrix[i][j].setPathCost(newLen);
						pathMatrix[i][j].setViaNode(k);
					}
				}
			}
		}

		// Check reachability...
		if (log != null && log.isLoggable(Level.FINE)) log.fine(this + ": Checking reachability...");
		int count = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (pathMatrix[i][j].getPathCost() == Integer.MAX_VALUE) {
					// WE'RE PURPOSEFULLY TESTING "FINER" LEVEL HERE!
					if (log != null && log.isLoggable(Level.FINER)) log.warning("Unreachable path from " + nodes.get(i) + " -> " + nodes.get(j));
					count++;
				}
			}
		}

		if (count > 0) {
			if (log != null && log.isLoggable(Level.WARNING)) log.warning(this + ": There are " + count + " unreachable nodes pairs (if you wish to see their list, set logging to Level.FINER).");
		} else {
			if (log != null && log.isLoggable(Level.INFO)) log.info(this + ": All nodes are connected, there are NO unreachable pairs of nodes.");
		}

		if (log != null && log.isLoggable(Level.INFO)) log.info(this + ": Computation for " + size + " navpoints took " + (System.currentTimeMillis() - start) + " millis.");
		
		if (log != null && log.isLoggable(Level.INFO)) {		
			log.info(this + ": Memory consumption (no paths cached) is aprox. " + getAproxMemory() + " bytes, might be as twice as much for 64-bit system.");
		}
				
		if (log != null && log.isLoggable(Level.FINE)) log.fine(this + ": Floyd-Warshall algorithm finished!");
	}

	/**
	 * Returns approximation of memory consumption of this object in bytes for 32-bit JVM, might be as twice for 64-bit JVM!
	 * @return
	 */
	public long getAproxMemory() {
		long bytes = 0;
		for (int i = 0; i < pathMatrix.length; ++i) {
			for (int j = 0; j < pathMatrix.length; ++j) {
				bytes += pathMatrix[i][j].getBytesAprox();
			}
		}
		bytes += pathMatrix.length * 8 * 2; 
		return bytes;
	}

	/**
	 * Sub-routine of {@link FloydWarshall#retrievePath(Integer, Integer)} - do not use! ... Well you may, it returns
	 * path without 'from', 'to' or null if such path dosn't exist.
	 * <p><p>
	 * DO NOT USE OUTSIDE {@link FloydWarshall#performFloydWarshall(List)} (relies on indicesNavPoints).
	 * <p><p>
	 * Uses recursion.
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	private List<NODE> retrievePathInner(Integer from, Integer to) {
		PathMatrixNode node = pathMatrix[from][to];
		if (node.getPathCost() == Integer.MAX_VALUE)
			return null;
		if (node.getViaNode() == null) {
			return new ArrayList<NODE>(0);
		}
		if (node.getViaNode() == null)
			return new ArrayList<NODE>(0);

		List<NODE> path = new ArrayList<NODE>();
		path.addAll(retrievePathInner(from, node.getViaNode()));
		path.add(indicesNodes.get(node.getViaNode()));
		path.addAll(retrievePathInner(node.getViaNode(), to));

		return path;
	}

	/**
	 * Returns path between from-to or null if path doesn't exist. Path begins
	 * with 'from' and ends with 'to'.
	 * <p><p>
	 * DO NOT USE OUTSIDE CONSTRUCTOR (relies on indicesNavPoints).
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	private List<NODE> retrievePath(Integer from, Integer to) {
		List<NODE> path = new ArrayList<NODE>();
		path.add(indicesNodes.get(from));
		path.addAll(retrievePathInner(from, to));
		path.add(indicesNodes.get(to));
		return path;
	}

	/**
	 * Returns {@link PathMatrixNode<NODE>} describing path from "nodeFrom" to "nodeTo". 
	 * @param nodeFrom
	 * @param nodeTo
	 * @return
	 */
	public PathMatrixNode<NODE> getPathMatrixNode(NODE nodeFrom, NODE nodeTo) {
		Integer from = nodesIndices.get(nodeFrom);
		Integer to = nodesIndices.get(nodeTo);
		if (from == null || to == null) return null;
		return pathMatrix[from][to];
	}

	/**
	 * Whether node 'to' is reachable (path exists) from the node 'from'.
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public boolean isReachable(NODE from, NODE to) {
		if ((from == null) || (to == null)) return false;
		PathMatrixNode matrixNode = getPathMatrixNode(from, to);
		if (matrixNode == null) return false;
		return matrixNode.getPathCost() != Integer.MAX_VALUE;
	}

	/**
	 * Calculate's distance between two nav points (using pathfinding).
	 * <p><p>
	 * Throws exception if object is disabled, see {@link FloydWarshallMap#setEnabled(boolean)}. Note that the object
	 * is enabled by default.
	 * 
	 * @return Distance or {@link Integer#MAX_VALUE} if there's no path.
	 */
	public int getPathCost(NODE from, NODE to) {
		if ((from == null) || (to == null))
			return Integer.MAX_VALUE;
		PathMatrixNode matrixNode = getPathMatrixNode(from, to);
		if (matrixNode == null) return Integer.MAX_VALUE;
		return matrixNode.getPathCost();
	}

	/**
	 * Returns path between navpoints 'from' -> 'to'. The path begins with
	 * 'from' and ends with 'to'. If such path doesn't exist, returns null.
	 * <p><p>
	 * Path is automatically cached using {@link SoftReference}.
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public List<NODE> getPath(NODE from, NODE to) {
		if ((from == null) || (to == null))
			return null;
		if (log != null && log.isLoggable(Level.FINE)) log.fine("Retrieving path: " + from + " -> " + to);
		PathMatrixNode matrixNode = getPathMatrixNode(from, to);
		if (matrixNode == null) return null;
		if (matrixNode.getPathCost() == Integer.MAX_VALUE) return null;
		List<NODE> path = matrixNode.getPath();
		if (path == null) {
			// was not cached or JVM has GC()ed it
			path = retrievePathInner(nodesIndices.get(from), nodesIndices.get(to));
			// cache the path
			matrixNode.setPath(path);
		}
		if (log != null && log.isLoggable(Level.FINE)) log.fine("Path " + from + " -> " + to + " exists, " + path.size() + " nodes long.");
		return path;
	}
	
	/**
	 * Returns matrix of nodes as computed by FloydWarshall algorithm. You should not alter it by hand!
	 * @return
	 */
	public PathMatrixNode<NODE>[][] getMatrix() {
		return pathMatrix;
	}
	
	/**
	 * Returns index of the node inside {@link FloydWarshall#getMatrix()}. Note that if node that is not inside the matrix is passed,
	 * this will return null! 
	 * @param node
	 * @return
	 */
	public Integer getNodeIndex(NODE node) {
		return nodesIndices.get(node);
	}

	@Override
	public String toString() {
		return "FloydWarshall";
	}

}
