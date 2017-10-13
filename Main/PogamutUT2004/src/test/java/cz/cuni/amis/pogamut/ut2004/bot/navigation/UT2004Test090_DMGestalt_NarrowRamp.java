package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test090_DMGestalt_NarrowRamp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Gestalt";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test90_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Gestalt.PathNode56, end: DM-Gestalt.PathNode64 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Gestalt.PathNode56",      "DM-Gestalt.PathNode64",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test90_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Gestalt.PathNode56, end: DM-Gestalt.PathNode64 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Gestalt.PathNode56",      "DM-Gestalt.PathNode64",    20,                        true)
		);
	}

}