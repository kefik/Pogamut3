package cz.cuni.amis.pogamut.ut2004.hideandseek.server;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.SocketConnection;
import cz.cuni.amis.pogamut.base.communication.messages.CommandMessage;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectListener;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.levelGeometry.LevelGeometryModule;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.drawing.IUT2004ServerProvider;
import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Configuration;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.KillBot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Respawn;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.SendControlMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.SendMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.StartPlayers;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BeginMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.EndMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerJoinsGame;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerLeft;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerMessage;
import cz.cuni.amis.pogamut.ut2004.communication.worldview.UT2004WorldView;
import cz.cuni.amis.pogamut.ut2004.hideandseek.observer.HSObserverGeom;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.HSBotState;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.HSGameConfig;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.HSGameState;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.HSMessages;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.HSScoreChangeReason;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSAssignSeeker;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSBotStateChanged;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSGameEnd;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSGameStart;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSMessage;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSPlayerScoreChanged;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSRoundStart;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSRoundState;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSRunnerCaptured;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSRunnerSafe;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSRunnerSpotted;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSRunnerSurvived;
import cz.cuni.amis.pogamut.ut2004.server.IUT2004Server;
import cz.cuni.amis.pogamut.ut2004.server.impl.UT2004Server;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.maps.LazyMap;

/**
 * BotHideAndSeekServer
 * 
 * @author Jimmy
 *
 */
/**
 * @author Jimmy
 *
 */
public class UT2004HSServer extends UT2004Server implements IUT2004Server {

	public static final UnrealId SERVER_UNREAL_ID = UnrealId.get("HSSERVER");

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

	private HSMessages messages = new HSMessages();

