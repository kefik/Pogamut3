package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test153_DM1on1Squader_Jumppad extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-1on1-Squader";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test153_jumpppad_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-1on1-Squader.InventorySpot99, end: DM-1on1-Squader.PathNode60 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-1on1-Squader.InventorySpot99",      "DM-1on1-Squader.PathNode60",    1,                        false)
		);
	}

        @Test
	public void test153_jumpppad_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 3 minutes
			3,
			// test movement between        start: DM-1on1-Squader.InventorySpot99, end: DM-1on1-Squader.PathNode60 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-1on1-Squader.InventorySpot99",      "DM-1on1-Squader.PathNode60",    20,                        false)
		);
	}

}
