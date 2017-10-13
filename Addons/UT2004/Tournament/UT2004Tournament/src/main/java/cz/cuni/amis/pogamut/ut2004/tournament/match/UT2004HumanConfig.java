package cz.cuni.amis.pogamut.ut2004.tournament.match;

import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;

/**
 * Describes configuration of a human for player UT2004 match.
 * 
 * @author Jimmy
 */
public class UT2004HumanConfig implements IUT2004HumanConfig {

	/**
	 * Unique id of this human, used for reference inside tournament results.
	 */
	private IToken humanId;
	
	/**
	 * Number of the team the human should be in. 
	 */
	private int teamNumber = 255;
	
	public UT2004HumanConfig() {
	}

	/**
	 * Copy-constructor.
	 * @param value
	 */
	public UT2004HumanConfig(UT2004HumanConfig value) {
		this.humanId = value.getHumanId();
		this.teamNumber = value.getTeamNumber();
	}

	@Override
	public IToken getHumanId() {
		return humanId;
	}

	/**
	 * Sets ID of this human configuration. This ID will be used for storing result of the tournament for this human.
	 * 
	 * @param botId
	 */
	public UT2004HumanConfig setHumanId(IToken botId) {
		this.humanId = botId;
		return this;
	}
	
	/**
	 * Sets ID of this bot configuration. This ID will be used for storing result of the tournament for this bot.
	 * 
	 * @param humanId
	 */
	public UT2004HumanConfig setHumanId(String humanId) {
		this.humanId = Tokens.get(humanId);
		return this;
	}

	@Override
	public int getTeamNumber() {
		return teamNumber;
	}

	/**
	 * Sets team number of the team the bot should join.
	 * @param teamNumber
	 * @return
	 */
	public UT2004HumanConfig setTeamNumber(int teamNumber) {
		this.teamNumber = teamNumber;
		return this;
	}

}
