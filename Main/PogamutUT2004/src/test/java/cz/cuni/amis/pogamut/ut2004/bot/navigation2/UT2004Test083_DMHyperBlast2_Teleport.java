package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test083_DMHyperBlast2_Teleport extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-HyperBlast2";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test83_teleport_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-HyperBlast2.PathNode138, end: DM-HyperBlast2.InventorySpot7 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-HyperBlast2.PathNode138",      "DM-HyperBlast2.InventorySpot7",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test83_teleport_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 5 minutes
			5,
			// test movement between        start: DM-HyperBlast2.PathNode138, end: DM-HyperBlast2.InventorySpot7 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-HyperBlast2.PathNode138",      "DM-HyperBlast2.InventorySpot7",    20,                        true)
		);
	}

}
