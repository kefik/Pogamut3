package cz.cuni.amis.utils;

/**
 * {@link Lazy} that has synchronized {@link SyncLazy#create()} method (you do not need to synchronize it for yourself).
 * <p><p>
 * THREAD-SAFE!
 * 
 * @author Jimmy
 *
 * @param <T>
 */
public abstract class SyncLazy<T> extends Lazy<T> {

	private Object mutex = new Object();
	
	/**
     * @deprecated
     * @return value created by {@link Lazy#create()} (SYNCHRONIZED CREATION!)
     */
	@Override
	public T getVal() {
		return get();
	}
	
	/**
     * @return value created by {@link Lazy#create()} (SYNCHRONIZED CREATION!)
     */
	@Override
	public T get() {
        if (obj != null) {
        	return obj;
        }
        synchronized(mutex) {
        	if (obj != null) return obj;
            obj = create();
            return obj;
        }
    }
	
	@Override
	public void set(T val) {
		synchronized(mutex) {
			this.obj = val;
		}
	}
	
}
