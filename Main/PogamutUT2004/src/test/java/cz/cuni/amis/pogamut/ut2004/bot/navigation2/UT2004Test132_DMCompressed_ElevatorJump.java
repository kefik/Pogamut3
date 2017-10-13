package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test132_DMCompressed_ElevatorJump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Compressed";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test132_elevatorjump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Compressed.PathNode47, end: DM-Compressed.InventorySpot96 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Compressed.PathNode47",      "DM-Compressed.InventorySpot96",    1,                        true)
		);
	}

         /*
        * TODO: Test fails
        * notice: this is VERY difficult test - bot would have to jump at the precise moment when the elevator stops
        */
        @Test
	public void test132_elevatorjump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Compressed.PathNode47, end: DM-Compressed.InventorySpot96 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Compressed.PathNode47",      "DM-Compressed.InventorySpot96",    20,                        true)
		);
	}

}
