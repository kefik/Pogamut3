package cz.cuni.amis.utils.iterators;

import java.util.Iterator;

/**
 * Implements a circular iterator that iterates over any reasonable iterable instance.
 * Not fully tested!
 * @author Radek 'Black_Hand' Pibil
 *
 * @param <E> contents of the iterable
 */
public class CircularIterator<E> implements Iterator<E> {
	
	protected boolean passedEnd = false;
	private Iterable<E> toIterateOver;
	private Iterator<E> iterator;
	
	public CircularIterator(Iterable<E> toIterateOver) {
		this.toIterateOver = toIterateOver;
		iterator = toIterateOver.iterator();
	}

	@Override
	public boolean hasNext() {
		return toIterateOver.iterator().hasNext();
	}

	@Override
	public E next() {
		E value = iterator.next();
		if (!iterator.hasNext()) {
			restartIterator();
			passedEnd = true;
		}
		return value;
	}

	@Override
	public void remove() {
		iterator.remove();
	}
	
	public boolean hasPassedEnd() {
		if (passedEnd) {
			passedEnd = false;
			return passedEnd;
		}
		return false;
	}
	
	protected Iterable<E> getIterable() {
		return toIterateOver;
	}
	
	protected Iterator<E> getIterator() {
		return iterator;
	}
	
	protected void restartIterator() {
		iterator = toIterateOver.iterator();	
	}
}
