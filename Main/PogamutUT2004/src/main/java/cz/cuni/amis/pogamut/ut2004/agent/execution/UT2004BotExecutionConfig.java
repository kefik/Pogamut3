package cz.cuni.amis.pogamut.ut2004.agent.execution;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cz.cuni.amis.pogamut.ut2004.utils.PogamutUT2004Property;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;

public class UT2004BotExecutionConfig {
	
	/**
	 * Unique id of this bot, used for reference inside tournament results.
	 * <p><p>
	 * DOES NOT MEAN THAT THE EXECUTED BOT WILL HAVE THIS ID IN UT2004!
	 */
	private IToken botId;
		
	/**
	 * Path to the directory that contains runnable jar file with the bot.
	 */
	private String pathToBotJar = null;
	
	/**
	 * Whether the StdErr of the bot execution should be redirected to log (== true, default) or sunk (== false).
	 */
	private boolean redirectStdErr = true;
	
	/**
	 * Whether the StdOut of the bot execution should be redirected to log (== true, default) or sunk (== false).
	 */
	private boolean redirectStdOut = true;

	/**
	 * {@link UT2004BotExecutionConfig#pathToBotJar} as a {@link File}.
	 */
	private File fileToJar;
	
	/**
	 * Directory from which to start the jar; {@link #fileToJar} parent directory used if not specified.
	 */
	private File execDir;
	
	/**
	 * Parameters to be passed to the bot process via "-D" Java switch.
	 */
	private Map<String, Object> parameters = new HashMap<String, Object>();

	/**
	 * Returns ID of this bot configuration. This ID will be used for storing result of the tournament for this bot.
	 * <p><p>
	 * DOES NOT MEAN THAT THE EXECUTED BOT WILL HAVE THIS ID IN UT2004!
	 * 
	 * @return
	 */
	public IToken getBotId() {
		return botId;
	}
	
	/**
	 * Parameter-less constructor.
	 */
	public UT2004BotExecutionConfig() {		
	}

	/**
	 * Copy-constructor;
	 * @param value
	 */
	public UT2004BotExecutionConfig(IToken botId, String pathToBotJar, boolean redirectStdOut, boolean redirectStdErr) {
		this.botId = botId;
		this.setPathToBotJar(pathToBotJar);
		this.redirectStdOut = redirectStdOut;
		this.redirectStdErr = redirectStdErr;		
	}

	/**
	 * Sets ID of this bot configuration. This ID will be used for storing result of the tournament for this bot.
	 * <p><p>
	 * DOES NOT MEAN THAT THE EXECUTED BOT WILL HAVE THIS ID IN UT2004!
	 * 
	 * @param botId
	 */
	public UT2004BotExecutionConfig setBotId(String botId) {
		NullCheck.check(botId, "botId");
		this.botId = Tokens.get(botId);
		return this;
	}
	
	/**
	 * Sets ID of this bot configuration. This ID will be used for storing result of the tournament for this bot.
	 * <p><p>
	 * DOES NOT MEAN THAT THE EXECUTED BOT WILL HAVE THIS ID IN UT2004!
	 * 
	 * @param botId
	 */
	public UT2004BotExecutionConfig setBotId(IToken botId) {
		NullCheck.check(botId, "botId");
		this.botId = botId;
		return this;
	}
	
	
	/**
	 * Directory from which to start the jar; {@link #fileToJar} parent directory used if not specified.
	 * @return
	 */
	public File getExecDir() {
		return execDir;
	}

	/**
	 * Directory from which to start the jar; {@link #fileToJar} parent directory used if not specified.
	 * @param execDir
	 */
	public void setExecDir(File execDir) {
		this.execDir = execDir;
	}
	
	/**
	 * Checks if {@link #execDir} exists and is directory. 
	 * @return
	 */
	public boolean isExecDirExists() {
		return execDir != null && execDir.exists() && execDir.isDirectory();
	}

	/**
	 * Path to the runnable jar file contining the bot to be run.
	 * @return
	 */
	public String getPathToBotJar() {
		return pathToBotJar;
	}

	/** 
	 * Sets the path to jar-file of the bot.
	 * @param botDirPath
	 */
	public UT2004BotExecutionConfig setPathToBotJar(String pathToBotJar) {
		if (pathToBotJar != null) {
			this.fileToJar = new File(pathToBotJar);
		} else {
			this.fileToJar = null;
		}
		this.pathToBotJar = pathToBotJar;
		return this;
	}
	
	/**
	 * Whether the jar specified by this config exists.
	 * @return
	 */
	public boolean isBotJarExist() {
		if (this.pathToBotJar == null) return false;
		File file = getJarFile();
		return file.exists() && file.isFile(); 
	}

	/**
	 * Returns path to jar as a file.
	 * @return
	 */
	public File getJarFile() {
		return fileToJar;
	}
	
	/**
	 * Whether the StdErr of the bot execution should be redirected to log (== true, default) or sunk (== false).
	 * @return
	 */
	public boolean isRedirectStdErr() {
		return redirectStdErr;
	}

	/**
	 * Sets whether the StdErr of the bot execution should be redirected to log (== true, default) or sunk (== false).
	 * @param redirectStdErr
	 */
	public UT2004BotExecutionConfig setRedirectStdErr(boolean redirectStdErr) {
		this.redirectStdErr = redirectStdErr;
		return this;
	}

