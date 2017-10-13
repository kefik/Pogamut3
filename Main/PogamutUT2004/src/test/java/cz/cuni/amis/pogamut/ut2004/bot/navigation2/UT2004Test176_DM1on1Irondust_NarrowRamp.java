package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test176_DM1on1Irondust_NarrowRamp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-1on1-Irondust";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test176_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-1on1-Irondust.PathNode25, end: DM-1on1-Irondust.InventorySpot51 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-1on1-Irondust.PathNode25",      "DM-1on1-Irondust.InventorySpot51",    1,                        true)
		);
	}

        @Test
	public void test176_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 5 minutes
			5,
			// test movement between        start: DM-1on1-Irondust.PathNode25, end: DM-1on1-Irondust.InventorySpot51 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-1on1-Irondust.PathNode25",      "DM-1on1-Irondust.InventorySpot51",    20,                        true)
		);
	}

}
