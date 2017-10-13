package cz.cuni.amis.pogamut.ut2004.vip.server;

import java.net.URI;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateRunning;
import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.SocketConnection;
import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.SocketConnectionAddress;
import cz.cuni.amis.pogamut.base.communication.messages.CommandMessage;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.levelGeometry.LevelGeometryModule;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.drawing.IUT2004ServerProvider;
import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Configuration;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.GameConfiguration;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.KillBot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Respawn;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.SendControlMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.SendMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.StartPlayers;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BeginMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.EndMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerJoinsGame;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerLeft;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerMessage;
import cz.cuni.amis.pogamut.ut2004.communication.worldview.UT2004WorldView;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004ObserverFactory;
import cz.cuni.amis.pogamut.ut2004.server.IUT2004Server;
import cz.cuni.amis.pogamut.ut2004.server.impl.UT2004Server;
import cz.cuni.amis.pogamut.ut2004.utils.UnrealUtils;
import cz.cuni.amis.pogamut.ut2004.vip.observer.CSObserver;
import cz.cuni.amis.pogamut.ut2004.vip.observer.CSObserverModule;
import cz.cuni.amis.pogamut.ut2004.vip.observer.CSObserverParams;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.CSBotTeam;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.CSMessages;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.CSRoundResult;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.VIPGameConfig;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.VIPGameState;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSAssignVIP;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSBotStateChanged;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSCounterTerroristsWin;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSMessage;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSRoundEnd;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSRoundStart;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSRoundState;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSSetVIPSafeArea;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSTeamScoreChanged;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSTerroristsWin;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSVIPKilled;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSVIPSafe;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.VIPGameEnd;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.VIPGameStart;
import cz.cuni.amis.utils.ExceptionToString;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.maps.LazyMap;

/**
 * VIP Game server class.
 * 
 * @author Jimmy
 *
 */
public class UT2004VIPServer extends UT2004Server implements IUT2004Server {

	public static final UnrealId SERVER_UNREAL_ID = UnrealId.get("VIPSERVER");

	public static final long ROUND_STATE_BROADCAST_PERIOD_SECS = 5;

	private Random random = new Random(System.currentTimeMillis());

	private Object mutex = new Object();

	private NumberFormat nf;

	private NumberFormat getNumberFormat() {
		if (nf == null) {
			nf = NumberFormat.getNumberInstance();
			nf.setMaximumFractionDigits(2);
		}
		return nf;
	}

	/**
	 * BeginMessage listener - we get current server time here.
	 */
	private IWorldEventListener<BeginMessage> myBeginMessageListener = new IWorldEventListener<BeginMessage>() {
		public void notify(BeginMessage event) {
			timeUpdate(event);
		}
	};

	/**
	 * BeginMessage listener - we get current server time here.
	 */
	private IWorldEventListener<EndMessage> myEndMessageListener = new IWorldEventListener<EndMessage>() {
		public void notify(EndMessage event) {
			batchEnd(event);
		}
	};

	/**
	 * PlayerJoinsGame listener - we get informed that new player/bot has
	 * entered the game.
	 */
	private IWorldEventListener<PlayerJoinsGame> myPlayerJoinsGameMessageListener = new IWorldEventListener<PlayerJoinsGame>() {
		public void notify(PlayerJoinsGame event) {
			playerJoinsGame(event);
		}
	};

	/**
	 * PlayerLeft listener - we get informed that new player/bot has entered the
	 * game.
	 */
	private IWorldEventListener<PlayerLeft> myPlayerLeftMessageListener = new IWorldEventListener<PlayerLeft>() {
		public void notify(PlayerLeft event) {
			playerLeft(event);
		}
	};

	/**
	 * Player listener - we simply print out all player messages we receive.
	 */
	private IWorldObjectListener<PlayerMessage> myPlayerListener = new IWorldObjectListener<PlayerMessage>() {
		public void notify(IWorldObjectEvent<PlayerMessage> event) {
			playerUpdate(event);
		}
	};

	private LevelGeometryModule level;

	private CSMessages messages = new CSMessages();

	@Inject
	public UT2004VIPServer(UT2004AgentParameters params,
			IAgentLogger agentLogger, IComponentBus bus,
			SocketConnection connection, UT2004WorldView worldView, IAct act) {
		
		super(params, agentLogger, bus, connection, worldView, act);
		
		level = new LevelGeometryModule(
			new IUT2004ServerProvider() {
				@Override
				public void killServer() {
				}
	
				@Override
				public UT2004Server getServer() {
					return UT2004VIPServer.this;
				}
			}, getWorldView(), getLogger()
		);
		
		getWorldView().addEventListener(BeginMessage.class, myBeginMessageListener);
		getWorldView().addEventListener(EndMessage.class, myEndMessageListener);
		getWorldView().addEventListener(PlayerJoinsGame.class, myPlayerJoinsGameMessageListener);
		getWorldView().addEventListener(PlayerLeft.class, myPlayerLeftMessageListener);
		getWorldView().addObjectListener(Player.class, WorldObjectUpdatedEvent.class, myPlayerListener);
	}

	// ==========
	// LIFE-CYCLE
	// ==========

	@Override
	protected void init() {
		super.init();
		// getLogger().getCategory(YylexParser.COMPONENT_ID.getToken()).setLevel(Level.ALL);
		// getLogger().getCategory(getWorldView()).setLevel(Level.ALL);
		synchronized (mutex) {
			getAct().act(new StartPlayers(true, true, false));
			getAct().act(new Configuration().setVisionTime(0.2d));
		}
	}

