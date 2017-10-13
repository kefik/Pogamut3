package cz.cuni.amis.utils.heap;

import java.util.Iterator;

/**
 * {@link Iterator} used by {@link Heap} inside {@link Heap#iterator()}.
 */
public class HeapIterator<NODE> implements Iterator<NODE> {
	
	private NODE[] nodes;
	private int items;
	private int current;
	private Heap heap;
	
	public HeapIterator(NODE[] myNodes, int myItems, Heap<NODE> myHeap){
		nodes = myNodes;
		items = myItems;
		current = 0;
		heap = myHeap;
	}

	@Override
	public boolean hasNext() {
		return (current < items);		
	}

	@Override
	public NODE next() {
		if (current < items){
			return nodes[current++];
		} else {
			return null;
		}		
	}

	@Override
	public void remove() {
		if (current == 0) 
			return;
		heap.remove(nodes[current-1]);
		current = current - 1;
		items = items - 1;
	}

}
