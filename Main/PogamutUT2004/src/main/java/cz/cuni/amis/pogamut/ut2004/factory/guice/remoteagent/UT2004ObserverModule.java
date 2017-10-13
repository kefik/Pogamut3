package cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.translator.IWorldMessageTranslator;
import cz.cuni.amis.pogamut.base.communication.worldview.ILockableWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencyType;
import cz.cuni.amis.pogamut.base.factory.guice.GuiceAgentModule;
import cz.cuni.amis.pogamut.base.factory.guice.GuiceRemoteAgentModule;
import cz.cuni.amis.pogamut.base.utils.guice.AdaptableProvider;
import cz.cuni.amis.pogamut.base3d.ILockableVisionWorldView;
import cz.cuni.amis.pogamut.base3d.worldview.IVisionWorldView;
import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;
import cz.cuni.amis.pogamut.ut2004.communication.translator.observer.ObserverFSM;
import cz.cuni.amis.pogamut.ut2004.communication.worldview.UT2004LockableWorldView;
import cz.cuni.amis.pogamut.ut2004.communication.worldview.UT2004WorldView;
import cz.cuni.amis.pogamut.ut2004.observer.IUT2004Observer;
import cz.cuni.amis.pogamut.ut2004.observer.impl.UT2004Observer;

/**
 * Module extending {@link UT2004CommunicationModule} for the purpose of {@link UT2004Observer} instantiation.
 * <p><p>
 * Introduces {@link UT2004ObserverModule#worldViewDependenciesProvider}.
 * <p><p>
 * Newly binded classes:
 * <table>
 * <tr><th>Mapped class</th>                    <th>  </th> <th>Target</th>                          <th>Description</th></tr>
 * 
 * <tr><td>{@link IWorldMessageTranslator}</td>	<td>-></td>	<td>{@link ObserverFSM}</td>			 <td>Protocol-validating translator of {@link InfoMessage}s of GameBots2004.</td></tr>
 * <tr><td>{@link IWorldView}</td>				<td>-></td> <td>{@link IVisionWorldView}</td>        <td>Binds world view as vision world view.</td></tr>
 * <tr><td>{@link IVisionWorldView}</td>		<td>-></td> <td>{@link ILockableVisionWorldView}</td><td>Binds vision world view as lockable one.</td></tr>
 * <tr><td>{@link ILockableWorldView}</td>		<td>-></td> <td>{@link ILockableVisionWorldView}</td><td>Binds lockable world view as vision world view.</td></tr>
 * <tr><td>{@link ILockableVisionWorldView}</td><td>-></td> <td>{@link UT2004LockableWorldView}</td> <td>Binds world view with concrete implementation.</td></tr>
 * <tr><td>{@link UT2004LockableWorldView} dependencies</td>
 *                                              <td>-></td> <td>{@link UT2004ObserverModule#worldViewDependenciesProvider}</td></tr>
 * <tr><td>{@link IAgent}</td>                  <td>-></td> <td>{@link IUT2004Observer}</td>         <td></td></tr>
 * <tr><td>{@link IUT2004Observer}</td>         <td>-></td> <td>{@link UT2004Observer}</td>          <td>Binds concrete implementation of the observer.</td></tr>
 * 
 * </table>
 * To have <b>successful module</b> the descendant <b>must specify</b> these <b>missing bindings</b>:
 * <table>
 * <tr><th>Mapped class</th>                    <th>Description</th></tr>
 * <tr><td>nothing</td></tr>
 * </table>
 * ... but all newly introduced dependencies (by various implementors of mentioned interfaces).<p>
 * ... <b>don't forget to call super.configureModules()</b> in the subclasses ;-)
 * 
 * @see UT2004CommunicationModule
 * @see GuiceRemoteAgentModule
 * @see GuiceAgentModule
 * @author Jimmy
 */
public class UT2004ObserverModule<PARAMS extends UT2004AgentParameters> extends UT2004CommunicationModule<PARAMS> {

	/**
	 * Dependency provider for the world view, so the world view know when to start.
	 */
	protected AdaptableProvider<ComponentDependencies> worldViewDependenciesProvider = new AdaptableProvider<ComponentDependencies>(null);
	
	@Override
	public void prepareNewAgent(PARAMS agentParameters) {
		super.prepareNewAgent(agentParameters);
		worldViewDependenciesProvider.set(new ComponentDependencies(ComponentDependencyType.STARTS_WITH).add(agentParameters.getAgentId()));
	}
	
	@Override
	protected void configureModules() {
		super.configureModules();
		addModule(new AbstractModule() {

			@Override
			public void configure() {
				bind(IWorldMessageTranslator.class).to(ObserverFSM.class);
				bind(IWorldView.class).to(IVisionWorldView.class);
				bind(IVisionWorldView.class).to(UT2004WorldView.class);
				bind(ComponentDependencies.class).annotatedWith(Names.named(UT2004WorldView.WORLDVIEW_DEPENDENCY)).toProvider(worldViewDependenciesProvider);
				bind(IAgent.class).to(IUT2004Observer.class);
				bind(IUT2004Observer.class).to(UT2004Observer.class);				
			}
			
		});
	}

}