	@Override
	protected void reset() {
		super.reset();
	}

	// =======
	// CONTROL
	// =======

	private Flag<Boolean> gameRunning = new Flag<Boolean>(false);

	private Flag<Boolean> gameFailed = new Flag<Boolean>(false);

	private Flag<VIPGameState> gameState = new Flag<VIPGameState>(VIPGameState.NOT_RUNNING);

	private int subState = 0;		

	private VIPGameConfig config;

	protected void failure(String reason) {
		gameFailed.setFlag(true);
		gameRunning.setFlag(false);
		throw new RuntimeException(reason);
	}

	public void startGame(VIPGameConfig config) {
		NullCheck.check(config, "config");
		if (config.getTargetMap() == null) {
			failure("TargetMap is not specified within the configuration!");
			return;
		}
		if (!config.getTargetMap().equalsIgnoreCase(getMapName())) {
			failure("VIPGameConfig is configured for '" + config.getTargetMap()	+ "', but currently the UT2004 server is running map '"	+ getMapName() + "'.");
			return;
		}
//		if (!level.isInitialized()) {
//			failure("Could not start the game as LevelGeometryModule did not initialized. Have you put correct files into map/ directory for "
//					+ getMapName()
//					+ " ? See javadoc for LevelGeometryModule for more info.");
//			return;
//		}

		synchronized (mutex) {
			if (gameRunning.getFlag()) {
				failure("Cannot start the game, game is already running!");
				return;
			}

			// RESET THE GAME
			this.config = config;
			resetVIPGame();
			
			// START THE GAME
			gameRunning.setFlag(true);
			gameState.setFlag(VIPGameState.GAME_STARTING);			
			speak("VIP Game STARTing!");
		}
	}

	public void endGame() {
		synchronized (mutex) {
			if (!gameRunning.getFlag()) {
				failure("Cannot end game, game is not running!");
				return;
			}
			
			VIPGameEnd endMsg = new VIPGameEnd();
			send(endMsg);

			long finishTime = System.currentTimeMillis();
			for (CSBotRecord<PlayerMessage> record : records.values()) {
				if (!record.isInGame())
					continue;
				record.setFinishTime(finishTime);
			}

			speak("Game ENDed!");

			setState(VIPGameState.NOT_RUNNING);
			gameRunning.setFlag(false);
		}
	}

	public Flag<Boolean> isGameRunning() {
		return gameRunning;
	}

	public boolean isRoundRunning() {
		return roundRunning;
	}

	public Flag<VIPGameState> getGameState() {
		return gameState;
	}

	public Flag<Boolean> getGameFailed() {
		return gameFailed;
	}

	public Map<UnrealId, CSBotRecord<PlayerMessage>> getBotRecords() {
		return records;
	}
	
	public CSTeamsRecord getTeamsRecord() {
		return teamsRecord;
	}

	// =======
	// MEMBERS
	// =======

	private boolean roundRunning = false;

	// SECONDS!
	private double timeCurrent = -1;
	private double timeLast = -1;
	private double timeDelta = -1;

	// SECONDS!
	private double utTimeCurrent = -1;
	private double utTimeLast = -1;
	private double utTimeDelta = -1;

	private long utSendNextRoundStateTimeLeft = ROUND_STATE_BROADCAST_PERIOD_SECS;

	private int roundLeft = -1;

	/**
	 * 0-based
	 */
	private int roundNumber = -1;

	private double roundTimeLeft = -1;

	private CSBotRecord<PlayerMessage> vip;
	
	private Location safeArea;

	private Map<UnrealId, CSBotRecord<PlayerMessage>> records = new LazyMap<UnrealId, CSBotRecord<PlayerMessage>>() {

		@Override
		protected CSBotRecord<PlayerMessage> create(UnrealId key) {
			return new CSBotRecord<PlayerMessage>(key, config);
		}

	};
	
	private CSTeamsRecord teamsRecord;

	Object observersMutex = new Object();
	CSObserver vipObserver;
	Map<UnrealId, CSObserver> observers = new HashMap<UnrealId, CSObserver>();
	Map<UnrealId, CSObserverStarter> observerStarters = new HashMap<UnrealId, CSObserverStarter>();

	private Map<UnrealId, Long> lastPlayerUpdate = new LazyMap<UnrealId, Long>() {
		@Override
		protected Long create(UnrealId key) {
			return System.currentTimeMillis();
		}
	};

	

	// ==============
	// EVENT HANDLERS
	// ==============

	private void playerUpdate(IWorldObjectEvent<PlayerMessage> event) {
		synchronized (mutex) {
			PlayerMessage player = event.getObject();
			CSBotRecord<PlayerMessage> record = ensurePlayer(player.getId());
			record.setPlayer(player);
		}
		synchronized (lastPlayerUpdate) {
			long lastTime = lastPlayerUpdate.get(event.getObject().getId());
			long currTime = System.currentTimeMillis();
			lastPlayerUpdate.put(event.getObject().getId(), currTime);
			long diff = currTime - lastTime;
//			if (diff > 300) {
//				log.warning("Player update too slow for Player[id="
//						+ event.getObject().getId().getStringId() + ", name="
//						+ event.getObject().getName() + "]! Delta " + diff
//						+ "ms.");
//			}
		}
	}

	private void playerJoinsGame(PlayerJoinsGame event) {
		synchronized (mutex) {
			ensurePlayer(event.getId());
		}
	}

