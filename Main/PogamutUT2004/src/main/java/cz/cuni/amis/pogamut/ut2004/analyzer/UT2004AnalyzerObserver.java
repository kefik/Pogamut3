package cz.cuni.amis.pogamut.ut2004.analyzer;

import java.io.File;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.ConfigurationObserver;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.InitializeObserver;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameRestarted;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.MyInventory;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.communication.worldview.UT2004WorldView;
import cz.cuni.amis.pogamut.ut2004.observer.impl.UT2004Observer;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Base class implementing {@link IUT2004AnalyzerObserver}, does not add that much functionality, except
 * starting the observation for desired agent and abide watching out for {@link GameRestarted} so you
 * have easy work to restart the observation data collection (and to abide {@link UT2004AnalyzerObserverParameters#isWaitForMatchRestart()}).
 * See {@link UT2004AnalyzerObserver#gameRestartStarted()} and {@link UT2004AnalyzerObserver#gameRestartEnd()}.
 * 
 * @author Jimmy
 */
@Deprecated
public class UT2004AnalyzerObserver extends UT2004Observer implements IUT2004AnalyzerObserver {
	
	private UnrealId observedBotId;	
	
	private IWorldEventListener<GameRestarted> gameRestartedListener = new IWorldEventListener<GameRestarted>() {

		@Override
		public void notify(GameRestarted event) {
			if (event.isStarted()) {
				gameRestartStarted();
			} else 
			if (event.isFinished()) {
				gameRestartEnd();
			} else {
				throw new PogamutException("GameRestarted has started==false && finished==false as well, invalid!", this);
			}
		}
		
	};
	
	@Inject
	public UT2004AnalyzerObserver(UT2004AnalyzerObserverParameters params,
			IComponentBus bus, IAgentLogger agentLogger,
			UT2004WorldView worldView, IAct act) {
		super(params, bus, agentLogger, worldView, act);
		observedBotId = UnrealId.get(params.getObservedAgentId());
		getWorldView().addEventListener(GameRestarted.class, gameRestartedListener);
	}
	
	@Override
	public UT2004AnalyzerObserverParameters getParams() {
		return (UT2004AnalyzerObserverParameters) super.getParams();
	}

	@Override
	public UnrealId getObservedBotId() {
		return observedBotId;
	}
	
	/**
	 * Returns path to file that should be used for outputting the data
	 * @return
	 */
	public String getOutputFilePath() {
		String path = getParams().getOutputPath();
		if (path == null) path = ".";
		path += File.separator;
		if (getParams().getFileName() != null) {
			path += getParams().getFileName();
		} else {
			path += getObservedBotId().toString();
			path += ".csv";
		}
		return path;
	}
	
	/**
	 * Called whenever {@link GameRestart} message with {@link GameRestarted#isStarted()} is received.
	 * <p><p>
	 * You probably won't need to override this method, better override {@link UT2004AnalyzerObserver#gameRestartEnd()}, that
	 * is the place where you should reset data collection statistics / start them in case of {@link UT2004AnalyzerObserverParameters#isWaitForMatchRestart()}.
	 * <p><p>
	 * Current implementation is empty.
	 */
	protected void gameRestartStarted() {
	}
	
	/**
	 * Called whenever {@link GameRestart} message with {@link GameRestarted#isFinished()} is received.
	 * <p><p>
	 * Place where you should reset data collection statistics / start them 
	 * in case of {@link UT2004AnalyzerObserverParameters#isWaitForMatchRestart()}.
	 * <p><p>
	 * Current implementation is empty.
	 */
	protected void gameRestartEnd() {
	}
	
	/**
	 * Initialize the observer to listen on the {@link UT2004AnalyzerObserverParameters#getObservedAgentId()} that is obtained from
	 * the {@link UT2004AnalyzerObserver#getParams()}.
	 */
	@Override
	protected void startAgent() {
		super.startAgent();
		getAct().act(new InitializeObserver().setId(getParams().getObservedAgentId()));
		configureObserver();
	}
	
	@Override
	protected void startPausedAgent() {
		super.startPausedAgent();
		getAct().act(new InitializeObserver().setId(getParams().getObservedAgentId()));
		configureObserver();
	}
	
	/**
	 * Called from the {@link UT2004AnalyzerObserver#startAgent()} after {@link InitializeObserver} command
	 * is sent to configure the observer instance.
	 * <p><p>
	 * Actually enables {@link Self}, {@link MyInventory} and async messages (i.e., {@link BotKilled}).
	 */
	protected void configureObserver() {
		getAct().act(new ConfigurationObserver().setUpdate(0.2).setAll(true).setSelf(true).setAsync(true).setGame(false).setSee(false).setSpecial(false));		
	}

}
