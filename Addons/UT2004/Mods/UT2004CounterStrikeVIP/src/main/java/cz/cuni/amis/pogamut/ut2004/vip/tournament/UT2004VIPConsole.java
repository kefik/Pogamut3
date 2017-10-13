package cz.cuni.amis.pogamut.ut2004.vip.tournament;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import cz.cuni.amis.pogamut.ut2004.tournament.UT2004Ini;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004BotConfig;
import cz.cuni.amis.pogamut.ut2004.utils.UnrealUtils;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.VIPGameConfig;

public class UT2004VIPConsole {
	
	private static final char ARG_UT2004_HOME_DIR_SHORT = 'u';
	
	private static final String ARG_UT2004_HOME_DIR_LONG = "ut2004-home-dir";
	
	private static final char ARG_BOT_JARs_SHORT = 'a';
	
	private static final String ARG_BOT_JARs_LONG = "bot-jars";
	
	private static final char ARG_BOT_NAMEs_SHORT = 'b';
	
	private static final String ARG_BOT_NAMEs_LONG = "bot-names";
	
	private static final char ARG_BOT_TEAMs_SHORT = 'c';
	
	private static final String ARG_BOT_TEAMs_LONG = "bot-teams";
	
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
	
	// VIP Config
	
	private static final char ARG_CT_SPAWN_AREAS_SHORT = 'i';
	
	private static final String ARG_CT_SPAWN_AREAS_LONG = "ct-spawn-areas";

	private static final char ARG_T_SPAWN_AREAS_SHORT = 'j';
	
	private static final String ARG_T_SPAWN_AREAS_LONG = "t-spawn-areas";
	
	private static final char ARG_VIP_SAFE_AREAS_SHORT = 'k';
	
	private static final String ARG_VIP_SAFE_AREAS_LONG = "vip-safe-areas";
	
	private static final char ARG_VIP_SAFE_AREA_RADIUS_SHORT = 'l';
	
	private static final String ARG_VIP_SAFE_AREA_RADIUS_LONG = "vip-safe-area-radius";

	private static final char ARG_ROUND_COUNT_SHORT = 'd';
	
	private static final String ARG_ROUND_COUNT_LONG = "round-count";
	
	private static final char ARG_ROUND_TIME_SECS_SHORT = 't';
	
	private static final String ARG_ROUND_TIME_SECS_LONG = "round-time-secs";

	private static final char ARG_FIXED_VIP_NAME_SHORT = 'x';
	
	private static final String ARG_FIXED_VIP_NAME_LONG = "fixed-vip-name-prefix";
	
	private static final char ARG_SCORE_VIP_KILLED_CT_SHORT = '1';
	
	private static final String ARG_SCORE_VIP_KILLED_CT_LONG = "score-vip-killed-counter-terrorists";
	
	private static final char ARG_SCORE_VIP_KILLED_T_SHORT = '2';
	
	private static final String ARG_SCORE_VIP_KILLED_T_LONG = "score-vip-killed-terrorists";
	
	private static final char ARG_SCORE_VIP_SAFE_CT_SHORT = '3';
	
	private static final String ARG_SCORE_VIP_SAFE_CT_LONG = "score-vip-safe-counter-terrorists";
	
	private static final char ARG_SCORE_VIP_SAFE_T_SHORT = '4';
	
	private static final String ARG_SCORE_VIP_SAFE_T_LONG = "score-vip-safe-terrorists";
	
	// UTILS

	private static JSAP jsap;
	
	private static JSAPResult config;

	private static boolean headerOutput = false;

	private static String ut2004HomeDir;

	private static String botJars;
	
	private static String[] botJarsSeparated;

	private static String botNames;
	
	private static String[] botNamesSeparated;
	
	private static String botTeams;
	
	private static String[] botTeamsSeparated;
	
	private static String ctSpawnAreas;
	
	private static String[] ctSpawnAreasSeparated;
	
	private static String tSpawnAreas;
	
	private static String[] tSpawnAreasSeparated;
	
	private static String vipSafeAreas;
	
	private static String[] vipSafeAreasSeparated;
	
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
	
