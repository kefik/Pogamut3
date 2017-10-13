package cz.cuni.amis.pogamut.base.factory.guice;

import com.google.inject.Injector;
import com.google.inject.Provider;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.params.IAgentParameters;
import cz.cuni.amis.pogamut.base.factory.IAgentFactory;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Guice-based {@link IAgent} factory that instantiates the agent according to the bindigs that are found inside
 * the {@link GuiceAgentModule}, which is provided during the construction.
 * <p><p>
 * Additionally to the classic {@link Injector#getInstance(Class)} method, the class offers a place where to
 * slip runtime dependencies. I.e., if you are unable to specify some dependencies during class coding, you need
 * to create {@link Provider}s for the runtime dependency (such as desired {@link IAgentId} for the {@link IAgent}).
 * These dependencies are injected into the module via the method {@link GuiceAgentFactory#configureModule(IAgentParameters)}
 * that is called before the new instance is created.
 * <p><p>
 * Additionally, the factory method {@link GuiceAgentFactory#newAgent(IAgentParameters)} clears the agent scope
 * that is defined by the {@link GuiceAgentModule}.
 * <p><p>
 * <b>NOTE:</b> if you are going to extend the implementation, than the only place that should suffice you for hacking is
 * {@link GuiceAgentFactory#configureModule(IAgentParameters)} where you slip runtime dependencies that the base {@link GuiceAgentFactory}
 * is unaware of, taking these dependencies from your custom {@link IAgentParameters} implementation.
 * <p><p>
 * <b>NOTE:</b> you might not need to override the {@link GuiceAgentFactory#configureModule(IAgentParameters)} as the
 * module configuration might be also done inside {@link GuiceAgentModule#prepareNewAgent(IAgentParameters)} which
 * is implicitly called from the {@link GuiceAgentFactory#configureModule(IAgentParameters)}.
 * <p><p>
 * <b>THREAD-SAFE</b>
 * 
 * @author Jimmy
 *
 * @param <AGENT>
 * @param <PARAMS>
 */
public class GuiceAgentFactory<AGENT extends IAgent, PARAMS extends IAgentParameters> extends AbstractGuiceAgentFactory implements IAgentFactory<AGENT, PARAMS> {
	
	/**
	 * Creates a Guice-based factory that will use {@link Injector} created using the 'module'.
	 * <p><p>
	 * The module MUST specify bindings for the {@link IAgent} interface as that is what's going to be
	 * instantiated using the injector from {@link GuiceAgentFactory#getInjector()}.
	 * 
	 * @param module module that configures bindings between classes, may be null (specify module later using {@link AbstractGuiceAgentFactory#setAgentModule(GuiceAgentModule)})
	 */
	public GuiceAgentFactory(GuiceAgentModule module) {
		super(module);
	}

	/**
	 * Called from within the {@link GuiceAgentFactory#newAgent(IAgentParameters)} to configure the {@link GuiceAgentFactory#getAgentModule()}
	 * with variables from 'agentParams'.
	 * <p><p>
	 * Just calls {@link GuiceAgentModule#prepareNewAgent(IAgentParameters)}.
	 * <p><p>
	 * NOTE: You will probably need to override this method in subclasses. If you do - do not forget to call
	 * super.configureModule(agentParameters) first! So other runtime-dependencies can be set too.
	 * 
	 * @param agentParameters
	 */
	protected void configureModule(PARAMS agentParameters) {
		getAgentModule().prepareNewAgent(agentParameters);
	}
	
	/**
	 * Creates a new instance of the {@link IAgent} interface that is cast to AGENT parameter.
	 * <p><p>
	 * Firstly, it calls {@link GuiceAgentFactory#configureModule(IAgentParameters)} to configure run-time
	 * dependencies of the module and prepare it for the new agent instance, secondly, 
	 * it instantiates a new {@link IAgent} instance.
	 * <p><p>
	 * NOTE: that the {@link GuiceAgentFactory} must be correctly instantiated, i.e., 
	 * the module passed into the constructor must bind {@link IAgent} interface to the 
	 * AGENT (or descendant) class, otherwise you may experience {@link ClassCastException}.
	 * 
	 * @param agentParameters
	 * @return agent instance configured with 'agentParameters'
	 */
	@Override
	public synchronized AGENT newAgent(PARAMS agentParameters) throws PogamutException {
		// configure the module with passed parameters
		configureModule(agentParameters);
		// instantiate the agent
		return (AGENT) getInjector().getInstance(IAgent.class);
	}
}
