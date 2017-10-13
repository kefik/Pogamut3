package cz.cuni.amis.pogamut.ut2004.hideandseek.tournament;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;

import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.HSGameConfig;
import cz.cuni.amis.pogamut.ut2004.tournament.UT2004Ini;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004BotConfig;
import cz.cuni.amis.pogamut.ut2004.utils.UnrealUtils;

public class UT2004HideAndSeekConsole {
	
	private static final char ARG_UT2004_HOME_DIR_SHORT = 'u';
	
	private static final String ARG_UT2004_HOME_DIR_LONG = "ut2004-home-dir";
	
	private static final char ARG_BOT_JARs_SHORT = 'a';
	
	private static final String ARG_BOT_JARs_LONG = "bot-jars";
	
	private static final char ARG_BOT_NAMEs_SHORT = 'b';
	
	private static final String ARG_BOT_NAMEs_LONG = "bot-names";
	
	private static final char ARG_MAP_NAME_SHORT = 'm';
	
	private static final String ARG_MAP_NAME_LONG = "map-name";
	
	private static final char ARG_MATCH_NAME_SHORT = 'n';
	
	private static final String ARG_MATCH_NAME_LONG = "match-name";
	
	private static final char ARG_RESULT_DIR_SHORT = 'r';
	
	private static final String ARG_RESULT_DIR_LONG = "result-directory";
	
	private static final char ARG_SERVER_NAME_SHORT = 's';
	
	private static final String ARG_SERVER_NAME_LONG = "server-name";
	
	private static final char ARG_HUMAN_LIKE_LOG_SHORT = 'h';
	
	private static final String ARG_HUMAN_LIKE_LOG_LONG = "human-like-log";
	
	private static final char ARG_UT2004_PORT_SHORT = 'p';
	
	private static final String ARG_UT2004_PORT_LONG = "ut2004-port";
	
	// HideAndSeek Config
	
	private static final char ARG_SAFE_AREA_SHORT = 'f';
	
	private static final String ARG_SAFE_AREA_LONG = "safe-area";
	
	private static final char ARG_ROUND_COUNT_SHORT = 'c';
	
	private static final String ARG_ROUND_COUNT_LONG = "round-count";
	
	private static final char ARG_ROUND_TIME_SECS_SHORT = 't';
	
	private static final String ARG_ROUND_TIME_SECS_LONG = "round-time-secs";
	
	private static final char ARG_HIDE_TIME_SECS_SHORT = 'i';
	
	private static final String ARG_HIDE_TIME_SECS_LONG = "hide-time-secs";
	
	private static final char ARG_RESTRICTED_AREA_TIME_SECS_SHORT = 'e';
	
	private static final String ARG_RESTRICTED_AREA_TIME_SECS_LONG = "restricted-area-time-secs";
	
	private static final char ARG_SAFE_AREA_RADIUS_SHORT = 'g';
	
	private static final String ARG_SAFE_AREA_RADIUS_LONG = "safe-area-radius";
	
	private static final char ARG_RESTRICTED_AREA_RADIUS_SHORT = 'j';
	
	private static final String ARG_RESTRICTED_AREA_RADIUS_LONG = "restricted-area-radius";
	
	private static final char ARG_SPOT_TIME_MILLIS_SHORT = 'k';
	
	private static final String ARG_SPOT_TIME_MILLIS_LONG = "spot-time-millis";
	
	private static final char ARG_RESTRICTED_AREA_SEEKER_MAX_TIME_MILLIS_SHORT = 'l';
	
	private static final String ARG_RESTRICTED_AREA_SEEKER_MAX_TIME_MILLIS_LONG = "restricted-area-seeker-max-time";
	
	private static final char ARG_SPAWN_RADIUS_FOR_RUNNERS_SHORT = 'w';
	
	private static final String ARG_SPAWN_RADIUS_FOR_RUNNERS_LONG = "spawn-radius-for-runners";
	
	private static final char ARG_FIXED_SEEKER_NAME_SHORT = 'x';
	
	private static final String ARG_FIXED_SEEKER_NAME_LONG = "fixed-seeker-name";
	
	private static final char ARG_SCORE_RUNNER_CAPTURED_SHORT = '1';
	
	private static final String ARG_SCORE_RUNNER_CAPTURED_LONG = "score-runner-captured";
	
	private static final char ARG_SCORE_RUNNER_SPOTTED_SHORT = '2';
	
	private static final String ARG_SCORE_RUNNER_SPOTTED_LONG = "score-runner-spotted";
	
	private static final char ARG_SCORE_RUNNER_SAFE_SHORT = '3';
	
	private static final String ARG_SCORE_RUNNER_SAFE_LONG = "score-runner-safe";
	
	private static final char ARG_SCORE_RUNNER_SURVIVED_SHORT = '4';
	
	private static final String ARG_SCORE_RUNNER_SURVIVED_LONG = "score-runner-survived";
	
	private static final char ARG_SCORE_RUNNER_FOULED_SHORT = '5';
	
	private static final String ARG_SCORE_RUNNER_FOULED_LONG = "score-runner-fouled";
	
	private static final char ARG_SCORE_SEEKER_CAPTURED_RUNNER_SHORT = '6';
	
	private static final String ARG_SCORE_SEEKER_CAPTURED_RUNNER_LONG = "score-seeker-captured-runner";
	
	private static final char ARG_SCORE_SEEKER_SPOTTED_RUNNER_SHORT = '7';
	
	private static final String ARG_SCORE_SEEKER_SPOTTED_RUNNER_LONG = "score-seeker-spotted-runner";
	
	private static final char ARG_SCORE_SEEKER_LET_RUNNER_ESCAPE_SHORT = '8';
	
