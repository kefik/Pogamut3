package cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.params.IRemoteAgentParameters;
import cz.cuni.amis.pogamut.base.factory.guice.GuiceAgentFactory;
import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;
import cz.cuni.amis.pogamut.ut2004.observer.IUT2004Observer;

/**
 * Guice-based {@link IUT2004Observer} factory. It needs {@link UT2004ObserverModule} to work correctly and the module is required
 * to bound {@link IAgent} to the {@link IUT2004Observer}, otherwise the method {@link UT2004ObserverFactory#newAgent(IRemoteAgentParameters)}
 * will throw {@link ClassCastException}.
 * <p><p>
 * For more info about the factory, see {@link GuiceAgentFactory}.
 *
 * @author Jimmy
 *
 * @param <BOT>
 * @param <PARAMS>
 */
public class UT2004ObserverFactory<SERVER extends IUT2004Observer, PARAMS extends UT2004AgentParameters> extends GuiceAgentFactory<SERVER, PARAMS> {

	public UT2004ObserverFactory(UT2004ObserverModule agentModule) {
		super(agentModule);
	}
	
	@Override
	protected UT2004ObserverModule getAgentModule() {
		return (UT2004ObserverModule) super.getAgentModule();
	}

}