package cz.cuni.amis.pogamut.ut2004.server;

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
import cz.cuni.amis.pogamut.unreal.server.exception.MapChangeException;
import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.ChangeMap;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004ServerFactory;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004ServerModule;
import cz.cuni.amis.pogamut.ut2004.server.exception.UCCStartException;
import cz.cuni.amis.pogamut.ut2004.server.impl.UT2004Server;
import cz.cuni.amis.pogamut.ut2004.utils.PogamutUT2004Property;
import cz.cuni.amis.pogamut.ut2004.utils.UCCWrapper;
import cz.cuni.amis.pogamut.ut2004.utils.UCCWrapperConf;

/**
 * Testing the {@link AgentKeepAlive} together with {@link UT2004Server} by changing maps on the UCCWrapper.
 */
public class UT2004Test06_UT2004Server_ChangeMap_Manual {

	private static final Level LOG_LEVEL = Level.INFO;

	protected static boolean useInternalUcc = !Pogamut.getPlatform().getProperty(PogamutUT2004Property.POGAMUT_UNREAL_TEST_EXT_SERVER.getKey()).equals("true");
	
	private UCCWrapper uccWrapper;
	
    IUT2004Server server;
    UT2004ServerFactory factory;
   
    @Before
    public void startUp() throws UCCStartException {
    	if (!useInternalUcc) {
        	Assert.fail("Must use internal ucc to be able to start/stop gb2004 server and test AgentKeepAlive.");
        }
    	System.out.println("Starting UCC...");
    	uccWrapper = new UCCWrapper(new UCCWrapperConf().setStartOnUnusedPort(false));
    }

    @After
    public void tearDown() {
    	if (server != null) {
    		server.kill();
    	}
    	System.out.println("Stopping UCC...");
    	uccWrapper.stop();
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
    		try {
    			server.getAct().act(new ChangeMap().setMapName(map));
    			server.stop();
    		} catch (MapChangeException e) {
    			Assert.fail("Map change failed...");
    		}
    	} else {
    		Assert.fail("Failed to connect to GB2004...");
    	}
		for (int i = 0; i < 10; ++i) {
			if (awaitAgentUp((AbstractAgent)server)) {
				System.out.println("Server is up and running (" + (i+1) + " / 10) ...");
				if (map.equalsIgnoreCase(server.getMapName())) {
					System.out.println("Map changed successfully to '" + map + "'...");
					return;
				} else {
					System.out.println("Map not changed, is still '" + server.getMapName() + "' ... sending change map again.");
					try {
						server.setGameMap(map);
					} catch (MapChangeException e) {
						Assert.fail("Map change failed...");
					}
					server.setAddress(uccWrapper.getHost(), uccWrapper.getControlPort());
				}
			} else {
				Assert.fail("Failed to connect to GB2004...");
			}
		}
		Assert.fail("10x reconnected to server and map has not been changed to '" + map + "'...");
	}

    @Test
    public void test01_ChangeMap() {
    	String host = uccWrapper.getHost();
        int port = uccWrapper.getControlPort();
        
        factory = new UT2004ServerFactory(new UT2004ServerModule());
        
        IAgentId agentId = new AgentId("Test-ChangeMap");
        server = (IUT2004Server) factory.newAgent(new UT2004AgentParameters().setAgentId(agentId).setWorldAddress(new SocketConnectionAddress(host, port)));
        AbstractAgent serverAgent = (AbstractAgent)server;
        serverAgent.getLogger().setLevel(LOG_LEVEL);
        serverAgent.getLogger().addDefaultConsoleHandler();
        
        AgentKeepAlive keepAlive = new AgentKeepAlive(server, 1000);
        keepAlive.start();
        
        if (!awaitAgentUp(serverAgent)) {
        	Assert.fail("Failed to connect server again.");
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
	        keepAlive.stop();
	        server.stop();
        }
		
        System.out.println("---/// TEST OK ///---");        
    }

}
