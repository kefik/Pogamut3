package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test073_DMInsidious_Longrun extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Insidious";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test73_longrun_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Insidious.InventorySpot253, end: DM-Insidious.InventorySpot254 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Insidious.InventorySpot253",      "DM-Insidious.InventorySpot254",    1,                        true)
		);
	}

        @Test
	public void test73_longrun_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 20 minutes
			20,
			// test movement between        start: DM-Insidious.InventorySpot253, end: DM-Insidious.InventorySpot254 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Insidious.InventorySpot253",      "DM-Insidious.InventorySpot254",    20,                        true)
		);
	}

}
