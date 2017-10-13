package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test095_DMGael_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Gael";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test95_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Gael.JumpSpot10, end: DM-Gael.PathNode56 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Gael.JumpSpot10",      "DM-Gael.PathNode56",    1,                        false)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test95_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 3 minutes
			3,
			// test movement between        start: DM-Gael.JumpSpot10, end: DM-Gael.PathNode56 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Gael.JumpSpot10",      "DM-Gael.PathNode56",    20,                        false)
		);
	}

}