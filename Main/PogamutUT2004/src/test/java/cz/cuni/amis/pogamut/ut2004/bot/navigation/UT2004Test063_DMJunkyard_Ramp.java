package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test063_DMJunkyard_Ramp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Junkyard";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test63_ramp_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Junkyard.InventorySpot10, end: DM-Junkyard.PathNode100 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Junkyard.InventorySpot10",      "DM-Junkyard.PathNode100",    1,                        true)
		);
	}


        /**
        * TODO: Test fails
        */
        @Test
	public void test63_ramp_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Junkyard.InventorySpot10, end: DM-Junkyard.PathNode100 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Junkyard.InventorySpot10",      "DM-Junkyard.PathNode100",    20,                        true)
		);
	}

}