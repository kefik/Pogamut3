package cz.cuni.amis.pogamut.ut2004.hideandseek.bot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cz.cuni.amis.pogamut.base.agent.module.SensorModule;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Players;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.UT2004AgentInfo;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BeginMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerJoinsGame;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerLeft;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Spawn;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.HSBotState;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.HSGameConfig;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.HSGameState;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.HSMessagesTranslator;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSAssignSeeker;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSBotStateChanged;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSGameEnd;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSGameStart;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSPlayerScoreChanged;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSRoundEnd;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSRoundStart;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSRoundState;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSRunnerCaptured;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSRunnerFouled;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSRunnerSafe;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSRunnerSpotted;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSRunnerSurvived;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSSeekerFouled;
import cz.cuni.amis.pogamut.ut2004.hideandseek.server.HSBotRecord;
import cz.cuni.amis.pogamut.ut2004.hideandseek.server.UT2004HSServer;
import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.flag.ImmutableFlag;
import cz.cuni.amis.utils.maps.LazyMap;

/**
 * BotHideAndSeekModule
 * 
 * @author Jimmy
 */
public class HSBotModule extends SensorModule<UT2004Bot>{

	// =======
	// MODULES
	// =======
	
	private AgentInfo info;
	
	private Players players;
	
	// ==========
	// TAG EVENTS
	// ==========
	
	private HSMessagesTranslator hsTranslator;
	
	private HSEvents hsEvents;
	
	private IWorldEventListener<BeginMessage> beginMessageListener = new IWorldEventListener<BeginMessage>() {

		@Override
		public void notify(BeginMessage event) {
			beginMessage(event);
		}
		
	};
	
	private IWorldEventListener<BotKilled> botKilledMessageListener = new IWorldEventListener<BotKilled>() {

		@Override
		public void notify(BotKilled event) {
			botKilled(event);
		}
		
	};
	
	private IWorldEventListener<Spawn> spawnMessageListener = new IWorldEventListener<Spawn>() {

		@Override
		public void notify(Spawn event) {
			spawn(event);
		}
		
	};
	
	/**
     * PlayerJoinsGame listener - we get informed that new player/bot has entered the game.
     */
    private IWorldEventListener<PlayerJoinsGame> myPlayerJoinsGameMessageListener = new IWorldEventListener<PlayerJoinsGame>() {
        public void notify(PlayerJoinsGame event) {
            playerJoinsGame(event);
        }
    };
    
