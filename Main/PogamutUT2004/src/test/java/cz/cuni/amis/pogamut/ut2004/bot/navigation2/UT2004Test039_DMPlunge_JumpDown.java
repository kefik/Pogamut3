package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test039_DMPlunge_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Plunge";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test39_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Plunge.InventorySpot721, end: DM-Plunge.PathNode284 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Plunge.InventorySpot721",      "DM-Plunge.PathNode284",    1,                        true)
		);
	}

        /**
        * TODO: Test fails
        */
        @Test
	public void test39_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Plunge.InventorySpot721, end: DM-Plunge.PathNode284 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Plunge.InventorySpot721",      "DM-Plunge.PathNode284",    20,                        true)
		);
	}

}
