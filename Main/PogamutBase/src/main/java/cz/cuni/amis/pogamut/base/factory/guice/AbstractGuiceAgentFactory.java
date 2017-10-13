package cz.cuni.amis.pogamut.base.factory.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;

import cz.cuni.amis.utils.NullCheck;

/**
 * Abstract Guice-based factory that uses {@link GuiceAgentModule} for the bindings.
 * <p><p>
 * It simply wraps the Guice's {@link Injector}, which it creates based on the passed module.
 * <p><p>
 * The module can be reset using {@link AbstractGuiceAgentFactory#setAgentModule(GuiceAgentModule)}.
 * 
 * @author Jimmy
 */
public abstract class AbstractGuiceAgentFactory {
	
	/**
	 * Custom module used to initialize the {@link AbstractGuiceAgentFactory#injector}.
	 */
	private GuiceAgentModule module;
	
	/**
	 * Injector created using {@link AbstractGuiceAgentFactory#module}. Lazy-initialized inside {@link AbstractGuiceAgentFactory#getInjector()}.
	 */
	private Injector injector;

	/**
	 * Parameter-less constructor that can be utilized in situations when you have to set the {@link GuiceAgentModule}
	 * later. (DO NOT FORGET TO DO IT VIA {@link AbstractGuiceAgentFactory#setAgentModule(GuiceAgentModule)} :-)
	 */
	public AbstractGuiceAgentFactory() {
	}
	
	/**
	 * Creates a Guice-based factory that will use {@link Injector} created using the 'module'.
	 * 
	 * @param module module that configures bindings between classes, may be null (specify module later using {@link AbstractGuiceAgentFactory#setAgentModule(GuiceAgentModule)})
	 */
	public AbstractGuiceAgentFactory(GuiceAgentModule module) {
		this.module = module;
	}
	
	/**
	 * Returns the module that the factory is working with. Can be utilized to slip run-time dependencies
	 * into the module.
	 * 
	 * @return factory module
	 */
	protected GuiceAgentModule getAgentModule() {
		return module;
	}
	
	/**
	 * Sets new agent module into the factory, it invalidates the {@link AbstractGuiceAgentFactory#injector}
	 * so the new one is created when {@link AbstractGuiceAgentFactory#getInjector()} is called.
	 * 
	 * @param module new module
	 */
	protected synchronized void setAgentModule(GuiceAgentModule module) {
		if (module == this.module) return;
		this.module = module;
		this.injector = null;
	}
	
	/**
	 * Injector that should be used to instantiates new objects according to the module.
	 * <p><p> 
	 * Lazy-initialized using {@link GuiceAgentFactory#getAgentModule()}.
	 * 
	 * @return injector configured by {@link GuiceAgentFactory#getAgentModule()}
	 */
	protected synchronized Injector getInjector() {
		if (this.injector == null) {
			GuiceAgentModule module = getAgentModule();
			NullCheck.check(module, "getAgentModule()");
			this.injector = Guice.createInjector(module);
			NullCheck.check(this.injector, "Guice.createInjector(getAgentModule())");
		}
		return this.injector;
	}

}
