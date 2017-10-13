package cz.cuni.amis.pogamut.ut2004.agent.module.sensor;

import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Formatter;
import java.util.Map;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.IObservingAgent;
import cz.cuni.amis.pogamut.base.agent.module.SensorModule;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.analyzer.UT2004AnalyzerObserver;
import cz.cuni.amis.pogamut.ut2004.analyzer.stats.UT2004AnalyzerObsStats;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Shoot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BeginMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ControlMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.EndMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.FlagInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameRestarted;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemPickedUp;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerScore;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.TeamScore;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.TeamScoreMessage;
import cz.cuni.amis.utils.FilePath;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.exception.PogamutIOException;
import cz.cuni.amis.utils.maps.LazyMap;

/**
 * Memory module specialized on collecting various statistics about the bot such as number of deaths, suicides,
 * etc.
 * <p><p>
 * The module abides {@link GameRestarted} (clear stats upon the restart) and can be set to export collected 
 * data into certain file. 
 * 
 * @author Jimmy
 */
public class AgentStats extends SensorModule<IObservingAgent> {

	protected boolean observer = false;
	
	/**
	 * Whether the module is being used inside observer. Default: FALSE.
	 * @return
	 */
	public boolean isObserver() {
		return observer;
	}
	
	/**
	 * Sets whether the module is being used inside observer. Default: FALSE.
	 * @param observer
	 */
	public void setObserver(boolean observer) {
		this.observer = observer;
	}
	
	/**
	 * Returns Id of the bot, sensitive to {@link AgentStats#isObserver()}.
	 * @return
	 */
	public UnrealId getBotId() {
		if (self == null) return null;		
		return self.getBotId();
	}

	/**
	 * Contains statistics about bot that was KILLED BY OBSERVED BOT.
	 */
	protected Map<UnrealId, Integer> killed = new LazyMap<UnrealId, Integer>() {

		@Override
		protected Integer create(UnrealId key) {
			return 0;
		}
		
	};
	
	/**
	 * Contains statistics about bots that KILLED OBSERVED BOT. Does not contain "suicidies", i.e., value for the key
	 * that is this bot ID.
	 */
	protected Map<UnrealId, Integer> killedBy = new LazyMap<UnrealId, Integer>() {

		@Override
		protected Integer create(UnrealId key) {
			return 0;
		}
		
	};
	
	 /** 
	  * Most rescent message containing info about the player's score. 
	  **/
	protected Map<UnrealId, PlayerScore> playerScores = new LazyMap<UnrealId, PlayerScore>() {

		@Override
		protected PlayerScore create(UnrealId key) {
			return new PlayerScore(currentUT2004Time, key, 0, 0);
		}
		
	};
	
	/** 
	 * Most rescent message containing info about the player team's score. 
	 **/
	protected Map<Integer, TeamScore> teamScores = new LazyMap<Integer, TeamScore>() {

		@Override
		protected TeamScore create(Integer key) {
			return new TeamScoreMessage();
		}
		
	};

	
	/**
     * How many times the observed bot has died.
     */
    protected int deaths = 0;
    
    /**
     * How many times this bot has comitted suicide.
     */
    protected int suicides = 0;
        
    /**
     * How many times was this bot killed by other players.
     */
    protected int killedByOthers = 0;
    
    /**
     * How many times this bot has killed other players.
     */
    protected int killedOthers = 0;
    
    /**
     * Sum of the bot's travelled distance.
     */
    protected double travelledDistance = 0.0;
    
    /**
     * Whether the bot is shooting.
     */
    protected boolean shooting = false;
    
    /**
     * For how long in total the bot was shooting.
     */
    protected double timeShooting = 0;
    
    /**
     * For how long in total the bot was moving (had non-zero (&gt; 1) velocity)
     */
    protected double timeMoving = 0;
    
    /**
     * For how long the bot was shooting with a certain weapon, truly counting the number of seconds the {@link Shoot} command
     * has been effective for the bot.
     */
    protected Map<ItemType, Double> weaponsUsedTime = new LazyMap<ItemType, Double>() {

		@Override
		protected Double create(ItemType key) {
			return (double)0;
		}
    	
    };

	/**
	 * How many items the bot has collected according to their item type.
	 */
	protected Map<ItemType, Integer> itemsCollected = new LazyMap<ItemType, Integer>() {

		@Override
		protected Integer create(ItemType key) {
			return 0;
		}
		
	};
	
	/**
	 * How many items according to its {@link Category} the bot has collected.
	 */
	protected Map<ItemType.Category, Integer> itemsByCategoryCollected = new LazyMap<ItemType.Category, Integer>() {

		@Override
		protected Integer create(ItemType.Category key) {
			return 0;
		}
		
	};
	
	/**
	 * Total number of items the bot has collected.
	 */
	protected int totalItemsCollected = 0;
	
	/**
	 * How many other players this bot has killed during its single life.
	 */
	protected int numberOfPlayersKilledWithoutDeath = 0;
	
	/**
	 * How many times the bot had double damage.
	 */
	protected int doubleDamageCount = 0;
	
