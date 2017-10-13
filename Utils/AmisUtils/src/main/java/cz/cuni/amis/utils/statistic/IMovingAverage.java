package cz.cuni.amis.utils.statistic;

/**
 * Represents interface to the moving average value.
 * 
 * @author Jimmy
 *
 * @param <TYPE>
 */
public interface IMovingAverage<TYPE> {

	/**
	 * Add another item into the moving average.
	 * @param item
	 */
	public void add(TYPE item);
	
	/**
	 * Returns an average of all items stored.
	 * <p><p>
	 * Returns null if no values are stored.
	 * @return
	 */
	public TYPE getAverage();
	
	/**
	 * Return current number of items that are used to compute the average returned
	 * via {@link IMovingAverage#getAverage()}. 
	 * @return
	 */
	public int getCurrentLength();
	
	/**
	 * Return max number of consecutive items (added via {@link IMovingAverage#add(Object)} that are used to
	 * compute the average returned via {@link IMovingAverage#getAverage()}. 
	 * @return
	 */
	public int getMaxLength();

	/**
	 * Sets number of items that the object requires for the computing of the average.
	 * (Note that computation is done all the time, but when there is enough items == set number,
	 * the {@link IMovingAverage#isEnoughValues()} reports true.)
	 * 
	 * @param length
	 */
	public void setMaxLength(int length);
	
	/**
	 * Whether the object has enough values to compute the avarage according to the max numbers it
	 * may store (returns {@link IMovingAverage#getCurrentLength()} == {@link IMovingAverage#getMaxLength()}.
	 * 
	 * @returns whether you may use {@link IMovingAverage#getAverage()} with the result that is desirable (i.e., the object has enough
	 *          items to compute the average from) 
	 */
	public boolean isEnoughValues();
	
	/**
	 * Resets the object -> it removes all items stored. The {@link IMovingAverage#getAverage()} will return
	 * null after the call.
	 */
	public void reset();
	
	
}