	private static VIPGameConfig gameConfig;
	
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
        System.out.println("Usage: java -jar ut2004-vip....jar ");
        System.out.println("                " + jsap.getUsage());
        System.out.println();
        System.out.println(jsap.getHelp());
        System.out.println();
        throw new RuntimeException("FAILURE: " + errorMessage);
	}

	private static void header() {
		if (headerOutput) return;
		System.out.println();
		System.out.println("=================================");
		System.out.println("Pogamut UT2004 VIP Match Executor");
		System.out.println("=================================");
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
		
		// VIP GAME CONFIG
		
		FlaggedOption opt12 = new FlaggedOption(ARG_CT_SPAWN_AREAS_LONG)
			.setStringParser(JSAP.STRING_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_CT_SPAWN_AREAS_SHORT)
			.setLongFlag(ARG_CT_SPAWN_AREAS_LONG)
			.setDefault("[2000;-915;-50],[-1000;415;25]");// TODO
		opt12.setHelp("Area where counter-terrorist (blue) team may spawn, format [x;y;z](,[x;y;z]){0,}. If multiple locations are provided, they will be chosen from at random every round. Default is configured for DM-TraningDay map.");		
		jsap.registerParameter(opt12);
		
		FlaggedOption opt13 = new FlaggedOption(ARG_T_SPAWN_AREAS_LONG)
			.setStringParser(JSAP.STRING_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_T_SPAWN_AREAS_SHORT)
			.setLongFlag(ARG_T_SPAWN_AREAS_LONG)
			.setDefault("[2000;-915;-50],[-1000;415;25]");// TODO
		opt13.setHelp("Area where terrorist (red) team may spawn, format [x;y;z](,[x;y;z]){0,}. If multiple locations are provided, they will be chosen from at random every round. Default is configured for DM-TraningDay map.");		
		jsap.registerParameter(opt13);
		
		FlaggedOption opt14 = new FlaggedOption(ARG_VIP_SAFE_AREAS_LONG)
			.setStringParser(JSAP.STRING_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_VIP_SAFE_AREAS_SHORT)
			.setLongFlag(ARG_VIP_SAFE_AREAS_LONG)
			.setDefault("[2000;-915;-50],[-1000;415;25]");// TODO
		opt14.setHelp("Safe area VIP needs to reach in order for counter-terrorist (blue) team to win, format [x;y;z](,[x;y;z]){0,}. If multiple locations are provided, they will be chosen from at random every round. Default is configured for DM-TraningDay map.");		
		jsap.registerParameter(opt14);
		
		FlaggedOption opt15 = new FlaggedOption(ARG_VIP_SAFE_AREA_RADIUS_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_VIP_SAFE_AREA_RADIUS_SHORT)
			.setLongFlag(ARG_VIP_SAFE_AREA_RADIUS_LONG)
			.setDefault("100");
		opt15.setHelp("How big is the safe area around the vip safe area point.");
		jsap.registerParameter(opt15);
		
		FlaggedOption opt16 = new FlaggedOption(ARG_ROUND_COUNT_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_ROUND_COUNT_SHORT)
			.setLongFlag(ARG_ROUND_COUNT_LONG)
			.setDefault("10");
		opt16.setHelp("Number of VIP game rounds to be played.");
		jsap.registerParameter(opt16);
		
		FlaggedOption opt17 = new FlaggedOption(ARG_ROUND_TIME_SECS_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_ROUND_TIME_SECS_SHORT)
			.setLongFlag(ARG_ROUND_TIME_SECS_LONG)
			.setDefault("300");
		opt17.setHelp("Total length of single VIP game round. If VIP does not reach safe area by this time, terrorist (red) team wins.");
		jsap.registerParameter(opt17);
		
		FlaggedOption opt18 = new FlaggedOption(ARG_FIXED_VIP_NAME_LONG)
			.setStringParser(JSAP.STRING_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_FIXED_VIP_NAME_SHORT)
			.setLongFlag(ARG_FIXED_VIP_NAME_LONG);
		opt18.setHelp("When specified, all VIP rounds will be played with fixed VIP; player in counter-terrorist (blue) team with the name that will begin with specified string will always be given a role of VIP.");
		jsap.registerParameter(opt18);
		
		
		FlaggedOption opt20 = new FlaggedOption(ARG_SCORE_VIP_KILLED_CT_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_SCORE_VIP_KILLED_CT_SHORT)
			.setLongFlag(ARG_SCORE_VIP_KILLED_CT_LONG)
			.setDefault("0");
		opt20.setHelp("Penalization-score for counter-terrorist (blue) team to be given to for losing the round.");		
		jsap.registerParameter(opt20);
		
		FlaggedOption opt21 = new FlaggedOption(ARG_SCORE_VIP_KILLED_T_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_SCORE_VIP_KILLED_T_SHORT)
			.setLongFlag(ARG_SCORE_VIP_KILLED_T_LONG)
			.setDefault("100");
		opt21.setHelp("Score for terrorist (red) team to be awarded with for winning the round.");		
		jsap.registerParameter(opt21);
		
		FlaggedOption opt22 = new FlaggedOption(ARG_SCORE_VIP_KILLED_CT_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_SCORE_VIP_KILLED_CT_SHORT)
			.setLongFlag(ARG_SCORE_VIP_KILLED_CT_LONG)
			.setDefault("100");
		opt22.setHelp("Score for counter-terrorist (blue) team to be awarded with for winning the round.");		
		jsap.registerParameter(opt22);
		
		FlaggedOption opt23 = new FlaggedOption(ARG_SCORE_VIP_KILLED_T_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_SCORE_VIP_KILLED_T_SHORT)
			.setLongFlag(ARG_SCORE_VIP_KILLED_T_LONG)
			.setDefault("0");
		opt23.setHelp("Penalization-score for terrorist (red) team to be given to for losing the round.");		
		jsap.registerParameter(opt23);
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
	    botTeams = config.getString(ARG_BOT_TEAMs_LONG);
	    botTeamsSeparated = botTeams == null ? null : botTeams.split(";");
	    ctSpawnAreas = config.getString(ARG_CT_SPAWN_AREAS_LONG);
	    ctSpawnAreasSeparated = ctSpawnAreas == null ? null : tSpawnAreas.split(",");
	    tSpawnAreas = config.getString(ARG_T_SPAWN_AREAS_LONG);
	    tSpawnAreasSeparated = tSpawnAreas == null ? null : tSpawnAreas.split(",");
	    vipSafeAreas = config.getString(ARG_T_SPAWN_AREAS_LONG);
	    vipSafeAreasSeparated = vipSafeAreas == null ? null : vipSafeAreas.split(",");
	    map = config.getString(ARG_MAP_NAME_LONG);
	    serverName = config.getString(ARG_SERVER_NAME_LONG);
	    resultDir = config.getString(ARG_RESULT_DIR_LONG);
	    matchName = config.getString(ARG_MATCH_NAME_LONG);
	    humanLikeLog = config.getBoolean(ARG_HUMAN_LIKE_LOG_LONG);
	    ut2004Port = config.getInt(ARG_UT2004_PORT_LONG);
	    
	    gameConfig = new VIPGameConfig();
	    
	    gameConfig.setTargetMap(map);
	    
	    String fixedVIP = config.getString(ARG_FIXED_VIP_NAME_LONG);
	    if (fixedVIP == null) {
	    	gameConfig.setFixedVIP(false);
	    	gameConfig.setFixedVIPNamePrefix(null);
	    } else {
	    	gameConfig.setFixedVIP(true);
	    	gameConfig.setFixedVIPNamePrefix(fixedVIP);
	    }
	    	
	    gameConfig.setRoundTimeUT((double)config.getInt(ARG_ROUND_TIME_SECS_LONG));
	    gameConfig.setRoundCount(config.getInt(ARG_ROUND_COUNT_LONG));
	    
	    if (ctSpawnAreasSeparated != null ){
	    	// CT SPAWN AREAS
	    	List<Location> areas = new ArrayList<Location>();
	    	for (String area : ctSpawnAreasSeparated) {
	    		try {
	    			Location loc = new Location(area.trim());
	    			areas.add(loc);
	    		} catch (Exception e) {
	    			fail("Failed to parse '--" + ARG_CT_SPAWN_AREAS_LONG + " " + config.getString(ARG_CT_SPAWN_AREAS_LONG) + "'; Required format [x;y;z], multiple locations must be ',' separated.");
	    		}
	    	}
	    	gameConfig.setCtsSpawnAreas(areas.toArray(new Location[0]));
	    }
	    
	    if (tSpawnAreasSeparated != null)  {
	    	// T SPAWN AREAS
	    	List<Location> areas = new ArrayList<Location>();
	    	for (String area : tSpawnAreasSeparated) {
	    		try {
	    			Location loc = new Location(area.trim());
	    			areas.add(loc);
	    		} catch (Exception e) {
	    			fail("Failed to parse '--" + ARG_T_SPAWN_AREAS_LONG + " " + config.getString(ARG_T_SPAWN_AREAS_LONG) + "'; Required format [x;y;z], multiple locations must be ',' separated.");
	    		}
	    	}
	    	gameConfig.setTsSpawnAreas(areas.toArray(new Location[0]));
	    }
	    
	    if (vipSafeAreasSeparated != null) {
	    	// VIP SAFE AREAS
	    	List<Location> areas = new ArrayList<Location>();
	    	for (String area : vipSafeAreasSeparated) {
	    		try {
	    			Location loc = new Location(area.trim());
	    			areas.add(loc);
	    		} catch (Exception e) {
	    			fail("Failed to parse '--" + ARG_VIP_SAFE_AREAS_LONG + " " + config.getString(ARG_VIP_SAFE_AREAS_LONG) + "'; Required format [x;y;z], multiple locations must be ',' separated.");
	    		}
	    	}
	    	gameConfig.setVipSafeAreas(areas.toArray(new Location[0]));
	    }
	    
	    gameConfig.setVipSafeAreaRadius(config.getInt(ARG_VIP_SAFE_AREA_RADIUS_LONG));
	    
	    gameConfig.setVipKilledCTsScore(config.getInt(ARG_SCORE_VIP_KILLED_CT_LONG));
	    gameConfig.setVipKilledTsScore(config.getInt(ARG_SCORE_VIP_KILLED_T_LONG));
	    gameConfig.setVipSafeCTsScore(config.getInt(ARG_SCORE_VIP_SAFE_CT_LONG));
	    gameConfig.setVipSafeTsScore(config.getInt(ARG_SCORE_VIP_SAFE_T_LONG));
	}
	
	private static void sanityChecks() {
		System.out.println("Sanity checks...");
		
		if (botJarsSeparated == null) {
			fail("Bot jar(s) was/were not specified correctly.");
		}
		
		if (botNamesSeparated == null) {
			fail("Bot name(s) was/were not specified correctly.");
		}
		
		if (botTeamsSeparated == null) {
			fail("Bot team(s) was/were not specified correctly.");
		}
		
		if (botJarsSeparated.length != botNamesSeparated.length) {
			fail("Bot jar(s) and name(s) numbers mismatch. I've parsed " + botJarsSeparated.length + " bot jar files != " + botNamesSeparated.length + " of bot names.");
		}
		if (botJarsSeparated.length != botTeamsSeparated.length) {
			fail("Bot jar(s) and team(s) numbers mismatch. I've parsed " + botJarsSeparated.length + " bot jar files != " + botTeamsSeparated.length + " of bot teams.");
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
	    
	    VIPGameConfig csConfig = gameConfig;
	    
	    if (csConfig.isFixedVIP() && (csConfig.getFixedVIPNamePrefix() == null || csConfig.getFixedVIPNamePrefix().isEmpty())) {
			fail("Fixed VIP configured as TRUE, but no name specified.");
		}
		
		if (csConfig.getTargetMap() == null || csConfig.getTargetMap().isEmpty()) {
			fail("No targetMap specified.");
		}
		
		if (csConfig.getRoundCount() <= 0) {
			fail("RoundCount == " + csConfig.getRoundCount() + " <= 0, invalid. There must be at least 1 round to be played.");
		}
		
		if (csConfig.getCtsSpawnAreas() == null || csConfig.getCtsSpawnAreas().length <= 0) {
			fail("Counter-terrorist (blue) team spawn area not specified correctly.");
		}
		
		if (csConfig.getTsSpawnAreas() == null || csConfig.getTsSpawnAreas().length <= 0) {
			fail("Terrorist (red) team spawn area not specified correctly.");
		}
		
		if (csConfig.getVipSafeAreas() == null || csConfig.getVipSafeAreas().length <= 0) {
			fail("VIP safe area not specified correctly.");
		}
		
		if (csConfig.getVipSafeAreaRadius() < 25) {
			fail("VIP safe area radius == " + csConfig.getVipSafeAreaRadius() + " < 25, invalid, UT2004 sensor snapshots are not that precise to be able to handle small areas.");
		}
	    
	    System.out.println("Sanity checks OK!");
	    
	    System.out.println("Hide&Seek Game Configuration...");
	    
	    System.out.println("-- Map:                            " + csConfig.getTargetMap());
	    System.out.println("-- Round count:                    " + csConfig.getRoundCount());
	    System.out.println("-- Round time (secs):              " + csConfig.getRoundTimeUT());
	    if (csConfig.isFixedVIP()) {
	    	System.out.println("-- Fixed VIP name prefix:          " + csConfig.getFixedVIPNamePrefix());
	    } else {
	    	System.out.println("-- Random VIP every round");
	    }	    
	    System.out.println("-- CT (blue) team spawn areas:     " + ctSpawnAreas);
	    System.out.println("-- T (red) team spawn areas:       " + tSpawnAreas);
	    System.out.println("-- VIP safe areas:                 " + vipSafeAreas);
	    System.out.println("-- VIP safe area radius:           " + csConfig.getVipSafeAreaRadius());
	    System.out.println("VIP Scoring Configuration:");
	    System.out.println("-- VIP safe => CT wins bonus:      " + csConfig.getVipSafeCTsScore());
	    System.out.println("-- VIP safe => T loses penalty:    " + csConfig.getVipSafeTsScore());
	    System.out.println("-- VIP killed => CT loses penalty: " + csConfig.getVipKilledCTsScore());
	    System.out.println("-- VIP killed => T wins bonus:     " + csConfig.getVipKilledTsScore());
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
		UT2004VIPConfig config = new UT2004VIPConfig();
		
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
		UT2004VIP match = new UT2004VIP(config, log);
		
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
