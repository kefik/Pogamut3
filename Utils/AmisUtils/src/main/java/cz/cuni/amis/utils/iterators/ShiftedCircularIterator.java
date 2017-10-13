package cz.cuni.amis.utils.iterators;

/**
 * Circular iterator that can start from any given position in given iterable.
 * Even less tested then {@link CircularIterator} or {@link CircularListIterator}.
 * @author Radek 'Black_Hand' Pibil
 *
 * @param <E>
 */
public class ShiftedCircularIterator<E> extends CircularIterator<E> {
	
	private int steps;
	private int passedSteps = 0;

	public ShiftedCircularIterator(Iterable<E> toIterateOver, int steps) {
		super(toIterateOver);
		
		for (int i = 0; i < steps; ++i)
			next();
		
		this.steps = steps;
		passedSteps = 0;
	}
	
	@Override
	public boolean hasNext() {
		return getIterable().iterator().hasNext();
	}

	@Override
	public E next() {
		
		E value = getIterator().next();
		++passedSteps;
		
		if (!getIterator().hasNext()) {
			restartIterator();
			passedSteps = 0;
		}
		
		if (passedSteps == steps)
			passedEnd = true;
		
		return value;
	}

	@Override
	public void remove() {
		getIterator().remove();
	}
	
	public boolean hasPassedEnd() {
		if (passedEnd) {
			passedEnd = false;
			return passedEnd;
		}
		return false;
	}
}
