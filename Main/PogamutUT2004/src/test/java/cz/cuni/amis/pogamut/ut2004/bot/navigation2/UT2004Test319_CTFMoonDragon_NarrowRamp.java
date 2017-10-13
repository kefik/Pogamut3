package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test319_CTFMoonDragon_NarrowRamp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-MoonDragon";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test319_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-MoonDragon.PathNode134, end: CTF-MoonDragon.PathNode199 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-MoonDragon.PathNode134",      "CTF-MoonDragon.PathNode199",    1,                        true)
		);
	}

        /*
         * TODO: Test fails
         */
        @Test
	public void test319_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: CTF-MoonDragon.PathNode134, end: CTF-MoonDragon.PathNode199 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-MoonDragon.PathNode134",      "CTF-MoonDragon.PathNode199",    20,                        true)
		);
	}

}
