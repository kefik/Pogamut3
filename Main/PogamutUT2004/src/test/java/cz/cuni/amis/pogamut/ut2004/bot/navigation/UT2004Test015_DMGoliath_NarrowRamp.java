package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test015_DMGoliath_NarrowRamp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Goliath";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test15_narrow_1_time_bothways() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Goliath.InventorySpot175, end: DM-Goliath.PathNode85 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Goliath.InventorySpot175",      "DM-Goliath.PathNode85",    1,                        true)
		);
	}

        /**
         * TODO: Test fails
         */
        @Test
	public void test15_narrow_20_time_bothways() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 7 minutes
			7,
			// test movement between        start: DM-Goliath.InventorySpot175, end: DM-Goliath.PathNode85 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Goliath.InventorySpot175",      "DM-Goliath.PathNode85",    20,                        true)
		);
	}

}
