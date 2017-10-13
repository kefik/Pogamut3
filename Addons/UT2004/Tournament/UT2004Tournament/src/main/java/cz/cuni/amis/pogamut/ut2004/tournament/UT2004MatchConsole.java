package cz.cuni.amis.pogamut.ut2004.tournament;

import java.io.File;
import java.util.Iterator;
import java.util.logging.Level;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;

import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.ut2004.tournament.capturetheflag.UT2004CaptureTheFlag;
import cz.cuni.amis.pogamut.ut2004.tournament.capturetheflag.UT2004CaptureTheFlagConfig;
import cz.cuni.amis.pogamut.ut2004.tournament.deathmatch.UT2004DeathMatch;
import cz.cuni.amis.pogamut.ut2004.tournament.deathmatch.UT2004DeathMatchConfig;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004BotConfig;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004HumanConfig;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004MatchConfig;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004NativeBotConfig;
import cz.cuni.amis.pogamut.ut2004.tournament.teamdeathmatch.UT2004TeamDeathMatch;
import cz.cuni.amis.pogamut.ut2004.tournament.teamdeathmatch.UT2004TeamDeathMatchConfig;

public class UT2004MatchConsole {
	
	public enum MatchType {
		
		DM("DM", "Death Match", false, "BotDeathMatch"),
		TDM("TDM", "Team Death Match", true, "BotTeamGame"),
		CTF("CTF", "Capture the flag", true, "BotCTFGame"),
		DD("DD", "Double Domination", true, "BotDoubleDomination"),
		;
		
		String shortName;
		String name;
		boolean teamGame;
		String uccGameType;
		
		private MatchType(String shortName, String name, boolean teamGame, String uccGameType) {
			this.shortName = shortName;
			this.name = name;
			this.teamGame = teamGame;
			this.uccGameType = uccGameType;
		}
		
	}
	
	private static final char ARG_MATCH_TYPE_SHORT = 'y';
	
	private static final String ARG_MATCH_TYPE_LONG = "match-type";
	
	private static final char ARG_UT2004_HOME_DIR_SHORT = 'u';
	
	private static final String ARG_UT2004_HOME_DIR_LONG = "ut2004-home-dir";
	
	private static final char ARG_NATIVE_COUNT_SHORT = 'c';
	
	private static final String ARG_NATIVE_COUNT_LONG = "native-count";
	
	private static final char ARG_NATIVE_NAMES_SHORT = 'd';
	
	private static final String ARG_NATIVE_NAMES_LONG = "native-names";
	
	private static final char ARG_NATIVE_SKILLS_SHORT = 'e';
	
	private static final String ARG_NATIVE_SKILLS_LONG = "native-skills";
	
	private static final char ARG_NATIVE_TEAMS_SHORT = 'g';
	
	private static final String ARG_NATIVE_TEAMS_LONG = "native-teams";
	
	private static final char ARG_HUMAN_COUNT_SHORT = 'x';
	
	private static final String ARG_HUMAN_COUNT_LONG = "human-count";
	
	private static final char ARG_HUMAN_TEAMS_SHORT = 'z';
	
	private static final String ARG_HUMAN_TEAMS_LONG = "human-teams";
	
	private static final char ARG_BOT_JARs_SHORT = 'a';
	
	private static final String ARG_BOT_JARs_LONG = "bot-jars";
	
	private static final char ARG_BOT_NAMES_SHORT = 'b';
	
	private static final String ARG_BOT_NAMES_LONG = "bot-names";
	
	private static final char ARG_BOT_SKINS_SHORT = 'k';
	
	private static final String ARG_BOT_SKINS_LONG = "bot-skins";
	
	private static final char ARG_BOT_SKILLS_SHORT = 'l';
	
	private static final String ARG_BOT_SKILLS_LONG = "bot-skills";
	
	private static final char ARG_BOT_TEAMS_SHORT = 'i';
	
	private static final String ARG_BOT_TEAMS_LONG = "bot-teams";
	
	private static final char ARG_MAP_NAME_SHORT = 'm';
	
	private static final String ARG_MAP_NAME_LONG = "map-name";
	
	private static final char ARG_MATCH_NAME_SHORT = 'n';
	
	private static final String ARG_MATCH_NAME_LONG = "match-name";
	
	private static final char ARG_RESULT_DIR_SHORT = 'r';
	
	private static final String ARG_RESULT_DIR_LONG = "result-directory";
	
	private static final char ARG_SERVER_NAME_SHORT = 's';
	
	private static final String ARG_SERVER_NAME_LONG = "server-name";
	
	private static final char ARG_SCORE_LIMIT_SHORT = 'f';
	
	private static final String ARG_SCORE_LIMIT_LONG = "score-limit";
	
	private static final char ARG_TIMEOUT_MINUTES_SHORT = 't';
	
	private static final String ARG_TIMEOUT_MINUTES_LONG = "timeout-minutes";
	
	private static final char ARG_HUMAN_LIKE_LOG_SHORT = 'h';
	
	private static final String ARG_HUMAN_LIKE_LOG_LONG = "human-like-log";
	
	private static final char ARG_UT2004_PORT_SHORT = 'p';
	
	private static final String ARG_UT2004_PORT_LONG = "ut2004-port";
	
	private static final char ARG_START_TC_SERVER_SHORT = 'q';
	
	private static final String ARG_START_TC_SERVER_LONG = "start-tc-server";

	private static JSAP jsap;

	private static boolean headerOutput = false;

	private static String ut2004HomeDir;
	
	private static int nativeCount;
	
	private static String matchTypeName;
	
