package cz.cuni.amis.pogamut.ut2004.tag.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.SocketConnection;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectListener;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Configuration;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.SendControlMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.SendMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.StartPlayers;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BeginMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.EndMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerJoinsGame;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerLeft;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerMessage;
import cz.cuni.amis.pogamut.ut2004.communication.worldview.UT2004WorldView;
import cz.cuni.amis.pogamut.ut2004.server.IUT2004Server;
import cz.cuni.amis.pogamut.ut2004.server.impl.UT2004Server;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.TagMessages;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.messages.TagGameEnd;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.messages.TagGameRunning;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.messages.TagGameStart;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.messages.TagMessage;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.messages.TagPassed;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.messages.TagPlayerImmunity;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.messages.TagPlayerScoreChanged;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.messages.TagPlayerStatusChanged;
import cz.cuni.amis.utils.Tuple3;
import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.maps.LazyMap;

public class UT2004TagServer extends UT2004Server implements IUT2004Server {
	
	public static final UnrealId SERVER_UNREAL_ID = UnrealId.get("TAG_SERVER");

	public static final double TAG_DISTANCE = 80;
	
	public static final long GAME_RUNNING_PERIOD_SECS = 5;

	private Random random = new Random(System.currentTimeMillis());
	
	private Object mutex = new Object();
	
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
    
    private TagMessages messages = new TagMessages();

    @Inject
    public UT2004TagServer(UT2004AgentParameters params, IAgentLogger agentLogger, IComponentBus bus, SocketConnection connection, UT2004WorldView worldView, IAct act) {
        super(params, agentLogger, bus, connection, worldView, act);
        getWorldView().addEventListener(BeginMessage.class, myBeginMessageListener);
        getWorldView().addEventListener(EndMessage.class, myEndMessageListener);
        getWorldView().addEventListener(PlayerJoinsGame.class, myPlayerJoinsGameMessageListener);
        getWorldView().addEventListener(PlayerLeft.class, myPlayerLeftMessageListener);
        getWorldView().addObjectListener(PlayerMessage.class, myPlayerListener);
    }
    
    // ==========
    // LIFE-CYCLE
    // ==========
    
