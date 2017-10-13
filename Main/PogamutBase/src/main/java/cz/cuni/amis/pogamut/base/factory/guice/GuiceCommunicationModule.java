package cz.cuni.amis.pogamut.base.factory.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.params.IAgentParameters;
import cz.cuni.amis.pogamut.base.communication.command.ICommandSerializer;
import cz.cuni.amis.pogamut.base.communication.command.impl.StringCommandSerializer;
import cz.cuni.amis.pogamut.base.communication.connection.IWorldConnection;
import cz.cuni.amis.pogamut.base.communication.connection.IWorldReaderProvider;
import cz.cuni.amis.pogamut.base.communication.connection.IWorldWriterProvider;
import cz.cuni.amis.pogamut.base.communication.mediator.IMediator;
import cz.cuni.amis.pogamut.base.communication.mediator.impl.Mediator;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.parser.IWorldMessageParser;
import cz.cuni.amis.pogamut.base.communication.parser.impl.yylex.IYylex;
import cz.cuni.amis.pogamut.base.communication.parser.impl.yylex.IYylexObserver;
import cz.cuni.amis.pogamut.base.communication.translator.IWorldMessageTranslator;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEventOutput;
import cz.cuni.amis.pogamut.base.communication.translator.impl.WorldMessageTranslator;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldChangeEventInput;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;

/**
 * Base GaviaLib Guice module that covers the simple bindings for Pogamut's communication chain.
 * <p><p>
 * <table>
 * <tr><th>Mapped class</th>                    <th>  </th> <th>Target</th>                          <th>Description</th></tr>
 * 
 * <tr><td>{@link IWorldReaderProvider}</td>    <td>-></td> <td.{@link IWorldConnection}</td>        <td>Reader of the world's information.</td></tr>
 * <tr><td>{@link IWorldWriterProvider}</td>    <td>-></td> <td.{@link IWorldConnection}</td>        <td>Writer that sends commands to the agent's body in the world.</td></tr>
 * <tr><td>{@link ICommandSerializer}</td>      <td>-></td> <td>{@link StringCommandSerializer}</td> <td>Serializes commands using .toString() method.</td></tr>
 * <tr><td>{@link IWorldChangeEventOutput}</td> <td>-></td> <td>{@link WorldMessageTranslator}</td>  <td>Translator of {@link InfoMessage}s into {@link IWorldChangeEvent}s. Relies on the wrapped (world-dependent) implementation of {@link IWorldMessageTranslator}</td></tr>
 * <tr><td>{@link IMediator}</td>               <td>-></td> <td>{@link Mediator}</td>                <td>Thread-wrapper, reader of {@link IWorldChangeEventOutput} that passes {@link IWorldChangeEvent} into {@link IWorldChangeEventInput}.</td></tr>
 * <tr><td>{@link IWorldChangeEventInput}</td>  <td>-></td> <td>{@link IWorldView}</td>              <td>Consumer of {@link IWorldChangeEvent}s.</td></tr> 
 * </table>
 * <p><p>
 * To have <b>successful module</b> the descendant <b>must specify</b> these <b>missing bindings</b>:
 * <table>
 * <tr><th>Mapped class</th>                    <th>Description</th></tr>
 * 
 * <tr><td>{@link IAgent}</td>                  <td>Agent that should be instantiated</td></tr>
 * <tr><td>{@link IWorldConnection}</td>        <td>Connection to the agent's world.</td></tr>
 * <tr><td>{@link IWorldMessageParser}</td>     <td>Line oriented parser based on Yylex.</td></tr>
 * <tr><td>{@link IYylex}</td>                  <td>World message parser implementation.</td></tr>
 * <tr><td>{@link IYylexObserver}</td>          <td>Yylex observer reporting errors.</td></tr>
 * <tr><td>{@link IWorldMessageTranslator}</td> <td>World-dependent implementation of {@link InfoMessage}s translator into {@link IWorldChangeEvent} that can be consumed by {@link IWorldView}.</td></tr>
 * <tr><td>{@link IWorldView</td>               <td>World view processing {@link IWorldChangeEvent}s into {@link IWorldEvent}s that should be consumed by {@link IAgent} implementation.</td></tr>
 * </table>
 * ... plus all newly introduced dependencies (by various implementors of mentioned interfaces).<p>
 * ... <b>don't forget to call super.configureModules()</b> in the subclasses. ;-)
 * <p><p>
 * If you want to bind custom (your own) class to one of the interface that is already binded (meaning you need to alter GaviaLib), do it this way:
 * <ol>
 * <li>BE TOTALY SURE WHAT YOU'RE DOING :-) or it will fail horribly or you may cripple the IDE...</li>
 * <li>Copy-paste (YES, COPY-PASTE IS THE BEST WAY) the implementation of the class you want to change and alter it to suits your needs.</li>
 * <li>Always make those classes <b>AgentScoped</b>!</li>
 * <li>Create new module where you re-specify the binding of desired interface.</li> 
 * </ol> 
 * 
 * @author Jimmy
 * @param PARAMS
 */
public class GuiceCommunicationModule<PARAMS extends IAgentParameters> extends GuiceAgentModule<PARAMS> {

	/**
	 * Override to create new module with your own bindings adding it into {@link GuiceCommunicationModule#modules} using {@link GuiceCommunicationModule#addModule(Module)}.
	 * <p><p>
	 * See {@link GuiceCommunicationModule#configureModules()} source code for the example (utilizes anonymous class, instantiating {@link AbstractModule}).
	 */
	protected void configureModules() {
		super.configureModules();
		addModule(
			new AbstractModule() {
				@Override
				protected void configure() {
					bind(IWorldChangeEventOutput.class).to(WorldMessageTranslator.class);
					bind(IMediator.class).to(Mediator.class);
					bind(IWorldChangeEventInput.class).to(IWorldView.class);
				}				
			}
		);
	}

}
