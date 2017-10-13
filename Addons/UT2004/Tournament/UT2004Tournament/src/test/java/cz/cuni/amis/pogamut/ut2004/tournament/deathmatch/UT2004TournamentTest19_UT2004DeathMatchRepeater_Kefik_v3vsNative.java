package cz.cuni.amis.pogamut.ut2004.tournament.deathmatch;

import java.io.File;
import java.util.logging.Level;

import org.junit.AfterClass;
import org.junit.Test;

import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.ut2004.tournament.deathmatch.config.JakubBot_v2;
import cz.cuni.amis.pogamut.ut2004.tournament.deathmatch.config.JakubBot_v3;
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
public class UT2004TournamentTest19_UT2004DeathMatchRepeater_Kefik_v3vsNative {

	@AfterClass
	public static void tearDown() {
		Pogamut.getPlatform().close();
	}
	
	private UT2004DeathMatchConfig configure1VsNative() {
		UT2004DeathMatchConfig matchConfig = new UT2004DeathMatchConfig();
		
		matchConfig.setMatchId("KefikBot_v3-vs-NativeBot");
		matchConfig.setOutputDirectory(new File("results" + File.separator + "matches"));
				
		matchConfig.setFragLimit(200);
		matchConfig.setTimeLimit(200); // in minutes
		
		matchConfig.getUccConf().setStartOnUnusedPort(true);
		matchConfig.getUccConf().setUnrealHome(Pogamut.getPlatform().getProperty(UT2004TournamentProperty.UT2004_DIR.getKey()));
		matchConfig.getUccConf().setGameType("BotDeathMatch");
		matchConfig.getUccConf().setMapName("DM-1on1-Albatross");
		
		UT2004BotConfig botConfig;
		
		matchConfig.addBot(JakubBot_v3.createConfig());
		
		UT2004NativeBotConfig nativeBotConfig;
		
		nativeBotConfig = new UT2004NativeBotConfig();
		nativeBotConfig.setBotId("NativeBot");
		nativeBotConfig.setDesiredSkill(5);
		nativeBotConfig.setTeamNumber(255);
		matchConfig.addNativeBot(nativeBotConfig);
		
		return matchConfig;
	}
	
	private UT2004DeathMatchConfig createMatch() {
		return configure1VsNative();
	}
	
	@Test
	public void test() {
		UT2004DeathMatchConfig match = createMatch();		
		UT2004DeathMatchRepeater repeater = new UT2004DeathMatchRepeater(match, 1, new LogCategory("DMRepeater"));
		repeater.getLog().addConsoleHandler();
		repeater.getLog().setLevel(Level.FINE);
		repeater.run();
		
		System.out.println("---/// TEST OK ///---");
	}

	
	
}
