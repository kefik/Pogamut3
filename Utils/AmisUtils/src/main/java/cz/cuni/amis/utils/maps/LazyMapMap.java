package cz.cuni.amis.utils.maps;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Map containing other maps. Whenever a map under some key is requested and does not exists,
 * the HashMapMap automatically creates new one.
 * <p><p>
 * The implementation is unsynchronized, created maps are synchronized (just iteration over the inner-map must
 * be synchronized by the user as described in Java(tm) documentation).
 * 
 * @author Jimmy
 *
 * @param <PRIMARY_KEY>
 * @param <SECONDARY_KEY>
 * @param <ITEM>
 */
public abstract class LazyMapMap<PRIMARY_KEY, SECONDARY_KEY, ITEM> extends HashMap<PRIMARY_KEY, Map<SECONDARY_KEY, ITEM>> {
	
	public LazyMapMap() {
	}
	
	/**
     * Creates value for given key.
     * @param key
     * @return
     */
    protected abstract ITEM create(SECONDARY_KEY key);

	/**
	 * The get method ensures that the requested map under primaryKey always exists!
	 * 
	 * @param primaryKey must be instance of PRIMARY_KEY
	 */
	@Override
	public Map<SECONDARY_KEY, ITEM> get(Object primaryKey) {
		Map<SECONDARY_KEY, ITEM> map = super.get(primaryKey);
		if (map != null) return map;
		map = Collections.synchronizedMap(new LazyMap<SECONDARY_KEY, ITEM>(){
			@Override
			protected ITEM create(SECONDARY_KEY key) {
				return LazyMapMap.this.create(key);
			}
		});
		super.put((PRIMARY_KEY)primaryKey, map);
		return map;
	}
	
	/**
	 * Returns an item under primary and secondary key if exists (otherwise a null is returned).
	 * @param primaryKey
	 * @param secondaryKey
	 * @return
	 */
	public ITEM get(PRIMARY_KEY primaryKey, SECONDARY_KEY secondaryKey) {
		return get(primaryKey).get(secondaryKey);
	}
	
	/**
	 * Inserts an item under primary and then secondary key.
	 * @param primaryKey
	 * @param secondaryKey
	 * @param item
	 */
	public void put(PRIMARY_KEY primaryKey, SECONDARY_KEY secondaryKey, ITEM item) {
		get(primaryKey).put(secondaryKey, item);
	}
	
	/**
	 * Remove returns the removed item, if item was non-existent, it returns empty map. 
	 * @param primaryKey
	 * @return
	 */
	@Override
	public Map<SECONDARY_KEY, ITEM> remove(Object primaryKey) {
		Map<SECONDARY_KEY, ITEM> map = super.remove(primaryKey);
		if (map != null) return map;
		return Collections.synchronizedMap(new LazyMap<SECONDARY_KEY, ITEM>(){
			@Override
			protected ITEM create(SECONDARY_KEY key) {
				return LazyMapMap.this.create(key);
			}
		});
	}

	/**
	 * Removes an item from the map.
	 * @param primaryKey
	 * @param secondaryKey
	 * @return
	 */
	public ITEM removeItem(PRIMARY_KEY primaryKey, SECONDARY_KEY secondaryKey) {
		return get(primaryKey).remove(secondaryKey);
	}

}
