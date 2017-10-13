package cz.cuni.amis.pogamut.ut2004.tournament.deathmatch;

import java.io.File;
import java.util.logging.Level;

import org.junit.AfterClass;
import org.junit.Test;

import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.ut2004.tournament.deathmatch.config.HunterBot;
import cz.cuni.amis.pogamut.ut2004.tournament.deathmatch.config.JakubBot;
import cz.cuni.amis.pogamut.ut2004.tournament.deathmatch.config.MichalBot;
import cz.cuni.amis.pogamut.ut2004.tournament.deathmatch.config.PyrohBot;
import cz.cuni.amis.pogamut.ut2004.tournament.deathmatch.config.RudaBot;
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
public class UT2004TournamentTest13_UT2004DeathMatchTournament {

	@AfterClass
	public static void tearDown() {
		Pogamut.getPlatform().close();
	}
	
	@Test
	public void test() {
		UT2004DeathMatchTournamentConfig config = new UT2004DeathMatchTournamentConfig();
		config.addBot(
				HunterBot.createConfig(), 
				JakubBot.createConfig(), 
				RudaBot.createConfig()
		);
		config.setMapName("DM-1on1-Albatross");
		config.setUnrealHome(Pogamut.getPlatform().getProperty(UT2004TournamentProperty.UT2004_DIR.getKey()));
		config.setFragLimit(20);
		config.setTimeLimitInMinutes(20);
		config.setNumBotsInOneMatch(2);
		config.setTournamentId("Tournament");
		config.setOutputDir("results" + File.separator + "tournament");
		
		LogCategory log = new LogCategory("Tournament");
		log.setLevel(Level.FINE);
		log.addConsoleHandler();
		UT2004DeathMatchTournament tournament = new UT2004DeathMatchTournament(config, log);
		tournament.cleanUp();
		tournament.run();
		
		if (tournament.getExceptions().size() > 0) {
			throw new RuntimeException("SOME MATCH HAS FAILED TO EXECUTE!");
		}
		if (tournament.getResults().size() != 3) {
			throw new RuntimeException("NOT ALL MATCH RESULTS HAS BEEN STORED WITHIN THE MATCH EXECUTOR!");
		}
		
		System.out.println("---/// TEST OK ///---");
		
	}
	
}