	private void playerLeft(PlayerLeft event) {
		synchronized (mutex) {
			if (!records.containsKey(event.getId()))
				return;

			CSBotRecord<PlayerMessage> record = records.get(event.getId());
			record.setInGame(false);
			record.setFinishTime(System.currentTimeMillis());

			if (!gameRunning.getFlag())
				return;

			if (vip == null)
				return;

			if (event.getId() == vip.getPlayer().getId()) {
				terroristsWin(CSRoundResult.VIP_LEFT);
				return;
			}
		}
	}

	private void timeUpdate(BeginMessage event) {
		synchronized (mutex) {
			utTimeLast = utTimeCurrent;
			utTimeCurrent = event.getTime();
			if (utTimeLast > 0) {
				utTimeDelta = utTimeCurrent - utTimeLast;
			} else {
				utTimeDelta = -1;
			}
			timeLast = timeCurrent;
			timeCurrent = (double) (System.currentTimeMillis()) / 1000.0d;
			if (timeLast > 0) {
				timeDelta = timeCurrent - timeLast;
			} else {
				timeDelta = -1;
			}

			if (!gameRunning.getFlag())
				return;

			if (utTimeLast <= 0 || utTimeCurrent <= 0 || utTimeDelta <= 0)
				return;

			utSendNextRoundStateTimeLeft -= utTimeDelta;
			if (utSendNextRoundStateTimeLeft < 0) {
				// ROUND STATE PERIODIC REPORT
				sendRoundStateUpdate();				
				utSendNextRoundStateTimeLeft = ROUND_STATE_BROADCAST_PERIOD_SECS;
			}

		}
	}

	private void sendRoundStateUpdate() {
		CSRoundState msg = new CSRoundState();
		msg.setGameState(gameState.getFlag().stateNumber);
		if (roundNumber >= 0) {
			msg.setRoundLeft(config.getRoundCount() - 1 - roundNumber);
		} else {
			msg.setRoundLeft(config.getRoundCount());
		}
		msg.setRoundTimeLeftUT(roundTimeLeft);
		msg.setVIPBotId(vip == null ? null : vip.getBotId());
		send(msg);
	}

	private void batchEnd(EndMessage event) {
		synchronized (mutex) {
			if (!gameRunning.getFlag())
				return;
			if (utTimeDelta <= 0)
				return;
			tick();
		}
	}

	// ======
	// STATES
	// ======

	private void setState(VIPGameState state) {
		if (state == gameState.getFlag())
			return;
		gameState.setFlag(state);
		subState = 0;

		if (state == VIPGameState.NOT_RUNNING)
			return;

		CSRoundState msg = new CSRoundState();
		msg.setGameState(state.stateNumber);
		msg.setRoundLeft(roundLeft);
		msg.setRoundNumber(roundNumber);
		msg.setRoundTimeLeftUT(roundTimeLeft);
		msg.setVIPBotId(vip == null ? null : vip.getBotId());
		send(msg);
	}

	private void tick() {
		assert (utTimeDelta > 0);
		
		getLogger().setLevel(Level.WARNING);

		if (roundRunning) {
			double origRoundTimeLeft = roundTimeLeft;
			roundTimeLeft -= utTimeDelta;
			
			// SPICING THE GAMEPLAY UP...
			if (origRoundTimeLeft > 120 && roundTimeLeft < 120) {
				speak("REMAINING ROUND TIME: 2 minutes");
			}
			if (origRoundTimeLeft > 60 && roundTimeLeft < 60) {
				speak("REMAINING ROUND TIME: 1 minute");
			}
			if (origRoundTimeLeft > 30 && roundTimeLeft < 30) {
				speak("REMAINING ROUND TIME: 30 seconds");
			}
			if (origRoundTimeLeft > 10 && roundTimeLeft < 10) {
				speak("REMAINING ROUND TIME: 10 seconds");
			}
			if (origRoundTimeLeft > 5 && roundTimeLeft < 5) {
				speak("REMAINING ROUND TIME: 5 seconds");
			}
			if (origRoundTimeLeft > 1 && roundTimeLeft < 1) {
				speak("REMAINING ROUND TIME: 1 second");
			}			
			if (roundTimeLeft < 0) {
				roundTimeLeft = -1;
				roundRunning = false;
				speak("ROUND TIMEOUT!");
				terroristsWin(CSRoundResult.ROUND_TIMEOUT);
			}
		}

		log.info(gameState.getFlag().toString());
		log.info("  +-- UT Time delta = " + utTimeDelta);
		log.info("  +--    Time delta = " + timeDelta);

		switch (gameState.getFlag()) {
		case NOT_RUNNING:
			stateNotRunning(); // SHOULD NOT REACH HERE!
			break;
		case GAME_STARTING:
			stateGameStarting();
			break;
		case GAME_STARTED:
			stateGameStarted();
			break;
		case START_NEXT_ROUND:
			stateStartNextRound();
			break;
		case ROUND_STARTING:
			stateRoundStarting();
			break;
		case ROUND_RUNNING:
			stateRoundRunning();
			break;
		case ROUND_ENDED:
			stateRoundEnded();
			break;
		case ROUND_RESET:
			stateRoundReset();
			break;
		default:
			failure("Unexpected VIPGameState: " + gameState.getFlag());
			return;
		}

	}

	// ==================
	// STATE: NOT_RUNNING
	// ==================

	private void stateNotRunning() {
		// NOTHING TO DO
	}
	
