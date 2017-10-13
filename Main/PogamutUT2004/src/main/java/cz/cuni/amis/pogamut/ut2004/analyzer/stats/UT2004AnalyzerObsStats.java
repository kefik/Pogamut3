package cz.cuni.amis.pogamut.ut2004.analyzer.stats;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentStats;
import cz.cuni.amis.pogamut.ut2004.analyzer.UT2004AnalyzerFullObserver;
import cz.cuni.amis.pogamut.ut2004.analyzer.UT2004AnalyzerFullObserverParameters;
import cz.cuni.amis.pogamut.ut2004.analyzer.UT2004AnalyzerObserver;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.ConfigurationObserver;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.InitializeObserver;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.MyInventory;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.communication.worldview.UT2004WorldView;

/**
 * Observer that provides logging of bot's health, number of deaths, traveled distance and bot's location, etc.
 * It is directly using {@link AgentStats} for that purpose.
 */
public class UT2004AnalyzerObsStats extends UT2004AnalyzerFullObserver {

	private AgentStats stats;
    
	@Inject
	public UT2004AnalyzerObsStats(UT2004AnalyzerFullObserverParameters params,
			                      IComponentBus bus,         IAgentLogger agentLogger,
			                      UT2004WorldView worldView, IAct act) {
		super(params, bus, agentLogger, worldView, act);
		stats = new AgentStats(this);
		stats.setObserver(true);
		if (getParams().isWaitForMatchRestart()) {
			stats.setLogBeforeMatchRestart(false);
		} else {
			stats.startOutput(getOutputFilePath(), true);
		}
	}

	/**
	 * Returns detailed statistics about the agent.
	 * @return
	 */
	public AgentStats getStats() {
		return stats;
	}

	@Override
	protected void gameRestartEnd() {
		stats.startOutput(getOutputFilePath(), true);
	}
	
	/**
	 * Called from the {@link UT2004AnalyzerObserver#startAgent()} after {@link InitializeObserver} command
	 * is sent to configure the observer instance.
	 * <p><p>
	 * Actually enables {@link Self}, {@link MyInventory} and async messages (i.e., {@link BotKilled}).
	 */
	protected void configureObserver() {
		getAct().act(new ConfigurationObserver().setUpdate(0.25).setAll(true).setSelf(true).setAsync(true).setGame(false).setSee(false).setSpecial(false));		
	}
	
}
