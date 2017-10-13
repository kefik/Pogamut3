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
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateDown;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateGoingUp;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateRunning;
import cz.cuni.amis.pogamut.base.agent.utils.AgentKeepAlive;
import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.SocketConnectionAddress;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004ServerFactory;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004ServerModule;
import cz.cuni.amis.pogamut.ut2004.server.exception.UCCStartException;
import cz.cuni.amis.pogamut.ut2004.utils.PogamutUT2004Property;
import cz.cuni.amis.pogamut.ut2004.utils.UCCWrapper;
import cz.cuni.amis.pogamut.ut2004.utils.UCCWrapperConf;

/**
 * Tests UT2004Server together with AgentKeepAlive ... starting and killing UCC and waits for UT2004Server to 
 * reconnect automatically.
 */
public class UT2004Test05_UT2004Server_AutoReconnect {

	private static boolean useInternalUcc = !Pogamut.getPlatform().getProperty(PogamutUT2004Property.POGAMUT_UNREAL_TEST_EXT_SERVER.getKey()).equals("true");
	
	private UCCWrapper uccWrapper;
	
    IUT2004Server server = null;
    UT2004ServerFactory factory;
   
    @Before
    public void startUp() throws UCCStartException {
    	if (!useInternalUcc) {
        	Assert.fail("Must use internal ucc to be able to start/stop gb2004 server and test AgentKeepAlive.");
        }
    	System.out.println("Starting ucc...");
    	uccWrapper = new UCCWrapper(new UCCWrapperConf());
    }

    @After
    public void tearDown() {
    	if (server != null) {
    		server.stop();
    	}
    	System.out.println("Stopping ucc...");
    	uccWrapper.stop();  
    	System.out.println("Closing PogamutPlatform...");
    	Pogamut.getPlatform().close();
    }
    
    private boolean awaitAgentUp(AbstractAgent agent) {
    	System.out.println("Awaiting server up (timeout 60s)...");
    	IAgentState state = new WaitForAgentStateChange(agent.getState(), IAgentStateRunning.class).await(60000, TimeUnit.MILLISECONDS);
    	return state != null && state instanceof IAgentStateRunning;
    }

    @Test
    public void test01_KeepAlive() {
    	String host = uccWrapper.getHost();
        int port = uccWrapper.getControlPort();
        
        factory = new UT2004ServerFactory(new UT2004ServerModule());
        
        IAgentId agentId = new AgentId("Test server-keepalive");
        server = (IUT2004Server) factory.newAgent(new UT2004AgentParameters().setAgentId(agentId).setWorldAddress(new SocketConnectionAddress(host, port)));
        AbstractAgent serverAgent = (AbstractAgent)server;
        serverAgent.getLogger().setLevel(Level.WARNING);
        serverAgent.getLogger().addDefaultConsoleHandler();
        
        AgentKeepAlive keepAlive = new AgentKeepAlive(server, 1000);
        keepAlive.start();
        
        for (int i = 0; i < 20; ++i) {   
        	System.out.println("Reconnection " + (i+1) + " / 20 ...");
        	
        	if (!awaitAgentUp(serverAgent)) {
        		Assert.fail("Failed to reconnect to server.");
        	} else {
        		System.out.println("Connected...");
        	}

        	try {
        		System.out.println("Sleeping for 1000ms.");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			
			System.out.println("Checking server state...");
			if (!serverAgent.inState(IAgentStateRunning.class)) {
				Assert.fail("Agent is not running!");
			}
			System.out.println("Server is running - OK.");
        	
			System.out.println("Stopping UCC...");
        	uccWrapper.stop();        
        	if (serverAgent.awaitState(IAgentStateDown.class, 10000) == null) {
        		Assert.fail("Failed to stop the server...");
        	}
        	        	
        	try {
        		System.out.println("Sleeping for 1000ms.");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			if (!serverAgent.inState(IAgentStateDown.class, IAgentStateGoingUp.class)) {
				Assert.fail("Agent is still running!");
			}
			System.out.println("Server is not running - OK.");
		
        	System.out.println("Starting UCC...");
        	uccWrapper = new UCCWrapper(new UCCWrapperConf());
        	server.setAddress(uccWrapper.getHost(), uccWrapper.getControlPort());
        }        
        
        keepAlive.stop();
        server.stop();
		
        System.out.println("---/// TEST OK ///---");
    }

}
