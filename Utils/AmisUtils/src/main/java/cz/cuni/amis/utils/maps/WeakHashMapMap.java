package cz.cuni.amis.utils.maps;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Two-level hashMap where the first-level keys are weakly referenced!
 * This means that if the key is not strongly referenced elsewhere, the maps can be garbage collected.
 * @author srlok
 *
 * @param <PRIMARY_KEY>
 * @param <SECONDARY_KEY>
 * @param <ITEM>
 */
public class WeakHashMapMap<PRIMARY_KEY,SECONDARY_KEY,ITEM>
	extends WeakHashMap<PRIMARY_KEY, Map<SECONDARY_KEY, ITEM>> {
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<SECONDARY_KEY,ITEM> get(Object primaryKey)
	{
		Map<SECONDARY_KEY,ITEM> result = super.get( primaryKey );
		if (result != null) return result;
		result = Collections.synchronizedMap( new HashMap<SECONDARY_KEY,ITEM>() );
		super.put( (PRIMARY_KEY)primaryKey, result);
		return result;
	}
	
	/**
	 * Returns the requested item under primary and secondary key.
	 * @param primaryKey weakly-referenced
	 * @param secondaryKey
	 * @return if no such item exists returns  NULL
	 */
	public ITEM get(PRIMARY_KEY primaryKey, SECONDARY_KEY secondaryKey)
	{
		return get(primaryKey).get(secondaryKey);
	}
	
	/**
	 * Inserts item under primary and secondary_key.
	 * @param primaryKey weakly-referenced
	 * @param secondaryKey
	 * @param item
	 * @return inserted item
	 */
	public ITEM put(PRIMARY_KEY primaryKey, SECONDARY_KEY secondaryKey, ITEM item)
	{
		return get(primaryKey).put(secondaryKey, item);
	}
	
	/**
	 * removes the item under primary and secondary key.
	 * @param primaryKey
	 * @param secondaryKey
	 * @return removed item
	 */
	public ITEM remove(PRIMARY_KEY primaryKey, SECONDARY_KEY secondaryKey)
	{
		return get(primaryKey).remove( secondaryKey);
	}
	
	/**
	 * removes all items under primaryKey
	 * @param primaryKey
	 * @return map of all removed items
	 */
	@Override
	public Map<SECONDARY_KEY,ITEM> remove(Object primaryKey)
	{
		Map<SECONDARY_KEY,ITEM> result = super.remove(primaryKey);
		if (result != null) { return result ; }
		result = Collections.synchronizedMap( new HashMap<SECONDARY_KEY,ITEM>() );
		return result;
	}
	
	
}
