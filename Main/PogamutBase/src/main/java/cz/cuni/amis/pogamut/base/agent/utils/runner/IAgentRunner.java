package cz.cuni.amis.pogamut.base.agent.utils.runner;

import java.util.List;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.params.IAgentParameters;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Utility interface for classes that can instantiate & start (possibly multiple) agents at once. All agents must be
 * of the same class though. Note that this usually is not a problem as the agent class is a shell that may
 * contain arbitrary agent's logic implementations. (If it proves to be a problem, check whether there is a suitable
 * implementation of the {@link IMultipleAgentRunner} implementation that you can use.)
 * <p><p>
 * Every implementor is instantiated with default values that should be passed into the agent when no
 * other parameters are provided. Therefore you may use {@link IAgentRunner#startAgent()} to start agent
 * with default params, or {@link IAgentRunner#startAgents(int)} and {@link IAgentRunner#startAgents(IAgentParameters...)}
 * where you may specify your own params.
 * <p><p>
 * Note that the {@link IAgentRunner} utilize {@link IAgentParameters#assignDefaults(IAgentParameters)} to fill
 * missing fields into {@link IAgentParameters} which allows you to instantiate the {@link IAgentParameters} implementor
 * with custom data leaving the rest to the {@link IAgentRunner} (eases the pain of starting agents greatly).
 * <p><p>
 * The interface also provides a "synchronizing" feature via {@link IAgentRunner#setPausing(boolean)}. If set true,
 * the runner will pause all agents after they start and resume them at once when all agents have been instantiated.
 * <p>
 * Pausing behavior is disabled (== set to false) as default.
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
 * @author jimmy
 */
public interface IAgentRunner<AGENT extends IAgent, PARAMS extends IAgentParameters> {	
	
	/**
	 * Starts the agent by providing default parameters (defined during the construction of the implementor).
	 * 
	 * @return agent instance configured with default parameters that has been started
	 * @throws PogamutException
	 */
	public AGENT startAgent() throws PogamutException;
		
	/**
	 * Starts agents by providing every one of them with default parameters 
	 * (defined during the construction of the implementor).
	 * <p><p>
	 * Note that if any instantiation/start of the agent fails, all agents are killed before the method throws 
	 * the exception.
	 * 
	 * @param count how many agents should be started
	 * @return list of started agents
	 * @throws PogamutException
	 */
	public List<AGENT> startAgents(int count) throws PogamutException;
		
    /**
     * Start an agent instance configured with 'agentsParameters'. The length of the 'agentsParameters' array
     * determines how many agents are going to be started.
     * <p><p>
	 * Note that if any instantiation/start of the agent fails, all agents are killed before the method throws 
	 * the exception.
	 * <p><p>
	 * WARNING: if you want to use this method, you have to carefully type your runner instance, i.e., provide java-generic &lt;AGENT_CLASS&gt;
	 * information, otherwise Java won't compile your code correctly!
     * 
     * @param agentsParameters
     * @return list of started agents
     */
	public List<AGENT> startAgents(PARAMS... agentsParameters) throws PogamutException;

	/**
	 * Sets the pausing behavior.
	 * <p><p>
	 * If set true, the runner will pause all agents after their construction and resume them 
	 * at once whenever all agents has been successfully started.
	 * 
	 * @param state
	 * @return this instance
	 */
	public IAgentRunner<AGENT, PARAMS> setPausing(boolean state);
	
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
	 * Sets 'main' functionality (see the Javadoc for the whole class).
	 * @param state
	 * @return
	 */
	public IAgentRunner<AGENT, PARAMS> setMain(boolean state);
	
	/**
	 * Whether the runner is set to provide 'main' functionality (see the Javadoc for the whole class).
	 * @return
	 */
	public boolean isMain();
	
}