    /**
     * PlayerLeft listener - we get informed that new player/bot has entered the game.
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
	
	// =============
	// TAG GAME DATA
	// =============
	
	private HSGameConfig gameConfig;
	
	private Flag<Boolean> gameRunning = new Flag<Boolean>(false);
	
	private Flag<Boolean> roundRunning = new Flag<Boolean>(false);
	
	private Flag<HSGameState> gameState = new Flag<HSGameState>(HSGameState.NOT_RUNNING);
	
	private Map<UnrealId, HSBotRecord<Player>> records = new LazyMap<UnrealId, HSBotRecord<Player>>() {
		@Override
		protected HSBotRecord<Player> create(UnrealId key) {
			return new HSBotRecord<Player>(key);
		}
	};
	
	private HSBotRecord<Player> seeker;
	
	private HSRoundState roundState;

	private long simTimeCurrent;
	
	private boolean alive = false;
	
	public HSBotModule(UT2004Bot agent, AgentInfo info, Players players) {
		super(agent);
		
		this.info = info;
		if (info == null) {
			info = new UT2004AgentInfo(agent);
		}
		
		this.players = players;
		if (this.players == null) {
			this.players = new Players(agent);
		}
		
		hsTranslator = new HSMessagesTranslator(agent.getWorldView(), false);
		
		hsEvents = new HSEvents(agent.getWorldView()) {
			
			@Override
			public void hsAssignSeeker(HSAssignSeeker event) {
				HSBotModule.this.hsAssignSeeker(event);
			}
			
			@Override
			public void hsBotStateChanged(HSBotStateChanged event) {
				HSBotModule.this.hsBotStateChanged(event);
			}
			
			@Override
			public void hsGameEnd(HSGameEnd event) {
				HSBotModule.this.hsGameEnd(event);
			}
			
			@Override
			public void hsGameStart(HSGameStart event) {
				HSBotModule.this.hsGameStart(event);
			}
			
			@Override
			public void hsPlayerScoreChanged(HSPlayerScoreChanged event) {
				HSBotModule.this.hsPlayerScoreChanged(event);
			}
			
			@Override
			public void hsRoundEnd(HSRoundEnd event) {
				HSBotModule.this.hsRoundEnd(event);
			}
			
			@Override
			public void hsRoundStart(HSRoundStart event) {
				HSBotModule.this.hsRoundStart(event);
			}
			
			@Override
			public void hsRoundState(HSRoundState event) {
				HSBotModule.this.hsRoundState(event);
			}
			
			@Override
			public void hsRunnerCaptured(HSRunnerCaptured event) {
				HSBotModule.this.hsRunnerCaptured(event);
			}
			
			@Override
			public void hsRunnerFouled(HSRunnerFouled event) {
				HSBotModule.this.hsRunnerFouled(event);
			}
			
			@Override
			public void hsRunnerSafe(HSRunnerSafe event) {
				HSBotModule.this.hsRunnerSafe(event);
			}
			
			@Override
			public void hsRunnerSpotted(HSRunnerSpotted event) {
				HSBotModule.this.hsRunnerSpotted(event);
			}
			
			@Override
			public void hsRunnerSurvived(HSRunnerSurvived event) {
				HSBotModule.this.hsRunnerSurvived(event);
			}
			
			@Override
			public void hsSeekerFouled(HSSeekerFouled event) {
				HSBotModule.this.hsSeekerFouled(event);
			}
		};
		
		agent.getWorldView().addEventListener(BeginMessage.class, beginMessageListener);
		agent.getWorldView().addEventListener(BotKilled.class, botKilledMessageListener);
		agent.getWorldView().addEventListener(Spawn.class, spawnMessageListener);
		agent.getWorldView().addEventListener(PlayerJoinsGame.class, myPlayerJoinsGameMessageListener);
		agent.getWorldView().addEventListener(PlayerLeft.class, myPlayerLeftMessageListener);
		agent.getWorldView().addObjectListener(Player.class, WorldObjectUpdatedEvent.class, myPlayerListener);
	}
	
	// ==============
	// USEFUL GETTERS
	// ==============
	
	/**
	 * Your BOT {@link UnrealId}.
	 * @return
	 */
	public UnrealId getId() {
		return info.getId();
	}
	
	/**
	 * Whether HS-GAME has been started. This is not saying anything about "whether ROUND is running" or "current STATE of the game".
	 * 
	 * @see #isRoundRunning()
	 * @see #getGameState()
	 * @return
	 */
	public boolean isGameRunning() {
		return gameRunning.getFlag();
	}
	
	/**
	 * Whether HS-GAME has been started flag. This is not saying anything about "whether ROUND is running" or "current STATE of the game".
	 * 
	 * @see #getGameRunningFlag()
	 * @return
	 */
	public ImmutableFlag<Boolean> getGameRunningFlag() {
		return gameRunning.getImmutable();
	}
	
	/**
	 * Whether GAME-ROUND is running. This does not say anything about "current STATE of the round".
	 * 
	 * @see #getGameState()
	 * @return
	 */
	public boolean isRoundRunning() {
		return gameRunning.getFlag() && roundRunning.getFlag();
	}
	
	/**
	 * Whether GAME-ROUND is running. This does not say anything about "current STATE of the round".
	 * 
	 * @see #getGameStateFlag()
	 * @return
	 */
	public ImmutableFlag<Boolean> getRoundRunningFlag() {
		return roundRunning.getImmutable();
	}
	
