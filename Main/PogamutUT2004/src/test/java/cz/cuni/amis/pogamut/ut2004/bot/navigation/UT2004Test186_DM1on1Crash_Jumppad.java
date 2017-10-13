package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test186_DM1on1Crash_Jumppad extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-1on1-Crash";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test186_jumppad_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-1on1-Crash.PathNode46, end: DM-1on1-Crash.PathNode35 number of repetitions   both ways
			new NavigationTestBotParameters("DM-1on1-Crash.PathNode46",      "DM-1on1-Crash.PathNode35",    1,                        true)
		);
	}

        @Test
	public void test186_jumppad_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 5 minutes
			5,
			// test movement between        start: DM-1on1-Crash.PathNode46, end: DM-1on1-Crash.PathNode35 number of repetitions   both ways
			new NavigationTestBotParameters("DM-1on1-Crash.PathNode46",      "DM-1on1-Crash.PathNode35",    20,                        true)
		);
	}

}