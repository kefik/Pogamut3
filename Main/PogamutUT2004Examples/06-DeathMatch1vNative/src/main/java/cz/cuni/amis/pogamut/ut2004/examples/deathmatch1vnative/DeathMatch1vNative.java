package cz.cuni.amis.pogamut.ut2004.examples.deathmatch1vnative;

import java.io.File;

import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.ut2004.tournament.deathmatch.UT2004DeathMatch;
import cz.cuni.amis.pogamut.ut2004.tournament.deathmatch.UT2004DeathMatchConfig;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004BotConfig;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004NativeBotConfig;

public class DeathMatch1vNative {
	
	public DeathMatch1vNative() {
		
	}
	
	//
	// GAME CONFIGURATION
	//
	
	/**
	 * Defines the home of UT2004 that has GameBots2004 installed.
	 * @return
	 */
	public String getUT2004Home() {
		// Specify path to the UT2004 that contains GameBots2004
		// i.e., path_to_ut2004\System\GameBots2004.u exists
		return "D:\\Games\\UT2004-Devel";
	}
	
	/**
	 * Defines map that will be used for death match.
	 * @return
	 */
	public String getMapName() {
		// Specify that where the death match will be carried out
		return "DM-1on1-Albatross";
	}
	
	/**
	 * Defines the frag limit (goal score) for the death match.
	 * @return
	 */
	public int getFragLimit() {
		return 20;
	}
	
	/**
	 * Defines time limit for the game in minutes. The game will end after the specified number of minutes
	 * if frag limit is not reached by neither of bots.
	 * @return
	 */
	public int getTimeLimitInMinutes() {
		return 20;
	}
	
	//
	// POGAMUT BOT CONFIGURATION
	//
	
	/**
	 * Defines name of the Pogamut bot, this identifier will be used in generated statistics.
	 * @return
	 */
	public String getPogamutBotName() {
		return "PogamutBot";
	}
	
	/**
	 * Defines path to the jar that contains the Pogamut bot. The jar must be executable, i.e., it contains
	 * all libraries embedded (fortunately example projects are auto-generating such jars ;-).
	 * 
	 * @return
	 */
	public String getPogamutBotJarPath() {
		return "D:\\Programming\\Workspaces\\Pogamut-Trunk\\Addons\\UT2004Tournament\\bots\\HunterBot\\HunterBot.jar";
	}
	
	//
	// NATIVE BOT CONFIGURATION
	//
	
	/**
	 * Defines name of the native bot, this identifier will be used in generated statistics.
	 * @return
	 */
	public String getNativeBotName() {
		return "NativeBot";
	}
	
	/**
	 * Defines skill level of the native bot (worst: 1-7 :best).
	 * @return
	 */
	public int getNativeBotSkillLevel() {
		return 5;
	}
	
	//
	// OUTPUT RESULT FOLDER
	//
	
	/**
	 * Defines folder where we will output results.
	 */
	public String getOutputDir() {
		return "C:\\UT2004-Match-Results";
	}
	
	//
	// MATCH CONFIGURATION
	//
	
	public UT2004DeathMatch createMatch() {
		LogCategory log = new LogCategory("DeathMatch1v1");
		log.addConsoleHandler();
		
		UT2004DeathMatchConfig config = new UT2004DeathMatchConfig();
		
		// GAME CONFIGURATION
		
		config.getUccConf().setUnrealHome(getUT2004Home());
		config.getUccConf().setMapName(getMapName());
		config.setFragLimit(getFragLimit());
		config.setTimeLimit(getTimeLimitInMinutes());
		
		// POGAMUT BOT CONFIGURATION
		
		UT2004BotConfig botConfig;
		
		botConfig = new UT2004BotConfig();
		botConfig.setBotId(getPogamutBotName());
		botConfig.setPathToBotJar(getPogamutBotJarPath());
		botConfig.setBotTeam(255);
		botConfig.setRedirectStdErr(true);
		botConfig.setRedirectStdOut(true);
		
		config.addBot(botConfig);
				
		// NATIVE BOT CONFIGURATION
		
		UT2004NativeBotConfig nativeBotConfig;
		
		nativeBotConfig = new UT2004NativeBotConfig();
		nativeBotConfig.setBotId(getNativeBotName());
		nativeBotConfig.setDesiredSkill(getNativeBotSkillLevel());
		nativeBotConfig.setTeamNumber(255);
		
		config.addNativeBot(nativeBotConfig);
		
		// OUTPUT RESULT
		
		config.setOutputDirectory(new File(getOutputDir()));
		
		// CREATING MATCH
		
		UT2004DeathMatch match = new UT2004DeathMatch(config, log);
				
		return match;
	}
	
	//
	// METHOD THAT EXECUTES THE MATCH
	//
	
	public void run() {
		UT2004DeathMatch match = createMatch();
		match.run();
	}
	
	// ===========
	// MAIN METHOD
	// ===========
	
	public static void main(String[] args) {
		try {
			// CONSTRUCT THE MATCH
			DeathMatch1vNative match = new DeathMatch1vNative();
		
			// EXECUTE THE MATCH (blocking method)
			match.run();
		} finally {
			// CLEAN UP
			Pogamut.getPlatform().close();
		}
	}

}
