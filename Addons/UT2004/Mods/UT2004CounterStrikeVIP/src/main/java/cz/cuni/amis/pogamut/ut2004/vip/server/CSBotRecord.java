package cz.cuni.amis.pogamut.ut2004.vip.server;

import java.util.Map;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerMessage;
import cz.cuni.amis.pogamut.ut2004.utils.UnrealUtils;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.CSBotRole;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.CSBotState;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.CSBotTeam;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.VIPGameConfig;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.VIPGameResult;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSTeamScoreChanged;
import cz.cuni.amis.utils.maps.LazyMap;
import cz.cuni.amis.utils.maps.LazyMapMap;

/**
 * VIPBotRecord
 * 
 * @author Jimmy
 *
 * @param <PLAYER_CONTAINER>
 */
public class CSBotRecord<PLAYER_CONTAINER> {
	
	private boolean inited = false;
	
	private UnrealId botId;
	
	private long initTime;
	
	private long finishTime = -1;	
	
	private CSBotState botState = CSBotState.TERRORIST;
	
	/**
	 * How many times this bot has been SEEKER.
	 */
	private int vipCount = 0;
	
	/**
	 * How many times this bot has been TERRORIST.
	 */
	private int terroristCount = 0;
	
	/**
	 * How many times this bot has been ("ordinary") COUNTER TERRORIST (not VIP).
	 */
	private int counterTerroristCount = 0;
	
	private boolean spawned = false;
	
	// ============
	// SCORE FIELDS
	// ============
	
	/**
	 * Values tracking results of teams.
	 */
	private CSTeamsRecord teamsRecord;

	/**
	 * How many times "BOT UNDER KEY" was VIP and ESCAPED.
	 */
	private Map<UnrealId, Integer> vipSafe = new LazyMap<UnrealId, Integer>() {
		@Override
		protected Integer create(UnrealId key) {
			return 0;
		}		
	};
	
	/**
	 * How many times "BOT UNDER KEY" was VIP and GOT KILLED.
	 */
	private Map<UnrealId, Integer> vipKilled = new LazyMap<UnrealId, Integer>() {
		@Override
		protected Integer create(UnrealId key) {
			return 0;
		}		
	};
	
	/**
	 * How many times "BOT UNDER FIRST KEY" was TERRORIST and killed "BOT UNDER SECOND KEY" who was playing VIP.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private LazyMapMap<UnrealId, UnrealId, Integer> vipKilledBy = new LazyMapMap() {

		@Override
		protected Object create(Object key) {
			return 0;
		}
		
	};
	
	// =========
	// UTILITIES
	// =========
	
	private boolean inGame = false;
	
	private PLAYER_CONTAINER player;
	
	// ==============
	// IMPLEMENTATION
	// ==============
	
	public CSBotRecord() {		
	}
	
	public CSBotRecord(UnrealId botId, VIPGameConfig config) {
		init(botId, config);
	}
	
	public void init(UnrealId botId, VIPGameConfig config) {
		if (inited) throw new RuntimeException("Cannot initialize twice!");
		inited = true;
		this.botId = botId;
		this.teamsRecord = new CSTeamsRecord(config);
	}
	
	public long getInitTime() {
		return initTime;
	}

	public void setInitTime(long initTime) {
		this.initTime = initTime;
	}

	public long getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(long finishTime) {
		this.finishTime = finishTime;
	}

	public CSBotState getBotState() {
		return botState;
	}
	
	public CSBotRole getBotRole() {
		return botState.role;
	}
	
	public CSBotTeam getMyTeam() {
		return botState.role.team;
	}
	
	public CSBotTeam getEnemyTeam() {
		return botState.role.team.getEnemyTeam();
	}

	public void setBotState(CSBotState botState) {
		this.botState = botState;
	}

	public boolean isInGame() {
		return inGame;
	}

	public void setInGame(boolean inGame) {
		this.inGame = inGame;
	}
	
	public UnrealId getBotId() {		
		if (botId != null) return botId;
		
		if (player != null) {
			if (player instanceof PlayerMessage) return ((PlayerMessage)player).getId();
			if (player instanceof Player) return ((Player)player).getId();
		}
		
		return botId;
	}
	
	public boolean isBot() {
		return UnrealUtils.isBotId(botId);
	}
	
	public String getBotName() {
		if (player instanceof PlayerMessage ) return ((PlayerMessage)player).getName();
		if (player instanceof Player) return ((Player)player).getName();		
		return "?";
	}
	
	public boolean isSpawned() {
		return spawned;
	}

	public void setSpawned(boolean spawned) {
		this.spawned = spawned;
	}
	
	// ==============
	// SCORE & COUNTS
	// ==============

	public int getVIPCount() {
		return vipCount;
	}

	public int getTCount() {
		return terroristCount;
	}
	
	public int getCTCount() {
		return counterTerroristCount;
	}
	
	public int getMyTeamWins() {
		return teamsRecord.getWins(getMyTeam());
	}
	
	public int getMyEnemyWins() {
		return teamsRecord.getWins(getEnemyTeam());
	}

	public int getMyTeamScore() {
		return teamsRecord.getScore(getMyTeam());
	}
	
	public int getEnemyTeamScore() {
		return teamsRecord.getScore(getEnemyTeam());
	}
	
	public VIPGameResult getGameResult() {
		return teamsRecord.getResult();
	}
	
	// ======
	// EVENTS
	// ======
	
	/**
	 * THIS BOT HAS BEEN SET AS SEEKER.
	 */
	public void setVIPForThisRound() {
		botState = CSBotState.VIP;
		++vipCount;
	}
	
