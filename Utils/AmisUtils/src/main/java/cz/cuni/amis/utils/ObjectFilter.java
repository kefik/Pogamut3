package cz.cuni.amis.utils;

/**
 * @param <T>
 * 
 * @deprecated use {@link IFilter} instead!
 */
public interface ObjectFilter<T> {
	
	public boolean accept(T obj);

}
