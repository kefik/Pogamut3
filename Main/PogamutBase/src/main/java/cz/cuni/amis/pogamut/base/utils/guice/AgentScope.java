package cz.cuni.amis.pogamut.base.utils.guice;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Key;
import com.google.inject.Provider;

/**
 * Scope used during the construction of the agent - treating all new instances (of classes annotated with {@link AgentScoped})
 * as singletons.
 * <p><p>
 * After instantiation of the agent, Pogamut calls clearScope() to release all
 * references to instantiated objects that allows to reuse the injector again
 * to get another instance of the agent.
 * <p><p>
 * We need this, because we're usually have one connection object that must be injected
 * into two different classes thus must be treated "as a singleton for one instantiation
 * of the agent". (This is not the only example...)
 * <p><p>
 * Every pogamut.base class is {@link AgentScoped}.
 * 
 * @author Jimmy
 *
 */
public class AgentScope implements IAgentScope {
	
	public static class SingletonProvider<T> implements Provider<T> {
		
		private Provider<T> creator;
		
		private T singleton = null;
		
		public SingletonProvider(Provider<T> creator) {
			this.creator = creator;
		}

		@Override
		public T get() {
			if (singleton != null) {
				return singleton;
			}
			singleton = creator.get();
			return singleton;
		}
		
		public void clear() {
			singleton = null;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private Map<Key, Provider> providers = new HashMap<Key, Provider>(); 

	@SuppressWarnings("unchecked")
	@Override
	public <T> Provider scope(Key<T> key, Provider<T> creator) {
		synchronized(providers) {
			Provider p = providers.get(key);
			if (p != null) { 
				return p;
			}
			SingletonProvider<T> provider = new SingletonProvider<T>(creator);
			providers.put(key, provider);
			return provider;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void clearScope() {
		synchronized(providers) {
			for (Provider provider : providers.values()) {
				((SingletonProvider)provider).clear();
			}
		}
	}

}