package cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.params.IRemoteAgentParameters;
import cz.cuni.amis.pogamut.base.factory.guice.GuiceAgentFactory;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;

/**
 * Guice-base {@link IUT2004Bot} factory. It needs {@link UT2004BotModule} to work correctly and the module is required
 * to bound {@link IAgent} to the {@link IUT2004Bot}, otherwise the method {@link UT2004BotFactory#newAgent(IRemoteAgentParameters)}
 * will throw {@link ClassCastException}.
 * <p><p>
 * For more info about the factory, see {@link GuiceAgentFactory}.
 *
 * @author Jimmy
 *
 * @param <BOT>
 * @param <PARAMS>
 */
public class UT2004BotFactory<BOT extends IUT2004Bot, PARAMS extends UT2004BotParameters> extends GuiceAgentFactory<BOT, PARAMS> {

	public UT2004BotFactory(UT2004BotModule<PARAMS> agentModule) {
		super(agentModule);
	}
	
	@Override
	protected UT2004BotModule<PARAMS> getAgentModule() {
		return (UT2004BotModule<PARAMS>) super.getAgentModule();
	}
	
}
