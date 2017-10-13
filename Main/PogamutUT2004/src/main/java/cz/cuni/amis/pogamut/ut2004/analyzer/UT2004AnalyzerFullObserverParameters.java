package cz.cuni.amis.pogamut.ut2004.analyzer;

import java.io.PrintWriter;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.params.IAgentParameters;
import cz.cuni.amis.pogamut.base.communication.connection.IWorldConnectionAddress;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;

public class UT2004AnalyzerFullObserverParameters extends UT2004AgentParameters {

	private String observedAgentId;
	
	private Boolean waitForMatchRestart = null;
	
	private String outputPath = null;
	
	private Boolean humanLike_observingEnabled = null;
	
	/**
	 * FileName to be used for the output. Must end with some file extension (e.g., .csv).
	 */
	private String fileName;

	private PrintWriter humanLike_writer;

	private String humanLike_botName;
	
	public UT2004AnalyzerFullObserverParameters() {
		super();
	}

	/**
	 * Returns ID of the player that should be observed.
	 * @return
	 */
	public String getObservedAgentId() {
		return observedAgentId;
	}

	/**
	 * Sets 'id' (usually taken from the {@link UnrealId} that is present for instance in {@link Self#getId()}) 
	 * of the bot that is going to be observed by newly created observer.
	 * <p><p>
	 * WARNING: Note that you should not mess with 'setters' in different threads as they
	 * are non-thread-safe and may interrupt horrible agent instantiations with such behavior.
	 * @param address
	 * @return this instance
	 */
	public UT2004AnalyzerFullObserverParameters setObservedAgentId(String observedAgentId) {
		this.observedAgentId = observedAgentId;
		return this;
	}
	
	@Override
	public UT2004AnalyzerFullObserverParameters setAgentId(IAgentId agentId) {
		super.setAgentId(agentId);
		return this;
	}
	
	@Override
	public UT2004AnalyzerFullObserverParameters setWorldAddress(IWorldConnectionAddress address) {
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
	 * Sets  path to directory where the observer should output its results. MUST POINT TO DIR!
	 * @param outputPath
	 */
	public UT2004AnalyzerFullObserverParameters setOutputPath(String outputPath) {
		this.outputPath = outputPath;
		return this;
	}

	/**
	 * Whether the observer should wait for match-restart before it starts to collect data.
	 * @return
	 */
	public boolean isWaitForMatchRestart() {
		return waitForMatchRestart == null ? false : waitForMatchRestart;
	}

	/**
	 * Sets whether the observer should wait for match-restart before it starts to collect data.
	 * @param waitForMatchRestart
	 */
	public UT2004AnalyzerFullObserverParameters setWaitForMatchRestart(boolean waitForMatchRestart) {
		this.waitForMatchRestart = waitForMatchRestart;
		return this;
	}	
	
	/**
	 * Returns file name that should be used for outputting data (just file name, must be combined with {@link UT2004AnalyzerObserver#getOutputFilePath()}.
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets FileName to be used for the output. Must end with some file extension (e.g., .csv). (Just file name, will be combined with {@link UT2004AnalyzerObserver#getOutputFilePath()}.
	 * @param fileName
	 */
	public UT2004AnalyzerFullObserverParameters setFileName(String fileName) {
		this.fileName = fileName;
		return this;
	}
	
	/**
	 * Whether to produce additional output for "HumanLikeBot" project analysis.
	 * @return
	 */
	public Boolean isHumanLikeObservingEnabled() {
		return humanLike_observingEnabled;
	}
	
	public PrintWriter getHumanLikeWriter() {
		return humanLike_writer;
	}
	
	public String getHumanLikeBotName() {
		return humanLike_botName;
	}

	public UT2004AnalyzerFullObserverParameters setHumanLikeObserving(String botName, PrintWriter humanLikeWriter) {
		this.humanLike_observingEnabled = humanLikeWriter != null;
		this.humanLike_botName = humanLikeWriter != null ? botName : null;		
		this.humanLike_writer = humanLikeWriter;
		return this;
	}

	@Override
	public void assignDefaults(IAgentParameters defaults) {
		super.assignDefaults(defaults);
		if (defaults instanceof UT2004AnalyzerFullObserverParameters) {
			if (waitForMatchRestart == null && ((UT2004AnalyzerFullObserverParameters)defaults).waitForMatchRestart != null)
				waitForMatchRestart = ((UT2004AnalyzerFullObserverParameters)defaults).waitForMatchRestart;
			if (outputPath == null) outputPath = ((UT2004AnalyzerFullObserverParameters)defaults).getOutputPath();
			if (fileName == null) fileName = ((UT2004AnalyzerFullObserverParameters)defaults).getFileName();
			if (humanLike_observingEnabled == null) humanLike_observingEnabled = ((UT2004AnalyzerFullObserverParameters)defaults).isHumanLikeObservingEnabled();
			if (humanLike_botName == null) humanLike_botName = ((UT2004AnalyzerFullObserverParameters)defaults).getHumanLikeBotName();
			if (humanLike_writer == null) humanLike_writer = ((UT2004AnalyzerFullObserverParameters)defaults).getHumanLikeWriter();
		}
	}

	

	
	
}
