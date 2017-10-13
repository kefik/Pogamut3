package cz.cuni.amis.pogamut.base.factory.guice;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.params.IRemoteAgentParameters;

/**
 * Guice agent factory configured by an agent module ({@link GuiceRemoteAgentModule}) that is specifying the bindings 
 * for respective interfaces.
 * 
 * @author Jimmy
 *
 * @param <ADDRESS>
 */
public class GuiceRemoteAgentFactory<AGENT extends IAgent, PARAMS extends IRemoteAgentParameters> extends GuiceAgentFactory<AGENT, PARAMS> {
	
	public GuiceRemoteAgentFactory(GuiceRemoteAgentModule agentModule) {
		super(agentModule);
	}
	
	@Override
	protected GuiceRemoteAgentModule getAgentModule() {
		return (GuiceRemoteAgentModule) super.getAgentModule();
	}

}
