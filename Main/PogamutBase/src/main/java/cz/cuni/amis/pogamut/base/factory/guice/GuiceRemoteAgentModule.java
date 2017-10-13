package cz.cuni.amis.pogamut.base.factory.guice;

import com.google.inject.AbstractModule;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.params.IRemoteAgentParameters;
import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.command.ICommandSerializer;
import cz.cuni.amis.pogamut.base.communication.command.impl.Act;
import cz.cuni.amis.pogamut.base.communication.command.impl.StringCommandSerializer;
import cz.cuni.amis.pogamut.base.communication.connection.IWorldConnection;
import cz.cuni.amis.pogamut.base.communication.connection.IWorldConnectionAddress;
import cz.cuni.amis.pogamut.base.communication.connection.IWorldReaderProvider;
import cz.cuni.amis.pogamut.base.communication.connection.IWorldWriterProvider;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.parser.impl.yylex.IYylex;
import cz.cuni.amis.pogamut.base.communication.translator.IWorldMessageTranslator;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;
import cz.cuni.amis.pogamut.base.utils.guice.AdaptableProvider;
import cz.cuni.amis.utils.NullCheck;

/**
 * Module extending {@link GuiceAgentModule} for the purpose of remote agents (those communicating with the world using 
 * {@link IWorldConnection}).
 * <p><p>
 * Introducing {@link GuiceRemoteAgentModule#getAddressProvider()} that allows you to specify {@link IWorldConnectionAddress} 
 * at runtime.
 * <p><p>
 * To have <b>successful module</b> the descendant <b>must specify</b> these <b>missing bindings</b>:
 * <table>
 * <tr><th>Mapped class</th>                    <th>Description</th></tr>
 * 
 * <tr><td>{@link IAgent}</td>                  <td>Agent that should be instantiated</td></tr>
 * <tr><td>{@link IWorldConnection}</td>        <td>Connection to the agent's world.</td></tr>
 * <tr><td>{@link IYylex}</td>                  <td>World message parser implementation.</td></tr>
 * <tr><td>{@link IWorldMessageTranslator}</td> <td>World-dependent implementation of {@link InfoMessage}s translator into {@link IWorldChangeEvent} that can be consumed by {@link IWorldView}.</td></tr>
 * <tr><td>{@link IWorldView</td>               <td>World view processing {@link IWorldChangeEvent}s into {@link IWorldEvent}s that should be consumed by {@link IAgent} implementation.</td></tr>
 * </table>
 * ... plus all newly introduced dependencies (by various implementors of mentioned interfaces).<p>
 * ... <b>don't forget to call super.configure()</b> in the subclasses ;-)
 * 
 * <b>NOTE></b> that the module is defining bindings for {@link IWorldReaderProvider} and {@link IWorldWriterProvider} which
 * might not be suitable for {@link INativeAgentFactory}s - fear not as those bindings might be rebind thanks to Guice v2. 
 * 
 * @see GuiceAgentModule
 * @author Jimmy
 */
public abstract class GuiceRemoteAgentModule<PARAMS extends IRemoteAgentParameters> extends GuiceCommunicationModule<PARAMS> {
	
	private AdaptableProvider<IWorldConnectionAddress> addressProvider = new AdaptableProvider<IWorldConnectionAddress>(null);
	
	protected AdaptableProvider getAddressProvider() { // return value can't be strongly typed - NetBeans freaks out in a certain type of use
		return addressProvider;
	}
	
	public void prepareNewAgent(PARAMS agentParameters) {
		super.prepareNewAgent(agentParameters);
		NullCheck.check(agentParameters.getWorldAddress(), "agentParameters.getWorldAddress()");
		addressProvider.set(agentParameters.getWorldAddress());
	};
	
	@Override
	protected void configureModules() {
		super.configureModules();
		
		addModule(
				new AbstractModule() {

					@Override
					protected void configure() {
						bind(ICommandSerializer.class).to(StringCommandSerializer.class);
						bind(IAct.class).to(Act.class);
						bind(IWorldReaderProvider.class).to(IWorldConnection.class);
						bind(IWorldWriterProvider.class).to(IWorldConnection.class);
					}				
					
				}
			);		
	}

}