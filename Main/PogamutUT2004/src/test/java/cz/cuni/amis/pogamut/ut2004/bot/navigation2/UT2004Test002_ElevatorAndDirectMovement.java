package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Knight
 */
public class UT2004Test002_ElevatorAndDirectMovement extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Goliath";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}
	
	@Test
	public void testElevatorAndDirectMovement_1_time_bothways() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Goliath.PathNode101, end: DM-Goliath.InventorySpot135 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Goliath.PathNode101",      "DM-Goliath.InventorySpot135",    1,                      true)
		);
	}

	@Test
	public void testElevatorAndDirectMovement_20_times_bothways() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 5 minutes
			5,
			// test movement between start: DM-Goliath.PathNode101, end: DM-Goliath.InventorySpot135 number of repetitions  both ways
			new Navigation2TestBotParameters("DM-Goliath.PathNode101", "DM-Goliath.InventorySpot135", 20,                    true)
		);
	}
}