	/**
	 * Total number of seconds the bot had double damage.
	 */
	protected double doubleDamageTime = 0;
	
	////
	//
	// LOGGING
	//
	////

	/**
	 * Outputs stats headers into the 'output'.
	 * 
	 * @param output
	 */
	public void outputHeader(Formatter output) {
		if (output == null) return;
		synchronized(output) {
			output.format("MatchTime;UT2004Time;Health;Armor;Adrenaline;Score;Deaths;Suicides;Killed;WasKilled;NumKillsWithoutDeath;"
					//      1            1.1       2      3      4         5      6       7       8       9        10          
					     +"Team;TeamScore;"
					//      11     12
					     +"ItemsCollect;WeaponsCollect;AmmoCollect;HealthCollect;ArmorCollect;ShieldCollect;AdrenalineCollect;OtherCollect;"				     
	                //        13          14              15             16           17             18           19              20
					     +"TimeMoving;TimeShooting;DoubleDamageCount;DoubleDamageTime;TraveledDistance;"
					//         21           22               23              24              25     
					     +"Combo;HasDoubleDamage;IsShooting;Velocity;Location_x;Location_y;Location_z");
				    //      26         27          28          29         30         31      32
			// WEAPON USED
			for (ItemType weapon : ItemType.Category.WEAPON.getTypes()) {
				output.format(";" + fixSemicolon(weapon.getName()).replace(".", "_") + "_TimeUsed");
			}
			// EVENTS
			output.format(";Event;EventParams...\n");
			output.flush();
		}
	}       
	
	/**
	 * Outputs stats line into 'output' appending 'eventOutput' at the end (also separated by semicolons)
	 * 
	 * @param output
	 * @param eventOutput
	 */
	public void outputStatLine(Formatter output, double time, String... eventOutput) {
		if (output == null) return;
		synchronized(output) {
			output.format("%.3f;%.3f;%d;%d;%d;%d;%d;%d;%d;%d;%d;%d;%d;%d;%d;%d;%d;%d;%d;%d;%d;%.3f;%.3f;%d;%.3f;%.3f;%s;%d;%d;%.3f;%.3f;%.3f;%.3f", 
					      // 1  1.1   2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20  21   22  23  24  25   26 27 28  29   30   31   32  
					           time,                                      // 1
					           getCurrentUT2004Time(),                    // 1.1
					           (self == null ? 0 : self.getHealth()),     // 2
					           (self == null ? 0 : self.getArmor()),      // 3
					           (self == null ? 0 : self.getAdrenaline()), // 4
					           getScore(),           // 5
					           deaths,               // 6
					           suicides,             // 7
					           killedOthers,         // 8
					           killedByOthers,       // 9
					           numberOfPlayersKilledWithoutDeath,     // 10
					           (self == null ? 255 : self.getTeam()), // 11
					           getTeamScore(),       // 12
					           totalItemsCollected,  // 13
					           itemsByCategoryCollected.get(ItemType.Category.WEAPON),     // 14
					           itemsByCategoryCollected.get(ItemType.Category.AMMO),       // 15
					           itemsByCategoryCollected.get(ItemType.Category.HEALTH),     // 16
					           itemsByCategoryCollected.get(ItemType.Category.ARMOR),      // 17
					           itemsByCategoryCollected.get(ItemType.Category.SHIELD),     // 18
					           itemsByCategoryCollected.get(ItemType.Category.ADRENALINE), // 19
					           itemsByCategoryCollected.get(ItemType.Category.OTHER),      // 20
					           timeMoving,           // 21
					           timeShooting,         // 22
					           doubleDamageCount,    // 23
					           doubleDamageTime,     // 24
					           travelledDistance,    // 25
					           (self == null ? "" : fixSemicolon(self.getCombo())),    // 26
					           (self == null ? 0 : self.getUDamageTime() > 0 ? 1 : 0), // 27
					           (self == null ? 0 : (self.isShooting() || self.isAltFiring()) ? 1 : 0), // 28
					           (self == null ? (double)0 : self.getVelocity().size()), // 29
					           (self == null ? (double)0 : self.getLocation().x),      // 30
					           (self == null ? (double)0 : self.getLocation().y),      // 31
					           (self == null ? (double)0 : self.getLocation().z)       // 32
			);
			// WEAPON USED
			for (ItemType weapon : ItemType.Category.WEAPON.getTypes()) {
				output.format(";%.3f", weaponsUsedTime.get(weapon));
			}
			// EVENTS
			for (String event : eventOutput) {
				output.format(";%s", fixSemicolon(event));
			}
			output.format("\n");
	        output.flush();
		}
	}
	
	/**
	 * Outputs stats line with event IFF logging (i.e., {@link AgentStats#outputFile} is not null, was initialized by {@link AgentStats#startOutput(String)} or {@link AgentStats#startOutput(String, boolean)}).
	 * @param eventOutput
	 */
	protected void outputStatLine(double time, String... eventOutput) {
		if (!isLogging()) return;
		if (outputFile == null) return;
		outputStatLine(outputFile, time, eventOutput);		
	}
	
