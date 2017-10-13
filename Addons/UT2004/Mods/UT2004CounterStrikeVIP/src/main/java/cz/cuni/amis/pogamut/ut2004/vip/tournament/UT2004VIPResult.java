package cz.cuni.amis.pogamut.ut2004.vip.tournament;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.analyzer.stats.UT2004AnalyzerObsStats;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerMessage;
import cz.cuni.amis.pogamut.ut2004.tournament.match.result.UT2004MatchResult;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.CSBotTeam;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.VIPGameResult;
import cz.cuni.amis.pogamut.ut2004.vip.server.CSBotRecord;
import cz.cuni.amis.pogamut.ut2004.vip.server.CSTeamsRecord;
import cz.cuni.amis.utils.token.IToken;

public class UT2004VIPResult extends UT2004MatchResult {

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
	 * Full scores for respective bots.
	 */
	private Map<IToken, CSBotRecord<PlayerMessage>> scoreDetails = new HashMap<IToken, CSBotRecord<PlayerMessage>>();
	
	/**
	 * Full scores of teams.
	 */
	private CSTeamsRecord teamsRecord;
	
	/**
	 * Overall result of the game.
	 */
	private VIPGameResult gameResult = null;
	
	/**
	 * Winners ~ bots of the team that has won.
	 */
	private List<IToken> winnerBotIds = new ArrayList<IToken>();
	
	/**
	 * How long was the match (in seconds);
	 */
	private double matchTime;	

	public UT2004VIPResult() {
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

	public List<IToken> getWinnerBotIds() {
		return winnerBotIds;
	}

	public void setWinnerBotIds(List<IToken> winners) {
		this.winnerBotIds = winners;
	}
	
	public CSTeamsRecord getTeamsRecord() {
		return teamsRecord;
	}

	public void setTeamsRecord(CSTeamsRecord teamsRecord) {
		this.teamsRecord = teamsRecord;
	}

	public VIPGameResult getGameResult() {
		return gameResult;
	}

	public void setGameResult(VIPGameResult gameResult) {
		this.gameResult = gameResult;
	}

	public Map<IToken, CSBotRecord<PlayerMessage>> getScoreDetails() {
		return scoreDetails;
	}

	public void setScoreDetails(Map<IToken, CSBotRecord<PlayerMessage>> scoreDetails) {
		this.scoreDetails = scoreDetails;
	}

	public double getMatchTime() {
		return matchTime;
	}

	public void setMatchTime(double matchTime) {
		this.matchTime = matchTime;
	}

	public int getWinnerScore() {
		return teamsRecord.getScore(teamsRecord.getResult().team);
	}

}
