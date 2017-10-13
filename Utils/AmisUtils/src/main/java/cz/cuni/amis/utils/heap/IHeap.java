package cz.cuni.amis.utils.heap;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

/**
 * Interface for standard Heap (with addition of decreace/increase/changedKey operations).
 * <p><p>
 * We also assume that heap will use {@link Comparator} for comparing stored nodes.
 * 
 * @author Jimmy
 *
 * @param <NODE>
 */
public interface IHeap<NODE> extends Collection<NODE> {
	
	/**
	 * Returns comparator that is used to compare the nodes in the heap.
	 * @return
	 */
	public Comparator<NODE> getComparator();
	
	/**
	 * Returns node with min-value from the heap.
	 * @return
	 */
	public NODE getMin();
	
	/**
	 * Deletes node with min-value, returns success (true if there was some object in the heap, false if there weren't).
	 * @return success
	 */
	public boolean deleteMin();
	
	/**
	 * "node" value has been decreased, bubble it through the heap.
	 * @param node
	 * @return
	 */
	public boolean decreaseKey(NODE node);
	
	/**
	 * "node" value has been increased, bubble it through the heap.
	 * @param node
	 * @return
	 */
	public boolean increaseKey(NODE node);
	
	/**
	 * "node" value has been changed (not sure if it was increased or decreased), bubble it through the heap.
	 * @param node
	 * @return
	 */
	public boolean changedKey(NODE node);
	
	/**
	 * Adds all items from 'items'.
	 * @param items
	 * @return
	 */
	public boolean addAll(NODE[] items);
	
	/**
	 * Whether this heap contains all 'items'.
	 * @param items
	 * @return
	 */
	public boolean containsAll(Object[] items);
	
	/**
	 * Whether this heap is empty.
	 * @return
	 */
	public boolean empty();
	
	/**
	 * Returns this heap as a set.
	 * @return
	 */
	public Set<NODE> toSet();	

}