	private static final String ARG_SCORE_SEEKER_LET_RUNNER_ESCAPE_LONG = "score-seeker-let-runner-escape";
	
	private static final char ARG_SCORE_SEEKER_LET_RUNNER_SURVIVE_SHORT = '9';
	
	private static final String ARG_SCORE_SEEKER_LET_RUNNER_SURVIVE_LONG = "score-seeker-let-runner-survive";
	
	private static final char ARG_SCORE_SEEKER_FOULED_SHORT = '0';
	
	private static final String ARG_SCORE_SEEKER_FOULED_LONG = "score-seeker-fouled";
	
	// UTILS

	private static JSAP jsap;
	
	private static JSAPResult config;

	private static boolean headerOutput = false;

	private static String ut2004HomeDir;

	private static String botJars;
	
	private static String[] botJarsSeparated;

	private static String botNames;
	
	private static String[] botNamesSeparated;

	private static String map;

	private static String serverName;

	private static String resultDir;

	private static String matchName;
	
	private static File ut2004HomeDirFile;

	private static File[] botJarFiles;
	
	private static File mapsDirFile;

	private static File mapFile;

	private static File ut2004SystemDirFile;

	private static File ut2004IniFile;

	private static boolean humanLikeLog;

	private static int ut2004Port;	
	
	// HideAndSeek Config
	
	private static HSGameConfig gameConfig;
	
	private static void fail(String errorMessage) {
		fail(errorMessage, null);
	}

	private static void fail(String errorMessage, Throwable e) {
		header();
		System.out.println("ERROR: " + errorMessage);
		System.out.println();
		if (e != null) {
			e.printStackTrace();
			System.out.println("");
		}		
        System.out.println("Usage: java -jar ut2004-hide-and-seek....jar ");
        System.out.println("                " + jsap.getUsage());
        System.out.println();
        System.out.println(jsap.getHelp());
        System.out.println();
        throw new RuntimeException("FAILURE: " + errorMessage);
	}

	private static void header() {
		if (headerOutput) return;
		System.out.println();
		System.out.println("=======================================");
		System.out.println("Pogamut UT2004 Hide&Seek Match Executor");
		System.out.println("=======================================");
		System.out.println();
		headerOutput = true;
	}
	
