package cz.cuni.amis.pogamut.ut2004.tag.tournament;

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
import cz.cuni.amis.pogamut.ut2004.tournament.UT2004Ini;
import cz.cuni.amis.pogamut.ut2004.tournament.deathmatch.UT2004DeathMatch1v1;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004BotConfig;

public class UT2004TagConsole {
	
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
	
	private static final char ARG_TAG_LIMIT_SHORT = 'l';
	
	private static final String ARG_TAG_LIMIT_LONG = "tag-limit";
	
	private static final char ARG_TIMEOUT_MINUTES_SHORT = 't';
	
	private static final String ARG_TIMEOUT_MINUTES_LONG = "timeout-minutes";
	
	private static final char ARG_HUMAN_LIKE_LOG_SHORT = 'h';
	
	private static final String ARG_HUMAN_LIKE_LOG_LONG = "human-like-log";
	
	private static final char ARG_UT2004_PORT_SHORT = 'p';
	
	private static final String ARG_UT2004_PORT_LONG = "ut2004-port";

	private static JSAP jsap;

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
	
	private static int tagLimit;
	
	private static int timeoutMinutes;

	private static JSAPResult config;

	private static File ut2004HomeDirFile;

	private static File[] botJarFiles;
	
	private static File mapsDirFile;

	private static File mapFile;

	private static File ut2004SystemDirFile;

	private static File ut2004IniFile;

	private static boolean humanLikeLog;

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
        System.out.println("Usage: java -jar ut2004-tag....jar ");
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
		System.out.println("Pogamut UT2004 Tag Match Executor");
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
	    	.setLongFlag(ARG_MAP_NAME_LONG);    
	    opt6.setHelp("Map where the game should be played (e.g. DM-1on1-TagMap).");
	
	    jsap.registerParameter(opt6);
        
	    FlaggedOption opt7 = new FlaggedOption(ARG_MATCH_NAME_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(false) 
	    	.setShortFlag(ARG_MATCH_NAME_SHORT)
	    	.setLongFlag(ARG_MATCH_NAME_LONG)
	    	.setDefault("TagGame");    
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
			.setDefault("TagGame");
		opt9.setHelp("Server name that should be advertised via LAN.");
		
		jsap.registerParameter(opt9);
		
		FlaggedOption opt10 = new FlaggedOption(ARG_TAG_LIMIT_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_TAG_LIMIT_SHORT)
			.setLongFlag(ARG_TAG_LIMIT_LONG)
			.setDefault("10");
		opt10.setHelp("Tag limit for the match. How many times can one bot get tagged at max.");
		
		jsap.registerParameter(opt10);
		
		FlaggedOption opt11 = new FlaggedOption(ARG_TIMEOUT_MINUTES_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_TIMEOUT_MINUTES_SHORT)
			.setLongFlag(ARG_TIMEOUT_MINUTES_LONG)
			.setDefault("10");
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
	    tagLimit = config.getInt(ARG_TAG_LIMIT_LONG);
	    timeoutMinutes = config.getInt(ARG_TIMEOUT_MINUTES_LONG);
	    humanLikeLog = config.getBoolean(ARG_HUMAN_LIKE_LOG_LONG);
	    ut2004Port = config.getInt(ARG_UT2004_PORT_LONG);
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
	    
	    if (tagLimit < 1) {
	    	fail("Invalid frag limit '" + tagLimit +"' specified, must be >= 1.");
	    }
	    System.out.println("-- Frag limit set as '" + tagLimit + "'");
	    
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
		UT2004TagConfig config = new UT2004TagConfig();
		
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
		config.setTagLimit(tagLimit);
		config.setTagTimeLimit(timeoutMinutes);
		
		config.getUccConf().setGameType("BotDeathMatch");
		config.getUccConf().setMapName(map);
		config.getUccConf().setUnrealHome(ut2004HomeDir);
		
		LogCategory log = new LogCategory(matchName);
		UT2004Tag match = new UT2004Tag(config, log);
		
	    match.getLog().setLevel(Level.ALL);
	    match.getLog().addConsoleHandler();
	    
	    System.out.println("EXECUTING MATCH!");
	    
	    match.run();
	}
	
	public static void main(String[] args) throws JSAPException {
//      FOR TESTING		
//		args = new String[] {
//			"-u",
//			"D:\\Games\\UT2004-Devel",
//			"-a",
//			"d:/Workspaces/Pogamut-Lectures-Trunk/Lectures/AB2013-Lectures/Lecture3-TagBot/Assignment-Students/Skupina-Jakub/Jiri_Dutkevic/target/tag-bot-3.5.0.one-jar.jar;"
//		+	"d:/Workspaces/Pogamut-Lectures-Trunk/Lectures/AB2013-Lectures/Lecture3-TagBot/Assignment-Students/Skupina-Jakub/Pavel_Herrmann/target/tag-bot-3.5.0.one-jar.jar;"
//		+	"d:/Workspaces/Pogamut-Lectures-Trunk/Lectures/AB2013-Lectures/Lecture3-TagBot/Assignment-Students/Skupina-Jakub/Vojtech_Kopal/target/tag-bot-3.5.0.one-jar.jar"
//			,
//			"-b",
//			"Jiri_Dutkevic;" 
//		+	"Pavel_Herrmann;"
//		+	"Vojtech_Kopal"
//			,
//			"-m",
//			"DM-TagMap",
//			"-n",
//			"TagMatch",
//			"-r",
//			"./results",			
//			"-s",
//			"TagServer",
//			"-l",
//			"2",
//			"-t",
//			"2",
////			"-h", // human-like log			
//		};
		
		initJSAP();
	    
	    header();
	    
	    readConfig(args);
	    
	    sanityChecks();
	    
	    setUT2004Ini();
	    
	    executeMatch();
	}
	
}
