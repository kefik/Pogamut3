package cz.cuni.amis.pogamut.ut2004.tournament.match.result;


/**
 * Represents the result of the TEAM match (very limited).
 * and the winner.
 * 
 * @author Jimmy
 */
public class UT2004TeamMatchResult extends UT2004MatchResult {

	private int winnerTeam;
	private boolean draw;

	public UT2004TeamMatchResult() {
		super(false);
	}
	
	public UT2004TeamMatchResult(int winnerTeam) {
		super(false);
		this.winnerTeam = winnerTeam;
	}

	public int getWinnerTeam() {
		return winnerTeam;
	}

	public void setWinnerTeam(int winnerTeam) {
		this.winnerTeam = winnerTeam;
	}
	
	public boolean isDraw() {
		return draw;
	}

	public void setDraw(boolean draw) {
		this.draw = draw;
	}
	
}