	private static void initJSAP() throws JSAPException {
		jsap = new JSAP();
		    	
    	FlaggedOption opt1 = new FlaggedOption(ARG_UT2004_HOME_DIR_LONG)
        	.setStringParser(JSAP.STRING_PARSER)
        	.setRequired(true) 
        	.setShortFlag(ARG_UT2004_HOME_DIR_SHORT)
        	.setLongFlag(ARG_UT2004_HOME_DIR_LONG);    
        opt1.setHelp("UT2004 home directory containing GameBots2004 (System/GameBots2004.u) present.");
        
        jsap.registerParameter(opt1);
        
        FlaggedOption opt2 = new FlaggedOption(ARG_BOT_JARs_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(true) 
	    	.setShortFlag(ARG_BOT_JARs_SHORT)
	    	.setLongFlag(ARG_BOT_JARs_LONG);    
        opt2.setHelp("Semicolon separated PATH/TO/JAR/file1;PATH/TO/JAR/file2 containing executable jars of bots.");
    
        jsap.registerParameter(opt2);
        
        FlaggedOption opt3 = new FlaggedOption(ARG_BOT_NAMEs_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(true) 
	    	.setShortFlag(ARG_BOT_NAMEs_SHORT)
	    	.setLongFlag(ARG_BOT_NAMEs_LONG);    
	    opt3.setHelp("Semicolon separated name1;name2;name3 (ids) that should be given to bots.");

	    jsap.registerParameter(opt3);
        
	    FlaggedOption opt6 = new FlaggedOption(ARG_MAP_NAME_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(true) 
	    	.setShortFlag(ARG_MAP_NAME_SHORT)
	    	.setLongFlag(ARG_MAP_NAME_LONG)
	    	.setDefault("DM-TraningDay");    
	    opt6.setHelp("Map where the game should be played (e.g. DM-1on1-TagMap).");
	
	    jsap.registerParameter(opt6);
        
	    FlaggedOption opt7 = new FlaggedOption(ARG_MATCH_NAME_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(false) 
	    	.setShortFlag(ARG_MATCH_NAME_SHORT)
	    	.setLongFlag(ARG_MATCH_NAME_LONG)
	    	.setDefault("HideAndSeekGame");    
	    opt7.setHelp("Name of the match == output folder for the results.");
	
	    jsap.registerParameter(opt7);
	    
	    FlaggedOption opt8 = new FlaggedOption(ARG_RESULT_DIR_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(false) 
	    	.setShortFlag(ARG_RESULT_DIR_SHORT)
	    	.setLongFlag(ARG_RESULT_DIR_LONG)
	    	.setDefault(".");
	    opt8.setHelp("PATH/TO/directory where to output results (does not need to exist).");
	
	    jsap.registerParameter(opt8);
	    
	    FlaggedOption opt9 = new FlaggedOption(ARG_SERVER_NAME_LONG)
			.setStringParser(JSAP.STRING_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_SERVER_NAME_SHORT)
			.setLongFlag(ARG_SERVER_NAME_LONG)
			.setDefault("HideAndSeekGame");
		opt9.setHelp("Server name that should be advertised via LAN.");
		
		jsap.registerParameter(opt9);
		
		Switch opt10 = new Switch(ARG_HUMAN_LIKE_LOG_LONG)
			.setShortFlag(ARG_HUMAN_LIKE_LOG_SHORT)
			.setLongFlag(ARG_HUMAN_LIKE_LOG_LONG)
			.setDefault("false");
		opt10.setHelp("Whether to produce log for 'HumanLike Project' analysis.");
		
		jsap.registerParameter(opt10);
		
		FlaggedOption opt11 = new FlaggedOption(ARG_UT2004_PORT_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_UT2004_PORT_SHORT)
			.setLongFlag(ARG_UT2004_PORT_LONG)
			.setDefault("7777");
		opt11.setHelp("UT2004 port for the dedicated server (1-32000).");
		
		jsap.registerParameter(opt11);
		
		// HS GAME CONFIG
		
		FlaggedOption opt12 = new FlaggedOption(ARG_SAFE_AREA_LONG)
			.setStringParser(JSAP.STRING_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_SAFE_AREA_SHORT)
			.setLongFlag(ARG_SAFE_AREA_LONG)
			.setDefault("[2000;-915;-50]");
		opt12.setHelp("Safe area location within the map, format [x;y;z]. Default is configured for DM-TraningDay map.");
		
		jsap.registerParameter(opt12);
		
		FlaggedOption opt13 = new FlaggedOption(ARG_ROUND_COUNT_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_ROUND_COUNT_SHORT)
			.setLongFlag(ARG_ROUND_COUNT_LONG)
			.setDefault("10");
		opt13.setHelp("Number of Hide&Seek rounds to be played.");
		
		jsap.registerParameter(opt13);
		
		FlaggedOption opt14 = new FlaggedOption(ARG_ROUND_TIME_SECS_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_ROUND_TIME_SECS_SHORT)
			.setLongFlag(ARG_ROUND_TIME_SECS_LONG)
			.setDefault("60");
		opt14.setHelp("Total length of single Hide&Seek round (includes " + ARG_HIDE_TIME_SECS_LONG + " and " + ARG_RESTRICTED_AREA_TIME_SECS_LONG + ")");
		
		jsap.registerParameter(opt14);
		
		FlaggedOption opt15 = new FlaggedOption(ARG_HIDE_TIME_SECS_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_HIDE_TIME_SECS_SHORT)
			.setLongFlag(ARG_HIDE_TIME_SECS_LONG)
			.setDefault("8");
		opt15.setHelp("How much time (seconds) runners will get to hide before the seeker is spawned. Part of " + ARG_ROUND_TIME_SECS_LONG + ".");

		jsap.registerParameter(opt15);
		
		FlaggedOption opt16 = new FlaggedOption(ARG_RESTRICTED_AREA_TIME_SECS_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_RESTRICTED_AREA_TIME_SECS_SHORT)
			.setLongFlag(ARG_RESTRICTED_AREA_TIME_SECS_LONG)
			.setDefault("4");
		opt16.setHelp("How much time (seconds) the safe area will be restricted for the runners after the seeker is spawned. Part of " + ARG_ROUND_TIME_SECS_LONG + ".");

		jsap.registerParameter(opt16);
		
		FlaggedOption opt17 = new FlaggedOption(ARG_SAFE_AREA_RADIUS_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_SAFE_AREA_RADIUS_SHORT)
			.setLongFlag(ARG_SAFE_AREA_RADIUS_LONG)
			.setDefault("100");
		opt17.setHelp("How big is the safe area around the safe area point. This must be less than " + ARG_RESTRICTED_AREA_RADIUS_LONG + ".");
		
		jsap.registerParameter(opt17);
		
		FlaggedOption opt18 = new FlaggedOption(ARG_RESTRICTED_AREA_RADIUS_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_RESTRICTED_AREA_RADIUS_SHORT)
			.setLongFlag(ARG_RESTRICTED_AREA_RADIUS_LONG)
			.setDefault("150");
		opt18.setHelp("How big is the restricted area around the safe area point. This must be greater than " + ARG_SAFE_AREA_RADIUS_LONG + ".");
		
		jsap.registerParameter(opt18);
		
		FlaggedOption opt181 = new FlaggedOption(ARG_RESTRICTED_AREA_SEEKER_MAX_TIME_MILLIS_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_RESTRICTED_AREA_SEEKER_MAX_TIME_MILLIS_SHORT)
			.setLongFlag(ARG_RESTRICTED_AREA_SEEKER_MAX_TIME_MILLIS_LONG)
			.setDefault("7000");
		opt181.setHelp("How long can seeker linger within restricted area around safe point during the game.");
		
		jsap.registerParameter(opt181);
		
		FlaggedOption opt19 = new FlaggedOption(ARG_SPOT_TIME_MILLIS_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_SPOT_TIME_MILLIS_SHORT)
			.setLongFlag(ARG_SPOT_TIME_MILLIS_LONG)
			.setDefault("600");
		opt19.setHelp("For how long (millis) the runner must be visible to the seeker in order to be marked as 'SPOTTED'.");
		
		jsap.registerParameter(opt19);
		
		FlaggedOption opt20 = new FlaggedOption(ARG_SPAWN_RADIUS_FOR_RUNNERS_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_SPAWN_RADIUS_FOR_RUNNERS_SHORT)
			.setLongFlag(ARG_SPAWN_RADIUS_FOR_RUNNERS_LONG)
			.setDefault("100");
		opt20.setHelp("How far are runners spawned from the safe area point. They are spawned into circle that has radius of THIS around the safe area point.");
		
		jsap.registerParameter(opt20);
		
		FlaggedOption opt21 = new FlaggedOption(ARG_FIXED_SEEKER_NAME_LONG)
			.setStringParser(JSAP.STRING_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_FIXED_SEEKER_NAME_SHORT)
			.setLongFlag(ARG_FIXED_SEEKER_NAME_LONG);
		opt21.setHelp("When specified, all Hide&Seek rounds will be played with fixed seeker with given name.");
		
		jsap.registerParameter(opt21);
		
		FlaggedOption opt22 = new FlaggedOption(ARG_SCORE_RUNNER_CAPTURED_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_SCORE_RUNNER_CAPTURED_SHORT)
			.setLongFlag(ARG_SCORE_RUNNER_CAPTURED_LONG)
			.setDefault("-10");
		opt22.setHelp("Penalization-score for the runner to receive when it is captured by the seeker.");
		
		jsap.registerParameter(opt22);
		
		FlaggedOption opt23 = new FlaggedOption(ARG_SCORE_RUNNER_SPOTTED_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_SCORE_RUNNER_SPOTTED_SHORT)
			.setLongFlag(ARG_SCORE_RUNNER_SPOTTED_LONG)
			.setDefault("0");
		opt23.setHelp("Penalization-score for the runner to receive when it is spotted by the seeker.");
		
		jsap.registerParameter(opt23);
		
		FlaggedOption opt24 = new FlaggedOption(ARG_SCORE_RUNNER_SAFE_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_SCORE_RUNNER_SAFE_SHORT)
			.setLongFlag(ARG_SCORE_RUNNER_SAFE_LONG)
			.setDefault("100");
		opt24.setHelp("Score for the runner to receive when it gets to the safe-area before the seeker spots and captures it.");
		
		jsap.registerParameter(opt24);
		
		FlaggedOption opt25 = new FlaggedOption(ARG_SCORE_RUNNER_SURVIVED_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_SCORE_RUNNER_SURVIVED_SHORT)
			.setLongFlag(ARG_SCORE_RUNNER_SURVIVED_LONG)
			.setDefault("50");
		opt25.setHelp("Score for the runner to receive when it survives the round (both not being captured by the seeker and not making it to the safe area before the end of the round).");
		
		jsap.registerParameter(opt25);
		
		FlaggedOption opt26 = new FlaggedOption(ARG_SCORE_RUNNER_FOULED_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_SCORE_RUNNER_FOULED_SHORT)
			.setLongFlag(ARG_SCORE_RUNNER_FOULED_LONG)
			.setDefault("-1000");
		opt26.setHelp("Penalization-score for the runner to receive when it steps into the activated restricted area.");
		
		jsap.registerParameter(opt26);
		
		FlaggedOption opt27 = new FlaggedOption(ARG_SCORE_SEEKER_CAPTURED_RUNNER_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_SCORE_SEEKER_CAPTURED_RUNNER_SHORT)
			.setLongFlag(ARG_SCORE_SEEKER_CAPTURED_RUNNER_LONG)
			.setDefault("100");
		opt27.setHelp("Score for the seeker to receive when it catches the runner.");
		
		jsap.registerParameter(opt27);
		
		FlaggedOption opt28 = new FlaggedOption(ARG_SCORE_SEEKER_SPOTTED_RUNNER_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_SCORE_SEEKER_SPOTTED_RUNNER_SHORT)
			.setLongFlag(ARG_SCORE_SEEKER_SPOTTED_RUNNER_LONG)
			.setDefault("20");
		opt28.setHelp("Score for the seeker to receive when it spots new runner.");
		
		jsap.registerParameter(opt28);
		
		FlaggedOption opt29 = new FlaggedOption(ARG_SCORE_SEEKER_LET_RUNNER_ESCAPE_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_SCORE_SEEKER_LET_RUNNER_ESCAPE_SHORT)
			.setLongFlag(ARG_SCORE_SEEKER_LET_RUNNER_ESCAPE_LONG)
			.setDefault("-20");
		opt29.setHelp("Penalization-score for the seeker to receive when it let some runner escape (runner makes it to the safe-area before the seeker).");
		
		jsap.registerParameter(opt29);
		
		FlaggedOption opt30 = new FlaggedOption(ARG_SCORE_SEEKER_LET_RUNNER_SURVIVE_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_SCORE_SEEKER_LET_RUNNER_SURVIVE_SHORT)
			.setLongFlag(ARG_SCORE_SEEKER_LET_RUNNER_SURVIVE_LONG)
			.setDefault("-10");
		opt30.setHelp("Penalization-score for the seeker when it let some runner survive (runner does not make it to the safe-area neither the seeker manages to catch it before the end of the round).");
		
		jsap.registerParameter(opt30);
		
		FlaggedOption opt31 = new FlaggedOption(ARG_SCORE_SEEKER_FOULED_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_SCORE_SEEKER_FOULED_SHORT)
			.setLongFlag(ARG_SCORE_SEEKER_FOULED_LONG)
			.setDefault("-100");
		opt31.setHelp("Penalization-score for the seeker when fouled due to lingering within restricted area for too long.");
		
		jsap.registerParameter(opt31);
	}

	private static void readConfig(String[] args) {
		System.out.println("Parsing command arguments.");
		
		try {
	    	config = jsap.parse(args);
	    } catch (Exception e) {
	    	fail(e.getMessage());
	    	System.out.println("");
	    	e.printStackTrace();
	    	throw new RuntimeException("FAILURE!");
	    }
		
		if (!config.success()) {
			String error = "Invalid arguments specified.";
			Iterator errorIter = config.getErrorMessageIterator();
			if (!errorIter.hasNext()) {
				error += "\n-- No details given.";
			} else {
				while (errorIter.hasNext()) {
					error += "\n-- " + errorIter.next();
				}
			}
			fail(error);
    	}
		
		ut2004HomeDir = config.getString(ARG_UT2004_HOME_DIR_LONG);
	    botJars = config.getString(ARG_BOT_JARs_LONG);
	    botJarsSeparated = botJars == null ? null : botJars.split(";");
	    botNames = config.getString(ARG_BOT_NAMEs_LONG);
	    botNamesSeparated = botNames == null ? null : botNames.split(";");
	    map = config.getString(ARG_MAP_NAME_LONG);
	    serverName = config.getString(ARG_SERVER_NAME_LONG);
	    resultDir = config.getString(ARG_RESULT_DIR_LONG);
	    matchName = config.getString(ARG_MATCH_NAME_LONG);
	    humanLikeLog = config.getBoolean(ARG_HUMAN_LIKE_LOG_LONG);
	    ut2004Port = config.getInt(ARG_UT2004_PORT_LONG);
	    
	    gameConfig = new HSGameConfig();
	    
	    gameConfig.setTargetMap(map);
	    
	    String fixedSeeker = config.getString(ARG_FIXED_SEEKER_NAME_LONG);
	    if (fixedSeeker == null) {
	    	gameConfig.setFixedSeeker(false);
	    	gameConfig.setFixedSeekerName(null);
	    } else {
	    	gameConfig.setFixedSeeker(true);
	    	gameConfig.setFixedSeekerName(fixedSeeker);
	    }
	    	
	    gameConfig.setHideTimeUT((double)config.getInt(ARG_HIDE_TIME_SECS_LONG));
	    gameConfig.setRestrictedAreaRadius(config.getInt(ARG_RESTRICTED_AREA_RADIUS_LONG));
	    gameConfig.setRestrictedAreaTimeSecs((double)config.getInt(ARG_RESTRICTED_AREA_TIME_SECS_LONG));
	    gameConfig.setRoundCount(config.getInt(ARG_ROUND_COUNT_LONG));
	    gameConfig.setRoundTimeUT((double)config.getInt(ARG_ROUND_TIME_SECS_LONG));
	    gameConfig.setRunnerCaptured(config.getInt(ARG_SCORE_RUNNER_CAPTURED_LONG));
	    gameConfig.setRunnerFouled(config.getInt(ARG_SCORE_RUNNER_FOULED_LONG));
	    gameConfig.setRunnerSafe(config.getInt(ARG_SCORE_RUNNER_SAFE_LONG));
	    gameConfig.setRunnerSpotted(config.getInt(ARG_SCORE_RUNNER_SPOTTED_LONG));
	    gameConfig.setRunnerSurvived(config.getInt(ARG_SCORE_RUNNER_SURVIVED_LONG));
	    gameConfig.setSeekerFouled(config.getInt(ARG_SCORE_SEEKER_FOULED_LONG));
	    
	    Location safeArea = null;	    
	    try {
	    	safeArea = new Location(config.getString(ARG_SAFE_AREA_LONG));
	    } catch (Exception e) {
	    	fail("Failed to parse --" + ARG_SAFE_AREA_LONG + " " + config.getString(ARG_SAFE_AREA_LONG) + " as location. Required format [x;y;z].");
	    }	    
	    gameConfig.setSafeArea(safeArea);
	    
	    gameConfig.setSafeAreaRadius(config.getInt(ARG_SAFE_AREA_RADIUS_LONG));
	    gameConfig.setSeekerCapturedRunner(config.getInt(ARG_SCORE_SEEKER_CAPTURED_RUNNER_LONG));
	    gameConfig.setSeekerLetRunnerEscape(config.getInt(ARG_SCORE_SEEKER_LET_RUNNER_ESCAPE_LONG));
	    gameConfig.setSeekerLetRunnerSurvive(config.getInt(ARG_SCORE_SEEKER_LET_RUNNER_SURVIVE_LONG));
	    gameConfig.setSeekerSpottedRunner(config.getInt(ARG_SCORE_SEEKER_SPOTTED_RUNNER_LONG));
	    gameConfig.setSpawnRadiusForRunners(config.getInt(ARG_SPAWN_RADIUS_FOR_RUNNERS_LONG));
	    gameConfig.setSpotTimeMillis(config.getInt(ARG_SPOT_TIME_MILLIS_LONG));	 
	    gameConfig.setRestrictedAreaTimeSecs(((double)config.getInt(ARG_RESTRICTED_AREA_SEEKER_MAX_TIME_MILLIS_LONG)) / 1000.0d);
	}
	
	private static void sanityChecks() {
		System.out.println("Sanity checks...");
		
		if (botJarsSeparated == null) {
			fail("Bot jar(s) was/were not specified correctly.");
		}
		
		if (botNamesSeparated == null) {
			fail("Bot name(s) was/were not specified correctly.");
		}
		
		if (botJarsSeparated.length != botNamesSeparated.length) {
			fail("Bot jar(s) and name(s) numbers mismatch. I've parsed " + botJarsSeparated.length + " bot jar files != " + botNamesSeparated.length + " of bot names.");
		}
		
	    ut2004HomeDirFile = new File(ut2004HomeDir);
	    if (!ut2004HomeDirFile.exists() || !ut2004HomeDirFile.isDirectory()) {
	    	fail("UT2004 directory was not found at '" + ut2004HomeDirFile.getAbsolutePath() + "', path resolved from configuration read as '" + ut2004HomeDir + "'.");
	    }
	    System.out.println("-- UT2004 directory found at '" + ut2004HomeDirFile.getAbsolutePath() + "'");
	    
	    ut2004SystemDirFile = new File(ut2004HomeDirFile, "System");
	    if (!ut2004SystemDirFile.exists() || !ut2004SystemDirFile.isDirectory()) {
	    	fail("UT2004/System directory was not found at '" + ut2004SystemDirFile.getAbsolutePath() + "', invalid UT2004 installation.");
	    }
	    System.out.println("-- UT2004/System directory found at '" + ut2004SystemDirFile.getAbsolutePath() + "'");
	    
	    ut2004IniFile = new File(ut2004SystemDirFile, "UT2004.ini");
	    if (!ut2004IniFile.exists() || !ut2004IniFile.isFile()) {
	    	fail("UT2004/System/UT2004.ini file was not found at '" + ut2004IniFile.getAbsolutePath() + "', invalid UT2004 installation.");
	    }
	    System.out.println("-- UT2004/System/UT2004.ini file found at '" + ut2004IniFile.getAbsolutePath() + "'");
	    
	    botJarFiles = new File[botJarsSeparated.length];
	    for (int i = 0; i < botJarsSeparated.length; ++i) {
	    	botJarFiles[i] = new File(botJarsSeparated[i]);
	    	if (!botJarFiles[i].exists() || !botJarFiles[i].isFile()) {
		    	fail("Bot" + (i+1) + " jar file was not found at '"+ botJarFiles[i].getAbsolutePath() + "', path resolved from configuration read as '" + botJarsSeparated[i] + "'.");
		    }
	    	System.out.println("-- Bot" + (i+1) + " jar file found at '" + botJarFiles[i].getAbsolutePath() + "'");
	    }
	    System.out.println("-- Bot jars ok");
	   	
	    for (int i = 0; i < botNamesSeparated.length; ++i) {
	    	if (botNamesSeparated[i] == null || botNamesSeparated[i].isEmpty()) {
	    		fail("Bot " + (i+1) + " invalid name '" + botNamesSeparated[i] +"' specified.");
	    	}
	    	System.out.println("-- Bot" + (i+1) + " name set as '" + botNamesSeparated[i] + "'");
	    }
	    System.out.println("-- Bot names ok");
	    
	    mapsDirFile = new File(ut2004HomeDirFile, "Maps");
	    if (!mapsDirFile.exists() || !mapsDirFile.isDirectory()) {
	    	fail("UT2004/Maps directory was not found at '" + mapsDirFile.getAbsolutePath() + "', invalid UT2004 installation.");
	    }
	    System.out.println("-- UT2004/Maps directory found at '" + mapsDirFile.getAbsolutePath() + "'");
	    
	    mapFile = new File(mapsDirFile, map + ".ut2");
	    if (!mapFile.exists() || !mapFile.isFile()) {
	    	fail("Specified map '" + map + "' was not found within UT2004/Maps dir at '" + mapFile.getAbsoluteFile() + "', could not execute the match.");
	    }
	    System.out.println("-- Map '" + map + "' found at '" + mapFile.getAbsolutePath() + "'");
	    
	    if (matchName == null || matchName.isEmpty()) {
	    	fail("Invalid match name '" + matchName + "' specified.");
	    }
	    System.out.println("-- Match name set as '" + matchName + "'");
	    
	    if (serverName == null || serverName.isEmpty()) {
	    	fail("Invalid server name '" + serverName + "' specified.");
	    }
	    System.out.println("-- Server name set as '" + serverName + "'");
	    
	    if (ut2004Port < 1 || ut2004Port > 32000) {
	    	fail("Invalid UT2004 port specified '" + ut2004Port + "', must be 1 <= port <= 32000.");
	    }
	    System.out.println("-- UT2004 port set as '" + ut2004Port + "'");
	    
	    // GAME CONFIG
	    
	    HSGameConfig hsConfig = gameConfig;
	    
	    if (hsConfig.isFixedSeeker() && (hsConfig.getFixedSeekerName() == null || hsConfig.getFixedSeekerName().isEmpty())) {
			fail("Fixed seeker configured as TRUE, but no name specified.");
		}
		
		if (hsConfig.getTargetMap() == null || hsConfig.getTargetMap().isEmpty()) {
			fail("No targetMap specified.");
		}
		
		if (hsConfig.getHideTimeUT() + hsConfig.getRestrictedAreaTimeSecs() >= hsConfig.getRoundTimeUT()) {
			fail("HideTime + RestrictedAreaTime == " + (hsConfig.getHideTimeUT() + hsConfig.getRestrictedAreaTimeSecs()) + " >= " + hsConfig.getRoundTimeUT() + " ==  RoundTime, invalid. HideTime and RestrictedTime are included within RoundTime.");
		}
		
		if (hsConfig.getRoundCount() <= 0) {
			fail("RoundCount == " + hsConfig.getRoundCount() + " <= 0, invalid. There must be at least 1 round to be played.");
		}
		
		if (hsConfig.getSafeArea() == null) {
			fail("SafeArea not specified, is null.");
		}
		
		if (hsConfig.getSafeAreaRadius() < 25) {
			fail("SafeAreaRadius == " + hsConfig.getSafeAreaRadius() + " < 25, invalid, UT2004 sensor snapshots are not that precise to be able to handle small areas.");
		}
		
		if (hsConfig.getRestrictedAreaRadius() < 25) {
			fail("RestrictedAreaRadius == " + hsConfig.getRestrictedAreaRadius() + " < 25, invalid, UT2004 sensor snapshots are not that precise to be able to handle small areas.");
		}
		
		if (hsConfig.getSafeAreaRadius() > hsConfig.getRestrictedAreaRadius()) {
			fail("RestrictedAreaRadius == " + hsConfig.getRestrictedAreaRadius() + " < " + hsConfig.getSafeAreaRadius() + " == SafeAreaRadius, invalid, restricted area must be greater than safe area.");
		}

		if (hsConfig.getSpawnRadiusForRunners() < UnrealUtils.CHARACTER_COLLISION_RADIUS) {
			fail("SpawnRadiusForRunners == " + hsConfig.getSpawnRadiusForRunners() + " < " + UnrealUtils.CHARACTER_COLLISION_RADIUS + " == UnrealUtils.CHARACTER_COLLISION_RADIUS, runners won't have enough place for spawning.");
		}
	    
	    System.out.println("Sanity checks OK!");
	    
	    System.out.println("Hide&Seek Game Configuration...");
	    
	    System.out.println("-- Map:                            " + hsConfig.getTargetMap());
	    System.out.println("-- Round count:                    " + hsConfig.getRoundCount());
	    if (hsConfig.isFixedSeeker()) {
	    	System.out.println("-- Fixed seeker:                   " + hsConfig.getFixedSeekerName());
	    } else {
	    	System.out.println("-- Random seeker every round");
	    }
	    System.out.println("-- Round time (secs):              " + hsConfig.getRoundTimeUT());
	    System.out.println("-- Hide time (secs):               " + hsConfig.getHideTimeUT());
	    System.out.println("-- Restricted area time (secs):    " + hsConfig.getRestrictedAreaTimeSecs());
	    System.out.println("-- Safe area location:             " + hsConfig.getSafeArea());
	    System.out.println("-- Safe area radius:               " + hsConfig.getSafeAreaRadius());
	    System.out.println("-- Restricted area radius:         " + hsConfig.getRestrictedAreaRadius());
	    System.out.println("-- Runners spawn radius:           " + hsConfig.getSpawnRadiusForRunners());
	    System.out.println("-- Spotting time (millis):         " + hsConfig.getSpotTimeMillis());
	    System.out.println("-- Restr. ar. max seeker time (s): " + hsConfig.getRestrictedAreaSeekerMaxTimeSecs());
	    System.out.println("Hide&Seek Scoring Configuration...");
	    System.out.println("-- RUNNER SCORING");
	    System.out.println("---- Runner captured by seeker:    " + hsConfig.getRunnerCaptured());
	    System.out.println("---- Runner spotted by seeker:     " + hsConfig.getRunnerSpotted());
	    System.out.println("---- Runner reached safe area:     " + hsConfig.getRunnerSafe());
	    System.out.println("---- Runner survived the round:    " + hsConfig.getRunnerSurvived());
	    System.out.println("---- Runner foul:                  " + hsConfig.getRunnerFouled());
	    System.out.println("-- SEEKER SCORING");
	    System.out.println("---- Seeker captured runner:       " + hsConfig.getSeekerCapturedRunner());
	    System.out.println("---- Seeker spotted runner:        " + hsConfig.getSeekerSpottedRunner());
	    System.out.println("---- Seeker let runner escape:     " + hsConfig.getSeekerLetRunnerEscape());
	    System.out.println("---- Seeker let runner survive:    " + hsConfig.getSeekerLetRunnerSurvive());
	    System.out.println("---- Seeker fouled:                " + hsConfig.getSeekerFouled());
	}
	
	private static void setUT2004Ini() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		File ut2004IniBackup = new File(ut2004SystemDirFile, "UT2004.ini." + sdf.format(date) + ".bak");
		
		System.out.println("Backing up '" + ut2004IniFile.getAbsolutePath() + "' into '" + ut2004IniBackup.getAbsolutePath() + "' ...");
		try {
			FileUtils.copyFile(ut2004IniFile, ut2004IniBackup);
		} catch (IOException e) {
			throw new RuntimeException("Failed to backup UT2004.ini", e);
		}
				
		System.out.println("Reading '" + ut2004IniFile.getAbsolutePath() + "' ...");
		List<String> lines;
		try {
			lines = FileUtils.readLines(ut2004IniFile);
		} catch (IOException e) {
			throw new RuntimeException("Failed to read UT2004.ini", e);
		}
		System.out.println("-- " + lines.size() + " lines read.");
		
		System.out.println("Searching for UT2004 Port and ServerName ...");
		
		// 0 -> nothing
		// 1 -> section URL
		// 2 -> section Engine.GameReplicationInfo
		int state = 0;
		
		Pattern patternSection = Pattern.compile("^\\s*\\[\\s*[^]]*\\s*\\]\\s*$");
		Pattern patternSectionURL = Pattern.compile("^\\s*\\[\\s*" + UT2004Ini.Section_URL + "\\s*\\]\\s*$");
		Pattern patternSectionEngineGameReplicationInfo = Pattern.compile("^\\s*\\[\\s*" + UT2004Ini.Section_Engine_GameReplicationInfo + "\\s*\\]\\s*$");
		Pattern patternPort = Pattern.compile("^\\s*" + UT2004Ini.Key_Port + "\\s*=\\s*.*$");
		Pattern patternServerName = Pattern.compile("^\\s*" + UT2004Ini.Key_ServerName + "\\s*=\\s*.*$");
		Pattern patternShortName = Pattern.compile("^\\s*" + UT2004Ini.Key_ShortName + "\\s*=\\s*.*$");
		
		boolean portFound = false;
		boolean nameFound = false;
		boolean shortNameFound = false;
		
		for (int lineNum = 0; 
				// IF lineNum == lines.size() 
				// => perform one more state switch to add possibly missing keys
			 lineNum <= lines.size()
			    // IF all set
				// => break;
			 && (!(portFound && nameFound && shortNameFound)); 
			++lineNum
		) {		
						
			
			// current line probed
			String line = lineNum < lines.size() ? lines.get(lineNum).trim() : null;
			
			// sanity check
			if (lineNum < lines.size() && line == null) {
				continue;
			}
			
			if (lineNum == lines.size() || patternSection.matcher(line).matches()) {
				switch (state) {
				case 0:
					break;
				case 1:
					if (!portFound) {
						lines.add(lineNum, UT2004Ini.Key_Port + "=" + ut2004Port);
						portFound = true;
						++lineNum;
					}
					break;
				case 2:
					if (!nameFound) {
						lines.add(lineNum, UT2004Ini.Key_ServerName + "=" + serverName);
						nameFound = true;
						++lineNum;
					}
					if (!shortNameFound) {
						lines.add(lineNum, UT2004Ini.Key_ShortName + "=" + serverName);
						shortNameFound = true;
						++lineNum;
					}
					break;
				}
				if (lineNum == lines.size()) {
					break;
				}
				if (line == null) {
					continue;
				}
				if (patternSectionURL.matcher(line).matches()) {
					state = 1;					
				} else
				if (patternSectionEngineGameReplicationInfo.matcher(line).matches()) {
					state = 2;
				} else {
					state = 0;
				}
				continue;
			}
			
			switch (state) {
			case 0: 
				continue;
			case 1: 
				if (!portFound && patternPort.matcher(line).matches()) {
					lines.set(lineNum, UT2004Ini.Key_Port + "=" + ut2004Port);
					portFound = true;
				}
				continue;
			case 2:
				if (!nameFound && patternServerName.matcher(line).matches()) {
					lines.set(lineNum, UT2004Ini.Key_ServerName + "=" + serverName);
					nameFound = true;
				} else
				if (!shortNameFound && patternShortName.matcher(line).matches()) {
					lines.set(lineNum, UT2004Ini.Key_ShortName + "=" + serverName);
					shortNameFound = true;
				}
				continue;
			default:
				continue;
			}
		}
		
		if (!portFound) {
			throw new RuntimeException("Failed to set UT2004 port!");
		}
		if (!nameFound) {
			throw new RuntimeException("Failed to set UT2004 ServerName!");
		}
		
		System.out.println("UT2004 Port and ServerName set.");
		System.out.println("Rewriting '" + ut2004IniFile.getAbsolutePath() + "' ...");
		try {
			FileUtils.writeLines(ut2004IniFile, lines);
		} catch (IOException e) {
			throw new RuntimeException("Failed to write UT2004.ini", e);
		}		
		
		System.out.println("UT2004 Port and ServerName set.");
	}
	
	private static void executeMatch() {		
		UT2004HideAndSeekConfig config = new UT2004HideAndSeekConfig();
		
		UT2004BotConfig[] botConfigs = new UT2004BotConfig[botJarFiles.length];
		for (int i = 0; i < botJarFiles.length; ++i) {
			UT2004BotConfig botConfig = new UT2004BotConfig();
			botConfig.setBotId(botNamesSeparated[i]);
			botConfig.setPathToBotJar(botJarFiles[i].getAbsolutePath());
			botConfig.setRedirectStdErr(true);
			botConfig.setRedirectStdOut(true);
			botConfigs[i] = botConfig;
		}
		
		config.setBot(botConfigs);
		config.setHumanLikeLogEnabled(humanLikeLog);
		config.setMatchId(matchName);
		config.setOutputDirectory(new File(resultDir));
		config.setHsConfig(gameConfig);
		
		config.getUccConf().setGameType("BotDeathMatch");
		config.getUccConf().setMapName(map);
		config.getUccConf().setUnrealHome(ut2004HomeDir);
		
		LogCategory log = new LogCategory(matchName);
		UT2004HideAndSeek match = new UT2004HideAndSeek(config, log);
		
	    match.getLog().setLevel(Level.ALL);
	    match.getLog().addConsoleHandler();
	    
	    System.out.println("EXECUTING MATCH!");
	    
	    match.run();
	}
	
	public static void main(String[] args) throws JSAPException {
////      FOR TESTING		
//		args = new String[] {
//			"--ut2004-home-dir",
//			"D:\\Games\\UT2004-Devel",
//			"--bot-jars",
//			 "d:/Workspaces/Pogamut-Trunk/Main/PogamutUT2004Examples/25-HideAndSeekBot/target/ut2004-25-hide-and-seek-bot-3.7.1-SNAPSHOT.one-jar.jar"
//		+	";d:/Workspaces/Pogamut-Trunk/Main/PogamutUT2004Examples/25-HideAndSeekBot/target/ut2004-25-hide-and-seek-bot-3.7.1-SNAPSHOT.one-jar.jar"
//		+	";d:/Workspaces/Pogamut-Trunk/Main/PogamutUT2004Examples/25-HideAndSeekBot/target/ut2004-25-hide-and-seek-bot-3.7.1-SNAPSHOT.one-jar.jar"
//			,
//			"--bot-names",
//			"Bot1" 
//		+	";Bot2"
//		+	";Bot3"
//			,
//			"--map-name",
//			"DM-TrainingDay",
//			"--match-name",
//			"HideAndSeekMatch",
//			"--result-directory",
//			"./results",			
//			"--server-name",
//			"HideAndSeekServer",
//// GAME CONFIGURATION
//			"--safe-area",
//			"[2000;-915;-50]",
//			"--round-count",
//			"4",
//			"--round-time-secs",
//			"60",
//			"--hide-time-secs",
//			"8",
//			"--restricted-area-time-secs",
//			"4",
//			"--safe-area-radius",
//			"100",
//			"--restricted-area-radius",
//			"150",
//			"--spot-time-millis",			
//			"600",
//			"--restricted-area-seeker-max-time",
//			"7000",
//			"--spawn-radius-for-runners",
//			"100",
//			"--fixed-seeker-name",
//			"Bot1",
//			"--score-runner-captured",
//			"-10",
//			"--score-runner-spotted",
//			"0",
//			"--score-runner-safe",
//			"100",
//			"--score-runner-survived",
//			"50",
//			"--score-runner-fouled",
//			"-1000",
//			"--score-seeker-captured-runner",
//			"100",
//			"--score-seeker-spotted-runner",
//			"20",
//			"--score-seeker-let-runner-escape",
//			"-20",
//			"--score-seeker-let-runner-survive",
//			"-10",
//			"--score-seeker-fouled",
//			"-1000"
////			"--human-like-log", // human-like log			
//		};
		
		initJSAP();
	    
	    header();
	    
	    readConfig(args);
	    
	    sanityChecks();
	    
	    setUT2004Ini();
	    
	    executeMatch();
	}
	
}