	protected String fixSemicolon(String text) {
		if (text == null) return "";
		return text.replace(";", "_");
	}
	
	////
	//
	// RESET STATS
	//
	////
	
	protected Object statsMutex = new Object();
	
	/**
	 * Reset all stats, DOES NOT RESET {@link AgentStats#getCurrentMatchTime()} (use {@link AgentStats#resetMatchTime()} for that purpose separately).
	 */
	public void resetStats() {
		synchronized (statsMutex) {
			self = null;
			deaths = 0;
			suicides = 0;
			killedOthers = 0;
			killedByOthers = 0;
			numberOfPlayersKilledWithoutDeath = 0;
			totalItemsCollected = 0;
			synchronized (itemsByCategoryCollected) {
				itemsByCategoryCollected.clear();
			}
			timeMoving = 0;
			timeShooting = 0;
			doubleDamageCount = 0;
			doubleDamageTime = 0;
			travelledDistance = 0;
			synchronized (itemsCollected) {
				itemsCollected.clear();
			}
			synchronized (weaponsUsedTime) {
				weaponsUsedTime.clear();
			}
			synchronized (playerScores) {
				playerScores.clear();
			}
			
			// UTILITY STUFF
			playerKilledInRow = 0;
			lastLocation = null;
		}
	}
	
	/**
	 * Resets match time to ZERO again (if is initialized, i.e., {@link AgentStats#getCurrentMatchTime()} > 0).
	 */
	public void resetMatchTime() {
		synchronized(statsMutex) {
			if (getCurrentMatchTime() > 0) {
				matchStartTime = getCurrentMatchTime();
			}
		}
	}
	
	////
	//
	// OUTPUT OF THE STATS
	//
	////
	
	/**
	 * Path to output as passed by the user.
	 */
	protected String pathToOutput = null;
	
	/**
	 * Concrete file we're currently using.
	 */
	protected File fileToOutput = null;
	
	/**
	 * Formatter that is used to output strings into the {@link UT2004AnalyzerObserver#observerFile}. 
	 */
    protected Formatter outputFile = null;
	
    /**
     * Whether the object is currently logging (it may be false while the match is being restarted).
     * @return
     */
	public boolean isOutputting() {
		return isLogging() && outputFile != null;
	}
	
	/**
	 * Returns the output path as supplied in {@link AgentStats#startOutput(String)} or {@link AgentStats#startOutput(String, boolean)}.
	 * <p><p>
	 * For concretely used file for the output, use {@link AgentStats#getOutputFile()}. 
	 * @return
	 */
	public String getOutputPath() {
		return pathToOutput;
	}
	
	/**
	 * Actually used file for outputting of stats.
	 * @return
	 */
	public File getOutputFile() {
		return fileToOutput;
	}

	/**
	 * Starts or redirect logging to 'pathToFile'. If it targets dir, throws an exception. Existing file will be overwritten.
	 * 
	 * @see setOutputPath(String, boolean)
	 * 
	 * @param pathToFile (target file format is .csv, should not need to end with any extension, will be supplied)
	 */
	public void startOutput(String pathToFile) {
		startOutput(pathToFile, false);
	}

	/**
	 * Stops outputting of stats into file, nullify {@link AgentStats#outputFile} and {@link AgentStats#fileToOutput}. 
	 * <p><p>
	 * After the call:
	 * <ul>
	 * <li>{@link AgentStats#isOutputting()} will report false</li>
	 * <li>{@link AgentStats#getOutputFile()} will report null</li>
	 * <li>{@link AgentStats#getOutputPath()} will still be reported the last path passed into {@link AgentStats#startOutput(String)} or {@link AgentStats#startOutput(String, boolean)}</li>
	 * </ul>
	 */
	public void stopOutput() {
		if (outputFile == null) return;
		synchronized(outputFile) {
			try {
				outputFile.close();
			} catch (Exception e) {				
			}
			outputFile = null;
			fileToOutput = null;
		}
	}
	
	/**
	 * Returns {@link File} for a given 'pathToFile'. Target file is either non-existent or is file (otherwise throws an exception).
	 * <p><p>
	 * If 'seekAlternatives' is true, the method will try to find another file if 'pathToFile' already exists by appending "_000", "_001", ...
	 * If alternative filename is found, it is returned. Otherwise throws an exception.
	 * 
	 * @param pathToFile
	 * @param seekAlternatives
	 * @return
	 */
	protected File getOutputFile(String pathToFile, boolean seekAlternatives) {
		NullCheck.check(pathToFile, "pathToFile");
		
		if (!seekAlternatives) {
			File file = new File(pathToFile);
			if (!file.exists() || file.isFile()) {
				return file;
			}
			throw new PogamutException("Can't output stats into " + file.getAbsolutePath() + ", invalid location.", this);
		}
		
		String fragment;
		String rest;
		if (pathToFile.contains(".")) {
			fragment = pathToFile.substring(0, pathToFile.lastIndexOf("."));
			rest = pathToFile.substring(pathToFile.lastIndexOf("."));			
		} else {
			fragment = pathToFile;
			rest = ".csv";
		}
		for (int i = 0; i < 1000; ++i) {
			String num = String.valueOf(i);
			while (num.length() < 3) {
				num = "0" + num;
			}
    		String fileName = fragment + "_" + num + rest;
    		File file = new File(fileName);
    		if (file.exists()) continue;
    		return file;
    	}
    	throw new PogamutException("No suitable filename for stats to the: " + pathToFile + "...", this);
	}
	
