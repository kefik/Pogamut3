package cz.cuni.amis.pogamut.base.utils.guice;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Key;
import com.google.inject.Provider;

/**
 * Scope used during the construction of the whole team of agents - treating all new instances (of classes annotated with {@link AgentTeamScoped})
 * as singletons for the purpose of construction of the team of agents.
 * <p><p>
 * Not all pogamut.base class is {@link AgentTeamScoped}.
 * 
 * @author Jimmy
 */
public class AgentTeamScope implements IAgentScope {
	
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