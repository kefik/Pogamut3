package cz.cuni.amis.pogamut.ut2004.tournament.tdm.table.report.summary;

import cz.cuni.amis.utils.Const;

public class TDMMatchesTableTeamResult {

	public final String team;
	
	public int frags = 0;
	
	public int deaths = 0;
	
	public int wins = 0;
	
	public int draws = 0;
	
	public int loses = 0;
	
	public int exceptions = 0;
	
	public int position = -1;
	
	public String exceptionsStr = "";
	
	public TDMMatchesTableTeamResult(String team) {
		this.team = team;
	}
	
	public void result(TDMMatchesResult result) {
		if (result.team1.equals(team)) {
			// I AM PLAYER 1
			frags += result.frags1;
			deaths += result.deaths1;
			if (result.score1 == result.score2) {
				++draws;
			} else 
			if (result.score1 > result.score2) {
				++wins;
			} else {
				++loses;
			}
			exceptions += result.exceptions1;
			if (result.exceptions1Str != null && !result.exceptions1Str.isEmpty()) {
				if (!exceptionsStr.isEmpty()) exceptionsStr += Const.NEW_LINE;
				exceptionsStr += result.exceptions1Str;	
			}			
		} else
		if (result.team2.equals(team)) {
			// I AM PLAYER 2
			frags += result.frags2;
			deaths += result.deaths2;
			if (result.score1 == result.score2) {
				++draws;
			} else 
			if (result.score1 > result.score2) {
				++loses;
			} else {
				++wins;
			}
			exceptions += result.exceptions2;
			if (result.exceptions2Str != null && !result.exceptions2Str.isEmpty()) {
				if (!exceptionsStr.isEmpty()) exceptionsStr += Const.NEW_LINE;
				exceptionsStr += result.exceptions2Str;	
			}
		} else {
			// ??? HUH
		}
	}
	
	@Override
	public String toString() {
		return "" + position + ". " + team + " [W" + wins + ":D" + draws + ";L" + loses + ":E" + exceptions + "]";
	}
	
}
