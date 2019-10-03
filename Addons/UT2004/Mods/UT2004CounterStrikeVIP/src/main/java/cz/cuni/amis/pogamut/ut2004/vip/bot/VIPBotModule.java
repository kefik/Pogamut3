package cz.cuni.amis.pogamut.ut2004.vip.bot;

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
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerLeft;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Spawn;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.CSBotRole;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.CSBotState;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.CSBotTeam;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.CSMessagesTranslator;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.VIPGameConfig;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.VIPGameResult;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.VIPGameState;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSAssignVIP;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSBotStateChanged;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSCounterTerroristsWin;
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
import cz.cuni.amis.pogamut.ut2004.vip.server.CSBotRecord;
import cz.cuni.amis.pogamut.ut2004.vip.server.UT2004VIPServer;
import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.flag.ImmutableFlag;
import cz.cuni.amis.utils.maps.LazyMap;

/**
 * VIPBotModule
 * 
 * @author Jimmy
 */
public class VIPBotModule extends SensorModule<UT2004Bot>{

	// =======
	// MODULES
	// =======
	
	private AgentInfo info;
	
	private Players players;
	
	// ==========
	// TAG EVENTS
	// ==========
	
	private CSMessagesTranslator csTranslator;
	
	private VIPEvents vipEvents;
	
	private IWorldEventListener<BeginMessage> beginMessageListener = new IWorldEventListener<BeginMessage>() {

		@Override
		public void notify(BeginMessage event) {
			beginMessage(event);
		}
		
	};
	