	// ====================
	// STATE: GAME_STARTING
	// ====================

	private void stateGameStarting() {
		// ALL BOTS SHOULD BE WITHIN "records" NOW
		// => FINISH GAME INITIALIZATION
		VIPGameStart startMsg = new VIPGameStart(config);
		send(startMsg);

		roundLeft = config.getRoundCount();

		setState(VIPGameState.GAME_STARTED);

		// BROADCAST BOT STATES
		for (CSBotRecord<PlayerMessage> record : getInGameBots()) {
			record.setConfig(config);
			if (record.isBot()) {
				botStateChanged(record);
			}
		}
	}

	// ===================
	// STATE: GAME_STARTED
	// ===================

	private void stateGameStarted() {
		// GAME HAS BEEN STARTED
		if (config.getRoundCount() <= 0) {
			// NO MORE ROUNDS TO PLAY
			endGame();
			return;
		}

		resetVIPRound();
		roundNumber = -1;
		roundLeft = config.getRoundCount();

		setState(VIPGameState.START_NEXT_ROUND);
	}

	// =======================
	// STATE: START_NEXT_ROUND
	// =======================

	private void stateStartNextRound() {
		if (roundLeft > 0) {
			--roundLeft;
			++roundNumber;
			speak("STARTING ROUND " + (roundNumber + 1) + " / "	+ config.getRoundCount() + " !!!");
			setState(VIPGameState.ROUND_STARTING);
		} else {
			endGame();
		}
	}

	// =====================
	// STATE: ROUND_STARTING
	// =====================

	private void stateRoundStarting() {
		if (getInGameBots().size() < 2) {
			if (subState == 0) {
				speak("Not enough players in the game, waiting...");
				return;
			}
			speakError("Some player has left the game during round-starting, restarting...");
			setState(VIPGameState.ROUND_RESET);
			return;
		}

		switch (subState) {
		case 0: // ASSIGN VIP
			if (getInGameBots().size() < 1) {
				speak("Not enough BOTS in the game, waiting...");
				return;
			}
			if (getInGameTeam(CSBotTeam.COUNTER_TERRORIST).size() < 1) {
				speak("Not enough BOTS in the COUNTER TERRORISTS (~ blue) TEAM, waiting...");
				return;
			}
			if (getInGameTeam(CSBotTeam.TERRORIST).size() < 1) {
				speak("Not enough BOTS in the TERRORISTS (~ red) TEAM, waiting...");
				return;
			}

			send(new CSRoundStart());
			++subState;
			break;
			
		case 1:
			// ASSIGN VIP first...
			if (config.isFixedVIP()) {
				if (!assignFixedVIP())
					return;
			} else {
				if (!assignRandomVIP())
					return;
			}

			// VIP ASSIGNED, treat other bots as non-vip...
			for (CSBotRecord<PlayerMessage> record : getInGameBots()) {
				if (record.getBotId() == vip.getBotId()) continue;
				switch (record.getPlayer().getTeam()) {
				case AgentInfo.TEAM_RED: record.setTerroristForThisRound(); break;
				case AgentInfo.TEAM_BLUE: record.setCounterTerroristForThisRound(); break;
				}					
				botStateChanged(record);
			}

			++subState;
			break;

		case 2: // RESTART LEVEL
			send(new GameConfiguration().setRestart(true));
			++subState;
			break;
			
		case 3: // CONFIG ALL BOTS TO MANUAL SPAWN
			speak("Configuring all bots to MANUAL SPAWN...");
			for (CSBotRecord<PlayerMessage> botRecord : getInGameBots()) {
				configManualSpawn(botRecord);
			}
			++subState;
			break;
			
		case 4: // KILLING ALL BOTS
			speak("Killing all bots...");
			for (CSBotRecord<PlayerMessage> botRecord : getInGameBots()) {
				killBot(botRecord);
			}
			++subState;
			break;
		case 5: // LET UT2004 SERVER TO CATCH UP WITH KILLS ... playing safe here a bit
		case 6:
		case 7:
			++subState;
			break;
			
		case 8: 
			if (ensureAllObserved()) {
				// MOVE ON!
				++subState;
			} else {
				speak("Waiting for bot observers to initialize...");
			}
			break;
		case 9:
			if (ensureVIPObserver()) {
				// MOVE ON!
				++subState;
			} else {
				speak("Waiting for vip-observer to initialize...");
			}
			break;
		
			
		case 10: 
			decideOnSafeArea();
			++subState;
			break;
			
		case 11: // SPAWN ALL RUNNERS
			spawnAll();
			++subState;
			break;
		
		case 12:
		case 13:
			++subState; // LET BOTS TO CATCH-UP
			break;		
		
		case 14: // ALL BOT SPAWNED, MOVE TO NEXT STATE
			speak("ALL BOTS SPAWNED! ROUND " + (roundNumber+1) + " BEGINS");
			
			roundTimeLeft = config.getRoundTimeUT();
			roundRunning = true;
			
			setState(VIPGameState.ROUND_RUNNING);
			
			++subState;
			break;
		}
	}

	private boolean assignFixedVIP() {
		List<CSBotRecord<PlayerMessage>> botRecords = getInGameTeam(CSBotTeam.COUNTER_TERRORIST);
		for (CSBotRecord<PlayerMessage> botRecord : botRecords) {
			if (botRecord.getPlayer().getName().startsWith(config.getFixedVIPNamePrefix())) {
				assignVIP(botRecord);
				return true;
			}
		}
		speak("Could not assign fixed VIP, cannot find player with name '" + config.getFixedVIPNamePrefix() + "' within counter-terrorist (blue) team, waiting ...");
		return false;
	}

