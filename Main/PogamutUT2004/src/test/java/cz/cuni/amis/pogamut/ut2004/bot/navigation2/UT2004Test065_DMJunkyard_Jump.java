package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test065_DMJunkyard_Jump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Junkyard";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test65_jump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Junkyard.InventorySpot16, end: DM-Junkyard.PathNode272 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Junkyard.InventorySpot16",      "DM-Junkyard.PathNode272",    1,                        false)
		);
	}

        @Test
	public void test65_jump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Junkyard.InventorySpot16, end: DM-Junkyard.PathNode272 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Junkyard.InventorySpot16",      "DM-Junkyard.PathNode272",    20,                        false)
		);
	}

}