	private static MatchType matchType;
	
	private static String nativeNames;
	
	private static String[] nativeNamesSeparated;
	
	private static String nativeSkills;
	
	private static String[] nativeSkillsSeparated;
	
	private static Integer[] nativeSkillsNumbers;
	
	private static String nativeTeams;
	
	private static String[] nativeTeamsSeparated;
	
	private static Integer[] nativeTeamsNumbers;
	
	public static int humanCount;
	
	private static String humanTeams;
	
	private static String[] humanTeamsSeparated;
	
	private static Integer[] humanTeamsNumbers;

	private static int botCount;
	
	private static String botJars;
	
	private static String[] botJarsSeparated;

	private static String botNames;
	
	private static String[] botNamesSeparated;
	
	private static String botSkills;
	
	private static String[] botSkillsSeparated;
	
	private static Integer[] botSkillsNumbers;
	
	private static String botTeams;
	
	private static String[] botTeamsSeparated;
	
	private static Integer[] botTeamsNumbers;
	
	private static String botSkins;
	
	private static String[] botSkinsSeparated;
	
	private static File[] botJarFiles;
	
	private static String map;

	private static String serverName;

	private static String resultDir;

	private static String matchName;
	
	private static int scoreLimit;
	
	private static int timeoutMinutes;

	private static JSAPResult config;

	private static File ut2004HomeDirFile;

	private static File bot1JarFile;

	private static File bot2JarFile;

	private static File mapsDirFile;

	private static File mapFile;

	private static File ut2004SystemDirFile;

	private static File ut2004IniFile;

	private static boolean humanLikeLog;
	
	private static boolean startTCServer;

