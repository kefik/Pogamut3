package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test074_DMInsidious_Corner extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Insidious";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test74_corner_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Insidious.PlayerStart4, end: DM-Insidious.InventorySpot252 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Insidious.PlayerStart4",      "DM-Insidious.InventorySpot252",    1,                        true)
		);
	}

        @Test
	public void test74_corner_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Insidious.PlayerStart4, end: DM-Insidious.InventorySpot252 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Insidious.PlayerStart4",      "DM-Insidious.InventorySpot252",    20,                        true)
		);
	}

}