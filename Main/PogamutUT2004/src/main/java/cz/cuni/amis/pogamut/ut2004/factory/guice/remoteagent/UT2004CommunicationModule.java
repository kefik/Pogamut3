package cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.name.Names;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.communication.connection.IWorldConnection;
import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.ISocketConnectionAddress;
import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.SocketConnection;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.parser.IWorldMessageParser;
import cz.cuni.amis.pogamut.base.communication.parser.impl.yylex.IYylex;
import cz.cuni.amis.pogamut.base.communication.parser.impl.yylex.IYylexObserver;
import cz.cuni.amis.pogamut.base.communication.translator.IWorldMessageTranslator;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencyType;
import cz.cuni.amis.pogamut.base.factory.guice.GuiceAgentModule;
import cz.cuni.amis.pogamut.base.factory.guice.GuiceRemoteAgentModule;
import cz.cuni.amis.pogamut.base.utils.guice.AdaptableProvider;
import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemTypeTranslator;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemTypeTranslator;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Yylex;
import cz.cuni.amis.pogamut.ut2004.communication.parser.IUT2004Yylex;
import cz.cuni.amis.pogamut.ut2004.communication.parser.UT2004Parser;
import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.ItemTranslator;

/**
 * Module extending {@link RemoteGuiceAgentModule} for the purpose of UT2004 communication specification.
 * <p><p>
 * Newly binded classes:
 * <table>
 * <tr><th>Mapped class</th>                    <th>  </th> <th>Target</th>                          <th>Description</th></tr>
 * 
 * <tr><td>{@link IWorldConnection}</td>        <td>-></td> <td>{@link SocketConnection}</td>        <td>Agent bus synchronizing starting/stopping/etc. events.</td></tr>
 * <tr><td>{@link SocketConnection} dependencies</td>
 *                                              <td>-></td> <td>{@link UT2004CommunicationModule#connectionDependenciesProvider}</td></tr>
 * <tr><td>{@link SocketConnection} address</td><td>-></td> <td>{@link UT2004BotModule#getAddressProvider()</td></tr>                                             
 * <tr><td>{@link IWorldMessageParser}</td>		<td>-></td> <td>{@link UT2004Parser}</td>            <td>Wrapper for the yylex parser of the messages coming from GameBots2004.</td></tr>
 * <tr><td>{@link IYylex}</td>                  <td>-></td> <td>{@link IUT2004Yylex}</td>            <td>Specifying yylex further.</td>
 * <tr><td>{@link IUT2004Yylex}</td>            <td>-></td> <td>{@link Yylex}</td>                   <td>Specifying yylex further.</td>
 * <tr><td>{@link IYylexObserver}</td>          <td>-></td> <td>{@link IYylexObserver.LogObserver}</td>
 *                                                                                                   <td>Yylex observer reporting errors.</td>
 * <tr><td>{@link IUT2004Yylex}</td>            <td>-></td> <td>{@link Yylex}</td>                   <td>Concrete Yylex implementations that parses the messages coming from GameBots2004.</td></tr>
 * <tr><td>{@link ItemTranslator}</td>			<td>-></td>	<td>{@link ItemTranslator}</td>			 <td>Object handling translation of INV messages.</td></tr>
 * <tr><td>{@link UT2004AgentParameters}</td>   <td>-></td> <td>{@link UT2004CommunicationModule#getAgentParamsProvider()}</td>
 *                                                                                                   <td>Agent parameters passed by the factory, contains additional runtime dependencies.</td>
 * </table>
 * <p></p>
 * 
 * </table>
 * To have <b>successful module</b> the descendant <b>must specify</b> these <b>missing bindings</b>:
 * <table>
 * <tr><th>Mapped class</th>                    <th>Description</th></tr>
 * <tr><td>{@link IWorldView}</td>				<td>Binds world view as vision world view.</td></tr>
 * <tr><td>{@link IWorldMessageTranslator}</td>	<td>Protocol-validating translator of {@link InfoMessage}s of GameBots2004.</td></tr>
 * <tr><td>{@link IAgent}</td>                 
 * </table>
 * ... plus all newly introduced dependencies (by various implementors of mentioned interfaces).<p>
 * ... <b>don't forget to call super.configureModules()</b> in the subclasses ;-)
 * 
 * @see GuiceRemoteAgentModule
 * @see GuiceAgentModule
 * @author Jimmy
 */
public class UT2004CommunicationModule<PARAMS extends UT2004AgentParameters> extends GuiceRemoteAgentModule<PARAMS>{

	protected AdaptableProvider<ComponentDependencies> connectionDependenciesProvider = new AdaptableProvider<ComponentDependencies>(null);
	
	/**
	 * Binds runtime dependencies to the module/{@link Injector}.
	 * <p><p>
	 * Must be called before the new agent is instantiated with {@link Injector}.
	 * @param agentId
	 * @param address
	 */
	@Override
	public void prepareNewAgent(PARAMS agentParameters) {
		super.prepareNewAgent(agentParameters);
		connectionDependenciesProvider.set(new ComponentDependencies(ComponentDependencyType.STARTS_WITH).add(agentParameters.getAgentId()));
	}
	
	@Override
	protected void configureModules() {
		super.configureModules();
		addModule(new AbstractModule() {

			@Override
			public void configure() {
				bind(IWorldConnection.class).to(SocketConnection.class);
				bind(ComponentDependencies.class).annotatedWith(Names.named(SocketConnection.CONNECTION_DEPENDENCY)).toProvider(connectionDependenciesProvider);
                bind(ISocketConnectionAddress.class).annotatedWith(Names.named(SocketConnection.CONNECTION_ADDRESS_DEPENDENCY)).toProvider((Provider<ISocketConnectionAddress>) getAddressProvider());
				bind(IWorldMessageParser.class).to(UT2004Parser.class);
				bind(ItemTypeTranslator.class).to(UT2004ItemTypeTranslator.class);
				bind(IYylex.class).to(IUT2004Yylex.class);
				bind(IUT2004Yylex.class).to(Yylex.class);
				bind(IYylexObserver.class).to(IYylexObserver.LogObserver.class);
				bind(UT2004AgentParameters.class).toProvider(getAgentParamsProvider());
			}
			
		});
	}

}
