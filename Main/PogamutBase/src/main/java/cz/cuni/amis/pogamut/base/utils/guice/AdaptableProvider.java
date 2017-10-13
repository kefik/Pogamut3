package cz.cuni.amis.pogamut.base.utils.guice;

import com.google.inject.Provider;

/**
 * Simple implementation of the Guice {@link Provider} interface that allows you to
 * set the value directly into the provider via {@link AdaptableProvider#set(Object)}.
 * <p><p>
 * This class is meant to be used by agent factories that has to preconfigure some providers
 * before they can instantiate a new agent.
 * 
 * @author Jimmy
 *
 * @param <T>
 */
public class AdaptableProvider<T> implements Provider<T> {
	
	/**
	 * Current object the provider is holding/returning
	 */
	private T value;
	
	/**
	 * Creates a provider with 'null' initial value.
	 */
	public AdaptableProvider() {
		value = null;
	}
	
	/**
	 * Creates a provider with initial value.
	 * 
	 * @param initialProvidedValue
	 */
	public AdaptableProvider(T initialProvidedValue) {
		this.value = initialProvidedValue;
	}
	
	/**
	 * Sets the provided value to 'value'.
	 * @param value
	 */
	public void set(T value) {
		this.value = value;
	}

	@Override
	public T get() {
		return value;
	}
	
}