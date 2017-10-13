package cz.cuni.amis.utils.maps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Map containing lists of items. Whenever a list under some key is requested and does not exists,
 * the HashMapList automatically creates new one.
 * <p><p>
 * The implementation is unsynchronized, created lists are synchronized (just iteration over list must
 * be synchronized by the user as described in Java(tm) documentation).
 * 
 * @author Jimmy
 *
 * @param <KEY>
 * @param <ITEM>
 */
public class HashMapList<KEY, ITEM> extends HashMap<KEY, List<ITEM>>{
	
	/**
	 * Returns a list under a specific key in the map. If list does not exist, it
	 * automatically creates new (synchronized) one, inserts it into map and returns it.
	 */
	@Override
	public List<ITEM> get(Object key) {
		List<ITEM> list = super.get(key);
		if (list != null) return list;
		list = Collections.synchronizedList(new ArrayList<ITEM>());
		super.put((KEY)key, list);
		return list;
	}
	
	/**
	 * Add a new item at the end of the list under a specific key. If list does not exists,
	 * it automatically creates new (synchronized) one.
	 * @param key
	 * @param item
	 */
	public void add(KEY key, ITEM item) {
		get(key).add(item);
	}
	
	/**
	 * Remove returns the removed item, if item was non-existent, it returns empty list. 
	 * @param key
	 * @return
	 */
	@Override
	public List<ITEM> remove(Object key) {
		List<ITEM> list = super.remove(key);
		if (list != null) return list;
		return Collections.synchronizedList(new ArrayList<ITEM>(0));
	}
	
	/**
	 * Remove an item at 'index' from the list under a specific key. The list bounds are not checked.
	 * @param key
	 * @param index
	 * @return
	 */
	public ITEM remove(KEY key, int index) {
		return get(key).remove(index);
	}
	
	/**
	 * Returns first item from the list under a specific key. If the list is empty, returns null.
	 * @param key
	 * @return
	 */
	public ITEM peek(KEY key) {
		List<ITEM> list = get(key);
		if (list.size() > 0) return list.get(0);
		return null;
	}
	
	/**
	 * Removes first item from the list under a specific key.
	 * @param key
	 * @return
	 */
	public ITEM pull(KEY key) {
		return remove(key, 0);
	}
	
}