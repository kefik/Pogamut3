package cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.params.IRemoteAgentParameters;
import cz.cuni.amis.pogamut.base.factory.guice.GuiceAgentFactory;
import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;
import cz.cuni.amis.pogamut.ut2004.server.IUT2004Server;

/**
 * Guice-base {@link IUT2004Server} factory. It needs {@link UT2004ServerModule} to work correctly and the module is required
 * to bound {@link IAgent} to the {@link IUT2004Server}, otherwise the method {@link UT2004ServerFactory#newAgent(IRemoteAgentParameters)}
 * will throw {@link ClassCastException}.
 * <p><p>
 * For more info about the factory, see {@link GuiceAgentFactory}.
 *
 * @author Jimmy
 *
 * @param <BOT>
 * @param <PARAMS>
 */
public class UT2004ServerFactory<SERVER extends IUT2004Server, PARAMS extends UT2004AgentParameters> extends GuiceAgentFactory<SERVER, PARAMS> {

	public UT2004ServerFactory(UT2004ServerModule agentModule) {
		super(agentModule);
	}
	
	@Override
	protected UT2004ServerModule getAgentModule() {
		return (UT2004ServerModule) super.getAgentModule();
	}

}
