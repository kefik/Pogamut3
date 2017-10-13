package cz.cuni.amis.pogamut.ut2004.tournament.deathmatch;

import java.io.File;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
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
public class UT2004TournamentTest01_UT2004DeathMatch {
	
	@Before
	public void before() {
		try {
			FileUtils.deleteQuietly(new File("test-results"));
		} catch (Exception e) {			
		}		
	}	
	
	@AfterClass
	public static void tearDown() {
		Pogamut.getPlatform().close();
	}
	
	private UT2004DeathMatchConfig configure1VsNative(String botName, String botPathToBotJar) {
		UT2004DeathMatchConfig matchConfig = new UT2004DeathMatchConfig();
		
		matchConfig.setMatchId("test-match");
		matchConfig.setOutputDirectory(new File("test-results" + File.separator + "matches"));
				
		matchConfig.setFragLimit(2);
		matchConfig.setTimeLimit(10); // in minutes
		
		matchConfig.getUccConf().setStartOnUnusedPort(true);
		matchConfig.getUccConf().setUnrealHome(Pogamut.getPlatform().getProperty(UT2004TournamentProperty.UT2004_DIR.getKey()));
		matchConfig.getUccConf().setGameType("BotDeathMatch");
		matchConfig.getUccConf().setMapName("DM-1on1-Albatross");
		
		UT2004BotConfig botConfig;
		
		botConfig = new UT2004BotConfig();
		botConfig.setBotId(botName);
		botConfig.setPathToBotJar(botPathToBotJar);
		
		botConfig.setParameter("hunterbot.say", "HELLO");
		
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
	
	private UT2004DeathMatchConfig configure1Vs1(String botName1, String botPathToBotJar1, String botName2, String botPathToBotJar2) {
		UT2004DeathMatchConfig matchConfig = new UT2004DeathMatchConfig();
		
		matchConfig.setMatchId("test-match");
		matchConfig.setOutputDirectory(new File("test-results" + File.separator + "matches"));
				
		matchConfig.setFragLimit(2);
		matchConfig.setTimeLimit(10); // in minutes
		
		matchConfig.getUccConf().setStartOnUnusedPort(true);
		matchConfig.getUccConf().setUnrealHome(Pogamut.getPlatform().getProperty(UT2004TournamentProperty.UT2004_DIR.getKey()));
		matchConfig.getUccConf().setGameType("BotDeathMatch");
		matchConfig.getUccConf().setMapName("DM-1on1-Albatross");
		
		UT2004BotConfig botConfig;
		
		botConfig = new UT2004BotConfig();
		botConfig.setBotId(botName1);
		botConfig.setPathToBotJar(botPathToBotJar1);
		botConfig.setBotTeam(255);
		botConfig.setRedirectStdErr(true);
		botConfig.setRedirectStdOut(true);		
		matchConfig.addBot(botConfig);
		
		botConfig = new UT2004BotConfig();
		botConfig.setBotId(botName2);
		botConfig.setPathToBotJar(botPathToBotJar2);
		botConfig.setBotTeam(255);
		botConfig.setRedirectStdErr(true);
		botConfig.setRedirectStdOut(true);		
		matchConfig.addBot(botConfig);
		
		return matchConfig;
	}
	
	@Test
	public void test01() {
		LogCategory log = new LogCategory("DMMatch");
		log.setLevel(Level.FINE);
		log.addConsoleHandler();
		
		UT2004DeathMatchConfig matchConfig = configure1VsNative("JakubBot", "bots" + File.separator + "KefikBot" + File.separator + "KefikBot.jar");
		
		UT2004DeathMatch match = new UT2004DeathMatch(matchConfig, log);
		
		UT2004DeathMatchResult result = match.execute();
		
		System.out.println("Result: " + result);
		
		checkTestOutput();
		
		System.out.println("[OK] All match result files exist!");
		
		System.out.println("---/// TEST OK ///---");
	}
	
	@Test
	public void test02() {
		LogCategory log = new LogCategory("DMMatch");
		log.setLevel(Level.FINE);
		log.addConsoleHandler();
		
		UT2004DeathMatchConfig matchConfig = configure1VsNative("MichalBot", "bots" + File.separator + "KnightHunter" + File.separator + "KnightHunter.jar");
		
		UT2004DeathMatch match = new UT2004DeathMatch(matchConfig, log);
		
		UT2004DeathMatchResult result = match.execute();
		
		System.out.println("Result: " + result);
		
		checkTestOutput();
		
		System.out.println("[OK] All match result files exist!");
		
		System.out.println("---/// TEST OK ///---");
	}
	
	@Test
	public void test03() {
		LogCategory log = new LogCategory("DMMatch");
		log.setLevel(Level.FINE);
		log.addConsoleHandler();
		
		UT2004DeathMatchConfig matchConfig = configure1VsNative("PyrohBot", "bots" + File.separator + "TobRetnuh" + File.separator + "TobRetnuh.jar");
		
		UT2004DeathMatch match = new UT2004DeathMatch(matchConfig, log);
		
		UT2004DeathMatchResult result = match.execute();
		
		System.out.println("Result: " + result);
		
		checkTestOutput();
		
		System.out.println("[OK] All match result files exist!");
		
		System.out.println("---/// TEST OK ///---");
	}
	
	@Test
	public void test04() {
		LogCategory log = new LogCategory("DMMatch");
		log.setLevel(Level.FINE);
		log.addConsoleHandler();
		
		UT2004DeathMatchConfig matchConfig = configure1Vs1("JakubBot",  "bots" + File.separator + "KefikBot"     + File.separator + "KefikBot.jar",
														   "MichalBot", "bots" + File.separator + "KnightHunter" + File.separator + "KnightHunter.jar"
														  );
		
		UT2004DeathMatch match = new UT2004DeathMatch(matchConfig, log);
		
		UT2004DeathMatchResult result = match.execute();
		
		System.out.println("Result: " + result);
		
		checkTestOutput();
		
		System.out.println("[OK] All match result files exist!");
		
		System.out.println("---/// TEST OK ///---");
	}
	
	@Test
	public void test05() {
		LogCategory log = new LogCategory("DMMatch");
		log.setLevel(Level.FINE);
		log.addConsoleHandler();
		
		UT2004DeathMatchConfig matchConfig = configure1VsNative("HunterBot", "bots" + File.separator + "HunterBot" + File.separator + "HunterBot.jar");
		
		UT2004DeathMatch match = new UT2004DeathMatch(matchConfig, log);
		
		UT2004DeathMatchResult result = match.execute();
		
		System.out.println("Result: " + result);
		
		checkTestOutput();
		
		System.out.println("[OK] All match result files exist!");
		
		System.out.println("---/// TEST OK ///---");
	}
	
	@Test
	public void test06() {
		LogCategory log = new LogCategory("DMMatch");
		log.setLevel(Level.FINE);
		log.addConsoleHandler();
		
		UT2004DeathMatchConfig matchConfig = configure1VsNative("PoJACTRHunter", "bots" + File.separator + "PoJACTRHunter" + File.separator + "PoJACTRHunter.jar");
		
		UT2004DeathMatch match = new UT2004DeathMatch(matchConfig, log);
		
		UT2004DeathMatchResult result = match.execute();
		
		System.out.println("Result: " + result);
		
		checkTestOutput();
		
		System.out.println("[OK] All match result files exist!");
		
		System.out.println("---/// TEST OK ///---");
	}

	private void checkTestOutput() {
		File file;
		file = new File("test-results/matches/test-match/match-test-match-replay.demo4"); 
		if (!file.exists()) {
			System.out.println("[ERROR] File does not exist (was not produced): " + file.getAbsolutePath());
			throw new RuntimeException("[ERROR] File does not exist (was not produced): " + file.getAbsolutePath());
		}
		file = new File("test-results/matches/test-match/match-test-match-result.csv"); 
		if (!file.exists()) {
			System.out.println("[ERROR] File does not exist (was not produced): " + file.getAbsolutePath());
			throw new RuntimeException("[ERROR] File does not exist (was not produced): " + file.getAbsolutePath());
		}
		file = new File("test-results/matches/test-match/match-test-match-bot-scores.csv"); 
		if (!file.exists()) {
			System.out.println("[ERROR] File does not exist (was not produced): " + file.getAbsolutePath());
			throw new RuntimeException("[ERROR] File does not exist (was not produced): " + file.getAbsolutePath());
		}
		file = new File("test-results/matches/test-match/bots"); 
		if (!file.exists()) {
			System.out.println("[ERROR] Directory does not exist (was not produced): " + file.getAbsolutePath());
			throw new RuntimeException("[ERROR] Directory does not exist (was not produced): " + file.getAbsolutePath());
		}
	}

}