	private IWorldEventListener<PlayerKilled> playerKilledMessageListener = new IWorldEventListener<PlayerKilled>() {

		@Override
		public void notify(PlayerKilled event) {
			playerKilled(event);
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
	
	private VIPGameConfig gameConfig;
	
	private Flag<Boolean> gameRunning = new Flag<Boolean>(false);
	
	private Flag<Boolean> roundRunning = new Flag<Boolean>(false);
	
	private Flag<VIPGameState> gameState = new Flag<VIPGameState>(VIPGameState.NOT_RUNNING);
	
	private Map<UnrealId, CSBotRecord<Player>> records = new LazyMap<UnrealId, CSBotRecord<Player>>() {
		@Override
		protected CSBotRecord<Player> create(UnrealId key) {
			return new CSBotRecord<Player>(key, gameConfig);
		}
	};
	
	private CSBotRecord<Player> vip;
	
	private Location vipSafeArea;
	
	private CSRoundState roundState;

	private long simTimeCurrent;
	
	private boolean alive = false;
	
	public VIPBotModule(UT2004Bot agent, AgentInfo info, Players players) {
		super(agent);
		
		this.info = info;
		if (info == null) {
			info = new UT2004AgentInfo(agent);
		}
		
		this.players = players;
		if (this.players == null) {
			this.players = new Players(agent);
		}
		
		csTranslator = new CSMessagesTranslator(agent.getWorldView(), false);
		
		vipEvents = new VIPEvents(agent.getWorldView()) {
			protected void csAssignVIP(CSAssignVIP event) {
				VIPBotModule.this.csAssignVIP(event);
			}

			protected void csBotStateChanged(CSBotStateChanged event) {
				VIPBotModule.this.csBotStateChanged(event);
			}

			protected void csCounterTerroristsWin(CSCounterTerroristsWin event) {
				VIPBotModule.this.csCounterTerroristsWin(event);
			}

			protected void csRoundEnd(CSRoundEnd event) {
				VIPBotModule.this.csRoundEnd(event);
			}

			protected void csRoundStart(CSRoundStart event) {
				VIPBotModule.this.csRoundStart(event);
			}

			protected void csRoundState(CSRoundState event) {
				VIPBotModule.this.csRoundState(event);
			}

			protected void csSetVIPSafeArea(CSSetVIPSafeArea event) {
				VIPBotModule.this.csSetVIPSafeArea(event);
			}

			protected void csTeamScoreChangedListener(CSTeamScoreChanged event) {
				VIPBotModule.this.csTeamScoreChangedListener(event);
			}

			protected void csTerroristsWin(CSTerroristsWin event) {
				VIPBotModule.this.csTerroristsWin(event);
			}

			protected void csVIPKilled(CSVIPKilled event) {
				VIPBotModule.this.csVIPKilled(event);
			}

			protected void csVIPSafe(CSVIPSafe event) {
				VIPBotModule.this.csVIPSafe(event);
			}

			protected void csVIPGameEnd(VIPGameEnd event) {
				VIPBotModule.this.csVIPGameEnd(event);
			}

			protected void csVIPGameStart(VIPGameStart event) {
				VIPBotModule.this.csVIPGameStart(event);
			}
		};
	}
	
	// ==============
	// USEFUL GETTERS
	// ==============
	
	/**
	 * YOUR BOT {@link UnrealId}.
	 * @return
	 */
	public UnrealId getId() {
		return info.getId();
	}
	
	/**
	 * Whether VIP-GAME has been started. This is not saying anything about "whether ROUND is running" or "current STATE of the game".
	 * 
	 * @see #isRoundRunning()
	 * @see #getGameState()
	 * @return
	 */
	public boolean isGameRunning() {
		return gameRunning.getFlag();
	}
	
	/**
	 * Whether VIP-GAME has been started flag. This is not saying anything about "whether ROUND is running" or "current STATE of the game".
	 * 
	 * @see #getGameRunningFlag()
	 * @return
	 */
	public ImmutableFlag<Boolean> getGameRunningFlag() {
		return gameRunning.getImmutable();
	}
	
	/**
	 * Whether VIP-GAME-ROUND is running. This does not say anything about "current STATE of the round".
	 * 
	 * @see #getGameState()
	 * @return
	 */
	public boolean isRoundRunning() {
		return gameRunning.getFlag() && roundRunning.getFlag();
	}
	
	/**
	 * Whether VIP-GAME-ROUND is running. This does not say anything about "current STATE of the round".
	 * 
	 * @see #getGameStateFlag()
	 * @return
	 */
	public ImmutableFlag<Boolean> getRoundRunningFlag() {
		return roundRunning.getImmutable();
	}
	
	/**
	 * Current detailed state of the game, see {@link VIPGameState}.
	 * 
	 * @return
	 */
	public VIPGameState getGameState() {
		return gameState.getFlag();
	}
	
	/**
	 * Current detailed state of the game, see {@link VIPGameState}.
	 * 
	 * @return
	 */
	public ImmutableFlag<VIPGameState> getGameStateFlag() {
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
	 * If {@link #isRoundRunning()} tells whether you are on Counter-Terrorist team. (Note that if you are {@link #isMeVIP()} then this is TRUE as well...)
	 * if round is NOT running, returns false.
	 * 
	 * @return
	 */
	public boolean isMeCounterTerrorist() {
		return isCounterTerrorist(getId());
	}
	
	/**
	 * If {@link #isRoundRunning()} tells whether you are on Terrorist team,
	 * if round is NOT running, returns false.
	 * 
	 * @return
	 */
	public boolean isMeTerrorist() {
		return isTerrorist(getId());
	}
	
	/**
	 * If {@link #isRoundRunning()} tells whether you are the VIP.
	 * if round is NOT running, returns false.
	 * 
	 * @return
	 */
	public boolean isMeVIP() {
		return isVIP(getId());
	}
	
	/**
	 * If round is running, returns {@link CSBotState} of your bot.
	 * <p><p>
	 * If round is NOT running, returns null.
	 * @return
	 */
	public CSBotState getMyState() {
		return getBotState(getId());
	}
	
	/**
	 * Get game state of the bot with 'id'. 
	 * @param id
	 * @return
	 */
	public CSBotState getBotState(UnrealId id) {
		if (records.containsKey(id)) return ensureRecord(id).getBotState();
		return null;
	}

	/**
	 * Returns overall score of your team.
	 * @return
	 */
	public int getTeamScore() {
		return ensureRecord(getId()).getMyTeamScore();
	}
	
	/**
	 * Returns overall round win count of your team.
	 * @return
	 */
	public int getTeamRoundWinCount() {
		return ensureRecord(getId()).getMyTeamWins();
	}
	
	/**
	 * Returns current result of the game, who is winning.
	 * @return
	 */
	public VIPGameResult getWinningTeam() {
		return ensureRecord(getId()).getGameResult();
	}
	
	/**
	 * If {@link #isRoundRunning()}, returns whether 'botId' is on Counter-Terrorist team.
	 * <p><p>
	 * If round is NOT running, returns false.
	 * 
	 * @param botId
	 * @return
	 */
	public boolean isCounterTerrorist(UnrealId botId) {
		if (!isRoundRunning()) return false;
		CSBotRecord<Player> record = ensureRecord(botId);
		if (record.getBotState() != null) return record.getBotState().role.team == CSBotTeam.COUNTER_TERRORIST;
		if (record.getBotId() == info.getId()) return info.getTeam() == CSBotTeam.COUNTER_TERRORIST.ut2004Team;
		if (record.getPlayer() != null) return record.getPlayer().getTeam() == CSBotTeam.COUNTER_TERRORIST.ut2004Team;
		return false;
	}
	
	/**
	 * If {@link #isRoundRunning()}, returns whether 'botId' is on Terrorist team.
	 * <p><p>
	 * If round is NOT running, returns false.
	 * 
	 * @param botId
	 * @return
	 */
	public boolean isTerrorist(UnrealId botId) {
		if (!isRoundRunning()) return false;
		CSBotRecord<Player> record = ensureRecord(botId);
		if (record.getBotState() != null) return record.getBotState().role.team == CSBotTeam.TERRORIST;
		if (record.getBotId() == info.getId()) return info.getTeam() == CSBotTeam.TERRORIST.ut2004Team;
		if (record.getPlayer() != null) return record.getPlayer().getTeam() == CSBotTeam.TERRORIST.ut2004Team;
		return false;
	}
	
	/**
	 * If {@link #isRoundRunning()}, returns whether 'botId' is VIP.
	 * <p><p>
	 * If round is NOT running, returns false.
	 * 
	 * @param botId
	 * @return
	 */
	public boolean isVIP(UnrealId botId) {
		if (!isRoundRunning()) return false;
		if (vip != null && vip.getBotId() == botId) return true;
		CSBotRecord<Player> record = ensureRecord(botId);
		if (record.getBotState() != null) return record.getBotState().role == CSBotRole.VIP;
		return false;
	}
	
	/**
	 * If {@link #isRoundRunning()}, returns time (in seconds) how much time VIP has to reach its safe area.
	 * <p><p>
	 * If round is NOT running, returns {@link Double#POSITIVE_INFINITY}.
	 * 
	 * @return
	 */
	public double getRemainingRoundTime() {
		return isRoundRunning() ? roundState.getRoundTimeLeftUT() : Double.POSITIVE_INFINITY; 
	}
	
	/**
	 * Returns UNREAL-ID of the VIP if already assigned.
	 * @return
	 */
	public UnrealId getVIPId() {
		return vip == null ? null : vip.getBotId();
	}
	
	/**
	 * Current game config - DO NOT SET ANYTHING! Only for reading.
	 * @return
	 */
	public VIPGameConfig getGameConfig() {
		return gameConfig;
	}
	
	/**
	 * Precise location of the VIP safe-area within the environment if known. 
	 * If you are on Terrorist (red) team, you will not get this information at all.
	 * 
	 * @return
	 */
	public Location getVIPSafeArea() {
		return vipSafeArea;
	}
	
	/**
	 * NavPoint nearest to the VIP safe area (typically very close) if known.
	 * If you are on Terrorist (red) team, you will not get SafeArea information at all.
	 * @return
	 */
	public NavPoint getVIPSafeAreaNavPoint() {
		if (vipSafeArea == null) return null;
		return DistanceUtils.getNearest(worldView.getAll(NavPoint.class).values(), getVIPSafeArea());
	}
	
	/**
	 * Returns distance between 'located' from the 'safe-area' including {@link VIPGameConfig#getVIPSafeAreaRadius()}, thus the returned number may be negative.
	 * If you are on Terrorist (red) team, you will not get SafeArea information, so this returns null.
	 * @param located
	 * @return
	 */
	public Double getVIPSafeAreaDistance(ILocated located) {		
		if (located == null) return Double.POSITIVE_INFINITY;
		Location safeArea = getVIPSafeArea();		
		if (safeArea == null) return Double.POSITIVE_INFINITY;
		Location location = located.getLocation();
		if (location == null) return Double.POSITIVE_INFINITY;
		return safeArea.getDistance(location) - gameConfig.getVipSafeAreaRadius();		
	}
	
	/**
	 * Returns distance betweenn your bot and the safe-area, the number may be negative.
	 * If you are on Terrorist (red) team, you will not get SafeArea information, so this returns null.
	 * @return
	 */
	public Double getMySafeAreaDistance() {
		return getVIPSafeAreaDistance(info.getLocation());
	}
	
	/**
	 * Whether 'located' is within the safe-area given its position ({@link VIPGameConfig#getVIPSafeArea()}) and area radius ({@link VIPGameConfig#getVIPSafeAreaRadius()}).
	 * If you are on Terrorist (red) team, you will not get SafeArea information, so this returns null.
	 * @param located
	 * @return
	 */
	public Boolean isInVIPSafeArea(ILocated located) {
		Double distance = getVIPSafeAreaDistance(located);
		if (distance == null) return null;
		return distance <= 0;
	}
		
	// ==============
	// EVENT HANDLERS
	// ==============
	
	protected void beginMessage(BeginMessage event) {
		simTimeCurrent = event.getSimTime();
	}
	
	protected void playerUpdate(IWorldObjectEvent<PlayerMessage> event) {
		if (event.getObject().isSpectator()) return;
		CSBotRecord<Player> record = ensureRecord(event.getObject().getId());
		if (record != null) record.setPlayer(event.getObject());
	}

	protected void playerLeft(PlayerLeft event) {
		CSBotRecord<Player> record = ensureRecord(event.getId());
		if (record != null) record.setInGame(false);		
	}

	protected void playerJoinsGame(PlayerJoinsGame event) {
		ensureRecord(event.getId());
	}
	
	protected void playerKilled(PlayerKilled event) {
		CSBotRecord<Player> record = ensureRecord(event.getId());
		if (record == null) return;
		record.botDied(event.getId());
	}
	
	protected void botKilled(BotKilled event) {
		alive = false;
	}
	
	protected void spawn(Spawn event) {
		alive = true;
	}
	
	// ===================
	// VIP EVENTS HANDLERS
	// ===================
	
	protected void csAssignVIP(CSAssignVIP event) {
		vip = ensureRecord(event.getBotId());
		if (vip == null) return;
		vip.setVIPForThisRound();
		
		// PRESET BOT STATE
		if (vip.getBotId() == getId()) {
			updateBotNameTag();
		}
	}

	protected void csBotStateChanged(CSBotStateChanged event) {		
		CSBotRecord<Player> record = ensureRecord(event.getBotId());
		if (record == null) return;
		record.setBotState(event.getNewStateEnum());
	}

	protected void csCounterTerroristsWin(CSCounterTerroristsWin event) {
		for (CSBotRecord<Player> record : records.values()) {
			record.counterTerroristsWin();
		}
	}

	protected void csRoundEnd(CSRoundEnd event) {
		roundRunning.setFlag(false);
		gameState.setFlag(VIPGameState.ROUND_ENDED);	
	}

	protected void csRoundStart(CSRoundStart event) {
		// ROUND RESET
		vip = null;
		vipSafeArea = null;
		
		for (CSBotRecord<Player> record : records.values()) {
			if (!record.isInGame()) {
				continue;
			}
			int ut2004Team;
			if (record.getBotId() == info.getId()) {
				ut2004Team = info.getTeam();
			} else {
				if (record.getPlayer() == null) continue;
				ut2004Team = record.getPlayer().getTeam();
			}
			switch (ut2004Team) {
				case AgentInfo.TEAM_RED: record.setBotState(CSBotState.TERRORIST); break;
				case AgentInfo.TEAM_BLUE: record.setBotState(CSBotState.COUNTER_TERRORIST); break;
			}
		}	
	}

	protected void csRoundState(CSRoundState event) {
		switch (event.getGameStateEnum()) {
		case ROUND_RUNNING:
			// ROUND HAS OFFICIALLY STARTED!
			for (CSBotRecord<Player> record : records.values()) {
				record.setSpawned(true);
			}	
			updateBotNameTag();
			roundRunning.setFlag(true);
			break;
		}
		
		roundState = event;
		gameState.setFlag(event.getGameStateEnum());
	}

	protected void csSetVIPSafeArea(CSSetVIPSafeArea event) {
		vipSafeArea = event.getSafeArea();
	}

	protected void csTeamScoreChangedListener(CSTeamScoreChanged event) {
		for (CSBotRecord<Player> record : records.values()) {
			record.teamScoreChanged(event);
		}
		if (event.getUt2004Team() == info.getTeam()) updateBotNameTag();
	}

	protected void csTerroristsWin(CSTerroristsWin event) {
		for (CSBotRecord<Player> record : records.values()) {
			record.terroristsWin();
		}
	}

	protected void csVIPKilled(CSVIPKilled event) {
		if (vip != null) vip.botDied(event.getKillerId());
	}

	protected void csVIPSafe(CSVIPSafe event) {
		for (CSBotRecord<Player> record : records.values()) {
			record.vipSafe(event.getVipId());
		}
	}

	protected void csVIPGameEnd(VIPGameEnd event) {
		roundRunning.setFlag(false);
		gameState.setFlag(VIPGameState.NOT_RUNNING);
		gameRunning.setFlag(false);
		removeBotNameTag();
	}

	protected void csVIPGameStart(VIPGameStart event) {
		gameConfig = new VIPGameConfig(event);
		resetCSGameData();
		gameRunning.setFlag(true);		
	}
		
	// =====
	// UTILS
	// =====
	
	private CSBotRecord<Player> ensureRecord(UnrealId botId) {
		if (botId == null || botId == UT2004VIPServer.SERVER_UNREAL_ID) return null;
		if (!gameRunning.getFlag()) return null;
		CSBotRecord<Player> record = records.get(botId);
		record.setInGame(true);
		Player player = players.getPlayer(botId);
		if (player != null) {
			record.setPlayer(player);
		}
		return record;
	}
	
	private void deleteRecord(UnrealId botId) {
		records.remove(botId);
	}
	
	private void resetCSGameData() {
		records.clear();
		removeBotNameTag();
	}
	
	private void removeBotNameTag() {
		info.getBotName().deleteInfo("CS");
	}
	
	private void updateBotNameTag() {
		CSBotState myState = records.get(getId()).getBotState();
		if (myState != null) {
			info.getBotName().setInfo("CS", myState.name() + " | " + records.get(getId()).getMyTeamWins());
		}
	}

	// ==========
	// LIFE-CYCLE
	// ==========
	
	@Override
	protected void start(boolean startToPaused) {
		super.start(startToPaused);
		csTranslator.enable();
		vipEvents.enableCSEvents();
		
		agent.getWorldView().addEventListener(BeginMessage.class, beginMessageListener);
		agent.getWorldView().addEventListener(BotKilled.class, botKilledMessageListener);
		agent.getWorldView().addEventListener(Spawn.class, spawnMessageListener);
		agent.getWorldView().addEventListener(PlayerJoinsGame.class, myPlayerJoinsGameMessageListener);
		agent.getWorldView().addEventListener(PlayerLeft.class, myPlayerLeftMessageListener);
		agent.getWorldView().addObjectListener(Player.class, WorldObjectUpdatedEvent.class, myPlayerListener);
	}
	
	@Override
	protected void cleanUp() {
		super.cleanUp();
		csTranslator.disable();
		vipEvents.disableTagEvents();
		
		agent.getWorldView().removeEventListener(BeginMessage.class, beginMessageListener);
		agent.getWorldView().removeEventListener(BotKilled.class, botKilledMessageListener);
		agent.getWorldView().removeEventListener(Spawn.class, spawnMessageListener);
		agent.getWorldView().removeEventListener(PlayerJoinsGame.class, myPlayerJoinsGameMessageListener);
		agent.getWorldView().removeEventListener(PlayerLeft.class, myPlayerLeftMessageListener);
		agent.getWorldView().removeObjectListener(Player.class, WorldObjectUpdatedEvent.class, myPlayerListener);
	}

}
