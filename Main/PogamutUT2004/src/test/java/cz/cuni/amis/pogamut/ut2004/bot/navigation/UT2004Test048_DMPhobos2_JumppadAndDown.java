package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test048_DMPhobos2_JumppadAndDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Phobos2";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test48_jumppad_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Phobos2.PathNode155, end: DM-Phobos2.InventorySpot526 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Phobos2.PathNode155",      "DM-Phobos2.InventorySpot526",    1,                        true)
		);
	}

        /**
        * TODO: Test fails
        */
        @Test
	public void test48_jumppad_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Phobos2.PathNode155, end: DM-Phobos2.InventorySpot526 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Phobos2.PathNode155",      "DM-Phobos2.InventorySpot526",    20,                        true)
		);
	}

}