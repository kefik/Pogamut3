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
 * Class used for creating, connecting and starting servers with default settings that are taken from the properties.
 * <p><p>
 * The address where the instances will connect are defined either in the constructor
 * or taken from the properties of the {@link PogamutPlatform}.
 * <p><p>
 * For more information about the class see {@link AgentRunner}.
 * 
 * @author ik
 * @author Jimmy
 *
 * @param <BOT>
 * @param <PARAMS>
 */
public abstract class UTBotRunner<BOT extends IUT2004Bot, PARAMS extends UT2004BotParameters> extends AgentRunner<BOT, PARAMS> {

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
	 * @param factory to be used for creating new {@link IUT2004Bot} instances
	 * @param name default name that serve as a basis for {@link IAgentId}
	 * @param host default host where the instances are going to be connected
	 * @param port default port where the instances are going to be connected
	 */
	public UTBotRunner(IAgentFactory<BOT, PARAMS> factory, String name, String host, int port) {
        super(factory);
        this.name = name;
        this.port = port;
        this.host = host;        
    }

	/**
	 * Construct the runner + specify the default name, host:port will be taken from the Pogamut platform properties.
	 * 
	 * @param factory factory to be used for creating new {@link IUT2004Bot} instances
	 * @param log used to log stuff
	 * @param name default name that serve as a basis for {@link IAgentId}
	 */
    public UTBotRunner(IAgentFactory<BOT, PARAMS> factory, String name) {
        this(
        	factory, 
        	name, 
        	Pogamut.getPlatform().getProperty(PogamutUT2004Property.POGAMUT_UT2004_BOT_HOST.getKey()) == null ? 
        				"localhost" 
        			:	Pogamut.getPlatform().getProperty(PogamutUT2004Property.POGAMUT_UT2004_BOT_HOST.getKey()), 
        	Pogamut.getPlatform().getIntProperty(PogamutUT2004Property.POGAMUT_UT2004_BOT_PORT.getKey()) == 0 ?
        				3000
        			:	Pogamut.getPlatform().getIntProperty(PogamutUT2004Property.POGAMUT_UT2004_BOT_PORT.getKey())
        );
    }
    
    /**
     * Construct the runner without specifying anything as default. Default name for bots will be "UT2004Bot"
     * and host:port will be taken from the Pogamut platform properties.
     * 
     * @param factory factory to be used for creating new {@link IUT2004Bot} instances
     */
    public UTBotRunner(IAgentFactory<BOT, PARAMS> factory) {
        this(factory, "UT2004Bot");
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
	public UTBotRunner<BOT, PARAMS> setName(String name) {
		if (name == null) name = "UTBot";
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
	public UTBotRunner<BOT, PARAMS> setHost(String host) {
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
	public UTBotRunner<BOT, PARAMS> setPort(int port) {
		this.port = port;
		return this;
	}

    /**
     * Provides default parameters that is, {@link IAgentId} using {@link UTBotRunner#name} and {@link SocketConnectionAddress}
     * using {@link UTBotRunner#host} and {@link UTBotRunner#port}.
     */
	@Override
	protected abstract IAgentParameters newDefaultAgentParameters();
	
}
