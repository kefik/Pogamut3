package cz.cuni.amis.pogamut.ut2004.teamcomm.server;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.params.IAgentParameters;
import cz.cuni.amis.pogamut.base.communication.connection.IWorldConnectionAddress;
import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;

public class UT2004TCServerParams extends UT2004AgentParameters {
	
	private String bindHost = null;
	private Integer bindPort = null;
	
	public UT2004TCServerParams() {
		super();
	}
	
	@Override
	public UT2004TCServerParams setAgentId(IAgentId agentId) {
		super.setAgentId(agentId);
		return this;
	}
	
	@Override
	public UT2004TCServerParams setWorldAddress(IWorldConnectionAddress address) {
		super.setWorldAddress(address);
		return this;
	}

	public String getBindHost() {
		return bindHost;
	}

	public void setBindHost(String bindHost) {
		this.bindHost = bindHost;
	}

	public int getBindPort() {
		return bindPort;
	}

	public void setBindPort(int bindPort) {
		this.bindPort = bindPort;
	}
	
	@Override
	public void assignDefaults(IAgentParameters defaults) {
		super.assignDefaults(defaults);
		if (defaults instanceof UT2004TCServerParams) {
			if (bindHost == null) bindHost = ((UT2004TCServerParams)defaults).getBindHost();
			if (bindPort == null) bindPort = ((UT2004TCServerParams)defaults).getBindPort();			
		}
	}
	
}
