package cz.cuni.amis.pogamut.ut2004.tournament.deathmatch;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;

import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.ut2004.agent.execution.UT2004BotExecutionConfig;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004BotConfig;
import cz.cuni.amis.pogamut.ut2004.utils.UCCWrapper;
import cz.cuni.amis.pogamut.ut2004.utils.UCCWrapperConf;
import cz.cuni.amis.utils.ExceptionToString;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Performs 1v1 death-match for two Pogamut bots (no native bots here).
 * <p><p>
 * You may run it inside thread by invoking new Thread(ut2004DeathMatch1v1).start() or use with {@link ThreadPoolExecutor} as it
 * is implementing {@link Callable}.
 * <p><p>
 * But you will usually want just to execute it using {@link UT2004DeathMatch1v1#call()}.
 * <p><p>
 * Encapsulates classes: {@link UT2004DeathMatch}, {@link UT2004DeathMatchConfig}, {@link UT2004BotConfig} and {@link UCCWrapperConf}.
 * <p><p>
 * If you want to know how all instanes of {@link UT2004DeathMatch} configuration are setup, see {@link UT2004DeathMatch1v1#configure1Vs1()}
 * code.
 * 
 * @author Jimmy
 */
public class UT2004DeathMatch1v1 implements Callable<UT2004DeathMatchResult>, Runnable {
	
	/**
	 * Used as {@link UT2004DeathMatchConfig#setFragLimit(int)}.
	 */
	private int fragLimit;
	
	/**
	 * Used as {@link UT2004DeathMatchConfig#setTimeLimit(int)}.
	 */
	private int timeLimitInMinutes;
	
	/**
	 * Used as {@link UT2004DeathMatchConfig#setMatchId(String)}.
	 * <p><p>
	 * Example: DMMatch1v1
	 */
	private String matchName;
	
	/**
	 * Used as {@link UT2004BotConfig#setBotId(String)} for the first bot.
	 * <p><p>
	 * Example: Bot1
	 */
	private String bot1Name;
	
	/**
	 * Used as {@link UT2004BotConfig#setDesiredSkill(int)} for the first bot.
	 * <p><p>
	 * Example: 5
	 */
	private Integer bot1Skill;
	
	/**
	 * Used as {@link UT2004BotConfig#setSkin(String)} for the first bot.
	 * <p><p>
	 * Example: HumanFemaleA.NightFemaleA
	 */
	private String bot1Skin;
	
	/**
	 * Used as {@link UT2004BotExecutionConfig#setPathToBotJar(String)} for the first bot.
	 * <p><p>
	 * Example: "bots" + File.separator + "Bot1" + File.separator + "Bot1.jar"
	 */
	private String bot1JarPath;
	
	/**
	 * Used as {@link UT2004BotConfig#setBotId(String)} for the second bot.
	 * <p><p>
	 * Example: Bot1
	 */
	private String bot2Name;
	
	/**
	 * Used as {@link UT2004BotConfig#setDesiredSkill(int)} for the second bot.
	 * <p><p>
	 * Example: 5
	 */
	private Integer bot2Skill;
	
	/**
	 * Used as {@link UT2004BotConfig#setSkin(String)} for the second bot.
	 * <p><p>
	 * Example: HumanFemaleA.NightFemaleA
	 */
	private String bot2Skin;
	
	/**
	 * Used as {@link UT2004BotExecutionConfig#setPathToBotJar(String)} for the second bot.
	 * <p><p>
	 * Example: "bots" + File.separator + "Bot2" + File.separator + "Bot2.jar"
	 */
	private String bot2JarPath;
	
	/**
	 * Used as {@link UCCWrapperConf#setUnrealHome(String)}.
	 * <p><p>
	 * Example: "d:" + File.separator + "Games" + File.separator + "UT2004"
	 */
	private String unrealHome;
	
	/**
	 * Used as {@link UCCWrapperConf#setMapName(String)}.
	 * <p><p>
	 * Example: DM-1on1-Albatross
	 */
	private String mapName;
	
	/**
	 * Used as {@link UT2004DeathMatchConfig#setOutputDirectory(File)}.
	 * <p><p>
	 * Example: "results" + File.separator + "matches"
	 */
	private String outputDir;
	
	/**
	 * Used for logging.
	 */
	private LogCategory log;

	/**
	 * Last result of the match. Available after first successful execution of {@link UT2004DeathMatch1v1#run()}.
	 */
	private UT2004DeathMatchResult result = null;
	
	/**
	 * Whether to produce output for "HumanLikeBot project" analysis.
	 */
	private boolean humanLikeLogEnabled = false;

	private Throwable exception;

	/**
	 * Parameter-less constructor, don't forget to initialize everything!	 
	 */
	public UT2004DeathMatch1v1() {
		log = new LogCategory(getMatchName());
	}
	
	/**
	 * Match-name only constructor, don't forget to initialize everything!	 
	 */
	public UT2004DeathMatch1v1(String matchName) {
		this.matchName = matchName;
		log = new LogCategory(getMatchName());
	}
	
	/**
	 * Initializes all needed fields for combat of 2 bots.
	 * <p><p>
	 * Used time limit: 20 minutes,<p>
	 * Used frag limit: 20<p>
	 * Recommended mapName: DM-1on1-Albatross
	 * 
	 * @param unrealHome
	 * @param mapName
	 * @param bot1Name
	 * @param bot1JarPath
	 * @param bot2Name
	 * @param bot2JarPath
	 */
	public UT2004DeathMatch1v1(String unrealHome, String mapName, String bot1Name, String bot1JarPath, String bot2Name, String bot2JarPath) {
		this(unrealHome, mapName, bot1Name, null, null, bot1JarPath, bot2Name, null, null, bot2JarPath);
	}
	
	/**
	 * Initializes all need fields for combat of 2 bots + customize skins and desired skill levels of bots.
	 * @param unrealHome
	 * @param mapName
	 * @param bot1Name
	 * @param bot1Skill
	 * @param bot1Skin
	 * @param bot1JarPath
	 * @param bot2Name
	 * @param bot2Skill
	 * @param bot2Skin
	 * @param bot2JarPath
	 */
	public UT2004DeathMatch1v1(String unrealHome, String mapName, String bot1Name, Integer bot1Skill, String bot1Skin, String bot1JarPath, String bot2Name, Integer bot2Skill, String bot2Skin, String bot2JarPath) {
		this.unrealHome = unrealHome;
		this.mapName = mapName;
		this.bot1Name = bot1Name;
		this.bot1Skill = bot1Skill;
		this.bot1Skin = bot1Skin;
		this.bot1JarPath = bot1JarPath;
		this.bot2Name = bot2Name;
		this.bot2Skill = bot2Skill;
		this.bot2Skin = bot2Skin;
		this.bot2JarPath = bot2JarPath;
		this.matchName = bot1Name + "-vs-" + bot2Name;
		this.outputDir = "results" + File.separator + "matches";
		this.fragLimit = 20;
		this.timeLimitInMinutes = 20;
		log = new LogCategory(getMatchName());
	}
	
	/**
	 * Logger used for outputting info about the match.
	 * <p><p>
	 * If you want to output stuff to console, use {@link LogCategory#addConsoleHandler()}.
	 * @return
	 */
	public LogCategory getLog() {
		return log;
	}

	/**
	 * Used as {@link UT2004DeathMatchConfig#setFragLimit(int)}.
	 * 
	 * @return
	 */
	public int getFragLimit() {
		return fragLimit;
	}

	/**
	 * Used as {@link UT2004DeathMatchConfig#setTimeLimit(int)}.
	 * 
	 * @return
	 */
	public int getTimeLimitInMinutes() {
		return timeLimitInMinutes;
	}

	/**
	 * Used as {@link UT2004DeathMatchConfig#setMatchId(String)}.
	 * 
	 * @return
	 */
	public String getMatchName() {
		return matchName;
	}
	
	/**
	 * Used as {@link UT2004BotConfig#setBotId(String)} for the first bot.
	 * 
	 * @return
	 */
	public String getBot1Name() {
		return bot1Name;
	}
	
	/**
	 * Used as {@link UT2004BotConfig#setDesiredSkill(int)} for the first bot.
	 * 
	 * @return
	 */
	public Integer getBot1Skill() {
		return bot1Skill;
	}
	
	/**
	 * Used as {@link UT2004BotConfig#setSkin(String)} for the first bot.
	 */
	public String getBot1Skin() {
		return bot1Skin;
	}
	
	/**
	 * Used as {@link UT2004BotExecutionConfig#setPathToBotJar(String)} for the first bot.
	 * 
	 * @return
	 */
	public String getBot1JarPath() {
		return bot1JarPath;
	}
	
	/**
	 * Used as {@link UT2004BotConfig#setBotId(String)} for the second bot.
	 * 
	 * @return
	 */
	public String getBot2Name() {
		return bot2Name;
	}
	
	/**
	 * Used as {@link UT2004BotConfig#setDesiredSkill(int)} for the second bot.
	 * 
	 * @return
	 */
	public Integer getBot2Skill() {
		return bot2Skill;
	}
	
	/**
	 * Used as {@link UT2004BotConfig#setSkin(String)} for the second bot.
	 */
	public String getBot2Skin() {
		return bot2Skin;
	}
	
	/**
	 * Used as {@link UT2004BotExecutionConfig#setPathToBotJar(String)} for the second bot.
	 * 
	 * @return
	 */
	public String getBot2JarPath() {
		return bot2JarPath;
	}
	
	/**
	 * Used as {@link UCCWrapperConf#setMapName(String)}.
	 * 
	 * @return
	 */
	public String getUnrealHome() {
		return unrealHome;
	}
	
	/**
	 * Used as {@link UCCWrapperConf#setMapName(String)}.
	 * @return
	 */
	public String getMapName() {
		return mapName;
	}
	
	/**
	 * Used as {@link UT2004DeathMatchConfig#setOutputDirectory(File)}.
	 * 
	 * @return
	 */
	public String getOutputDir() {
		return outputDir;
	}
	
	/**
	 * Last result of the match. Available after first successful execution of {@link UT2004DeathMatch1v1#run()}.
	 * 
	 * @return
	 */
	public UT2004DeathMatchResult getResult() {
		return result;
	}

	/**
	 * If {@link UT2004DeathMatch1v1#run()} terminates with an exception, it will be made available through this getter.
	 * @return
	 */
	public Throwable getException() {
		return exception;
	}

	/**
	 * Sets logger used for outputting info about the match.
	 * @param log
	 */
	public void setLog(LogCategory log) {
		this.log = log;
	}

	public UT2004DeathMatch1v1 setFragLimit(int fragLimit) {
		this.fragLimit = fragLimit;
		return this;
	}

	public UT2004DeathMatch1v1 setTimeLimitInMinutes(int timeLimitInMinutes) {
		this.timeLimitInMinutes = timeLimitInMinutes;
		return this;
	}

	public UT2004DeathMatch1v1 setMatchName(String matchName) {
		this.matchName = matchName;
		return this;
	}

	public UT2004DeathMatch1v1 setBot1Name(String bot1Name) {
		this.bot1Name = bot1Name;
		return this;
	}
	
	public UT2004DeathMatch1v1 setBot1Skill(Integer skill) {
		bot1Skill = skill;
		return this;
	}
	
	public UT2004DeathMatch1v1 setBot1Skin(String skin) {
		bot1Skin = skin;
		return this;
	}

	public UT2004DeathMatch1v1 setBot1JarPath(String bot1JarPath) {
		this.bot1JarPath = bot1JarPath;
		return this;
	}
	
	public UT2004DeathMatch1v1 setBot2Skill(Integer skill) {
		bot2Skill = skill;
		return this;
	}
	
	public UT2004DeathMatch1v1 setBot2Skin(String skin) {
		bot2Skin = skin;
		return this;
	}

	public UT2004DeathMatch1v1 setBot2Name(String bot2Name) {
		this.bot2Name = bot2Name;
		return this;
	}

	public UT2004DeathMatch1v1 setBot2JarPath(String bot2JarPath) {
		this.bot2JarPath = bot2JarPath;
		return this;
	}

	public UT2004DeathMatch1v1 setUnrealHome(String unrealHome) {
		this.unrealHome = unrealHome;
		return this;
	}

	public UT2004DeathMatch1v1 setMapName(String mapName) {
		this.mapName = mapName;
		return this;
	}

	public UT2004DeathMatch1v1 setOutputDir(String outputDir) {
		this.outputDir = outputDir;
		return this;
	}
	
	public UT2004DeathMatch1v1 setHumanLikeLogEnabled(boolean humanLikeLogEnabled) {
		this.humanLikeLogEnabled = humanLikeLogEnabled;
		return this;
	}

	/**
	 * Removes directory with match results, called automatically in {@link UT2004DeathMatch1v1#run()}.
	 */
	public void cleanUp() {
		try {
			FileUtils.deleteQuietly(new File(getOutputDir() + File.separator + getMatchName()));
		} catch (Exception e) {			
		}		
	}	
	
	/**
	 * Contains main code that setups the {@link UT2004DeathMatchConfig}, {@link UT2004BotConfig} and {@link UCCWrapper}
	 * instances (it might be interesting for you to check the code for yourself if you wish to customize it further...).
	 * 
	 * @return
	 */
	protected UT2004DeathMatchConfig configure1Vs1() {
		UT2004DeathMatchConfig matchConfig = new UT2004DeathMatchConfig();
		
		matchConfig.setMatchId(getMatchName());
		matchConfig.setOutputDirectory(new File(getOutputDir() == null ? "results" + File.separator + "matches" : getOutputDir()));
				
		matchConfig.setFragLimit(getFragLimit());
		matchConfig.setTimeLimit(getTimeLimitInMinutes()); // in minutes
		
		matchConfig.setHumanLikeLogEnabled(humanLikeLogEnabled);
		
		matchConfig.getUccConf().setStartOnUnusedPort(true);
		matchConfig.getUccConf().setUnrealHome(getUnrealHome());
		matchConfig.getUccConf().setGameType("BotDeathMatch");
		matchConfig.getUccConf().setMapName(getMapName());
		
		UT2004BotConfig botConfig;
		
		botConfig = new UT2004BotConfig();
		botConfig.setBotId(getBot1Name());
		if (getBot1Skill() != null) {
			botConfig.setBotSkill(getBot1Skill());
		}
		if (getBot1Skin() != null) {
			botConfig.setBotSkin(getBot1Skin());
		}
		botConfig.setPathToBotJar(getBot1JarPath());
		botConfig.setBotTeam(255);
		botConfig.setRedirectStdErr(true);
		botConfig.setRedirectStdOut(true);		
		matchConfig.addBot(botConfig);
		
		botConfig = new UT2004BotConfig();
		botConfig.setBotId(getBot2Name());
		if (getBot2Skill() != null) {
			botConfig.setBotSkill(getBot2Skill());
		}
		if (getBot2Skin() != null) {
			botConfig.setBotSkin(getBot2Skin());
		}
		botConfig.setPathToBotJar(getBot2JarPath());
		botConfig.setBotTeam(255);
		botConfig.setRedirectStdErr(true);
		botConfig.setRedirectStdOut(true);		
		matchConfig.addBot(botConfig);
		
		return matchConfig;
	}
	
	/**
	 * Creates new instance of {@link UT2004DeathMatch} with desired configuration + validates it (i.e., if it won't throw an exception
	 * you may be sure, that the {@link UT2004DeathMatch} instance is correctly configured).
	 * @return
	 */
	public UT2004DeathMatch createMatch() {
		log.info("Configuring match: " + getMatchName());
		UT2004DeathMatchConfig matchConfig = configure1Vs1();
		UT2004DeathMatch match = new UT2004DeathMatch(matchConfig, log);
		match.validate();
		return match;
	}
	
	/**
	 * Executes the match. It first {@link UT2004DeathMatch1v1#cleanUp()}, than it configures {@link UT2004DeathMatch} via {@link UT2004DeathMatch1v1#createMatch()}
	 * and {@link UT2004DeathMatch#execute()}s it.
	 * <p><p>
	 * If no exception occurs during the match (== match will successfully finish), match result will be available through {@link UT2004DeathMatch1v1#getResult()}.
	 * <p><p>
	 * If exception occurs (due to whatever reason), this exception will be available through {@link UT2004DeathMatch1v1#getException()}.
	 * 
	 * @throws PogamutException wraps any exception into this one
	 */
	@Override
	public void run() {
		try {
			cleanUp();
			
			UT2004DeathMatch match = createMatch();
	
			log.info("Executing match: " + getMatchName());
			
			this.result = match.execute();
			
			log.info("Match " + getMatchName() + " result: " + result);
				
			log.info("---/// MATCH OK ///---");
		} catch (Exception e) {
			if (log != null && log.isLoggable(Level.SEVERE)) log.severe(ExceptionToString.process("Failed to execute the match: " + getMatchName() + ".", e));
			this.exception = e;
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new PogamutException("Failed to execute the match: " + getMatchName(), e, this);
		}
	}

	@Override
	public UT2004DeathMatchResult call() throws Exception {
		run();
		return getResult();
	}

}
