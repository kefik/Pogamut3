package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test091_DMGestalt_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Gestalt";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test91_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Gestalt.PathNode68, end: DM-Gestalt.InventorySpot1 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Gestalt.PathNode68",      "DM-Gestalt.InventorySpot1",    1,                        false)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test91_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 4 minutes
			4,
			// test movement between        start: DM-Gestalt.PathNode68, end: DM-Gestalt.InventorySpot1 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Gestalt.PathNode68",      "DM-Gestalt.InventorySpot1",    20,                        false)
		);
	}

}
