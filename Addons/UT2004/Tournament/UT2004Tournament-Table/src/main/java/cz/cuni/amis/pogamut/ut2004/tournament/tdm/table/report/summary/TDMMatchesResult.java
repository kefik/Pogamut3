package cz.cuni.amis.pogamut.ut2004.tournament.tdm.table.report.summary;

public class TDMMatchesResult {

	public String team1;
	public String team2;
	
	public int score1;
	public int score2;
	
	public int frags1 = 0;
	public int frags2 = 0;
	
	public int deaths1 = 0;
	public int deaths2 = 0;
	
	public int exceptions1;
	public int exceptions2;
	
	public String exceptions1Str;
	public String exceptions2Str;
		
	public TDMMatchesResult(String team1, String team2, int score1, int score2, int frags1, int frags2, int deaths1, int deaths2, int exceptions1, int exceptions2, String exceptions1Str, String exceptions2Str) {
		super();
		this.team1 = team1;
		this.team2 = team2;
		this.score1 = score1;
		this.score2 = score2;
		this.frags1 = frags1;
		this.frags2 = frags2;
		this.deaths1 = deaths1;
		this.deaths2 = deaths2;
		this.exceptions1 = exceptions1;
		this.exceptions2 = exceptions2;
		this.exceptions1Str = exceptions1Str;
		this.exceptions2Str = exceptions2Str;
	}
	
	public boolean isWin(String player) {
		if (team1.equals(player) && score1 > score2) return true;
		if (team2.equals(player) && score1 < score2) return true;
		return false;
	}
	
	public int getScore(String player) {
		if (team1.equals(player)) return score1;
		if (team2.equals(player)) return score2;
		return 0;
	}
	
	public int getFrags(String player) {
		if (team1.equals(player)) return frags1;
		if (team2.equals(player)) return frags2;
		return 0;
	}
	
	public int getDeaths(String player) {
		if (team1.equals(player)) return deaths1;
		if (team2.equals(player)) return deaths2;
		return 0;
	}
	
	public int getExceptions(String player) {
		if (team1.equals(player)) return exceptions1;
		if (team2.equals(player)) return exceptions2;
		return 0;
	}
		
	public String toString() {
		return team1 + "-vs-" + team2 + "[" + score1 + ":" + score2 + "]";
	}
	
}
