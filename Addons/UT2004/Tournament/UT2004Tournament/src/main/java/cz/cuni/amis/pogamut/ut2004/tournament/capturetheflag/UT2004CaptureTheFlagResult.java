package cz.cuni.amis.pogamut.ut2004.tournament.capturetheflag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cuni.amis.pogamut.ut2004.analyzer.stats.UT2004AnalyzerObsStats;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerScore;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.TeamScore;
import cz.cuni.amis.pogamut.ut2004.tournament.match.result.UT2004TeamMatchResult;
import cz.cuni.amis.utils.maps.HashMapMap;
import cz.cuni.amis.utils.token.IToken;

public class UT2004CaptureTheFlagResult extends UT2004TeamMatchResult {
	
	private List<IToken> bots = new ArrayList<IToken>();
	
	private List<IToken> nativeBots = new ArrayList<IToken>();
	
	private List<IToken> humans = new ArrayList<IToken>();
	
	private Map<IToken, String> names = new HashMap<IToken, String>();
	
	private Map<IToken, PlayerScore> finalScores = new HashMap<IToken, PlayerScore>();
	
	private Map<IToken, Integer> totalKills = new HashMap<IToken, Integer>();
	
	private Map<IToken, Integer> wasKilled = new HashMap<IToken, Integer>();
	
	private HashMapMap<IToken, IToken, Integer> killCounts = new HashMapMap<IToken, IToken, Integer>(); 
	
	private Map<IToken, Integer> suicides = new HashMap<IToken, Integer>();
	
	private Map<IToken, UT2004AnalyzerObsStats> botObservers = new HashMap<IToken, UT2004AnalyzerObsStats>();

	private Map<Integer, TeamScore> teamScores = new HashMap<Integer, TeamScore>();
	
	/**
	 * When the match has ended (in seconds);
	 */
	public double matchTimeEnd;

	@Override
	public String toString() {
		return "UT2004CaptureTheFlagResult[" + (isDraw() ? "DRAW" : ("winnerTeam=" + getWinnerTeam()) ) + "]";
	}
	
	public UT2004CaptureTheFlagResult() {
		super();
	}
	
	public UT2004CaptureTheFlagResult(int winnerTeam) {
		super(winnerTeam);
	}

	/**
	 * Returns list with custom bots (run by Pogamut platform).
	 * @return
	 */
	public List<IToken> getBots() {
		return bots;
	}

	/**
	 * List with custom bots (run by Pogamut platform).
	 * @param bots
	 */
	public void setBots(List<IToken> bots) {
		this.bots = bots;
	}

	/**
	 * Returns list with native bots (bots from UT2004 itself).
	 * @return
	 */
	public List<IToken> getNativeBots() {
		return nativeBots;
	}

	/**
	 * List with native bots (bots from UT2004 itself).
	 * @param nativeBots
	 */
	public void setNativeBots(List<IToken> nativeBots) {
		this.nativeBots = nativeBots;
	}
	
	/**
	 * Returns list with humans.
	 * @return
	 */
	public List<IToken> getHumans() {
		return humans;
	}

	/**
	 * List with humans.
	 * @param HumanBots
	 */
	public void setHumans(List<IToken> humans) {
		this.humans = humans;
	}
	
	/**
	 * Returns mapping ID to Name the bot/human had in the game.
	 * @return
	 */
	public Map<IToken, String> getNames() {
		return names;
	}

	/**
	 * Sets mapping ID to Name the bot/human had in the game.
	 * @param names
	 */
	public void setNames(Map<IToken, String> names) {
		this.names = names;
	}

	/**
	 * Returns list of all bot (custom + native) ids.
	 * @return
	 */
	public List<IToken> getAllBots() {
		List<IToken> all = new ArrayList<IToken>(this.bots);
		all.addAll(this.nativeBots);
		all.addAll(this.humans);
		return all;
	}

	/**
	 * When the match has ended (in seconds). I.e., how long was the match.
	 */
	public double getMatchTimeEnd() {
		return matchTimeEnd;
	}

	/**
	 * When the match has ended (in seconds). I.e., how long was the match.
	 */
	public void setMatchTimeEnd(double matchTimeEnd) {
		this.matchTimeEnd = matchTimeEnd;
	}

	/**
	 * Final scores of bots.
	 * @return
	 */
	public Map<IToken, PlayerScore> getFinalScores() {
		return finalScores;
	}

	/**
	 * Final scores of bots.
	 * @return
	 */
	public void setFinalScores(Map<IToken, PlayerScore> finalScores) {
		this.finalScores = finalScores;
	}

	/**
	 * Who -&gt; killed Whom -&gt; How many times, i.e., map.get(killerId).get(victimId) == how many time killer killed the victim.
	 * @return
	 */
	public HashMapMap<IToken, IToken, Integer> getKillCounts() {
		return killCounts;
	}

	/**
	 * Who -&gt; killed Whom -&gt; How many times, i.e., map.get(killerId).get(victimId) == how many time killer killed the victim.
	 * @return
	 */
	public void setKillCounts(HashMapMap<IToken, IToken, Integer> killCounts) {
		this.killCounts = killCounts;
	}
	
	
	/**
	 * How many times one bot killed another bot.
	 * @return
	 */
	public Map<IToken, Integer> getTotalKills() {
		return totalKills;
	}

	/**
	 * How many times one bot killed another bot.
	 * @return
	 */
	public void setTotalKills(Map<IToken, Integer> totalKills) {
		this.totalKills = totalKills;
	}
	
	/**
	 * How many times some bot was killed by ANOTHER bot (== without suicides).
	 * @return
	 */
	public Map<IToken, Integer> getWasKilled() {
		return wasKilled;
	}

	/**
	 * How many times some bot was killed by ANOTHER bot (== without suicides).
	 * @return
	 */
	public void setWasKilled(Map<IToken, Integer> wasKilled) {
		this.wasKilled = wasKilled;
	}

	/**
	 * How many times the bot (key == botId) has commit suicide.
	 * @return
	 */
	public Map<IToken, Integer> getSuicides() {
		return suicides;
	}

	/**
	 * How many times the bot (key == botId) has commit suicide.
	 * @param suicides
	 */
	public void setSuicides(Map<IToken, Integer> suicides) {
		this.suicides = suicides;
	}

	/**
	 * Map with observers (custom bots only!) containing detailed statistics about respective bots.
	 * @return
	 */
	public Map<IToken, UT2004AnalyzerObsStats> getBotObservers() {
		return botObservers;
	}

	/**
	 * Map with observers (custom bots only!) containing detailed statistics about respective bots.
	 * @param botObservers
	 */
	public void setBotObservers(Map<IToken, UT2004AnalyzerObsStats> botObservers) {
		this.botObservers = botObservers;
	}

	/**
	 * Returns scores of respective teams that were in the game.
	 * @return
	 */
	public Map<Integer, TeamScore> getTeamScores() {
		return teamScores;
	}

	/**
	 * Sets team scores.
	 * @param teamScores
	 */
	public void setTeamScores(Map<Integer, TeamScore> teamScores) {
		this.teamScores = teamScores;
	}

}