	/**
	 * Starts or redirect logging to 'pathToFile'. If it targets dir, throws an exception. Existing file will be overwritten.
	 * <p><p>
	 * If 'seekAlternatives' is true, the method will try to find another file if 'pathToFile' already exists by appending "_000", "_001", ...
	 * If alternative filename is found, the log is redirected there. Otherwise throws an exception.
	 * 
	 * @param pathToFile (target file format is .csv, should not need to end with any extension, will be supplied)
	 * @param seekAlternatives whether to try other file (using suffixes to file name '_000', '_001', ..., '_999'
	 */
	public void startOutput(String pathToFile, boolean seekAlternatives) {
		stopOutput();		
		this.pathToOutput = pathToFile;
		fileToOutput = getOutputFile(pathToFile, seekAlternatives);
		FilePath.makeDirsToFile(fileToOutput);
		try {
			outputFile = new Formatter(fileToOutput);
		} catch (FileNotFoundException e) {
			throw new PogamutIOException("Could not start logging into '" + fileToOutput.getAbsolutePath() + "' due to: " + e.getMessage(), e, this);
		}		
		outputHeader(outputFile);		
	}
	
	////
	//
	// STATS GETTERS
	//
	////
	
	/**
	 * Returns map that counts how many time your bot kills other bot (according to opponent bot ID), i.e., under
	 * the key (other bot ID) is "how many time your bot has billed the other bot".
	 */
	public Map<UnrealId, Integer> getKilled() {
		return killed;
	}

	/**
	 * Returns map with scores of respective players in the game.
	 * @return
	 */
	public Map<UnrealId, PlayerScore> getPlayerScores() {
		return playerScores;
	}

	/**
	 * Returns map with scores of respective teams in the game.
	 * @return
	 */
	public Map<Integer, TeamScore> getTeamScores() {
		return teamScores;
	}

	/**
	 * Current match time - note that this might different from the current GB2004 time because the match time
	 * is reset upon {@link GameRestarted}.
	 * <p><p>
	 * Returns negative number if it can't be evaluated, i.e., the match has not been started (and we're waiting for it,
	 * that happens only if you have used {@link AgentStats#setLogBeforeMatchRestart(boolean)}), or we do not have enough data.
	 * 
	 * @return
	 */
	public double getCurrentMatchTime() {
		if (isLogging() && currentUT2004Time > 0) {
			// assumes that UT2004 time is running in Secs (it should...)
			return currentUT2004Time - matchStartTime + ((System.currentTimeMillis() - currentSystemTime) / 1000);
		} else {
			return -1;
		}
	}
	
	/**
	 * Returns current UT2004 time (without any deltas, i.e., this is completely driven by the time from {@link BeginMessage}s).
	 * <p><p>
	 * Returns negative number if it can't be evaluated.
	 * 
	 * @return
	 */
	public double getCurrentUT2004Time() {
		return currentUT2004Time;
	}

	/**
	 * How many other bots your bot has killed so far.
	 * @return
	 */
	public int getKilledOthers() {
		return killedOthers;
	}

	/**
	 * Whether the bot is currently shooting
	 * @return
	 */
	public boolean isShooting() {
		return shooting;
	}

	/**
	 * For how long your bot was shooting (in total). In seconds.
	 * @return
	 */
	public double getTimeShooting() {
		return timeShooting;
	}

	/**
	 * For how long your bot was moving (in total). In seconds.
	 * @return
	 */
	public double getTimeMoving() {
		return timeMoving;
	}

	/**
	 * For how long your bot was using a certain weapon (in total). In seconds.
	 * @return
	 */
	public Map<ItemType, Double> getWeaponsUsedTime() {
		return weaponsUsedTime;
	}

	/**
	 * How many items (per {@link ItemType}) your bot has collected so far (in total).
	 * @return
	 */
	public Map<ItemType, Integer> getItemsCollected() {
		return itemsCollected;
	}

	/**
	 * How many items (per respective {@link Category}) your bot has collected so far.
	 * @return
	 */
	public Map<ItemType.Category, Integer> getItemsByCategoryCollected() {
		return itemsByCategoryCollected;
	}

	/**
	 * Total number of items the bot collected (including items received when the bot respawns).
	 * @return
	 */
	public int getTotalItemsCollected() {
		return totalItemsCollected;
	}

	/**
	 * The biggest number of other bots killed in a row (without being killed).
	 * @return
	 */
	public int getNumberOfPlayersKilledWithoutDeath() {
		return numberOfPlayersKilledWithoutDeath;
	}

