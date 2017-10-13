package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test316_CTFMoonDragon_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-MoonDragon";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test316_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-MoonDragon.PathNode558, end: CTF-MoonDragon.PathNode556 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-MoonDragon.PathNode558",      "CTF-MoonDragon.PathNode556",    1,                        false)
		);
	}

        /*
         * TODO: Test fails
         */
        @Test
	public void test316_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 2 minutes
			2,
			// test movement between        start: CTF-MoonDragon.PathNode558, end: CTF-MoonDragon.PathNode556 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-MoonDragon.PathNode558",      "CTF-MoonDragon.PathNode556",    20,                        false)
		);
	}

}