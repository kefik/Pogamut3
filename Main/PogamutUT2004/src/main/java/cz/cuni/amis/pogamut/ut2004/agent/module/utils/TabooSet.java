package cz.cuni.amis.pogamut.ut2004.agent.module.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BeginMessage;
import cz.cuni.amis.utils.IFilter;

/**
 * This class is a simple implementation of TabooSet (similar to TabooList). It allows you to insert
 * items that are taboo either for infinite time (via {@link TabooSet#add(Object)}) or for a specified
 * amount of UT2004 time (via {@link TabooSet#add(Object, double)}).
 * <p><p>
 * Items inside taboo set are either removed automatically due to a timeout (a specified amount of time has passed)
 * or manually via {@link TabooSet#remove(Object)}.
 */
public class TabooSet<T> implements IFilter<T>, Collection<T> {
	
	public static interface IRelaxedTaboo<T> {
		
		public boolean isTaboo(T item, double remainingTabooTime);
		
	}
	
	/**
	 * Map of tabu items together with their time until which they are valid (negative time == valid for infinity).
	 */
	private Map<T, Double> taboo = new HashMap<T, Double>();	
		
	/**
	 * If not tabooized forever, it returns remaining time for the item to remain taboo.
	 * <p><p>
	 * If tabooized forever, returns {@link Double#POSITIVE_INFINITY}.
	 * <p><p>
	 * If item is not tabooized, returns 0.
	 * 
	 * @param item
	 * @return
	 */
	public double getTabooTime(T item) {
		Double tabooTime = taboo.get(item);
		if (tabooTime == null) return 0;
		if (tabooTime < 0) return Double.POSITIVE_INFINITY;
		if (tabooTime < time) {
			taboo.remove(item);
			return 0;
		} else {
			return tabooTime - time;
		}
	}
	
	/**
	 * Determines whether an 'item' is inside taboo set or not based on the current
	 * UT2004 time.
	 * @param item
	 * @return
	 */
	public boolean isTaboo(T item) {
		if (taboo.containsKey(item)) {
			double tabooTime = taboo.get(item);
			if (tabooTime < 0) {
				return true;
			}
			if (tabooTime < time) {
				taboo.remove(item);
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * Determines whether an 'item' will still be taboo "afterTime" passes by.
	 * @param item
	 * @param afterTime
	 * @return
	 */
	public boolean willBeTaboo(T item, double afterTime) {
		if (!isTaboo(item)) return false;
		if (taboo.containsKey(item)) {
			double tabooTime = taboo.get(item);
			return tabooTime > time + afterTime;
		} else {
			return false;
		}
	}
	
	/**
	 * Determines whether an 'item' is considered to be taboo using relaxed 'estimator'.
	 * @param item
	 * @param estimator
	 * @return
	 */
	public boolean isTaboo(T item, IRelaxedTaboo estimator) {
		if (!isTaboo(item)) {
			return false;
		} else {
			return estimator.isTaboo(item, getTabooTime(item));
		}
	}
	
	/**
	 * Method that is used by {@link DistanceUtils} methods to filter items.
	 * @param item
	 * @return returns !{@link TabooSet#isTaboo(Object)}
	 */
	@Override
	public boolean isAccepted(T item) {
		return !isTaboo(item);
	}
	
	
	/**
	 * Filters collection according to the current state of the tabu set. It returns a new hash set
	 * containing items from 'collection' that are not inside tabu set.
	 * 
	 * @param collection
	 * @return
	 */
	public Set<T> filter(Collection<T> collection) {
		Set<T> set = new HashSet<T>();
		for (T t : collection) {
			if (isTaboo(t)) continue;
			set.add(t);
		}
		return set;
	}
	
	/**
	 * Filters collection according to the current state of the tabu set. It returns a new hash set
	 * containing items from 'collection' that are not inside tabu set.
	 * 
	 * @param collection
	 * @return
	 */
	public Set<T> filter(Collection<T> collection, IRelaxedTaboo estimator) {
		Set<T> set = new HashSet<T>();
		for (T t : collection) {
			if (!isTaboo(t)) {
				set.add(t);
			} else {
				if (!estimator.isTaboo(t, getTabooTime(t))) {
						set.add(t);
				}
			}
		}
		return set;
	}	
	
	/**
	 * Returns current UT2004 time that is used by the TabooSet.
	 */
	public double getTime() {
		return time;
	}
	
	/**
	 * Current UT2004 time updated by {@link TabooSet#beginMessageListener}.
	 */
	private double time;
	
	/**
	 * {@link BeginMessage} listener that updates {@link TabooSet#time}.
	 * @author Jimmy
	 */
	private class BeginMessageListener implements IWorldEventListener<BeginMessage> {

		
		public BeginMessageListener(IWorldView worldView) {
			worldView.addEventListener(BeginMessage.class, this);
		}

		@Override
		public void notify(BeginMessage event) {
			time = event.getTime();
		}
		
	};
	
	/**
	 * {@link BeginMessage} listener that updates {@link TabooSet#time}.
	 */
	BeginMessageListener beginMessageListener;
	
	/**
	 * Constructor of the TabuSet.
	 * @param bot
	 */
	public TabooSet(UT2004Bot bot) {
		beginMessageListener = new BeginMessageListener(bot.getWorldView());
	}
	
	// =======================
	// Collection<T> INTERFACE
	// =======================

	/**
	 * Adds a taboo item that is valid for an infinite amount of time.
	 * @param item
	 * @return 
	 */
	public boolean add(T item) {
		boolean newItem = taboo.containsKey(item);
		taboo.put(item, (double)-1);
		return newItem;
	}
	
	/**
	 * Adds a tabu item that is valid for a period of 'timeout' time (in seconds).
	 * @param item
	 * @param timeout in seconds
	 */
	public void add(T item, double timeout) {
		taboo.put(item, time+timeout);
	}
	
	/**
	 * Removes a tabu item from the set.
	 * @param item
	 * @return 
	 */
	public boolean remove(Object item) {
		return taboo.remove(item) != null;
	}
	
	@Override
	public boolean isEmpty() {
		return taboo.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return taboo.containsKey(o);
	}

	@Override
	public Iterator<T> iterator() {
		return taboo.keySet().iterator();
	}

	@Override
	public Object[] toArray() {
		return taboo.keySet().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return taboo.keySet().toArray(a);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return taboo.keySet().containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean result = false;
		for (T value : c) {
			result = result || add(value);
		}
		return result;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean result = false;
		for (Object value : c) {
			result = result || remove(value);
		}
		return result;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean result = false;
		Iterator<T> iterator = taboo.keySet().iterator();
		while (iterator.hasNext()) {
			T value = iterator.next();
			if (c.contains(value)) continue;
			iterator.remove();
			result = true;
		}
		return result;
	}

	/**
	 * Return how many items are inside taboo set.
	 * @return
	 */
	@Override
	public int size() {
		return taboo.size();
	}
	
	/**
	 * Clears the taboo set.
	 */
	@Override
	public void clear() {
		taboo.clear();
	}

	
}
