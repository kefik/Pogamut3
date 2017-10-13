package cz.cuni.amis.utils.sets;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashSet<K> implements Set<K>{

	private ConcurrentHashMap<K, K> map = new ConcurrentHashMap<K, K>();
	
	public ConcurrentHashSet() {
	}
	
	@Override
	public boolean add(K e) {
		boolean contains = map.containsKey(e);
		map.put(e,e);
		return !contains;
	}

	@Override
	public boolean addAll(Collection<? extends K> c) {
		boolean changed = false;
		for (K element : c) {
			changed = add(element) || changed;
		}
		return changed;
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o)) return false;
		}
		return true;
	}

	@Override
	public boolean isEmpty() {
		return map.size() == 0;
	}

	@Override
	public Iterator<K> iterator() {
		return map.values().iterator();
	}

	@Override
	public boolean remove(Object o) {
		return map.remove(o) != null;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for (Object o : c) {
			changed = remove(o) || changed;
		}
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		clear();
		for (Object element : c) {
			add((K) element);
		}
		return true;
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Object[] toArray() {
		return map.values().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return map.values().toArray(a);
	}

}
