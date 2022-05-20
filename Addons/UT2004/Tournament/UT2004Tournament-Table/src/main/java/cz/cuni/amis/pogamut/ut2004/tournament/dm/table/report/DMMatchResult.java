package cz.cuni.amis.pogamut.ut2004.tournament.dm.table.report;

public class DMMatchResult {

	public String player1;
	public String player2;
	
	public int score1;
	public int score2;
	
	public boolean player1Exception;
	public boolean player2Exception;
	
	public String player1ExceptionTrace;
	public String player2ExceptionTrace;
	
	public DMMatchResult(String player1, String player2, int score1, int score2, boolean bot1LogicEx, boolean bot2LogicEx, String bot1Ex, String bot2Ex) {
		super();
		this.player1 = player1;
		this.player2 = player2;
		this.score1 = score1;
		this.score2 = score2;
		this.player1Exception = bot1LogicEx;
		this.player2Exception = bot2LogicEx;
		this.player1ExceptionTrace = bot1Ex;
		this.player2ExceptionTrace = bot2Ex;
	}
	
	public boolean isException() {
		return player1Exception || player2Exception;
	}
	
	public boolean isException(String player) {
		if (player1.equals(player) && player1Exception) return true;
		if (player2.equals(player) && player2Exception) return true;
		return false;
	}
	
	public boolean isWinPlayer1() {
		return isWin(player1);
	}
	
	public boolean isWinPlayer2() {
		return isWin(player2);
	}
	
	public boolean isWin(String player) {
		if (isException() && !isException(player)) return true;
		if (player1.equals(player) && score1 > score2) return true;
		if (player2.equals(player) && score1 < score2) return true;
		return false;
	}
	
	public int getScore(String player) {
		if (player1.equals(player)) return score1;
		if (player2.equals(player)) return score2;
		return 0;
	}
	
	public String getException(String player) {
		if (player1.equals(player)) return player1ExceptionTrace;
		if (player2.equals(player)) return player2ExceptionTrace;
		return "";
	}
	
	public String getResult() {
		if (isException()) return "EXCEPTION";
		if (isWinPlayer1()) return player1;
		if (isWinPlayer2()) return player2;
		return "DRAW";
	}
	
	public String toString() {
		return player1 + "-vs-" + player2 + "[" + (player1Exception ? "E" : score1) + ":" + (player2Exception ? "E" : score2) + "]";
	}


	
}