	private boolean assignRandomVIP() {
		List<CSBotRecord<PlayerMessage>> botRecords = getInGameTeam(CSBotTeam.COUNTER_TERRORIST);
		if (botRecords.size() == 0) {
			// TODO: restart the round...
			speak("Could not assign random VIP, no players in counter-terrorist (blue) team, waiting ...");
			return false;
		}
		CSBotRecord<PlayerMessage> randomBot = botRecords.get(random.nextInt(botRecords.size()));
		assignVIP(randomBot);
		return true;
	}

	private void assignVIP(CSBotRecord<PlayerMessage> botRecord) {
		vip = botRecord;
		vip.setVIPForThisRound();
		CSAssignVIP msg = new CSAssignVIP();
		msg.setBotId(vip.getBotId());
		send(msg);
		botStateChanged(vip);
		speak("VIP assigned to: " + botRecord.getBotName());
	}

	private boolean ensureVIPObserver() {
		assert(vip != null);
		vipObserver = ensureSingleObserver(vip);
		return vipObserver != null;
	}
	
	private boolean ensureAllObserved() {
		boolean result = true;
		for (CSBotRecord<PlayerMessage> record : getInGameBots()) {
			if (ensureSingleObserver(record) == null) result = false;
		}
		return result;
	}
	
	private CSObserver ensureSingleObserver(CSBotRecord<PlayerMessage> bot) {
		synchronized(observersMutex) {
			// OBSERVER ALREADY PRESENT?
			if (observers.containsKey(bot.getBotId())) {
				CSObserver observer = observers.get(bot.getBotId());
				
				// OBSERVER ALREADY RUNNING?
				if (observer.isObserving() && observer.inState(IAgentStateRunning.class)) {
					// WE'RE DONE!
					return observer;
				}
				
				// OBSERVER INVALID ... remove it...
				killObserver(bot);
				
				// NO VALID OBSERVER FOR 'bot' YET...
				return null;
			}
			
			// OBSERVER STARTING?
			if (observerStarters.containsKey(bot.getBotId())) {
				// OBSERVER STILL STARTING
				return null;
			}
			
			// NO OBSERVER, NO OBSERVER STARTER
			// => start the observer
			startObserver(bot);
			
			return null;
		}
	}
	
	private void decideOnSafeArea() {
		speak("Sending safe area location to counter-terrorist (blue) team...");
		
		safeArea = config.getVipSafeAreas()[random.nextInt(config.getVipSafeAreas().length)];
		
		CSSetVIPSafeArea msg = new CSSetVIPSafeArea();
		msg.setSafeArea(safeArea);
		
		sendTeam(msg, CSBotTeam.COUNTER_TERRORIST);		
	}

	private void spawnAll() {
		speak("Spawning all...");
		spawnCounterTerrorists();
		spawnTerrorists();
	}
	
	private void spawnCounterTerrorists() {
		List<CSBotRecord<PlayerMessage>> bots = getInGameTeam(CSBotTeam.COUNTER_TERRORIST);
		if (bots.size() == 0) {
			failure("There are ZERO players to spawn within Counter-Terrorists team! Restarting...");
			return;
		}
		
		Location spawnLocation = config.getCtsSpawnAreas()[random.nextInt(config.getCtsSpawnAreas().length)];
		spawnBotsAround(bots, spawnLocation);
	}
	
	private void spawnTerrorists() {
		List<CSBotRecord<PlayerMessage>> bots = getInGameTeam(CSBotTeam.TERRORIST);
		if (bots.size() == 0) {
			failure("There are ZERO players to spawn within Terrorists team! Restarting...");
			return;
		}
		
		Location spawnLocation = config.getTsSpawnAreas()[random.nextInt(config.getTsSpawnAreas().length)];
		spawnBotsAround(bots, spawnLocation);
	}
	
	private void spawnBotsAround(List<CSBotRecord<PlayerMessage>> bots, Location spawnLocation) {
		double botsSize = bots.size() * UnrealUtils.CHARACTER_COLLISION_RADIUS * 5;
		double spawnRadius = botsSize / Math.PI / 2; 
		
		double angleDelta = 2 * Math.PI / (double) bots.size();

		double currentAngle = 0;

		for (CSBotRecord<PlayerMessage> botRecord : bots) {
			Location spawnAngle = new Location(1, 0, 0).rotateXY(currentAngle);
			spawnAngle = spawnAngle.scale(spawnRadius);

			Location start = spawnLocation.add(spawnAngle);
			spawn(botRecord, start);

			currentAngle += angleDelta;
		}
	}
	
	// ====================
	// STATE: ROUND_RUNNING
	// ====================

	private void stateRoundRunning() {
		if (subState < 5) {
			// let observers to catch up
			++subState;
			return;
		}
		roundRunning();
	}

	private void roundRunning() {
		if (checkVIPDead()) return;
		if (checkVIPInSafeArea()) return;
		if (checkTerroristsDead()) return;
	}
	
	private boolean checkVIPDead() {
		if (vip == null) {
			// VIP is null, considering it dropped out
			vipDead(CSRoundResult.VIP_LEFT);
			return true;
		}
		if (vipObserver == null) {
			ensureVIPObserver();
			return false;
		}
		if (!vipObserver.isObserving()) {
			killObserver(vip);
			return false;
		}
		if (!vipObserver.isBotAlive()) {
			vipDead(CSRoundResult.VIP_HAS_BEEN_KILLED);
			return true;
		}
		return false;
	}
	
