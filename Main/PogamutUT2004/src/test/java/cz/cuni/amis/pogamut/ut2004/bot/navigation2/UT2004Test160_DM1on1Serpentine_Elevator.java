package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test160_DM1on1Serpentine_Elevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-1on1-Serpentine";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test160_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-1on1-Serpentine.InventorySpot1, end: DM-1on1-Serpentine.InventorySpot5 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-1on1-Serpentine.InventorySpot1",      "DM-1on1-Serpentine.InventorySpot5",    1,                        true)
		);
	}

        @Test
	public void test160_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-1on1-Serpentine.InventorySpot1, end: DM-1on1-Serpentine.InventorySpot5 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-1on1-Serpentine.InventorySpot1",      "DM-1on1-Serpentine.InventorySpot5",    20,                        true)
		);
	}

}
