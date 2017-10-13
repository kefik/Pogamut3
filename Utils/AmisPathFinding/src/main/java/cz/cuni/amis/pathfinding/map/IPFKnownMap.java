package cz.cuni.amis.pathfinding.map;

import java.util.Collection;

/**
 * This class represents the discrete search space for path-finding algorithms for games. It conceptualize the map/location/environment of the game for
 * the purpose of planners as finite graph whose nodes are easily distinguishable from each others (it is suitable for NavigationGraphs using discrete navigation
 * points, but it is not suitable for GOAP planners for strategic games such as Defcon).
 * <p><p>
 * You should first read {@link IPFMap} javadoc, then by looking at new interface methods, you can see that this interface is suitable for algorithms which
 * need to know the whole graph in advance (such as Floyd-Warshall).
 * <p><p>
 * So you have to provide implementation for methods that returns all the nodes which are present in the map ({@link IPFKnownMap#getNodes()}).
 * 
 * @param NODE
 */
public interface IPFKnownMap<NODE> extends IPFMap<NODE> {
	
	/**
	 * This must return the list of ALL NODES that are present in your map (== environment).  
	 */
	public Collection<NODE> getNodes();
	
}
