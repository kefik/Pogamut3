package cz.cuni.amis.utils.maps;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Map containing sets. Whenever a set under some key is requested and does not exists,
 * the HashMapSet automatically creates new one.
 * <p><p>
 * The implementation is unsynchronized, created sets are synchronized (just iteration over the set must
 * be synchronized by the user as described in Java(tm) documentation).
 * 
 * @author Jimmy
 *
 * @param <KEY>
 * @param <ITEM>
 */
public class HashMapSet<KEY, ITEM> extends HashMap<KEY, Set<ITEM>> {
	
	/**
	 * The get method ensures that the requested set under primaryKey always exists!
	 * 
	 * @param primaryKey must be instance of PRIMARY_KEY
	 */
	@Override
	public Set<ITEM> get(Object primaryKey) {
		Set<ITEM> set = super.get(primaryKey);
		if (set != null) return set;
		set = Collections.synchronizedSet(new HashSet<ITEM>());
		super.put((KEY)primaryKey, set);
		return set;
	}
	
	/**
	 * Adds the item into the set under the key.
	 * @param key
	 * @param item
	 */
	public void add(KEY key, ITEM item) {
		get(key).add(item);
	}

	/**
	 * Remove returns the removed item, if item was non-existent, it returns empty set. 
	 * @param primaryKey
	 * @return
	 */
	@Override
	public Set<ITEM> remove(Object key) {
		Set<ITEM> set = super.remove(key);
		if (set != null) return set;
		return Collections.synchronizedSet(new HashSet<ITEM>());
	}
	
	/**
	 * Removes the item from the set under the key.
	 * @param key
	 * @param item
	 */
	public boolean removeItem(Object key, Object item) {
		return get(key).remove(item);
	}
	
	/**
	 * Tests whether an 'item' is inside the set under 'key'.
	 * @param key
	 * @param item
	 * @return
	 */
	public boolean contains(KEY key, ITEM item) {
		return get(key).contains(item);
	}
	
}