package cz.cuni.amis.utils.heap;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

import cz.cuni.amis.utils.NullCheck;

/**
 * Unmodifiable decorator the {@link IHeap} objects.
 * 
 * @author Jimmy
 *
 * @param <NODE>
 */
public class ImmutableHeap<NODE> implements IHeap<NODE> {

	private IHeap<NODE> heap;
	
	public ImmutableHeap(IHeap<NODE> heap) {
		this.heap = heap;
		NullCheck.check(this.heap, "heap");
	}
	
	@Override
	public boolean deleteMin() {
		throw new UnsupportedOperationException("Immutable heap!");
	}
	
	@Override
	public boolean decreaseKey(NODE arg0) {
		throw new UnsupportedOperationException("Immutable heap!");
	}
	
	@Override
	public boolean add(NODE arg0) {
		throw new UnsupportedOperationException("Immutable heap!");
	} 
	
	@Override
	public boolean addAll(Collection arg0) {
		throw new UnsupportedOperationException("Immutable heap!");
	}
	
	@Override
	public boolean addAll(NODE[] arg0) {
		throw new UnsupportedOperationException("Immutable heap!");
	}
	
	@Override
	public void clear() {
		throw new UnsupportedOperationException("Immutable heap!");
	}

	@Override
	public boolean changedKey(NODE node) {
		throw new UnsupportedOperationException("Immutable heap!");		
	}

	@Override
	public boolean containsAll(Object[] items) {
		return heap.containsAll(items);
	}

	@Override
	public boolean empty() {
		return heap.empty();
	}

	@Override
	public Comparator<NODE> getComparator() {
		return heap.getComparator();
	}

	@Override
	public NODE getMin() {
		return heap.getMin();
	}

	@Override
	public boolean increaseKey(NODE node) {
		throw new UnsupportedOperationException("Immutable heap!");
	}

	@Override
	public Set<NODE> toSet() {
		return heap.toSet();
	}

	@Override
	public boolean contains(Object o) {
		return heap.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return heap.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		return heap.isEmpty();
	}

	@Override
	public Iterator<NODE> iterator() {
		return new HeapImmutableIterator<NODE>(heap.iterator());
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException("Immutable heap!");
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("Immutable heap!");
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("Immutable heap!");
	}

	@Override
	public int size() {
		return heap.size();
	}

	@Override
	public Object[] toArray() {
		return heap.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return heap.toArray(a);
	}
	
}
