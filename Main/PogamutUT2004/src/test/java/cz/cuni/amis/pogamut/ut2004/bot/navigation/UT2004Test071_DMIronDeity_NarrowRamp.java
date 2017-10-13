package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test071_DMIronDeity_NarrowRamp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-IronDeity";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test71_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-IronDeity.PathNode3, end: DM-IronDeity.InventorySpot40 number of repetitions   both ways
			new NavigationTestBotParameters("DM-IronDeity.PathNode3",      "DM-IronDeity.InventorySpot40",    1,                        true)
		);
	}

         /**
        * TODO: Test fails
        */
        @Test
	public void test71_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-IronDeity.PathNode3, end: DM-IronDeity.InventorySpot40 number of repetitions   both ways
			new NavigationTestBotParameters("DM-IronDeity.PathNode3",      "DM-IronDeity.InventorySpot40",    20,                        true)
		);
	}

}