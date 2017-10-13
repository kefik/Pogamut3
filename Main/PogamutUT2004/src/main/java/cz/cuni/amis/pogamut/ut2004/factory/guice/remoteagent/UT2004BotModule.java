package cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
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
import cz.cuni.amis.pogamut.base3d.agent.IAgent3D;
import cz.cuni.amis.pogamut.base3d.worldview.IVisionWorldView;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.translator.bot.BotFSM;
import cz.cuni.amis.pogamut.ut2004.communication.worldview.UT2004SyncLockableWorldView;

/**
 * Module extending {@link UT2004CommunicationModule} for the purpose of {@link UT2004Bot} instantiation.
 * <p><p>
 * Introduces {@link UT2004BotModule#worldViewDependenciesProvider}.
 * <p><p>
 * Newly binded classes:
 * <table>
 * <tr><th>Mapped class</th>                    <th>  </th> <th>Target</th>                          <th>Description</th></tr>
 * 
 * <tr><td>{@link IWorldMessageTranslator}</td>	<td>-></td>	<td>{@link BotFSM}</td>					 <td>Protocol-validating translator of {@link InfoMessage}s of GameBots2004.</td></tr>
 * <tr><td>{@link IWorldView}</td>				<td>-></td> <td>{@link IVisionWorldView}</td>        <td>Binds world view as vision world view.</td></tr>
 * <tr><td>{@link IVisionWorldView}</td>		<td>-></td> <td>{@link ILockableVisionWorldView}</td><td>Binds vision world view as lockable one.</td></tr>
 * <tr><td>{@link ILockableWorldView}</td>		<td>-></td> <td>{@link ILockableVisionWorldView}</td><td>Binds lockable world view as vision world view.</td></tr>
 * <tr><td>{@link ILockableVisionWorldView}</td><td>-></td> <td>{@link UT2004SyncLockableWorldView}</td> <td>Binds world view with concrete implementation.</td></tr>
 * <tr><td>{@link UT2004SyncLockableWorldView} dependencies</td>
 *                                              <td>-></td> <td>{@link UT2004BotModule#worldViewDependenciesProvider}</td></tr>
 * <tr><td>{@link IAgent}</td>                  <td>-></td> <td>{@link IAgent3D}</td>                <td></td></tr>
 * <tr><td>{@link IAgent3D}</td>                <td>-></td> <td>{@link IUT2004Bot}</td>            <td></td></tr>
 * <tr><td>{@link IUT2004Bot}</td>            <td>-></td> <td>{@link UT2004Bot}</td>               <td>Binds concrete implementation of the agent.</td></tr>
 * 
 * </table>
 * To have <b>successful module</b> the descendant <b>must specify</b> these <b>missing bindings</b>:
 * <table>
 * <tr><th>Mapped class</th>                    <th>Description</th></tr>
 * 
 * <tr><td>{@link IUT2004BotController}</td>    <td>Controller of the bot.</td></tr>
 * </table>
 * ... plus all newly introduced dependencies (by various implementors of mentioned interfaces).<p>
 * ... <b>don't forget to call super.configureModules()</b> in the subclasses ;-)
 * 
 * @see UT2004CommunicationModule
 * @see GuiceRemoteAgentModule
 * @see GuiceAgentModule
 * @author Jimmy
 */
public class UT2004BotModule<PARAMS extends UT2004BotParameters> extends UT2004CommunicationModule<PARAMS> {

	/**
	 * Dependency provider for the world view, so the world view know when to start.
	 */
	protected AdaptableProvider<ComponentDependencies> worldViewDependenciesProvider = new AdaptableProvider<ComponentDependencies>(null);
	
	private Class<? extends IUT2004BotController> botControllerClass;
	
	protected UT2004BotModule() {
	}
	
	public UT2004BotModule(Class<? extends IUT2004BotController> botControllerClass) {
		this.botControllerClass = botControllerClass;
	}

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
				bind(IWorldMessageTranslator.class).to(BotFSM.class);
				bind(IWorldView.class).to(IVisionWorldView.class);
				bind(IVisionWorldView.class).to(ILockableVisionWorldView.class);
				bind(ILockableWorldView.class).to(ILockableVisionWorldView.class);
				bind(ILockableVisionWorldView.class).to(UT2004SyncLockableWorldView.class);
				bind(ComponentDependencies.class).annotatedWith(Names.named(UT2004SyncLockableWorldView.WORLDVIEW_DEPENDENCY)).toProvider(worldViewDependenciesProvider);
				bind(IAgent.class).to(IAgent3D.class);
				bind(IAgent3D.class).to(IUT2004Bot.class);
				bind(IUT2004Bot.class).to(UT2004Bot.class);
				if (botControllerClass != null) {
					bind(IUT2004BotController.class).to(botControllerClass);
				}
				bind(UT2004BotParameters.class).toProvider((Provider<? extends UT2004BotParameters>) getAgentParamsProvider());
			}
			
		});
	}

}