	/**
	 * Current detailed state of the game, see {@link HSGameState}.
	 * 
	 * @return
	 */
	public HSGameState getGameState() {
		return gameState.getFlag();
	}
	
	/**
	 * Current detailed state of the game, see {@link HSGameState}.
	 * 
	 * @return
	 */
	public ImmutableFlag<HSGameState> getGameStateFlag() {
		return gameState.getImmutable();
	}
	
	/**
	 * Whether game round is running and I'm spawned, i.e., should be doing something.
	 * <p><p>
	 * If true, you should examine {@link #getGameState()} and {@link #getMyState()} and act accordingly.
	 * 
	 * @return
	 */
	public boolean isMeAlive() {
		return isRoundRunning() && alive;
	}
	
	/**
	 * If {@link #isRoundRunning()} tells whether you are seeker (true) or not (false),<p>
	 * if round is NOT running, returns false.
	 * 
	 * @return
	 */
	public boolean isMeSeeker() {
		return isSeeker(getId());
	}
	
	/**
	 * If {@link #isRoundRunning()} tells whether you are runner (true) or not (false),<p>
	 * if round is NOT running, returns false.
	 * 
	 * @return
	 */
	public boolean isMeRunner() {
		return isRunner(getId());
	}
	
	/**
	 * If round is running, returns {@link HSBotState} of your bot.
	 * <p><p>
	 * If round is NOT running, returns null.
	 * @return
	 */
	public HSBotState getMyState() {
		return getBotState(getId());
	}
	
	/**
	 * Returns overall score of your bot.
	 * @return
	 */
	public int getMyScore() {
		return records.get(getId()).getScore();
	}
	
	/**
	 * If {@link #isRoundRunning()}, returns whether 'botId' is seeker.
	 * <p><p>
	 * If round is NOT running, returns false.
	 * 
	 * @param botId
	 * @return
	 */
	public boolean isSeeker(UnrealId botId) {
		return isRoundRunning() ? records.get(botId).getBotState() == HSBotState.SEEKER || records.get(botId).getBotState() == HSBotState.SEEKER_FOULED : false;
	}
	
	/**
	 * If {@link #isRoundRunning()}, returns whether 'bot' is seeker.
	 * <p><p>
	 * If round is NOT running, returns false.
	 * 
	 * @param botId
	 * @return
	 */
	public boolean isSeeker(Player bot) {
		return isSeeker(bot.getId());
	}
	
	/**
	 * If {@link #isRoundRunning()}, returns whether 'botId' is runner.
	 * <p><p>
	 * If round is NOT running, returns false.
	 * 
	 * @param botId
	 * @return
	 */
	public boolean isRunner(UnrealId botId) {
		return isRoundRunning() ? records.get(botId).getBotState() != HSBotState.SEEKER && records.get(botId).getBotState() != HSBotState.SEEKER_FOULED : false;
	}
	
	/**
	 * If {@link #isRoundRunning()}, returns whether 'bot' is runner.
	 * <p><p>
	 * If round is NOT running, returns false.
	 * 
	 * @param botId
	 * @return
	 */
	public boolean isRunner(Player bot) {
		return isRunner(bot.getId());
	}
	
	/**
	 * Tells you whether there is some RUNNER, who has been SPOTTED and was neither captured nor it escaped yet,
	 * thus it make sense to return to the safe-area to capture is.
	 * @return
	 */
	public boolean canCaptureRunner() {
		return getSpottedRunnerRecords().size() > 0;
	}
	
	/**
	 * Returns list of bot records who are in the {@link HSBotState#RUNNER_SPOTTED}.
	 * <p><p>
	 * Note that {@link HSBotRecord#getPlayer()} might not be present!
	 * 
	 * @return
	 */
	public List<HSBotRecord<Player>> getSpottedRunnerRecords() {
		return getBotRecords(HSBotState.RUNNER_SPOTTED);
	}
	