	/**
	 * THIS BOT HAS BEEN SET AS TERRORIST.
	 */
	public void setTerroristForThisRound() {
		botState = CSBotState.TERRORIST;
		++terroristCount;
	}
	
	/**
	 * THIS BOT HAS BEEN SET AS COUNTER TERRORIST.
	 */
	public void setCounterTerroristForThisRound() {
		botState = CSBotState.COUNTER_TERRORIST;
		++terroristCount;
	}
	
	/**
	 * VIP has just been safed
	 * @param botId
	 */
	public void vipSafe(UnrealId botId) {
		vipSafe.put(botId, vipSafe.get(botId)+1);
		if (getBotId() == botId) {
			switch (botState) {
			case VIP:
				botState = CSBotState.VIP_SAFE;
				break;
			}
		}
	}
	
	/**
	 * THIS BOT HAS DIED
	 */
	public void botDied(UnrealId killedBy) {
		switch (botState) {
		case COUNTER_TERRORIST: 
			botState = CSBotState.COUNTER_TERRORIST_DEAD; 
			break;
		case TERRORIST: 
			botState = CSBotState.TERRORIST_DEAD; 
			break;
		case VIP: 
			botState = CSBotState.VIP_DEAD;
			vipKilled.put(getBotId(), vipKilled.get(getBotId())+1);
			if (killedBy != null) {
				vipKilledBy.put(killedBy, getBotId(), vipKilledBy.get(killedBy, getBotId())+1);
			}
			break;
		}
		setSpawned(false);
	}
	
	/**
	 * Terrorist TEAM has won.
	 */
	public void terroristsWin() {
		teamsRecord.terroristsWin();		
	}
	
	/**
	 * Counter-terrorist TEAM has won.
	 */
	public void counterTerroristsWin() {
		teamsRecord.counterTerroristsWin();
	}

	public void reset() {
		initTime = System.currentTimeMillis();
		finishTime = -1;
		botState = CSBotState.TERRORIST;
		
		counterTerroristCount = 0;
		vipCount = 0;
		
		teamsRecord.clear();
		
		vipSafe.clear();
		vipKilled.clear();
		vipKilledBy.clear();		
	}

	public PLAYER_CONTAINER getPlayer() {
		return player;
	}

	public void setPlayer(PLAYER_CONTAINER player) {
		this.player = player;
	}

	public void teamScoreChanged(CSTeamScoreChanged event) {
		teamsRecord.teamScoreChanged(event);
	}

	protected void setConfig(VIPGameConfig config) {		
		this.teamsRecord.setConfig(config);
	}
	
}
