package cz.cuni.amis.utils;

/**
 * Merge arrays.
 * <p><p>
 * Solution from http://forum.java.sun.com/thread.jspa?threadID=202127&messageID=676603
 * 
 * @author ik
 */
public class ArrayMerger {
    
    /** Creates a new instance of ArrayMerger */
    public ArrayMerger() {
    }
    
    /**
     * Merge multiple arrays.
     */
    @SuppressWarnings ("unchecked")
    public static <T> T[] merge(T[]... arrays) {
        int count = 0;
        for (T[] array : arrays) {
            count += array.length;
        }
        // create new array
        T[] rv = (T[]) new Object[count];// Array.newInstance(arrays[0][0].getClass(),count);

        int start = 0;
        for (T[] array : arrays) {
            System.arraycopy(array,0,rv,start,array.length);
            start += array.length;
        }
        return (T[]) rv;
    }

}
