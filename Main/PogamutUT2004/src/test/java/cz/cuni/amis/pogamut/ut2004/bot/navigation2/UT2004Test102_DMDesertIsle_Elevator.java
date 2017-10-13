package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test102_DMDesertIsle_Elevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-DesertIsle";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void testelevator_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-DesertIsle.InventorySpot1, end: DM-DesertIsle.PathNode77 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-DesertIsle.InventorySpot1",      "DM-DesertIsle.PathNode77",    1,                        false)
		);
	}

        @Test
	public void testelevator_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-DesertIsle.InventorySpot1, end: DM-DesertIsle.PathNode77 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-DesertIsle.InventorySpot1",      "DM-DesertIsle.PathNode77",    20,                        false)
		);
	}

}