	/**
	 * How many times your bot had double damage so far.
	 * @return
	 */
	public int getDoubleDamageCount() {
		return doubleDamageCount;
	}

	/**
	 * For how long (in total) your bot had double damage. In seconds.
	 * @return
	 */
	public double getDoubleDamageTime() {
		return doubleDamageTime;
	}

	/**
	 * Who has killed your bot the most? This map holds number according to killers' ides.
	 * @return
	 */
	public Map<UnrealId, Integer> getKilledBy() {
		return killedBy;
	}

	/**
	 * How many times in total your bot has died (regardless the type of death).
	 * @return
	 */
	public int getDeaths() {
		return deaths;
	}

	/**
	 * How big distance your bot has travelled so far. In UT-Units.
	 * @return
	 */
	public double getTravelledDistance() {
		return travelledDistance;
	}

	/**
	 * How many times your bot has committed suicide.
	 * @return
	 */
	public int getSuicides() {
		return suicides;
	}

	/**
	 * Current score of your bot.
	 * @return
	 */
	public int getScore() {
		return self == null ? 0 : playerScores.get(getBotId()).getScore();
	}
	
	/**
	 * Current score of the bot's team.
	 * @return
	 */
	public int getTeamScore() {
		return self == null ? 0 : teamScores.get(self.getTeam()).getScore();
	}

	/**
	 * How many times your bot has been killed by other bots.
	 * @return
	 */
	public int getKilledByOthers() {
		return killedByOthers;
	}
	
	////
	//
	// UTILITY GETTERS
	//
	////
	
	/**
	 * Whether we have already received at least two {@link BeginMessage} in order to have all time-vars initialized
	 * so we may collect all stats.
	 */
	public boolean isTimeInitialized() {
		return previousUT2004Time > 0 && currentUT2004Time > 0;
	}

	/**
	 * Returns the global UT2004 time that we're considering as a start-of-match.
	 * @return
	 */
	public double getMatchStartTime() {
		return matchStartTime;
	}
	
	//// 
	//
	// LISTENERS
	//
	////
    
	/*========================================================================*/

	/**
	 * BeginMessage listener.
	 */
	private class ControlMessageListener implements IWorldEventListener<ControlMessage>
	{
		@Override
		public void notify(ControlMessage event)
		{
			synchronized(statsMutex) {
				outputStatLine(getCurrentMatchTime(), event.getType(), String.valueOf(event.getPF1()), String.valueOf(event.getPF2()), String.valueOf(event.getPF3()), String.valueOf(event.getPI1()), String.valueOf(event.getPI2()), String.valueOf(event.getPI3()), String.valueOf(event.getPS1()), String.valueOf(event.getPS2()), String.valueOf(event.getPS3()));
			}
		}

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public ControlMessageListener(IWorldView worldView)
		{
			worldView.addEventListener(ControlMessage.class, this);
		}
	}

	/** BeginMessage listener */
	private ControlMessageListener controlMessageListener;
	
	/*========================================================================*/


	/**
	 * BeginMessage listener.
	 */
	private class BeginMessageListener implements IWorldEventListener<BeginMessage>
	{
		@Override
		public void notify(BeginMessage event)
		{
			synchronized(statsMutex) {
				lastBeginMessage = event;
				
				if (currentUT2004Time <= 0) {
					if (isLogBeforeMatchRestart()) {
						matchStartTime = event.getTime();
					}
				}
				
				previousUT2004Time = currentUT2004Time;
				currentUT2004Time = event.getTime();
				ut2004TimeDelta = currentUT2004Time - previousUT2004Time;
				
				previousSystemTime = currentSystemTime;
				currentSystemTime = System.currentTimeMillis();
				systemTimeDelta = currentSystemTime - previousSystemTime;
			}
		}

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public BeginMessageListener(IWorldView worldView)
		{
			worldView.addEventListener(BeginMessage.class, this);
		}
	}

	/** BeginMessage listener */
	private BeginMessageListener beginMessageListener;
	
	/** Most rescent message containing info about the game frame. */
	private BeginMessage lastBeginMessage = null;
	
	/** Previous time in UT2004 */
	private double previousUT2004Time = -1;
	
	/** Previous {@link System#currentTimeMillis()} when {@link BeginMessage} was received. */
	private long previousSystemTime = -1;
	
	/** Current time in UT2004 */
	private double currentUT2004Time = -1;
	
	/** Current (== last) {@link System#currentTimeMillis()} when {@link BeginMessage} was received. */
	private long currentSystemTime = -1;
	
	/**
	 * How many time has passed between last two {@link BeginMessage}.
	 */
	private double ut2004TimeDelta = -1;
	
	/**
	 * How many millis has passed between last two {@link BeginMessage}.
	 */
	private long systemTimeDelta = -1;
	
	/*========================================================================*/

	/**
	 * EndMessage listener.
	 */
	private class EndMessageListener implements IWorldEventListener<EndMessage>
	{
		@Override
		public void notify(EndMessage event)
		{
			synchronized(statsMutex) {
				lastEndMessage = event;
				updateStats(event);
			}
		}

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public EndMessageListener(IWorldView worldView)
		{
			worldView.addEventListener(EndMessage.class, this);
		}
	}

