package cz.cuni.amis.utils.maps;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;


/**
 * Three-level hash map, where the PRIMARY_KEY maps are implemented using
 * {@link WeakHashMap}.
 * This means, that the PRIMARY_KEY maps, may be garbage collected unless there is a direct reference to the PRIMARY_KEY.
 * @author srlok
 *
 * @param <PRIMARY_KEY>
 * @param <SECONDARY_KEY>
 * @param <TERTIARY_KEY> 
 * @param <ITEM>
 */
public class WeakHashTriMap<PRIMARY_KEY,SECONDARY_KEY,TERTIARY_KEY,ITEM>
	extends WeakHashMap<PRIMARY_KEY,Map<SECONDARY_KEY,Map<TERTIARY_KEY,ITEM>>> 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int secondaryCapacity;
	private int tertiaryCapacity;
	
	/**
	 * Creates a WeakHashTriMap with default loadFactor(0.75) and default starting capacities (16,16,16)
	 */
	public WeakHashTriMap()
	{
		secondaryCapacity = 16;
		tertiaryCapacity = 16;
	}
	
	/**
	 * Creates a new WeakHashTriMap with the default loadFactor (0.75) for all levels and the provided starting capacties for different levels.
	 * @param primaryCapacity
	 * @param secondaryCapacity
	 * @param tertiaryCapacity
	 */
	public WeakHashTriMap( int primaryCapacity, int secondaryCapacity, int tertiaryCapacity)
	{
		super(primaryCapacity);
		this.secondaryCapacity = secondaryCapacity;
		this.tertiaryCapacity = tertiaryCapacity;
	}
	
	/**
	 * Returns a HashMap<SECONDARY_KEY,HashMap<TERTIARY_KEY,ITEM>>
	 * Never returns null, if the map under primary key doesn't exist, an empty one is added and returned.
	 * @param primaryKey
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<SECONDARY_KEY, Map<TERTIARY_KEY,ITEM>> get(Object primaryKey)
	{
		Map<SECONDARY_KEY, Map<TERTIARY_KEY,ITEM>> result = super.get(primaryKey);
		if (result != null) { return result; };
		result = Collections.synchronizedMap( new HashMapMap<SECONDARY_KEY,TERTIARY_KEY,ITEM>(secondaryCapacity, tertiaryCapacity) );
		super.put( (PRIMARY_KEY)primaryKey,result);
		return result;
	}
	
	/**
	 * Returns the requested map, never returns null.
	 * If the map does not exist an empty map is created and returned.
	 * @param primaryKey
	 * @param secondaryKey
	 * @return
	 */
	public Map<TERTIARY_KEY,ITEM> get(PRIMARY_KEY primaryKey, SECONDARY_KEY secondaryKey)
	{
		Map<TERTIARY_KEY, ITEM> result = get(primaryKey).get(secondaryKey);
		if (result != null) { return result; };
		result = Collections.synchronizedMap( new HashMap<TERTIARY_KEY,ITEM>( tertiaryCapacity ));
		get(primaryKey).put(secondaryKey, result);
		return result;
	}
	
	/**
	 * Returns item specified, returns null if item does not appear in the map.
	 * @param primaryKey
	 * @param secondaryKey
	 * @param tertiaryKey
	 * @return
	 */
	public ITEM get(PRIMARY_KEY primaryKey, SECONDARY_KEY secondaryKey, TERTIARY_KEY tertiaryKey)
	{
		return get(primaryKey,secondaryKey).get(tertiaryKey);
	}
	
	/**
	 * Puts the item into the map.
	 * @param primaryKey
	 * @param secondaryKey
	 * @param tertiaryKey
	 * @param item
	 */
	public void put(PRIMARY_KEY primaryKey, SECONDARY_KEY secondaryKey, TERTIARY_KEY tertiaryKey, ITEM item)
	{
		get(primaryKey,secondaryKey).put(tertiaryKey, item);
	}
	
	/**
	 * Removes the requested map. If the map doesn't exist, returns an empty map.
	 */
	@Override
	public Map<SECONDARY_KEY, Map<TERTIARY_KEY,ITEM>> remove(Object primaryKey)
	{
		Map<SECONDARY_KEY, Map<TERTIARY_KEY,ITEM>> result = super.remove(primaryKey);
		if (result != null) { return result; };
		return Collections.synchronizedMap( new HashMapMap<SECONDARY_KEY, TERTIARY_KEY,ITEM>(secondaryCapacity, tertiaryCapacity) );
	}
	
	/**
	 * removes the map under primary and secondary key, if the map does not exist,
	 * the data structure is not changed and a new map is returned.
	 * @param primaryKey
	 * @param secondaryKey
	 * @return
	 */
	public Map<TERTIARY_KEY,ITEM> remove(PRIMARY_KEY primaryKey, SECONDARY_KEY secondaryKey)
	{
		Map<TERTIARY_KEY,ITEM> result = get(primaryKey).remove(secondaryKey);
		if ( result != null) { return result; };
		return Collections.synchronizedMap( new HashMap<TERTIARY_KEY, ITEM> (tertiaryCapacity));
	}
	
	/**
	 * Returns the item under specified keys and removes it from the map,
	 * returns null if item is not present in the map.
	 * @param primaryKey
	 * @param secondaryKey
	 * @param tertiaryKey
	 * @return
	 */
	public ITEM remove(PRIMARY_KEY primaryKey, SECONDARY_KEY secondaryKey, TERTIARY_KEY tertiaryKey)
	{
		return get(primaryKey,secondaryKey).remove(tertiaryKey);
	}

}