	/**
	 * Returns list of known bots playing hide-and-seek in state 'botWhoAreInThisState'.
	 * <p><p>
	 * Note that {@link HSBotRecord#getPlayer()} might not be present!
	 * 
	 * @param botWhoAreEitherInThisState
	 * @return
	 */
	public List<HSBotRecord<Player>> getBotRecords(HSBotState botWhoAreInThisState) {
		List<HSBotRecord<Player>> result = new ArrayList<HSBotRecord<Player>>();
		for (HSBotRecord<Player> record : records.values()) {
			if (!record.isBot()) continue;
			if (record.getBotState() == botWhoAreInThisState) {
				result.add(record);
			}
		}
		return result;
	}
	
	/**
	 * If {@link #isRoundRunning()}, returns state of 'botId' player in the game.
	 * <p><p>
	 * If round is NOT running, returns null.
	 * 
	 * @param botId
	 * @return
	 */
	public HSBotState getBotState(UnrealId botId) {
		return isRoundRunning() ? records.get(botId).getBotState() : null;
	}
	
	/**
	 * If {@link #isRoundRunning()}, returns whether seeker is present within the environment.
	 * <p><p>
	 * If round is NOT running, returns false.
	 * 
	 * @return
	 */
	public boolean isSeekerSpawned() {
		return isRoundRunning() ? gameState.getFlag() == HSGameState.RESTRICTED_AREA_ACTIVE || gameState.getFlag() == HSGameState.ROUND_RUNNING : false;
	}
	
	/**
	 * If {@link #isRoundRunning()}, returns whether seeker now have "hiding-time" period during which the seeker is not present within the environment.
	 * <p><p>
	 * If round is NOT running, returns false.
	 * 
	 * @return
	 */
	public boolean isHidingTime() {
		return isRoundRunning() ? gameState.getFlag() == HSGameState.HIDING_TIME : false;
	}
	
	/**
	 * If {@link #isRoundRunning()}, returns time (in seconds) how much time runners have left to hide before the seeker is spawned.
	 * <p><p>
	 * If round is NOT running, returns {@link Double#POSITIVE_INFINITY}.
	 * 
	 * @return
	 */
	public double getRemainingHidingTime() {
		double simTimeDelta = simTimeCurrent > roundState.getSimTime() ? ((double)(simTimeCurrent - roundState.getSimTime())) / (double)1000 : 0;
		return isRoundRunning() ? (isHidingTime() ? roundState.getHideTimeLeftUT() - simTimeDelta : 0) : Double.POSITIVE_INFINITY; 
	}
	
	/**
	 * If {@link #isRoundRunning()}, returns whether restricted-area is activated, e.g., whether it will kill any runner stepping into it.
	 * <p><p>
	 * If round is NOT running, returns false.
	 * 
	 * @return
	 */
	public boolean isRestrictedAreaActivated() {
		return isRoundRunning() ? (gameState.getFlag() == HSGameState.RESTRICTED_AREA_ACTIVE || gameState.getFlag() == HSGameState.SPAWNING_SEEKER) : false;
	}
	
	/**
	 * If {@link #isRoundRunning()}, returns time (in seconds) for how long runners cannot approach safe-area (cannot get into the restricted area).
	 * <p><p>
	 * If round is NOT running, returns {@link Double#POSITIVE_INFINITY}.
	 * 
	 * @return
	 */
	public double getRemainingRestrictedAreaTime() {
		double simTimeDelta = simTimeCurrent > roundState.getSimTime() ? ((double)(simTimeCurrent - roundState.getSimTime())) / (double)1000 : 0;
		return isRoundRunning() ? (isRestrictedAreaActivated() ? roundState.getRestrictedAreaTimeLeftUT() - simTimeDelta : 0) : Double.POSITIVE_INFINITY; 
	}
	
	/**
	 * If {@link #isRoundRunning()}, returns whether safe-area is approachable (== !restricted-area-activated) for runners.
	 * <p><p>
	 * If round is NOT running, returns false.
	 * 
	 * @return
	 */
	public boolean isSafeAreaApproachableByRunner() {
		return isRoundRunning() ? gameState.getFlag() == HSGameState.ROUND_RUNNING : false;
	}
	
