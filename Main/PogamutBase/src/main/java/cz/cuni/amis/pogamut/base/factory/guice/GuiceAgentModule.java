package cz.cuni.amis.pogamut.base.factory.guice;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AbstractAgent;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.agent.params.IAgentParameters;
import cz.cuni.amis.pogamut.base.agent.utils.runner.impl.AgentRunner;
import cz.cuni.amis.pogamut.base.component.bus.ComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.guice.AdaptableProvider;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScope;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.guice.AgentTeamScoped;
import cz.cuni.amis.pogamut.base.utils.guice.IAgentScope;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.utils.NullCheck;

/**
 * GuiceAgentModule, implementation of {@link AbstractModule}, provides a way to hierarchically specify the bindings
 * for interfaces and classes.
 * <p><p>
 * The module is a place where you assemble the pieces of the dependency puzzle. You're specifying which implementor
 * should be created for specific interface, etc. This sounds good but then you find out that it is somehow hard
 * to override once set bindings.
 * <p><p>
 * The {@link GuiceAgentModule} solves this by providing {@link GuiceAgentModule#addModule(AbstractModule)} method
 * that should be called from {@link GuiceAgentModule#configureModules()} (see their javadocs). The {@link GuiceAgentModule#configureModules()}
 * is meant to be overridden in every descendant of the {@link GuiceAgentModule} where it should call <i>super.configureModules()</i>
 * and than add another module to the queue via {@link GuiceAgentModule#addModule(AbstractModule)}. We're simply collecting
 * respective modules in every <i>configureModules()</i> implementation. These collected modules are than applied inside
 * standard Guice's method {@link GuiceAgentModule#configure()} where they are applied in the order they have been added (that's
 * why you have to call <i>super.configureModules</i> as a first command in the descendants).   
 * <p><p>
 * Additionally, we're introducing {@link AgentScope} under annotation {@link AgentScoped} (concrete scope implementation can
 * be changed by in descendants via overriding {@link GuiceAgentModule#createAgentScope()}) and convenient providers for {@link IAgentId}
 * and {@link IAgentParameters} exposed via {@link GuiceAgentModule#getAgentIdProvider()} and {@link GuiceAgentModule#getAgentParamsProvider()}.
 * <p><p>
 * <p><p>
 * <b>IMPORTANT</b> the {@link GuiceAgentModule} introduces public method {@link GuiceAgentModule#prepareNewAgent(IAgentParameters)} that
 * is meant to configure run-time dependencies inside the module before another agent is instantiated (i.e., for passing run-time
 * parameters such as {@link IAgentParameters}. The method contains only one parameter - PARAMS, therefore it forces you to create
 * new descendants of {@link IAgentParameters} if you want to introduce new run-time parameters (which follows the philosophy that
 * every {@link AbstractAgent} implementation should also defines: 1) own parameters ({@link IAgentParameters} descendants), 2) own module ({@link GuiceAgentModule} descendants), 3) own runners ({@link AgentRunner} descendants).<p>
 * <b>NOTE</b> that this method <b>MUST BE CALLED</b> before the factory creates another agent (but rest assured, it's already done in {@link GuiceAgentFactory} for 
 * you automatically}).
 * <p><p>
 * <p><p>
 * FINALLY the module is providing basic bindings that are always needed for {@link AbstractAgent}
 * <table>
 * <tr><th>Mapped class</th>                    <th>  </th> <th>Target</th>                          <th>Description</th></tr>
 * 
 * <tr><td>{@link IComponentBus}</td>           <td>-></td> <td>{@link ComponentBus}</td>            <td>Agent bus synchronizing starting/stopping/etc. events.</td></tr>
 * <tr><td>{@link IAgentId}</td>                <td>-></td> <td>provided by the {@link GuiceAgentModule#agentIdProvider}.</td>
 *                                                                                                   <td>Id that is provided during runtime, you may use {@link AgentId} implementation of {@link IAgentId}.</td></tr>
 * <tr><td>{@link IAgentParameters}</td>        <td>-></td> <td>provided by the {@link GuiceAgentModule#agentParamsProvider}.</td>
 * <tr><td>{@link IAgentLogger}</td>            <td>-></td> <td>{@link AgentLogger}</td>             <td>Takes care about logging.</td></tr>
 * </table>
 * <p><p>
 * To have <b>successful module</b> the descendant <b>must specify</b> these <b>missing bindings</b>:
 * <table>
 * <tr><th>Mapped class</th>                    <th>Description</th></tr>
 * 
 * <tr><td>{@link IAgent}</td>                  <td>Agent that should be instantiated (preferable descendant of {@link AbstractAgent}.</td></tr>
 * </table>
 * ... plus all newly introduced dependencies (by various implementors of mentioned interfaces).<p>
 * ... <b>don't forget to call super.configureModules()</b> in the subclasses. ;-)
 *  
 * 
 * @author Jimmy
 * @param PARAMS
 */
public class GuiceAgentModule<PARAMS extends IAgentParameters> extends AbstractModule {

	/**
	 * Agent scope used to hold instances annotated with {@link AgentScoped}.
	 */
	private IAgentScope agentScope;

	/**
	 * Agent-team scope used to hold instances annotated with {@link AgentTeamScoped}.
	 */
	private IAgentScope agentTeamScope;
	
	/**
	 * Provider for the {@link IAgentId} run-time dependence.
	 */
	private AdaptableProvider<IAgentId> agentIdProvider = new AdaptableProvider(null);
	
	/**
	 * Provider for the {@link IAgentParameters} run-time dependencies.
	 */
	private AdaptableProvider<PARAMS> agentParamsProvider = new AdaptableProvider(null);
	
	/**
	 * List of modules that are joined together when the module is used to provide the {@link Injector}.
	 */
	private List<Module> modules = new ArrayList<Module>();
		
