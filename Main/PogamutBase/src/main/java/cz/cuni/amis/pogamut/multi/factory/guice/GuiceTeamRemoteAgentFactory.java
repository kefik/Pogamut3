package cz.cuni.amis.pogamut.multi.factory.guice;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.factory.guice.GuiceRemoteAgentFactory;
import cz.cuni.amis.pogamut.multi.params.ITeamRemoteAgentParameters;

/**
 * Guice agent factory configured by an agent module ({@link GuiceTeamRemoteAgentModule}) that is specifying the bindings 
 * for respective interfaces.
 * 
 * @author Jimmy
 *
 * @param <ADDRESS>
 */
public class GuiceTeamRemoteAgentFactory<AGENT extends IAgent, PARAMS extends ITeamRemoteAgentParameters> extends GuiceRemoteAgentFactory<AGENT, PARAMS> {
	
	public GuiceTeamRemoteAgentFactory(GuiceTeamRemoteAgentModule agentModule) {
		super(agentModule);
	}
	
	@Override
	protected GuiceTeamRemoteAgentModule getAgentModule() {
		return (GuiceTeamRemoteAgentModule) super.getAgentModule();
	}

}
