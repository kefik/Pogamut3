package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test274_CTFGeothermal_Ramp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Geothermal";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test274_ramp_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Geothermal.InventorySpot183, end: CTF-Geothermal.InventorySpot185 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Geothermal.InventorySpot183",      "CTF-Geothermal.InventorySpot185",    1,                        true)
		);
	}

        @Test
	public void test274_ramp_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: CTF-Geothermal.InventorySpot183, end: CTF-Geothermal.InventorySpot185 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Geothermal.InventorySpot183",      "CTF-Geothermal.InventorySpot185",    20,                        true)
		);
	}

}