package cz.cuni.amis.pogamut.ut2004.tournament.match;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;

import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateUp;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectListener;
import cz.cuni.amis.pogamut.base.component.bus.event.BusAwareCountDownLatch;
import cz.cuni.amis.pogamut.base.utils.guice.AdaptableProvider;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base.utils.logging.LogFormatter;
import cz.cuni.amis.pogamut.base.utils.logging.LogHandler;
import cz.cuni.amis.pogamut.base.utils.logging.LogPublisher;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.execution.UT2004BotExecution;
import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;
import cz.cuni.amis.pogamut.ut2004.analyzer.IAnalyzerObserverListener;
import cz.cuni.amis.pogamut.ut2004.analyzer.IUT2004AnalyzerObserver;
import cz.cuni.amis.pogamut.ut2004.analyzer.UT2004Analyzer;
import cz.cuni.amis.pogamut.ut2004.analyzer.UT2004AnalyzerModule;
import cz.cuni.amis.pogamut.ut2004.analyzer.UT2004AnalyzerParameters;
import cz.cuni.amis.pogamut.ut2004.analyzer.stats.UT2004AnalyzerObsStatsModule;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.AddBot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.ChangeTeam;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.GameConfiguration;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Kick;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Record;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Respawn;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.StartPlayers;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameRestarted;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004AnalyzerFactory;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004ServerFactory;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004ServerModule;
import cz.cuni.amis.pogamut.ut2004.server.impl.UT2004Server;
import cz.cuni.amis.pogamut.ut2004.teamcomm.server.UT2004TCServer;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004MatchConfig.BotType;
import cz.cuni.amis.pogamut.ut2004.tournament.match.result.UT2004MatchResult;
import cz.cuni.amis.pogamut.ut2004.utils.UCCWrapper;
import cz.cuni.amis.utils.ExceptionToString;
import cz.cuni.amis.utils.FilePath;
import cz.cuni.amis.utils.Iterators;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.collections.CollectionEventListener;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;
import cz.cuni.amis.utils.flag.FlagListener;
import cz.cuni.amis.utils.maps.LazyMap;
import cz.cuni.amis.utils.token.IToken;

/**
 * This class serves as a gateway for creating various matches using custom bots.
 * <p><p>
 * The class is sought to be inherited and its abstract method be additionally defined.
 * <p><p>
 * It also defines may handful methods that can be used to create required GameBots2004.ini file,
 * start UCC, start all bots with observers / change their team / restart match.
 *  
 * @author Jimmy
 */
public abstract class UT2004Match<CONFIG extends UT2004MatchConfig, RESULT extends UT2004MatchResult> implements Callable<RESULT>, Runnable {

	private static final int MAX_TEAMS = 8;
	/**
	 * Configuration of the match, it contains all information that is needed to start the match.
	 */
	protected CONFIG config;
	protected LogCategory log;
	protected RESULT result;
	protected Throwable exception;
	protected boolean teamMatch;
	
	protected LogHandler fileHandler;
	
	/**
	 * Where we have backed up the UT2004.ini file...
	 */
	protected File ut2004FileBackup;
	
	/**
	 * Where we have backed up the GB2004.ini file...
	 */
	protected File gb2004FileBackup;

	/**
	 * Construct the match with provided configuration. 
	 * @param config MUST NOT BE NULL
	 */
	public UT2004Match(boolean teamMatch, CONFIG config, LogCategory log) {
		NullCheck.check(config, "config");
		this.config = config;
		NullCheck.check(config.getMatchId(), "config.getMatchId()");
		this.log = log;
		this.teamMatch = teamMatch;
	}
	
	/**
	 * Whether team / individuals are fighting in this match.
	 * <p><p>
	 * True == team match, False = match of individual bots.
	 * @return
	 */
	public boolean isTeamMatch() {
		return teamMatch;
	}
	
	public static boolean isHumanPlayer(Player player) {
		// IS THIS CORRECT?
		return player.getId() != null && player.getId().getStringId().toLowerCase().contains("player");
	}
	
	//
	//
	// EXECUTION / Callable / Runnable
	//
	//
	
	/**
	 * Returns result of the match (if it was performed by either calling {@link UT2004Match#run()} or {@link UT2004Match#call()}).
	 */
	public RESULT getResult() {
		return result;
	}
	
	/**
	 * If exception occures during the match execution, it is stored and made available through this method.
	 * @return
	 */
	public Throwable getException() {
		return exception;
	}

	/**
	 * Runs the match, its result is than available through {@link UT2004Match#getResult()}.
	 */
	public void run() {
		try {
			if (log != null && log.isLoggable(Level.WARNING)) log.warning("Executing match: " + getMatchId().getToken());
			
			result = null;
			exception = null;
			
			this.result = execute();
			
			if (log != null && log.isLoggable(Level.WARNING)) log.warning("---/// MATCH OK ///---");
		} catch (Exception e) {
			if (log != null && log.isLoggable(Level.SEVERE)) log.severe(ExceptionToString.process("Failed to execute the match: " + getMatchId().getToken() + ".", e));
			this.exception = e;
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new PogamutException("Failed to execute the match: " + getMatchId().getToken(), e, this);
		}
	}
	
	public RESULT call() {
		run();
		return getResult();
	}

	/**
	 * Performs the match and return the result (or throw an exception in case of error).
	 * <p><p>
	 * It is called by {@link UT2004Match#run()} that is used by {@link UT2004Match#call()}.
	 * @return
	 */
	protected abstract RESULT execute();
	
	//
	//
	// STEPS OF EXECUTIONS
	//
	//
	
