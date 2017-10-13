package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test341_CTFTwinTombs_Combined extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-TwinTombs";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test341_combined_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-TwinTombs.InventorySpot218, end: CTF-TwinTombs.InventorySpot228 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-TwinTombs.InventorySpot218",      "CTF-TwinTombs.InventorySpot228",    1,                        true)
		);
	}

        @Test
	public void test341_combined_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 15 minutes
			15,
			// test movement between        start: CTF-TwinTombs.InventorySpot218, end: CTF-TwinTombs.InventorySpot228 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-TwinTombs.InventorySpot218",      "CTF-TwinTombs.InventorySpot228",    20,                        true)
		);
	}

}