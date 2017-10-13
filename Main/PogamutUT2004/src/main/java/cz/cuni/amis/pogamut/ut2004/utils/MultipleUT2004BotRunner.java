package cz.cuni.amis.pogamut.ut2004.utils;

import java.util.List;
import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.params.IAgentParameters;
import cz.cuni.amis.pogamut.base.agent.utils.runner.IAgentDescriptor;
import cz.cuni.amis.pogamut.base.agent.utils.runner.impl.MultipleAgentRunner;
import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.SocketConnectionAddress;
import cz.cuni.amis.pogamut.base.factory.IAgentFactory;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004BotFactory;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004BotModule;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * This class has only one purpose - execute ONE OR MORE BOTS inside 'main' method. You can't use it for anything else!
 * It is THE SHORTCUT of all SHORTCUTS to execute multiple bots, wait till they finishe and close the whole Pogamut.
 * <p><p>
 * Designed especially for the usage inside NetBeans projects.
 * <p><p>
 * NOTE: by default, all bots get paused after they start and they are resumed after all bots are present in UT2004. To
 * change this behaviour pass 'false' through {@link MultipleUT2004BotRunner#setPausing(boolean)}. 
 * <p><p>
 * NOTE: It's not even meant to be instantiated twice for two different batch of bots and consequently executed in two different threads!
 * Single-purpose class only ;-)
 * <p><p>
 * NOTE: It might be very interesting for you to check out the source of method {@link MultipleUT2004BotRunner#startAgent()} to
 * see how the agent should be instantiated via {@link UT2004BotFactory} using {@link UT2004BotModule}.
 * <p><p>
 * 
 * 
 * @author Jimmy
 */
public class MultipleUT2004BotRunner<BOT extends UT2004Bot, PARAMS extends UT2004BotParameters, MODULE extends UT2004BotModule> extends MultipleAgentRunner<BOT, PARAMS, MODULE> {
	
	/**
	 * Default host where the instances are going to be connected as defaults, see {@link IAgentParameters#assignDefaults(IAgentParameters)}.
	 */
    protected String host;
    
    /**
	 * Default port where the instances are going to be connected as defaults, see {@link IAgentParameters#assignDefaults(IAgentParameters)}.
	 */
    protected int port;
    
    /**
	 * Default name that will serve as a basis for {@link IAgentId}, see {@link IAgentParameters#assignDefaults(IAgentParameters)}.
	 */
	protected String name;
	
	/** 
	 * Construct the runner + specify all defaults.
	 * 
 	 * @param name default name that serve as a basis for {@link IAgentId}
	 * @param host default host where the instances are going to be connected
	 * @param port default port where the instances are going to be connected
	 */
	public MultipleUT2004BotRunner(String name, String host, int port) {
        this.name = name;
        this.port = port;
        this.host = host;
    }

	/**
	 * Construct the runner + specify the default name, host:port will be taken from the Pogamut platform properties.
	 * 
	 * @param name default name that serve as a basis for {@link IAgentId}
	 */
    public MultipleUT2004BotRunner(String name) {
        this(
        	name, 
        	Pogamut.getPlatform().getProperty(PogamutUT2004Property.POGAMUT_UT2004_BOT_HOST.getKey()), 
        	Pogamut.getPlatform().getIntProperty(PogamutUT2004Property.POGAMUT_UT2004_BOT_PORT.getKey())
        );
    }
    
    /**
     * Returns name that is going to be used to form new {@link IAgentId} of the bots.
     *     
     * @return name used for the newly started bots
     */
    public String getName() {
		return name;
	}

    /**
     * Sets name that is going to be used to form new {@link IAgentId} of the bots.
     * <p><p>
     * If null is passed, generic "UT2004Bot" will be set.
     *     
     * @param name name used for the newly started bots
     * @return this instance
     */
	public MultipleUT2004BotRunner<BOT, PARAMS, MODULE> setName(String name) {
		if (name == null) name = "UT2004Bot";
		this.name = name;
		return this;
	}

	/**
     * Returns host, where newly launched bots will be connected to.
     * 
     * @return host running GB2004 server
     */
    public String getHost() {
		return host;
	}

    /**
     * Sets host, where newly launched bots will be connected to.
     * 
     * @param host host running GB2004 server (can't be null)
     * @return this instance
     */
	public MultipleUT2004BotRunner<BOT, PARAMS, MODULE> setHost(String host) {
		this.host = host;
		NullCheck.check(this.host, "host");
		return this;
	}

	/**
     * Returns port, where newly launched bots will be connected to.
     * 
     * @return port at the host where GB2004 server is listening for bot connections
     */
	public int getPort() {
		return port;
	}

	/**
     * Sets port, where newly launched bots will be connected to.
     * 
     * @param port at the host where GB2004 server is listening for bot connections
     * @return this instance
     */
	public MultipleUT2004BotRunner<BOT, PARAMS, MODULE> setPort(int port) {
		this.port = port;
		return this;
	}
	
	/**
     * We're setting the logging level to {@link Level#WARNING} here so the bot won't log much.
     */
    @Override
    protected void preStartHook(BOT agent) throws PogamutException {
        //agent.getLogger().setLevel(Level.WARNING);
    }

    /**
     * Provides default parameters that is, {@link IAgentId} using {@link MultipleUT2004BotRunner#name} and {@link SocketConnectionAddress}
     * using {@link MultipleUT2004BotRunner#host} and {@link MultipleUT2004BotRunner#port}.
     */
	@Override
	protected IAgentParameters newDefaultAgentParameters() {
		return new UT2004BotParameters().setAgentId(newAgentId(name)).setWorldAddress(new SocketConnectionAddress(host, port));
	}

	/**
	 * Uses {@link UT2004BotFactory} for agent construction.
	 */
	@Override
	protected IAgentFactory newAgentFactory(MODULE agentModule) {
		return new UT2004BotFactory<IUT2004Bot, UT2004BotParameters>(agentModule);
	}
	
	public List<BOT> startAgents(IAgentDescriptor<PARAMS,MODULE>... agentDescriptors) {
		return super.startAgents(agentDescriptors);
	};

}
