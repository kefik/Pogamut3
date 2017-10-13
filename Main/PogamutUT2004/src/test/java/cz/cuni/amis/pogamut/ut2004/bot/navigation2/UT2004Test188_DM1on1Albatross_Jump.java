package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 * 
 * @author Peta Michalik
 */
public class UT2004Test188_DM1on1Albatross_Jump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-1on1-Albatross";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

	@Test
	public void test188_jump_1_time() {
		startTest(
		// use NavigationTestBot for the test
				Navigation2TestBot.class,
				// timeout: 1 minute
				1,
				// test movement between start: DM-1on1-Albatross.PathNode21,
				// end: DM-1on1-Albatross.InventorySpot333 number of repetitions
				// both ways
				new Navigation2TestBotParameters("DM-1on1-Albatross.PathNode21",
						"DM-1on1-Albatross.InventorySpot333", 1, false));
	}

	/*
	 * TODO: Test fails
	 */
	@Test
	public void test188_jump_20_time() {

		startTest(
		// use NavigationTestBot for the test
				Navigation2TestBot.class,
				// timeout: 3 minutes
				3,
				// test movement between start: DM-1on1-Albatross.PathNode21,
				// end: DM-1on1-Albatross.InventorySpot333 number of repetitions
				// both ways
				new Navigation2TestBotParameters("DM-1on1-Albatross.PathNode21",
						"DM-1on1-Albatross.InventorySpot333", 20, false));
	}

}
