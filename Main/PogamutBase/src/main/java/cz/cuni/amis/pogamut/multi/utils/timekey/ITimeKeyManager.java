package cz.cuni.amis.pogamut.multi.utils.timekey;

import java.util.Set;

import cz.cuni.amis.pogamut.multi.utils.exception.TimeKeyNotLockedException;

public interface ITimeKeyManager {

	/**
	 * Locks some "time" (or increment lock number if existing lock exists).
	 * @param time
	 */
	public void lock(long time);
	
	/**
	 * True if the provided timeKey is explicitly locked (lock(key) was called).
	 * @param key
	 * @return
	 */
	public boolean isLocked(TimeKey key);
	
	/**
	 * True if the provided timeKey is explicitly locked (lock(key) was called).
	 * @param time
	 * @return
	 */
	public boolean isLocked(long time);
	
	/**
	 * Unlocks some "time".
	 * @param key
	 * @throws TimeKeyNotLockedException
	 */
	public void unlock(long key) throws TimeKeyNotLockedException;
	
	/**
	 * Completely unlocks one time (regardless number of locks held).
	 * @param time
	 * @throws TimeKeyNotLockedException
	 */
	public void unlockAll(long time) throws TimeKeyNotLockedException;
	
	/**
	 * Unlock all times.
	 */
	public void unlockAll();
	
	/**
	 * Returns an immutable collection of currently held timeKeys.
	 * @return
	 */
	public Set<Long> getHeldKeys();
	
	/**
	 * Returns an immutable collection of currently held timeKeys as string.
	 * @return
	 */
	public String getHeldKeysStr();
	
}
