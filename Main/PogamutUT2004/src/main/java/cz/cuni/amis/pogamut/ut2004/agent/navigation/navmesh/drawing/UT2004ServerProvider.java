package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.drawing;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.state.WaitForAgentStateChange;
import cz.cuni.amis.pogamut.base.agent.state.level0.IAgentState;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateGoingUp;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateUp;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateRunning;
import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.ISocketConnectionAddress;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004ServerFactory;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004ServerModule;
import cz.cuni.amis.pogamut.ut2004.server.impl.UT2004Server;
import cz.cuni.amis.pogamut.ut2004.utils.PogamutUT2004Property;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004ServerRunner;
import cz.cuni.amis.utils.maps.LazyMap;

public class UT2004ServerProvider implements IUT2004ServerProvider {

	private static Map<String, UT2004Server> servers = new HashMap<String, UT2004Server>();
	
	private static Map<String, AtomicInteger> counts = new LazyMap<String, AtomicInteger>() {

		@Override
		protected AtomicInteger create(String key) {
			return new AtomicInteger(0);
		}
		
	};
	
	protected Logger log;
	
	protected UT2004Server server;
	
	public UT2004ServerProvider() {
		this(null);
	}
	
	public UT2004ServerProvider(Logger log) {
		this.log = log;
		if (this.log == null) {
			this.log = new LogCategory("UT2004ServerProvider");
		}
	}
	
	// =====================
	// UT2004 SERVER SHARING
	// =====================
	
	@Override
	public UT2004Server getServer() {
		if (server == null) init();
		return server;
	}
	
	protected UT2004AgentParameters getDefaultServerParams() {
		UT2004AgentParameters result = new UT2004AgentParameters();
		return result;
	}
	
	// ============
	// INITIALIZING
	// ============
	
	public boolean isInited() {
		return server != null && server.inState(IAgentStateRunning.class);
	}
	
	public void init() {
		if (server != null && server.inState(IAgentStateRunning.class)) return;
		init(getDefaultServerParams());
	}
	
	public void init(UT2004AgentParameters params) {
		if (server != null && server.inState(IAgentStateRunning.class)) return;
		initForced(params);
	}
	
	public void initForced() {
		initForced(getDefaultServerParams());
	}
	
	public void initForced(UT2004AgentParameters params) {	
		UT2004Server server;
		String address;
		synchronized (servers) {
			ISocketConnectionAddress socketAddress = (ISocketConnectionAddress) params.getWorldAddress();
			address = (socketAddress == null ? getDefaultAddress() : socketAddress.getHost().toLowerCase() + ":" + socketAddress.getPort());
			server = servers.get(address);			
			if (server != null) {
				synchronized (server) {
					AtomicInteger count = counts.get(address);
					count.incrementAndGet();
					if (server.inState(IAgentStateUp.class)) {					
						this.server = server;
						log.warning("UT2004Server count rised: " + address + " => " + count.get());
						return;
					} else
					if (server.inState(IAgentStateGoingUp.class)) {
						IAgentState state = new WaitForAgentStateChange(server.getState(), IAgentStateRunning.class).await(60 * 1000, TimeUnit.MILLISECONDS);
						if (state instanceof IAgentStateRunning) {
							this.server = server;
							log.warning("UT2004Server count rised: " + address + " => " + count.get());
							return;
						}
						count.decrementAndGet();
					}
				}
			}
			
			log.warning("Initializing. Creating UT2004Server...");
			
			killServer();
			
			UT2004ServerModule module = new UT2004ServerModule<UT2004AgentParameters>();
			UT2004ServerFactory factory = new UT2004ServerFactory(module);
			UT2004ServerRunner runner = new UT2004ServerRunner(factory);
			runner.setLogLevel(Level.SEVERE);
			server = (UT2004Server) runner.startAgents(params).get(0);
			
			log.warning("Initialized. New UT2004Server instance created for: " + address);
			
			servers.put(address, server);
			counts.get(address).incrementAndGet();
			
			this.server = server;
		}		
		
		
	}

	@Override
	public void killServer() {
		if (server == null) return;
		
		ISocketConnectionAddress socketAddress = (ISocketConnectionAddress) server.getParams().getWorldAddress();
		String address = (socketAddress == null ? getDefaultAddress() : socketAddress.getHost().toLowerCase() + ":" + socketAddress.getPort());
		
		synchronized (servers) {
			AtomicInteger count = counts.get(address);
			if (count.get() == 1) {
				log.warning("Killing old UT2004Server instance...");
				count.decrementAndGet();
				servers.remove(address);					
			} else {
				log.warning("Decrementing UT2004Server instance: " + address + " -> " + count.decrementAndGet());
				server = null;
				return;
			}
		}
		
		try {
			server.kill();
		} catch (Exception e) {				
		}
		
		server = null;
	}
	
	protected String getDefaultAddress() {
		return "" + (Pogamut.getPlatform().getProperty(PogamutUT2004Property.POGAMUT_UT2004_SERVER_HOST.getKey()) == null 
				              ? 
 				                "localhost" 
			                  :	Pogamut.getPlatform().getProperty(PogamutUT2004Property.POGAMUT_UT2004_SERVER_HOST.getKey())) 
				  + ":"				  
				  + (Pogamut.getPlatform().getIntProperty(PogamutUT2004Property.POGAMUT_UT2004_SERVER_PORT.getKey()) == 0 
				              ?
				                "3001"
			                  :	"" + Pogamut.getPlatform().getIntProperty(PogamutUT2004Property.POGAMUT_UT2004_SERVER_PORT.getKey()))
		;
	}
	
	
		
}
