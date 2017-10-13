package cz.cuni.amis.pogamut.ut2004.examples.deathmatch1v1;

import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.ut2004.tournament.deathmatch.UT2004DeathMatch1v1;

public class DeathMatch1v1 {
	
	public DeathMatch1v1() {
		
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
		return "C:\\Games\\UT2004";
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
	// BOT 1 CONFIGURATION
	//
	
	/**
	 * Defines name of the first bot, this identifier will be used in generated statistics.
	 * @return
	 */
	public String getBot1Name() {
		return "Bot1";
	}
	
	/**
	 * Defines path to the jar that contains the first bot. The jar must be executable, i.e., it contains
	 * all libraries embedded (fortunately example projects are auto-generating such jars ;-).
	 * 
	 * @return
	 */
	public String getBot1JarPath() {
		return "C:\\Workspace\\Bot1\\target\\bot1-3.2.0-SNAPSHOT-one-jar.jar";
	}
	
	//
	// BOT 2 CONFIGURATION
	//
	
	/**
	 * Defines name of the second bot, this identifier will be used in generated statistics.
	 * @return
	 */
	public String getBot2Name() {
		return "Bot2";
	}
	
	/**
	 * Defines path to the jar that contains the second bot. The jar must be executable, i.e., it contains
	 * all libraries embedded (fortunately example projects are auto-generating such jars ;-).
	 * 
	 * @return
	 */
	public String getBot2JarPath() {
		return "C:\\Workspace\\Bot2\\target\\bot2-3.2.0-SNAPSHOT-one-jar.jar";
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
	
	public UT2004DeathMatch1v1 createMatch() {
		LogCategory log = new LogCategory("DeathMatch1v1");
		log.addConsoleHandler();
		
		UT2004DeathMatch1v1 match = new UT2004DeathMatch1v1();
		match.setLog(log);
		
		// GAME CONFIGURATION
		
		match.setUnrealHome(getUT2004Home());
		match.setMapName(getMapName());
		match.setFragLimit(getFragLimit());
		match.setTimeLimitInMinutes(getTimeLimitInMinutes());
		
		// BOT 1 CONFIGURATION
		
		match.setBot1Name(getBot1Name());
		match.setBot1JarPath(getBot1JarPath());
		
		// BOT 2 CONFIGURATION
		
		match.setBot2Name(getBot2Name());
		match.setBot2JarPath(getBot2JarPath());
		
		// OUTPUT RESULT
		
		match.setOutputDir(getOutputDir());
		
		return match;
	}
	
	//
	// METHOD THAT EXECUTES THE MATCH
	//
	
	public void run() {
		UT2004DeathMatch1v1 match = createMatch();
		match.run();
	}
	
	// ===========
	// MAIN METHOD
	// ===========
	
	public static void main(String[] args) {
		try {
			// CONSTRUCT THE MATCH
			DeathMatch1v1 match = new DeathMatch1v1();
		
			// EXECUTE THE MATCH (blocking method)
			match.run();
		} finally {
			// CLEAN UP
			Pogamut.getPlatform().close();
		}
	}

}
