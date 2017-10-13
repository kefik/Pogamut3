package cz.cuni.amis.pogamut.ut2004.tournament.match;

import cz.cuni.amis.utils.token.IToken;

public interface IUT2004HumanConfig {
	
	/**
	 * Returns ID of this human configuration. This ID will be used for storing result of the tournament for this human.
	 * 
	 * @return
	 */
	public IToken getHumanId();
	
	/**
	 * Returns the team where the human should be in.
	 * @return
	 */
	public int getTeamNumber();

}
