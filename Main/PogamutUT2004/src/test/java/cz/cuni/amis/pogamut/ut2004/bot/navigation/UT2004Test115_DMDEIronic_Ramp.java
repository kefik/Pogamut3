package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test115_DMDEIronic_Ramp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-DE-Ironic";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test115_ramp_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-DE-Ironic.PlayerStart2, end: DM-DE-Ironic.InventorySpot149 number of repetitions   both ways
			new NavigationTestBotParameters("DM-DE-Ironic.PlayerStart2",      "DM-DE-Ironic.InventorySpot149",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test115_ramp_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-DE-Ironic.PlayerStart2, end: DM-DE-Ironic.InventorySpot149 number of repetitions   both ways
			new NavigationTestBotParameters("DM-DE-Ironic.PlayerStart2",      "DM-DE-Ironic.InventorySpot149",    20,                        true)
		);
	}

}