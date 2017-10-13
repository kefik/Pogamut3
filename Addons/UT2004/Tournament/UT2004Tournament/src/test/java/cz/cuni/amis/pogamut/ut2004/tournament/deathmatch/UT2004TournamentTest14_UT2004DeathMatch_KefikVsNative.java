package cz.cuni.amis.pogamut.ut2004.tournament.deathmatch;

import java.io.File;
import java.util.logging.Level;

import org.junit.AfterClass;
import org.junit.Test;

import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004BotConfig;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004NativeBotConfig;
import cz.cuni.amis.pogamut.ut2004.tournament.utils.UT2004TournamentProperty;

/**
 * Generic test that is using various bots from 'bots' folder and runs them agains native bot.
 * 
 * WARNING: you must have correctly set property "pogamut.ut2004.tournament.ut2004.dir", i.e., edit PogamutUT2004Tournament.properties !!!
 * 
 * OR you can start the JVM with -Dpogamut.ut2004.tournament.ut2004.dir=path/to/your/ut2004
 * 
 * @author Jimmy
 */
public class UT2004TournamentTest14_UT2004DeathMatch_KefikVsNative {

	@AfterClass
	public static void tearDown() {
		Pogamut.getPlatform().close();
	}
	
	private UT2004DeathMatchConfig configure1VsNative(String botName, String botPathToBotJar) {
		UT2004DeathMatchConfig matchConfig = new UT2004DeathMatchConfig();
		
		matchConfig.setMatchId("KefikBot-vs-NativeBot");
		matchConfig.setOutputDirectory(new File("results" + File.separator + "matches"));
				
		matchConfig.setFragLimit(2);
		matchConfig.setTimeLimit(20); // in minutes
		
		matchConfig.getUccConf().setStartOnUnusedPort(true);
		matchConfig.getUccConf().setUnrealHome(Pogamut.getPlatform().getProperty(UT2004TournamentProperty.UT2004_DIR.getKey()));
		matchConfig.getUccConf().setGameType("BotDeathMatch");
		matchConfig.getUccConf().setMapName("DM-1on1-Albatross");
		
		UT2004BotConfig botConfig;
		
		botConfig = new UT2004BotConfig();
		botConfig.setBotId(botName);
		botConfig.setPathToBotJar(botPathToBotJar);
		botConfig.setBotTeam(255);
		botConfig.setRedirectStdErr(true);
		botConfig.setRedirectStdOut(true);		
		matchConfig.addBot(botConfig);
		
		UT2004NativeBotConfig nativeBotConfig;
		
		nativeBotConfig = new UT2004NativeBotConfig();
		nativeBotConfig.setBotId("NativeBot");
		nativeBotConfig.setDesiredSkill(5);
		nativeBotConfig.setTeamNumber(255);
		matchConfig.addNativeBot(nativeBotConfig);
		
		return matchConfig;
	}
	
	private UT2004DeathMatch createMatch() {
		return new UT2004DeathMatch(configure1VsNative("KefikBot", "bots" + File.separator + "KefikBot" + File.separator + "KefikBot.jar"), new LogCategory("DM"));
	}
	
	@Test
	public void test() {
		UT2004DeathMatch match = createMatch();
		match.getLog().setLevel(Level.FINE);
		match.getLog().addConsoleHandler();
		match.cleanUp();
		match.run();
		System.out.println("---/// TEST OK ///---");
	}

	
	
}
