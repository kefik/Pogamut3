package cz.cuni.amis.pogamut.ut2004.utils;

import java.util.List;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.params.IAgentParameters;
import cz.cuni.amis.pogamut.base.agent.utils.runner.impl.AgentRunner;
import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.SocketConnectionAddress;
import cz.cuni.amis.pogamut.base.factory.IAgentFactory;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.PogamutPlatform;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004BotFactory;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004BotModule;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Class used for creating, connecting and starting servers with default
 * settings that are taken from the properties.
 * <p>
 * <p>
 * The address where the instances will connect are defined either in the
 * constructor or taken from the properties of the {@link PogamutPlatform}.
 * <p>
 * <p>
 * For more information about the class see {@link AgentRunner}.
 * 
 * @author ik
 * @author Jimmy
 * 
 * @param <BOT>
 * @param <PARAMS>
 */
public class UT2004BotRunner<BOT extends IUT2004Bot, PARAMS extends UT2004BotParameters>
		extends UTBotRunner<BOT, PARAMS> {

	/**
	 * Construct the runner + specify all defaults.
	 * 
	 * @param factory
	 *            to be used for creating new {@link IUT2004Bot} instances
	 * @param name
	 *            default name that serve as a basis for {@link IAgentId}
	 * @param host
	 *            default host where the instances are going to be connected
	 * @param port
	 *            default port where the instances are going to be connected
	 */
	public UT2004BotRunner(IAgentFactory<BOT, PARAMS> factory, String name,
			String host, int port) {
		super(factory, name, host, port);
	}

	/**
	 * Construct the runner + specify the default name, host:port will be taken
	 * from the Pogamut platform properties.
	 * 
	 * @param factory
	 *            factory to be used for creating new {@link IUT2004Bot}
	 *            instances
	 * @param log
	 *            used to log stuff
	 * @param name
	 *            default name that serve as a basis for {@link IAgentId}
	 */
	public UT2004BotRunner(IAgentFactory<BOT, PARAMS> factory, String name) {
		super(factory, name);
	}

	/**
	 * Construct the runner without specifying anything as default. Default name
	 * for bots will be "UT2004Bot" and host:port will be taken from the Pogamut
	 * platform properties.
	 * 
	 * @param factory
	 *            factory to be used for creating new {@link IUT2004Bot}
	 *            instances
	 */
	public UT2004BotRunner(IAgentFactory<BOT, PARAMS> factory) {
		this(factory, "UT2004Bot");
	}

	/**
	 * Construct the runner + specify all defaults.
	 * 
	 * @param module
	 *            Guice module that is going to be used by the
	 *            {@link UT2004BotFactory}
	 * @param name
	 *            default name that serve as a basis for {@link IAgentId}
	 * @param host
	 *            default host where the instances are going to be connected
	 * @param port
	 *            default port where the instances are going to be connected
	 */
	public UT2004BotRunner(UT2004BotModule module, String name, String host,
			int port) {
		this(new UT2004BotFactory<BOT, PARAMS>(module), name, host, port);
	}

	/**
	 * Construct the runner + specify the default name, host:port will be taken
	 * from the Pogamut platform properties.
	 * 
	 * @param module
	 *            Guice module that is going to be used by the
	 *            {@link UT2004BotFactory}
	 * @param name
	 *            default name that serve as a basis for {@link IAgentId}
	 */
	public UT2004BotRunner(UT2004BotModule module, String name) {
		this(module, name, Pogamut.getPlatform().getProperty(
				PogamutUT2004Property.POGAMUT_UT2004_BOT_HOST.getKey()),
				Pogamut.getPlatform().getIntProperty(
						PogamutUT2004Property.POGAMUT_UT2004_BOT_PORT.getKey()));
	}

	/**
	 * Construct the runner without specifying anything as default. Default name
	 * for bots will be "UT2004Bot" and host:port will be taken from the Pogamut
	 * platform properties.
	 * 
	 * @param module
	 *            Guice module that is going to be used by the
	 *            {@link UT2004BotFactory}
	 */
	public UT2004BotRunner(UT2004BotModule module) {
		this(module, "UT2004Bot");
	}

	/**
	 * Construct the runner + specify all defaults.
	 * 
	 * @param botControllerClass
	 *            controller that will be used to instantiate
	 *            {@link UT2004BotModule}, i.e., it will control the
	 *            {@link UT2004Bot} instance
	 * @param name
	 *            default name that serve as a basis for {@link IAgentId}
	 * @param host
	 *            default host where the instances are going to be connected
	 * @param port
	 *            default port where the instances are going to be connected
	 */
	public UT2004BotRunner(
			Class<? extends IUT2004BotController> botControllerClass,
			String name, String host, int port) {
		this(new UT2004BotModule(botControllerClass), name, host, port);
	}

	/**
	 * Construct the runner + specify the default name, host:port will be taken
	 * from the Pogamut platform properties.
	 * 
	 * @param botControllerClass
	 *            controller that will be used to instantiate
	 *            {@link UT2004BotModule}, i.e., it will control the
	 *            {@link UT2004Bot} instance
	 * @param name
	 *            default name that serve as a basis for {@link IAgentId}
	 */
	public UT2004BotRunner(
			Class<? extends IUT2004BotController> botControllerClass,
			String name) {
		this(
				new UT2004BotModule(botControllerClass),
				name,
				Pogamut.getPlatform().getProperty(
						PogamutUT2004Property.POGAMUT_UT2004_BOT_HOST.getKey()),
				Pogamut.getPlatform().getIntProperty(
						PogamutUT2004Property.POGAMUT_UT2004_BOT_PORT.getKey()));
	}

	/**
	 * Construct the runner without specifying anything as default. Default name
	 * for bots will be "UT2004Bot" and host:port will be taken from the Pogamut
	 * platform properties.
	 * 
	 * @param botControllerClass
	 *            controller that will be used to instantiate
	 *            {@link UT2004BotModule}, i.e., it will control the
	 *            {@link UT2004Bot} instance
	 */
	public UT2004BotRunner(
			Class<? extends IUT2004BotController> botControllerClass) {
		this(new UT2004BotModule(botControllerClass), "UT2004Bot");
	}

	@Override
	public BOT startAgent() throws PogamutException {
		return super.startAgent();
	}

	@Override
	public List<BOT> startAgents(int count) throws PogamutException {
		return super.startAgents(count);
	}

	@Override
	public List<BOT> startAgents(PARAMS... agentParameters)
			throws PogamutException {
		return super.startAgents(agentParameters);
	};

	/**
	 * Sets name that is going to be used to form new {@link IAgentId} of the
	 * bots.
	 * <p>
	 * <p>
	 * If null is passed, generic "UT2004Bot" will be set.
	 * 
	 * @param name
	 *            name used for the newly started bots
	 * @return this instance
	 */
	public UT2004BotRunner<BOT, PARAMS> setName(String name) {
		if (name == null)
			name = "UT2004Bot";
		super.setName(name);
		return this;
	}

	/**
	 * Sets host, where newly launched bots will be connected to.
	 * 
	 * @param host
	 *            host running GB2004 server (can't be null)
	 * @return this instance
	 */
	public UT2004BotRunner<BOT, PARAMS> setHost(String host) {
		super.setHost(host);
		return this;
	}

	/**
	 * Sets port, where newly launched bots will be connected to.
	 * 
	 * @param port
	 *            at the host where GB2004 server is listening for bot
	 *            connections
	 * @return this instance
	 */
	public UT2004BotRunner<BOT, PARAMS> setPort(int port) {
		super.setPort(port);
		return this;
	}

	/**
	 * Provides default parameters that is, {@link IAgentId} using
	 * {@link UT2004BotRunner#name} and {@link SocketConnectionAddress} using
	 * {@link UT2004BotRunner#host} and {@link UT2004BotRunner#port}.
	 */
	@Override
	protected IAgentParameters newDefaultAgentParameters() {
		return new UT2004BotParameters().setAgentId(newAgentId(name))
				.setWorldAddress(new SocketConnectionAddress(host, port));
	}

	@Override
	public UT2004BotRunner<BOT, PARAMS> setMain(boolean state) {
		super.setMain(state);
		return this;
	}

	@Override
	public UT2004BotRunner<BOT, PARAMS> setConsoleLogging(boolean enabled) {
		super.setConsoleLogging(enabled);
		return this;
	}

}
