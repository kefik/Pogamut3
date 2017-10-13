package cz.cuni.amis.pogamut.ut2004.tag.tournament;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.analyzer.stats.UT2004AnalyzerObsStats;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerMessage;
import cz.cuni.amis.pogamut.ut2004.tag.server.BotTagRecord;
import cz.cuni.amis.pogamut.ut2004.tournament.match.result.UT2004MatchResult;
import cz.cuni.amis.utils.maps.HashMapMap;
import cz.cuni.amis.utils.token.IToken;

public class UT2004TagResult extends UT2004MatchResult {
	
	/**
	 * Participants of the match.
	 */
	private List<IToken> bots = new ArrayList<IToken>();
	
	/**
	 * Bot IDs within the game
	 */
	private Map<IToken, UnrealId> botIds = new HashMap<IToken, UnrealId>();
	
	/**
	 * Losers ~ bots with highest tag counts.
	 */
	private List<IToken> losers = new ArrayList<IToken>();
	
	/**
	 * Full scores for respective bots.
	 */
	private Map<IToken, BotTagRecord<PlayerMessage>> scores = new HashMap<IToken, BotTagRecord<PlayerMessage>>();

	/**
	 * How many times was some bot "tagged".
	 */
	private Map<IToken, Integer> tagCounts = new HashMap<IToken, Integer>();
	
	/**
	 * How many times some bot has tagged other bots.
	 */
	private Map<IToken, Integer> tagOthersCounts = new HashMap<IToken, Integer>();
	
	/**
	 * How many times some bot (first-key) has passed the tag to other bot (second-key).
	 */
	private HashMapMap<IToken, IToken, Integer> tagPassedCounts = new HashMapMap<IToken, IToken, Integer>(); 
	
	/**
	 * Observers of respecitve bots ~ they contain more infos about the game such as positions, etc...
	 */
	private Map<IToken, UT2004AnalyzerObsStats> botObservers = new HashMap<IToken, UT2004AnalyzerObsStats>();

	/**
	 * How long was the match (in seconds);
	 */
	private double matchTime;	

	@Override
	public String toString() {
		return "UT2004TagGameResult[#losers=" + losers.size() + "]";
	}
	
	public UT2004TagResult() {
		super(true);
	}
	
	/**
	 * Whether there is more than 1 loser.
	 */
	public boolean isDraw() {
		return losers.size() > 1;
	}

	/**
	 * Participants of the match.
	 */
	public List<IToken> getBots() {
		return bots;
	}

	/**
	 * Participants of the match.
	 */
	public void setBots(List<IToken> bots) {
		this.bots = bots;
	}

	/**
	 * Bot IDs within the game
	 */
	public Map<IToken, UnrealId> getBotIds() {
		return botIds;
	}

	/**
	 * Bot IDs within the game
	 */
	public void setBotIds(Map<IToken, UnrealId> botIds) {
		this.botIds = botIds;
	}

	/**
	 * Losers ~ bots with highest tag counts.
	 */
	public List<IToken> getLosers() {
		return losers;
	}

	/**
	 * Losers ~ bots with highest tag counts.
	 */
	public void setLosers(List<IToken> losers) {
		this.losers = losers;
	}

	/**
	 * Full scores for respective bots.
	 */
	public Map<IToken, BotTagRecord<PlayerMessage>> getScores() {
		return scores;
	}

	/**
	 * Full scores for respective bots.
	 */
	public void setScores(Map<IToken, BotTagRecord<PlayerMessage>> scores) {
		this.scores = scores;
	}

	/**
	 * How many times was some bot "tagged".
	 */
	public Map<IToken, Integer> getTagCounts() {
		return tagCounts;
	}

	/**
	 * How many times was some bot "tagged".
	 */
	public void setTagCounts(Map<IToken, Integer> tagCounts) {
		this.tagCounts = tagCounts;
	}
	
	/**
	 * How many times some bot has tagged other bots.
	 */
	public Map<IToken, Integer> getTagOthersCounts() {
		return tagOthersCounts;
	}

	/**
	 * How many times some bot has tagged other bots.
	 */
	public void setTagOthersCounts(Map<IToken, Integer> tagOthersCounts) {
		this.tagOthersCounts = tagOthersCounts;
	}


	/**
	 * How many times some bot (first-key) has passed the tag to other bot (second-key).
	 */
	public HashMapMap<IToken, IToken, Integer> getTagPassedCounts() {
		return tagPassedCounts;
	}

	/**
	 * How many times some bot (first-key) has passed the tag to other bot (second-key).
	 */
	public void setTagPassedCounts(HashMapMap<IToken, IToken, Integer> tagPassedCounts) {
		this.tagPassedCounts = tagPassedCounts;
	}

	/**
	 * Observers of respecitve bots ~ they contain more infos about the game such as positions, etc...
	 */
	public Map<IToken, UT2004AnalyzerObsStats> getBotObservers() {
		return botObservers;
	}

	/**
	 * Observers of respecitve bots ~ they contain more infos about the game such as positions, etc...
	 */
	public void setBotObservers(Map<IToken, UT2004AnalyzerObsStats> botObservers) {
		this.botObservers = botObservers;
	}

	/**
	 * When the match has ended (in seconds);
	 */
	public double getMatchTime() {
		return matchTime;
	}

	/**
	 * When the match has ended (in seconds);
	 */
	public void setMatchTime(double matchTime) {
		this.matchTime = matchTime;
	}

	/**
	 * How many times the loser(s) had the tag.
	 * @return
	 */
	public int getLoserTagCount() {
		if (losers.size() == 0) return 0;
		return tagCounts.get(losers.get(0));
	}
		
}
