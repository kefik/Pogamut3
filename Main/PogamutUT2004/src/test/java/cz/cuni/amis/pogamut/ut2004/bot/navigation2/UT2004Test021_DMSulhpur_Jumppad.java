package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test021_DMSulhpur_Jumppad extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Sulphur";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test21_jumppad_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Sulphur.PathNode37, end: DM-Sulphur.InventorySpot323 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Sulphur.PathNode37",      "DM-Sulphur.InventorySpot323",    1,                        false)
		);
	}

        @Test
	public void test21_jumppad_20_time() {

		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 2 minutes
			2,
			// test movement between        start: DM-Sulphur.PathNode37, end: DM-Sulphur.InventorySpot323 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Sulphur.PathNode37",      "DM-Sulphur.InventorySpot323",    20,                        false)
		);
	}

}
