package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test060_DMMetallurgy_Combined extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Metallurgy";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test60_combined_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Metallurgy.JumpSpot0, end: DM-Metallurgy.InventorySpot57 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Metallurgy.JumpSpot0",      "DM-Metallurgy.InventorySpot57",    1,                        true)
		);
	}

        @Test
	public void test60_combined_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Metallurgy.JumpSpot0, end: DM-Metallurgy.InventorySpot57 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Metallurgy.JumpSpot0",      "DM-Metallurgy.InventorySpot57",    20,                        true)
		);
	}

}