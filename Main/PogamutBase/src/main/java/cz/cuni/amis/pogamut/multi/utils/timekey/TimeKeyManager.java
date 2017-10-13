package cz.cuni.amis.pogamut.multi.utils.timekey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.cuni.amis.pogamut.multi.utils.exception.TimeKeyNotLockedException;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Singleton class that manages timekey locks and unlocks,
 * 
 * @author srlok
 *
 */
public class TimeKeyManager implements ITimeKeyManager {
	
	private static class KeyHolder {
		
		public long data;
		
		public TimeKey timeKey;
		
		public KeyHolder() {
			this(null, 0);
		}
		
		public KeyHolder(long key, long data) {
			this.timeKey = TimeKey.get(key);
			this.data = data;
		}
		
		public KeyHolder(TimeKey key, long data) {
			this.timeKey = key;
			this.data = data;
		}

	}
	
	private HashMap<Long, KeyHolder> locks;
	
	private static Object instanceCreationMutex = new Object();
	private static TimeKeyManager instance;
	
	@Override
	public Set<Long> getHeldKeys()
	{
		synchronized(locks) {
			return Collections.unmodifiableSet( new HashSet( locks.keySet() ) );
		}
	}
	
	@Override
	public String getHeldKeysStr()
	{
		List<Long> keys = new ArrayList<Long>(getHeldKeys());
		Collections.sort(keys);
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for (Long key : keys) {
			if (first) first = false;
			else sb.append(", ");
			sb.append(key);
		}
		if (sb.length() == 0) return "nothing";
		return sb.toString();
	}
	
	public static TimeKeyManager get() {
		if (instance != null) return instance;
		synchronized(instanceCreationMutex) {
			if (instance != null) return instance;
			instance = new TimeKeyManager() ;
			return instance;
		}		
	}
	
	@Override
	public boolean isLocked(TimeKey key) {
		return isLocked(key.getTime());
	}
	
	@Override
	public boolean isLocked(long time) {
		synchronized(locks) {
			return locks.containsKey(time);
		}
	}
	
	@Override
	public void unlock(long key) throws TimeKeyNotLockedException {
		synchronized(locks) {
			KeyHolder n = locks.get(key);
			if (n == null) {
				throw new TimeKeyNotLockedException("Trying to lock an unlocked " + key);
			}
			if (n.data <= 0) {
				throw new PogamutException("Locks corrupted! " + key + " locks == " + n + " <= 0, ILLEGAL!", this);
			}
			if (n.data == 1) {
				locks.remove(key);
			} else {
				n.data -= 1;
			}
		}
	}
	
	@Override
	public void unlockAll(long key) throws TimeKeyNotLockedException {
		synchronized(locks) {
			KeyHolder n = locks.get(key);
			if (n == null) {
				throw new TimeKeyNotLockedException("Trying to lock an unlocked " + key);
			}
			locks.remove(key);			
		}
	}
	
	@Override
	public void lock(long key) {
		synchronized(locks) {
			KeyHolder n = locks.get(key);
			if (n == null) {
				locks.put(key, new KeyHolder(key, 1));
			} else {
				n.data += 1;
			}
		}
	}
	
	private TimeKeyManager() {
		locks = new HashMap<Long, KeyHolder>();
	}

	@Override
	public void unlockAll() {
		synchronized(locks) {
			while (locks.size() > 0) {
				Long time = locks.keySet().iterator().next();
				if (time == null) break;
				try {
					unlockAll(time);
				} catch (TimeKeyNotLockedException e) {
				}
			}
		}
	}

}