	@Inject
	public UT2004HSServer(UT2004AgentParameters params,
			IAgentLogger agentLogger, IComponentBus bus,
			SocketConnection connection, UT2004WorldView worldView, IAct act) {
		super(params, agentLogger, bus, connection, worldView, act);
		getWorldView().addEventListener(BeginMessage.class,
				myBeginMessageListener);
		getWorldView().addEventListener(EndMessage.class, myEndMessageListener);
		getWorldView().addEventListener(PlayerJoinsGame.class,
				myPlayerJoinsGameMessageListener);
		getWorldView().addEventListener(PlayerLeft.class,
				myPlayerLeftMessageListener);
		getWorldView().addObjectListener(PlayerMessage.class, myPlayerListener);

		level = new LevelGeometryModule(

		new IUT2004ServerProvider() {

			@Override
			public void killServer() {
			}

			@Override
			public UT2004Server getServer() {
				return UT2004HSServer.this;
			}
		}, getWorldView(), getLogger());
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
			getAct().act(new Configuration().setVisionTime(0.1d));
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

	private Flag<HSGameState> gameState = new Flag<HSGameState>(
			HSGameState.NOT_RUNNING);

	private int subState = 0;

	private HSGameConfig config;

	protected void failure(String reason) {
		gameFailed.setFlag(true);
		gameRunning.setFlag(false);
		throw new RuntimeException(reason);
	}

	public void startGame(HSGameConfig config) {
		NullCheck.check(config, "config");
		if (config.getTargetMap() == null) {
			failure("TargetMap is not specified within the configuration!");
			return;
		}
		if (!config.getTargetMap().equalsIgnoreCase(getMapName())) {
			failure("HSGameConfig is configured for '" + config.getTargetMap()
					+ "', but currently the UT2004 server is running map '"
					+ getMapName() + "'.");
			return;
		}
		if (!level.isInitialized()) {
			failure("Could not start the game as LevelGeometryModule did not initialized. Have you put correct files into map/ directory for "
					+ getMapName()
					+ " ? See javadoc for LevelGeometryModule for more info.");
			return;
		}

		synchronized (mutex) {
			if (gameRunning.getFlag()) {
				failure("Cannot start the game, game is already running!");
				return;
			}

			this.config = config;
			resetHSGame();
			gameRunning.setFlag(true);
			speak("Game STARTed!");

			HSGameStart startMsg = new HSGameStart(config);
			send(startMsg);

			roundLeft = config.getRoundCount();

			setState(HSGameState.GAME_STARTED);

			for (HSBotRecord<PlayerMessage> record : getInGameBots()) {
				if (record.isBot()) {
					botStateChanged(record);
				}
			}
		}
	}

	public void endGame() {
		synchronized (mutex) {
			if (!gameRunning.getFlag()) {
				failure("Cannot end game, game is not running!");
				return;
			}

			HSGameEnd endMsg = new HSGameEnd();
			send(endMsg);

			long finishTime = System.currentTimeMillis();
			for (HSBotRecord<PlayerMessage> record : records.values()) {
				if (!record.isInGame())
					continue;
				record.setFinishTime(finishTime);
			}

			speak("Game ENDed!");

			setState(HSGameState.NOT_RUNNING);
			gameRunning.setFlag(false);
		}
	}

	public Flag<Boolean> isGameRunning() {
		return gameRunning;
	}

	public boolean isRoundRunning() {
		return roundRunning;
	}

	public Flag<HSGameState> getGameState() {
		return gameState;
	}

	public Flag<Boolean> getGameFailed() {
		return gameFailed;
	}

	public Map<UnrealId, HSBotRecord<PlayerMessage>> getBotRecords() {
		return records;
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

	private double hideTimeLeft = -1;

	private double restrictedAreaTimeLeft = -1;

	private double seekerAtRestrictedAreaUTTime = 0;

	private HSBotRecord<PlayerMessage> seeker;

	private Map<UnrealId, HSBotRecord<PlayerMessage>> records = new LazyMap<UnrealId, HSBotRecord<PlayerMessage>>() {

		@Override
		protected HSBotRecord<PlayerMessage> create(UnrealId key) {
			return new HSBotRecord<PlayerMessage>(key);
		}

	};

	HSObserverGeom seekerObserver;

	// Object observersMutex = new Object();
	//
	// HSObserver seekerObserver;
	//
	// Map<UnrealId, HSObserver> observers = new HashMap<UnrealId,
	// HSObserver>();
	//
	// Map<UnrealId, HSObserverStarter> observerStarters = new HashMap<UnrealId,
	// HSObserverStarter>();

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
			HSBotRecord<PlayerMessage> record = ensurePlayer(player.getId());
			record.setPlayer(player);
		}
		synchronized (lastPlayerUpdate) {
			long lastTime = lastPlayerUpdate.get(event.getObject().getId());
			long currTime = System.currentTimeMillis();
			lastPlayerUpdate.put(event.getObject().getId(), currTime);
			long diff = currTime - lastTime;
			if (diff > 205) {
				log.warning("Player update too slow for Player[id="
						+ event.getObject().getId().getStringId() + ", name="
						+ event.getObject().getName() + "]! Delta " + diff
						+ "ms.");
			}
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

			HSBotRecord<PlayerMessage> record = records.get(event.getId());
			record.setInGame(false);
			record.setFinishTime(System.currentTimeMillis());

			if (!gameRunning.getFlag())
				return;

			if (seeker == null)
				return;

			if (event.getId() == seeker.getPlayer().getId()) {
				failure("Seeker has left the game!");
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
				HSRoundState msg = new HSRoundState();
				msg.setGameState(gameState.getFlag().stateNumber);
				msg.setHideTimeLeftUT(hideTimeLeft);
				msg.setRestrictedAreaTimeLeftUT(restrictedAreaTimeLeft);
				if (roundNumber >= 0) {
					msg.setRoundLeft(config.getRoundCount() - 1 - roundNumber);
				} else {
					msg.setRoundLeft(config.getRoundCount());
				}
				msg.setRoundTimeLeftUT(roundTimeLeft);
				msg.setSafeArea(config.getSafeArea());
				msg.setSeekerBotId(seeker == null ? null : seeker.getPlayer()
						.getId());
				send(msg);
				utSendNextRoundStateTimeLeft = ROUND_STATE_BROADCAST_PERIOD_SECS;
			}

		}
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

	private void setState(HSGameState state) {
		if (state == gameState.getFlag())
			return;
		gameState.setFlag(state);
		subState = 0;

		if (state == HSGameState.NOT_RUNNING)
			return;

		HSRoundState msg = new HSRoundState();
		msg.setGameState(state.stateNumber);
		msg.setHideTimeLeftUT(hideTimeLeft);
		msg.setRestrictedAreaTimeLeftUT(restrictedAreaTimeLeft);
		msg.setRoundLeft(roundLeft);
		msg.setRoundNumber(roundNumber);
		msg.setRoundTimeLeftUT(roundTimeLeft);
		msg.setSafeArea(config.getSafeArea());
		msg.setSeekerBotId(seeker == null ? null : seeker.getBotId());
		send(msg);

	}

	private void tick() {
		assert (utTimeDelta > 0);
		
		getLogger().setLevel(Level.OFF);

		if (roundRunning) {
			roundTimeLeft -= utTimeDelta;
			if (roundTimeLeft < 0) {
				roundTimeLeft = -1;
				roundRunning = false;
				speak("Round ENDING!");
				setState(HSGameState.ROUND_ENDED);
			}
		}

		log.info(gameState.getFlag().toString());
		log.info("  +-- UT Time delta = " + utTimeDelta);
		log.info("  +--    Time delta = " + timeDelta);

		switch (gameState.getFlag()) {
		case NOT_RUNNING:
			stateNotRunning(); // SHOULD NOT REACH HERE!
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
		case HIDING_TIME:
			stateHidingTime();
			break;
		case SPAWNING_SEEKER:
			stateSpawningSeeker();
			break;
		case RESTRICTED_AREA_ACTIVE:
			stateRestrictedAreaActive();
			break;
		case ROUND_RUNNING:
			stateRoundRunning();
			break;
		case ROUND_ENDED:
			stateRoundEnded();
			break;
		default:
			failure("Unexpected HSGameState: " + gameState.getFlag());
			return;
		}

	}

	// ==================
	// STATE: NOT_RUNNING
	// ==================

	private void stateNotRunning() {
		// NOTHING TO DO
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

		resetHSRound();
		roundNumber = -1;
		roundLeft = config.getRoundCount();

		setState(HSGameState.START_NEXT_ROUND);
	}

	// =======================
	// STATE: START_NEXT_ROUND
	// =======================

	private void stateStartNextRound() {
		if (roundLeft > 0) {
			--roundLeft;
			++roundNumber;
			speak("STARTING ROUND " + (roundNumber + 1) + " / "
					+ config.getRoundCount() + " !!!");

			setState(HSGameState.ROUND_STARTING);
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
			speakError("Some player has left the game during round-starting, unsupported!");
			failure("Some player has left the game during round-starting, unsupported!");
			return;
		}

		switch (subState) {
		case 0: // ASSIGN SEEKER + RUNNERS
			if (getInGameBots().size() < 1) {
				speak("Not enought BOTS in the game, waiting...");
				return;
			}

			send(new HSRoundStart());

			if (config.isFixedSeeker()) {
				if (!assignFixedSeeker())
					return;
			} else {
				if (!assignRandomSeeker())
					return;
			}

			// SEEKER ASSIGNED, treat other bots as runners
			for (HSBotRecord<PlayerMessage> record : getInGameBots()) {
				if (record.getBotId() != seeker.getBotId()) {
					record.setRunnerForThisRound();
					botStateChanged(record);
				}
			}

			subState = 1;
			break;
		case 1: // CONFIG ALL BOTS TO MANUAL SPAWN
			speak("Configuring all bots to MANUAL SPAWN...");
			for (HSBotRecord<PlayerMessage> botRecord : getInGameBots()) {
				configManualSpawn(botRecord);
			}
			subState = 2;
			break;
		case 2: // KILLING ALL BOTS
			speak("Killing all bots...");
			for (HSBotRecord<PlayerMessage> botRecord : getInGameBots()) {
				killBot(botRecord);
			}
			subState = 3;
			break;
		case 3: // LET UT2004 SERVER TO CATCH UP WITH KILLS ... playing safe
				// here a bit
		case 4:
		case 5:
			++subState;
			break;
		case 6:
			speak("Waiting for seeker-observer to initialize...");
			++subState;
			break;
		case 7:
			// NO NEED TO HAVE SEEKER-OBSERVER
			// => we're going to do ray casting ourselves as we have
			// LevelGeometry at hand
			// WAIT FOR SEEKER OBSERVER TO INITIALIZE
			if (ensureSeekerObserver()) {
				subState = 8;
				speak("Seeker-observer to initialized.");
			} else {
			}
			break;
		case 8: // SPAWN ALL RUNNERS
			spawnRunners();
			subState = 9;
			break;
		case 9:
		case 10:
			++subState; // LET RUNNERS TO CATCH-UP
			break;
		case 11: // ALL BOT SPAWNED, MOVE TO NEXT STATE
			speak("ALL RUNNERS SHOULD START HIDING!");
			roundTimeLeft = config.getRoundTimeUT();
			hideTimeLeft = config.getHideTimeUT();
			restrictedAreaTimeLeft = -1;
			seekerAtRestrictedAreaUTTime = 0;
			roundRunning = true;
			setState(HSGameState.HIDING_TIME);
			break;
		}
	}

	private boolean assignFixedSeeker() {
		List<HSBotRecord<PlayerMessage>> botRecords = getInGameBots();
		for (HSBotRecord<PlayerMessage> botRecord : botRecords) {
			if (botRecord.getPlayer().getName()
					.startsWith(config.getFixedSeekerName())) {
				assignSeeker(botRecord);
				return true;
			}
		}
		speak("Could not assign fixed seeker, cannot find player with name '"
				+ config.getFixedSeekerName() + "', waiting ...");
		return false;
	}

	private boolean assignRandomSeeker() {
		List<HSBotRecord<PlayerMessage>> botRecords = getInGameBots();
		HSBotRecord<PlayerMessage> randomBot = botRecords.get(random.nextInt(botRecords.size()));
		assignSeeker(randomBot);
		return true;
	}

	private void assignSeeker(HSBotRecord<PlayerMessage> botRecord) {
		seeker = botRecord;
		seeker.setSeekerForThisRound();
		HSAssignSeeker msg = new HSAssignSeeker();
		msg.setBotId(seeker.getBotId());
		send(msg);
		botStateChanged(seeker);
		speak("Seeker assigned to: " + botRecord.getPlayer().getName());
	}

	private boolean ensureSeekerObserver() {
		// synchronized(observersMutex) {
		// if (observers.containsKey(seeker.getBotId())) {
		// seekerObserver = observers.get(seeker.getBotId());
		// return true;
		// } else {
		// ensureSingleObserver(seeker);
		// return false;
		// }
		// }
		seekerObserver = new HSObserverGeom(level, config);
		seekerObserver.setPlayers(getInGameBots());
		seekerObserver.setSeeker(seeker);
		
		return true;
	}

	private void spawnRunners() {
		speak("Spawning all RUNNERs...");

		List<HSBotRecord<PlayerMessage>> runners = getInGameRunners();

		double angleDelta = 2 * Math.PI / (double) runners.size();

		double currentAngle = 0;

		if (runners.size() == 0) {
			failure("There are ZERO runners in the game! Invalid state.");
			return;
		}

		for (HSBotRecord<PlayerMessage> botRecord : runners) {
			Location spawnAngle = new Location(1, 0, 0).rotateXY(currentAngle);
			spawnAngle = spawnAngle.scale(config.getSpawnRadiusForRunners());

			Location start = config.getSafeArea().add(spawnAngle);
			respawn(botRecord, start);

			currentAngle += angleDelta;
		}
	}

	// ==================
	// STATE: HIDING_TIME
	// ==================

	private void stateHidingTime() {
		hideTimeLeft -= utTimeDelta;
		if (hideTimeLeft < 0) {
			// HIDING TIME IS UP
			// => switch to SPAWNING_SEEKER state
			restrictedAreaTimeLeft = config.getRestrictedAreaTimeSecs();
			setState(HSGameState.SPAWNING_SEEKER);
		}
	}

	// ======================
	// STATE: SPAWNING_SEEKER
	// ======================

	private void stateSpawningSeeker() {
		switch (subState) {
		case 0:
			speak("Spawning SEEKER!");
			respawn(seeker, config.getSafeArea());
			subState = 1;
			break;
		case 1:
			speak("Restricted area activated! Runners are not allowed to move near the safe area, radius: "
					+ config.getRestrictedAreaRadius());
			hideTimeLeft = -1;
			restrictedAreaTimeLeft = config.getRestrictedAreaTimeSecs();
			setState(HSGameState.RESTRICTED_AREA_ACTIVE);
			break;
		}
	}

	// =============================
	// STATE: RESTRICTED_AREA_ACTIVE
	// =============================

	private void stateRestrictedAreaActive() {
		killRunnersInRestrictedArea();

		restrictedAreaTimeLeft -= utTimeDelta;
		if (restrictedAreaTimeLeft < 0) {
			speak("Restricted are DEactivated! Runners now may reach safe area, radius: "
					+ config.getSafeAreaRadius());
			restrictedAreaTimeLeft = -1;
			setState(HSGameState.ROUND_RUNNING);
		}

		roundRunning();
	}

	private void killRunnersInRestrictedArea() {
		List<HSBotRecord<PlayerMessage>> runners = getInGameAliveRunners();
		for (HSBotRecord<PlayerMessage> runner : runners) {
			double distance = runner.getPlayer().getLocation()
					.getDistance(config.getSafeArea());
			if (distance < config.getRestrictedAreaRadius()) {
				foulRunner(runner, distance);
			}
		}
	}

	private void foulRunner(HSBotRecord<PlayerMessage> runner,
			double distanceFromSafeArea) {
		speak("Fouling runner " + runner.getBotName()
				+ ", its distance from safe area = "
				+ getNumberFormat().format(distanceFromSafeArea) + " < "
				+ config.getRestrictedAreaRadius()
				+ " = restricted area radius.");
		runner.runnerFauled(config.getRunnerFouled(), seeker.getBotId());
		botStateChanged(runner);
		scoreChanged(
				runner,
				HSScoreChangeReason.RUNNER_FAULED_DUE_TO_BEING_IN_RESTRICTED_AREA);
		killBot(runner);
	}

	// ====================
	// STATE: ROUND_RUNNING
	// ====================

	private void stateRoundRunning() {
		roundRunning();
	}

	private void roundRunning() {
		checkSeekerObserverRunning(); // is seeker observer running so we can
										// tell whether it spots runners?
		checkSpotRunners(); // is any runner newly spotted?
		checkRunnersSafe(); // did any runner reached the safe area?
		checkRunnersCapture(); // was any runner captured?
		checkSeekerInRestrictedAreaTooLong(); // if seeker dwells too long
												// within restricted area, it is
												// penalized
		checkAnyRunnersAndSeekerAlive(); // are any runners/seekers left to play
											// with?
	}

	private void checkSeekerInRestrictedAreaTooLong() {
		// synchronized(observersMutex) {
		if (seeker.getBotState() == HSBotState.SEEKER_FOULED)
			return;
		double distance = seeker.getPlayer().getLocation()
				.getDistance(config.getSafeArea());
		if (distance < config.getRestrictedAreaRadius()) {
			seekerAtRestrictedAreaUTTime += utTimeDelta;
		} else {
			seekerAtRestrictedAreaUTTime -= utTimeDelta;
			if (seekerAtRestrictedAreaUTTime < 0)
				seekerAtRestrictedAreaUTTime = 0;
		}
		if (seekerAtRestrictedAreaUTTime >= config
				.getRestrictedAreaSeekerMaxTimeSecs()) {
			foulSeeker(seeker, distance);
		}
		// }
	}

	private void foulSeeker(HSBotRecord<PlayerMessage> seeker,
			double distanceFromSafeArea) {
		speak("Fouling seeker " + seeker.getBotName()
				+ ", its distance from safe area = "
				+ getNumberFormat().format(distanceFromSafeArea) + " < "
				+ config.getRestrictedAreaRadius()
				+ " = restricted area radius.");
		seeker.seekerFauled(config.getSeekerFouled(), seeker.getBotId());
		botStateChanged(seeker);
		scoreChanged(
				seeker,
				HSScoreChangeReason.SEEKER_FAULED_DUE_TO_BEING_IN_RESTRICTED_AREA_TOO_LONG);
		killBot(seeker);
	}

	private void checkSeekerObserverRunning() {
		// synchronized(observersMutex) {
		// if (!seekerObserver.inState(IAgentStateRunning.class)) {
		// log.severe("Seeker-Observer for bot " +
		// seeker.getBotId().getStringId() + " has died out.");
		// speakError("Seeker-Observer for bot " +
		// seeker.getBotId().getStringId() + " has died out.");
		// kill();
		// }
		// }
	}

	private void checkSpotRunners() {
		// synchronized(observersMutex) {
		// List<Player> spottedBySeeker =
		// seekerObserver.getPlayersVisibleMoreThanMillis(config.getSpotTimeMillis());
		// for (Player spottedPlayer : spottedBySeeker) {
		// HSBotRecord<PlayerMessage> spotted =
		// records.get(spottedPlayer.getId());
		// if (spotted.getBotState() == HSBotState.RUNNER) {
		// runnerSpotted(spotted);
		// }
		// }
		// }

		seekerObserver.setPlayers(getInGameAliveRunners());
		seekerObserver.tick(timeDelta);

		Set<HSBotRecord<PlayerMessage>> spottedBySeeker = seekerObserver
				.getSpotted();
		for (HSBotRecord<PlayerMessage> spotted : spottedBySeeker) {
			PlayerMessage spottedPlayer = spotted.getPlayer();
			if (spotted.getBotState() == HSBotState.RUNNER) {
				runnerSpotted(spotted);
			}
		}
	}

	private void runnerSpotted(HSBotRecord<PlayerMessage> runner) {
		HSRunnerSpotted msg = new HSRunnerSpotted();
		msg.setBotId(runner.getBotId());
		send(msg);
		runner.runnerSpottedBySeeker(config.getRunnerSpotted(),
				seeker.getBotId());
		botStateChanged(runner);
		scoreChanged(runner, HSScoreChangeReason.RUNNER_SPOTTED_BY_SEEKER);
		seeker.seekerSpottedRunner(config.getSeekerSpottedRunner(),
				runner.getBotId());
		scoreChanged(runner, HSScoreChangeReason.SEEKER_SPOTTED_RUNNER);
	}

	private void checkRunnersSafe() {
		List<HSBotRecord<PlayerMessage>> aliveRunners = getInGameAliveRunners();
		for (HSBotRecord<PlayerMessage> aliveRunner : aliveRunners) {
			double safeAreaDistance = aliveRunner.getPlayer().getLocation()
					.getDistance(config.getSafeArea());
			if (safeAreaDistance < config.getSafeAreaRadius()) {
				runnerSafe(aliveRunner, safeAreaDistance);
			}
		}
	}

	private void runnerSafe(HSBotRecord<PlayerMessage> runner,
			double safeAreaDistance) {
		speak("Runner " + runner.getBotName()
				+ " reached safe area! Distance from safe area = "
				+ getNumberFormat().format(safeAreaDistance) + " < "
				+ config.getSafeAreaRadius() + " = safe area radius.");
		HSRunnerSafe msg = new HSRunnerSafe();
		msg.setBotId(runner.getBotId());
		send(msg);
		runner.runnerSafe(config.getRunnerSafe(), seeker.getBotId());
		botStateChanged(runner);
		scoreChanged(runner, HSScoreChangeReason.RUNNER_REACHED_SAFE_AREA);
		seeker.seekerLetRunnerEscape(config.getSeekerLetRunnerEscape(),
				runner.getBotId());
		scoreChanged(seeker, HSScoreChangeReason.SEEKER_LET_RUNNER_ESCAPE);
		killBot(runner);
	}

	private void checkRunnersCapture() {
		double seekerSafeAreaDistance = seeker.getPlayer().getLocation()
				.getDistance(config.getSafeArea());
		if (seekerSafeAreaDistance < config.getSafeAreaRadius()) {
			// MAY CAPTURE RUNNERS
			List<HSBotRecord<PlayerMessage>> spottedRunners = getInGameSpottedRunners();
			for (HSBotRecord<PlayerMessage> spottedRunner : spottedRunners) {
				captureRunner(spottedRunner);
			}
		}
	}

	private void captureRunner(HSBotRecord<PlayerMessage> spottedRunner) {
		seekerAtRestrictedAreaUTTime = 0;
		
		HSRunnerCaptured msg = new HSRunnerCaptured();
		msg.setBotId(spottedRunner.getBotId());
		send(msg);
				
		spottedRunner.runnerCapturedBySeeker(config.getRunnerCaptured(),
				seeker.getBotId());
		botStateChanged(spottedRunner);
		scoreChanged(spottedRunner,
				HSScoreChangeReason.RUNNER_CAPTURED_BY_SEEKER);

		seeker.seekerCapturedRunner(config.getSeekerCapturedRunner(),
				spottedRunner.getBotId());
		scoreChanged(seeker, HSScoreChangeReason.SEEKER_HAS_CAPTURED_RUNNER);

		killBot(spottedRunner);
	}

	private void checkAnyRunnersAndSeekerAlive() {
		List<HSBotRecord<PlayerMessage>> aliveRunners = getInGameAliveRunners();
		if (aliveRunners.size() == 0) {
			speak("No runners remaining... ending the round.");
			setState(HSGameState.ROUND_ENDED);
		}
		if (!seeker.isSpawned()
				|| seeker.getBotState() == HSBotState.SEEKER_FOULED) {
			speak("Seeker was fouled out ... ending the round.");
			setState(HSGameState.ROUND_ENDED);
		}
	}

	// ==================
	// STATE: ROUND_ENDED
	// ==================

	private void stateRoundEnded() {
		switch (subState) {
		case 0:
			surviveAllAliveRunners();
			killAllAliveBots();
			speak("Round ENDed!");
			subState = 1;
			break;
		case 1: // LET GB2004 to catch up
		case 2:
		case 3:
		case 4:
			++subState;
			break;
		case 5:
			setState(HSGameState.START_NEXT_ROUND);
			break;
		}
	}

	private void surviveAllAliveRunners() {
		List<HSBotRecord<PlayerMessage>> aliveRunners = getInGameAliveRunners();
		for (HSBotRecord<PlayerMessage> aliveRunner : aliveRunners) {
			surviveRunner(aliveRunner);
		}
	}

	private void surviveRunner(HSBotRecord<PlayerMessage> survivedRunner) {
		survivedRunner.runnerSurvived(config.getRunnerSurvived(),
				seeker.getBotId());
		HSRunnerSurvived msg = new HSRunnerSurvived();
		msg.setBotId(survivedRunner.getBotId());
		send(msg);
		botStateChanged(survivedRunner);
		scoreChanged(survivedRunner,
				HSScoreChangeReason.RUNNER_SURVIVED_THE_ROUND);
	}

	private void killAllAliveBots() {
		speak("Killing all remaining bots...");
		List<HSBotRecord<PlayerMessage>> aliveBots = getInGameAliveBots();
		for (HSBotRecord<PlayerMessage> aliveBot : aliveBots) {
			killBot(aliveBot);
		}
	}

	// =====
	// UTILS
	// =====

	private void respawn(HSBotRecord<PlayerMessage> botRecord,
			Location spawningPoint) {
		botRecord.setSpawned(true);
		Respawn respawn = new Respawn();
		respawn.setId(botRecord.getBotId());
		respawn.setStartLocation(spawningPoint);
		send(respawn);
	}

	private void botStateChanged(HSBotRecord<PlayerMessage> botRecord) {
		HSBotStateChanged msg = new HSBotStateChanged();
		msg.setBotId(botRecord.getBotId());
		msg.setNewState(botRecord.getBotState());
		send(msg);
	}

	private void configManualSpawn(HSBotRecord<PlayerMessage> botRecord) {
		Configuration conf = new Configuration();
		conf.setId(botRecord.getBotId());
		conf.setManualSpawn(true);
		send(conf);
		// speak("Bot " + botRecord.getBotName() +
		// " configured to manual spawning.");
	}

	private void killBot(HSBotRecord<PlayerMessage> botRecord) {
		botRecord.setSpawned(false);
		KillBot cmd = new KillBot();
		cmd.setId(botRecord.getBotId());
		send(cmd);
		// speak("Bot " + botRecord.getBotName() + " killed.");
	}

	private HSBotRecord<PlayerMessage> ensurePlayer(UnrealId botId) {
		if (botId == null)
			return null;

		HSBotRecord<PlayerMessage> record = records.get(botId);
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

	// private void ensureSingleObserver(HSBotRecord<PlayerMessage> record) {
	// synchronized(observersMutex) {
	// // ENSURE record.getBotId() OBSERVER
	// if (!observers.containsKey(record.getBotId())) {
	// if (!observerStarters.containsKey(record.getBotId())) {
	// ensureObserver(record);
	// }
	// }
	// // KILL ALL IRRELEVANT OBSERVERS (not needed right now)
	// List<UnrealId> toKill = new ArrayList<UnrealId>();
	// for (Entry<UnrealId, HSObserver> observer : observers.entrySet()) {
	// if (observer.getKey() != record.getBotId()) {
	// toKill.add(observer.getKey());
	// }
	// }
	// for (UnrealId idToKill : toKill) {
	// new HSObserverKiller(observers.remove(idToKill)).start();
	// }
	// // IF ANY INVALID OBSERVERS ARE STARTING, MARK THEM AS INVALID ~ do not
	// let them fully start
	// for (Entry<UnrealId, HSObserverStarter> observerStarter :
	// observerStarters.entrySet()) {
	// if (observerStarter.getKey() != record.getBotId()) {
	// observerStarter.getValue().observerValid = false;
	// }
	// }
	// }
	// }

	// private void ensureObserver(HSBotRecord<PlayerMessage> record) {
	// if (observers.containsKey(record.getBotId())) return;
	// if (observerStarters.containsKey(record.getBotId())) return;
	// HSObserverStarter starter = null;
	// synchronized(observersMutex) {
	// if (observers.containsKey(record.getBotId())) return;
	// if (observerStarters.containsKey(record.getBotId())) return;
	// starter = new HSObserverStarter(record, config.getObserverPort());
	// observerStarters.put(record.getBotId(), starter);
	// }
	// starter.start();
	// }

	private List<HSBotRecord<PlayerMessage>> getInGameBots() {
		List<HSBotRecord<PlayerMessage>> result = new ArrayList<HSBotRecord<PlayerMessage>>();
		for (HSBotRecord<PlayerMessage> record : records.values()) {
			if (!record.isInGame())
				continue;
			if (record.getPlayer().getJmx() == null)
				continue;
			result.add(record);
		}
		return result;
	}

	private List<HSBotRecord<PlayerMessage>> getInGameRunners() {
		List<HSBotRecord<PlayerMessage>> result = new ArrayList<HSBotRecord<PlayerMessage>>();
		for (HSBotRecord<PlayerMessage> record : records.values()) {
			if (!record.isInGame())
				continue;
			if (record.getPlayer().getJmx() == null)
				continue;
			if (record.getBotState() == HSBotState.SEEKER
					|| record.getBotState() == HSBotState.SEEKER_FOULED)
				continue;
			result.add(record);
		}
		return result;
	}

	private List<HSBotRecord<PlayerMessage>> getInGameAliveRunners() {
		List<HSBotRecord<PlayerMessage>> result = new ArrayList<HSBotRecord<PlayerMessage>>();
		for (HSBotRecord<PlayerMessage> record : records.values()) {
			if (!record.isInGame())
				continue;
			if (record.getPlayer().getJmx() == null)
				continue;
			if (!record.isSpawned())
				continue;
			if (record.getBotState() == HSBotState.RUNNER
					|| record.getBotState() == HSBotState.RUNNER_SPOTTED) {
				result.add(record);
			}
		}
		return result;
	}

	private List<HSBotRecord<PlayerMessage>> getInGameAliveBots() {
		List<HSBotRecord<PlayerMessage>> result = new ArrayList<HSBotRecord<PlayerMessage>>();
		for (HSBotRecord<PlayerMessage> record : records.values()) {
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

	private List<HSBotRecord<PlayerMessage>> getInGameSpottedRunners() {
		List<HSBotRecord<PlayerMessage>> result = new ArrayList<HSBotRecord<PlayerMessage>>();
		for (HSBotRecord<PlayerMessage> record : records.values()) {
			if (!record.isInGame())
				continue;
			if (record.getPlayer().getJmx() == null)
				continue;
			if (!record.isSpawned())
				continue;
			if (record.getBotState() != HSBotState.RUNNER_SPOTTED)
				continue;
			result.add(record);
		}
		return result;
	}

	private void scoreChanged(HSBotRecord<PlayerMessage> botRecord,
			HSScoreChangeReason reason) {
		HSPlayerScoreChanged msg = new HSPlayerScoreChanged();
		msg.setBotId(botRecord.getBotId());
		msg.setScore(botRecord.getScore());
		msg.setScoreChangeReason(reason.number);
		send(msg);
	}

	private void send(HSMessage message) {
		if (gameRunning.getFlag()) {
			log.info("Sending to ALL: " + message);
			SendControlMessage command = messages.write(message);
			command.setSendAll(true);
			getAct().act(command);
		}
	}

	private void send(CommandMessage command) {
		if (gameRunning.getFlag()) {
			getAct().act(command);
		}
	}

	private void speak(String message) {
		if (gameRunning.getFlag()) {
			log.info(message);
			getAct().act(
					new SendMessage().setGlobal(true)
							.setText("[HS] " + message));
		}
	}

	private void speakError(String message) {
		if (gameRunning.getFlag()) {
			log.severe(message);
			getAct().act(
					new SendMessage().setGlobal(true).setText(
							"[HS] [ERROR] " + message));
		}
	}

	private void resetHSRound() {
		roundRunning = false;
		roundTimeLeft = config.getRoundTimeUT();
		hideTimeLeft = -1;
		restrictedAreaTimeLeft = -1;
	}

	private void resetHSGame() {
		gameRunning.setFlag(false);
		roundLeft = -1;
		roundNumber = -1;
		seeker = null;

		utSendNextRoundStateTimeLeft = ROUND_STATE_BROADCAST_PERIOD_SECS;

		Iterator<HSBotRecord<PlayerMessage>> recordIter = records.values()
				.iterator();
		while (recordIter.hasNext()) {
			HSBotRecord<PlayerMessage> record = recordIter.next();
			if (!record.isInGame()) {
				recordIter.remove();
				continue;
			}
			record.reset();
			record.setInGame(true);
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
		// synchronized(observersMutex) {
		// for (HSObserver observer : observers.values()) {
		// try {
		// new HSObserverKiller(observer);
		// } catch (Exception e) {
		// }
		// }
		// observers.clear();
		// }
	}

	// =========================
	// HS OBSERVER MANIPULATIONS
	// =========================

	// public void observerFailedToStart(HSBotRecord botToObserve, Exception e)
	// {
	// synchronized(observerStarters) {
	// observerStarters.remove(botToObserve.getBotId());
	// }
	// log.severe(ExceptionToString.process("Failed to start observer for bot: "
	// + botToObserve.getBotId(), e));
	// kill();
	// }

	// private class HSObserverStarter extends Thread {
	//
	// private HSBotRecord botToObserve;
	//
	// private int observerPort;
	//
	// /**
	// * If set to FALSE, will kill observer instead adding it into 'observers'
	// map.
	// */
	// public boolean observerValid = true;
	//
	// public HSObserverStarter(HSBotRecord botToObserve, int observerPort) {
	// this.botToObserve = botToObserve;
	// this.observerPort = observerPort;
	// }
	//
	// public void run() {
	// try {
	// HSObserverParams params = new HSObserverParams();
	// params.setAgentId(new AgentId("Observer-" +
	// botToObserve.getBotId().getStringId()));
	// URI worldAddress = UT2004HSServer.this.getWorldAddress();
	// params.setWorldAddress(new
	// SocketConnectionAddress(worldAddress.getHost(), observerPort));
	//
	// // PUT HERE NAME OF THE BOT WE WANT TO OBSERVE
	// params.setBotIDToObserve(botToObserve.getBotId().getStringId());
	//
	// // Creating module that will tell Guice to instantiate our Observer
	// HSObserverModule module = new HSObserverModule();
	// // Creating Observer factory
	// UT2004ObserverFactory observerFactory = new
	// UT2004ObserverFactory(module);
	// HSObserver observer = (HSObserver) observerFactory.newAgent(params);
	//
	// // Start our observer
	// try {
	// observer.start();
	// } catch (Exception e) {
	// observerFailedToStart(botToObserve, e);
	// return;
	// }
	//
	// synchronized(observersMutex) {
	// if (observerValid &&
	// UT2004HSServer.this.inState(IAgentStateRunning.class)) {
	// observers.put(botToObserve.getBotId(), observer);
	// observerStarters.remove(botToObserve.getBotId());
	// } else {
	// new HSObserverKiller(observer).start();
	// }
	// }
	// } catch (Exception e) {
	// synchronized(observersMutex) {
	// observerStarters.remove(botToObserve.getBotId());
	// }
	// }
	// };
	//
	// }
	//
	// private class HSObserverKiller extends Thread {
	//
	// private HSObserver observer;
	//
	// public HSObserverKiller(HSObserver observer) {
	// this.observer = observer;
	// }
	//
	// public void run() {
	// if (observer == null) return;
	// observer.kill();
	// };
	//
	// }

}