	/**
	 * Returns UNREAL-ID of the seeker if already assigned.
	 * @return
	 */
	public UnrealId getSeekerId() {
		return seeker == null ? null : seeker.getBotId();
	}
	
	/**
	 * If {@link Player} object is known for the seeker, returns it.
	 * @return
	 */
	public Player getSeeker() {
		return getSeekerId() == null ? null : players.getPlayer(getSeekerId());
	}
	
	/**
	 * Current game config - DO NOT SET ANYTHING! Only for reading.
	 * @return
	 */
	public HSGameConfig getGameConfig() {
		return gameConfig;
	}
	
	/**
	 * Precise location of the safe-area within the environment.
	 * @return
	 */
	public Location getSafeArea() {
		return gameConfig == null ? null : gameConfig.getSafeArea();
	}
	
	/**
	 * NavPoint nearest to the safe area (typically very close).
	 * @return
	 */
	public NavPoint getSafeAreaNavPoint() {
		return DistanceUtils.getNearest(worldView.getAll(NavPoint.class).values(), getSafeArea());
	}
	
	/**
	 * Returns nearest {@link NavPoint} to the safe-area.
	 * @return
	 */
	public NavPoint getSafeAreaNearestNavPoint() {
		return gameConfig == null ? null : info.getNearestNavPoint(gameConfig.getSafeArea());
	}
	
	/**
	 * Returns distance between 'located' from the 'safe-area' including {@link HSGameConfig#getSafeAreaRadius()}, thus the returned number may be negative.
	 * @param located
	 * @return
	 */
	public double getSafeAreaDistance(ILocated located) {		
		if (located == null) return Double.POSITIVE_INFINITY;
		Location safeArea = getSafeArea();		
		if (safeArea == null) return Double.POSITIVE_INFINITY;
		Location location = located.getLocation();
		if (location == null) return Double.POSITIVE_INFINITY;
		return safeArea.getDistance(location) - gameConfig.getSafeAreaRadius();		
	}
	
	/**
	 * Returns distance betweenn your bot and the safe-area, the number may be negative.
	 * @return
	 */
	public double getMySafeAreaDistance() {
		return getSafeAreaDistance(info.getLocation());
	}
	
	/**
	 * Whether 'located' is within the safe-area given its position ({@link HSGameConfig#getSafeArea()}) and area radius ({@link HSGameConfig#getSafeAreaRadius()}).
	 * @param located
	 * @return
	 */
	public boolean isInSafeArea(ILocated located) {
		return getSafeAreaDistance(located) <= 0;
	}
	
	/**
	 * Whether your bot is within the safe-area.
	 * @return
	 */
	public boolean isMeInSafeArea() {
		return isInRestrictedArea(info.getLocation());
	}
	
	/**
	 * Returns distance between 'located' from the 'restricted-area' including {@link HSGameConfig#getRestrictedAreaRadius()}, thus the returned number may be negative.
	 * @param located
	 * @return
	 */
	public double getRestrictedAreaDistance(ILocated located) {
		if (located == null) return Double.POSITIVE_INFINITY;
		Location safeArea = getSafeArea();		
		if (safeArea == null) return Double.POSITIVE_INFINITY;
		Location location = located.getLocation();
		if (location == null) return Double.POSITIVE_INFINITY;
		return safeArea.getDistance(location) - gameConfig.getRestrictedAreaRadius();		
	}
	
	/**
	 * Returns distance between your bot and the restricted area, the number may be negative.
	 * @return
	 */
	public double getMyRestrictedAreaDistance() {
		return getRestrictedAreaDistance(info.getLocation());
	}
	
	/**
	 * Whether 'located' is within the restricted-area given its position ({@link HSGameConfig#getSafeArea()}) and area radius ({@link HSGameConfig#getRestrictedAreaRadius()}).
	 * @param located
	 * @return
	 */
	public boolean isInRestrictedArea(ILocated located) {
		return isInRestrictedArea(located, 0);
	}
	
