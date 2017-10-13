package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.drawing;

import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateRunning;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004ServerFactory;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004ServerModule;
import cz.cuni.amis.pogamut.ut2004.server.impl.UT2004Server;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004ServerRunner;

public class UT2004ServerProvider implements IUT2004ServerProvider {

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
		log.warning("Initializing. Creating UT2004Server...");
		
		killServer();
		
		UT2004ServerModule module = new UT2004ServerModule<UT2004AgentParameters>();
		UT2004ServerFactory factory = new UT2004ServerFactory(module);
		UT2004ServerRunner runner = new UT2004ServerRunner(factory);
		runner.setLogLevel(Level.SEVERE);
		server = (UT2004Server) runner.startAgents(params).get(0);
		
		log.warning("Initialized. New UT2004Server instance created.");
	}

	@Override
	public void killServer() {
		if (server == null) return;
		log.warning("Killing old UT2004Server instance...");
		try {
			server.kill();
		} catch (Exception e) {				
		}
		server = null;
	}
	
	
		
}
