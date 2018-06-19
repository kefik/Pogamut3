package cz.cuni.amis.pogamut.ut2004.tournament.tdm.table.report.one;

public class TDMOneMatchResult {

	public String team1;
	public String team2;
	
	public int score1;
	public int score2;
	
	public boolean team1Exception;
	public boolean team2Exception;
	
	public String team1ExceptionTrace;
	public String team2ExceptionTrace;
	
	public TDMOneMatchResult(String team1, String team2, int score1, int score2, boolean team1LogicEx, boolean team2LogicEx, String team1Ex, String team2Ex) {
		super();
		this.team1 = team1;
		this.team2 = team2;
		this.score1 = score1;
		this.score2 = score2;
		this.team1Exception = team1LogicEx;
		this.team2Exception = team2LogicEx;
		this.team1ExceptionTrace = team1Ex;
		this.team2ExceptionTrace = team2Ex;
	}
	
	public boolean isException() {
		return team1Exception || team2Exception;
	}
	
	public boolean isException(String player) {
		if (team1.equals(player) && team1Exception) return true;
		if (team2.equals(player) && team2Exception) return true;
		return false;
	}
	
	public boolean isWin(String player) {
		if (isException() && !isException(player)) return true;
		if (team1.equals(player) && score1 > score2) return true;
		if (team2.equals(player) && score1 < score2) return true;
		return false;
	}
	
	public int getScore(String player) {
		if (team1.equals(player)) return score1;
		if (team2.equals(player)) return score2;
		return 0;
	}
	
	public String getException(String player) {
		if (team1.equals(player)) return team1ExceptionTrace;
		if (team2.equals(player)) return team2ExceptionTrace;
		return "";
	}
	
	public String toString() {
		return team1 + "-vs-" + team2 + "[" + (team1Exception ? "E" : score1) + ":" + (team2Exception ? "E" : score2) + "]";
	}


	
}
