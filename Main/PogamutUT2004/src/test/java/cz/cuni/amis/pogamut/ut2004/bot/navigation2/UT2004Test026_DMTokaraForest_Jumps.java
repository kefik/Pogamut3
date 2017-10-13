package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test026_DMTokaraForest_Jumps extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-TokaraForest";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test26_jumps_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-TokaraForest.InventorySpot408, end: DM-TokaraForest.PathNode22 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-TokaraForest.InventorySpot408",      "DM-TokaraForest.PathNode22",    1,                        false)
		);
	}

        @Test
	public void test26_jumps_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 5 minutes
			5,
			// test movement between        start: DM-TokaraForest.InventorySpot408, end: DM-TokaraForest.PathNode22 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-TokaraForest.InventorySpot408",      "DM-TokaraForest.PathNode22",    20,                        false)
		);
	}

}
