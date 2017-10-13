package cz.cuni.amis.pogamut.ut2004.tournament.match;

import cz.cuni.amis.pogamut.ut2004.agent.execution.UT2004BotExecutionConfig;
import cz.cuni.amis.utils.token.IToken;

/**
 * Describes configuration of the custom-bot created using Pogamut platform.
 * @author Jimmy
 */
public class UT2004BotConfig extends UT2004BotExecutionConfig implements IUT2004BotConfig {

	public UT2004BotConfig() {
	}
	
	/**
	 * Copy-constructor.
	 * @param value
	 */
	public UT2004BotConfig(UT2004BotConfig value) {
		super(value.getBotId(), value.getJarFile().getAbsolutePath(), value.isRedirectStdOut(), value.isRedirectStdErr());
		setBotName(value.getBotName());
		setBotTeam(value.getBotTeam());
		setBotSkill(value.getBotSkill());
		setBotSkin(value.getBotSkin());
	}

	/**
	 * Name to give the bot within the game. 
	 * @param name
	 * @return
	 */
	@Override
	public UT2004BotConfig setBotName(String name) {
		super.setBotName(name);
		return this;
	}
	
	/**
	 * Sets team number the bot should play for.
	 * @param teamNumber
	 */
	@Override
	public UT2004BotConfig setBotTeam(Integer teamNumber) {
		super.setBotTeam(teamNumber);
		return this;
	}
	
	/** 
	 * Sets desired skill level for the bot (0 ... NOOB - 7 ... GODLIKE), if negative, default will be used. 
	 * @param desiredSkill
	 * @return
	 */
	public UT2004BotConfig setBotSkill(Integer desiredSkill) {
		super.setBotSkill(desiredSkill);
		return this;
	}
	
	/**
	 * Set desired (custom) bot skin.
	 * @param skin
	 * @return
	 */
	public UT2004BotConfig setBotSkin(String skin) {
		super.setBotSkin(skin);
		return this;
	}
		
	
	@Override
	public UT2004BotConfig setPathToBotJar(String pathToBotJar) {
		super.setPathToBotJar(pathToBotJar);
		return this;
	}
	
	@Override
	public UT2004BotConfig setRedirectStdErr(boolean redirectStdErr) {
		super.setRedirectStdErr(redirectStdErr);
		return this;
	}
	
	@Override
	public UT2004BotConfig setRedirectStdOut(boolean redirectStdOut) {
		super.setRedirectStdOut(redirectStdOut);
		return this;
	}	

	@Override
	public String toString() {
		return "UT2004BotConfig[botId=" + getBotId().getToken() + ", team=" + getBotTeam() + ", jar=" + getPathToBotJar() + "]";
	}
}
