package cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.params.IRemoteAgentParameters;
import cz.cuni.amis.pogamut.base.factory.guice.GuiceAgentFactory;
import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;
import cz.cuni.amis.pogamut.ut2004.analyzer.IUT2004Analyzer;
import cz.cuni.amis.pogamut.ut2004.analyzer.UT2004AnalyzerModule;
import cz.cuni.amis.pogamut.ut2004.server.IUT2004Server;

/**
 * Guice-base {@link IUT2004Server} factory. It needs {@link UT2004ServerModule} to work correctly and the module is required
 * to bound {@link IAgent} to the {@link IUT2004Server}, otherwise the method {@link UT2004AnalyzerFactory#newAgent(IRemoteAgentParameters)}
 * will throw {@link ClassCastException}.
 * <p><p>
 * For more info about the factory, see {@link GuiceAgentFactory}.
 *
 * @author Jimmy
 *
 * @param <ANALYZER>
 * @param <PARAMS>
 */
public class UT2004AnalyzerFactory<ANALYZER extends IUT2004Analyzer, PARAMS extends UT2004AgentParameters> extends GuiceAgentFactory<ANALYZER, PARAMS> {

	public UT2004AnalyzerFactory(UT2004AnalyzerModule agentModule) {
		super(agentModule);
	}
	
	@Override
	protected UT2004AnalyzerModule getAgentModule() {
		return (UT2004AnalyzerModule) super.getAgentModule();
	}

}
