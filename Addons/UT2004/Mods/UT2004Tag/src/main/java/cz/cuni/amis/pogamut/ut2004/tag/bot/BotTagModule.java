package cz.cuni.amis.pogamut.ut2004.tag.bot;

import java.util.Formatter;
import java.util.Map;

import cz.cuni.amis.pogamut.base.agent.module.SensorModule;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Players;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.UT2004AgentInfo;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BeginMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerJoinsGame;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerLeft;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerMessage;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.TagMessagesTranslator;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.messages.TagGameEnd;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.messages.TagGameRunning;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.messages.TagGameStart;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.messages.TagPassed;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.messages.TagPlayerImmunity;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.messages.TagPlayerScoreChanged;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.messages.TagPlayerStatusChanged;
import cz.cuni.amis.pogamut.ut2004.tag.server.BotTagRecord;
import cz.cuni.amis.pogamut.ut2004.tag.server.UT2004TagServer;
import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.flag.ImmutableFlag;
import cz.cuni.amis.utils.maps.LazyMap;

public class BotTagModule extends SensorModule<UT2004Bot>{

	// =======
	// MODULES
	// =======
	
	private AgentInfo info;
	
	private Players players;
	
	// ==========
	// TAG EVENTS
	// ==========
	
	private TagMessagesTranslator tagTranslator;
	
	private TagEvents tagEvents;
	