	private void vipDead(CSRoundResult result) {
		CSVIPKilled msg = new CSVIPKilled();
		msg.setVipId(vip.getBotId());		
		send(msg); 
		
		terroristsWin(result);
	}
	
	private boolean checkVIPInSafeArea() {
		if (safeArea.getDistance(vip.getPlayer().getLocation()) > config.getVipSafeAreaRadius()) return false;
		vipSafe(vip, safeArea.getDistance(vip.getPlayer().getLocation()));
		return true;
	}
	
	private void vipSafe(CSBotRecord<PlayerMessage> vip, double safeAreaDistance) {
		speak("VIP " + vip.getBotName() + " reached safe area! Distance from safe area = " + getNumberFormat().format(safeAreaDistance) + " < "	+ config.getVipSafeAreaRadius() + " = safe area radius.");

		CSVIPSafe msg = new CSVIPSafe();
		msg.setVipId(vip.getBotId());
		send(msg);
		vip.vipSafe(vip.getBotId());		
		botStateChanged(vip);
		counterTerroristsWin(CSRoundResult.VIP_ESCAPED);		
	}
	
	private boolean checkTerroristsDead() {
		for (CSBotRecord<PlayerMessage> record : getInGameTeam(CSBotTeam.TERRORIST)) {
			CSObserver observer = ensureSingleObserver(record);
			if (observer == null) return false;
			if (observer.isBotAlive()) return false;
		}
		
		// ALL TERRORISTS DEAD AT THIS POINT...
		counterTerroristsWin(CSRoundResult.TERRORISTS_DEAD);		
		return true;
	}

	private void counterTerroristsWin(CSRoundResult result) {
		if (!roundRunning) return;
		speak("Counter-terrorist (blue) team WINS the round: " + result.message);
		
		// INFORM RECORDS
		teamsRecord.counterTerroristsWin();
		for (CSBotRecord<PlayerMessage> record : getInGameBots()) {
			record.counterTerroristsWin();
		}
		
		// SEND ROUND RESULT
		CSCounterTerroristsWin roundResult = new CSCounterTerroristsWin();
		roundResult.setRoundResult(result);
		send(roundResult);
		
		// SEND TEAM SCORE CHAGNES
		CSTeamScoreChanged teamScoreChanged = new CSTeamScoreChanged();
		
		teamScoreChanged.setUt2004Team(CSBotTeam.COUNTER_TERRORIST.ut2004Team);
		teamScoreChanged.setScore(teamsRecord.getScore(CSBotTeam.COUNTER_TERRORIST));
		teamScoreChanged.setRoundResult(result);
		send(teamScoreChanged);
		
		teamScoreChanged.setUt2004Team(CSBotTeam.TERRORIST.ut2004Team);
		teamScoreChanged.setScore(teamsRecord.getScore(CSBotTeam.TERRORIST));
		teamScoreChanged.setRoundResult(result);
		send(teamScoreChanged);
		
		// SWITCH ROUND STATE
		roundRunning = false;
		setState(VIPGameState.ROUND_ENDED);
	}
	
	private void terroristsWin(CSRoundResult result) {
		if (!roundRunning) return;
		speak("Terrorists (red) team WINS the round: " + result.message);
		
		// INFORM RECORDS
		teamsRecord.terroristsWin();
		for (CSBotRecord<PlayerMessage> record : getInGameBots()) {
			record.terroristsWin();
		}
		
		// SEND ROUND RESULT
		CSTerroristsWin roundResult = new CSTerroristsWin();
		roundResult.setRoundResult(result);
		send(roundResult);
		
		// SEND TEAM SCORE CHAGNES
		CSTeamScoreChanged teamScoreChanged = new CSTeamScoreChanged();
		
		teamScoreChanged.setUt2004Team(CSBotTeam.COUNTER_TERRORIST.ut2004Team);
		teamScoreChanged.setScore(teamsRecord.getScore(CSBotTeam.COUNTER_TERRORIST));
		teamScoreChanged.setRoundResult(result);
		send(teamScoreChanged);
		
		teamScoreChanged.setUt2004Team(CSBotTeam.TERRORIST.ut2004Team);
		teamScoreChanged.setScore(teamsRecord.getScore(CSBotTeam.TERRORIST));
		teamScoreChanged.setRoundResult(result);
		send(teamScoreChanged);
		
		// SWITCH ROUND STATE
		roundRunning = false;
		setState(VIPGameState.ROUND_ENDED);
	}

	// ==================
	// STATE: ROUND_ENDED
	// ==================

	private void stateRoundEnded() {
		roundRunning = false;
		send(new CSRoundEnd());
		setState(VIPGameState.ROUND_RESET);
	}
	
	// ==================
	// STATE: ROUND_RESET
	// ==================

	
	private void stateRoundReset() {
		switch (subState) {
		case 0:
			killAllAliveBots();
			speak("Round ENDed!");
			++subState;
			break;
		case 1: // LET GB2004 to catch up
		case 2:
		case 3:
		case 4:
			++subState;
			break;
		case 5:
			setState(VIPGameState.START_NEXT_ROUND);
			break;
		}
	}

