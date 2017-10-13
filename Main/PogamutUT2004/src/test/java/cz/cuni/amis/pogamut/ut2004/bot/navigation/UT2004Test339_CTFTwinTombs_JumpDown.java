package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test339_CTFTwinTombs_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-TwinTombs";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test339_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-TwinTombs.InventorySpot240, end: CTF-TwinTombs.InventorySpot265 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-TwinTombs.InventorySpot240",      "CTF-TwinTombs.InventorySpot265",    1,                        false)
		);
	}

        /*
         * TODO: Test fails
         */
        @Test
	public void test339_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 8 minutes
			8,
			// test movement between        start: CTF-TwinTombs.InventorySpot240, end: CTF-TwinTombs.InventorySpot265 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-TwinTombs.InventorySpot240",      "CTF-TwinTombs.InventorySpot265",    20,                        false)
		);
	}

}