	/**
	 * Whether your bot is in the restricted area.
	 * @return
	 */
	public boolean isMeInRestrictedArea() {
		return isInRestrictedArea(info.getLocation());
	}
	
	/**
	 * Whether 'located' is within the restricted-area given its position ({@link HSGameConfig#getSafeArea()}) and area radius ({@link HSGameConfig#getRestrictedAreaRadius()}).
	 * @param located
	 * @param tolerance added to the {@link HSGameConfig#getRestrictedAreaRadius()} when comparing distances from the safe-point
	 * @return
	 */
	public boolean isInRestrictedArea(ILocated located, double tolerance) {
		return getRestrictedAreaDistance(located) <= tolerance;
	}
	
	/**
	 * Whether your bot is in the restricted area enlarged given the tolerance.
	 * @param tolerance added to the {@link HSGameConfig#getRestrictedAreaRadius()} when comparing distances from the safe-point
	 * @return
	 */
	public boolean isMeInRestrictedArea(double tolerance) {
		return isInRestrictedArea(info.getLocation(), tolerance);
	}
	
	/**
	 * If {@link #isRoundRunning()}, returns remaining round-time (in seconds), this time includes possible hiding-time and restricted-area-time.
	 * <p><p>
	 * If round is NOT running, returns {@link Double#POSITIVE_INFINITY}.
	 * 
	 * @return
	 */
	public double getRemainingRoundTime() {
		double simTimeDelta = simTimeCurrent > roundState.getSimTime() ? ((double)(simTimeCurrent - roundState.getSimTime())) / (double)1000 : 0;
		return isRoundRunning() ? (roundState.getRoundTimeLeftUT() - simTimeDelta) : Double.POSITIVE_INFINITY;
	}
	
	// ==============
	// EVENT HANDLERS
	// ==============
	
	protected void beginMessage(BeginMessage event) {
		simTimeCurrent = event.getSimTime();
	}
	
	protected void playerUpdate(IWorldObjectEvent<PlayerMessage> event) {
		ensureRecord(event.getObject().getId()).setPlayer(event.getObject());
	}

	protected void playerLeft(PlayerLeft event) {
		ensureRecord(event.getId()).setInGame(false);		
	}

	protected void playerJoinsGame(PlayerJoinsGame event) {
		ensureRecord(event.getId());
	}
	
	protected void botKilled(BotKilled event) {
		alive = false;
	}
	
	protected void spawn(Spawn event) {
		alive = true;
	}
	
	protected void hsAssignSeeker(HSAssignSeeker event) {
		seeker = ensureRecord(event.getBotId());
		seeker.setSeekerForThisRound();
		
		// PRESET BOT STATE
		if (seeker.getBotId() != getId()) {
			info.getBotName().setInfo("HS", HSBotState.RUNNER.name());
		} else {
			info.getBotName().setInfo("HS", HSBotState.SEEKER.name());
		}
	}
	
	protected void hsBotStateChanged(HSBotStateChanged event) {
		ensureRecord(event.getBotId());
		if (isRoundRunning()) {
			updateBotNameTag();
		}
	}
	
	protected void hsGameEnd(HSGameEnd event) {
		roundRunning.setFlag(false);
		gameState.setFlag(HSGameState.NOT_RUNNING);
		gameRunning.setFlag(false);
		removeBotNameTag();
	}
	
	protected void hsGameStart(HSGameStart event) {
		resetHSGameData();
		gameConfig = new HSGameConfig(event);
		gameRunning.setFlag(true);
		gameState.setFlag(HSGameState.GAME_STARTED);
	}
	
	protected void hsPlayerScoreChanged(HSPlayerScoreChanged event) {
		if (event.getBotId() == null) return;
		
		HSBotRecord<Player> record = ensureRecord(event.getBotId());
		record.setScore(event.getScore());
		
		if (event.getBotId() == getId()) {
			agent.getBotName().setInfo("S", String.valueOf(event.getScore()));
		}
	}

