package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test100_DMFlux2_Longrun extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Flux2";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test100_longrun_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Flux2.InventorySpot85, end: DM-Flux2.InventorySpot95 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Flux2.InventorySpot85",      "DM-Flux2.InventorySpot95",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test100_longrun_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Flux2.InventorySpot85, end: DM-Flux2.InventorySpot95 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Flux2.InventorySpot85",      "DM-Flux2.InventorySpot95",    20,                        true)
		);
	}

}