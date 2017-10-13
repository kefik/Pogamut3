package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test167_DM1on1Roughinery_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-1on1-Roughinery";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test167_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-1on1-Roughinery.PlayerStart13, end: DM-1on1-Roughinery.PathNode87 number of repetitions   both ways
			new NavigationTestBotParameters("DM-1on1-Roughinery.PlayerStart13",      "DM-1on1-Roughinery.PathNode87",    1,                        false)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test167_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 3 minutes
			3,
			// test movement between        start: DM-1on1-Roughinery.PlayerStart13, end: DM-1on1-Roughinery.PathNode87 number of repetitions   both ways
			new NavigationTestBotParameters("DM-1on1-Roughinery.PlayerStart13",      "DM-1on1-Roughinery.PathNode87",    20,                        false)
		);
	}

}