	/**
	 * May be used for file names.
	 * @return
	 */
	public static String getCurrentDate() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		return dateFormat.format(date);
	}

	/**
	 * Returns configuration of the match.
	 * @return
	 */
	public CONFIG getConfig() {
		return config;
	}

	/**
	 * Returns ID of the match.
	 * @return
	 */
	public IToken getMatchId() {
		return config.getMatchId();
	}
	
	/**
	 * Returns logger used for outputting stuff.
	 * @return
	 */
	public LogCategory getLog() {
		return log;
	}

	@Override
	public String toString() {
		if (this == null) return "UT2004Match";
		return getClass().getSimpleName() + "[id=" + config.getMatchId().getToken() + ", custom bots=" + config.getBots().size() + ", native bots=" + config.getNativeBots().size() + ", humans=" + config.getHumans().size() + "]";
	}
	
	/**
	 * Parent path of all files that should be output by the class as results.
	 * @return
	 */
	public File getOutputPath() {
		return getOutputPath("");
	}
	
	/**
	 * Returns path relative to {@link UT2004Match#getOutputPath()}.
	 * @param relativePath
	 * @return
	 */
	public File getOutputPath(String relativePath) {
		if (relativePath == null || relativePath.length() == 0) {
			return new File(config.getOutputDirectory().getAbsoluteFile() + File.separator + config.getMatchId().getToken());
		} else {
			return new File(config.getOutputDirectory().getAbsoluteFile() + File.separator + config.getMatchId().getToken() + File.separator + relativePath);
		}
	}
	
	
	/**
	 * Usually STEP 0 that set up logger to output to file.
	 */
	protected void setupLogger() {
		if (log != null && log.isLoggable(Level.FINE)) {
			log.fine(config.getMatchId().getToken() + ": Adding output of logs into " + getOutputPath("match-" + config.getMatchId().getToken() + ".log"));
		}
		if (log != null) {
			log.addHandler(fileHandler = new LogHandler(new LogPublisher.FilePublisher(getOutputPath("match-" + config.getMatchId().getToken() + ".log"), new LogFormatter(new AgentId(config.getMatchId().getToken()), true))));
		}
		if (log != null && log.isLoggable(Level.INFO)) {
			log.info(this + " file output setup");
		}
	}
	
	/**
	 * Usually STEP 1 in match execution ... it validates the contents of {@link UT2004Match#config}.
	 * <p><p>
	 * Raises exception in case of error.
	 * <p><p>
	 * As it is public, you may also use it prior the match execution by yourself to ensure that the match is correctly configured.
	 */
	public void validate() {
		try {
			if (log != null && log.isLoggable(Level.FINE)) {
				log.fine(config.getMatchId().getToken() + ": Validating match configuration...");
			}
			config.validate();
			if (log != null && log.isLoggable(Level.INFO)) {
				log.info(config.getMatchId().getToken() + ": Match configuration validated - OK.");
			}
		} catch (PogamutException e) {
			if (log != null) log.severe(e.getMessage());
			throw e;
		} catch (Exception e) {
			if (log != null) log.severe(e.getMessage());
			throw new PogamutException("Validation failed.", e, log, this);
		}
	}
	
	/**
	 * WARNING: this method will delete the whole directory where results are stored! IT WILL DELETE IT COMPLETELY!
	 * DO NOT USE IT ON A WHIM... be sure you're using separate directories for all matches.
	 */
	public void cleanUp() {
		if (log != null && log.isLoggable(Level.WARNING)) log.warning("Cleaning up! Deleting: " + getOutputPath().getAbsolutePath());
		FileUtils.deleteQuietly(getOutputPath());
	}
	
	/**
	 * Returns file that is pointing to a directory that contains ucc.exe.
	 * @return
	 */
	protected File getUccHome() {
		return new File(config.getUccConf().getUnrealHome() + File.separator + "System");
	}
	
	/**
	 * Returns file that is pointing to GameBots2004.ini that will be used by ucc.exe.
	 * @return
	 */
	protected File getUT2004IniFile() {
		return new File(config.getUccConf().getUnrealHome() + File.separator + "System" + File.separator + "UT2004.ini");
	}
	
	/**
	 * Returns file that is pointing to GameBots2004.ini that will be used by ucc.exe.
	 * @return
	 */
	protected File getGB2004IniFile() {
		return new File(config.getUccConf().getUnrealHome() + File.separator + "System" + File.separator + "GameBots2004.ini");
	}
	
	/**
	 * Usually STEP 2.2 ... it overwrites the GameBots2004.ini file that is present in UT2004 home.
	 * <p><p>
	 * Raises exception in case of any error.
	 */
	protected void createUT2004Ini() {
		if (log != null && log.isLoggable(Level.FINE)) {
			log.fine(config.getMatchId().getToken() + ": Outputting UT2004.ini into " + getUT2004IniFile().getAbsolutePath() + " ...");
		}
		File ut2004File = getUT2004IniFile();
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		ut2004FileBackup = new File(ut2004File.getParent() + File.separator + "UT2004.ini." + sdf.format(date) + ".backup");
		if (ut2004File.isFile() && ut2004File.exists()) {
			if (log != null && log.isLoggable(Level.FINE)) {
				log.fine(config.getMatchId().getToken() + ": Backing up UT2004.ini from " + ut2004File.getAbsolutePath() + " into + " + ut2004FileBackup.getAbsolutePath() + " ...");
			}
			boolean backup = true;
			try {
				FileUtils.copyFile(ut2004File, ut2004FileBackup);
			} catch (IOException e) {
				backup = false;
			}
			if (backup && log != null && log.isLoggable(Level.FINE)) {
				log.fine(config.getMatchId().getToken() + ": Backed up UT2004.ini from " + ut2004File.getAbsolutePath() + " into + " + ut2004FileBackup.getAbsolutePath() + " ...");
			} else
			if (!backup && log != null && log.isLoggable(Level.SEVERE)){
				log.severe(config.getMatchId().getToken() + ": Failed to back up UT2004.ini from " + ut2004File.getAbsolutePath() + " into + " + ut2004FileBackup.getAbsolutePath() + " !!!");
				
			}
		}
		config.getUT2004Ini().output(ut2004File);
		if (log != null && log.isLoggable(Level.INFO)) {
			log.info(config.getMatchId().getToken() + ": UT2004.ini output into " + getUT2004IniFile().getAbsolutePath() + ".");
		}
	}
	
	/**
	 * Usually STEP 2.2 ... it overwrites the GameBots2004.ini file that is present in UT2004 home.
	 * <p><p>
	 * Raises exception in case of any error.
	 */
	protected void createGB2004Ini() {
		if (log != null && log.isLoggable(Level.FINE)) {
			log.fine(config.getMatchId().getToken() + ": Outputting GameBots2004.ini into " + getGB2004IniFile().getAbsolutePath() + " ...");
		}
		File gb2004File = getGB2004IniFile();
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		gb2004FileBackup = new File(gb2004File.getParent() + File.separator + "GameBots2004.ini." + sdf.format(date) + ".backup");
		if (gb2004File.isFile() && gb2004File.exists()) {
			if (log != null && log.isLoggable(Level.FINE)) {
				log.fine(config.getMatchId().getToken() + ": Backing up GameBots2004.ini from " + gb2004File.getAbsolutePath() + " into + " + gb2004FileBackup.getAbsolutePath() + " ...");
			}
			boolean backup = true;
			try {
				FileUtils.copyFile(gb2004File, gb2004FileBackup);
			} catch (IOException e) {
				backup = false;
			}
			if (backup && log != null && log.isLoggable(Level.FINE)) {
				log.fine(config.getMatchId().getToken() + ": Backed up GameBots2004.ini from " + gb2004File.getAbsolutePath() + " into + " + gb2004FileBackup.getAbsolutePath() + " ...");
			} else
			if (!backup && log != null && log.isLoggable(Level.SEVERE)){
				log.severe(config.getMatchId().getToken() + ": Failed to back up GameBots2004.ini from " + gb2004File.getAbsolutePath() + " into + " + gb2004FileBackup.getAbsolutePath() + " !!!");
				
			}
		}
		config.getGb2004Ini().output(gb2004File);
		if (log != null && log.isLoggable(Level.INFO)) {
			log.info(config.getMatchId().getToken() + ": GameBots2004.ini output into " + getGB2004IniFile().getAbsolutePath() + ".");
		}
	}
	
	/**
	 * Usually STEP 3 ... it starts up the UCC, it is a blocking method that waits until UCC is up and running.
	 * <p><p>
	 * Raises exception in case of any error.
	 *  
	 * @return started wrapper of the ucc process
	 */
	protected UCCWrapper startUCC() {
		if (log != null && log.isLoggable(Level.FINE)) {
			log.fine(config.getMatchId().getToken() + ": Starting UCC with " + config.getUccConf() + " ...");
		}
		UCCWrapper result = new UCCWrapper(config.getUccConf());
		if (log != null && log.isLoggable(Level.INFO)) {
			log.info(config.getMatchId().getToken() + ": UCC started with " + config.getUccConf() + ".");
			log.info(config.getMatchId().getToken() + ": GB2004 host                = " + result.getHost());
			log.info(config.getMatchId().getToken() + ": GB2004 bot port            = " + result.getBotPort());
			log.info(config.getMatchId().getToken() + ": GB2004 control server port = " + result.getControlPort());
			log.info(config.getMatchId().getToken() + ": GB2004 observer port       = " + result.getObserverPort());
		}
		return result;
	}
	
	/**
	 * Usually STEP 4 ... after the UCC has started up, you usually want to connect to it to confirm it is up and running
	 * and be able to observe any changes in the environment / alter the environment, etc.
	 * <p><p>
	 * This method may need to be override to provide custom implementation of {@link UT2004Server} interface, i.e.,
	 * provide your custom control server. Current implementation is using {@link UT2004Server}.
	 * <p><p>
	 * Raises exception in case of any error.
	 * <p><p>
	 * If {@link UT2004MatchConfig#isStartTCServer()} it will start {@link UT2004TCServer} instead of plain {@link UT2004Server}.
	 * 
	 * @param ucc MUST NOT BE NULL
	 * @return running control server
	 */
	protected UT2004Server startControlServer(UCCWrapper ucc) {
		if (log != null && log.isLoggable(Level.FINE)) {
			log.fine(config.getMatchId().getToken() + ": Starting UT2004Server...");
		}
		NullCheck.check(ucc, "ucc");
		
		if (config.isStartTCServer()) {
			if (log != null && log.isLoggable(Level.INFO)) {
				log.info(config.getMatchId().getToken() + ": config.isStartTCServer() == TRUE! Starting UT2004TCServer!");
			}
			UT2004TCServer server = UT2004TCServer.startTCServer(ucc.getHost(), ucc.getControlPort());
			server.getLogger().setLevel(Level.WARNING);
			server.getLogger().addDefaultConsoleHandler();
			if (log != null && log.isLoggable(Level.INFO)) {
				log.info(config.getMatchId().getToken() + ": UT2004TCServer started.");
			}
			return server;			
		} else {
			UT2004ServerModule module = new UT2004ServerModule<UT2004AgentParameters>();
			UT2004ServerFactory<UT2004Server, UT2004AgentParameters> factory = new UT2004ServerFactory(module);
			UT2004Server server = (UT2004Server) factory.newAgent(new UT2004AgentParameters().setAgentId(new AgentId(config.getMatchId().getToken()+"-UT2004Server")).setWorldAddress(ucc.getServerAddress()));
			server.getLogger().setLevel(Level.WARNING);
			server.getLogger().addDefaultConsoleHandler();
			server.start();
			if (log != null && log.isLoggable(Level.INFO)) {
				log.info(config.getMatchId().getToken() + ": UT2004Server started.");
			}
			return server;
		}
	}
	
	public static class Bots {
		
		/**
		 * Contains mapping of {@link IUT2004BotConfig#getBotId()} (from {@link UT2004MatchConfig#bots}) to {@link UnrealId} of the bot inside UT2004 (id that was given
		 * to bot by UT2004).
		 * <p><p>
		 * Contains only mappings for CUSTOM bots.
		 */
		public Map<IToken, UnrealId> botId2UnrealId = new HashMap<IToken, UnrealId>();
		
		/**
		 * Contains mapping of {@link UnrealId} of the bot inside UT2004 (id that was given
		 * to bot by UT2004) to {@link IUT2004BotConfig#getBotId()} (from {@link UT2004MatchConfig#bots}).
		 * <p><p>
		 * Contains only mappings for CUSTOM bots.
		 */
		public Map<UnrealId, IToken> unrealId2BotId = new HashMap<UnrealId, IToken>();
		
		/**
		 * Contains mapping of {@link IUT2004BotConfig#getBotId()} (from {@link UT2004MatchConfig#nativeBots}) to {@link UnrealId} of the bot inside UT2004 (id that was given
		 * to bot by UT2004).
		 * <p><p>
		 * Contains only mappings for CUSTOM bots.
		 */
		public Map<IToken, UnrealId> nativeBotId2UnrealId = new HashMap<IToken, UnrealId>();
				
		/**
		 * Contains mapping of {@link UnrealId} of the bot inside UT2004 (id that was given
		 * to bot by UT2004) to {@link IUT2004BotConfig#getBotId()} (from {@link UT2004MatchConfig#nativeBots}).
		 * <p><p>
		 * Contains only mappings for NATIVE bots.
		 */
		public Map<UnrealId, IToken> nativeUnrealId2BotId = new HashMap<UnrealId, IToken>();
				 
		/**
		 * Contains mapping of {@link IUT2004HumanConfig#getHumanId()} (from {@link UT2004MatchConfig#humans}) to {@link UnrealId} of the human inside UT2004.
		 * <p><p> 
		 * Contains only mappings for HUMANs.
		 */
		public Map<IToken, UnrealId> humanId2UnrealId = new HashMap<IToken, UnrealId>();
		
		/**
		 * Contains mapping of {@link UnrealId} of the human inside UT2004 to {@link IUT2004HumanConfig#getHumanId()} (from {@link UT2004MatchConfig#humans}).
		 * <p><p> 
		 * Contains only mappings for HUMANs.
 		 */
		public Map<UnrealId, IToken> humanUnrealId2HumanId = new HashMap<UnrealId, IToken>();
		
		/**
		 * Contains mapping of {@link UnrealId} to "NAMES" (real names within the game). 
		 *
		 */
		public Map<UnrealId, String> names = new HashMap<UnrealId, String>();
		
		/**
		 * Wrappers of custom bots' processes.
		 */
		public Map<IToken, UT2004BotExecution> bots = new HashMap<IToken, UT2004BotExecution>();
		
		/**
		 * If {@link UT2004Analyzer} is used, this will be filled with respective observers in STEP 6. Contains both CUSTOM+NATIVES+HUMAN observers.
		 */
		public Map<IToken, IUT2004AnalyzerObserver> botObservers = new HashMap<IToken, IUT2004AnalyzerObserver>();
		
		/**
		 * Which bots have died off?
		 */
		public Set<IToken> diedOff = new HashSet<IToken>();

		/**
		 * {@link System#currentTimeMillis()} when the match was restarted.
		 */
		public long matchStart = 0;

		/**
		 * {@link System#currentTimeMillis()} when the match has ended.
		 */
		public long matchEnd = 0;
		
		public UnrealId getUnrealId(IToken botId) {
			UnrealId result = botId2UnrealId.get(botId);
			if (result != null) return result;
			result = nativeBotId2UnrealId.get(botId);
			if (result != null) return result;
			return humanId2UnrealId.get(botId);
		}
		
		public IToken getBotId(UnrealId unrealId) {
			IToken result = unrealId2BotId.get(unrealId);
			if (result != null) return result;
			result = nativeUnrealId2BotId.get(unrealId);
			if (result != null) return result;
			return humanUnrealId2HumanId.get(unrealId);
		}
		
		public boolean isBot(IToken botId) {
			return botId2UnrealId.containsKey(botId);
		}
		
		public boolean isBot(UnrealId unrealId) {
			return unrealId2BotId.containsKey(unrealId);
		}
		
		public boolean isNativeBot(IToken botId) {
			return nativeBotId2UnrealId.containsKey(botId);
		}
		
		public boolean isNativeBot(UnrealId unrealId) {
			return nativeUnrealId2BotId.containsKey(unrealId);
		}
		
		public boolean isHuman(IToken botId) {
			return humanId2UnrealId.containsKey(botId);
		}
		
		public boolean isHuman(UnrealId unrealId) {
			return humanUnrealId2HumanId.containsKey(unrealId);
		}
		
		public BotType getType(IToken botId) {
			if (isBot(botId)) return BotType.BOT;
			if (isNativeBot(botId)) return BotType.NATIVE;
			if (isHuman(botId)) return BotType.HUMAN;
			return null;
		}
		
		public BotType getType(UnrealId botId) {
			if (isBot(botId)) return BotType.BOT;
			if (isNativeBot(botId)) return BotType.NATIVE;
			if (isHuman(botId)) return BotType.HUMAN;
			return null;
		}
		
	}
	
	protected void changeBotTeam(UT2004Server server, UnrealId botId, int desiredTeam) {
		NullCheck.check(server, "server");
		NullCheck.check(botId, "botId");
		final int targetTeam = desiredTeam;
		if (targetTeam < 0 || targetTeam >= MAX_TEAMS) return;
		
		Player player = (Player)server.getWorldView().get(botId);
		if (player == null) {
			throw new PogamutException("Bot with unrealId '" + botId + "' does not exists in 'server' worldview! **PUZZLED**", log, this);
		}
		if (player.getTeam() == targetTeam) {
			// NO NEED TO CHANGE THE TEAM
			return;
		}
		
		if (log != null && log.isLoggable(Level.FINE)) {
			log.fine(config.getMatchId().getToken() + ": Switching Bot[unrealId=" + botId.getStringId() + "] to team " + desiredTeam + "...");
		}
		
		final CountDownLatch teamChangedLatch = new CountDownLatch(1);
		
		IWorldObjectListener<Player> playerListener = new IWorldObjectListener<Player>() {
			Location previous;
			@Override
			public void notify(IWorldObjectEvent<Player> event) {
				if (event.getObject().getTeam() == targetTeam) teamChangedLatch.countDown();
			}
		};
		server.getWorldView().addObjectListener(botId, playerListener);
		
		try {
			server.getAct().act(new ChangeTeam(botId, targetTeam));
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				throw new PogamutInterruptedException("Interrupted while awaiting team-change.", log, e);
			}
			server.getAct().act(new Respawn().setId(botId));
			
			long teamChangeTimeoutSecs = 60;
			
			if (log != null && log.isLoggable(Level.FINE)) {
				log.fine(config.getMatchId().getToken() + ": Waitng for Bot[unrealId=" + botId.getStringId() + "] to be switched to team " + desiredTeam + " for " + teamChangeTimeoutSecs + "secs...");
			}
			try {
				teamChangedLatch.await(teamChangeTimeoutSecs, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				throw new PogamutInterruptedException("Interrupted while awaiting team-change.", log, e);
			}
			
			player = (Player)server.getWorldView().get(botId);
			if (player == null) {
				throw new PogamutException("Bot with unrealId '" + botId + "' does not exists in 'server' worldview! **PUZZLED**", log, this);
			}
			if (player.getTeam() != targetTeam) {
				throw new PogamutException("Failed to change the bot with botId '" + botId +"' corresponding unrealId '" + botId + "' into correct team within " + teamChangeTimeoutSecs + "secs! Required team is " + targetTeam + ", current team is " + player.getTeam() + ".", log, this);
			}
			if (log != null && log.isLoggable(Level.INFO)) {
				log.info(config.getMatchId().getToken() + ": Bot[unrealId=" + botId.getStringId() + "] switched to team " + desiredTeam + ".");
			}
		} finally {
			server.getWorldView().removeObjectListener(botId, playerListener);
		}

	}
	
	/**
	 * Usually STEP 5.1 ... starts all custom & native bots + changes their teams if needed.
	 * <p><p>
	 * Raises exception in case of any error.
	 * <p><p>
	 * Note that the inner implementation is pretty complex... if you need to alter the method, copy-paste
	 * it and dig in.
	 * 
	 * @param ucc MUST NOT BE NULL
	 * @param server MUST NOT BE NULL
	 * @param analyzer may be NULL
	 * @return context of bot executions
	 */
	protected Bots startBots(UCCWrapper ucc, UT2004Server server) {
		if (log != null && log.isLoggable(Level.FINE)) {
			log.fine(config.getMatchId().getToken() + ": Starting custom & native bots...");
		}

		NullCheck.check(ucc, "ucc");
		NullCheck.check(server, "server");
		
		final Bots bots = new Bots();
		
		server.getAct().act(new StartPlayers());
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			throw new PogamutInterruptedException("Interrupted while awaiting start of players' exporting on the server.", log, e);
		}
		
		if (server.getPlayers().size() > 0) {
			boolean bot = false;
			for (Player player : server.getPlayers()) {
				if (isHumanPlayer(player)) continue;
				if (player.isSpectator()) continue;
				bot = true;
			}
			if (bot) {
				throw new PogamutException("There are already some bots/players/native bots connected to the game, even though we have not started to connect any of them yet. INVALID STATE!", log, this);
			}
		}
		
		//
		// CONNECT ALL CUSTOM BOTS (== BASED ON POGAMUT PLATFORM)
		//
		{
			final AdaptableProvider<IToken> connecting = new AdaptableProvider<IToken>(null);
			final AdaptableProvider<CountDownLatch> latch = new AdaptableProvider<CountDownLatch>(null);
			
			CollectionEventListener<Player> playerListener = new CollectionEventListener<Player>() {
				@Override
				public void postAddEvent(Collection<Player> alreadyAdded,Collection<Player> whereWereAdded) {				
				}
				@Override
				public void postRemoveEvent(Collection<Player> alreadyAdded,Collection<Player> whereWereRemoved) {
					if (alreadyAdded.size() == 0) return;
				}
				@Override
				public void preAddEvent(Collection<Player> toBeAdded, Collection<Player> whereToAdd) {
					if (toBeAdded.size() == 0) return;
					for (Player player : toBeAdded) {
						if (isHumanPlayer(player)) continue;
						if (player.isSpectator()) continue;
						if (log != null && log.isLoggable(Level.FINE)) {
							log.fine(config.getMatchId().getToken() + ": New bot connected to GB2004. Bot[unrealId=" + player.getId().getStringId() + ", name=" + player.getName() + "], binding its unrealId to botId " + connecting.get().getToken() + ".");
						}
						// ADD BINDING BETWEEN MATCH_BOT_ID <-> UT2004_PLAYER_ID
						bots.botId2UnrealId.put(connecting.get(), player.getId());
						bots.unrealId2BotId.put(player.getId(), connecting.get());
						bots.names.put(player.getId(), player.getName());
						
						latch.get().countDown();
					}
				}
				@Override
				public void preRemoveEvent(Collection<Player> toBeRemoved, Collection<Player> whereToRemove) {
					if (toBeRemoved.size() == 0) return;
					boolean bot = false;
					for (Player player : toBeRemoved) {
						if (isHumanPlayer(player)) continue;
						if (player.isSpectator()) continue;
						bot = true;
					}
					if (!bot) return;
					if (log != null && log.isLoggable(Level.WARNING)) {
						StringBuffer sb = new StringBuffer();
						sb.append(config.getMatchId().getToken() + ": Bot(s) removed from GB2004!!!");
						boolean first = true;
						for (Player plr : toBeRemoved) {
							if (first) first = false;
							else sb.append(", ");
							sb.append("Bot[unrealId=" + plr.getId().getStringId() + ", name=" + plr.getName() + "]");
						}
						log.warning(sb.toString());
					}
					throw new PogamutException("(CustomBot connecting) There can't be any 'removes' at this stage.", log, this);
				}
			};
			// ADD COLLECTION LISTENER TO PLAYERS IN THE UT2004Server
			server.getPlayers().addCollectionListener(playerListener);
			
			boolean exception = false;
			try {
				for (UT2004BotConfig botConfig : config.getBots().values()) {
					if (log != null && log.isLoggable(Level.FINE)) {
						log.fine(config.getMatchId().getToken() + ": Starting custom Bot[botId=" + botConfig.getBotId().getToken() + "]...");
					}
					
					// CHECK THE SERVER STATE
					if (server.notInState(IAgentStateUp.class)) {
						throw new PogamutException("(CustomBot connecting) Server is DEAD! Some previous exception will have the explanation...", log, this);
					}
					// OKEY, WE'RE STILL HERE
					
					// INITIALIZE THE LATCH
					latch.set(new CountDownLatch(1)); // we wait till the bot get connected to the environment
					
					// SET WHICH BOTID WE'RE GOING TO CONNECT NOW
					connecting.set(botConfig.getBotId());
					
//					// OVERRIDE DEFAULT PARAMETERS
//					// commented out => already taken care of by UT2004BotExecutionConfig by itself				
//					botConfig.getParameters().put(PogamutUT2004Property.POGAMUT_UT2004_BOT_INIT_OVERRIDE_PARAMS.getKey(), true);
//					
//					// NAME?
//					if (botConfig.getName() != null) {
//						botConfig.getParameters().put(PogamutUT2004Property.POGAMUT_UT2004_BOT_INIT_NAME.getKey(), botConfig.getName());
//					}
//					
//					// TEAM?
//					if (botConfig.getBotTeam() >= 0 && botConfig.getBotTeam() < 4) {
//						botConfig.getParameters().put(PogamutUT2004Property.POGAMUT_UT2004_BOT_INIT_TEAM.getKey(), botConfig.getBotTeam());
//					}
//					
//					// SKIN?
//					if (botConfig.getBotSkin() != null) {
//						botConfig.getParameters().put(PogamutUT2004Property.POGAMUT_UT2004_BOT_INIT_SKIN.getKey(), botConfig.getBotSkin());
//					}
//					
//					// DESIRED SKILL LEVEL?
//					if (botConfig.getBotSkill() >= 0) {
//						botConfig.getParameters().put(PogamutUT2004Property.POGAMUT_UT2004_BOT_INIT_SKILL.getKey(), botConfig.getBotSkill());
//					}					
					
					// CREATE WRAPPER FOR THE BOT CONFIGURATION
					UT2004BotExecution execution = new UT2004BotExecution(botConfig, log);					
					
					// CREATE TEMPORARY RUNNING LISTENER
					FlagListener<Boolean> botObs = new FlagListener<Boolean>() {
						@Override
						public void flagChanged(Boolean changedValue) {
							if (!changedValue) {
								latch.get().countDown();
							}
						}
					};
					
					// SUBSCRIBE THE RUNNING LISTENER
					execution.getRunning().addListener(botObs);
					
					try {
						// SUBSCRIBE THE WRAPPER INTO THE RESULT STRUCTURE
						bots.bots.put(botConfig.getBotId(), execution);
						
						// START THE BOT
						execution.start(ucc.getHost(), ucc.getBotPort(), ucc.getControlPort(), ucc.getObserverPort());
						
						// WAIT TILL WE CATCH HIS ID / OR TIMEOUT / OR BOT FAILURE
						latch.get().await(10 * 60 * 1000, TimeUnit.MILLISECONDS);						
					} finally {
						// REMOVE THE LISTENER
						execution.getRunning().removeListener(botObs);
					}
					
					if (!execution.isRunning()) {
						throw new PogamutException("(CustomBot connecting) Bot[botId=" + botConfig.getBotId().getToken() + "] startup failed!", log, this);
					}
					if (latch.get().getCount() > 0) {
						// LATCH TIMEOUT!
						throw new PogamutException("(CustomBot connecting) Bot[botId=" + botConfig.getBotId().getToken() + "], startup failed! It does not showed up on server for 10 minutes... either failed to start, or " + server + " failed to got its presence from GB2004.", log, this);
					}
					
					// SUCCESS! THE BOT IS UP AND RUNNING
					if (log != null && log.isLoggable(Level.INFO)) {
						log.info(config.getMatchId().getToken() + ": Started custom Bot[botId=" + botConfig.getBotId().getToken() + ", unrealId=" + bots.getUnrealId(botConfig.getBotId()).getStringId() + "].");
					}
					
					// CHANGE ITS TEAM IF REQUIRED
					if (botConfig.getBotTeam() != null && botConfig.getBotTeam() >= 0 && botConfig.getBotTeam() < MAX_TEAMS) {
						changeBotTeam(server, bots.getUnrealId(botConfig.getBotId()), botConfig.getBotTeam());
					}
				}
				if (bots.botId2UnrealId.size() != config.getBots().size()) {
					throw new PogamutException("(CustomBot connecting) Not all mappings BotId<->UnrealId has been created. **PUZZLING**", log, this);
				}
				if (bots.unrealId2BotId.size() != config.getBots().size()) {
					throw new PogamutException("(CustomBot connecting) Not all mappings UnrealId<->BotId has been created. **PUZZLING**", log, this);
				}
			} catch (Exception e) {
				exception = true;
				if (e instanceof PogamutException) throw (PogamutException)e;
				throw new PogamutException("(CustomBot connecting) Can't start all custom bots! Exception happened while starting " + connecting.get().getToken() + ".", e, log, this);
			} finally {
				if (exception) {
					// some bullshit has happened, we have to tear it all down
					for (UT2004BotExecution execution : bots.bots.values()) {
						execution.stop();
					}
					bots.bots.clear();
				}
				// drop the listener, not needed anymore
				server.getPlayers().removeCollectionListener(playerListener);
			}
			// SUCCESS ALL CUSTOM BOTS ARE RUNNING AS THEY SHOULD
		}
		
		//
		// CONNECT ALL NATIVE BOTS
		//
		{
			final AdaptableProvider<IToken> connecting = new AdaptableProvider<IToken>(null);
			final AdaptableProvider<CountDownLatch> latch = new AdaptableProvider<CountDownLatch>(null);
			
			CollectionEventListener<Player> playerListener = new CollectionEventListener<Player>() {
				@Override
				public void postAddEvent(Collection<Player> alreadyAdded,Collection<Player> whereWereAdded) {				
				}
				@Override
				public void postRemoveEvent(Collection<Player> alreadyAdded,Collection<Player> whereWereRemoved) {
					if (alreadyAdded.size() == 0) return;
				}
				@Override
				public void preAddEvent(Collection<Player> toBeAdded, Collection<Player> whereToAdd) {
					if (toBeAdded.size() == 0) return;
					for (Player player : toBeAdded) {					
						if (isHumanPlayer(player)) continue;
						if (player.isSpectator()) continue;
						
						if (log != null && log.isLoggable(Level.FINE)) {
							log.fine(config.getMatchId().getToken() + ": New bot connected to GB2004. Bot[unrealId=" + player.getId().getStringId() + ", name=" + player.getName() + "], binding its unrealId to botId " + connecting.get().getToken() + "...");
						}
						// ADD BINDING BETWEEN MATCH_BOT_ID <-> UT2004_PLAYER_ID
						bots.nativeBotId2UnrealId.put(connecting.get(), player.getId());
						bots.nativeUnrealId2BotId.put(player.getId(), connecting.get());
						bots.names.put(player.getId(), player.getName());
						
						latch.get().countDown();
					}
				}
				@Override
				public void preRemoveEvent(Collection<Player> toBeRemoved, Collection<Player> whereToRemove) {
					if (toBeRemoved.size() == 0) return;
					boolean bot = false;
					for (Player player : toBeRemoved) {
						if (isHumanPlayer(player)) continue;
						if (player.isSpectator()) continue;
						bot = true;
					}
					if (!bot) return;
					if (log != null && log.isLoggable(Level.WARNING)) {
						StringBuffer sb = new StringBuffer();
						sb.append(config.getMatchId().getToken() + ": Bot(s) removed from GB2004!!!");
						boolean first = true;
						for (Player plr : toBeRemoved) {
							if (first) first = false;
							else sb.append(", ");
							sb.append("Bot[unrealId=" + plr.getId().getStringId() + ", name=" + plr.getName() + "]");
						}
						log.warning(sb.toString());
					}
					throw new PogamutException("(NativeBot connecting) There can't be any 'removes' at this stage.", log, this);
				}
			};
			server.getPlayers().addCollectionListener(playerListener);
			
			boolean exception = false;
			try {
				for (UT2004NativeBotConfig botConfig : config.getNativeBots().values()) {
					if (log != null && log.isLoggable(Level.FINE)) {
						log.fine(config.getMatchId().getToken() + ": Starting native Bot[botId=" + botConfig.getBotId().getToken() + "]...");
					}
					
					// CHECK THE SERVER STATE
					if (server.notInState(IAgentStateUp.class)) {
						throw new PogamutException("(NativeBot connecting) Server is DEAD! Some previous exception will have the explanation...", log, this);
					}
					// OKEY, WE'RE STILL HERE
					
					// INITIALIZE THE LATCH
					latch.set(new CountDownLatch(1));
					
					// SET WHICH BOTID WE'RE GOING TO CONNECT NOW
					connecting.set(botConfig.getBotId());
					
					// ADD NATIVE BOT
					AddBot addBotCommand = new AddBot().setSkill(botConfig.getBotSkill());
					Integer teamNumber = botConfig.getBotTeam();
					addBotCommand.setTeam(teamNumber);
					server.getAct().act(addBotCommand);
					
					// WAIT TILL WE CATCH HIS ID
					latch.get().await(2 * 60 * 1000, TimeUnit.MILLISECONDS);
					if (latch.get().getCount() > 0) {
						// LATCH TIMEOUT!
						throw new PogamutException("(NativeBot connecting) We're tried to start up native bot " + botConfig.getBotId().getToken() + ", but it does not showed up on server for 2 minutes... either failed to start, or " + server + " failed to got it from GB2004.", log, this);
					}
					
					// SUCCESS! THE BOT IS UP AND RUNNING
					if (log != null && log.isLoggable(Level.INFO)) {
						log.info(config.getMatchId().getToken() + ": Started native Bot[botId=" + botConfig.getBotId().getToken() + ", unrealId=" + bots.getUnrealId(botConfig.getBotId()).getStringId() + "].");
					}
					
					// CHANGE ITS TEAM IF REQUIRED
					if (botConfig.getBotTeam() >= 0 && botConfig.getBotTeam() < MAX_TEAMS) {
						changeBotTeam(server, bots.getUnrealId(botConfig.getBotId()), botConfig.getBotTeam());
					}
				}
				if (bots.nativeBotId2UnrealId.size() != config.getNativeBots().size()) {
					throw new PogamutException("(NativeBot connecting) Not all mappings BotId<->UnrealId has been created. **PUZZLING**", log, this);
				}
				if (bots.nativeUnrealId2BotId.size() != config.getNativeBots().size()) {
					throw new PogamutException("(NativeBot connecting) Not all mappings UnrealId<->BotId has been created. **PUZZLING**", log, this);
				}
			} catch (Exception e) {
				exception = true;
				if (e instanceof PogamutException) throw (PogamutException)e;
				throw new PogamutException("(NativeBot connecting) Can't start all native bots! Exception happened while starting " + connecting.get().getToken() + ".", e, log, this);
			} finally {
				if (exception) {
					// some bullshit has happened, we have to tear it all down
					for (UT2004BotExecution execution : bots.bots.values()) {
						execution.stop();
					}
					for (UnrealId id : bots.nativeUnrealId2BotId.keySet()) {
						try {
							server.getAct().act(new Kick(id));
						} catch (Exception e) {
						}
					}
				}
				// drop the listener, not needed anymore
				server.getPlayers().removeCollectionListener(playerListener);
			}
			// SUCCESS ALL NATIVE BOTS ARE RUNNING AS THEY SHOULD
		}
		
		// COOL WE HAVE ALL BOTS UP AND RUNNING
		if (log != null && log.isLoggable(Level.INFO)) {
			log.info(config.getMatchId().getToken() + ": All custom & native bots are up and running in correct teams..");
		}
		
		return bots;
	}
	
	/**
	 * Usually STEP 5.2 ... wait till all human players gets connected to the game.
	 * <p><p>
	 * Raises exception in case of any error.
	 * <p><p>
	 * Note that the inner implementation is pretty complex... if you need to alter the method, copy-paste
	 * it and dig in.
	 * 
	 * @param server MUST NOT BE NULL
	 */
	protected void waitHumanPlayers(UT2004Server server, Bots bots) {
		int humanCount = config.getHumans() == null ? 0 : config.getHumans().size();
		
		if (humanCount == 0) {
			if (log != null && log.isLoggable(Level.INFO)) {
				log.info(config.getMatchId().getToken() + ": No humans should participate within the match.");
			}
			return;
		}
		
		if (log != null && log.isLoggable(Level.INFO)) {
			log.info(config.getMatchId().getToken() + ": Waiting for " + humanCount + " human non-spectator players to join the game.");
		}
		
		// DETERMINE WHICH TEAMS HUMANS SHOULD BE ON
		Map<Integer, Integer> humansInTeam = new LazyMap<Integer, Integer>() {
			@Override
			protected Integer create(Integer key) {
				return 0;
			}			
		};
		for (UT2004HumanConfig human : config.getHumans().values()) {
			if (isTeamMatch()) {
				humansInTeam.put(human.getTeamNumber(), humansInTeam.get(human.getTeamNumber()) + 1);
			} else {
				humansInTeam.put(0, humansInTeam.get(0) + 1);
			}
		}
		
		// BUSY WAITING!
		
		boolean connected = false;
		
		int waiting = 0;
		int humansConnected = 0;
		
		while (waiting < 4 * 60 * 10) { // 10 minutes timeout
			
			humansConnected = 0;
			
			Map<Integer, Integer> toConnect = new LazyMap<Integer, Integer>() {
				@Override
				protected Integer create(Integer key) {
					return 0;
				}				
			};
			for (Integer teamKey : humansInTeam.keySet()) {
				toConnect.put(teamKey, 0);
			}
			
			for (Player player : server.getPlayers()) {
				if (!isHumanPlayer(player)) continue;
				if (player.isSpectator()) continue;
				++humansConnected;				
				toConnect.put(isTeamMatch() ? player.getTeam() : 0, toConnect.get(isTeamMatch() ? player.getTeam() : 0) + 1);
			}
						
			if (humansConnected == humanCount) {
				connected = true;
				for (Entry<Integer, Integer> connectedTo : toConnect.entrySet()) {
					if (connectedTo.getValue() != humansInTeam.get(connectedTo.getKey())) connected = false;
				}
				if (connected) {
					break;
				}
			}
			
			if (waiting % 8 == 0) {
				String teamReport = "";
				List<Integer> teamKeys = new ArrayList<Integer>(toConnect.keySet());
				Collections.sort(teamKeys);
				boolean first = true;
				for (Integer teamKey : teamKeys) {
					if (first) first = false;
					else teamReport += ", ";
					teamReport += "Team" + teamKey + "[" + toConnect.get(teamKey) + "/" + humansInTeam.get(teamKey) + "]";
				}
				if (log != null && log.isLoggable(Level.INFO)) {
					log.info(config.getMatchId().getToken() + ": " + humansConnected + " / " + humanCount + " | " + teamReport + " human non-spectator(s) connected, waiting ...");
				}
			}
			
			++waiting;
			
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
			}
		}
		
		humansConnected = 0;
		List<IToken> humansToAssign = new ArrayList<IToken>(config.getHumans().keySet()); 
		for (Player player : server.getPlayers()) {
			if (!isHumanPlayer(player)) continue;
			if (player.isSpectator()) continue;
			++humansConnected;
			if (humansToAssign.size() == 0) continue;
			IToken humanToken = humansToAssign.remove(0);
			bots.humanUnrealId2HumanId.put(player.getId(), humanToken);
			bots.humanId2UnrealId.put(humanToken, player.getId());
			bots.names.put(player.getId(), player.getName());
			if (log != null && log.isLoggable(Level.INFO)) {
				log.info(config.getMatchId().getToken() + ": Human id assigned. " + humanToken.getToken() + " -> Player[id=" + player.getId() + ", name=" + player.getName() + "].");
			}
			if (isTeamMatch()) {
				if (player.getTeam() != config.getHumans().get(humanToken).getTeamNumber()) {
					// SHOULD WE DO ANYTHING? OR LET IT BE?
				}
			}
		}
		
		if (humansConnected != humanCount) {
			throw new PogamutException("Timeout (10 minutes)! " + humansConnected + " / " + humanCount + " human non-spectator(s) connected, invalid, could not start the match!", log, this);
		}
		
		if (log != null && log.isLoggable(Level.INFO)) {
			log.info(config.getMatchId().getToken() + ": " + humanCount + " human non-spectator players joined the game.");
		}
	}
	
	/**
	 * Optional (usually) STEP 6 ... it is nice to collect some data about bots, that is done by the analyzer that automatically
	 * observe any custom-bot.
	 * <p><p>
	 * This method may need to be override to provide custom implementation of {@link UT2004Analyzer} interface, i.e.,
	 * provide your custom control server. Current implementation is using {@link UT2004Analyzer}.
	 * <p><p>
	 * Fills data {@link Bots#botObservers}.
	 * <p><p>
	 * Raises exception in case of any error.
	 * 
	 * @param ucc MUST NOT BE NULL
	 * @param bots
	 * @param outputDirectory where to output data about bots, MUST NOT BE NULL
	 * @param humanLikeObserving whether to produce output for "HumanLikeBot project" analysis 
	 * @return
	 */
	protected UT2004Analyzer startAnalyzer(UCCWrapper ucc, Bots bots, File outputDirectory, boolean humanLikeObserving) {		
		if (log != null && log.isLoggable(Level.FINE)) {
			log.fine(config.getMatchId().getToken() + ": Starting UT2004Analyzer" + (humanLikeObserving ? "[humanLikeObserving enabled]" : "") + "...");
		}
		NullCheck.check(ucc, "ucc");
		NullCheck.check(bots, "bots");
		NullCheck.check(outputDirectory, "outputDirectory");
		
		UT2004AnalyzerModule module = new UT2004AnalyzerModule();
		UT2004AnalyzerFactory factory = new UT2004AnalyzerFactory<UT2004Analyzer, UT2004AgentParameters>(module);
		
		Map<UnrealId, String> fileNames = new HashMap<UnrealId, String>();
		Iterators<Entry<UnrealId, IToken>> entryIter = new Iterators<Entry<UnrealId, IToken>>(bots.unrealId2BotId.entrySet(), bots.nativeUnrealId2BotId.entrySet(), bots.humanUnrealId2HumanId.entrySet());
		for (Entry<UnrealId, IToken> entry : entryIter) {
			String name = bots.names.get(entry.getKey());
			if (name != null) {
				fileNames.put(entry.getKey(), entry.getValue().getToken() + "-" + FilePath.getValidFileName(name));
			} else {
				fileNames.put(entry.getKey(), entry.getValue().getToken());
			}
		}
		
		UT2004Analyzer analyzer = (UT2004Analyzer)factory.newAgent(
			new UT2004AnalyzerParameters()
				.setAgentId(new AgentId(config.getMatchId().getToken()+"-UT2004Analyzer"))
				.setWorldAddress(ucc.getServerAddress())
				.setObserverModule(new UT2004AnalyzerObsStatsModule())
				.setObserverAddress(ucc.getObserverAddress())
				.setWaitForMatchRestart(true)
				.setOutputPath(getOutputPath("bots").getAbsolutePath())
				.setFileNames(fileNames)
				.setHumanLikeObserving(humanLikeObserving)
		);
		analyzer.getLogger().setLevel(Level.WARNING);
		analyzer.getLogger().addDefaultConsoleHandler();
		
		final CountDownLatch observersLatch = new CountDownLatch(bots.unrealId2BotId.size() + bots.nativeUnrealId2BotId.size() + bots.humanUnrealId2HumanId.size());
		final Bots myBots = bots;
		
		IAnalyzerObserverListener observerListener = new IAnalyzerObserverListener() {

			@Override
			public void observerAdded(UnrealId botId, IUT2004AnalyzerObserver observer) {
				BotType botType = myBots.getType(botId);
				if (botType == null) {
					// NOT MINE BOT... IGNORE... probably spectator
					return;
				}
				switch (botType) {
				case BOT:
					myBots.botObservers.put(myBots.unrealId2BotId.get(botId), observer);
					break;
				case NATIVE:
					myBots.botObservers.put(myBots.nativeUnrealId2BotId.get(botId), observer);
					break;
				case HUMAN:
					myBots.botObservers.put(myBots.humanUnrealId2HumanId.get(botId), observer);
					break;
				default: 
					if (log != null && log.isLoggable(Level.WARNING)) {
						log.warning(config.getMatchId().getToken() + ": Unknown type of Player[id=" + botId.getStringId() + "], tracked neither as BOT nor NATIVE nor HUMAN.");
					}
					return;
				}				
				observersLatch.countDown();
			}

			@Override
			public void observerRemoved(UnrealId botId, IUT2004AnalyzerObserver observer) {
				// can happen for "spectators"
			}
			
		};
		
		analyzer.addListener(observerListener);
		analyzer.start();

		try {
			observersLatch.await(5 * 60 * 1000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			analyzer.removeListener(observerListener);
			analyzer.kill();
			throw new PogamutInterruptedException("Interrupted while awaiting for observers to be setup for bots.", log, this);
		}
		
		analyzer.removeListener(observerListener);
		
		if (observersLatch.getCount() > 0) {
			analyzer.kill();
			throw new PogamutException("Timeout (5min) - not all observers has been attached within 5 minutes.", log, this);
		}
		
		if (analyzer.notInState(IAgentStateUp.class)) {
			analyzer.kill();
			throw new PogamutException("After all observers have been started, analyzer was found DEAD... :-(", log, this);
		}
		
		if (bots.botObservers.size() != (config.getBots().size() + config.getNativeBots().size() + config.getHumans().size())) {
			analyzer.kill();
			throw new PogamutException("Not all observers have been attached to running custom bots :-( - " + "bots.botObservers.size() = " + bots.botObservers.size() + "; config.getBots().size() = " + config.getBots().size() + "; config.getNativeBots().size() = " + config.getNativeBots().size() + "; config.getHumans().size() = " + config.getHumans().size(), log, this);
		}

		if (log != null && log.isLoggable(Level.INFO)) {
			log.info(config.getMatchId().getToken() + ": UT2004Analyzer started.");
		}
		
		return analyzer;
	}
	
	/**
	 * Optional (usually) STEP 7 ... set up any listeners needed.
	 * <p><p>
	 * Current implementation is empty.
	 * 
	 * @param ucc MUST NOT BE NULL
	 * @param server MUST NOT BE NULL
	 * @param analyzer may be null
	 * @param bots MUST NOT BE NULL
	 */
	protected void matchIsAboutToBegin(UCCWrapper ucc, UT2004Server server, UT2004Analyzer analyzer, Bots bots) {
	}
	
	/**
	 * Usually STEP 8 ... restarts the match (in order to reinitialize the environment and restart all bots).
	 * <p><p>
	 * Blocks until the match is restarted.
	 * <p><p>
	 * Raises exception in case of any error.
	 * 
	 * @param server MUST NOT BE NULL
	 */
	protected void restartMatch(UT2004Server server, Bots bots) {
		if (log != null && log.isLoggable(Level.FINE)) {
			log.fine(config.getMatchId().getToken() + ": Restarting match...");
		}
		
		NullCheck.check(server, "server");
		
		final BusAwareCountDownLatch latch = new BusAwareCountDownLatch(1, server.getEventBus(), server.getWorldView());
		IWorldEventListener<GameRestarted> listener = new IWorldEventListener<GameRestarted>() {
			@Override
			public void notify(GameRestarted event) {
				if (event.isFinished()) {
					latch.countDown();
				}
			}
		};
		
		if (bots.botObservers.size() > 0) {
			if (log != null && log.isLoggable(Level.FINE)) {
				log.fine(config.getMatchId().getToken() + ": Waiting for 5 seconds, to give GB2004 time to initialize observers...");
			}
		}
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			throw new PogamutInterruptedException("Interrupted while giving GB2004 time to hook up listeners...", log, this);
		}
		
		server.getWorldView().addEventListener(GameRestarted.class, listener);
		server.getAct().act(new GameConfiguration().setRestart(true));
		
		latch.await(5 * 60 * 1000, TimeUnit.MILLISECONDS);
		
		if (latch.getCount() > 0) {
			throw new PogamutException("Restart was not successful, event GameRestarted[finished==true] was not received.", log, this);
		}
		
		bots.matchStart = System.currentTimeMillis();
		
		if (log != null && log.isLoggable(Level.INFO)) {
			log.info(config.getMatchId().getToken() + ": Match restarted.");
		}
	}
	
	/**
	 * Optional (usually) STEP 9 ... begin recording UT2004 replay
	 * @param server
	 * @param fileName
	 */
	protected void recordReplay(UT2004Server server, String fileName) {
		if (log != null && log.isLoggable(Level.INFO)) {
			log.info(config.getMatchId().getToken() + ": Recording replay " + fileName);
		}
		server.getAct().act(new Record(fileName));
	}
	
	/**
	 * Usually STEP 10 ... you wait for a predefined time (blocking method) for the match to finish.
	 * <p><p>
	 * This method may need to be override to provide correct MATCH-FINISHED detecting routine.
	 * <p><p>
	 * Always abide the timeout!
	 * <p><p>
	 * Don't forget to observe whether all 'custom bots' are running! Use {@link UT2004BotExecution#getRunning()} flag and {@link FlagListener}.
	 * <p><p>
	 * Raises exception in case of any error / timeout
	 * 
	 * @param ucc MUST NOT BE NULL
	 * @param server MUST NOT BE NULL
	 * @param analyzer may be null
	 * @param bots MUST NOT BE NULL
	 * @param timeoutInMillis must be specified correctly
	 * 
	 * @return who has won (or null in case of failure / timeout)
	 */
	protected abstract UT2004MatchResult waitMatchFinish(UCCWrapper ucc, UT2004Server server, UT2004Analyzer analyzer, Bots bots, long timeoutInMillis);
	
	/**
	 * Usually STEP 10.1
	 * <p><p>
	 * Overwrites current UT2004.ini with UT2004.ini.backup that was created during {@link UT2004Match#createUT2004Ini()}.
	 */
	public void restoreUT2004IniBackup() {
		File ut2004File = getUT2004IniFile();
		if (ut2004FileBackup == null) {
			if (log != null && log.isLoggable(Level.WARNING)) {
				log.warning(config.getMatchId().getToken() + ": Could not restore UT2004.ini file, missing backup file reference, ut2004FileBackup == null.");
			}
			return;
		}
		if (ut2004FileBackup.isFile() && ut2004FileBackup.exists()) {
			if (log != null && log.isLoggable(Level.FINE)) {
				log.fine(config.getMatchId().getToken() + ": Restoring " + ut2004File.getAbsolutePath() + " into + " + ut2004FileBackup.getAbsolutePath() + " ...");
			}
			boolean restore = true;
			try {
				FileUtils.copyFile(ut2004FileBackup, ut2004File);
			} catch (IOException e) {
				restore = false;
			}
			if (restore && log != null && log.isLoggable(Level.FINE)) {
				log.fine(config.getMatchId().getToken() + ": UT2004.ini restored from " + ut2004FileBackup.getAbsolutePath() + ".");
			} else
			if (!restore && log != null && log.isLoggable(Level.SEVERE)){
				log.severe(config.getMatchId().getToken() + ": Failed to restore up UT2004.ini from " + ut2004FileBackup.getAbsolutePath() + " into + " + ut2004File.getAbsolutePath() + " !!!");
				
			}
		}
	}
	
	/**
	 * Usually STEP 10.2
	 * <p><p>
	 * Overwrites current GameBots2004.ini with GameBots2004.ini.backup that was created during {@link UT2004Match#createGB2004Ini()}.
	 */
	public void restoreGB2004IniBackup() {
		File gb2004File = getGB2004IniFile();
		if (gb2004FileBackup == null) {
			if (log != null && log.isLoggable(Level.WARNING)) {
				log.warning(config.getMatchId().getToken() + ": Could not restore GameBots2004.ini file, missing backup file reference, gb2004FileBackup == null.");
			}
			return;
		}
		if (gb2004FileBackup.isFile() && gb2004FileBackup.exists()) {
			if (log != null && log.isLoggable(Level.FINE)) {
				log.fine(config.getMatchId().getToken() + ": Restoring " + gb2004File.getAbsolutePath() + " into + " + gb2004FileBackup.getAbsolutePath() + " ...");
			}
			boolean restore = true;
			try {
				FileUtils.copyFile(gb2004FileBackup, gb2004File);
			} catch (IOException e) {
				restore = false;
			}
			if (restore && log != null && log.isLoggable(Level.FINE)) {
				log.fine(config.getMatchId().getToken() + ": GameBots2004.ini restored from " + gb2004FileBackup.getAbsolutePath() + ".");
			} else
			if (!restore && log != null && log.isLoggable(Level.SEVERE)){
				log.severe(config.getMatchId().getToken() + ": Failed to restore up GameBots2004.ini from " + gb2004FileBackup.getAbsolutePath() + " into + " + gb2004File.getAbsolutePath() + " !!!");
				
			}
		}
	}
	
	/**
	 * Optional (usually) STEP 11 ... moves replay file to desired directory.  
	 * @param ucc
	 * @param fileName
	 */
	protected void copyReplay(UCCWrapper ucc, String fileName, File outputDirectory) {
		if (log != null && log.isLoggable(Level.FINE)) {
			log.fine(config.getMatchId().getToken() + ": Copying replay file into " + outputDirectory.getAbsolutePath());
		}
		File replayFile = new File(ucc.getConfiguration().getUnrealHome() + File.separator + "Demos" + File.separator + fileName + ".demo4");
		File destination = new File(outputDirectory.getAbsoluteFile() + File.separator + "match-" + config.getMatchId().getToken() + "-replay.demo4");
		FilePath.makeDirsToFile(destination);
		boolean ex = false;
		try {
			FileUtils.copyFile(replayFile, destination);
		} catch (IOException e) {
			ex = true;
			if (log != null) {
				log.warning(config.getMatchId().getToken() + ": Failed to copy replay file from: " + replayFile.getAbsolutePath() + " into " + destination.getAbsolutePath() + ": " + e.getMessage());
			}
		}		
		if (!ex) {
			if (log != null && log.isLoggable(Level.INFO)) {
				log.info(config.getMatchId().getToken() + ": Replay copied into " + destination.getAbsolutePath());
			}
		}
	}
	
	/**
	 * Usually STEP 12 ... concludes the match by producing/presenting whatever statistics needed about the match. Everything
	 * should be outputted to 'outputDirectory'.
	 * <p><p>
	 * Raises exception in case of any error / timeout
	 * 
	 * @param ucc MUST NOT BE NULL
	 * @param server MUST NOT BE NULL
	 * @param analyzer may be null 
	 * @param bots MUST NOT BE NULL
	 * @param result MUST NOT BE NULL
	 * @param outputDirectory MUST NOT BE NULL
	 */
	protected abstract void outputResults(UCCWrapper ucc, UT2004Server server, UT2004Analyzer analyzer, Bots bots, UT2004MatchResult result, File outputDirectory);
	
	/**
	 * Usually STEP 13 ... shutdowns everything
	 * @param ucc
	 */
	protected void shutdownAll(UCCWrapper ucc, UT2004Server server, UT2004Analyzer analyzer, Bots bots) {
		if (log != null && log.isLoggable(Level.FINE)) {
			log.fine(config.getMatchId().getToken() + ": Shutting down everything...");
		}
		
		if (ucc != null) {
			try {
				if (log != null && log.isLoggable(Level.INFO)) {
					log.info(config.getMatchId().getToken() + ": Killing UCC...");
				} 
			} catch (Exception e) {				
			}
			try {
				ucc.stop();
			} catch (Exception e) {					
			}
		}
		if (server != null) {
			try {
				if (log != null && log.isLoggable(Level.INFO)) {
					log.info(config.getMatchId().getToken() + ": Killing UT2004Server...");
				} 
			} catch (Exception e) {				
			}
			try {
				server.kill();
			} catch (Exception e) {					
			}
		}
		if (bots != null) {
			try {
				if (log != null && log.isLoggable(Level.INFO)) {
					log.info(config.getMatchId().getToken() + ": Killing Custom bots...");
				} 
			} catch (Exception e) {				
			}
			if (bots.bots != null) {
				for (UT2004BotExecution exec : bots.bots.values()) {
					try {
						exec.stop();					
					} catch (Exception e) {					
					}
				}
			}
			try {
				if (log != null && log.isLoggable(Level.INFO)) {
					log.info(config.getMatchId().getToken() + ": Killing Custom bot observers...");
				} 
			} catch (Exception e) {				
			}
			if (bots.botObservers != null) {
				for (IUT2004AnalyzerObserver obs : bots.botObservers.values()) {
					try {
						obs.kill();
					} catch (Exception e) {						
					}
				}
			}
		}
		if (analyzer != null) {
			try {
				if (log != null && log.isLoggable(Level.INFO)) {
					log.info(config.getMatchId().getToken() + ": Killing UT2004Analyzer...");
				} 
			} catch (Exception e) {				
			}
			try {
				analyzer.kill();
			} catch (Exception e) {					
			}
		}	
		
		if (log != null && log.isLoggable(Level.INFO)) {
			log.info(config.getMatchId().getToken() + ": UCC, Bots, UT2004Server, UT2004Analyzer + observers were shut down.");
		}
	}
	
	/**
	 * Usually STEP 14 that close logging to file.
	 */
	protected void closeLogger() {
		if (log != null && log.isLoggable(Level.WARNING)) {
			log.warning(config.getMatchId().getToken() + ": Closing file output...");
		}
		if (fileHandler != null) {
			if (log != null) {
				log.removeHandler(fileHandler);
			}
			fileHandler.close();
			fileHandler = null;
		}
		if (log != null && log.isLoggable(Level.WARNING)) {
			log.warning(config.getMatchId().getToken() + ": Logging to file stopped.");
		}		
	}
}
