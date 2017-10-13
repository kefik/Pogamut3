package cz.cuni.amis.utils;

/**
 * Utility class for lazy initialization of objects.
 * <p><p>
 * THREAD-UNSAFE!
 * 
 * @author ik
 */
public abstract class Lazy<T> {

    protected T obj = null;

    /**
     * Creates lazy initialized object.
     * @return
     */
    abstract protected T create();

    /**
     * @deprecated
     * @return value created by {@link Lazy#create()} (UNSYNCHRONIZED CREATION!)
     */
    public T getVal() {
        if (obj == null) {
            obj = create();
        }
        return obj;
    }
    
    /**
     * Synonym for {@link Lazy#getVal()}.
     * @return
     */
    public T get() {
        return getVal();
    }
    
    /**
     * Sets value that should be returned via {@link Lazy#get()}.
     * @param val
     */
    public void set(T val) {
    	this.obj = val;
    }
}
