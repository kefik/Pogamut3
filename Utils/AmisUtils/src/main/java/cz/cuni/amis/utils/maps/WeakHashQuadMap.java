package cz.cuni.amis.utils.maps;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * A 4-level hashMap, the PrimaryKeys are weakly referenced.
 * Get methods of all levels assure that the corresponding maps are created.
 * @author srlok
 *
 * @param <PRIMARY_KEY>
 * @param <SECONDARY_KEY>
 * @param <TERTIARY_KEY>
 * @param <QUATERNARY_KEY>
 * @param <ITEM>
 */
public class WeakHashQuadMap<PRIMARY_KEY,SECONDARY_KEY,TERTIARY_KEY,QUATERNARY_KEY,ITEM>
extends WeakHashMap<PRIMARY_KEY, Map<SECONDARY_KEY,Map<TERTIARY_KEY,Map<QUATERNARY_KEY,ITEM>>>>{
	
	private int secondaryCapacity;
	private int tertiaryCapacity;
	private int quaternaryCapacity;
	
	public WeakHashQuadMap()
	{
		this.secondaryCapacity = 16;
		this.tertiaryCapacity = 16;
		this.quaternaryCapacity = 16;
	}
	
	public WeakHashQuadMap(int primaryCapacity, int secondaryCapacity, int tertiaryCapacity, int quaternaryCapacity)
	{
		super(primaryCapacity);
		this.secondaryCapacity = secondaryCapacity;
		this.tertiaryCapacity = tertiaryCapacity;
		this.quaternaryCapacity = quaternaryCapacity;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	/**
	 * If the primary_key map does not exist, an empty map is created, inserted and returned.
	 */
	public Map<SECONDARY_KEY,Map<TERTIARY_KEY, Map<QUATERNARY_KEY,ITEM>>> get(Object key)
	{
		Map<SECONDARY_KEY,Map<TERTIARY_KEY,Map<QUATERNARY_KEY,ITEM>>> result = super.get(key);
		if ( result != null)
		{
			return result;
		}
		result = Collections.synchronizedMap( new HashTriMap<SECONDARY_KEY,TERTIARY_KEY,QUATERNARY_KEY,ITEM>(secondaryCapacity,tertiaryCapacity,quaternaryCapacity) );
		super.put((PRIMARY_KEY) key, result);
		return result;
	}
	
	/**
	 * If the requested secondLevel map does not exist an empty one is created, inserted
	 * according to the primaryKey and returned.
	 * @param primaryKey
	 * @param secondaryKey
	 * @return
	 */
	public Map<TERTIARY_KEY,Map<QUATERNARY_KEY,ITEM>> get(PRIMARY_KEY primaryKey, SECONDARY_KEY secondaryKey)
	{
		Map<TERTIARY_KEY,Map<QUATERNARY_KEY,ITEM>> result = get(primaryKey).get(secondaryKey);
		if ( result != null)
		{
			return result;
		}
		result = Collections.synchronizedMap( new HashMapMap<TERTIARY_KEY, QUATERNARY_KEY,ITEM>(tertiaryCapacity, quaternaryCapacity ) );
		get(primaryKey).put(secondaryKey, result);
		return result;
	}

	/**
	 * If the requested level 3 map does not exist
	 * an empty one is created, inserted and returned.
	 * @param primaryKey
	 * @param secondaryKey
	 * @param tertiaryKey
	 * @return
	 */
	public Map<QUATERNARY_KEY,ITEM> get(PRIMARY_KEY primaryKey, SECONDARY_KEY secondaryKey, TERTIARY_KEY tertiaryKey)
	{
		Map<QUATERNARY_KEY,ITEM> result = get(primaryKey,secondaryKey).get(tertiaryKey);
		if ( result != null)
		{
			return result;
		}
		result = Collections.synchronizedMap( new HashMap<QUATERNARY_KEY,ITEM>(quaternaryCapacity));
		get(primaryKey,secondaryKey).put(tertiaryKey, result);
		return result;
	}
	
	/**
	 * Returns null if the mapping is not present.
	 * @param primaryKey
	 * @param secondaryKey
	 * @param tertiaryKey
	 * @param quaternaryKey
	 * @return
	 */
	public ITEM get(PRIMARY_KEY primaryKey, SECONDARY_KEY secondaryKey, TERTIARY_KEY tertiaryKey, QUATERNARY_KEY quaternaryKey)
	{
		return get(primaryKey,secondaryKey,tertiaryKey).get(quaternaryKey);
	}
	
	/**
	 * Returns the inserted item.
	 * @param primaryKey
	 * @param secondaryKey
	 * @param tertiaryKey
	 * @param quaternaryKey
	 * @param item
	 * @return
	 */
	public ITEM put(PRIMARY_KEY primaryKey, SECONDARY_KEY secondaryKey, TERTIARY_KEY tertiaryKey, QUATERNARY_KEY quaternaryKey, ITEM item)
	{
		return get(primaryKey,secondaryKey,tertiaryKey).put(quaternaryKey, item);
	}
	
	/**
	 * 
	 */
	@Override
	public Map<SECONDARY_KEY,Map<TERTIARY_KEY, Map<QUATERNARY_KEY,ITEM>>> remove(Object key )
	{
		Map<SECONDARY_KEY,Map<TERTIARY_KEY, Map<QUATERNARY_KEY,ITEM>>> result = super.remove(key);
		if ( result != null)
		{
			return result;
		}
		return Collections.synchronizedMap( new HashTriMap<SECONDARY_KEY, TERTIARY_KEY, QUATERNARY_KEY,ITEM>(secondaryCapacity,tertiaryCapacity,quaternaryCapacity));
	}
	
	public Map<TERTIARY_KEY,Map<QUATERNARY_KEY,ITEM>> remove2(PRIMARY_KEY primaryKey, SECONDARY_KEY secondaryKey)
	{
		 Map<TERTIARY_KEY,Map<QUATERNARY_KEY,ITEM>> result = get(primaryKey).remove(secondaryKey);
		 if (result != null)
		 {
			return result; 
		 }
		 return Collections.synchronizedMap(new  HashMapMap<TERTIARY_KEY, QUATERNARY_KEY, ITEM>(tertiaryCapacity,quaternaryCapacity));
	}
	
	public Map<QUATERNARY_KEY,ITEM> remove(PRIMARY_KEY primaryKey, SECONDARY_KEY secondaryKey, TERTIARY_KEY tertiaryKey)
	{
		Map<QUATERNARY_KEY,ITEM> result = get(primaryKey,secondaryKey).remove(tertiaryKey);
		if (result != null)
		{
			return result;
		}
		return Collections.synchronizedMap( new HashMap<QUATERNARY_KEY,ITEM>(quaternaryCapacity));
	}
	
	public ITEM remove(PRIMARY_KEY primaryKey, SECONDARY_KEY secondaryKey, TERTIARY_KEY tertiaryKey, QUATERNARY_KEY quaternaryKey)
	{
		return get(primaryKey,secondaryKey,tertiaryKey).remove(quaternaryKey);
		
	}
}