package cz.cuni.amis.pogamut.ut2004.analyzer;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.params.IAgentParameters;
import cz.cuni.amis.pogamut.base.communication.connection.IWorldConnectionAddress;
import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;

@Deprecated
public class UT2004AnalyzerObserverParameters extends UT2004AgentParameters {

	private String observedAgentId;
	
	private Boolean waitForMatchRestart = null;
	
	private String outputPath = null;
	
	/**
	 * FileName to be used for the output. Must end with some file extension (e.g., .csv).
	 */
	private String fileName;
	
	public UT2004AnalyzerObserverParameters() {
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
	public UT2004AnalyzerObserverParameters setObservedAgentId(String observedAgentId) {
		this.observedAgentId = observedAgentId;
		return this;
	}
	
	@Override
	public UT2004AnalyzerObserverParameters setAgentId(IAgentId agentId) {
		super.setAgentId(agentId);
		return this;
	}
	
	@Override
	public UT2004AnalyzerObserverParameters setWorldAddress(IWorldConnectionAddress address) {
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
	public UT2004AnalyzerObserverParameters setOutputPath(String outputPath) {
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
	public UT2004AnalyzerObserverParameters setWaitForMatchRestart(boolean waitForMatchRestart) {
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
	public UT2004AnalyzerObserverParameters setFileName(String fileName) {
		this.fileName = fileName;
		return this;
	}

	@Override
	public void assignDefaults(IAgentParameters defaults) {
		super.assignDefaults(defaults);
		if (defaults instanceof UT2004AnalyzerObserverParameters) {
			if (waitForMatchRestart == null && ((UT2004AnalyzerObserverParameters)defaults).waitForMatchRestart != null)
				waitForMatchRestart = ((UT2004AnalyzerObserverParameters)defaults).waitForMatchRestart;
			if (outputPath == null) outputPath = ((UT2004AnalyzerObserverParameters)defaults).getOutputPath();
			if (fileName == null) fileName = ((UT2004AnalyzerObserverParameters)defaults).getFileName();
		}
	}

	

	
	
}
