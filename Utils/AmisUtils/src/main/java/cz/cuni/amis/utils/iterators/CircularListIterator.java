package cz.cuni.amis.utils.iterators;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Implements a circular iterator that iterates over any List instance instance.
 * Useful for obtaining bidirectional ListIterator<E>.
 * Not fully tested!
 * @author Radek 'Black_Hand' Pibil
 *
 * @param <E> contents of the iterable
 */
public class CircularListIterator<E> implements ListIterator<E> {

	protected boolean passedEnd = false;
	protected boolean passedBeginning = false;
	
	protected boolean moved = false;

	private int index = 0;
	private List<E> toIterateOver;
	private ListIterator<E> iterator;

	/**
	 * Constructs an instance of an CircularListIterator.
	 * @param toIterateOver List to be iterated over
	 */	
	public CircularListIterator(List<E> toIterateOver) {
		if (toIterateOver == null)
			throw new NullPointerException("List<E> to be iterated over cannot be null.");
			
		this.toIterateOver = toIterateOver;
		iterator = toIterateOver.listIterator();
	}

	/**
	 * Constructs an instance of an CircularListIterator.
	 * @param toIterateOver List to be iterated over
	 * @param index an index to begin iteration at
	 */
	public CircularListIterator(List<E> toIterateOver, int index) {
		if (toIterateOver == null)
			throw new IllegalArgumentException("List<E> to be iterated over cannot be null.");
		
		if (index < 0 && index > toIterateOver.size())
			throw new IllegalArgumentException("Index parameter cannot be outside toIterateOver parameter.");
		
		this.toIterateOver = toIterateOver;
		iterator = toIterateOver.listIterator(index);
		this.index = index;
	}

	/**
	 * Copy constructor for CircularListIterator.
	 * @param source
	 */	
	public CircularListIterator(CircularListIterator<E> source) {
		this.passedEnd = source.passedEnd;
		this.passedBeginning = source.passedBeginning;
		this.index = source.index;
		this.toIterateOver = source.toIterateOver;
		
		int currentIndex = source.currentIndex();
			
		this.iterator = toIterateOver.listIterator(
				(currentIndex == -1 ? toIterateOver.size() - 1 : currentIndex));
	}

	@Override
	public boolean hasNext() {
		return toIterateOver.size() > 0;
	}

	@Override
	public E next() {
		
		if (currentIndex() == toIterateOver.size()) {
			restartIteratorBeginning();
		}

		E value = iterator.next();

		if (currentIndex() == index && moved)
			passedEnd = true;
		
		moved = true;

		return value;
	}

	@Override
	public void remove() {
		
		iterator.remove();

		if (currentIndex() == index)
			passedEnd = true;

		if (currentIndex() == toIterateOver.size()) {
			restartIteratorBeginning();
		}

	}

	public boolean hasPassedEnd() {
		if (passedEnd) {
			passedEnd = false;
			return passedEnd;
		}
		return false;
	}

	public boolean hasPassedBeginning() {
		if (passedBeginning) {
			passedBeginning = false;
			return passedBeginning;
		}
		return false;
	}

	protected Iterable<E> getIterable() {
		return toIterateOver;
	}

	protected Iterator<E> getIterator() {
		return iterator;
	}

	protected void restartIteratorBeginning() {
		iterator = toIterateOver.listIterator();
	}

	protected void restartIteratorEnd() {
		iterator = toIterateOver.listIterator(toIterateOver.size());
	}

	@Override
	public void add(E e) {
		iterator.add(e);
	}

	@Override
	public boolean hasPrevious() {
		return hasNext();
	}

	@Override
	public int nextIndex() {
		return (iterator.nextIndex() % toIterateOver.size());
	}

	@Override
	public E previous() {
		
		if (currentIndex() == 0) {
			restartIteratorEnd();
		}
		
		E value = iterator.previous();

		if (currentIndex() == index && moved)
			passedBeginning = true;
		
		moved = true;

		return value;
	}

	@Override
	public int previousIndex() {
		int previous = iterator.previousIndex();
		return (previous == -1 ? toIterateOver.size() - 1 : previous);
	}

	@Override
	public void set(E e) {
		iterator.set(e);
	}
	
	public int currentIndex() {
		int next = nextIndex() - 1;
		
		return (next == toIterateOver.size() ? 0 : (nextIndex() - 1));
	}
	
	public CircularListIterator<E> previousIter() {
		this.previous();
		return this;
	}
	
	public CircularListIterator<E> nextIter() {
		this.next();
		return this;
	}
}
