package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test069_DMIronDeity_Corner extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-IronDeity";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test69_corner_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-IronDeity.InventorySpot44, end: DM-IronDeity.InventorySpot46 number of repetitions   both ways
			new NavigationTestBotParameters("DM-IronDeity.InventorySpot44",      "DM-IronDeity.InventorySpot46",    1,                        true)
		);
	}

        @Test
	public void test69_corner_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-IronDeity.InventorySpot44, end: DM-IronDeity.InventorySpot46 number of repetitions   both ways
			new NavigationTestBotParameters("DM-IronDeity.InventorySpot44",      "DM-IronDeity.InventorySpot46",    20,                        true)
		);
	}

}