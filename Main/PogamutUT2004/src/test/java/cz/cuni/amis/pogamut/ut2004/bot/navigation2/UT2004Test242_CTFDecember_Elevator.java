package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test242_CTFDecember_Elevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-December";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test242_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-December.InventorySpot270, end: CTF-December.InventorySpot258 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-December.InventorySpot270",      "CTF-December.InventorySpot258",    1,                        false)
		);
	}

        @Test
	public void test242_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 5 minutes
			5,
			// test movement between        start: CTF-December.InventorySpot270, end: CTF-December.InventorySpot258 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-December.InventorySpot270",      "CTF-December.InventorySpot258",    20,                        false)
		);
	}

}
