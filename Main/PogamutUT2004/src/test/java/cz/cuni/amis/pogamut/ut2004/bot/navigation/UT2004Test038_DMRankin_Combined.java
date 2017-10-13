package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test038_DMRankin_Combined extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Rankin";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test38_combined_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Rankin.PlayerStart2, end: DM-Rankin.InventorySpot73 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Rankin.PlayerStart2",      "DM-Rankin.InventorySpot73",    1,                        true)
		);
	}

        @Test
	public void test38_combined_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 8 minutes
			8,
			// test movement between        start: DM-Rankin.PlayerStart2, end: DM-Rankin.InventorySpot73 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Rankin.PlayerStart2",      "DM-Rankin.InventorySpot73",    20,                        true)
		);
	}

}