package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test241_CTFDecember_DoubleJump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-December";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test241_doublejump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-December.InventorySpot199, end: CTF-December.InventorySpot277 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-December.InventorySpot199",      "CTF-December.InventorySpot277",    1,                        false)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test241_doublejump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 3 minutes
			3,
			// test movement between        start: CTF-December.InventorySpot199, end: CTF-December.InventorySpot277 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-December.InventorySpot199",      "CTF-December.InventorySpot277",    20,                        false)
		);
	}

}
