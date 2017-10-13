package cz.cuni.amis.pogamut.base.agent.utils.runner;

import java.util.List;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.params.IAgentParameters;
import cz.cuni.amis.pogamut.base.factory.guice.GuiceAgentModule;

/**
 * Utility interface for classes that can instantiate & start (possibly multiple) agents at once. Agents may be also
 * of different classes.
 * <p><p>
 * Every implementor is instantiated with default values that should be passed into the agent when no
 * other parameters are provided.
 * <p><p>
 * Note that the {@link IMultipleAgentRunner} utilize {@link IAgentParameters#assignDefaults(IAgentParameters)} to fill
 * missing fields into {@link IAgentParameters} which allows you to instantiate the {@link IAgentParameters} implementor
 * with custom data leaving the rest to the {@link IMultipleAgentRunner} (eases the pain of starting agents greatly).
 * <p><p>
 * The interface also provides a "synchronizing" feature via {@link IMultipleAgentRunner#setPausing(boolean)}. If set true,
 * the runner will pause all agents after their construction and resume them at once when all agents has been instantiated.
 * <p>
 * Pausing behavior is disabled (== set to false) as default.
 * <p><p>
 * NOTE: it might seem strange why there exists {@link IAgentRunner} and {@link IMultipleAgentRunner} interfaces when
 * {@link IAgentRunner} can be implemented using {@link IMultipleAgentRunner}. Even though that is true, it would
 * be infeasible as {@link IMultipleAgentRunner} always needs to instantiate new factories for every {@link IAgentDescriptor}
 * passed (unlike the {@link IAgentRunner} that may utilize the same factory instance repeatedly). 
 * <p><p>
 * <b>USING FROM THE main(String[] args) METHOD</b>
 * <p>
 * Starting agents from the main method requires special care:
 * <ol>
 * <li>if one of your agents fails, all agents should be closed (simulation has been broken)</li>
 * <li>when all your agent dies, Pogamut platform should be closed (so the JVM could terminate)</li>
 * </ol>
 * Previous two points are not-so-easy to implement (and we won't bother you with them). Instead, you
 * could just call {@link IAgentRunner#setMain(boolean)} with 'true' and the runner will behave differently.
 * (Note that all startAgent methods will block!)
 * 
 * @author Jimmy
 * 
 * @param <AGENT> common ancestor of all agents that are going to be started
 * @param <PARAMS>
 * @param <MODULE>
 */
public interface IMultipleAgentRunner<AGENT extends IAgent, PARAMS extends IAgentParameters, MODULE extends GuiceAgentModule> {

	 /**
     * Start an agent instances described by 'agentDescriptors'. The method creates a new factory
     * for every descriptor (as it must use different agent modules). The length of the 'agentDescriptors' array
     * together with {@link IAgentDescriptor#getCount()} determines how many agents are going to be instantiated and started.
     * <p><p>
	 * Note that if any instantiation/start of the agent fails, all agents are killed before the method throws 
	 * the exception.
     * 
     * @param agentsParameters
     * @return array of started agents
     */
	public List<AGENT> startAgents(IAgentDescriptor<PARAMS, MODULE>... agentDescriptors);
	
	/**
	 * Sets the pausing behavior.
	 * <p><p>
	 * If set true, the runner will pause all agents after their construction and resume them 
	 * at once whenever all agents has been successfully started.
	 * 
	 * @param state
	 * @return this instance
	 */
	public IMultipleAgentRunner<AGENT, PARAMS, MODULE> setPausing(boolean state);
	
	/**
	 * Tells, whether the pausing behavior is enabled.
	 * <p><p>
	 * If enabled, the runner will pause all agents after their construction and resume them 
	 * at once whenever all agents has been instantiated.
	 * 
	 * @return state of the pausing behavior
	 */
	public boolean isPausing();
	
	/**
	 * Sets 'main' functionality.
	 * @param state
	 * @return
	 */
	public IMultipleAgentRunner<AGENT, PARAMS, MODULE> setMain(boolean state);
	
	/**
	 * Whether the runner is set to provide 'main' functionality.
	 * @return
	 */
	public boolean isMain();
}
