package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test135_DMCompressed_Jump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Compressed";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test135_jump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Compressed.InventorySpot98, end: DM-Compressed.InventorySpot79 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Compressed.InventorySpot98",      "DM-Compressed.InventorySpot79",    1,                        false)
		);
	}

        @Test
	public void test135_jump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 3 minutes
			3,
			// test movement between        start: DM-Compressed.InventorySpot98, end: DM-Compressed.InventorySpot79 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Compressed.InventorySpot98",      "DM-Compressed.InventorySpot79",    20,                        false)
		);
	}

}