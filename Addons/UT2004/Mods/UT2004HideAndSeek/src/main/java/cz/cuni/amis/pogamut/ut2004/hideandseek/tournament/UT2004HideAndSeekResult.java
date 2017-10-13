package cz.cuni.amis.pogamut.ut2004.hideandseek.tournament;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.analyzer.stats.UT2004AnalyzerObsStats;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerMessage;
import cz.cuni.amis.pogamut.ut2004.hideandseek.server.HSBotRecord;
import cz.cuni.amis.pogamut.ut2004.tournament.match.result.UT2004MatchResult;
import cz.cuni.amis.utils.token.IToken;

public class UT2004HideAndSeekResult extends UT2004MatchResult {

	/**
	 * Participants of the match.
	 */
	private List<IToken> bots = new ArrayList<IToken>();
	
	/**
	 * Observers of respecitve bots ~ they contain more infos about the game such as positions, etc...
	 */
	private Map<IToken, UT2004AnalyzerObsStats> botObservers = new HashMap<IToken, UT2004AnalyzerObsStats>();
	
	/**
	 * Bot IDs within the game
	 */
	private Map<IToken, UnrealId> botIds = new HashMap<IToken, UnrealId>();
	
	/**
	 * Winners ~ bots with the highest score.
	 */
	private List<IToken> winners = new ArrayList<IToken>();
	
	/**
	 * Full scores for respective bots.
	 */
	private Map<IToken, HSBotRecord<PlayerMessage>> scoreDetails = new HashMap<IToken, HSBotRecord<PlayerMessage>>();
	
	/**
	 * How long was the match (in seconds);
	 */
	private double matchTime;	

	public UT2004HideAndSeekResult() {
		super(true);
	}

	public List<IToken> getBots() {
		return bots;
	}

	public void setBots(List<IToken> bots) {
		this.bots = bots;
	}

	public Map<IToken, UT2004AnalyzerObsStats> getBotObservers() {
		return botObservers;
	}

	public void setBotObservers(Map<IToken, UT2004AnalyzerObsStats> botObservers) {
		this.botObservers = botObservers;
	}

	public Map<IToken, UnrealId> getBotIds() {
		return botIds;
	}

	public void setBotIds(Map<IToken, UnrealId> botIds) {
		this.botIds = botIds;
	}

	public List<IToken> getWinners() {
		return winners;
	}

	public void setWinners(List<IToken> winners) {
		this.winners = winners;
	}

	public Map<IToken, HSBotRecord<PlayerMessage>> getScoreDetails() {
		return scoreDetails;
	}

	public void setScoreDetails(Map<IToken, HSBotRecord<PlayerMessage>> scoreDetails) {
		this.scoreDetails = scoreDetails;
	}

	public double getMatchTime() {
		return matchTime;
	}

	public void setMatchTime(double matchTime) {
		this.matchTime = matchTime;
	}

	public int getWinnerScore() {
		if (winners == null || winners.size() == 0) return 0;
		return scoreDetails.get(winners.get(0)).getScore();
	}

}
