package cz.cuni.amis.pogamut.ut2004.vip.server;

import java.util.Map;

import cz.cuni.amis.pogamut.ut2004.vip.protocol.CSBotTeam;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.VIPGameConfig;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.VIPGameResult;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSTeamScoreChanged;
import cz.cuni.amis.utils.maps.LazyMap;

public class CSTeamsRecord {

	/**
	 * Number of rounds given {@link CSBotTeam} has won.
	 */
	private Map<CSBotTeam, Integer> wins = new LazyMap<CSBotTeam, Integer>() {

		@Override
		protected Integer create(CSBotTeam key) {
			return 0;
		}
		
	};
	
	/**
	 * Total score of respective teams.
	 */
	private Map<CSBotTeam, Integer> scores = new LazyMap<CSBotTeam, Integer>() {

		@Override
		protected Integer create(CSBotTeam key) {
			return 0;
		}
		
	};
		
	private VIPGameConfig config;	
	
	public CSTeamsRecord(VIPGameConfig config) {
		this.config = config;
	}
	
	// =======
	// GETTERS
	// =======
	
	public int getWins(CSBotTeam team) {
		if (team == null) return 0;
		return wins.get(team);
	}
	
	public int getScore(CSBotTeam team) {
		if (team == null) return 0;
		return scores.get(team);
	}
	
	public VIPGameResult getResult() {
		if (scores.get(CSBotTeam.COUNTER_TERRORIST) > scores.get(CSBotTeam.TERRORIST)) return VIPGameResult.COUNTER_TERRORISTS_WIN;
		if (scores.get(CSBotTeam.COUNTER_TERRORIST) < scores.get(CSBotTeam.TERRORIST)) return VIPGameResult.TERRORISTS_WIN;
		return VIPGameResult.DRAW;
	}
	
	// ======
	// EVENTS
	// ======
	
	public void counterTerroristsWin() {
		wins.put(CSBotTeam.COUNTER_TERRORIST, wins.get(CSBotTeam.COUNTER_TERRORIST) + 1);
		scores.put(CSBotTeam.COUNTER_TERRORIST, scores.get(CSBotTeam.COUNTER_TERRORIST) + config.getVipSafeCTsScore());
		scores.put(CSBotTeam.TERRORIST, scores.get(CSBotTeam.TERRORIST) + config.getVipSafeTsScore());
	}
	
	public void terroristsWin() {
		wins.put(CSBotTeam.TERRORIST, wins.get(CSBotTeam.TERRORIST) + 1);
		scores.put(CSBotTeam.COUNTER_TERRORIST, scores.get(CSBotTeam.COUNTER_TERRORIST) + config.getVipKilledCTsScore());
		scores.put(CSBotTeam.TERRORIST, scores.get(CSBotTeam.TERRORIST) + config.getVipKilledTsScore());
	}

	public void clear() {
		wins.clear();
		scores.clear();
	}

	public void teamScoreChanged(CSTeamScoreChanged event) {
		scores.put(CSBotTeam.getFromUT2004Team(event.getUt2004Team()), event.getScore());
	}

	protected void setConfig(VIPGameConfig config) {
		this.config = config;
	}
	
}
