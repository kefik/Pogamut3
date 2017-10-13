package cz.cuni.amis.pogamut.ut2004.tournament.capturetheflag;

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
public class UT2004TournamentTest01_UT2004CaptureTheFlag {
	
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
	
	private UT2004CaptureTheFlagConfig configure1AndNativeVs2Natives(String botName, String botPathToBotJar) {
		UT2004CaptureTheFlagConfig matchConfig = new UT2004CaptureTheFlagConfig();
		
		matchConfig.setMatchId("test-ctf-match");
		matchConfig.setOutputDirectory(new File("test-results" + File.separator + "matches"));
				
		matchConfig.setScoreLimit(10);
		matchConfig.setTimeLimit(1); // in minutes
		
		matchConfig.getUccConf().setStartOnUnusedPort(true);
		matchConfig.getUccConf().setUnrealHome(Pogamut.getPlatform().getProperty(UT2004TournamentProperty.UT2004_DIR.getKey()));
		matchConfig.getUccConf().setMapName("CTF-1on1-Joust");
		
		UT2004BotConfig botConfig;
				
		botConfig = new UT2004BotConfig();
		botConfig.setBotId("Team1_" + botName);
		botConfig.setPathToBotJar(botPathToBotJar);
		botConfig.setBotTeam(1);
		botConfig.setRedirectStdErr(true);
		botConfig.setRedirectStdOut(true);		
		matchConfig.addBot(botConfig);
		
		UT2004NativeBotConfig nativeBotConfig;
		
		nativeBotConfig = new UT2004NativeBotConfig();
		nativeBotConfig.setBotId("Team1_NativeBot1");
		nativeBotConfig.setDesiredSkill(5);
		nativeBotConfig.setTeamNumber(1);
		matchConfig.addNativeBot(nativeBotConfig);
		
		nativeBotConfig = new UT2004NativeBotConfig();
		nativeBotConfig.setBotId("Team0_NativeBot2");
		nativeBotConfig.setDesiredSkill(5);
		nativeBotConfig.setTeamNumber(0);
		matchConfig.addNativeBot(nativeBotConfig);
		
		nativeBotConfig = new UT2004NativeBotConfig();
		nativeBotConfig.setBotId("Team0_NativeBot3");
		nativeBotConfig.setDesiredSkill(5);
		nativeBotConfig.setTeamNumber(0);
		matchConfig.addNativeBot(nativeBotConfig);
		
		return matchConfig;
	}
	
	@Test
	public void test01() {
		LogCategory log = new LogCategory("CTFMatch");
		log.setLevel(Level.FINE);
		log.addConsoleHandler();
		
		UT2004CaptureTheFlagConfig matchConfig = configure1AndNativeVs2Natives("HunterBot", "bots" + File.separator + "HunterBot" + File.separator + "HunterBot.jar");
		
		UT2004CaptureTheFlag match = new UT2004CaptureTheFlag(matchConfig, log);
		
		UT2004CaptureTheFlagResult result = match.execute();
		
		System.out.println("Result: " + result);
		
		checkTestOutput();
		
		System.out.println("[OK] All match result files exist!");
		
		System.out.println("---/// TEST OK ///---");
	}

	private void checkTestOutput() {
		File file;
		file = new File("test-results/matches/test-ctf-match/match-test-ctf-match-replay.demo4"); 
		if (!file.exists()) {
			System.out.println("[ERROR] File does not exist (was not produced): " + file.getAbsolutePath());
			throw new RuntimeException("[ERROR] File does not exist (was not produced): " + file.getAbsolutePath());
		}
		file = new File("test-results/matches/test-ctf-match/match-test-ctf-match-result.csv"); 
		if (!file.exists()) {
			System.out.println("[ERROR] File does not exist (was not produced): " + file.getAbsolutePath());
			throw new RuntimeException("[ERROR] File does not exist (was not produced): " + file.getAbsolutePath());
		}
		file = new File("test-results/matches/test-ctf-match/match-test-ctf-match-team-scores.csv"); 
		if (!file.exists()) {
			System.out.println("[ERROR] File does not exist (was not produced): " + file.getAbsolutePath());
			throw new RuntimeException("[ERROR] File does not exist (was not produced): " + file.getAbsolutePath());
		}
		file = new File("test-results/matches/test-ctf-match/match-test-ctf-match-bot-scores.csv"); 
		if (!file.exists()) {
			System.out.println("[ERROR] File does not exist (was not produced): " + file.getAbsolutePath());
			throw new RuntimeException("[ERROR] File does not exist (was not produced): " + file.getAbsolutePath());
		}
		file = new File("test-results/matches/test-ctf-match/bots"); 
		if (!file.exists()) {
			System.out.println("[ERROR] Directory does not exist (was not produced): " + file.getAbsolutePath());
			throw new RuntimeException("[ERROR] Directory does not exist (was not produced): " + file.getAbsolutePath());
		}
	}

}
