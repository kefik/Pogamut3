package cz.cuni.amis.utils.maps;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Maps whose items are initialized on demand by create(K) method.
 * @author ik
 */
public abstract class LazyMap<K, V> implements Map<K, V> {

    Map<K, V> map = null;

    /**
     * Creates value for given key.
     * @param key
     * @return
     */
    protected abstract V create(K key);

    public LazyMap() {
        map = new HashMap<K, V>();
    }

    public LazyMap(Map<K, V> baseMap) {
        map = baseMap;
    }


    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        V val = map.get((K)key);
        if(val == null) {
        	synchronized(this) {
        		val = map.get((K)key);
        		if (val != null) return val;
	            val = create((K)key);
	            if(val != null) {
	                map.put((K)key, val);
	            }
        	}
        }
        return val;
    }

    @Override
    public V put(K key, V value) {
    	return map.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<V> values() {
        return map.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

}
