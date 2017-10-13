package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test321_CTFMoonDragon_NarrowRamp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-MoonDragon";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test321_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-MoonDragon.PathNode9, end: CTF-MoonDragon.InventorySpot137 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-MoonDragon.PathNode9",      "CTF-MoonDragon.InventorySpot137",    1,                        true)
		);
	}

        /*
         * TODO: Test fails
         */
        @Test
	public void test321_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 5 minutes
			5,
			// test movement between        start: CTF-MoonDragon.PathNode9, end: CTF-MoonDragon.InventorySpot137 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-MoonDragon.PathNode9",      "CTF-MoonDragon.InventorySpot137",    20,                        true)
		);
	}

}