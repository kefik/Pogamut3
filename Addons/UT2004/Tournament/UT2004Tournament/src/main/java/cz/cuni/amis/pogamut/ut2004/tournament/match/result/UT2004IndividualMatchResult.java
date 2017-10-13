package cz.cuni.amis.pogamut.ut2004.tournament.match.result;

import cz.cuni.amis.utils.token.IToken;

/**
 * Represents the result of the match (very limited). Just stating whether the match was fought by teams/individuals
 * and the winner.
 * 
 * @author Jimmy
 */
public class UT2004IndividualMatchResult extends UT2004MatchResult {

	private IToken winnerBot;
	private boolean draw;

	public UT2004IndividualMatchResult() {
		super(true);
	}
	
	public UT2004IndividualMatchResult(IToken winnerBot) {
		super(true);
		this.winnerBot = winnerBot;
	}
	
	public IToken getWinnerBot() {
		return winnerBot;
	}

	public void setWinnerBot(IToken winnerBot) {
		this.winnerBot = winnerBot;
	}

	public boolean isDraw() {
		return draw;
	}

	public void setDraw(boolean draw) {
		this.draw = draw;
	}
	
}
