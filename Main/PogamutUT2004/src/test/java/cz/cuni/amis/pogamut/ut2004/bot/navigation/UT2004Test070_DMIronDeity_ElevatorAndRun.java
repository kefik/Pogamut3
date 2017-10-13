package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test070_DMIronDeity_ElevatorAndRun extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-IronDeity";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test70_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-IronDeity.PathNode17, end: DM-IronDeity.InventorySpot9 number of repetitions   both ways
			new NavigationTestBotParameters("DM-IronDeity.PathNode17",      "DM-IronDeity.InventorySpot9",    1,                        true)
		);
	}

        /**
        * TODO: Test fails
        * (bot kills himself by jumping down - the elevator keeps waiting in the down position
         *  --> this test is practically unpassable)
        */
        @Test
	public void test70_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 15 minutes
			15,
			// test movement between        start: DM-IronDeity.PathNode17, end: DM-IronDeity.InventorySpot9 number of repetitions   both ways
			new NavigationTestBotParameters("DM-IronDeity.PathNode17",      "DM-IronDeity.InventorySpot9",    20,                        true)
		);
	}

}