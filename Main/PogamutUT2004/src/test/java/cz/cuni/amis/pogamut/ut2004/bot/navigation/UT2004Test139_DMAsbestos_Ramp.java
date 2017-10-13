package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test139_DMAsbestos_Ramp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Asbestos";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test139_ramp_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Asbestos.PlayerStart6, end: DM-Asbestos.InventorySpot29 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Asbestos.PlayerStart6",      "DM-Asbestos.InventorySpot29",    1,                        true)
		);
	}

        @Test
	public void test139_ramp_20_time() {

		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Asbestos.PlayerStart6, end: DM-Asbestos.InventorySpot29 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Asbestos.PlayerStart6",      "DM-Asbestos.InventorySpot29",    20,                        true)
		);
	}

}