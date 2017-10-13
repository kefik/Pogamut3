package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test054_DMMorpheus3_Jumppad extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Morpheus3";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test54_jumppad_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Morpheus3.PathNode21, end: DM-Morpheus3.PathNode61 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Morpheus3.PathNode21",      "DM-Morpheus3.PathNode61",    1,                        false)
		);
	}


        /**
        * TODO: Test fails
        */
        @Test
	public void test54_jumppad_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Morpheus3.PathNode21, end: DM-Morpheus3.PathNode61 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Morpheus3.PathNode21",      "DM-Morpheus3.PathNode61",    20,                        false)
		);
	}

}
