package cz.cuni.amis.pogamut.ut2004.tournament.dm.table.report;

public class DMTablePlayerResult {

	public final String player;
	
	public int frags = 0;
	
	public int deaths = 0;
	
	public int wins = 0;
	
	public int draws = 0;
	
	public int loses = 0;
	
	public int exceptions = 0;
	
	public int position = -1;
	
	public DMTablePlayerResult(String player) {
		this.player = player;
	}
	
	public void result(DMMatchResult result) {
		if (result.isException(player)) {
			// I HAVE FAILED...
			exceptions += 1;
			return;
		}
		if (result.isException()) {
			// THE OTHER BOT EXCEPTION
			++wins;
			return;
		}
		
		if (result.player1.equals(player)) {
			// I AM PLAYER 1
			frags += result.score1;
			deaths += result.score2;
			if (result.score1 == result.score2) {
				++draws;
			} else 
			if (result.score1 > result.score2) {
				++wins;
			} else {
				++loses;
			}
		} else
		if (result.player2.equals(player)) {
			// I AM PLAYER 2
			frags += result.score2;
			deaths += result.score1;
			if (result.score1 == result.score2) {
				++draws;
			} else 
			if (result.score1 > result.score2) {
				++loses;
			} else {
				++wins;
			}
		} else {
			// ??? HUH
		}
	}
	
	@Override
	public String toString() {
		return "" + position + ". " + player + " [W" + wins + ":D" + draws + ";L" + loses + ":E" + exceptions + "]";
	}
	
}
