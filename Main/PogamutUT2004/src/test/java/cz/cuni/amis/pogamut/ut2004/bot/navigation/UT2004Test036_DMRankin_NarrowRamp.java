package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test036_DMRankin_NarrowRamp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Rankin";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test36_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Rankin.PlayerStart11, end: DM-Rankin.PathNode102 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Rankin.PlayerStart11",      "DM-Rankin.PathNode102",    1,                        true)
		);
	}

        /**
        * TODO: Test fails
        */
        @Test
	public void test36_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 8 minutes
			8,
			// test movement between        start: DM-Rankin.PlayerStart11, end: DM-Rankin.PathNode102 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Rankin.PlayerStart11",      "DM-Rankin.PathNode102",    20,                        true)
		);
	}

}