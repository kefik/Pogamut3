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
import cz.cuni.amis.pogamut.ut2004.observer.IUT2004Observer;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Class used for creating, connecting and starting observers with default settings that are taken from the properties.
 * <p><p>
 * The address where the instances will connect are defined either in the constructor
 * or taken from the properties of the {@link PogamutPlatform}.
 * <p><p>
 * For more information about the class see {@link AgentRunner}.
 * 
 * @author ik
 * @author Jimmy
 */
public class UT2004ObserverRunner<OBSERVER extends IUT2004Observer, PARAMS extends UT2004AgentParameters> extends AgentRunner<OBSERVER, PARAMS> {

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
	 * @param factory to be used for creating new {@link IUT2004Observer} instances
	 * @param name default name that serve as a basis for {@link IAgentId}
	 * @param host default host where the instances are going to be connected
	 * @param port default port where the instances are going to be connected
	 */
	public UT2004ObserverRunner(IAgentFactory<OBSERVER, PARAMS> factory, String name, String host, int port) {
        super(factory);
        this.name = name;
        this.port = port;
        this.host = host;
    }

	/**
	 * Construct the runner + specify the default name, host:port will be taken from the Pogamut platform properties.
	 * 
	 * @param factory factory to be used for creating new {@link IUT2004Observer} instances
	 * @param name default name that serve as a basis for {@link IAgentId}
	 */
    public UT2004ObserverRunner(IAgentFactory<OBSERVER, PARAMS> factory, String name) {
        this(
        	factory, 
        	name, 
        	Pogamut.getPlatform().getProperty(PogamutUT2004Property.POGAMUT_UT2004_OBSERVER_HOST.getKey()) == null ? 
    				"localhost" 
    			:	Pogamut.getPlatform().getProperty(PogamutUT2004Property.POGAMUT_UT2004_OBSERVER_HOST.getKey()), 
    		Pogamut.getPlatform().getIntProperty(PogamutUT2004Property.POGAMUT_UT2004_OBSERVER_PORT.getKey()) == 0 ?
    				3002
    			:	Pogamut.getPlatform().getIntProperty(PogamutUT2004Property.POGAMUT_UT2004_OBSERVER_PORT.getKey())
        );
    }
    
    /**
     * Construct the runner without specifying anything as default. Default name for server agents will be "UT2004Observer"
     * and host:port will be taken from the Pogamut platform properties.
     * 
     * @param factory factory to be used for creating new {@link IUT2004Observer} instances
     */
    public UT2004ObserverRunner(IAgentFactory<OBSERVER, PARAMS> factory) {
        this(factory, "UT2004Observer");
    }
    
    @Override
    public OBSERVER startAgent() throws PogamutException {
    	return super.startAgent();
    }
    
    @Override
    public List<OBSERVER> startAgents(int count) throws PogamutException {
    	return super.startAgents(count);
    }
    
    @Override
    public List<OBSERVER> startAgents(PARAMS... agentParameters) throws PogamutException {
    	return super.startAgents(agentParameters);
    };

    /**
     * Provides default parameters that is, {@link IAgentId} using {@link UT2004ObserverRunner#name} and {@link SocketConnectionAddress}
     * using {@link UT2004ObserverRunner#host} and {@link UT2004ObserverRunner#port}.
     */
	@Override
	protected IAgentParameters newDefaultAgentParameters() {
		return new UT2004AgentParameters().setAgentId(newAgentId(name)).setWorldAddress(new SocketConnectionAddress(host, port));
	}
	
}
