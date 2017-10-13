package cz.cuni.amis.pogamut.ut2004.tournament.dm.table.report;

public class DMMatchResult {

	public String player1;
	public String player2;
	
	public int score1;
	public int score2;
	
	public DMMatchResult(String player1, String player2, int score1, int score2) {
		super();
		this.player1 = player1;
		this.player2 = player2;
		this.score1 = score1;
		this.score2 = score2;
	}
	
	public boolean isWin(String player) {
		if (player1.equals(player) && score1 > score2) return true;
		if (player2.equals(player) && score1 < score2) return true;
		return false;
	}
	
	public int getScore(String player) {
		if (player1.equals(player)) return score1;
		if (player2.equals(player)) return score2;
		return 0;
	}
	
	public String toString() {
		return player1 + "-vs-" + player2 + "[" + score1 + ":" + score2 + "]";
	}
	
}
