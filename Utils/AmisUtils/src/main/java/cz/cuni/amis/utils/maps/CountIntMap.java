package cz.cuni.amis.utils.maps;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CountIntMap<KEY> implements Map<KEY, Integer>{
	
	private HashMap<KEY, Integer> counts = new HashMap<KEY, Integer>();
	
	@Override
	public void clear() {
		counts.clear();
	}
	
	@Override
	public Set<KEY> keySet() { 
		return counts.keySet();
	}

	@Override
	public Set<Entry<KEY, Integer>> entrySet() {
		return counts.entrySet();
	}
	
	@Override
	public Collection<Integer> values() {
		return counts.values();
	}
	
	@Override
	public Integer get(Object key) {
		Integer value = counts.get(key);
		if (value == null) {
			counts.put((KEY) key, 0);
			return 0;
		}
		return value;
	}
	
	@Override
	public Integer remove(Object key) {
		return counts.remove(key);
	}
	
	/**
	 * Sets key to 'value' returning previously associated value (alias for {@link CountIntMap#put(Object, Integer)}).
	 * @param key
	 * @param value
	 * @return
	 */
	public int set(KEY key, int value) {
		return counts.put(key, value);
	}
	
	/**
	 * Increase 'key' +1 returning previous value.
	 * @param key
	 * @return
	 */
	public int increase(KEY key) {
		return increase(key, 1);
	}
	
	/**
	 * Increase 'key' +amount returning previous value.
	 * @param key
	 * @return
	 */
	public int increase(KEY key, int amount) {
		return counts.put(key, get(key) + amount);
	}

	/**
	 * Increase 'key' -1 returning previous value.
	 * @param key
	 * @return
	 */
	public int decrease(KEY key) {
		return increase(key, -1);
	}
	
	/**
	 * Increase 'key' -amount returning previous value.
	 * @param key
	 * @return
	 */
	public int decrease(KEY key, int amount) {
		return increase(key, -amount);
	}

	/**
	 * Multiplies 'key' with amount returning previous value.
	 * @param key
	 * @return
	 */
	public int multi(KEY key, int amount) {
		return counts.put(key, get(key) * amount);
	}

	@Override
	public boolean containsKey(Object key) {
		return counts.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return counts.containsValue(value);
	}

	@Override
	public boolean isEmpty() {
		return counts.isEmpty();
	}

	@Override
	public Integer put(KEY key, Integer value) {
		return counts.put(key, value);
	}

	@Override
	public void putAll(Map<? extends KEY, ? extends Integer> m) {
		counts.putAll(m);
	}

	@Override
	public int size() {
		return counts.size();
	}

}