	protected void hsRoundEnd(HSRoundEnd event) {
		roundRunning.setFlag(false);
		gameState.setFlag(HSGameState.ROUND_ENDED);	
	}

	
	protected void hsRoundStart(HSRoundStart event) {		
		gameState.setFlag(HSGameState.ROUND_STARTING);
		updateBotNameTag();
	}
	
	protected void hsRoundState(HSRoundState event) {
		if (event.getGameStateEnum() == HSGameState.HIDING_TIME) {
			// ROUND HAS OFFICIALLY STARTED!
			for (HSBotRecord<Player> record : records.values()) {
				if (!record.isInGame()) {
					continue;
				}
				if (record.getBotId() == seeker.getBotId()) {
					continue;
				}
				record.setRunnerForThisRound();
			}		
			updateBotNameTag();
			roundRunning.setFlag(true);
		}
		roundState = event;
		gameState.setFlag(event.getGameStateEnum());
	}
	
	protected void hsRunnerCaptured(HSRunnerCaptured event) {
		HSBotRecord<Player> record = ensureRecord(event.getBotId());
		record.runnerCapturedBySeeker(0, getSeekerId());
		if (seeker != null) {
			seeker.seekerCapturedRunner(0, event.getBotId());
		}
	}
	
	protected void hsRunnerFouled(HSRunnerFouled event) {
		HSBotRecord<Player> record = ensureRecord(event.getBotId());
		record.runnerFauled(0, getSeekerId());
	}
	
	protected void hsRunnerSafe(HSRunnerSafe event) {
		HSBotRecord<Player> record = ensureRecord(event.getBotId());
		record.runnerSafe(0, getSeekerId());
		if (seeker != null) {
			seeker.seekerLetRunnerEscape(0, event.getBotId());
		}
	}
	
	protected void hsRunnerSpotted(HSRunnerSpotted event) {
		HSBotRecord<Player> record = ensureRecord(event.getBotId());
		record.runnerSpottedBySeeker(0, getSeekerId());
		if (seeker != null) {
			seeker.seekerSpottedRunner(0, event.getBotId());
		}
	}
	
	protected void hsRunnerSurvived(HSRunnerSurvived event) {
		HSBotRecord<Player> record = ensureRecord(event.getBotId());
		record.runnerSurvived(0, getSeekerId());
		if (seeker != null) {
			seeker.seekerLetRunnerSurvive(0, event.getBotId());
		}
	}
	
	protected void hsSeekerFouled(HSSeekerFouled event) {
		HSBotRecord<Player> record = ensureRecord(event.getBotId());
		record.seekerFauled(0, getSeekerId());
	}
	
	// =====
	// UTILS
	// =====
	
	private HSBotRecord<Player> ensureRecord(UnrealId botId) {
		if (botId == null || botId == UT2004HSServer.SERVER_UNREAL_ID) return null;
		HSBotRecord<Player> record = records.get(botId);
		record.setInGame(true);		
		return record;
	}
	
	private void deleteRecord(UnrealId botId) {
		records.remove(botId);
	}
	
	private void resetHSGameData() {
		records.clear();
		removeBotNameTag();
	}
	
	private void removeBotNameTag() {
		info.getBotName().deleteInfo("HS");
	}
	
	private void updateBotNameTag() {
		HSBotState myState = records.get(getId()).getBotState();
		if (myState != null) {
			info.getBotName().setInfo("HS", myState.name());
		}
	}

	// ==========
	// LIFE-CYCLE
	// ==========
	
	@Override
	protected void start(boolean startToPaused) {
		super.start(startToPaused);
		hsTranslator.enable();
		hsEvents.enableHSEvents();
	}
	
	@Override
	protected void cleanUp() {
		super.cleanUp();
		hsTranslator.disable();
		hsEvents.disableTagEvents();
	}

}
