package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test053_DMMorpheus3_Longjump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Morpheus3";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test53_jump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Morpheus3.JumpSpot36, end: DM-Morpheus3.PathNode6 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Morpheus3.JumpSpot36",      "DM-Morpheus3.PathNode6",    1,                        false)
		);
	}


        /**
        * TODO: Test fails
        */
        @Test
	public void test53_jump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Morpheus3.JumpSpot36, end: DM-Morpheus3.PathNode6 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Morpheus3.JumpSpot36",      "DM-Morpheus3.PathNode6",    20,                        false)
		);
	}

}
