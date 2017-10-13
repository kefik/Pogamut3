package cz.cuni.amis.pogamut.ut2004.tournament.deathmatch;

import java.io.File;
import java.util.logging.Level;

import org.junit.AfterClass;
import org.junit.Test;

import cz.cuni.amis.pogamut.base.utils.Pogamut;
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
public class UT2004TournamentTest09_UT2004DeathMatch_PyrohVsHunter {
	
	@AfterClass
	public static void tearDown() {
		Pogamut.getPlatform().close();
	}
	
	public static UT2004DeathMatch createMatch() {
		return
			new UT2004DeathMatch1v1(
					Pogamut.getPlatform().getProperty(UT2004TournamentProperty.UT2004_DIR.getKey()),
					"DM-1on1-Albatross",
					"PyrohBot",
					"bots" + File.separator + "TobRetnuh" + File.separator + "TobRetnuh.jar",
					"HunterBot",
					"bots" + File.separator + "HunterBot" + File.separator + "HunterBot.jar"
			).createMatch();
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
