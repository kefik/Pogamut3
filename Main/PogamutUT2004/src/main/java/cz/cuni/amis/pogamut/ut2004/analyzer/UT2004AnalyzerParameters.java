package cz.cuni.amis.pogamut.ut2004.analyzer;

import java.util.Map;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.params.IAgentParameters;
import cz.cuni.amis.pogamut.base.communication.connection.IWorldConnectionAddress;
import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.SocketConnectionAddress;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004ObserverModule;

/**
 * Agent parameters are meant to provide run-time parameters needed by {@link UT2004Analyzer}.
 * <p><p>
 * Crucial parameters that (even though are present in the {@link UT2004AnalyzerRunner} might be needed
 * in order to customize whole {@link UT2004Analyzer}: {@link UT2004AnalyzerParameters#setObserverModule(UT2004AnalyzerObserverModule).
 * <p><p>
 * NOTE: all {@link IAgentParameters} implementors are usually used together with {@link IAgentRunner} or {@link IMultipleAgentRunner}
 * which usually contains sensible default params, therefore there is no need to set all parameters
 * into newly created ones as runners will supply them via {@link IAgentParameters#assignDefaults(IAgentParameters)}.
 *
 * @see UT2004AgentParameters
 * @author Jimmy
 */
/**
 * @author Jimmy
 *
 */
public class UT2004AnalyzerParameters extends UT2004AgentParameters {

	private SocketConnectionAddress observerAddress;
	private UT2004AnalyzerFullObserverModule observerModule;
	private String outputPath;
	private Boolean waitForMatchRestart = null;
	private Map<UnrealId, String> fileNames = null;
	private Boolean humanLikeObserving = null;
	
	/**
	 * If you need to populate the parameters after instantiation, use setters available in this
	 * class: {@link UT2004AnalyzerParameters#setAgentId(IAgentId)}, {@link UT2004AnalyzerParameters#setWorldAddress(IWorldConnectionAddress)},
	 * {@link UT2004AnalyzerParameters#setObserverModule(UT2004ObserverModule)}.
	 */
	public UT2004AnalyzerParameters() {
		super();
	}

	public UT2004AnalyzerFullObserverModule getObserverModule() {
		return observerModule;
	}

	/**
	 * Sets observer module (one that will be used to construct new {@link UT2004AnalyzerObserver} in 
	 * order to sniff info about connected bots.
	 * <p><p>
	 * WARNING: Note that you should not mess with 'setters' in different threads as they
	 * are non-thread-safe and may interrupt horrible agent instantiations with such behavior.
	 * @param address
	 * @return this instance
	 */
	public UT2004AnalyzerParameters setObserverModule(UT2004AnalyzerFullObserverModule observerModule) {
		this.observerModule = observerModule;
		return this;
	}
	
	@Override
	public UT2004AnalyzerParameters setAgentId(IAgentId agentId) {
		super.setAgentId(agentId);
		return this;
	}
	
	@Override
	public UT2004AnalyzerParameters setWorldAddress(IWorldConnectionAddress address) {
		super.setWorldAddress(address);
		return this;
	}
	
	/**
	 * Contains path to directory where the observer should output its results. MUST POINT TO DIR!
	 * @return
	 */
	public String getOutputPath() {
		return outputPath;
	}

	/**
	 * Sets path to directory where the observer should output its results. MUST POINT TO DIR!
	 * @param outputPath
	 */
	public UT2004AnalyzerParameters setOutputPath(String outputPath) {
		this.outputPath = outputPath;
		return this;
	}
	
	/**
	 * Whether the analyzer's observers should wait for match-restart before it starts to collect data.
	 * @return
	 */
	public boolean isWaitForMatchRestart() {
		return waitForMatchRestart == null ? false : waitForMatchRestart;
	}

	/**
	 * Sets whether the analyzer's observers should wait for match-restart before it starts to collect data.
	 * @param waitForMatchRestart
	 */
	public UT2004AnalyzerParameters setWaitForMatchRestart(boolean waitForMatchRestart) {
		this.waitForMatchRestart = waitForMatchRestart;
		return this;
	}	
	
	/**
	 * This may be used to provide concrete filenames for outputting stats for bots identified by their id.
	 * @return
	 */
	public Map<UnrealId, String> getFileNames() {
		return fileNames;
	}

	/**
	 * This may be used to provide concrete filenames for outputting stats for bots identified by their id.
	 * @param fileNames
	 * @return
	 */
	public UT2004AnalyzerParameters setFileNames(Map<UnrealId, String> fileNames) {
		this.fileNames = fileNames;
		return this;
	}

	/**
	 * Returns observer address that should be used for spawning new observers.
	 * @return
	 */
	public SocketConnectionAddress getObserverAddress() {
		return observerAddress;
	}

	/**
	 * Sets observer address that should be used for spawning new observers. If you do not specify it a default address will be used.
	 * @param observerAddress
	 */
	public UT2004AnalyzerParameters setObserverAddress(SocketConnectionAddress observerAddress) {
		this.observerAddress = observerAddress;
		return this;
	}

	/**
	 * Whether to produce logs for "HumanLikeBot project" analysis.
	 * @return
	 */
	public Boolean getHumanLikeObserving() {
		return humanLikeObserving;
	}

	/**
	 * Whether to produce logs for "HumanLikeBot project" analysis.
	 * @param humanLikeObserving
	 */
	public UT2004AnalyzerParameters setHumanLikeObserving(Boolean humanLikeObserving) {
		this.humanLikeObserving = humanLikeObserving;
		return this;
	}

	@Override
	public void assignDefaults(IAgentParameters defaults) {
		super.assignDefaults(defaults);
		if (defaults instanceof UT2004AnalyzerParameters) {
			if (observerModule == null) observerModule = ((UT2004AnalyzerParameters)defaults).getObserverModule();
			if (observerAddress == null) observerAddress = ((UT2004AnalyzerParameters)defaults).getObserverAddress();
			if (outputPath == null) outputPath = ((UT2004AnalyzerParameters)defaults).getOutputPath();
			if (waitForMatchRestart == null && ((UT2004AnalyzerParameters)defaults).waitForMatchRestart != null)
				waitForMatchRestart = ((UT2004AnalyzerParameters)defaults).waitForMatchRestart;
			if (fileNames == null && ((UT2004AnalyzerParameters)defaults).fileNames != null) {
				this.fileNames = ((UT2004AnalyzerParameters)defaults).fileNames;
			}
			if (humanLikeObserving == null) {
				humanLikeObserving = ((UT2004AnalyzerParameters)defaults).humanLikeObserving;
			}
		}
	}	
	
}
