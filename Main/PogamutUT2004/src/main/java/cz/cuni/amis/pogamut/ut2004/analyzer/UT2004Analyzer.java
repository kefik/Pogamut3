package cz.cuni.amis.pogamut.ut2004.analyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateUp;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateRunning;
import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.SocketConnection;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectListener;
import cz.cuni.amis.pogamut.base.communication.worldview.react.EventReactOnce;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.StartPlayers;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.EndMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerJoinsGame;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerLeft;
import cz.cuni.amis.pogamut.ut2004.communication.worldview.UT2004WorldView;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004AnalyzerFactory;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004ObserverFactory;
import cz.cuni.amis.pogamut.ut2004.observer.IUT2004Observer;
import cz.cuni.amis.pogamut.ut2004.server.impl.UT2004Server;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004AnalyzerRunner;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004ObserverRunner;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;
import cz.cuni.amis.utils.listener.Listeners;

/**
 * UT2004Analyzer can be used to automatically observe all bots/players in the game sniff their messages.
 * <p><p>
 * It creates and launches {@link IUT2004AnalyzerObserver} that is constructed according to the {@link UT2004AnalyzerParameters#getObserverModule()}.
 * 
 * @author Jimmy
 */
public class UT2004Analyzer extends UT2004Server implements IUT2004Analyzer {

	private Object mutex = new Object();
	
	private Listeners<IAnalyzerObserverListener> observerListeners = new Listeners<IAnalyzerObserverListener>();
	private IAnalyzerObserverListener.ObserverAddedNotifier observerAddedNotifier = new IAnalyzerObserverListener.ObserverAddedNotifier();
	private IAnalyzerObserverListener.ObserverRemovedNotifier observerRemovedNotifier = new IAnalyzerObserverListener.ObserverRemovedNotifier();
	
	private void addObserver(UnrealId botId, String botName, boolean forced) {
		if (!forced && !inState(IAgentStateRunning.class)) {
			if (log.isLoggable(Level.INFO)) log.info("Not running yet, could not add observer for " + botId + ".");
			return;
		}
		synchronized(observers) {
			if (observers.containsKey(botId)) return;
			if (log.isLoggable(Level.INFO)) log.info("New bot has connected to the game, creating new observer for the bot with id '" + botId.getStringId() + "'.");
			String fileName = getParams().getFileNames() != null && getParams().getFileNames().get(botId) != null ? getParams().getFileNames().get(botId) + ".csv" : botId + ".csv";
			IUT2004AnalyzerObserver observer = getObserverRunner().startAgents(
					new UT2004AnalyzerFullObserverParameters()
						.setObservedAgentId(botId.getStringId())
						.setOutputPath(getParams().getOutputPath())
						.setWaitForMatchRestart(getParams().isWaitForMatchRestart())
						.setFileName(fileName)
						.setWorldAddress(getParams().getObserverAddress())
						.setHumanLikeObserving(botName, humanLike_writer)
			).get(0);
			observers.put(botId, observer);
			observerAddedNotifier.setBotId(botId);
			observerAddedNotifier.setObserver(observer);
			observerListeners.notify(observerAddedNotifier);
			nonObserved.remove(botId);
		}
	}
	
