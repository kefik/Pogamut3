package cz.cuni.amis.pogamut.ut2004.tournament.deathmatch;

import java.util.logging.Level;

import org.junit.AfterClass;
import org.junit.Test;

import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.ut2004.tournament.deathmatch.UT2004DeathMatch;
import cz.cuni.amis.pogamut.ut2004.tournament.deathmatch.UT2004DeathMatchResult;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004MatchExecutor;

/**
 * Generic test that is using various bots from 'bots' folder and runs them agains native bot.
 * 
 * WARNING: you must have correctly set property "pogamut.ut2004.tournament.ut2004.dir", i.e., edit PogamutUT2004Tournament.properties !!!
 * 
 * OR you can start the JVM with -Dpogamut.ut2004.tournament.ut2004.dir=path/to/your/ut2004
 * 
 * @author Jimmy
 */
public class UT2004TournamentTest12_UT2004MatchExecutor {

	@AfterClass
	public static void tearDown() {
		Pogamut.getPlatform().close();
	}
	
	@Test
	public void test() {
		// INITIALIZE MATCHES
		UT2004DeathMatch[] matches = new UT2004DeathMatch[] {
			UT2004TournamentTest02_UT2004DeathMatch_JakubVsMichal.createMatch(),
			UT2004TournamentTest03_UT2004DeathMatch_JakubVsPyroh.createMatch(),
			UT2004TournamentTest04_UT2004DeathMatch_JakubVsHunter.createMatch(),
			UT2004TournamentTest05_UT2004DeathMatch_JakubVsRuda.createMatch(),
			UT2004TournamentTest06_UT2004DeathMatch_MichalVsPyroh.createMatch(),
			UT2004TournamentTest07_UT2004DeathMatch_MichalVsHunter.createMatch(),
			UT2004TournamentTest08_UT2004DeathMatch_MichalVsRuda.createMatch(),
			UT2004TournamentTest09_UT2004DeathMatch_PyrohVsHunter.createMatch(),
			UT2004TournamentTest10_UT2004DeathMatch_PyrohVsRuda.createMatch(),
			UT2004TournamentTest11_UT2004DeathMatch_HunterVsRuda.createMatch()
		};
		
		// CLEANS UP DIRECTORY WITH RESULTS
		for (UT2004DeathMatch match : matches) {
			match.getLog().setLevel(Level.FINE);
			match.getLog().addConsoleHandler();
			match.cleanUp();
		}
		
		// SETUP UT2004MatchExecutor
		LogCategory log = new LogCategory("UT2004MatchExecutor");
		log.setLevel(Level.ALL);
		log.addConsoleHandler();
		UT2004MatchExecutor<UT2004DeathMatch, UT2004DeathMatchResult> matchExecutor = new UT2004MatchExecutor<UT2004DeathMatch, UT2004DeathMatchResult>(matches, log);
		
		// EXECUTE ALL MATCHES
		matchExecutor.run();
		
		// CHECK RESULTS
		if (matchExecutor.getExceptions().size() > 0) {
			throw new RuntimeException("SOME MATCH HAS FAILED TO EXECUTE!");
		}
		if (matchExecutor.getResults().size() != matches.length) {
			throw new RuntimeException("NOT ALL MATCH RESULTS HAS BEEN STORED WITHIN THE MATCH EXECUTOR!");
		}
		for (UT2004DeathMatch match : matches) {
			if (!matchExecutor.getResults().containsKey(match.getMatchId())) {
				throw new RuntimeException("MATCH RESULT OF ID " + match.getMatchId().getToken() + " IS NOT STORED WITHIN THE MATCH EXECUTOR!");
			}
		}
		
		System.out.println("---/// TEST OK ///---");
	}
	
}
