package cz.cuni.amis.pogamut.ut2004.tag.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.utils.maps.LazyMap;

public class BotTagRecord<PLAYER_CONTAINER> {
	
	private final UnrealId botId;
	
	private long initTime;
	
	private long finishTime = -1;
	
	private Map<UnrealId, List<Long>> tagGotMillis = new LazyMap<UnrealId, List<Long>>() {
		@Override
		protected List<Long> create(UnrealId key) {
			return new ArrayList<Long>();
		}
	};
	
	private Map<UnrealId, List<Long>> tagPassedMillis = new LazyMap<UnrealId, List<Long>>() {
		@Override
		protected List<Long> create(UnrealId key) {
			return new ArrayList<Long>();
		}
	};
	
	private int score = 0;
	
	/**
	 * How long the bot possed the "tag". 
	 */
	private long totalTagTimeMillis = 0; 
	
	private UnrealId lastTaggedBy = null; // who has tagged this bot as last
	
	private UnrealId immunity = null;
	
	private boolean hasTag = false;
	
	private boolean inGame = false;
	
	private PLAYER_CONTAINER player;
	
	public BotTagRecord(UnrealId botId) {
		this.botId = botId;
	}

	/**
	 * How many times was this bot TAGGED (negative score).
	 * @return
	 */
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	public long getTotalTagTimeMillis() {
		return totalTagTimeMillis;
	}

	public UnrealId getBotId() {
		return botId;
	}

	public UnrealId getImmunity() {
		return immunity;
	}

	public void setImmunity(UnrealId immunity) {
		this.immunity = immunity;
	}

	public boolean isInGame() {
		return inGame;
	}

	public void setInGame(boolean inGame) {
		this.inGame = inGame;
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

	public Map<UnrealId, List<Long>> getTagGotMillis() {
		return tagGotMillis;
	}

	public Map<UnrealId, List<Long>> getTagPassedMillis() {
		return tagPassedMillis;
	}
	
	public boolean isHasTag() {
		return hasTag;
	}
	
	public void setHasTag(boolean hasTag) {
		this.hasTag = hasTag;
	}

	public void tagged(UnrealId taggedBy) {
		if (hasTag) return;
		if (taggedBy == null) taggedBy = UT2004TagServer.SERVER_UNREAL_ID;
		lastTaggedBy = taggedBy;
		hasTag = true;
		if (taggedBy != UT2004TagServer.SERVER_UNREAL_ID) ++score;
		tagGotMillis.get(taggedBy).add(System.currentTimeMillis());
	}
	
	public void tagPassed(UnrealId toWhom) {
		if (!hasTag) return;
		if (toWhom == null) toWhom = UT2004TagServer.SERVER_UNREAL_ID;
		hasTag = false;
		long taggedTime = System.currentTimeMillis();
		tagPassedMillis.get(toWhom).add(taggedTime);		
		if (lastTaggedBy != null && lastTaggedBy != UT2004TagServer.SERVER_UNREAL_ID) {
			// count how long we have got the tag
			totalTagTimeMillis += taggedTime - tagGotMillis.get(lastTaggedBy).get(tagGotMillis.get(lastTaggedBy).size()-1);
		}
	}

	public void reset() {
		initTime = System.currentTimeMillis();
		finishTime = -1;
		tagGotMillis.clear();
		tagPassedMillis.clear();
		inGame = false;
		hasTag = false;
		score = 0;
		immunity = null;
		totalTagTimeMillis = 0;
		lastTaggedBy = null;
	}

	public PLAYER_CONTAINER getPlayer() {
		return player;
	}

	public void setPlayer(PLAYER_CONTAINER player) {
		this.player = player;
	}

}