	private IWorldEventListener<BeginMessage> beginMessageListener = new IWorldEventListener<BeginMessage>() {

		@Override
		public void notify(BeginMessage event) {
			beginMessage(event);
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
	
	private Flag<Boolean> gameRunning = new Flag<Boolean>(false);
	
	private Map<UnrealId, BotTagRecord<Player>> records = new LazyMap<UnrealId, BotTagRecord<Player>>() {
		@Override
		protected BotTagRecord<Player> create(UnrealId key) {
			return new BotTagRecord<Player>(key);
		}
	};

	private TagGameStart tagSettings;

	private long simTimeCurrent;
	
	public BotTagModule(UT2004Bot agent, AgentInfo info, Players players) {
		super(agent);
		
		this.info = info;
		if (info == null) {
			info = new UT2004AgentInfo(agent);
		}
		
		this.players = players;
		if (this.players == null) {
			this.players = new Players(agent);
		}
		
		tagTranslator = new TagMessagesTranslator(agent.getWorldView(), false);
		
		tagEvents = new TagEvents(agent.getWorldView()) {
			public void tagGameStart(TagGameStart event) {
				BotTagModule.this.tagGameStart(event);
			}
			
			@Override
			public void tagGameRunning(TagGameRunning event) {
				BotTagModule.this.tagGameRunning(event);
			}
			
			public void tagGameEnd(TagGameEnd event) {
				BotTagModule.this.tagGameEnd(event);
			}
			
			public void tagPassed(TagPassed event) {
				BotTagModule.this.tagPassed(event);
			}
			
			public void tagPlayerImmunity(TagPlayerImmunity event) {
				BotTagModule.this.tagPlayerImmunity(event);
			}
			
			public void tagPlayerScoreChanged(TagPlayerScoreChanged event) {
				BotTagModule.this.tagPlayerScoreChanged(event);
			}
			
			public void tagPlayerStatusChanged(TagPlayerStatusChanged event) {
				BotTagModule.this.tagPlayerStatusChanged(event);
			}
		};
		
		agent.getWorldView().addEventListener(BeginMessage.class, beginMessageListener);
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
	 * Returns {@link BotTagRecord} for the player under 'botId'.
	 * <br/><br/>
	 * DO NOT UPDATE THE RECORD - READ-ONLY !!!
	 *  
	 * @param botId
	 * @return
	 */
	public BotTagRecord<Player> getTagRecord(UnrealId botId) {
		return ensureRecord(botId);
	}
	
	/**
	 * Returns {@link BotTagRecord} for the 'bot'. 
	 * <br/><br/>
	 * DO NOT UPDATE THE RECORD - READ-ONLY !!!
	 * 
	 * @param bot
	 * @return
	 */
	public BotTagRecord<Player> getTagRecord(Player bot) {
		return ensureRecord(bot.getId());
	}
	
	/**
	 * Whether TAG-GAME has been started.
	 * @return
	 */
	public boolean isGameRunning() {
		return gameRunning.getFlag();
	}
	
	/**
	 * Whether TAG-GAME has been started.
	 * @return
	 */
	public ImmutableFlag<Boolean> getGameRunningFlag() {
		return gameRunning.getImmutable();
	}
	
	/**
	 * When some bot reached THIS score, the game ends prematurely.
	 * @return
	 */
	public int getGameMaxScore() {
		if (tagSettings == null) return -1;
		return tagSettings.getGameMaxScore();
	}
	
	/**
	 * In seconds, total time that has passed since the game start.
	 * @return
	 */
	public double getGameTime() {
		if (tagSettings == null) return -1;
		return ((double)(simTimeCurrent - tagSettings.getSimTime())) / (double)1000;
	}
	
	/**
	 * In seconds, time remaining before the tag-game ends.
	 * @return
	 */
	public double getRemainingTime() {
		if (tagSettings == null) return -1;
		return ((double)(tagSettings.getGameTimeUT() * 1000)) - getGameTime();
	}
	
	/**
	 * How close you have to get to the bot to pass the tag (and vice versa).
	 * @return
	 */
	public double getTagDistance() {
		if (tagSettings == null) return -1;
		return tagSettings.getTagPassDistance();
	}
	
	/**
	 * Whether YOUR-BOT is immune to other bot (it cannot pass the tag to you).
	 * @param botId
	 * @return
	 */
	public boolean isMeImmune(UnrealId botId) {
		if (botId == null) return false;
		
		if (getId() == null) return false;
		
		return ensureRecord(getId()).getImmunity() == botId;
	}
	
	/**
	 * Whether YOUR-BOT is immune to other bot (it cannot pass the tag to you).
	 * @param bot
	 * @return
	 */
	public boolean isMeImmune(Player bot) {
		if (bot == null) return false;
		return isMeImmune(bot.getId());
	}
	
	/**
	 * Whether OTHER-BOT is immune to you (you cannot pass the tag to it).
	 * @param botId
	 * @return
	 */
	public boolean isImmuneToMe(UnrealId botId) {
		if (botId == null) return false;
		
		if (getId() == null) return false;
		
		return ensureRecord(botId).getImmunity() == getId();
	}
	
	/**
	 * Whether OTHER-BOT is immune to you (you cannot pass the tag to it).
	 * @param bot
	 * @return
	 */
	public boolean isImmuneToMe(Player bot) {
		if (bot == null) return false;
		return isImmuneToMe(bot.getId());
	}
	
	/**
	 * How many times YOU-BOT has been tagged by other bot (excluding tags assigned as random by the server).
	 * @return
	 */
	public int getMyScore() {
		if (getId() == null) return 0;
		return ensureRecord(getId()).getScore();
	}
	
	/**
	 * How many times OTHER-BOT has been tagged by yet-another bot (excluding tags assigned as random by the server).
	 * @param botId
	 * @return
	 */
	public int getScore(UnrealId botId) {
		if (botId == null) return 0;
		
		return ensureRecord(botId).getScore();
	}
	
	/**
	 * Whether YOUR-BOT has the tag (should chase others non-immune-to-you).
	 * @return
	 */
	public boolean hasTag() {
		if (getId() == null) return false;
		return ensureRecord(getId()).isHasTag();
	}
	
	/**
	 * Whether OTHER-BOT has the tag (should chase others non-immune-to-it).
	 * @return
	 */
	public boolean hasTag(UnrealId botId) {
		if (botId == null) return false;
		if (getId() == null) return false;
		
		return ensureRecord(botId).isHasTag();
	}
	
	/**
	 * Whether OTHER-BOT has the tag (should chase others non-immune-to-it).
	 * @return
	 */
	public boolean hasTag(Player bot) {
		return hasTag(bot.getId());
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
		deleteRecord(event.getId());		
	}

	protected void playerJoinsGame(PlayerJoinsGame event) {
		ensureRecord(event.getId());
	}
	
	protected void tagGameStart(TagGameStart event) {
		resetTagGameData();
		tagSettings = event;
		gameRunning.setFlag(true);
	}
	
	protected void tagGameRunning(TagGameRunning event) {
		if (gameRunning.getFlag()) return;
		
		resetTagGameData();			
		tagSettings = new TagGameStart();
		tagSettings.setGameMaxScore(event.getGameMaxScore());
		tagSettings.setGameTimeUT(event.getGameTimeUT());
		tagSettings.setTagPassDistance(event.getTagPassDistance());
		gameRunning.setFlag(true);
	}
		
	protected void tagGameEnd(TagGameEnd event) {
		gameRunning.setFlag(false);
	}
	
	protected void tagPassed(TagPassed event) {
		if (event.getFromBotId() != null) {
			ensureRecord(event.getFromBotId()).tagPassed(event.getToBotId());
		}
		if (event.getToBotId() != null) {
			ensureRecord(event.getToBotId()).tagged(event.getFromBotId());
		}
		if (event.getFromBotId() == getId()) {
			long totalTimeMillis = ensureRecord(getId()).getTotalTagTimeMillis();
			double totalTimeMins = (((double)totalTimeMillis) / 1000) / 60;
			String time = String.format("%.2f", totalTimeMins);
			agent.getBotName().setInfo("TM", time + "min");	
		}
	}
	
	protected void tagPlayerImmunity(TagPlayerImmunity event) {
		if (event.getBotId() == null) return;
		
		BotTagRecord<Player> record = ensureRecord(event.getBotId());
		ensureRecord(event.getImmuneFromBotId());
				
		if (event.getStatus()) {
			if (event.getImmuneFromBotId() == null) record.setImmunity(null);
			else record.setImmunity(event.getImmuneFromBotId());
		} else {
			record.setImmunity(null);
		}
		
		if (event.getBotId() == getId()) {
			if (event.getStatus()) {
				Player plr = players.getPlayer(event.getImmuneFromBotId());
				agent.getBotName().setInfo("IMUNE", plr == null ? "???" : players.getPlayerName(event.getImmuneFromBotId(), true).trim());				
			} else {
				agent.getBotName().deleteInfo("IMUNE");
			}
		}
	}
	
	protected void tagPlayerScoreChanged(TagPlayerScoreChanged event) {
		if (event.getBotId() == null) return;
		
		BotTagRecord<Player> record = ensureRecord(event.getBotId());
		record.setScore(event.getScore());
		
		if (event.getBotId() == getId()) {
			agent.getBotName().setInfo("T", String.valueOf(event.getScore()));			
		}
	}
	
	protected void tagPlayerStatusChanged(TagPlayerStatusChanged event) {
		if (event.getBotId() == null) return;
		
		BotTagRecord<Player> record = ensureRecord(event.getBotId());
		record.setHasTag(event.getTagStatus());
		
		if (event.getBotId() == getId()) {
			if (event.getTagStatus()) {
				agent.getBotName().setTag("TAG");
			} else {
				agent.getBotName().deleteTag();	
			}
		}
	}
	
	// =====
	// UTILS
	// =====
	
	private BotTagRecord<Player> ensureRecord(UnrealId botId) {
		if (botId == null || botId == UT2004TagServer.SERVER_UNREAL_ID) return null;
		if (records.containsKey(botId)) return records.get(botId);
		BotTagRecord<Player> record = records.get(botId);
		record.setInGame(true);
		return record;
	}
	
	private void deleteRecord(UnrealId botId) {
		records.remove(botId);
	}
	
	private void resetTagGameData() {
		tagSettings = null;
		records.clear();
	}

	// ==========
	// LIFE-CYCLE
	// ==========
	
	@Override
	protected void start(boolean startToPaused) {
		super.start(startToPaused);
		tagTranslator.enable();
		tagEvents.enableTagEvents();
	}
	
	@Override
	protected void cleanUp() {
		super.cleanUp();
		tagTranslator.disable();
		tagEvents.disableTagEvents();		
	}

}