	/**
	 * Initializes {@link GuiceAgentModule#agentScope} via {@link GuiceAgentModule#createAgentScope()}.
	 */
	public GuiceAgentModule() {
		agentScope = createAgentScope();
		NullCheck.check(this.agentScope, "createAgentScope()");
		agentTeamScope = createAgentTeamScope();
		NullCheck.check(this.agentTeamScope, "createAgentTeamScope()");		
	}
	
	/**
	 * Must be called before another agent instance can be created. It clears the {@link GuiceAgentModule#agentScope}
	 * and binds {@link IAgentParameters#getAgentId()} to the {@link GuiceAgentModule#agentIdProvider}.
	 * <p><p>
	 * Whenever you create your own {@link IAgentParameters} you may need to override this method to utilize your new
	 * run-time dependencies. In such case, always call <i>super.prepareNewAgent(agentParameters)</i> as a first command.
	 * 
	 * @param agentParameters
	 */
	public void prepareNewAgent(PARAMS agentParameters) {
		NullCheck.check(agentParameters, "agentParameters");
		NullCheck.check(agentParameters.getAgentId(), "agentParameters.getAgentId()");
		agentScope.clearScope();
		agentIdProvider.set(agentParameters.getAgentId());
		agentParamsProvider.set(agentParameters);
	}
	
	/**
	 * Adds next modules containing new bindings that extend (and/or override) previous bindings.
	 * <p><p>
	 * Designed to be used from {@link GuiceAgentModule#configureModules()}.
	 * 
	 * @param module
	 */
	protected final void addModule(AbstractModule module) {
		this.modules.add(module);
	}
	
	/**
	 * Meant to introduce new {@link AbstractModule} into the module's queue {@link GuiceAgentModule#modules} via {@link GuiceAgentModule#addModule(AbstractModule)}.
	 * <p><p>
	 * See {@link GuiceAgentModule#configureModules()} source code for the example (utilizes anonymous class instantiation, 
	 * instantiating {@link AbstractModule} where you only have to override {@link AbstractModule#configure()} method where
	 * you use {@link AbstractModule#bind(Class)} method to specify the bindings).
	 */
	protected void configureModules() {		
		addModule(
			new AbstractModule() {
				@Override
				protected void configure() {
					bind(IComponentBus.class).to(ComponentBus.class);
					bind(IAgentId.class).toProvider(getAgentIdProvider());
					bind(IAgentParameters.class).toProvider(getAgentParamsProvider());
					bind(IAgentLogger.class).to(AgentLogger.class);										
				}				
			}
		);
	}
	
	/**
	 * Method called from the {@link GuiceAgentModule#GuiceAgentModule()} to initialize the {@link GuiceAgentModule#agentScope},
	 * override if you need you own {@link AgentScope} implementation.
	 * @return
	 */
	protected IAgentScope createAgentScope() {
		return new AgentScope();
	}
	
	/**
	 * Method called from the {@link GuiceAgentModule#GuiceAgentModule()} to initialize the {@link GuiceAgentModule#agentTeamScope},
	 * override if you need you own {@link AgentScope} implementation.
	 * @return
	 */
	protected IAgentScope createAgentTeamScope() {
		return new AgentScope();
	}
	
	/**
	 * AgentScope that is holding agent-scope-singletons (classes annotated with {@link AgentScoped}).
	 * <p><p>
	 * Use {@link AgentScope#clearScope()} to release the objects thus preparing the scope for the next initialization
	 * of the {@link IAgent} (automatically called from {@link GuiceAgentModule#prepareNewAgent(IAgentParameters)}.
	 * @return
	 */
	public IAgentScope getAgentScope() {
		return agentScope;
	}
	
	/**
	 * AgentTeamScope that is holding agent-team-scope-singletons (classes annotated with {@link AgentTeamScoped}).
	 * <p><p>
	 * Use {@link IAgentScope#clearScope()} to release the objects thus preparing the scope for the next team initialization.
	 * @return
	 */
	public IAgentScope getAgentTeamScope() {
		return agentTeamScope;
	}
	
	/**
	 * Returns a provider for the {@link IAgentId} interface. Use when utilizing descendants of {@link IAgentId}
	 * to provide the same instance for new interface/implementors.
	 * 
	 * @return
	 */
	protected AdaptableProvider<IAgentId> getAgentIdProvider() {
		return agentIdProvider;
	}
	
	/**
	 * Returns a provider for the {@link IAgentParameters} interface. Use when utilizing descendants of {@link IAgentParameters}
	 * to provide the same instace for new interface/implementors.
	 * 
	 * @return
	 */
	protected AdaptableProvider<PARAMS> getAgentParamsProvider() {
		return agentParamsProvider;
	}

	/**
	 * Binds {@link GuiceAgentModule#agentScope} into the module and then it iterates over {@link GuiceAgentModule#modules} and
	 * adds all their bindings to the module - each module always overrides previous ones (uses {@link Modules#override(Module...)}).
	 * <p><p>
	 * The advantage over classical {@link AbstractModule#configure()} method is that you may easily re-bind already bound classes
	 * (which is unachievable by simple subclassing). 
	 */
	@Override
	protected final void configure() {
		configureModules();
		bindScope(AgentScoped.class, this.agentScope);
		bindScope(AgentTeamScoped.class, this.agentTeamScope);
		if (modules.size() == 0) {
			throw new IllegalStateException("There is no module defined, nobody has ever called addModule() method to introduce new bindings for the module.");
		}
		Module actual = modules.get(0);
		for (int i = 1; i < modules.size(); ++i) {
			actual = Modules.override(actual).with(modules.get(i));
		}
		install(actual);
	}
	
}
