package cz.cuni.amis.pogamut.ut2004.tournament.match.result;

/**
 * Represents the result of the match (very limited). Just stating whether the match was fought by teams/individuals
 * and the winner.
 * 
 * @author Jimmy
 */
public abstract class UT2004MatchResult {

	private boolean individual;

	public UT2004MatchResult(boolean individual) {
		this.individual = individual;
	}
	
	public boolean isIndividual() {
		return individual;
	}

}