	/** EndMessage listener */
	EndMessageListener endMessageListener;
	
	/** Most rescent message containing info about the game frame. */
	EndMessage lastEndMessage = null;

	/*========================================================================*/
	
	/**
	 * PlayerScore listener.
	 */
	private class PlayerScoreListener implements IWorldEventListener<PlayerScore>
	{
		@Override
		public void notify(PlayerScore event)
		{
			synchronized(statsMutex) {
				synchronized(playerScores) {
					playerScores.put(event.getId(), event);
				}
			}
		}

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public PlayerScoreListener(IWorldView worldView)
		{
			worldView.addEventListener(PlayerScore.class, this);
		}
	}

	/** PlayerScore listener */
	private PlayerScoreListener playerScoreListener;
	
	/*========================================================================*/
	
	/**
	 * TeamScore listener.
	 */
	private class TeamScoreListener implements IWorldObjectEventListener<TeamScore, WorldObjectUpdatedEvent<TeamScore>>
	{
		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public TeamScoreListener(IWorldView worldView)
		{
			worldView.addObjectListener(TeamScore.class, WorldObjectUpdatedEvent.class, this);
		}

		@Override
		public void notify(WorldObjectUpdatedEvent<TeamScore> event) {
			synchronized(statsMutex) {
				synchronized(teamScores) {
					teamScores.put(event.getObject().getTeam(), event.getObject());
				}
			}
		}
	}

	/** TeamScore listener */
	private TeamScoreListener teamScoreListener;
		
	/*========================================================================*/	
	
	/**
	 * Self listener.
	 */
	private class SelfListener implements IWorldObjectEventListener<Self, WorldObjectUpdatedEvent<Self>>
	{
		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public SelfListener(IWorldView worldView)
		{
			worldView.addObjectListener(Self.class, WorldObjectUpdatedEvent.class, this);
		}

		@Override
		public void notify(WorldObjectUpdatedEvent<Self> event) {
			synchronized(statsMutex) {
				self = event.getObject();
			}
		}
	}
	
	/** Self listener */
	private SelfListener selfListener;
	
	/** Last self received */
	private Self self = null;
	
	/*========================================================================*/	
	
	/**
	 * BotKilled listener.
	 */
	private class BotKilledListener implements IWorldEventListener<BotKilled>
	{
		/**
		 * Constructor. Registers itBotKilled on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public BotKilledListener(IWorldView worldView)
		{
			worldView.addEventListener(BotKilled.class, this);
		}

		@Override
		public void notify(BotKilled event) {
			botKilled(event);
		}
	}
	
	/** BotKilled listener */
	private BotKilledListener botKilledListener;
		
	/*========================================================================*/	
	
	/**
	 * PlayerKilled listener.
	 */
	private class PlayerKilledListener implements IWorldEventListener<PlayerKilled>
	{
		/**
		 * Constructor. Registers itPlayerKilled on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public PlayerKilledListener(IWorldView worldView)
		{
			worldView.addEventListener(PlayerKilled.class, this);
		}

		@Override
		public void notify(PlayerKilled event) {
			playerKilled(event);
		}
	}
	
	/** PlayerKilled listener */
	private PlayerKilledListener playerKilledListener;
	
	/*========================================================================*/	
	
	/**
	 * GameRestarted listener.
	 */
	private class GameRestartedListener implements IWorldEventListener<GameRestarted>
	{
		/**
		 * Constructor. Registers itGameRestarted on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public GameRestartedListener(IWorldView worldView)
		{
			worldView.addEventListener(GameRestarted.class, this);
		}

		@Override
		public void notify(GameRestarted event) {
			gameRestarted(event);
		}
	}
	
	/** GameRestarted listener */
	private GameRestartedListener gameRestartedListener;
	
	/*========================================================================*/	
	
	/**
	 * ItemPickedUp listener.
	 */
	private class ItemPickedUpListener implements IWorldEventListener<ItemPickedUp>
	{
		/**
		 * Constructor. Registers itItemPickedUp on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public ItemPickedUpListener(IWorldView worldView)
		{
			worldView.addEventListener(ItemPickedUp.class, this);
		}

		@Override
		public void notify(ItemPickedUp event) {
			itemPickedUp(event);
		}
	}
	
	/** ItemPickedUp listener */
	private ItemPickedUpListener itemPickedUpListener;
	
	/*========================================================================*/
	
	/**
	 * ItemPickedUp listener.
	 */
	private class FlagListener implements IWorldObjectListener<FlagInfo>
	{
		/**
		 * Constructor. Registers itItemPickedUp on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public FlagListener(IWorldView worldView)
		{
			worldView.addObjectListener(FlagInfo.class, this);
		}

                @Override
                public void notify(IWorldObjectEvent<FlagInfo> event) {
                    FlagInfo t = event.getObject();
                    flagInfo(t);
                }
	}
	
	/** ItemPickedUp listener */
	private FlagListener flagListener;
	
	/*========================================================================*/	
	
