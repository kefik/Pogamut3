package cz.cuni.amis.utils.statistic;

/**
 * Returns an average of all items passed.
 * 
 * @author Jimmy
 *
 * @param <TYPE>
 */
public interface IAveragator<TYPE> {

	/**
	 * Returns an average of all items passed.
	 * 
	 * @param items to make average from
	 * @return average of all 'items'
	 */
	public TYPE getAverage(TYPE[] items);
	
}
