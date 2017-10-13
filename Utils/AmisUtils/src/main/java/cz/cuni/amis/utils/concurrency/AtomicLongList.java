package cz.cuni.amis.utils.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLongArray;

public class AtomicLongList {

	private List<AtomicLongArray> arrays = new ArrayList<AtomicLongArray>();
	
	private int capacityStep;
	
	private int size = 0;

	public AtomicLongList(int initialCapacity, int capacityStep) {
		this.capacityStep = capacityStep;
		if (initialCapacity > 0) {
			getArray(initialCapacity-1);
		}
	}

	private int arrayIndex(int index) {
		return index / capacityStep;
	}
	
	private int trueIndex(int index) {
		return index - capacityStep * (index / capacityStep);
	}
	
	private AtomicLongArray getArray(int index) {
		int arrayIndex = arrayIndex(index);
		if (size <= index) {
			synchronized(arrays) {
				if (size <= index) {
					size = index + 1;
				}
			}
		}
		if (arrayIndex < arrays.size()) return arrays.get(arrayIndex);
		synchronized(arrays) {
			while (arrays.size() <= arrayIndex) arrays.add(new AtomicLongArray(capacityStep));			
		}
		return arrays.get(arrayIndex);
	}
	
	/**
	 * Atomically add the given value to element at index i.
	 * 
	 * @param i
	 * @param delta
	 * @return
	 */
	public long addAndGet(int i, long delta) {
		return getArray(i).addAndGet(trueIndex(i), delta);
	}

	/**
	 * Atomically set the value to the given updated value if the current value
	 * == the expected value.
	 * 
	 * @param i
	 * @param expect
	 * @param update
	 * @return
	 */
	public boolean compareAndSet(int i, long expect, long update) {
		return getArray(i).compareAndSet(trueIndex(i), expect, update);
	}

	/**
	 * Atomically decrement by one the element at index i.
	 * 
	 * @param i
	 * @return
	 */
	public long decrementAndGet(int i) {
		return getArray(i).decrementAndGet(trueIndex(i));
	}

	/**
	 * Get the current value at position i.
	 * 
	 * @param i
	 * @return
	 */
	public long get(int i) {
		return getArray(i).get(trueIndex(i));
	}

	/**
	 * Atomically add the given value to element at index i.
	 * 
	 * @param i
	 * @param delta
	 * @return
	 */
	public long getAndAdd(int i, long delta) {
		return getArray(i).getAndAdd(trueIndex(i), delta);
	}

	/**
	 * Atomically decrement by one the element at index i.
	 * 
	 * @param i
	 * @return
	 */
	public long getAndDecrement(int i) {
		return getArray(i).getAndDecrement(trueIndex(i));
	}

	/**
	 * Atomically increment by one the element at index i.
	 * 
	 * @param i
	 * @return
	 */
	public long getAndIncrement(int i) {
		return getArray(i).getAndIncrement(trueIndex(i));
	}

	/**
	 * Set the element at position i to the given value and return the old
	 * value.
	 * 
	 * @param i
	 * @param newValue
	 * @return
	 */
	public long getAndSet(int i, long newValue) {
		return getArray(i).getAndSet(trueIndex(i), newValue);
	}

	/**
	 * Atomically increment by one the element at index i.
	 * 
	 * @param i
	 * @return
	 */
	public long incrementAndGet(int i) {
		return getArray(i).incrementAndGet(trueIndex(i));
	}

	/**
	 * Returns the length of the array (== max-touched-index+1).
	 * 
	 * @return
	 */
	public int size() {
		return size;
	}
	
	/**
	 * Returns current capacity of the array.
	 * @return
	 */
	public int capacity() {
		return arrays.size() * capacityStep;
	}

	/**
	 * Set the element at position i to the given value.
	 * 
	 * @param i
	 * @param newValue
	 */
	public void set(int i, long newValue) {
		getArray(i).set(trueIndex(i), newValue);
	}

	/**
	 * Atomically set the value to the given updated value if the current value
	 * == the expected value.
	 * 
	 * @param i
	 * @param expect
	 * @param update
	 * @return
	 */
	public boolean weakCompareAndSet(int i, long expect, long update) {
		return getArray(i).weakCompareAndSet(trueIndex(i), expect, update);
	}
}
