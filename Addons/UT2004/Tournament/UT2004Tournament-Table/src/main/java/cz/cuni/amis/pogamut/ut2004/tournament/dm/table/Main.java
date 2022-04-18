package cz.cuni.amis.pogamut.ut2004.tournament.dm.table;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
import cz.cuni.amis.pogamut.ut2004.tournament.UT2004Ini;
import cz.cuni.amis.pogamut.ut2004.tournament.deathmatch.UT2004DeathMatch;
import cz.cuni.amis.pogamut.ut2004.tournament.deathmatch.UT2004DeathMatchConfig;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004BotConfig;
import cz.cuni.amis.pogamut.ut2004.tournament.utils.CSV;

/**
 * Locate Bot Jar Files in some folder automatically.
 * 
 * @author Jimmy
 *
 */
public class Main {
	
	private static final char ARG_UT2004_HOME_DIR_SHORT = 'u';
	
	private static final String ARG_UT2004_HOME_DIR_LONG = "ut2004-home-dir";
	
	private static final char ARG_BOT_JARs_SHORT = 'a';
	
	private static final String ARG_BOT_JARs_LONG = "bot-jars-dir";

//  BOT NAMES ARE DETERMINED FROM THE JAR FILE NAME
//	private static final char ARG_BOT_NAMEs_SHORT = 'b';
//	
//	private static final String ARG_BOT_NAMEs_LONG = "bot-names";
	
	private static final char ARG_MAP_NAME_SHORT = 'm';
	
	private static final String ARG_MAP_NAME_LONG = "map-name";
		
	private static final char ARG_RESULT_DIR_SHORT = 'r';
	
	private static final String ARG_RESULT_DIR_LONG = "result-directory";
	
	private static final char ARG_SERVER_NAME_SHORT = 's';
	
	private static final String ARG_SERVER_NAME_LONG = "server-name";
	
	private static final char ARG_FRAG_LIMIT_SHORT = 'f';
	
	private static final String ARG_FRAG_LIMIT_LONG = "frag-limit";
	
	private static final char ARG_TIMEOUT_MINUTES_SHORT = 't';
	
	private static final String ARG_TIMEOUT_MINUTES_LONG = "timeout-minutes";
	
	private static final char ARG_HUMAN_LIKE_LOG_SHORT = 'h';
	
	private static final String ARG_HUMAN_LIKE_LOG_LONG = "human-like-log";
	
	private static final char ARG_UT2004_PORT_SHORT = 'p';
	
	private static final String ARG_UT2004_PORT_LONG = "ut2004-port";
	
	private static final char ARG_CONCURRENT_THREADS_SHORT = 'c';
	
	private static final String ARG_CONCURRENT_THREADS_LONG = "threads";
	
	private static final char ARG_GENERATE_BATCH_FILES_SHORT = 'g';
	
	private static final String ARG_GENERATE_BATCH_FILES_LONG = "generate-batch-files";
	
	private static final char ARG_DEBUG_SHORT = 'd';
	
	private static final String ARG_DEBUG_LONG = "debug";
	
	private static final char ARG_CONTINUE_SHORT = 'o';
	
	private static final String ARG_CONTINUE_LONG = "continue";

	private static final long MATCH_START_INTERLEAVE_MILLIS = 2 * 60 * 1000;

	private static JSAP jsap;

	private static boolean headerOutput = false;

	private static String ut2004HomeDir;

	private static String botJarDir;
	
	private static List<String> botNames;
	
	private static List<File> botJarFiles;
	
	private static String map;

	private static String serverName;

	private static String resultDir;

	private static int fragLimit;
	
	private static int timeoutMinutes;

	private static JSAPResult config;

	private static File ut2004HomeDirFile;

	private static File mapsDirFile;

	private static File mapFile;

	private static File ut2004SystemDirFile;

	private static File ut2004IniFile;

	private static boolean humanLikeLog;

	private static int ut2004Port;	
	
	private static int threadCount;
	
	private static boolean generateBatchFiles;
	
	private static boolean debug;
	
