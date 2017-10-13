package cz.cuni.amis.pogamut.multi.utils.timekey;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cuni.amis.utils.flag.FlagInteger;
import cz.cuni.amis.utils.flag.ImmutableFlag;

/**
 * the TimeKey class makes sure that for every integer time there is only one instance of the TimeKey object.
 * The inner structure of active is implemented via a WeakHashMap.
 * This means that if a process loses reference for the TimeKey object, it can be garbage collected.
 * @author srlok
 *
 */
public class TimeKey implements Comparable {
	
	public static class TimeKeyComparator implements Comparator<TimeKey> {

		@Override
		public int compare(TimeKey o1, TimeKey o2) {
			if (o1 == null && o2 == null) return 0;
			if (o1 == null) return -1;
			if (o2 == null) return 1;
			if (o1._time < o2._time) return -1;
			if (o1._time > o2._time) return 1;
			return 0;
		}
		
	};
	
	private static FlagInteger instances = new FlagInteger(0);
	
	public static ImmutableFlag<Integer> getInstances() {
		return instances.getImmutable();
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		synchronized(keys) {
			WeakReference<TimeKey> ref = keys.get(_time);
			if (ref == null || ref.get() == null || ref.get()._time == _time) {
				keys.remove(_time);
			}
		}
		instances.decrement(1);
	}
	
	private long _time;

	private TimeKey(long time) {
		instances.increment(1);
		_time = time;
	}
	
	// PROTECTED because of Test00_TimeKey
	protected static Map<Long, WeakReference<TimeKey>> keys = new HashMap<Long, WeakReference<TimeKey>>(1024);
	
	/**
	 * Returns the TimeKey object for the required time. <b>synchronized</b>
	 * 
	 * @param time
	 * @return always returns the same reference for the same time!
	 */
	public static TimeKey get(long time) {
		WeakReference<TimeKey> ref = keys.get(time);
		if (ref != null) {
			TimeKey key = ref.get();
			if (key != null) return key;
		}
		synchronized(keys) {
			ref = keys.get(time);
			if (ref == null) {
				TimeKey key = new TimeKey(time);
				keys.put(time, new WeakReference<TimeKey>(key));
				return key;
			}
			TimeKey key = ref.get();
			if (key == null) {
				key = new TimeKey(time);
				keys.put(time, new WeakReference<TimeKey>(key));
				return key;
			}
			return key;			
		}		
	}
	
	/**
	 * Wipes out case with time keys. Used by tests.
	 */
	public static void clear() {
		synchronized(keys) {
			keys.clear();
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == this) return true;  // early success
		if (other == null) return false; // early fail
		// We usually won't get here if comparing just TimeKey(s) as we maintain max 1 instance per _time.
		if (other instanceof TimeKey) {
			return ((TimeKey)other)._time == this._time;
		}
		return false;		
	}
	
	@Override
	public int hashCode() {
		return (int)(_time % Integer.MAX_VALUE);
	}
	
	/**
	 * Checks if a TimeKey instance exists for the integer time.
	 * @param time
	 * @return
	 */
	public static boolean exists(long time) {
		synchronized(keys) {
			WeakReference<TimeKey> ref = keys.get(time);
			if (ref == null) return false;
			if (ref.get() == null) {
				keys.remove(time);
				return false;
			}
			return true;
		}
	}
	
	/**
	 * returns the integer time for this timekey.
	 * @return
	 */
	public long getTime() {
		return _time;
	}

	@Override
	public int compareTo(Object key) {
		if (this == key) return 0; // early success
		if (key == null) return 1; // early fail
		if (!(key instanceof TimeKey)) {
			throw new ClassCastException("TimeKey can only be compared with TimeKey objects");
		}
		if ( this._time < ((TimeKey)key)._time ) {
			return -1;
		} else {
			return 1;
		}
	}
	
	@Override
	public String toString() {
		return "TimeKey[" + _time + "]";
	}

	public static List<WeakReference<TimeKey>> getAllKeys() {
		synchronized(keys) {
			return new ArrayList<WeakReference<TimeKey>>(keys.values());
		}
	}
}