	////
	//
	// GAME RESTART HANDLING
	//
	////
	
	/**
	 * Global UT2004 time when the match was started.
	 */
	private double matchStartTime = 0;
	
	/**
	 * Whether we should be logging. Default: TRUE... set to FALSE with {@link AgentStats#setLogBeforeMatchRestart(boolean)} with arg. TRUE.
	 * <p><p>
	 * Never use directly to decide whether you should collect stats, always use {@link AgentStats#isLogging()}.
	 */
	private boolean shouldLog = true;
	
	/**
	 * Should we log something before {@link GameRestarted}? Default: TRUE. 
	 */
	private boolean logBeforeMatchRestart = true;

	/**
	 * Whether the object is currently collecting stats. 
	 * <p><p>
	 * This depends on three things: 
	 * <ol>
	 * <li>we have to have {@link AgentStats#isTimeInitialized()}</li>
	 * <li>and we should not be waiting for {@link GameRestarted}, i.e., you have used {@link AgentStats#setLogBeforeMatchRestart(boolean)}</li>
	 * <li>we have already received bot's {@link Self}</li>
	 * </ol>
	 * 
	 * @return
	 */
	public boolean isLogging() {
		return isTimeInitialized() && shouldLog && self != null;
	}

	/**
	 * Should we log something before {@link GameRestarted}? Default: TRUE.
	 * @return
	 */
	public boolean isLogBeforeMatchRestart() {
		return logBeforeMatchRestart;
	}

	/**
	 * Sets whether we should collect stats before {@link GameRestarted} event. Default: FALSE.
	 * <p><p>
	 * Best to be utilized in {@link IUT2004BotController#prepareBot(UT2004Bot)}.
	 * 
	 * @param logBeforeMatchRestart
	 */
	public void setLogBeforeMatchRestart(boolean logBeforeMatchRestart) {
		this.logBeforeMatchRestart = logBeforeMatchRestart;
		if (this.logBeforeMatchRestart) {
			shouldLog = true;
		} else {
			shouldLog = false;
		}
	}

	protected void gameRestarted(GameRestarted event) {
		synchronized(statsMutex) {
			if (event.isFinished()) {
				shouldLog = true;
				resetStats();
				matchStartTime = currentUT2004Time;
				outputStatLine(0, "GAME_RESTARTED");
			}
		}
	}
	
	////
	//
	// EVENT HANDLING
	//
	////
	
	protected int playerKilledInRow = 0;
	
	protected void botKilled(BotKilled event) {
		synchronized(statsMutex) {
			if (!isLogging()) return;
			++deaths;
			if (event.getKiller() == null || (event.getKiller().equals(getBotId())) || (self != null && event.getKiller().equals(self.getBotId()))) {
				++suicides;			
			} else {
				++killedByOthers;
				synchronized(killedBy) {
					killedBy.put(event.getKiller(), killedBy.get(event.getKiller())+1);
				}
			}
			
			// CLEANING UP
			playerKilledInRow = 0;
			lastLocation = null;
			
			// OUTPUTTING STATS
			if (event.getKiller() == null || (self != null && event.getKiller().equals(getBotId()))) {
				outputStatLine(getCurrentMatchTime(), "BOT_KILLED", "SUICIDE", event.getDamageType());
			} else {
				outputStatLine(getCurrentMatchTime(), "BOT_KILLED", event.getKiller().getStringId(), event.getDamageType());
			}
		}
	}
	
	protected void playerKilled(PlayerKilled event) {
		synchronized(statsMutex) {
			if (!isLogging()) return;
			UnrealId killer = event.getKiller();
			UnrealId me = getBotId();
			if (event.getId().equals(me)) {
				// this is handled elsewhere!
				return;
			}
	    	if (killer == null || (!killer.equals(me))) {
				// the player has committed suicide or was killed by other bot
			} else {
				// WE HAVE KILLED THE BOT!
				++killedOthers;					
				++playerKilledInRow;
				if (playerKilledInRow > numberOfPlayersKilledWithoutDeath) {
					numberOfPlayersKilledWithoutDeath = playerKilledInRow;
				}
				synchronized(killed) {
					killed.put(event.getId(), killed.get(event.getId())+1);
				}
				outputStatLine(getCurrentMatchTime(), "PLAYER_KILLED", event.getId().getStringId(), event.getDamageType());
			}	
		}
	}
        
        private String team0FlagState;
        private String team1FlagState;
	
