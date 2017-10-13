package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test151_DM1on1Trite_Longrun extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-1on1-Trite";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test151_longrun_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 2 minute
			2,
			// test movement between        start: DM-1on1-Trite.InventorySpot67, end: DM-1on1-Trite.InventorySpot66 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-1on1-Trite.InventorySpot67",      "DM-1on1-Trite.InventorySpot66",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test151_longrun_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 14 minutes
			14,
			// test movement between        start: DM-1on1-Trite.InventorySpot67, end: DM-1on1-Trite.InventorySpot66 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-1on1-Trite.InventorySpot67",      "DM-1on1-Trite.InventorySpot66",    20,                        true)
		);
	}

}
