package cz.cuni.amis.utils.maps;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cz.cuni.amis.utils.collections.LazyMapValuesCollection;

/**
 * Abstract implementation of a lazy map.
 * The values are created on-demand by the create method if they are not cached already.
 * The keySet is kept updated however, only the values are lazy-generated.
 * If you need to iterate over the map, but you don't actually need all the values, iterate over the keySet.
 * @author srlok
 *
 * @param <KEY> KeyType
 * @param <VALUE> ValueType
 */
public abstract class AbstractLazyMap<KEY, VALUE> implements Map<KEY,VALUE> {

	private Map<KEY,VALUE> cachedObjects = new HashMap<KEY,VALUE>(); 
	private Set<KEY> keySet = null;
	
	/**
	 * Creates the Mapped object based on its key. Every LazyMap must implement this.
	 * @param key
	 * @return
	 */
	protected abstract VALUE create( Object key );
	
	public AbstractLazyMap()
	{
		cachedObjects = new HashMap<KEY,VALUE>();
		keySet = new HashSet<KEY>();
	}
	
	public AbstractLazyMap( Set<KEY> entryKeySet )
	{
		this.keySet = entryKeySet;
	}
	
	public AbstractLazyMap( Map<KEY,VALUE> baseMap )
	{
		this.cachedObjects = baseMap;
		this.keySet = baseMap.keySet();
	}
	
	public void addKey( KEY key )
	{
		keySet.add(key);
	}
	
	@Override
	public void clear() {
		keySet.clear();
		cachedObjects.clear();
	}
	
	/**
	 * manually clears the cached objects
	 */
	public void clearCache()
	{
		cachedObjects.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return keySet.contains( key );
	}
	
	public void setKeySet( Set<KEY> newKeySet)
	{
		this.clear();
		this.keySet = newKeySet;
	}

	@Override
	/**
	 * Warning O(N) complexity, has to create all objects
	 */
	public boolean containsValue(Object value) 
	{
		for ( KEY key : keySet )
		{
			if ( this.get(key).equals(value) )
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public Set<java.util.Map.Entry<KEY, VALUE>> entrySet() {
		return cachedObjects.entrySet();
	}

	@Override
	public synchronized VALUE get(Object key) {
		VALUE value = cachedObjects.get(key);
		if ( value != null ) return value;
		if ( keySet.contains(key) )
		{
			value = create(key);
			cachedObjects.put((KEY) key, value);
		}
		return value;
	}

	@Override
	public boolean isEmpty() {
		return keySet.isEmpty();
	}

	@Override
	public Set<KEY> keySet() {
		return keySet;
	}

	@Override
	public VALUE put(KEY key, VALUE value) {
		keySet.add(key);
		return cachedObjects.put(key, value);
	}

	@Override
	public void putAll(Map<? extends KEY, ? extends VALUE> m) {
		keySet.addAll( m.keySet() );
		cachedObjects.putAll(m);
	}

	@Override
	public VALUE remove(Object key) {
		keySet.remove(key);
		return cachedObjects.remove(key);
	}

	@Override
	public int size() {
		return keySet.size();
	}

	@Override
	public Collection<VALUE> values() {
		return new LazyMapValuesCollection<VALUE,KEY>(this);
	}
	
	

}