	/**
	 * Whether the StdOut of the bot execution should be redirected to log (== true, default) or sunk (== false).
	 * @return
	 */
	public boolean isRedirectStdOut() {
		return redirectStdOut;
	}

	/**
	 * Sets whether the StdOut of the bot execution should be redirected to log (== true, default) or sunk (== false).
	 * @param redirectStdOut
	 */
	public UT2004BotExecutionConfig setRedirectStdOut(boolean redirectStdOut) {
		this.redirectStdOut = redirectStdOut;
		return this;
	}
	
	/**
	 * Adds parameter to be passed onto the command-line executing the bot via Java "-D" switch.
	 * 
	 * @param key only alpha-num chars and '_', '-', '.' chars allowed
	 * @param value {@link Object#toString()} will be used to serialize the parameter into the command-line, parameter MUST NOT contain '"'
	 * @return previously stored value under 'key'.
	 */
	public Object setParameter(String key, Object value) {
		return this.parameters.put(key, value.toString());
	}
	
	/**
	 * Removes parameter to be passed onto the command-line executing the bot via Java "-D" switch.
	 * 
	 * @param key
	 * @return
	 */
	public Object removeParameter(String key) {
		Object result = this.parameters.remove(key);		
		return result;
	}
	
	public Map<String, Object> getParameters() {
		return this.parameters;
	}
			
	public void setParameters(Map<String, ?> parameters) {
		this.parameters = (Map<String, Object>) parameters;
	}
	
	/**
	 * Returns current (custom) name for the bot within the game or NULL (default one will be used). 
	 * @return
	 */
	public String getBotName() {
		return (String)parameters.get(PogamutUT2004Property.POGAMUT_UT2004_BOT_INIT_NAME.getKey());
	}
	
	/**
	 * Name to give the bot within the game. 
	 * @param name
	 * @return
	 */
	public UT2004BotExecutionConfig setBotName(String name) {		
		if (name == null) {
			removeParameter(PogamutUT2004Property.POGAMUT_UT2004_BOT_INIT_NAME.getKey());
		} else {
			setParameter(PogamutUT2004Property.POGAMUT_UT2004_BOT_INIT_OVERRIDE_PARAMS.getKey(), true);
			setParameter(PogamutUT2004Property.POGAMUT_UT2004_BOT_INIT_NAME.getKey(), name);
		}
		return this;
	}
	
	/**
	 * Returns current bot team or NULL.
	 * @return
	 */
	public Integer getBotTeam() {
		Object teamObj = parameters.get(PogamutUT2004Property.POGAMUT_UT2004_BOT_INIT_TEAM.getKey());
		if (teamObj == null) return 0;
		String team = teamObj.toString();
		return Integer.parseInt(team);
	}
	
	/**
	 * Sets team number the bot should play for.
	 * @param teamNumber
	 */
	public UT2004BotExecutionConfig setBotTeam(Integer team) {	
		if (team == null) {
			removeParameter(PogamutUT2004Property.POGAMUT_UT2004_BOT_INIT_TEAM.getKey());
		} else {
			setParameter(PogamutUT2004Property.POGAMUT_UT2004_BOT_INIT_OVERRIDE_PARAMS.getKey(), true);
			setParameter(PogamutUT2004Property.POGAMUT_UT2004_BOT_INIT_TEAM.getKey(), team);
		}
		return this;
	}
	
	/**
	 * Returns current (custom) bot skin or NULL.
	 * @return
	 */
	public String getBotSkin() {
		return (String)parameters.get(PogamutUT2004Property.POGAMUT_UT2004_BOT_INIT_SKIN.getKey());
	}
	
	/**
	 * Sets bot skin to be used. If null, default will be used. 
	 * @param skin
	 * @return
	 */
	public UT2004BotExecutionConfig setBotSkin(String skin) {		
		if (skin == null) {
			removeParameter(PogamutUT2004Property.POGAMUT_UT2004_BOT_INIT_SKIN.getKey());
		} else {
			setParameter(PogamutUT2004Property.POGAMUT_UT2004_BOT_INIT_OVERRIDE_PARAMS.getKey(), true);
			setParameter(PogamutUT2004Property.POGAMUT_UT2004_BOT_INIT_SKIN.getKey(), skin);
		}
		return this;
	}
	
	public Integer getBotSkill() {
		return (Integer)parameters.get(PogamutUT2004Property.POGAMUT_UT2004_BOT_INIT_SKILL.getKey());
	}
	
	public UT2004BotExecutionConfig setBotSkill(Integer skill) {
		if (skill != null) {
			skill = (skill < 0 ? 0 : skill > 7 ? 7 : skill);
		}
		if (skill == null) {
			removeParameter(PogamutUT2004Property.POGAMUT_UT2004_BOT_INIT_SKILL.getKey());
		} else {
			setParameter(PogamutUT2004Property.POGAMUT_UT2004_BOT_INIT_OVERRIDE_PARAMS.getKey(), true);
			setParameter(PogamutUT2004Property.POGAMUT_UT2004_BOT_INIT_SKILL.getKey(), skill);
		}
		return this;
	}
	
	@Override
	public String toString() {
		return "UT2004BotExecutionConfig[botId=" + botId.getToken() + ", jar=" + pathToBotJar + "]";
	}

}
