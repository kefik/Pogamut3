package cz.cuni.amis.pogamut.ut2004.server;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AbstractAgent;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.agent.state.WaitForAgentStateChange;
import cz.cuni.amis.pogamut.base.agent.state.level0.IAgentState;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateRunning;
import cz.cuni.amis.pogamut.base.agent.utils.AgentKeepAlive;
import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.SocketConnectionAddress;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004ServerFactory;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004ServerModule;
import cz.cuni.amis.pogamut.ut2004.server.exception.UCCStartException;
import cz.cuni.amis.pogamut.ut2004.server.impl.UT2004Server;
import cz.cuni.amis.pogamut.ut2004.utils.PogamutUT2004Property;
import cz.cuni.amis.pogamut.ut2004.utils.UCCWrapper;
import cz.cuni.amis.pogamut.ut2004.utils.UCCWrapperConf;

/**
 * Testing the {@link UT2004Server#setGameMap(String)} by changing maps on the UCCWrapper + server is managed by {@link AgentKeepAlive}.
 */
public class UT2004Test08_UT2004Server_SetGameMap_AgentKeepAlive {

	private static final Level LOG_LEVEL = Level.INFO;

	protected static boolean useInternalUcc = !Pogamut.getPlatform().getProperty(PogamutUT2004Property.POGAMUT_UNREAL_TEST_EXT_SERVER.getKey()).equals("true");
	
	private UCCWrapper uccWrapper;
	
    IUT2004Server server;
    UT2004ServerFactory factory;
   
    @Before
    public void startUp() throws UCCStartException {
    	if (useInternalUcc) {
    		System.out.println("Starting UCC...");
    		uccWrapper = new UCCWrapper(new UCCWrapperConf().setStartOnUnusedPort(false));
    	}
    }

    @After
    public void tearDown() {
    	if (server != null) {
    		server.kill();
    	}
    	if (useInternalUcc) {
	    	System.out.println("Stopping UCC...");
	    	uccWrapper.stop();
    	}
    	System.out.println("Closing PogamutPlatform...");
    	Pogamut.getPlatform().close();
    }
    
    private boolean awaitAgentUp(AbstractAgent agent) {
    	System.out.println("Awaiting server up (timeout 60s)...");
    	IAgentState state = new WaitForAgentStateChange(agent.getState(), IAgentStateRunning.class).await(60000, TimeUnit.MILLISECONDS);
    	return state instanceof IAgentStateRunning;
    }
    
    private void changeMap(IUT2004Server server, String map) {
    	if (awaitAgentUp((AbstractAgent)server)) {
    		System.out.println("Changing map to '" + map + "'...");
    		Future<Boolean> future = server.setGameMap(map);
    		try {
    			System.out.println("Waiting for the GB2004 to change the map (60sec timeout).");
    			Boolean result = future.get(60000, TimeUnit.MILLISECONDS);
        		if (result == null || !result) {
        			Assert.fail("Failed to change map to '" + map + "'.");
        		}
    		} catch (Exception e) {
    			Assert.fail("Failed to change map to '" + map + "'.");
    		}
    	} else {
    		Assert.fail("Failed to connect to GB2004...");
    	}
	}

    @Test
    public void test01_SetGameMap() {
    	String host = "localhost";
    	int port = 3001;
    	
    	if (useInternalUcc) {
    		host = uccWrapper.getHost();
    		port = uccWrapper.getControlPort();
    	}
        
        factory = new UT2004ServerFactory(new UT2004ServerModule());
        
        IAgentId agentId = new AgentId("Test-ChangeMap");
        server = (IUT2004Server) factory.newAgent(new UT2004AgentParameters().setAgentId(agentId).setWorldAddress(new SocketConnectionAddress(host, port)));
        AbstractAgent serverAgent = (AbstractAgent)server;
        serverAgent.getLogger().setLevel(LOG_LEVEL);
        serverAgent.getLogger().addDefaultConsoleHandler();
        
        AgentKeepAlive keepAlive = new AgentKeepAlive(serverAgent, 1000);
        keepAlive.start();
        
        if (!awaitAgentUp(serverAgent)) {
        	Assert.fail("Failed to connect to GB2004.");
        } else {
        	System.out.println("Connected...");
        }
		
        String[] maps = new String[] {
        		"DM-1on1-Albatross",
        		"DM-1on1-Crash",
        		"DM-1on1-Desolation",
        		"DM-1on1-Idoma",
        		"DM-1on1-Irondust",
        		"DM-1on1-Mixer",
        		"DM-1on1-Roughinery",
        		"DM-1on1-Serpentine",
        		"DM-1on1-Spirit",
        		"DM-1on1-Squader",
        		"DM-1on1-Trite",
        		"DM-Antalus",
        		"DM-Asbestos",
        		"DM-Compressed",
        		"DM-Corrugation",
        		"DM-Curse4",
        		"DM-DE-Grendelkeep",
        		"DM-DE-Ironic",
        		"DM-DE-Osiris2",
        		"DM-Deck17",
        		"DM-DesertIsle",
        		"DM-Flux2",
        		"DM-Gael",
        		"DM-Gestalt",
        		"DM-Goliath",
        		"DM-HyperBlast2",
        		"DM-Icetomb",
        		"DM-Inferno",
        		"DM-Injector",
        		"DM-Insidious",
        		"DM-IronDeity",
        		"DM-Junkyard",
        		"DM-Leviathan",
        		"DM-Metallurgy",
        		"DM-Morpheus3",
        		"DM-Oceanic",
        		"DM-Phobos2",
        		"DM-Plunge",
        		"DM-Rankin",
        		"DM-Rrajigar",
        		"DM-Rustatorium",
        		"DM-Sulphur",
        		"DM-TokaraForest",
        		"DM-TrainingDay"
        };
        
        try {
	        for (int i = 0; i < maps.length; ++i) {
	        	System.out.println("Change map test, map " + (i+1) + " / " + maps.length + "...");
	        	changeMap(server, maps[i]);
	        }
        } finally {
	        server.kill();
        }
		
        System.out.println("---/// TEST OK ///---");        
    }

	public static void main(String[] args) {
        UT2004Test08_UT2004Server_SetGameMap_AgentKeepAlive test = new UT2004Test08_UT2004Server_SetGameMap_AgentKeepAlive();
        test.startUp();
        test.test01_SetGameMap();
        test.tearDown();
    }
}
