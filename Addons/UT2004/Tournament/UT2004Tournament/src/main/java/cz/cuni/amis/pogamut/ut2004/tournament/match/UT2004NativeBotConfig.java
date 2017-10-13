package cz.cuni.amis.pogamut.ut2004.tournament.match;

import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;

/**
 * Describes configuratioin of UT2004 native bot.
 * 
 * @author Jimmy
 */
public class UT2004NativeBotConfig implements IUT2004BotConfig {

	/**
	 * Unique id of this bot, used for reference inside tournament results.
	 * <p><p>
	 * DOES NOT MEAN THAT THE EXECUTED BOT WILL HAVE THIS ID IN UT2004!
	 */
	private IToken botId;
	
	/**
	 * Number of the team the bot should be in. 
	 */
	private int teamNumber = 255;
	
	/**
	 * Skill level of the native bot. From 1 to 7 (best). Default: 4.
	 */
	private int skillLevel = 4;
	
	/** Class representing this bot in game */
	private String botClass = "JakobM";

	public UT2004NativeBotConfig() {
	}

	/**
	 * Copy-constructor.
	 * @param value
	 */
	public UT2004NativeBotConfig(UT2004NativeBotConfig value) {
		this.botId = value.getBotId();
		this.teamNumber = value.getBotTeam();
		this.skillLevel = value.getBotSkill();
	}

	@Override
	public IToken getBotId() {
		return botId;
	}

	/**
	 * Sets ID of this bot configuration. This ID will be used for storing result of the tournament for this bot.
	 * <p><p>
	 * DOES NOT MEAN THAT THE EXECUTED BOT WILL HAVE THIS ID IN UT2004!
	 * 
	 * @param botId
	 */
	public UT2004NativeBotConfig setBotId(IToken botId) {
		this.botId = botId;
		return this;
	}
	
	/**
	 * Sets ID of this bot configuration. This ID will be used for storing result of the tournament for this bot.
	 * <p><p>
	 * DOES NOT MEAN THAT THE EXECUTED BOT WILL HAVE THIS ID IN UT2004!
	 * 
	 * @param botId
	 */
	public UT2004NativeBotConfig setBotId(String botId) {
		this.botId = Tokens.get(botId);
		return this;
	}

	@Override
	public Integer getBotTeam() {
		return teamNumber;
	}

	/**
	 * Sets team number of the team the bot should join.
	 * @param teamNumber
	 * @return
	 */
	public UT2004NativeBotConfig setTeamNumber(int teamNumber) {
		this.teamNumber = teamNumber;
		return this;
	}

	/**
	 * Skill level of the native bot.
	 * @return
	 */
	@Override
	public Integer getBotSkill() {
		return skillLevel;
	}

	/**
	 * Sets desired skill level that the bot should have. From 1 to 7 (best). Default: 4.
	 * @param skillLevel
	 * @return
	 */
	public UT2004NativeBotConfig setDesiredSkill(int skillLevel) {
		this.skillLevel = skillLevel;
		return this;
	}
	
	@Override
	public String getBotSkin() {
		return null; // we cannot configure skin for nativebot, or can we?
	}

	public String getBotClass() {
		return botClass;
	}
	
	/** not used right now */
	public void setBotClass(String botClass) {
		this.botClass = botClass;		
	}

}