	private void killAllAliveBots() {
		speak("Killing all remaining bots...");
		List<CSBotRecord<PlayerMessage>> aliveBots = getInGameAliveBots();
		for (CSBotRecord<PlayerMessage> aliveBot : aliveBots) {
			killBot(aliveBot);
		}
	}

	// =====
	// UTILS
	// =====

	private void spawn(CSBotRecord<PlayerMessage> botRecord, Location spawningPoint) {
		botRecord.setSpawned(true);
		Respawn respawn = new Respawn();
		respawn.setId(botRecord.getBotId());
		respawn.setStartLocation(spawningPoint);
		send(respawn);
	}

	private void botStateChanged(CSBotRecord<PlayerMessage> botRecord) {
		CSBotStateChanged msg = new CSBotStateChanged();
		msg.setBotId(botRecord.getBotId());
		msg.setNewState(botRecord.getBotState());
		send(msg);
	}

	private void configManualSpawn(CSBotRecord<PlayerMessage> botRecord) {
		Configuration conf = new Configuration();
		conf.setId(botRecord.getBotId());
		conf.setManualSpawn(true);
		send(conf);
		// speak("Bot " + botRecord.getBotName() +
		// " configured to manual spawning.");
	}

	private void spawnBot(CSBotRecord<PlayerMessage> botRecord) {
		
	}
	
	private void killBot(CSBotRecord<PlayerMessage> botRecord) {
		botRecord.setSpawned(false);
		
		KillBot cmd = new KillBot();
		cmd.setId(botRecord.getBotId());
		send(cmd);
		// speak("Bot " + botRecord.getBotName() + " killed.");
	}

	private CSBotRecord<PlayerMessage> ensurePlayer(UnrealId botId) {
		if (botId == null)
			return null;

		CSBotRecord<PlayerMessage> record = records.get(botId);
		if (record.isInGame())
			return record;

		record.reset();
		record.setInGame(true);

		// OBSERVER IS SPAWNED ONLY FOR THE SEEKER DYNAMICALLY
		// if (record.isBot()) {
		// ensureObserver(record);
		// }

		if (!gameRunning.getFlag())
			return record;

		botStateChanged(record);

		return record;
	}

	private List<CSBotRecord<PlayerMessage>> getInGameBots() {
		List<CSBotRecord<PlayerMessage>> result = new ArrayList<CSBotRecord<PlayerMessage>>();
		for (CSBotRecord<PlayerMessage> record : records.values()) {
			if (!record.isInGame())
				continue;
			if (record.getPlayer().getJmx() == null)
				continue;
			result.add(record);
		}
		return result;
	}
	
	private List<CSBotRecord<PlayerMessage>> getInGameAliveBots() {
		List<CSBotRecord<PlayerMessage>> result = new ArrayList<CSBotRecord<PlayerMessage>>();
		for (CSBotRecord<PlayerMessage> record : records.values()) {
			if (!record.isInGame())
				continue;
			if (record.getPlayer().getJmx() == null)
				continue;
			if (!record.isSpawned())
				continue;
			result.add(record);
		}
		return result;
	}
	
	private List<CSBotRecord<PlayerMessage>> getInGameTeam(CSBotTeam team) {
		List<CSBotRecord<PlayerMessage>> result = new ArrayList<CSBotRecord<PlayerMessage>>();
		for (CSBotRecord<PlayerMessage> record : records.values()) {
			if (!record.isInGame())
				continue;
			if (record.getPlayer().getJmx() == null)
				continue;
			if (record.getPlayer().getTeam() != team.ut2004Team)
				continue;
			result.add(record);
		}
		return result;
	}
	
	private List<CSBotRecord<PlayerMessage>> getInGameTeamAlive(CSBotTeam team) {
		List<CSBotRecord<PlayerMessage>> result = new ArrayList<CSBotRecord<PlayerMessage>>();
		for (CSBotRecord<PlayerMessage> record : records.values()) {
			if (!record.isInGame())
				continue;
			if (record.getPlayer().getJmx() == null)
				continue;
			if (record.getPlayer().getTeam() != team.ut2004Team)
				continue;
			if (!record.isSpawned())
				continue;
			result.add(record);
		}
		return result;
	}
	
	private void scoreChanged(CSBotTeam team, CSRoundResult roundResult) {
		CSTeamScoreChanged msg = new CSTeamScoreChanged();
		msg.setUt2004Team(team.ut2004Team);
		msg.setScore(teamsRecord.getScore(team));
		msg.setRoundResult(roundResult);
		send(msg);
	}

	private void send(CSMessage message) {
		if (!gameRunning.getFlag()) return;
		log.info("Sending to ALL: " + message);
		SendControlMessage command = messages.write(message);
		command.setSendAll(true);
		getAct().act(command);
	}
	
	private void sendTeam(CSMessage message, CSBotTeam team) {
		if (!gameRunning.getFlag()) return;
		log.info("Sending to " + team + ": " + message);
		SendControlMessage command = messages.write(message);
		command.setSendAll(false);		
		for (CSBotRecord<PlayerMessage> record : getInGameTeam(team)) {
			command.setBotId(record.getBotId());
			send(command);
		}		
	}
	
	private void sendTeamAlive(CSMessage message, CSBotTeam team) {
		if (!gameRunning.getFlag()) return;
		log.info("Sending to " + team + ": " + message);
		SendControlMessage command = messages.write(message);
		command.setSendAll(false);		
		for (CSBotRecord<PlayerMessage> record : getInGameTeamAlive(team)) {
			command.setBotId(record.getBotId());
			send(command);
		}		
	}

