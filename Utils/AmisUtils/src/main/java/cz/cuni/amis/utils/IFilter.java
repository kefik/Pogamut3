package cz.cuni.amis.utils;

/**
 * Simple iterface for filtering objects of arbitrary types. 
 * <p><p>
 * Usually used in some forms of for/while cycles that determines acceptability of objects they iterate over.
 * 
 * @author Jimmy
 *
 * @param <T>
 */
public interface IFilter<T> {
	
	/**
	 * Returns true if the 'object' if accepted for further computation
	 * @param object object to be examined
	 * @return acceptability of the 'object'
	 */
	public boolean isAccepted(T object);

}
