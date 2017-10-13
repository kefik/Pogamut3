package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 * 
 * @author Peta Michalik
 */
public class UT2004Test189_DM1on1Albatross_Elevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-1on1-Albatross";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

	@Test
	public void test189_elevator_1_time() {
		startTest(
		// use NavigationTestBot for the test
				Navigation2TestBot.class,
				// timeout: 1 minute
				1,
				// test movement between start:
				// DM-1on1-Albatross.InventorySpot342, end:
				// DM-1on1-Albatross.InventorySpot363 number of repetitions both
				// ways
				new Navigation2TestBotParameters(
						"DM-1on1-Albatross.InventorySpot342",
						"DM-1on1-Albatross.InventorySpot363", 1, true));
	}

	/*
	 * TODO: Test fails
	 */
	@Test
	public void test189_elevator_20_time() {

		startTest(
		// use NavigationTestBot for the test
				Navigation2TestBot.class,
				// timeout: 4 minutes
				4,
				// test movement between start:
				// DM-1on1-Albatross.InventorySpot342, end:
				// DM-1on1-Albatross.InventorySpot363 number of repetitions both
				// ways
				new Navigation2TestBotParameters(
						"DM-1on1-Albatross.InventorySpot342",
						"DM-1on1-Albatross.InventorySpot363", 20, true));
	}

}
