package cz.cuni.amis.utils.collections;

import java.util.Collection;
import java.util.Iterator;

import cz.cuni.amis.utils.maps.AbstractLazyMap;

/**
 * Read-only Collection used for lazy implementation of Map.values().
 * The items in this collection are backed by the map and Map.get() is used if an object is required.
 * @author srlok
 *
 */
public class LazyMapValuesCollection<VALUE,KEY> implements Collection<VALUE> {

	private AbstractLazyMap<KEY,VALUE> map;
	
	public AbstractLazyMap<KEY,VALUE> getBaseMap()
	{
		return map;
	}
	
	public LazyMapValuesCollection(AbstractLazyMap<KEY,VALUE> sourceMap)
	{
		map = sourceMap;
	}
	
	@Override
	public boolean add(VALUE e) {
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends VALUE> c) {		
		return false;
	}

	@Override
	public void clear() {
	}

	@Override
	/**
	 * Warning! may have - O(N) complexity!
	 */
	public boolean contains(Object o) {
		for ( Object key : map.keySet() )
		{
			if ( map.get(key).equals(o))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	/**
	 * Warning! O(N)*M complexity!
	 */
	public boolean containsAll(Collection<?> c) {
		for ( Object o : c)
		{
			if ( !contains(o))
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Iterator<VALUE> iterator() {
		return new Iterator<VALUE>()
		{
			Iterator<KEY> it = null;
			KEY value = null;
			
			{
				it = getBaseMap().keySet().iterator();
			}
			
			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public VALUE next() {
				value = it.next();
				return getBaseMap().get(value);
			}

			@Override
			public void remove() {
				getBaseMap().remove(value);
				
			}
			
		};
	}

	@Override
	public boolean remove(Object o) {
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return false;
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Object[] toArray() {
		Object[] arr = new Object[ map.size()];
		int i = 0;
		for ( Object key : map.keySet() )
		{
			arr[i] = map.get(key);
		}
		return arr;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		Object[] arr = new Object[ map.size()];
		int i = 0;
		for ( Object key : map.keySet() )
		{
			arr[i] = map.get(key);
		}
		return (T[])arr;
	}

}