	private void removeObserver(UnrealId botId) {
		synchronized(observers) {
			if (log.isLoggable(Level.INFO)) log.info("Bot '" + botId.getStringId() + "' has left the game");
			IUT2004AnalyzerObserver observer = observers.get(botId);						
			if (observer != null) {
				if (log.isLoggable(Level.INFO)) log.info("Stopping observer for the bot.");
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					throw new PogamutInterruptedException(e, this);
				}
				if (observer.getState().getFlag() instanceof IAgentStateUp) {
					try {
						observer.stop();
					} catch (Exception e) {
						log.warning("Observer for the bot '" + observer.getObservedBotId().getStringId() + "' could not be stopped, killing...");
						try {
							observer.kill();
						} catch (Exception e2) {
							log.warning("Observer for the bot '" + observer.getObservedBotId().getStringId() + "' could not be killed: " + e2.getMessage());
						}
					}
				}
				observers.remove(botId);
				observerRemovedNotifier.setBotId(botId);
				observerRemovedNotifier.setObserver(observer);
				observerListeners.notify(observerRemovedNotifier);				
			}
			nonObserved.remove(botId); // must be here in case of "PlayerLeft"!
		}
	}
	
	private IWorldObjectListener<Player> playerListener = new IWorldObjectListener<Player>() {
		@Override
		public void notify(IWorldObjectEvent<Player> event) {			
			Player plr = event.getObject();
			if (!observers.containsKey(plr.getId())) return;
			synchronized(observers){ 
				if (plr.isSpectator()) {
					// DO NOT OBSERVE SPECTATORS!
					removeObserver(plr.getId());
					// MARK AS SPECTATOR
					nonObserved.add(plr.getId());
				} else {
					if (!observers.containsKey(plr.getId())) addObserver(plr.getId(), plr.getName(), false);
				}
			}
		}
	};
	
	private IWorldEventListener<PlayerJoinsGame> playerJoinsGameListener = new IWorldEventListener<PlayerJoinsGame>() {
		@Override
		public void notify(PlayerJoinsGame event) {
			addObserver(event.getId(), event.getName(), false);
		}
	};
	
	private IWorldEventListener<PlayerLeft> playerLeftListener = new IWorldEventListener<PlayerLeft>() {

		@Override
		public void notify(PlayerLeft event) {
			removeObserver(event.getId());
		}
		
	};
	
	/**
	 * Stored pointers to observers the analyzer owns.
	 */
	private Map<UnrealId, IUT2004AnalyzerObserver> observers = new HashMap<UnrealId, IUT2004AnalyzerObserver>();

	/**
	 * Contains {@link UnrealId} of {@link Player}s that are within the game (we should observe them), but they are spectators,
	 * therefore, they are not inside 'observers'. 
	 */
	private Set<UnrealId> nonObserved = new HashSet<UnrealId>();
	
	/**
	 * Runner that is used to start new instances of {@link IUT2004AnalyzerObserver}
	 */
	private UT2004ObserverRunner<IUT2004AnalyzerObserver, UT2004AnalyzerFullObserverParameters> observerRunner;
	
	private File humanLike_outputFile = null;
	
	private PrintWriter humanLike_writer = null;
	
	@Inject
	public UT2004Analyzer(UT2004AnalyzerParameters params,
			IAgentLogger agentLogger, IComponentBus bus,
			SocketConnection connection, UT2004WorldView worldView, IAct act) {
		super(params, agentLogger, bus, connection, worldView, act);
		
		observerListeners.setLog(log, "ObserverListeners");
		
		// WE NEED THESE LISTENERS AS SOON AS POSSIBLE... otherwise we might miss some events
		
		getWorldView().addEventListener(PlayerJoinsGame.class, playerJoinsGameListener);
		getWorldView().addEventListener(PlayerLeft.class, playerLeftListener);
		
		// WARNING PLAYER UPDATES ARE TRICKY!
		// if some bot leaves the play, first PlayerLeft message is received, but after that (sometimes) another PLR message is received, causing observer to start for already-left bot :(
		getWorldView().addObjectListener(Player.class, playerListener);
	}
	
	private UT2004ObserverRunner<IUT2004AnalyzerObserver, UT2004AnalyzerFullObserverParameters> getObserverRunner() {
		if (observerRunner == null) {
			synchronized(mutex) {
				if (observerRunner == null) {
					observerRunner = 
						new UT2004ObserverRunner(
							new UT2004ObserverFactory<IUT2004Observer, UT2004AnalyzerFullObserverParameters>(
								getParams().getObserverModule())
							);
				}
			}
		}		
		return observerRunner; 
	}
	
	@Override
	public UT2004AnalyzerParameters getParams() {
		return (UT2004AnalyzerParameters) super.getParams();
	}

	@Override
	public Map<UnrealId, IUT2004AnalyzerObserver> getObservers() {
		synchronized(observers) {
			return new HashMap<UnrealId, IUT2004AnalyzerObserver>(observers);
		}
	}
	
	@Override
	protected void startAgent() {
		super.startAgent();
		if (getParams().getHumanLikeObserving() != null && getParams().getHumanLikeObserving()) {
			File humanLike_outputFileDir = new File(getParams().getOutputPath());
			humanLike_outputFileDir.mkdirs();
			humanLike_outputFile = new File(getParams().getOutputPath() + System.getProperty("file.separator") + "humanLikeData.log");
			try {
				humanLike_writer = new PrintWriter(new FileOutputStream(humanLike_outputFile));
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Could not create writer for human-like log at: " + humanLike_outputFile.getAbsolutePath());
			}
		}
	}
	
	@Override
	protected void startPausedAgent() {
		super.startPausedAgent();
		if (getParams().getHumanLikeObserving() != null && getParams().getHumanLikeObserving()) {
			File humanLike_outputFileDir = new File(getParams().getOutputPath());
			humanLike_outputFileDir.mkdirs();
			humanLike_outputFile = new File(getParams().getOutputPath() + System.getProperty("file.separator") + "humanLikeData.log");
			try {
				humanLike_writer = new PrintWriter(new FileOutputStream(humanLike_outputFile));
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Could not create writer for human-like log at: " + humanLike_outputFile.getAbsolutePath());
			}			
		}		
	}
	
	@Override
	protected void stopAgent() {
		super.stopAgent();
		cleanUp();
	}
	
	@Override
	protected void killAgent() {
		super.killAgent();
		cleanUp();
	}
	
	private EventReactOnce addObserversCallback;
	
	protected void init() {
		super.init();
		synchronized(mutex) {
	        getAct().act(new StartPlayers(true, true, true));
	        addObserversCallback = new EventReactOnce<EndMessage>(EndMessage.class, getWorldView()) {
				@Override
				protected void react(EndMessage event) {
					 for (Player player : getWorldView().getAll(Player.class).values()) {
						addObserver(player.getId(), player.getName(), true);					
			        }
				}
			};
	       
    	}
    }

	/**
	 * Called from {@link UT2004Analyzer#stopAgent()} and {@link UT2004Analyzer#killAgent()} to clean up stuff (stops observers).
	 */
	protected void cleanUp() {
		synchronized(observers) {
			addObserversCallback.disable();
			for (IUT2004AnalyzerObserver observer : observers.values()) {
				if (observer.getState().getFlag() instanceof IAgentStateUp) {
					try {
						observer.stop();
					} catch (Exception e) {
						if (log.isLoggable(Level.WARNING)) log.warning("Observer for the bot '" + observer.getObservedBotId().getStringId() + "' could not be stopped, killing...");
						try {
							observer.kill();
						} catch (Exception e2) {
							if (log.isLoggable(Level.WARNING)) log.warning("Observer for the bot '" + observer.getObservedBotId().getStringId() + "' could not be killed: " + e2.getMessage());
						}
					}
				}
			}
			if (humanLike_writer != null) {
				try {
					humanLike_writer.close();
				} catch (Exception e) {
					if (humanLike_outputFile != null) {
						log.warning("Failed to close human-like log file at: " + humanLike_outputFile.getAbsolutePath());
					} else {
						log.warning("Failed to close human-like log file (unknown location, weird as well...)!");
					}
				}
				humanLike_outputFile = null;
				humanLike_writer = null;
			}
		}
	}
	
	@Override
	public void addListener(IAnalyzerObserverListener listener) {
		observerListeners.addWeakListener(listener);
	}

	@Override
	public boolean isListening(IAnalyzerObserverListener listener) {
		return observerListeners.isListening(listener);
	}

	@Override
	public void removeListener(IAnalyzerObserverListener listener) {
		observerListeners.removeListener(listener);
	}
	
	public static void main(String[] args) {
		UT2004AnalyzerRunner<IUT2004Analyzer, UT2004AnalyzerParameters> analyzerRunner = new UT2004AnalyzerRunner<IUT2004Analyzer, UT2004AnalyzerParameters>(
    		new UT2004AnalyzerFactory(
    			new UT2004AnalyzerModule()
    		)
    	);
    	analyzerRunner.setLogLevel(Level.INFO);
    	analyzerRunner.setMain(true).startAgent();
	}


}
