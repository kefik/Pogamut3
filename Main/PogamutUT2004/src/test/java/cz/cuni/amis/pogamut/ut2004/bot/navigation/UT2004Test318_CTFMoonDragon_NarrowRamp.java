package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test318_CTFMoonDragon_NarrowRamp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-MoonDragon";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test318_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-MoonDragon.PathNode334, end: CTF-MoonDragon.PathNode120 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-MoonDragon.PathNode334",      "CTF-MoonDragon.PathNode120",    1,                        true)
		);
	}

        @Test
	public void test318_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 6 minutes
			6,
			// test movement between        start: CTF-MoonDragon.PathNode334, end: CTF-MoonDragon.PathNode120 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-MoonDragon.PathNode334",      "CTF-MoonDragon.PathNode120",    20,                        true)
		);
	}

}