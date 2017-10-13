package cz.cuni.amis.pathfinding.alg.astar;

import java.util.Comparator;
import java.util.Map;

/**
 * This comparator is a tricky object - it serves for the {@link AStarHeap} to compare nodes inside the heap.
 * <p><p>
 * The trick is, that it is initialized {@link AStarHeapComparator#AStarHeapComparator(Map)} with a map
 * that contains node's costs that are used during the compare inside {@link AStarHeapComparator#compare(Object, Object)}.
 * No magic yet, ha? Well, the magic is that this map is not cloned... simply a pointer to this very instance passed
 * inside the constructor is saved to you may alter the cost as you wish to! Which is truly needed by the {@link AStar} class
 * that is obtaining nodes' costs from the {@link AStarMap} implementor.
 * 
 * @author Jimmy
 *
 * @param <NODE>
 */
public class AStarHeapComparator<NODE> implements Comparator {
	
	private Map<NODE, Integer> values;
	
	public AStarHeapComparator(Map<NODE, Integer> estimatedCosts){
		values = estimatedCosts;
	}

	public int compare(Object arg0, Object arg1) {
		return values.get(arg0).intValue() - values.get(arg1).intValue();
	}

}
