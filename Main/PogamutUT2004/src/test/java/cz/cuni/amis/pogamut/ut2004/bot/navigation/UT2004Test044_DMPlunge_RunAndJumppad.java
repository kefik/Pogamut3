package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test044_DMPlunge_RunAndJumppad extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Plunge";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test44_runAndJump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Plunge.PlayerStart18, end: DM-Plunge.PathNode159 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Plunge.PlayerStart18",      "DM-Plunge.PathNode159",    1,                        false)
		);
	}


        /**
        * TODO: Test fails
        */
        @Test
	public void test44_runAndJump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Plunge.PlayerStart18, end: DM-Plunge.PathNode159 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Plunge.PlayerStart18",      "DM-Plunge.PathNode159",    20,                        false)
		);
	}

}