    @Override
    protected void init() {
    	super.init();
    	
    	//getLogger().getCategory(YylexParser.COMPONENT_ID.getToken()).setLevel(Level.ALL);
    	//getLogger().getCategory(getWorldView()).setLevel(Level.ALL);
    	
    	synchronized(mutex) {
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
    
    private double tagGameStartUT;
    
    private double tagGameLengthUT;
    
    private double tagGameTimeLeftUT;
    
    private int tagGameMaxScore;
    
    public void startGame(int gameTimeUT, int toScore) {
    	if (gameRunning.getFlag()) throw new RuntimeException("Cannot start the game, game is already running!");
    	
    	synchronized(mutex) {
	    	resetTagGame();    	
	    	gameRunning.setFlag(true);
	    	speak("Game STARTed!");
	    	
	    	tagGameStartUT    = utTimeCurrent;
	    	tagGameLengthUT   = gameTimeUT;
	    	tagGameTimeLeftUT = gameTimeUT;
	    	tagGameMaxScore   = toScore;
	    	
	    	TagGameStart startMsg = new TagGameStart();
	    	startMsg.setGameTimeUT(gameTimeUT);
	    	startMsg.setGameMaxScore(toScore);    
	    	startMsg.setTagPassDistance(TAG_DISTANCE);
	    	send(startMsg);
	    	    	
	    	if (tagged.size() == 0) {
	    		assignTagRandom();
	    	}
    	}
    }
    
    public void endGame() {
    	if (!gameRunning.getFlag()) throw new RuntimeException("Cannot end game, game is not running!");
    	
    	synchronized(mutex) {
	    	TagGameEnd endMsg = new TagGameEnd();
	    	send(endMsg);
	    	
	    	long finishTime = System.currentTimeMillis();
	    	for (BotTagRecord<PlayerMessage> record : records.values()) {
	    		if (!record.isInGame()) continue;
	    		record.setFinishTime(finishTime);
	    		if (record.isHasTag()) {
	    			// PASS ALL TAGS BACK TO SERVER -> will count totalTaggedMillis then...
	    			record.tagPassed(null);
	    		}
	    	}
	    	
	    	speak("Game ENDed!");
	    	
	    	gameRunning.setFlag(false);
    	}
    }
    
    public Flag<Boolean> isGameRunning() {
    	return gameRunning;
    }
    
    // =======
    // MEMBERS
    // =======
    
	private double utTimeCurrent = -1;
	
	private double utTimeLast = -1;
	
	private double utTimeDelta = -1;	
	
	private long utTimeRunningNext = GAME_RUNNING_PERIOD_SECS;
	
	private Set<BotTagRecord<PlayerMessage>> tagged = new HashSet<BotTagRecord<PlayerMessage>>();
	
	private Map<UnrealId, BotTagRecord<PlayerMessage>> records = new LazyMap<UnrealId, BotTagRecord<PlayerMessage>>() {

		@Override
		protected BotTagRecord<PlayerMessage> create(UnrealId key) {
			return new BotTagRecord<PlayerMessage>(key);
		}
		
	};
	
	/**
	 * READ-ONLY!
	 * @return
	 */
	public Map<UnrealId, BotTagRecord<PlayerMessage>> getBotRecords() {
		return records;
	}

    // ==============
    // EVENT HANDLERS
    // ==============
	
	private Map<UnrealId, Long> lastPlayerUpdate = new LazyMap<UnrealId, Long>() {
		@Override
		protected Long create(UnrealId key) {
			return System.currentTimeMillis();
		}		
	};
    
    private void playerUpdate(IWorldObjectEvent<PlayerMessage> event) {
    	synchronized(mutex) {
    		PlayerMessage player = event.getObject();
	    	BotTagRecord<PlayerMessage> record = ensurePlayer(player.getId());
	    	record.setPlayer(player);	    	
    	}
    	synchronized(lastPlayerUpdate) {    		
    		long lastTime = lastPlayerUpdate.get(event.getObject().getId());
    		long currTime = System.currentTimeMillis();
    		lastPlayerUpdate.put(event.getObject().getId(), currTime);
    		long diff = currTime - lastTime;
    		if (diff > 120) {
    			log.warning("Player update too slow for Player[id=" + event.getObject().getId().getStringId() + ", name=" + event.getObject().getName() + "]! Delta " + diff + "ms.");
    		}
    	}
    }
    
    private void playerJoinsGame(PlayerJoinsGame event) {
    	synchronized(mutex) {
    		ensurePlayer(event.getId());
    	}
    }
    
    private void playerLeft(PlayerLeft event) {
    	synchronized(mutex) {
	    	if (!records.containsKey(event.getId())) return;
	    	
	    	BotTagRecord<PlayerMessage> record = records.get(event.getId());
	    	record.setInGame(false);
	    	record.setFinishTime(System.currentTimeMillis());
	    	
	    	if (!gameRunning.getFlag()) return;
	    	
	    	if (record.isHasTag()) {
	    		assignTag(record, null);
	    	}
	    	if (tagged.size() == 0) {
	    		assignTagRandom();
	    	}
    	}
    }
    
    private void timeUpdate(BeginMessage event) {
    	synchronized(mutex) {
	    	utTimeLast = utTimeCurrent;
	    	utTimeCurrent = event.getTime();
	    	utTimeDelta = utTimeCurrent - utTimeLast;
	    	
	    	if (!gameRunning.getFlag()) return;    	
	    	
	    	if (utTimeLast > 0 && utTimeCurrent > 0 && utTimeDelta > 0) {
	    		tagGameTimeLeftUT -= utTimeDelta;
	    		utTimeRunningNext -= utTimeDelta;
	    		if (utTimeRunningNext < 0) {
	    			TagGameRunning runningMsg = new TagGameRunning();
	    			runningMsg.setGameMaxScore(tagGameMaxScore);
	    			runningMsg.setGameTimeUT((int)tagGameLengthUT);
	    			runningMsg.setTagPassDistance(TAG_DISTANCE);
	    			send(runningMsg);
	    			utTimeRunningNext = GAME_RUNNING_PERIOD_SECS;
	    		}
	    	}
	    	if (tagGameTimeLeftUT <= 0) {
	    		endGame();
	    	}
    	}
    }
    
    private void batchEnd(EndMessage event) {
    	synchronized(mutex) {
	    	if (!gameRunning.getFlag()) return;
	    	
	    	List<Tuple3<BotTagRecord<PlayerMessage>, BotTagRecord<PlayerMessage>, Double>> passing = new ArrayList<Tuple3<BotTagRecord<PlayerMessage>, BotTagRecord<PlayerMessage>, Double>>();
	    	
	    	List<BotTagRecord<PlayerMessage>> inGame = getInGameBots();    	
	    	for (BotTagRecord<PlayerMessage> botHasTag : tagged) {
	    		if (botHasTag.getPlayer() == null) continue;
	    		
	    		Tuple3<BotTagRecord<PlayerMessage>, BotTagRecord<PlayerMessage>, Double> passTo = 
	    			new Tuple3<BotTagRecord<PlayerMessage>, BotTagRecord<PlayerMessage>, Double>(
	    				botHasTag, 
	    				null, 
	    				TAG_DISTANCE+1
	    			);
	    		
	    		for (BotTagRecord<PlayerMessage> otherBot : inGame) {
	    			if (botHasTag == otherBot || // same bot 
	    				otherBot.isHasTag() ||   // other bot already has a tag
	    				otherBot.getImmunity() == botHasTag.getBotId() // other bot is immute to botHasTag
	    			) {
	    				continue;
	    			}    			
	    			if (otherBot.getPlayer() == null) {
	    				continue;
	    			}
	    			
	    			double distance = botHasTag.getPlayer().getLocation().getDistance2D(otherBot.getPlayer().getLocation());
	    			if (distance < passTo.getThird()) {
	    				passTo.setSecond(otherBot);
	    				passTo.setThird(distance);
	    			}
	    		}
	    		
	    		if (passTo.getSecond() != null) {
	    			passing.add(passTo);
	    		}
	    	}
	    	
	    	for (Tuple3<BotTagRecord<PlayerMessage>, BotTagRecord<PlayerMessage>, Double> pass : passing) {
	    		assignTag(pass.getFirst(), pass.getSecond());
	    	}
    	}
    }
    
    // =====
    // UTILS
    // =====
    
    private BotTagRecord<PlayerMessage> ensurePlayer(UnrealId botId) {
		if (botId == null) return null;
    	
    	BotTagRecord<PlayerMessage> record = records.get(botId);    	
    	if (record.isInGame()) return record;
    	
    	record.reset();
    	record.setInGame(true);
    	record.setHasTag(false);
    	
    	if (!gameRunning.getFlag()) return record;
    	
    	tagScoreChanged(record);    	
    	if (tagged.size() == 0) {
    		assignTag(null, record);
    	} else {
    		tagStatusChanged(record);
    	}    	
    	
    	return record;
    }
    
    private List<BotTagRecord<PlayerMessage>> getInGameBots() {
		List<BotTagRecord<PlayerMessage>> result = new ArrayList<BotTagRecord<PlayerMessage>>();
    	for (BotTagRecord<PlayerMessage> record : records.values()) {
    		if (!record.isInGame()) continue;
    		if (record.getPlayer().getJmx() == null) continue;
    		result.add(record);
    	}
    	return result;
    }
        
    private void assignTagRandom() {
    	assert(gameRunning.getFlag());
    	
    	List<BotTagRecord<PlayerMessage>> alive = getInGameBots();
    	if (alive.size() == 0) return;
    	BotTagRecord<PlayerMessage> record = alive.get(random.nextInt(alive.size()));
    	assignTag(null, record);
    }
    
    /**
     * @param from may be null == server, will use {@link #SERVER_UNREAL_ID}
     * @param to may be null == server, will use {@link #SERVER_UNREAL_ID}
     */
    private void assignTag(BotTagRecord<PlayerMessage> from, BotTagRecord<PlayerMessage> to) {
    	assert(gameRunning.getFlag());
    	
    	if (from != null || to != null) {
    		TagPassed passedMsg = new TagPassed();
    		passedMsg.setFromBotId(from == null ? null : from.getBotId());
    		passedMsg.setToBotId  (to   == null ? null : to  .getBotId());
    		send(passedMsg);
       	}
    	
    	if (from != null) {
    		removeTag(from, to == null ? SERVER_UNREAL_ID : to.getBotId());    		
    	}
    	if (to != null) {
    		addTag(to, from == null ? SERVER_UNREAL_ID : from.getBotId());
    		speak(to.getPlayer().getName() + " now has the tag!");
    	}   
    	
    }
    
    /**
     * TO BE CALLED FROM {@link #assignTag(BotTagRecord, BotTagRecord)} ONLY!
     * @param to
     * @param from
     */
    private void addTag(BotTagRecord<PlayerMessage> to, UnrealId from) {
    	assert(gameRunning.getFlag());
    	
    	if (from == null) from = SERVER_UNREAL_ID;
    	if (to.isHasTag()) return;
    	
    	to.tagged(from);
    	tagged.add(to);
    	
    	if (from != SERVER_UNREAL_ID) tagScoreChanged(to);
    	tagStatusChanged(to);
    	
    	if (to.getImmunity() != null) {
    		removeImmunity(to);
    	}
    	
    	if (from != SERVER_UNREAL_ID && records.containsKey(from)) {
    		BotTagRecord<PlayerMessage> fromRecord = records.get(from);
    		addImmunity(fromRecord, to.getBotId());
    	}
    	
    	if (to.getScore() >= tagGameMaxScore) {
    		endGame();
    	}
    }
    
    /**
     * TO BE CALLED FROM {@link #assignTag(BotTagRecord, BotTagRecord)} ONLY!
     * @param to
     * @param from
     */
    private void removeTag(BotTagRecord<PlayerMessage> from, UnrealId to) {
    	assert(gameRunning.getFlag());
    	
    	if (to == null) to = SERVER_UNREAL_ID;
    	if (!from.isHasTag()) return;
    	
    	from.tagPassed(to);
    	tagged.remove(from);
    	
    	tagStatusChanged(from);
    	
    	for (BotTagRecord<PlayerMessage> record : records.values()) {
			if (record.getImmunity() == from.getBotId()) {
				removeImmunity(record);
			}
		}
    }
    
    private void addImmunity(BotTagRecord<PlayerMessage> record, UnrealId immuneTo) {
    	assert(gameRunning.getFlag());
    	
    	if (record.getImmunity() != null) {
    		removeImmunity(record);
    	}
    	
    	record.setImmunity(immuneTo);
    	
    	TagPlayerImmunity msg = new TagPlayerImmunity();
    	msg.setBotId(record.getBotId());
    	msg.setImmuneFromBotId(record.getImmunity());
    	msg.setStatus(true);
    	send(msg);    	
    }
    
    private void removeImmunity(BotTagRecord<PlayerMessage> record) {
    	assert(gameRunning.getFlag());
    	
    	if (record.getImmunity() == null) return;
    	
    	TagPlayerImmunity msg = new TagPlayerImmunity();
    	msg.setBotId(record.getBotId());
    	msg.setImmuneFromBotId(record.getImmunity());
    	msg.setStatus(false);
    	send(msg);
    	
    	record.setImmunity(null);
    }
    
    private void tagScoreChanged(BotTagRecord<PlayerMessage> record) {
    	TagPlayerScoreChanged scoreMsg = new TagPlayerScoreChanged();
    	scoreMsg.setBotId(record.getBotId());
    	scoreMsg.setScore(record.getScore());
    	send(scoreMsg);
    }
    
    private void tagStatusChanged(BotTagRecord<PlayerMessage> record) {
    	TagPlayerStatusChanged statusMsg = new TagPlayerStatusChanged();
    	statusMsg.setBotId(record.getBotId());
    	statusMsg.setTagStatus(record.isHasTag());
    	send(statusMsg);
    }
    
    private void send(TagMessage message) {
    	if (gameRunning.getFlag()) {
    		SendControlMessage command = messages.write(message);
    		command.setSendAll(true);
    		getAct().act(command);
    	}
    }
    
    private void speak(String message) {
    	if (gameRunning.getFlag()) {
    		getAct().act(new SendMessage().setGlobal(true).setText("[TAG] " + message));
    	}
    }
    
    private void resetTagGame() {
    	gameRunning.setFlag(false);	
    	tagGameStartUT = -1;
   	    tagGameLengthUT = -1;
    	tagGameTimeLeftUT = -1;
    	
    	utTimeRunningNext = GAME_RUNNING_PERIOD_SECS;
    	
    	tagged.clear();
    	
    	Iterator<BotTagRecord<PlayerMessage>> recordIter = records.values().iterator();
    	while (recordIter.hasNext()) {
    		BotTagRecord<PlayerMessage> record = recordIter.next();
    		if (!record.isInGame()) {
    			recordIter.remove();
    			continue;
    		}
    		record.reset();
    		record.setInGame(true);
    	}
    }    

}