	private void send(CommandMessage command) {
		if (!gameRunning.getFlag()) return;
		getAct().act(command);		
	}

	private void speak(String message) {
		if (!gameRunning.getFlag()) return;
		log.warning(message);
		getAct().act(new SendMessage().setGlobal(true).setText("[GAME] " + message));
	}

	private void speakError(String message) {
		if (!gameRunning.getFlag()) log.severe(message);
		getAct().act(new SendMessage().setGlobal(true).setText("[GAME] [ERROR] " + message));
	}

	private void resetVIPRound() {
		roundRunning = false;
		vip = null;	
	}

	private void resetVIPGame() {
		gameRunning.setFlag(false);
		roundLeft = -1;
		roundNumber = -1;
		vip = null;

		utSendNextRoundStateTimeLeft = ROUND_STATE_BROADCAST_PERIOD_SECS;

		Iterator<CSBotRecord<PlayerMessage>> recordIter = records.values()	.iterator();
		while (recordIter.hasNext()) {
			CSBotRecord<PlayerMessage> record = recordIter.next();
			if (!record.isInGame()) {
				recordIter.remove();
				continue;
			}
			record.reset();
			record.setInGame(true);
		}
		
		if (teamsRecord == null) {
			teamsRecord = new CSTeamsRecord(config);
		}
	}

	// ==========
	// LIFE-CYCLE
	// ==========

	@Override
	protected void stopAgent() {
		super.stopAgent();
		cleanUp();
	}

	@Override
	protected void killAgent() {
		super.killAgent();
		try {
			cleanUp();
		} catch (Exception e) {
		}
	}

	protected void cleanUp() {
		 synchronized(observersMutex) {
			 for (CSObserver observer : observers.values()) {
				 try {
					 new CSObserverKiller(observer);
				 } catch (Exception e) {					 
				 }
			 }
		}
	}

	// ==========================
	// VIP OBSERVER MANIPULATIONS
	// ==========================

	 public void observerFailedToStart(CSBotRecord botToObserve, Exception e)
	 {
		 synchronized (observersMutex) {
			 observerStarters.remove(botToObserve.getBotId());
	 	 }
		 speakError("Failed to start observer for the bot: " + botToObserve.getBotId());
		 log.severe(ExceptionToString.process("Failed to start observer for the bot: " + botToObserve.getBotId(), e));
		 kill();
	 }
	 
	 public CSObserver getObserver(CSBotRecord<PlayerMessage> bot) {
		 return observers.get(bot.getBotId());
	 }
	 
	 public void startObserver(CSBotRecord<PlayerMessage> bot) {
		 synchronized (observersMutex) {
			 CSObserverStarter starter = observerStarters.get(bot.getBotId());
			 if (starter != null) {
				 return;
			 }			 
			 starter = new CSObserverStarter(bot, config.getObserverPort());
			 observerStarters.put(bot.getBotId(), starter);
			 starter.start();
		 }
	 }
	 
	 public void killObserver(CSBotRecord<PlayerMessage> bot) {
		 synchronized (observersMutex) {
			 CSObserver observer = observers.remove(bot.getBotId());			 
			 if (observer != null) {
				 new CSObserverKiller(observer).start();
			 }
			 CSObserverStarter starter = observerStarters.get(bot.getBotId());
			 if (starter != null) {
				 starter.observerValid = false;
			 }
		 }
	 }

	 private class CSObserverStarter extends Thread {

		private CSBotRecord botToObserve;

		private int observerPort;

		/**
		 * If set to FALSE, will kill observer instead adding it into
		 * 'observers' map.
		 */
		public boolean observerValid = true;

		public CSObserverStarter(CSBotRecord botToObserve, int observerPort) {
			this.botToObserve = botToObserve;
			this.observerPort = observerPort;
		}

		public void run() {
			try {
				CSObserverParams params = new CSObserverParams();
				params.setAgentId(new AgentId("Observer-" + botToObserve.getBotId().getStringId()));
				URI worldAddress = UT2004VIPServer.this.getWorldAddress();
				params.setWorldAddress(new SocketConnectionAddress(worldAddress.getHost(), observerPort));

				// PUT HERE NAME OF THE BOT WE WANT TO OBSERVE
				params.setBotIDToObserve(botToObserve.getBotId().getStringId());

				// Creating module that will tell Guice to instantiate our observer
				CSObserverModule module = new CSObserverModule();
				// Creating Observer factory
				UT2004ObserverFactory observerFactory = new UT2004ObserverFactory(module);
				CSObserver observer = (CSObserver) observerFactory.newAgent(params);

				// Start our observer
				try {
					observer.getLogger().setLevel(Level.WARNING);
					observer.start();
				} catch (Exception e) {
					observerFailedToStart(botToObserve, e);
					return;
				}

				synchronized (observersMutex) {					
					if (observerValid && UT2004VIPServer.this.inState(IAgentStateRunning.class)) {
						observers.put(botToObserve.getBotId(), observer);						
					} else {
						new CSObserverKiller(observer);
					}
				}
			} catch (Exception e) {
			} finally {
				synchronized (observersMutex) {
					observerStarters.remove(botToObserve.getBotId());
				}
			}
		};

	}

	private class CSObserverKiller extends Thread {

		private CSObserver observer;

		public CSObserverKiller(CSObserver observer) {
			this.observer = observer;
		}

		public void run() {
			if (observer == null)
				return;
			try {
				observer.kill();
			} catch (Exception e) {				
			}
		};

	}

}