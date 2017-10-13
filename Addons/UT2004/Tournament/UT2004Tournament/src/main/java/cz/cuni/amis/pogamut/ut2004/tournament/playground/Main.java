package cz.cuni.amis.pogamut.ut2004.tournament.playground;

import java.io.File;
import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.ut2004.tournament.UT2004Ini;
import cz.cuni.amis.pogamut.ut2004.tournament.deathmatch.UT2004DeathMatchTournament;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004BotConfig;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004HumanConfig;
import cz.cuni.amis.pogamut.ut2004.tournament.teamdeathmatch.UT2004TeamDeathMatch;
import cz.cuni.amis.pogamut.ut2004.tournament.teamdeathmatch.UT2004TeamDeathMatchConfig;
import cz.cuni.amis.pogamut.ut2004.tournament.utils.UT2004TournamentProperty;

public class Main {

	public static UT2004BotConfig holBot(int number, int skillLevel) {
		return holBot(number, skillLevel, 255);
	}
	
	public static UT2004BotConfig holBot(int number, int skillLevel, int team) {
		UT2004BotConfig config = new UT2004BotConfig();
		
		config.setBotId("HolBot" + number);
		config.setPathToBotJar("d:/Workspaces/Pogamut-Competitions/2015/Matches/Bots-2015-Compiled/HolBot/pogamut-cup-2015-hol-bot-1.2-SNAPSHOT.one-jar.jar");
		config.setRedirectStdErr(true);
		config.setRedirectStdOut(true);
		config.setBotTeam(team);
	
		return config;
	}
	
	public static UT2004BotConfig gladiatorBot(int number, int skillLevel) {
		return gladiatorBot(number, skillLevel, 255);
	}
	
	public static UT2004BotConfig gladiatorBot(int number, int skillLevel, int team) {
		UT2004BotConfig config = new UT2004BotConfig();
		
		config.setBotId("GladiatorBot" + number);
		config.setPathToBotJar("d:/Workspaces/Pogamut-Competitions/2015/Matches/Bots-2015-Compiled/GladiatorBot/pogamut-cup-2015-gladiator-bot-2-pri-3.7.1-SNAPSHOT.one-jar.jar");
		config.setRedirectStdErr(true);
		config.setRedirectStdOut(true);
		config.setBotTeam(team);
	
		return config;
	}
	
	public static UT2004HumanConfig human(int number, int team) {
		UT2004HumanConfig config = new UT2004HumanConfig();
		
		config.setHumanId("Human" + number);
		config.setTeamNumber(team);
	
		return config;
	}
	
	public static void main(String[] args) {
		UT2004TeamDeathMatchConfig config = new UT2004TeamDeathMatchConfig();
		
		// UT2004 INI
		config.getUT2004Ini().setServerName("TDM", "TDM");
		config.getUT2004Ini().setDemoSpectatorClass(UT2004Ini.Value_DemoSpectatorClass);
		
		// UCC
		config.getUccConf().setStartOnUnusedPort(true);
	    config.getUccConf().setUnrealHome(Pogamut.getPlatform().getProperty(UT2004TournamentProperty.UT2004_DIR.getKey()));	
	    config.getUccConf().setGameType("BotTeamGame");
	    config.getUccConf().setMapName("DM-Rankin-FE");
	    
	    // OTHERS
	    config.setOutputDirectory(new File("./results"));
	    config.setMatchId("TDM");
	    config.setHumanLikeLogEnabled(false);
	    config.setStartTCServer(false);
		
		// BOTS
		config.addBot(
			holBot(1, 5, 1) 
			,holBot(2, 5, 1) 
//			,holBot(3, 5, 1)
		);
		
		// HUMANS
		config.addHuman(
				human(1, 0) 
//				,human(2, 0) 
//				,human(3, 0)
		);
		
		
		// CAPTURE THE FLAG SPECIFIC CONFIGS		
	    config.setScoreLimit(10);
	    config.setTimeLimit(10);
	     
	    // ------------
	    // START IT UP!
	    // ------------
	    
	    System.out.println("EXECUTING TEAM DEATH MATCH!");

	    LogCategory log = new LogCategory("TDM");
	    UT2004TeamDeathMatch match = new UT2004TeamDeathMatch(config, log);
	    
	    match.getLog().setLevel(Level.INFO);
	    match.getLog().addConsoleHandler();
	    
	    match.run();	
		
	}
	
}