	private static int ut2004Port;	
	
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
        System.out.println("Usage: java -jar ut2004-tournament-1v1....jar ");
        System.out.println("                " + jsap.getUsage());
        System.out.println();
        System.out.println(jsap.getHelp());
        System.out.println();
        throw new RuntimeException("FAILURE: " + errorMessage);
	}

	private static void header() {
		if (headerOutput) return;
		System.out.println();
		System.out.println("=============================");
		System.out.println("Pogamut UT2004 Match Executor");
		System.out.println("=============================");
		System.out.println();
		headerOutput = true;
	}
	
	private static String getMatchTypes() {
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for (MatchType matchType : MatchType.values()) {
			if (first) first = false;
			else sb.append(", ");
			sb.append(matchType.shortName + " (" + matchType.name + ")");
		}
		return sb.toString();
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
        
        FlaggedOption opt111 = new FlaggedOption(ARG_MATCH_TYPE_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(true) 
	    	.setShortFlag(ARG_MATCH_TYPE_SHORT)
	    	.setLongFlag(ARG_MATCH_TYPE_LONG);    
	    opt111.setHelp("Type of the match to execute. Valid values: " + getMatchTypes());
	    
	    jsap.registerParameter(opt111);
        
        FlaggedOption opt2 = new FlaggedOption(ARG_BOT_JARs_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(false) 
	    	.setShortFlag(ARG_BOT_JARs_SHORT)
	    	.setLongFlag(ARG_BOT_JARs_LONG);    
	    opt2.setHelp("Semicolon separated PATH/TO/JAR/file1;PATH/TO/JAR/file2 containing executable jars of bots.");
	
	    jsap.registerParameter(opt2);
	    
	    FlaggedOption opt3 = new FlaggedOption(ARG_BOT_NAMES_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(false) 
	    	.setShortFlag(ARG_BOT_NAMES_SHORT)
	    	.setLongFlag(ARG_BOT_NAMES_LONG);    
	    opt3.setHelp("Semicolon separated name1;name2;name3 (ids) that should be given to bots.");
	    
	    jsap.registerParameter(opt3);
	    
	    FlaggedOption opt31 = new FlaggedOption(ARG_BOT_SKILLS_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(false) 
	    	.setShortFlag(ARG_BOT_SKILLS_SHORT)
	    	.setLongFlag(ARG_BOT_SKILLS_LONG);    
	    opt31.setHelp("Semicolon separated skill1;skill2;skill3 (desired skill levels) that should be given to bots. Can have 'empty space', e.g 1;;2, within to mark 'use bot supplied default value'.");
	
	    jsap.registerParameter(opt31);
	    
	    FlaggedOption opt32 = new FlaggedOption(ARG_BOT_SKINS_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(false) 
	    	.setShortFlag(ARG_BOT_SKINS_SHORT)
	    	.setLongFlag(ARG_BOT_SKINS_LONG);    
	    opt32.setHelp("Semicolon separated skin1;skin2;skin3 (skins) that should be given to bots. Can have 'empty space', e.g skin1;;skin3, within to mark 'use bot supplied default value'.");
	    
	    jsap.registerParameter(opt32);
	    
	    FlaggedOption opt33 = new FlaggedOption(ARG_BOT_TEAMS_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(false) 
	    	.setShortFlag(ARG_BOT_TEAMS_SHORT)
	    	.setLongFlag(ARG_BOT_TEAMS_LONG);    
	    opt33.setHelp("Semicolon separated team1;team2;team3 (desired teams) that should bots be in.");
	
	    jsap.registerParameter(opt33);
    
	    FlaggedOption opt6 = new FlaggedOption(ARG_MAP_NAME_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(false) 
	    	.setShortFlag(ARG_MAP_NAME_SHORT)
	    	.setLongFlag(ARG_MAP_NAME_LONG);    
	    opt6.setHelp("Map where the game should be played (e.g. DM-1on1-Albatross).");
	
	    jsap.registerParameter(opt6);
        
	    FlaggedOption opt7 = new FlaggedOption(ARG_MATCH_NAME_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(false) 
	    	.setShortFlag(ARG_MATCH_NAME_SHORT)
	    	.setLongFlag(ARG_MATCH_NAME_LONG)
	    	.setDefault("DMMatch1v1");    
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
			.setDefault("DMMatch1v1");
		opt9.setHelp("Server name that should be advertised via LAN.");
		
		jsap.registerParameter(opt9);
		
		FlaggedOption opt10 = new FlaggedOption(ARG_SCORE_LIMIT_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_SCORE_LIMIT_SHORT)
			.setLongFlag(ARG_SCORE_LIMIT_LONG)
			.setDefault("20");
		opt10.setHelp("DeathMatch - frag limit, Capture The Flag - team score limit");
		
		jsap.registerParameter(opt10);
		
		FlaggedOption opt11 = new FlaggedOption(ARG_TIMEOUT_MINUTES_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_TIMEOUT_MINUTES_SHORT)
			.setLongFlag(ARG_TIMEOUT_MINUTES_LONG)
			.setDefault("20");
		opt11.setHelp("Match timeout in minutes.");
		
		jsap.registerParameter(opt11);
		
		Switch opt12 = new Switch(ARG_HUMAN_LIKE_LOG_LONG)
			.setShortFlag(ARG_HUMAN_LIKE_LOG_SHORT)
			.setLongFlag(ARG_HUMAN_LIKE_LOG_LONG)
			.setDefault("false");
		opt12.setHelp("Whether to produce log for 'HumanLike Project' analysis.");
		
		jsap.registerParameter(opt12);
		
		FlaggedOption opt13 = new FlaggedOption(ARG_UT2004_PORT_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_UT2004_PORT_SHORT)
			.setLongFlag(ARG_UT2004_PORT_LONG)
			.setDefault("7777");
		opt13.setHelp("UT2004 port for the dedicated server (1-32000).");
		
		jsap.registerParameter(opt13);
		
		FlaggedOption opt14 = new FlaggedOption(ARG_NATIVE_COUNT_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_NATIVE_COUNT_SHORT)
			.setLongFlag(ARG_NATIVE_COUNT_LONG)
			.setDefault("0");
		opt14.setHelp("Number of native bots to participate within the match.");
		
		jsap.registerParameter(opt14);
		
		FlaggedOption opt15 = new FlaggedOption(ARG_NATIVE_NAMES_LONG)
			.setStringParser(JSAP.STRING_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_NATIVE_NAMES_SHORT)
			.setLongFlag(ARG_NATIVE_NAMES_LONG);
		opt15.setHelp("Semicolon separated name1;name2;... of names to be given to native bots.");
		
		jsap.registerParameter(opt15);
		
		FlaggedOption opt16 = new FlaggedOption(ARG_NATIVE_SKILLS_LONG)
			.setStringParser(JSAP.STRING_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_NATIVE_SKILLS_SHORT)
			.setLongFlag(ARG_NATIVE_SKILLS_LONG);
		opt16.setHelp("Semicolon separated skill1;skill2;... of skill levels of native bots.");
		
		jsap.registerParameter(opt16);
		
		FlaggedOption opt17 = new FlaggedOption(ARG_NATIVE_TEAMS_LONG)
			.setStringParser(JSAP.STRING_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_NATIVE_TEAMS_SHORT)
			.setLongFlag(ARG_NATIVE_TEAMS_LONG);
		opt17.setHelp("Semicolon separated team1;team2;... of teams the native bots should be in.");
		
		jsap.registerParameter(opt17);
		
		FlaggedOption opt20 = new FlaggedOption(ARG_HUMAN_COUNT_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_HUMAN_COUNT_SHORT)
			.setLongFlag(ARG_HUMAN_COUNT_LONG)
			.setDefault("0");
		opt20.setHelp("Number of humans that should participate in the match.");
		
		jsap.registerParameter(opt20);
		
		FlaggedOption opt21 = new FlaggedOption(ARG_HUMAN_TEAMS_LONG)
			.setStringParser(JSAP.STRING_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_HUMAN_TEAMS_SHORT)
			.setLongFlag(ARG_HUMAN_TEAMS_LONG);
		opt21.setHelp("Semicolon separated team1;team2;... of teams humans should be in.");
		
		jsap.registerParameter(opt21);
		
		Switch opt22 = new Switch(ARG_START_TC_SERVER_LONG)
			.setShortFlag(ARG_START_TC_SERVER_SHORT)
			.setLongFlag(ARG_START_TC_SERVER_LONG);
		opt22.setHelp("Whether to start instance of UT2004TCServer to allow Java chat between bots.");
		
		jsap.registerParameter(opt22);
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
		
		matchTypeName = config.getString(ARG_MATCH_TYPE_LONG);
		
		botJars = config.getString(ARG_BOT_JARs_LONG);
	    botJarsSeparated = botJars == null ? null : botJars.split(";");
	    botCount = botJarsSeparated == null ? 0 : botJarsSeparated.length;
	    botNames = config.getString(ARG_BOT_NAMES_LONG);
	    botNamesSeparated = botNames == null ? null : botNames.split(";");
	    botSkills = config.getString(ARG_BOT_SKILLS_LONG);
	    botSkillsSeparated = botSkills == null || botSkills.length() == 0 ? null : botSkills.split(";");
	    botSkins = config.getString(ARG_BOT_SKINS_LONG);
	    botSkinsSeparated = botSkins == null || botSkins.length() == 0 ? null : botSkins.split(";");
	    botTeams = config.getString(ARG_BOT_TEAMS_LONG);
	    botTeamsSeparated = botTeams == null || botTeams.length() == 0 ? null : botTeams.split(";");
	    
	    nativeCount = config.getInt(ARG_NATIVE_COUNT_LONG);
	    nativeNames = config.getString(ARG_NATIVE_NAMES_LONG);
	    nativeNamesSeparated = nativeNames == null ? null : nativeNames.split(";");
	    nativeSkills = config.getString(ARG_NATIVE_SKILLS_LONG);
	    nativeSkillsSeparated = nativeSkills == null ? null : nativeSkills.split(";");
	    nativeTeams = config.getString(ARG_NATIVE_TEAMS_LONG);
	    nativeTeamsSeparated = nativeTeams == null ? null : nativeTeams.split(";");
	    
	    humanCount = config.getInt(ARG_HUMAN_COUNT_LONG);
	    humanTeams = config.getString(ARG_HUMAN_TEAMS_LONG);
	    humanTeamsSeparated = humanTeams == null ? null : humanTeams.split(";");
	    
	    map = config.getString(ARG_MAP_NAME_LONG);
	    serverName = config.getString(ARG_SERVER_NAME_LONG);
	    resultDir = config.getString(ARG_RESULT_DIR_LONG);
	    matchName = config.getString(ARG_MATCH_NAME_LONG);
	    scoreLimit = config.getInt(ARG_SCORE_LIMIT_LONG);
	    timeoutMinutes = config.getInt(ARG_TIMEOUT_MINUTES_LONG);
	    humanLikeLog = config.getBoolean(ARG_HUMAN_LIKE_LOG_LONG);
	    startTCServer = config.getBoolean(ARG_START_TC_SERVER_LONG);
	    ut2004Port = config.getInt(ARG_UT2004_PORT_LONG);
	}
	
	private static void sanityChecks() {
		System.out.println("Sanity checks...");
		
		// UT2004
		
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
	    
	    // MATCH TYPE
	    for (MatchType validMatchType : MatchType.values()) {
	    	if (validMatchType.shortName.equalsIgnoreCase(matchTypeName)) {
	    		matchType = validMatchType;
	    		break;
	    	}
	    }
	    if (matchType == null) {
	    	fail("Invalid match type specified '" + matchTypeName + "', valid values: " + getMatchTypes());
	    }
	    
	    System.out.println("-- Match type set as " + matchType.name + " (" + matchType.shortName + ")");
	    
	    // BOTS
	    
	    if (botCount > 0) {
	    	
	    	System.out.println("-- Adding " + botCount + " custom bots into the match");
	    	
		    if (botNamesSeparated == null) {
				fail("Bot name(s) was/were not specified correctly.");
			}
			
			if (botJarsSeparated.length != botNamesSeparated.length) {
				fail("Bot jar(s) and name(s) numbers mismatch. I've parsed " + botJarsSeparated.length + " bot jar files != " + botNamesSeparated.length + " of bot names.");
			}
			
			if (botSkillsSeparated != null && botSkillsSeparated.length != botJarsSeparated.length) {
				fail("Bot jar(s) and skills(s) numbers mismatch. I've parsed " + botJarsSeparated.length + " bot jar files != " + botSkillsSeparated.length + " of bot skill levels.");
			}
			
			if (botSkinsSeparated != null && botSkinsSeparated.length != botJarsSeparated.length) {
				fail("Bot jar(s) and skins(s) numbers mismatch. I've parsed " + botJarsSeparated.length + " bot jar files != " + botSkinsSeparated.length + " of bot skins.");
			}
			
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
		    
		    if (botSkillsSeparated != null) {
		    	botSkillsNumbers = new Integer[botSkillsSeparated.length];
		    	for (int i = 0; i < botSkillsSeparated.length; ++i) {
		    		if (botSkillsSeparated[i] == null || botSkillsSeparated[i].length() == 0) {
		    			botSkillsNumbers[i] = null;
		    			System.out.println("-- Bot" + (i+1) + " skill level will be set to default");
		    			continue;
		    		} 
		    		
		    		Integer number = null;
		    		try {
		    			number = Integer.parseInt(botSkillsSeparated[i]);
		    		} catch (Exception e) {
		    			fail("Bot " + (i+1) + " skill level specified as '" + botSkillsSeparated[i] + "', which is not a number!");
		    		}
		    		if (number < 0 || number > 7) {
		    			fail("Bot " + (i+1) + " skill level specified as '" + botSkillsSeparated[i] + "' and parsed as '" + number + "', which is of unsupported value, not from the range 0-7!");
		    		}
		    		
		    		botSkillsNumbers[i] = number;
		    		
		    		System.out.println("-- Bot" + (i+1) + " skill level set to " + number);
		    	}
		    	
		    	System.out.println("-- Bot skills ok");
		    }
		    
		    if (botSkinsSeparated != null) {
		    	for (int i = 0; i < botSkinsSeparated.length; ++i) {
		    		if (botSkinsSeparated[i] == null || botSkinsSeparated[i].length() == 0) {
		    			botSkinsSeparated[i] = null;
		    			System.out.println("-- Bot" + (i+1) + " skin will be supplied by the bot itself");
		    			continue;
		    		} 
		    		System.out.println("-- Bot" + (i+1) + " skin set to '" + botSkinsSeparated[i] + "'");
		    	}
		    	
		    	System.out.println("-- Bot skins ok");
		    }
	    
		    if (matchType.teamGame) {
		    	if (botTeamsSeparated == null) {
		    		fail("Bot teams not specified, but a team game specified (" + matchType.name + ").");
		    	}
		    	if (botTeamsSeparated.length != botJarsSeparated.length) {
					fail("Bot jar(s) and team(s) numbers mismatch. I've parsed " + botJarsSeparated.length + " bot jar files != " + botTeamsSeparated.length + " of bot teams.");
				}
	    		botTeamsNumbers = new Integer[botTeamsSeparated.length];
		    	for (int i = 0; i < botTeamsSeparated.length; ++i) {
		    		if (botTeamsSeparated[i] == null || botTeamsSeparated[i].length() == 0) {
		    			fail("Bot" + (i+1) + " does not have a team number specified.");
		    			continue;
		    		} 
		    		
		    		Integer number = null;
		    		try {
		    			number = Integer.parseInt(botTeamsSeparated[i]);
		    		} catch (Exception e) {
		    			fail("Bot " + (i+1) + " team number specified as '" + botTeamsSeparated[i] + "', which is not a number!");
		    		}
		    		if (number < 0 || number > 3) {
		    			fail("Bot " + (i+1) + " team number specified as '" + botTeamsSeparated[i] + "' and parsed as '" + number + "', which is of unsupported value, not from the range 0-3!");
		    		}
		    		
		    		botTeamsNumbers[i] = number;
		    		
		    		System.out.println("-- Bot" + (i+1) + " team number set to " + number);
		    	}
		    	
		    	System.out.println("-- Bot teams ok");
		    }
	    } 
	    
	    // NATIVES
	    
	    if (nativeCount > 0) {
	    	
	    	if (nativeCount < 1 || nativeCount > 16) {
	    		fail("Could start match with 1-16 native bots at max!");
	    	}
	    	
	    	System.out.println("-- Adding " + nativeCount + " native bots into the match");
	    
		    if (nativeNamesSeparated == null) {
				fail("Native bot name(s) was/were not specified correctly.");
			}
			
			if (nativeCount != nativeNamesSeparated.length) {
				fail("Native bot name(s) numbers invalid. I've parsed " + nativeNamesSeparated.length + " native bot name != " + nativeCount + " of native bot count.");
			}
			
			if (nativeSkillsSeparated == null) {
				fail("Native bot skill(s) not specified correctly.");
			}
			
			if (nativeSkillsSeparated.length != nativeCount) {
				fail("Native bot skills(s) numbers mismatch. I've parsed " + nativeSkillsSeparated.length + " native bot skills != " + nativeCount + " of native bot count.");
			}
			
		    for (int i = 0; i < nativeNamesSeparated.length; ++i) {
		    	if (nativeNamesSeparated[i] == null || nativeNamesSeparated[i].isEmpty()) {
		    		fail("Native bot " + (i+1) + " invalid name '" + nativeNamesSeparated[i] +"' specified.");
		    	}
		    	System.out.println("-- Native bot " + (i+1) + " name set as '" + nativeNamesSeparated[i] + "'");
		    }
		    System.out.println("-- Native names ok");
		    
		    nativeSkillsNumbers = new Integer[nativeSkillsSeparated.length];
	    	for (int i = 0; i < nativeSkillsSeparated.length; ++i) {
	    		if (nativeSkillsSeparated[i] == null || nativeSkillsSeparated[i].length() == 0) {
	    			fail("Native bot " + (i+1) + " invalid skill level '" + nativeNamesSeparated[i] +"' specified.");
	    		} 
	    		
	    		Integer number = null;
	    		try {
	    			number = Integer.parseInt(nativeSkillsSeparated[i]);
	    		} catch (Exception e) {
	    			fail("Native bot " + i + " skill level specified as '" + nativeSkillsSeparated[i] + "', which is not a number!");
	    		}
	    		if (number < 0 || number > 7) {
	    			fail("Native bot " + i + " skill level specified as '" + nativeSkillsSeparated[i] + "' and parsed as '" + number + "', which is of unsupported value, not from the range 0-7!");
	    		}
	    		
	    		nativeSkillsNumbers[i] = number;
	    		
	    		System.out.println("-- Native bot " + (i+1) + " skill level set to " + number);
	    	}
		    	
		    System.out.println("-- Native bot skills OK");
		    
		    if (matchType.teamGame) {
		    	if (nativeTeamsSeparated == null) {
		    		fail("Native bot teams not specified, but a team game specified (" + matchType.name + ").");
		    	}
		    	if (nativeTeamsSeparated.length != nativeCount) {
					fail("Native bot team(s) and native count numbers mismatch. I've parsed " + nativeCount + " native count != " + nativeTeamsSeparated.length + " of native bot teams.");
				}
		    	nativeTeamsNumbers = new Integer[nativeTeamsSeparated.length];
		    	for (int i = 0; i < nativeTeamsSeparated.length; ++i) {
		    		if (nativeTeamsSeparated[i] == null || nativeTeamsSeparated[i].length() == 0) {
		    			fail("Native bot " + (i+1) + " does not have team number specified.");
		    			continue;
		    		} 
		    		
		    		Integer number = null;
		    		try {
		    			number = Integer.parseInt(nativeTeamsSeparated[i]);
		    		} catch (Exception e) {
		    			fail("Native bot " + (i+1) + " team number specified as '" + nativeTeamsSeparated[i] + "', which is not a number!");
		    		}
		    		if (number < 0 || number > 3) {
		    			fail("Native bot " + (i+1) + " team number specified as '" + nativeTeamsSeparated[i] + "' and parsed as '" + number + "', which is of unsupported value, not from the range 0-3!");
		    		}
		    		
		    		nativeTeamsNumbers[i] = number;
		    		
		    		System.out.println("-- Native bot " + (i+1) + " team number set to " + number);
		    	}
		    	
		    	System.out.println("-- Native bot teams ok");
		    }
	    }
	    
	    // HUMANS
	    
	    if (humanCount > 0) {
	    	
	    	if (humanCount < 1 || humanCount > 16) {
	    		fail("Could start match with 1-16 humans at max!");
	    	}
	    	
	    	System.out.println("-- Expect " + humanCount + " humans to participate in the match");
	    
		    if (matchType.teamGame) {
		    	if (humanTeamsSeparated == null) {
		    		fail("Teams for humans not specified, but a team game specified (" + matchType.name + ").");
		    	}
		    	if (humanTeamsSeparated.length != humanCount) {
					fail("Human team(s) and human count numbers mismatch. I've parsed " + humanCount + " human count != " + humanTeamsSeparated.length + " of human bot teams.");
				}
		    	humanTeamsNumbers = new Integer[humanTeamsSeparated.length];
		    	for (int i = 0; i < humanTeamsSeparated.length; ++i) {
		    		if (humanTeamsSeparated[i] == null || humanTeamsSeparated[i].length() == 0) {
		    			fail("Human " + (i+1) + " does not have a team number specified.");
		    			continue;
		    		} 
		    		
		    		Integer number = null;
		    		try {
		    			number = Integer.parseInt(humanTeamsSeparated[i]);
		    		} catch (Exception e) {
		    			fail("Human " + (i+1) + " team number specified as '" + humanTeamsSeparated[i] + "', which is not a number!");
		    		}
		    		if (number < 0 || number > 3) {
		    			fail("Human " + (i+1) + " team number specified as '" + humanTeamsSeparated[i] + "' and parsed as '" + number + "', which is of unsupported value, not from the range 0-3!");
		    		}
		    		
		    		humanTeamsNumbers[i] = number;
		    		
		    		System.out.println("-- Human " + (i+1) + " is expected to belong to the team number" + number);
		    	}
		    	
		    	System.out.println("-- Human teams ok");
		    }
	    }
	    
	    // COUNT
	    
	    if (botCount + nativeCount + humanCount < 2) {
	    	fail("There must be at least 2 participants specified, custom + natives + humans = " + botCount + " + " + nativeCount + " + " + humanCount + " = " + (botCount + nativeCount + humanCount) + " < 2.");
	    }
	    
	    // MATCH CONFIG
	    
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
	    
	    if (scoreLimit < 1) {
	    	fail("Invalid frag/score limit '" + scoreLimit +"' specified, must be >= 1.");
	    }
	    System.out.println("-- Frag limit set as '" + scoreLimit + "'");
	    
	    if (timeoutMinutes < 1) {
	    	fail("Invalid time limit '" + timeoutMinutes +"' specified, must be >= 1.");
	    }
	    System.out.println("-- Timeout set as '" + timeoutMinutes + "' minutes.");
	    
	    if (ut2004Port < 1 || ut2004Port > 32000) {
	    	fail("Invalid UT2004 port specified '" + ut2004Port + "', must be 1 <= port <= 32000.");
	    }
	    System.out.println("-- UT2004 port set as '" + ut2004Port + "'");

	    System.out.println("Sanity checks OK!");
	}
	
	private static void setGenericMatchConfigs(UT2004MatchConfig config) {
		// UT2004 INI
		config.getUT2004Ini().setPort(ut2004Port);
		config.getUT2004Ini().setServerName(serverName, serverName);
		config.getUT2004Ini().setDemoSpectatorClass(UT2004Ini.Value_DemoSpectatorClass);
		
		// UCC
		config.getUccConf().setStartOnUnusedPort(true);
	    config.getUccConf().setUnrealHome(ut2004HomeDir);	
	    config.getUccConf().setGameType(matchType.uccGameType);
	    config.getUccConf().setMapName(map);
	    
	    // OTHERS
	    config.setOutputDirectory(new File(resultDir));
	    config.setMatchId(matchName);
	    config.setHumanLikeLogEnabled(humanLikeLog);
	    config.setStartTCServer(startTCServer);
	}
	
	private static UT2004BotConfig[] createBotConfigs() {
		if (botCount <= 0) return null;
		UT2004BotConfig[] botConfigs = new UT2004BotConfig[botJarFiles.length];
		for (int i = 0; i < botJarFiles.length; ++i) {
			UT2004BotConfig botConfig = new UT2004BotConfig();
			botConfig.setBotId(botNamesSeparated[i]);
			botConfig.setPathToBotJar(botJarFiles[i].getAbsolutePath());
			if (botSkillsNumbers != null && botSkillsNumbers[i] != null) {
				botConfig.setBotSkill(botSkillsNumbers[i]);
			}
			if (botSkinsSeparated != null && botSkinsSeparated[i] != null) {
				botConfig.setBotSkin(botSkinsSeparated[i]);
			}
			if (matchType.teamGame) {
				botConfig.setBotTeam(botTeamsNumbers[i]);
			}
			botConfig.setRedirectStdErr(true);
			botConfig.setRedirectStdOut(true);
			botConfigs[i] = botConfig;
		}
		return botConfigs;
	}
	
	private static UT2004NativeBotConfig[] createNativeBotConfig() {
		if (nativeCount <= 0) return null;
		UT2004NativeBotConfig[] nativeConfigs = new UT2004NativeBotConfig[nativeCount];
		for (int i = 0; i < nativeCount; ++i) {
			UT2004NativeBotConfig nativeConfig = new UT2004NativeBotConfig();
			nativeConfig.setBotId(nativeNamesSeparated[i]);
			nativeConfig.setDesiredSkill(nativeSkillsNumbers[i]);
			if (matchType.teamGame) {
				nativeConfig.setTeamNumber(nativeTeamsNumbers[i]);
			}
			nativeConfigs[i] = nativeConfig;
		}
		return nativeConfigs;
	}
	
	private static UT2004HumanConfig[] createHumanConfig() {
		if (humanCount <= 0) return null;
		UT2004HumanConfig[] humanConfigs = new UT2004HumanConfig[humanCount];
		for (int i = 0; i < humanCount; ++i) {
			UT2004HumanConfig humanConfig = new UT2004HumanConfig();
			humanConfig.setHumanId("Human" + i);
			if (matchType.teamGame) {
				humanConfig.setTeamNumber(humanTeamsNumbers[i]);
			}
			humanConfigs[i] = humanConfig;
		}
		return humanConfigs;
	}
	
	private static void executeDeathMatch() {
		UT2004DeathMatchConfig config = new UT2004DeathMatchConfig();

		// GENERIC CONFIGS
		setGenericMatchConfigs(config);
		
		// CUSTOM BOTS
		if (botCount > 0) {
			config.setBot(createBotConfigs());
		}
		
		// NATIVE BOTS		
		if (nativeCount > 0) {			
			config.setNativeBot(createNativeBotConfig());
		}
		
		// HUMANS
		if (humanCount > 0) {
			config.setHuman(createHumanConfig());
		}
		
		// DEATH MATCH SPECIFIC CONFIGS		
	    config.setFragLimit(scoreLimit);
	    config.setTimeLimit(timeoutMinutes);
	     
	    // ------------
	    // START IT UP!
	    // ------------
	    
	    System.out.println("EXECUTING DEATH MATCH!");

	    LogCategory log = new LogCategory(matchName);
	    UT2004DeathMatch match = new UT2004DeathMatch(config, log);
	    
	    match.getLog().setLevel(Level.INFO);
	    match.getLog().addConsoleHandler();
	    
	    match.run();
	}
	
	public static void executeTeamDeathMatch() { 
		UT2004TeamDeathMatchConfig config = new UT2004TeamDeathMatchConfig();

		// GENERIC CONFIGS
		setGenericMatchConfigs(config);
		
		// CUSTOM BOTS
		if (botCount > 0) {
			config.setBot(createBotConfigs());
		}
		
		// NATIVE BOTS		
		if (nativeCount > 0) {			
			config.setNativeBot(createNativeBotConfig());
		}
		
		// HUMANS
		if (humanCount > 0) {
			config.setHuman(createHumanConfig());
		}
		
		// CAPTURE THE FLAG SPECIFIC CONFIGS		
	    config.setScoreLimit(scoreLimit);
	    config.setTimeLimit(timeoutMinutes);
	     
	    // ------------
	    // START IT UP!
	    // ------------
	    
	    System.out.println("EXECUTING TEAM DEATH MATCH!");

	    LogCategory log = new LogCategory(matchName);
	    UT2004TeamDeathMatch match = new UT2004TeamDeathMatch(config, log);
	    
	    match.getLog().setLevel(Level.INFO);
	    match.getLog().addConsoleHandler();
	    
	    match.run();	
	}
	
	public static void executeCaptureTheFlag() { 
		UT2004CaptureTheFlagConfig config = new UT2004CaptureTheFlagConfig();

		// GENERIC CONFIGS
		setGenericMatchConfigs(config);
		
		// CUSTOM BOTS
		if (botCount > 0) {
			config.setBot(createBotConfigs());
		}
		
		// NATIVE BOTS		
		if (nativeCount > 0) {			
			config.setNativeBot(createNativeBotConfig());
		}
		
		// HUMANS
		if (humanCount > 0) {
			config.setHuman(createHumanConfig());
		}
		
		// CAPTURE THE FLAG SPECIFIC CONFIGS		
	    config.setScoreLimit(scoreLimit);
	    config.setTimeLimit(timeoutMinutes);
	     
	    // ------------
	    // START IT UP!
	    // ------------
	    
	    System.out.println("EXECUTING CAPTURE THE FLAG!");

	    LogCategory log = new LogCategory(matchName);
	    UT2004CaptureTheFlag match = new UT2004CaptureTheFlag(config, log);
	    
	    match.getLog().setLevel(Level.INFO);
	    match.getLog().addConsoleHandler();
	    
	    match.run();
	}
	
	public static void executeDoubleDomination() { 
		fail("DOUBLE DOMINATION NOT SUPPORTED YET!");	
	}
	
	
	// ==============
	// TEST ARGUMENTS
	// ==============
	public static String[] getArgs_DM_2v2v1() {
		return new String[] {
				"-y", // MATCH TYPE
				"DM", // DEATH-MATCH
				// GENERIC CONFIG
				"-u",
				"D:\\Games\\UT2004-Devel",
				"-h", // human-like-log
				"-r",
				"./results",
	            "-n",
	            "Test-DM-2v2v1", // MATCH NAME
				"-s",
				"DMServer",				
				// CUSTOM BOTS CONFIG
				"-a",
				"D:\\Workspaces\\Pogamut-Trunk\\Main\\PogamutUT2004Examples\\04-HunterBot\\target\\ut2004-04-hunter-bot-3.6.2-SNAPSHOT.one-jar.jar;D:\\Workspaces\\Pogamut-Trunk\\Main\\PogamutUT2004Examples\\04-HunterBot\\target\\ut2004-04-hunter-bot-3.6.2-SNAPSHOT.one-jar.jar",
				"-b",
				"HunterBot1;HunterBot2",
	            "-l",
	            "1;5",
	            "-k",
	            "HumanFemaleA.NightFemaleA;Aliens.AlienMaleB",
				// NATIVE BOTS CONFIG
	            "-c", // NATIVE BOT COUNT
	            "2",
	            "-d", // NATIVE BOT NAME
	            "Native1;Native2",
	            "-e", // NATIVE BOT SKILL
	            "2;3",
	            // HUMANS CONFIG
	            "-x",
	            "1", // HUMAN COUNT
				// DEATH MATCH SPECIFIC CONFIG
	            "-m",
				"DM-TrainingDay",
				"-f", // FRAG LIMIT
				"5",
				"-t", // TIME LIMIT IN MINS
				"5",
			};
	}
	
	public static String[] getArgs_TDM_2v2v1() {
		return new String[] {
				"-y", // MATCH TYPE
				"TDM", // CAPTURE THE FALG
				// GENERIC CONFIG
				"-u",
				"D:\\Games\\UT2004-Devel",
				"-h", // human-like-log
				"-r",
				"./results",
	            "-n",
	            "Test-CTF-2v2v1", // MATCH NAME
				"-s",
				"CTFServer",				
				// CUSTOM BOTS CONFIG
				"-a",
				"D:\\Workspaces\\Pogamut-Trunk\\Main\\PogamutUT2004Examples\\04-HunterBot\\target\\ut2004-04-hunter-bot-3.7.1-SNAPSHOT.one-jar.jar;D:\\Workspaces\\Pogamut-Trunk\\Main\\PogamutUT2004Examples\\04-HunterBot\\target\\ut2004-04-hunter-bot-3.7.1-SNAPSHOT.one-jar.jar",
				"-b",
				"TDMBot1;TDMBot2",
	            "-l",
	            "3;4",
	            "-k",
	            "HumanFemaleA.NightFemaleA;HumanFemaleA.NightFemaleA",
	            "-i",
	            "0;1",
				// NATIVE BOTS CONFIG
	            "-c", // NATIVE BOT COUNT
	            "2",
	            "-d", // NATIVE BOT NAME
	            "Native1;Native2",
	            "-e", // NATIVE BOT SKILL
	            "2;1",
	            "-g", // NATIVE BOT TEAMS
	            "0;1",
	            // HUMANS CONFIG
	            "-x",
	            "1", // HUMAN COUNT
	            "-z",
	            "1",
				// TEAM DEATH MATCH
	            "-m",
				"DM-Flux2",
				"-f",
				"5", // SCORE LIMIT
				"-t",
				"5", // TIME LIMIT
			};
	}
	
	public static String[] getArgs_CTF_2v2v1() {
		return new String[] {
				"-y", // MATCH TYPE
				"CTF", // CAPTURE THE FALG
				// GENERIC CONFIG
				"-u",
				"D:\\Games\\UT2004-Devel",
				"-h", // human-like-log
				"-r",
				"./results",
	            "-n",
	            "Test-TDM-2v2v1", // MATCH NAME
				"-s",
				"TDMServer",				
				// CUSTOM BOTS CONFIG
				"-a",
				"D:\\Workspaces\\Pogamut-Trunk\\Main\\PogamutUT2004Examples\\09-CTFBot\\target\\ut2004-09-ctf-bot-3.6.2-SNAPSHOT.one-jar.jar;D:\\Workspaces\\Pogamut-Trunk\\Main\\PogamutUT2004Examples\\09-CTFBot\\target\\ut2004-09-ctf-bot-3.6.2-SNAPSHOT.one-jar.jar",
				"-b",
				"CTFBot1;CTFBot2",
	            "-l",
	            "1;2",
	            "-k",
	            "HumanFemaleA.NightFemaleA;HumanFemaleA.NightFemaleA",
	            "-i",
	            "0;1",
				// NATIVE BOTS CONFIG
	            "-c", // NATIVE BOT COUNT
	            "2",
	            "-d", // NATIVE BOT NAME
	            "Native1;Native2",
	            "-e", // NATIVE BOT SKILL
	            "5;6",
	            "-g", // NATIVE BOT TEAMS
	            "0;1",
	            // HUMANS CONFIG
	            "-x",
	            "1", // HUMAN COUNT
	            "-z",
	            "1",
				// CAPTURE THE FLAG SPECIFIC CONFIG
	            "-m",
				"CTF-LostFaith",
				"-f",
				"1", // SCORE LIMIT
				"-t",
				"5", // TIME LIMIT
			};
	}
	
	public static void main(String[] args) throws JSAPException {
		// -----------
		// FOR TESTING
		// -----------
		//args = getArgs_DM_2v2v1();		
		//args = getArgs_TDM_2v2v1();
		//args = getArgs_CTF_2v2v1();		
		
		// --------------
		// IMPLEMENTATION
		// --------------
		
		initJSAP();
	    
	    header();
	    
	    readConfig(args);
	    
	    sanityChecks();
	    
	    switch (matchType) {
	    case DM:
	    	executeDeathMatch();
	    	break;
	    case TDM:
	    	executeTeamDeathMatch();
	    	break;
	    case CTF:
	    	executeCaptureTheFlag();
	    	break;
	    case DD:
	    	executeDoubleDomination();
	    	break;
	    default:
	    	fail("Unsupported match type specified " + matchTypeName + " recognized as " + matchType.shortName + "[" + matchType.name + "].");
	    }
	}
	
}
