package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test175_DM1on1Irondust_Elevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-1on1-Irondust";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test175_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-1on1-Irondust.InventorySpot55, end: DM-1on1-Irondust.LiftExit1 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-1on1-Irondust.InventorySpot55",      "DM-1on1-Irondust.LiftExit1",    1,                        true)
		);
	}

        @Test
	public void test175_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-1on1-Irondust.InventorySpot55, end: DM-1on1-Irondust.LiftExit1 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-1on1-Irondust.InventorySpot55",      "DM-1on1-Irondust.LiftExit1",    20,                        true)
		);
	}

}
