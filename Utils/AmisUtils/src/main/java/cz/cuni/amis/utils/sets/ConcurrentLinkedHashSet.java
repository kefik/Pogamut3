package cz.cuni.amis.utils.sets;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConcurrentLinkedHashSet<K> implements Set<K>{

	private ConcurrentHashSet<K> set = new ConcurrentHashSet<K>();
	
	private CopyOnWriteArrayList<K> holder = new CopyOnWriteArrayList<K>();
	
	public ConcurrentLinkedHashSet() {
	}
	
	@Override
	public boolean add(K e) {
		if (set.contains(e)) return false;
		synchronized(set) {
			set.add(e);
			holder.add(e);
		}
		return true;
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
		holder.clear();
	}

	@Override
	public boolean contains(Object o) {
		return set.contains(o);
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
		return holder.size() == 0;
	}

	@Override
	public Iterator<K> iterator() {
		return holder.iterator();
	}

	@Override
	public boolean remove(Object o) {
		if (!set.contains(o)) return false;
		synchronized(set) {
			set.remove(o);
			holder.remove(o);
		}
		return true;
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
		return holder.size();
	}

	@Override
	public Object[] toArray() {
		return holder.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return holder.toArray(a);
	}

}