	private static boolean shouldContinue;
	
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
        System.out.println("Usage: java -jar ut2004-tournament-dm-table.jar ");
        System.out.println("                " + jsap.getUsage());
        System.out.println();
        System.out.println(jsap.getHelp());
        System.out.println();
        throw new RuntimeException("FAILURE: " + errorMessage);
	}

	private static void header() {
		if (headerOutput) return;
		System.out.println();
		System.out.println("========================================");
		System.out.println("Pogamut UT2004 DeathMatch Table Executor");
		System.out.println("========================================");
		System.out.println();
		headerOutput = true;
	}
	
	private static void info(String msg) {
		System.out.println("[INFO]    " + msg);
	}
	
	private static void warning(String msg) {
		System.out.println("[WARNING] " + msg);
	}
	
	private static void severe(String msg) {
		System.out.println("[SEVERE]  " + msg);
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
	    opt2.setHelp("PATH/TO/DIR where to look for *one-jar*.jar files that are treated as bot-jar-files that contains a bot for tournament.");
	
	    jsap.registerParameter(opt2);
	    
//	    FlaggedOption opt3 = new FlaggedOption(ARG_BOT_NAMEs_LONG)
//	    	.setStringParser(JSAP.STRING_PARSER)
//	    	.setRequired(true) 
//	    	.setShortFlag(ARG_BOT_NAMEs_SHORT)
//	    	.setLongFlag(ARG_BOT_NAMEs_LONG);    
//	    opt3.setHelp("Semicolon separated name1;name2;name3 (ids) that should be given to bots.");
//	
//	    jsap.registerParameter(opt3);
    
	    FlaggedOption opt6 = new FlaggedOption(ARG_MAP_NAME_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(true) 
	    	.setShortFlag(ARG_MAP_NAME_SHORT)
	    	.setLongFlag(ARG_MAP_NAME_LONG);    
	    opt6.setHelp("Map where the game should be played (e.g. DM-1on1-Albatross).");
	
	    jsap.registerParameter(opt6);
        
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
		
		FlaggedOption opt10 = new FlaggedOption(ARG_FRAG_LIMIT_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_FRAG_LIMIT_SHORT)
			.setLongFlag(ARG_FRAG_LIMIT_LONG)
			.setDefault("20");
		opt10.setHelp("Frag limit for the match.");
		
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
		
		FlaggedOption opt14 = new FlaggedOption(ARG_CONCURRENT_THREADS_LONG)
			.setStringParser(JSAP.INTEGER_PARSER)
			.setRequired(false) 
			.setShortFlag(ARG_CONCURRENT_THREADS_SHORT)
			.setLongFlag(ARG_CONCURRENT_THREADS_LONG)
			.setDefault("1");
		opt14.setHelp("How many matches should be executed in parallel.");
		
		jsap.registerParameter(opt14);
		
		Switch opt15 = new Switch(ARG_GENERATE_BATCH_FILES_LONG)
			.setShortFlag(ARG_GENERATE_BATCH_FILES_SHORT)
			.setLongFlag(ARG_GENERATE_BATCH_FILES_LONG);
		opt15.setHelp("Generate batch file for every match executed (requires ut2004-tournament.jar and java on PATH to execute then).");
		
		jsap.registerParameter(opt15);
		
		Switch opt16 = new Switch(ARG_DEBUG_LONG)
			.setShortFlag(ARG_DEBUG_SHORT)
			.setLongFlag(ARG_DEBUG_LONG);
		opt16.setHelp("DEBUG - Whether to output stdout/err of respective executed bots.");
	
		jsap.registerParameter(opt16);
		
		Switch opt17 = new Switch(ARG_CONTINUE_LONG)
			.setShortFlag(ARG_CONTINUE_SHORT)
			.setLongFlag(ARG_CONTINUE_LONG);
		opt17.setHelp("Whether to skip matches for which the result folder already exists.");
	
		jsap.registerParameter(opt17);
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
		botJarDir = config.getString(ARG_BOT_JARs_LONG);
	    map = config.getString(ARG_MAP_NAME_LONG);
	    serverName = config.getString(ARG_SERVER_NAME_LONG);
	    resultDir = config.getString(ARG_RESULT_DIR_LONG);
	    fragLimit = config.getInt(ARG_FRAG_LIMIT_LONG);
	    timeoutMinutes = config.getInt(ARG_TIMEOUT_MINUTES_LONG);
	    humanLikeLog = config.getBoolean(ARG_HUMAN_LIKE_LOG_LONG);
	    ut2004Port = config.getInt(ARG_UT2004_PORT_LONG);
	    threadCount = config.getInt(ARG_CONCURRENT_THREADS_LONG);
	    generateBatchFiles = config.getBoolean(ARG_GENERATE_BATCH_FILES_LONG);
	    debug = config.getBoolean(ARG_DEBUG_LONG);
	    shouldContinue = config.getBoolean(ARG_CONTINUE_LONG);
	}
	
	private static void findBotJarFiles() {
		botJarFiles = new ArrayList<File>();
		botNames = new ArrayList<String>();
		
		File baseDir = new File(botJarDir);
		
		if (!baseDir.exists()) {
			fail("Directory that should contain bot jars '" + botJarDir + "' that resolved to '" + baseDir.getAbsolutePath() + "' does not exist.");
		}
		if (!baseDir.exists() || !baseDir.isDirectory()) {
			fail("Directory that should contain bot jars '" + botJarDir + "' that resolved to '" + baseDir.getAbsolutePath() + "' is not directory.");
		}
		
		locateJarFiles(baseDir);
	}
	
	private static void locateJarFiles(File dir) {
		if (!dir.isDirectory()) return;
		if (!dir.exists()) return;
		
		for (File file : dir.listFiles()) {
			if (!file.exists()) continue;
			if (file.isDirectory()) {
				locateJarFiles(file);
				continue;
			} 
			if (!file.isFile()) {
				continue;
			}
			
			String name = file.getName();
			if (name.endsWith(".jar") && name.contains("one-jar")) {
				// JAR FILE!
				if (name.startsWith("rewrite")) {
					// BAD ONE
					continue;
				}				
				botJarFiles.add(file);
				String botName = name.substring(0, name.indexOf("one-jar"));
				while (botName.endsWith(".")) botName = botName.substring(0, botName.length()-1);
				if (botName.toLowerCase().endsWith("-snapshot")) botName = botName.substring(0, botName.toLowerCase().indexOf("-snapshot"));
				if (botName.startsWith("DMBot-")) botName = botName.substring(6);
				if (botName.startsWith("2") && botName.charAt(4) == '-') botName = botName.substring(5);
				while (botName.contains("-")) botName = botName.substring(0, botName.indexOf("-"));
				botNames.add(botName); 
			}
		}
	}
	
	private static void sanityChecks() {
		System.out.println("Sanity checks...");
		
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
	    
	    System.out.println("-- LOCATED BOT NAME:JAR FILES (Total: " + botJarFiles.size() + ")");
	    for (int i = 0; i < botJarFiles.size(); ++i) {
	    	System.out.println("---- [" + (i+1) + "] " + botNames.get(i) + " -> " + botJarFiles.get(i).getAbsolutePath());
	    }
	    
	    if (botJarFiles.size() < 2) {
	    	fail("We need at least 2 bots to perform the matches.");
	    }
	    
	    for (int i = 0; i < botJarFiles.size(); ++i) {
	    	if (!botJarFiles.get(i).exists() || !botJarFiles.get(i).isFile()) {
		    	fail("Bot" + (i+1) + " jar file was not found at '"+ botJarFiles.get(i).getAbsolutePath() + "'.");
		    }
	    }
	   	
	    for (int i = 0; i < botNames.size(); ++i) {
	    	if (botNames.get(i) == null || botNames.get(i).isEmpty()) {
	    		fail("Bot " + (i+1) + " invalid name '" + botNames.get(i) +"' specified.");
	    	}
	    }
	    
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
	    	    
	    if (serverName == null || serverName.isEmpty()) {
	    	fail("Invalid server name '" + serverName + "' specified.");
	    }
	    System.out.println("-- Server name set as '" + serverName + "'");
	    
	    if (fragLimit < 1) {
	    	fail("Invalid frag limit '" + fragLimit +"' specified, must be >= 1.");
	    }
	    System.out.println("-- Frag limit set as '" + fragLimit + "'");
	    
	    if (timeoutMinutes < 1) {
	    	fail("Invalid time limit '" + timeoutMinutes +"' specified, must be >= 1.");
	    }
	    System.out.println("-- Timeout set as '" + timeoutMinutes + "' minutes.");
	    
	    if (ut2004Port < 1 || ut2004Port > 32000) {
	    	fail("Invalid UT2004 port specified '" + ut2004Port + "', must be 1 <= port <= 32000.");
	    }
	    System.out.println("-- UT2004 port set as '" + ut2004Port + "'");
	    
	    if (0 > threadCount || threadCount > 20) {
	    	fail("Invalid parallel thread count " + threadCount + ", must be 20 >= N > 0.");
	    }	    
	    
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
		Pattern patternSectionURL = Pattern.compile("^\\s*\\[\\s*" + UT2004Ini .Section_URL + "\\s*\\]\\s*$");
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
		
		System.out.println("Rewriting '" + ut2004IniFile.getAbsolutePath() + "' ...");
		try {
			FileUtils.writeLines(ut2004IniFile, lines);
		} catch (IOException e) {
			throw new RuntimeException("Failed to write UT2004.ini", e);
		}		
		
		System.out.println("UT2004 Port and ServerName set.");
	}
	
	private static void generateBatchFile(String matchName, File bot1Jar, String bot1Id, File bot2Jar, String bot2Id) {
		String fileName = "match-" + bot1Id + "-vs-" + bot2Id + ".bat";
		File file = new File(fileName);
		if (file.exists()) {
			if (file.isFile()) {
				warning("Batch file already exists, skipping: " + file.getAbsolutePath());				
 			} else {
 				severe("Batch file already exists, but it is not file, skipping: " + file.getAbsolutePath());
 			}
			return;			
		}
		try {
			FileWriter writer = new FileWriter(file);
			PrintWriter print = new PrintWriter(writer);
			
			print.println("java -jar ut2004-tournament.jar ^");
			print.println("  -u " + ut2004HomeDir + " ^");
			print.println("  -a " + bot1Jar.getPath() + ";" + bot2Jar.getPath() + " ^");
			print.println("  -b " + bot1Id + ";" + bot2Id + " ^");
			print.println("  -m " + map + " ^");
			print.println("  -r " + resultDir + " ^");
			print.println("  -n " + matchName + " ^");
			print.println("  -s DMServer-" + matchName + " ^");
			print.println("  -f " + fragLimit + " ^");
			print.println("  -t " + timeoutMinutes);
			
			try {
				print.close();
			} catch (Exception e) {				
			}
			try { 
				writer.close();
			} catch (Exception e) {				
			}
		} catch (Exception e) {
			
		}
	}
	
	private static String getMatchName(String bot1Id, String bot2Id) {
		return "Match-" + bot1Id + "-vs-" + bot2Id;
	}
	
	private static void generateBatchFiles() {
		for (int i = 0; i < botJarFiles.size(); ++i) {
			String bot1Id = botNames.get(i);
			File bot1Jar = botJarFiles.get(i);
			for (int j = i+1; j < botJarFiles.size(); ++j) {
				String bot2Id = botNames.get(j);
				File bot2Jar = botJarFiles.get(j);
				String matchName = getMatchName(bot1Id, bot2Id);
				generateBatchFile(matchName, bot1Jar, bot1Id, bot2Jar, bot2Id);
			}
		}
	}
	
	private static UT2004DeathMatch executeMatch(Match match) {		
		// DELETE THE RESULT FOLDER
		File resultFolder = new File(new File(resultDir), match.matchName);
		try {
			FileUtils.deleteDirectory(resultFolder);
		} catch (Exception e) {			
		}
		
		// CONFIGURE AND FIRE THE MATCH
		UT2004DeathMatchConfig config = new UT2004DeathMatchConfig();
		
		UT2004BotConfig bot1Config = new UT2004BotConfig();
		bot1Config.setBotId(match.bot1Id);
		bot1Config.setPathToBotJar(match.bot1Jar.getAbsolutePath());
		bot1Config.setRedirectStdErr(debug);
		bot1Config.setRedirectStdOut(debug);
		
		UT2004BotConfig bot2Config = new UT2004BotConfig();
		bot2Config.setBotId(match.bot2Id);
		bot2Config.setPathToBotJar(match.bot2Jar.getAbsolutePath());
		bot2Config.setRedirectStdErr(debug);
		bot2Config.setRedirectStdOut(debug);
		
		config.setBot(bot1Config, bot2Config);
		config.setOutputDirectory(new File(resultDir));
	    config.setMatchId(match.matchName);
	    config.setFragLimit(fragLimit);
	    config.setTimeLimit(timeoutMinutes);
	    config.setHumanLikeLogEnabled(humanLikeLog);
	    
	    config.getUccConf().setStartOnUnusedPort(true);
	    config.getUccConf().setUnrealHome(ut2004HomeDir);
	    config.getUccConf().setGameType("BotDeathMatch");
	    config.getUccConf().setMapName(map);
	     
	    System.out.println("EXECUTING MATCH!");

	    LogCategory log = new LogCategory(match.matchName);
	    UT2004DeathMatch ut2004Match = new UT2004DeathMatch(config, log);
	    
	    ut2004Match.getLog().setLevel(Level.INFO);
	    ut2004Match.getLog().addConsoleHandler();
	    
	    ut2004Match.run();
	    
	    return ut2004Match;
	}
	
	private static class Match {
		
		String matchName;
		File bot1Jar;
		String bot1Id;
		File bot2Jar;
		String bot2Id;
		
		public Match(String matchName, File bot1Jar, String bot1Id, File bot2Jar, String bot2Id) {
			super();
			this.matchName = matchName;
			this.bot1Jar = bot1Jar;
			this.bot1Id = bot1Id;
			this.bot2Jar = bot2Jar;
			this.bot2Id = bot2Id;
		}
		
		@Override
		public String toString() {
			return "Match[" + matchName + "]";
		}
		
	}
	
	private static class ExecuteOne extends Thread {

		private Match match;
		
		private UT2004DeathMatch result;
		
		public boolean done = false;
		
		public boolean success = true;

		public ExecuteOne(Match match) {
			super(match.matchName);
			this.match = match;
		}
		
		@Override
		public void run() {
			try {
				info("STARTING: " + match.matchName);				
				result = executeMatch(match);
			} catch (Exception e) {				
				success = false;
			} finally {
				done = true;
				if (success) {
					info("ENDED-SUCCESS: " + match.matchName);
				} else {
					info("ENDED-FAILURE: " + match.matchName);
				}
			}
		}
		
	}
	
	private static void executeInParallel(Match[] matches, int threadCount) throws InterruptedException {
		int currentMatch = 0;
		int liveThreads = 0;
		
		int successes = 0;
		int skipped = 0;
		
		ExecuteOne[] threads = new ExecuteOne[threadCount];
		
		while (currentMatch < matches.length || liveThreads > 0) {
			
			for (int i = 0; i < threads.length; ++i) {
				if (threads[i] != null) {
					if (!threads[i].done) {
						// THREAD IS ALIVE AND MATCH IS RUNNING
						continue;
					}
					// THREAD HAS FINISHED, MATCH HAS ENDED
					--liveThreads;
					// CHECK SUCCESS
					if (threads[i].success) {
						++successes;
						
					}
					// DELETE THREAD
					threads[i] = null;					
				}
				
				assert(threads[i] == null);

				if (shouldContinue) {
					while (currentMatch < matches.length) {
						Match match = matches[currentMatch];
						File resultFolder = new File(new File(resultDir), match.matchName);
						if (resultFolder.exists() && resultFolder.isDirectory()) {
							warning("Result folder for the match " + match.matchName + " already exists, checking the result...");
							File resultFile = new File(resultFolder, "match-" + match.matchName + "-result.csv");
							File botScoresFile = new File(resultFolder, "match-" + match.matchName + "-bot-scores.csv");
							if (!resultFile.exists() || !resultFile.isFile()) {
								warning("  +-- result file does not exist at: " + resultFile.getAbsolutePath());
								break;
							}
							if (!botScoresFile.exists() || !botScoresFile.isFile()) {
								warning("  +-- bot-scores file does not exist at: " + botScoresFile.getAbsolutePath());
								break;
							}
							if (!isMatchSuccess(resultFile)) {
								break;
							}
							++skipped;
						} else {
							break;
						}
						++currentMatch;
					}
				}
								
				if (currentMatch < matches.length) {
					threads[i] = new ExecuteOne(matches[currentMatch]);
					++currentMatch;
					++liveThreads;
					threads[i].start();
					Thread.sleep(MATCH_START_INTERLEAVE_MILLIS); // INTERLEAVE RESPECTIVE MATCH START
				}
			}
			
			Thread.sleep(1000); // CHECK FOR MATCH END EVERY 1 sec
		}
			
		if (successes == matches.length - skipped) {
			info("ALL SUCCEEDED!");	
			if (shouldContinue) {
				info("Succeeded: " + successes + ", Skipped: " + skipped);
			}						
		} else {
			severe("Some matches failed, only " + successes + "/" + (matches.length - skipped) + " has succeed.");
			if (shouldContinue) {
				severe("Skipped: " + skipped);
			}
		}
	}
	
	private static boolean isMatchSuccess(File resultFile) {		
		CSV csv = null;
		try {
			csv = new CSV(resultFile, ";", true);
		} catch (Exception e) {
			warning("  +-- Failed to open: " + resultFile.getAbsolutePath());
			e.printStackTrace();
			return false;
		}
		if (csv.rows.size() != 1) {
			warning("  +-- Result file contains invalid number of data rows (" + csv.rows.size() + "), ignoring.");
			return false;
		}
		
		if (!csv.keys.contains("Winner")) {
			warning("  +-- Result file does not contain column 'Winner'. Ignoring.");
			return false;
		}
		String winner = csv.rows.get(0).getString("Winner");
		if (winner.toLowerCase().contains("failure")) {
			warning("  +-- Result file is indicating that the match has FAILED!");
			return false;
		}
		info("  +-- Match success");
		return true;
	}

	private static void executeMatches() throws InterruptedException {
		
		int matchCount = botJarFiles.size() * (botJarFiles.size()-1) / 2;		
		Match[] matches = new Match[matchCount];
		
		int k = 0;
		for (int i = 0; i < botJarFiles.size()-1; ++i) {
			String bot1Id = botNames.get(i);
			File bot1Jar = botJarFiles.get(i);
			
			for (int j = i+1; j < botJarFiles.size(); ++j) {
				String bot2Id = botNames.get(j);
				File bot2Jar = botJarFiles.get(j);
			
				String matchName = getMatchName(bot1Id, bot2Id);
				
				matches[k] = new Match(matchName, bot1Jar, bot1Id, bot2Jar, bot2Id);
						
			    ++k;
			}
		}
		
		System.out.println("Going to perform " + matchCount + " matches using " + threadCount + " threads.");

		executeInParallel(matches, threadCount);
	}

	public static void main(String[] args) throws JSAPException {
		if (args == null || args.length == 0) {
			// FOR TESTING 
			
			String ut2004Dir = "e:\\Games\\Devel\\UT2004\\UT2004-Devel\\";
			
			String botsDir = "d:\\MFF\\NAIL068-UmeleBytosti\\Lectures\\AB2021-Labs\\Lab05-SpatialAwareness\\Students\\";
			
			String resultsDir = "d:\\MFF\\NAIL068-UmeleBytosti\\Lectures\\AB2021-Labs\\Lab05-SpatialAwareness\\Students\\_Results\\";
			
			args = new String[] {
				"-u",
				ut2004Dir,
				"-a",
				botsDir,
				"-r",
				resultsDir,
				"-s",
				"DMServer",
				"-m",
				"DM-1on1-Roughinery-FPS",
				"-f",
				"40",
				"-t",
				"20",
				//"-h", // human-like log			
			    "-c",
			    "2",  // concurrent thread count
			    //"-g", // generate batch-files
		        "-d", // debug
	            "-o"  // continue		
			};
		}
		
		initJSAP();
	    
	    header();
	    
	    readConfig(args);
	    
	    findBotJarFiles();
	    
	    sanityChecks();
	    
	    if (generateBatchFiles) {
	    	generateBatchFiles();
	    }
	    
	    setUT2004Ini();
	    
	    try {
	    	executeMatches();
	    } catch (Exception e) {
	    	fail("ERROR", e);
	    }
	    
	    MainExcel.main(new String[] {
	    	"-r",
	    	resultDir,
	    	"-o",
	    	resultDir
	    });
	    
	}
	
}
