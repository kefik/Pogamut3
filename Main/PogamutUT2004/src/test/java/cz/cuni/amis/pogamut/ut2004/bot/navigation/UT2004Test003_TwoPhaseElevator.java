package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Knight
 */
public class UT2004Test003_TwoPhaseElevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Metallurgy";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

	@Test
	public void testTwoPhaseElevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between start: DM-Metallurgy.InventorySpot89, end: DM-Metallurgy.PathNode58 number of repetitions both_ways
			new NavigationTestBotParameters("DM-Metallurgy.InventorySpot89", "DM-Metallurgy.PathNode58", 1, true)		
		);
	}

	@Test
	public void testTwoPhaseElevator_20_times() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 5 minutes
			5,
			// test movement between start: DM-Metallurgy.InventorySpot89, end: DM-Metallurgy.PathNode58 number of repetitions both_ways
			new NavigationTestBotParameters("DM-Metallurgy.InventorySpot89", "DM-Metallurgy.PathNode58", 20, true)
		);
	}
}
