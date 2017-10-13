package cz.cuni.amis.utils.heap;

import java.util.Iterator;

import cz.cuni.amis.utils.NullCheck;

public class HeapImmutableIterator<NODE> implements Iterator<NODE> {

	private Iterator<NODE> iter;

	public HeapImmutableIterator(Iterator<NODE> iter) {
		this.iter = iter;
		NullCheck.check(this.iter, "iter");
	}
	
	@Override
	public boolean hasNext() {
		return iter.hasNext();
	}

	@Override
	public NODE next() {
		return iter.next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("ImmutableHeapIterator, can't remove!");
	}

}
