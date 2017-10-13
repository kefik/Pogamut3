package cz.cuni.amis.utils.maps;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * THREAD-SAFE unless you misuse {@link SyncHashMap#getMap()} without locking {@link SyncHashMap#getReadLock()} or {@link SyncHashMap#getWriteLock()}
 * according to the operation you want to perform.
 * 
 * @author Jimmy
 *
 * @param <K>
 * @param <V>
 */
public class SyncHashMap<K, V> {
	
	private Map<K, V> map = new HashMap<K, V>();
	
	private ReadWriteLock rwLock = new ReentrantReadWriteLock(false);
	
	private Lock readLock = rwLock.readLock();
	
	private Lock writeLock = rwLock.writeLock();
	
	public V put(K key, V value) {
		writeLock.lock();
		try {
			return map.put(key, value);
		} finally {
			writeLock.unlock();
		}
	}
	
	public V remove(K key) {
		writeLock.lock();
		try {
			return map.remove(key);
		} finally {
			writeLock.unlock();
		}
	}
	
	public V get(Object key) {
		readLock.lock();
		try {
			return map.get(key);
		} finally {
			readLock.unlock();
		}
	}

	public int size() {
		return map.size();
	}
	
	/**
	 * Use with care ... you need to synchronize read/writes via {@link SyncHashMap#getReadLock()} and {@link SyncHashMap#getWriteLock()}.
	 * @return
	 */
	public Map<K, V> getMap() {
		return map;
	}
	
	/**
	 * Returns READ LOCK.
	 * @return
	 */
	public Lock getReadLock() {
		return readLock;
	}
	
	/**
	 * Returns WRITE LOCK.
	 * @return
	 */
	public Lock getWriteLock() {
		return writeLock;
	}

}
