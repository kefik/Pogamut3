package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test220_CTFCitadel_jump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Citadel";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test220_jump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Citadel.InventorySpot201, end: CTF-Citadel.InventorySpot204 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Citadel.InventorySpot201",      "CTF-Citadel.InventorySpot204",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test220_jump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: CTF-Citadel.InventorySpot201, end: CTF-Citadel.InventorySpot204 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-Citadel.InventorySpot201",      "CTF-Citadel.InventorySpot204",    20,                        true)
		);
	}

}