	protected void flagInfo(FlagInfo event) {
		synchronized(statsMutex) {
			if (!isLogging()) return;
                        String flagState;
                        if(event.getTeam() == 0 )
                            flagState = team0FlagState;
                        else
                            flagState = team1FlagState;
                        
                        //if the state has change, log it
                        if(flagState != null && !flagState.equals(event.getState())){
                            if(event.getState().toLowerCase().equals("home"))
                            {
                                if(flagState.toLowerCase().equals("held"))
                                    outputStatLine(getCurrentMatchTime(), "FLAG_CAPTURED", event.getTeam().toString(), event.getHolder()!=null?event.getHolder().getStringId():"");
                                else if(flagState.toLowerCase().equals("dropped"))
                                    outputStatLine(getCurrentMatchTime(), "FLAG_RETURNED", event.getTeam().toString(), event.getHolder()!=null?event.getHolder().getStringId():"");
                            }else if(event.getState().toLowerCase().equals("held"))
                                outputStatLine(getCurrentMatchTime(), "FLAG_PICKEDUP".toUpperCase(), event.getTeam().toString(), event.getHolder()!=null?event.getHolder().getStringId():"");
                            else // only drop left here, but just to be sure
                                outputStatLine(getCurrentMatchTime(), "FLAG_"+event.getState().toUpperCase(), event.getTeam().toString(), event.getHolder()!=null?event.getHolder().getStringId():"");
                        }
                        
                        if(event.getTeam() == 0 )
                            team0FlagState = event.getState();
                        else
                            team1FlagState = event.getState();
		}
	}
	
	protected void itemPickedUp(ItemPickedUp event) {
		synchronized(statsMutex) {
			if (!isLogging()) return;
			if (event.getType() == UT2004ItemType.U_DAMAGE_PACK) {
				++doubleDamageCount;
			}
			synchronized(itemsCollected) {
				itemsCollected.put(event.getType(), itemsCollected.get(event.getType()) + 1);
			}
			synchronized(itemsByCategoryCollected) {
				itemsByCategoryCollected.put(event.getType().getCategory(), itemsByCategoryCollected.get(event.getType().getCategory())+1);
			}
			outputStatLine(getCurrentMatchTime(), "ITEM_PICKEDUP", event.getType().getName(), event.getType().getCategory().name);
		}
	}
	
	////
	//
	// UPDATE STATS (EndMessage event)
	//
	////
	
	protected Location lastLocation;
	
	/**
	 * Called when {@link EndMessage} is received, writes another line into the {@link UT2004AnalyzerObsStats#outputFile}.
	 */
    protected void updateStats(EndMessage event) {
    	synchronized(statsMutex) {
	    	if (self == null) {
	    		log.warning("EndMessage received but no SELF was received.");
	    		return;
	    	}
	    	if (!isLogging()) return;
	    	
	    	if (self.getVelocity().size() > 1) {
	    		timeMoving += ut2004TimeDelta;
	    	}
	    	if (self.isShooting()) {
	    		timeShooting += ut2004TimeDelta;
	    		ItemType weapon = UT2004ItemType.getWeapon(UnrealId.get(self.getWeapon()));
	    		if (weapon == null) {
	    			log.warning("Unrecognized weapon of id: " + self.getWeapon());
	    		} else {
	    			synchronized(weaponsUsedTime) {
	    				weaponsUsedTime.put(weapon, weaponsUsedTime.get(weapon) + ut2004TimeDelta);
	    			}
	    		}
	    	}
	    	if (self.getUDamageTime() > 0) {
	    		doubleDamageTime += ut2004TimeDelta;
	    	}
	    	if (lastLocation != null) {
	    		travelledDistance += lastLocation.getDistance(self.getLocation());
	    	}
	    	lastLocation = self.getLocation();
	    	
	    	outputStatLine(getCurrentMatchTime());
    	}
    }
    
    ////
    //
    // LIFECYCLE MANAGEMENT
    //
    ////
    
    @Override
    protected void start(boolean startToPaused) {
    	super.start(startToPaused);
    	resetStats();
    	resetMatchTime();
    }
	
    @Override
    protected void stop() {
    	super.stop();
    	synchronized(statsMutex) {
	    	stopOutput();
    	}
    }
    
    @Override
    protected void kill() {
    	super.kill();
    	synchronized(statsMutex) {
    		stopOutput();
    	}    	
    }

    ////
    // 
    // CONSTRUCTOR
    //
    ////

	/**
	 * Constructor. Setups the memory module based on bot's world view.
	 * @param bot owner of the module that is using it
	 */
	public AgentStats(IObservingAgent bot) {
		this(bot, null);
	}
	
	/**
	 * Constructor. Setups the memory module based on bot's world view.
	 * @param bot owner of the module that is using it
	 * @param log Logger to be used for logging runtime/debug info. If <i>null</i>, the module creates its own logger.
	 */
	public AgentStats(IObservingAgent bot, Logger log)
	{
		super(bot, log);

		// create listeners
		controlMessageListener = new ControlMessageListener(worldView);
		beginMessageListener   = new BeginMessageListener(worldView);
		endMessageListener     = new EndMessageListener(worldView);
		selfListener           = new SelfListener(worldView);
		botKilledListener      = new BotKilledListener(worldView);
		playerKilledListener   = new PlayerKilledListener(worldView);
		itemPickedUpListener   = new ItemPickedUpListener(worldView);
		flagListener           = new FlagListener(worldView);
		gameRestartedListener  = new GameRestartedListener(worldView);
		playerScoreListener    = new PlayerScoreListener(worldView);
		teamScoreListener      = new TeamScoreListener(worldView);
        cleanUp();
	}
	
}
