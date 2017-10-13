package cz.cuni.amis.pogamut.ut2004.utils;

import java.util.List;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.params.IAgentParameters;
import cz.cuni.amis.pogamut.base.agent.utils.runner.impl.AgentRunner;
import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.SocketConnectionAddress;
import cz.cuni.amis.pogamut.base.factory.IAgentFactory;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.PogamutPlatform;
import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;
import cz.cuni.amis.pogamut.ut2004.server.IUT2004Server;
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
 */
public class UT2004ServerRunner<SERVER extends IUT2004Server, PARAMS extends UT2004AgentParameters> extends AgentRunner<SERVER, PARAMS> {

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
	 * @param factory to be used for creating new {@link IUT2004Server} instances
	 * @param name default name that serve as a basis for {@link IAgentId}
	 * @param host default host where the instances are going to be connected
	 * @param port default port where the instances are going to be connected
	 */
	public UT2004ServerRunner(IAgentFactory<SERVER, PARAMS> factory, String name, String host, int port) {
        super(factory);
        this.name = name;
        this.port = port;
        this.host = host;
    }

	/**
	 * Construct the runner + specify the default name, host:port will be taken from the Pogamut platform properties.
	 * 
	 * @param factory factory to be used for creating new {@link IUT2004Server} instances
	 * @param name default name that serve as a basis for {@link IAgentId}
	 */
    public UT2004ServerRunner(IAgentFactory<SERVER, PARAMS> factory, String name) {
        this(
        	factory, 
        	name, 
        	Pogamut.getPlatform().getProperty(PogamutUT2004Property.POGAMUT_UT2004_SERVER_HOST.getKey()) == null ? 
    				"localhost" 
    			:	Pogamut.getPlatform().getProperty(PogamutUT2004Property.POGAMUT_UT2004_SERVER_HOST.getKey()), 
    		Pogamut.getPlatform().getIntProperty(PogamutUT2004Property.POGAMUT_UT2004_SERVER_PORT.getKey()) == 0 ?
    				3001
    			:	Pogamut.getPlatform().getIntProperty(PogamutUT2004Property.POGAMUT_UT2004_SERVER_PORT.getKey())
        );
    }
    
    @Override
    public SERVER startAgent() throws PogamutException {
    	return super.startAgent();
    }
    
    @Override
    public List<SERVER> startAgents(int count) throws PogamutException {
    	return super.startAgents(count);
    }
    
    @Override
    public List<SERVER> startAgents(PARAMS... agentParameters) throws PogamutException {
    	return super.startAgents(agentParameters);
    };
    
    /**
     * Construct the runner without specifying anything as default. Default name for server agents will be "UTServer Factory"
     * and host:port will be taken from the Pogamut platform properties.
     * 
     * @param factory factory to be used for creating new {@link IUT2004Server} instances
     */
    public UT2004ServerRunner(IAgentFactory<SERVER, PARAMS> factory) {
        this(factory, "UT2004Server");
    }

    /**
     * Provides default parameters that is, {@link IAgentId} using {@link UT2004ServerRunner#name} and {@link SocketConnectionAddress}
     * using {@link UT2004ServerRunner#host} and {@link UT2004ServerRunner#port}.
     */
	@Override
	protected IAgentParameters newDefaultAgentParameters() {
		return new UT2004AgentParameters().setAgentId(newAgentId(name)).setWorldAddress(new SocketConnectionAddress(host, port));
	}
	
}
