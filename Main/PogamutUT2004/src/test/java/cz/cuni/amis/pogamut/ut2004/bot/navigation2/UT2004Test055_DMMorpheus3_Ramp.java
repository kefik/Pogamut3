package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test055_DMMorpheus3_Ramp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Morpheus3";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test55_ramp_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Morpheus3.PathNode66, end: DM-Morpheus3.InventorySpot3 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Morpheus3.PathNode66",      "DM-Morpheus3.InventorySpot3",    1,                        true)
		);
	}


        /**
        * TODO: Test fails
        * (stava se to pomerne zridka, ale obcas bot pri ceste po rampe nahoru prepadne pres okraj)
        */
        @Test
	public void test55_ramp_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Morpheus3.PathNode66, end: DM-Morpheus3.InventorySpot3 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Morpheus3.PathNode66",      "DM-Morpheus3.InventorySpot3",    20,                        true)
		);
	}

}
