package cz.cuni.amis.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * This class allows you to combine several iterators in the single one allowing you to seamlessly iterate over several
 * collections at once.
 * <p><p>
 * This class behaves as defined by {@link Iterator} contract.
 * 
 * @author Jimmy
 *
 * @param <NODE>
 */
public class Iterators<NODE> implements Iterator<NODE>, Iterable<NODE> {
	
	/**
	 * Array of iterators that are going to be iterated over. 
	 */
	private Iterator<NODE>[] iterators;
	
	/**
	 * Current index of {@link Iterators#iterator} inside {@link Iterators#iterators}. If this index is >= iterators.length then
	 * there are no more iterators and current iterator is not valid.
	 */
	private int currentIteratorIndex;
	
	/**
	 * Previous iterator from {@link Iterators#iterators}, this is stored because of {@link Iterators#remove()} operation that
	 * is a bit complicated by the fact that one may call {@link Iterators#hasNext()} which advances {@link Iterators#currentIteratorIndex}
	 * but remove() must still remove previously next()ed node.
	 */
	private Iterator<NODE> previousIterator;
	
	/**
	 * Current iterator to be used for {@link Iterators#hasNext()} and {@link Iterators#next()} operations.
	 */
	private Iterator<NODE> iterator;
	
	/**
	 * Whether {@link Iterators#currentIteratorIndex} has been recently advanced and {@link Iterators#next()} has not yet been called.
	 * This means if this is true {@link Iterators#remove()} must use {@link Iterators#previousIterator} instead of {@link Iterators#iterator}.
	 */
	private boolean switched;
	
	/**
	 * Whether the last NODE returned by {@link Iterators#next()} was already removed or not.
	 */
	private boolean removed;
	
	/**
	 * Whether the {@link Iterators#next()} has ever been called.
	 */
	private boolean next;
	
	/**
	 * Initialize this class to use "iterators" in the order as they are passed into the constructor.
	 * @param iterators may contain nulls
	 */
	public Iterators(Iterator<NODE>... iterators) {
		init(iterators);		
	}
	
	/**
	 * Initialize this class to use "iterators" from 'iterables' in the order as they are passed into the constructor.
	 * @param iterators may contain nulls
	 */
	public Iterators(Iterable<NODE>... iterables) {
		Iterator<NODE>[] iterators = new Iterator[iterables == null ? 0 : iterables.length];
		for (int i = 0; i < iterables.length; ++i) {
			iterators[i] = (iterables[i] == null ? null : iterables[i].iterator());
		}
		init(iterators);
	}
	
	/**
	 * To be called from CONSTRUCTORs only!
	 * @param iterators
	 */
	private void init(Iterator<NODE>... iterators) {
		if (iterators == null || iterators.length == 0) {
			currentIteratorIndex = 1;
			iterator = null;
		} else {
			this.iterators = iterators;
			currentIteratorIndex = 0;
			previousIterator = null;
			iterator = iterators[0];
			switched = false;
			removed = false;
			next = false;
		}
	}
	
	/**
	 * Advances {@link Iterators#currentIteratorIndex} and {@link Iterators#iterator}.
	 * @return
	 */
	private boolean nextIterator() {		
		if (iterators == null || currentIteratorIndex >= iterators.length) return false;
		previousIterator = iterator;
		switched = true;
		while (true) {
			++currentIteratorIndex;
			if (currentIteratorIndex >= iterators.length) {
				return false;
			}			
			iterator = iterators[currentIteratorIndex];
			if (iterator == null) continue;
			if (iterator.hasNext()) {
				return true;
			}
		}
	}
	
	@Override
	public boolean hasNext() {
		return (iterator != null && iterator.hasNext()) || nextIterator();
	}

	@Override
	public NODE next() {
		if (iterator == null) throw new NoSuchElementException("Last iterator fully used.");
		if (iterator.hasNext() || nextIterator()) {
			next = true;
			switched = false;
			removed = false;
			return iterator.next();
		}
		throw new NoSuchElementException("Last iterator fully used.");
	}

	@Override
	public void remove() {
		if (!next) throw new IllegalStateException("next() method has never been successfully called, no element to remove!");
		if (removed) throw new IllegalStateException("remove() was called twice for the same element, unsupported!");
		if (switched) {
			previousIterator.remove();
		} else {
			iterator.remove();
		}
		removed = true;
	}

	@Override
	public Iterator<NODE> iterator() {
		return this;
	}

}
