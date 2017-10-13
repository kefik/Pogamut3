package cz.cuni.amis.utils.astar;

import java.util.Iterator;

/**
 * Clasical iterator for AStarHeap.
 * 
 * <p><p>
 * Use amis-path-finding library instead, see svn://artemis.ms.mff.cuni.cz/pogamut/trunk/project/Utils/AmisPathFinding
 */
@Deprecated
public class AStarHeapIterator<NODE> implements Iterator {
	
	private NODE[] nodes;
	private int items;
	private int current;
	private AStarHeap heap;
	
	public AStarHeapIterator(NODE[] myNodes, int myItems, AStarHeap<NODE> myHeap){
		nodes = myNodes;
		items = myItems;
		current = 0;
		heap = myHeap;
	}

	public boolean hasNext() {
		return (current < items);		
	}

	public Object next() {
		if (current < items){
			return nodes[current++];
		} else {
			return null;
		}		
	}

	public void remove() {
		if (current == 0) 
			return;
		heap.remove(nodes[current-1]);
		current = current - 1;
		items = items - 1;
	}

}
