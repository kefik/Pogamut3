package cz.cuni.amis.pathfinding.map;

import java.util.Collection;

/**
 * This interface provides additional information about the map algorithms are going to work with. In its implementation you may provide custom
 * view on the map as needed for your search (for instance you need to forbid some nodes / edges or change the general cost of nodes / edges
 * in order to change how your agent is going to plan the path).
 * <p><p>
 * Generally, you will use {@link IPFKnownMap} interface to define the map in general and then use this {@link IPFKnownMapView} interface
 * to specify a specific needs you need to impose over the map as is "forbidding" some nodes or "imposing additional costs 
 * onto the nodes".
 * <p><p>
 * See also {@link IPFMapView}
 *   
 * @author Jimmy
 */
public interface IPFKnownMapView<NODE> extends IPFMapView<NODE> {
	
	/**
	 * This method may return new nodes which are not present in standard 'map' (as returned by {@link IPFKnownMap#getNodes()}).
	 * Such nodes are then exclusively accessible to your particular agent, that is, this methods is adding nodes that can be accessed 
	 * by the agent but are not part of your general map description.
	 * <p><p>
	 * Returned collection must not contain multiple references to a single node.
	 * <p><p>
	 * Returned collection must not contain any node from "mapNodes".
	 * 
	 * @param mapNodes "nodes" of map as returned by {@link IPFKnownMap#getNodes()}, may return null
	 */
	public Collection<NODE> getExtraNodes(Collection<NODE> mapNodes);

	/**
	 * Default view does not impose any specific view on the map... all nodes/arcs are opened, no extra cost/nodes/arcs defined.
	 * @author Jimmy
	 */
	public class DefaultView<NODE> extends IPFMapView.DefaultView<NODE> implements IPFKnownMapView<NODE> {

		@Override
		public Collection<NODE> getExtraNodes(Collection<NODE> mapNodes) {
			return null;
		}

		
	}
	
}
