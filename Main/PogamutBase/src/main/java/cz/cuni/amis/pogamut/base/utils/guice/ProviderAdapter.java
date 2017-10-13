package cz.cuni.amis.pogamut.base.utils.guice;

import com.google.inject.Provider;

/**
 * Adapts the value from some {@link Provider} to by of type "T".
 * <p><p>
 * Usually not needed, as you can cast the provider to a provider of different type.
 * 
 * @author Jimmy
 *
 * @param <T>
 */
public class ProviderAdapter<T> implements Provider<T> {
	
	private Provider provider;

	public ProviderAdapter(Provider wrappedProvider) {
		this.provider = wrappedProvider;
	}

	@Override
	public T get() {
		return (T) provider.get();
	}

}
