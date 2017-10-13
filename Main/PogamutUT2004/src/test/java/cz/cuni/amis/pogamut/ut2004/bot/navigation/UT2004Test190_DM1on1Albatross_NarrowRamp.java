package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 * 
 * @author Peta Michalik
 */
public class UT2004Test190_DM1on1Albatross_NarrowRamp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-1on1-Albatross";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

	@Test
	public void test190_narrow_1_time() {
		startTest(
		// use NavigationTestBot for the test
				NavigationTestBot.class,
				// timeout: 1 minute
				1,
				// test movement between start: DM-1on1-Albatross.PathNode1,
				// end: DM-1on1-Albatross.PathNode7 number of repetitions both
				// ways
				new NavigationTestBotParameters("DM-1on1-Albatross.PathNode1",
						"DM-1on1-Albatross.PathNode7", 1, true));
	}

	@Test
	public void test190_narrow_20_time() {

		startTest(
		// use NavigationTestBot for the test
				NavigationTestBot.class,
				// timeout: 4 minutes
				4,
				// test movement between start: DM-1on1-Albatross.PathNode1,
				// end: DM-1on1-Albatross.PathNode7 number of repetitions both
				// ways
				new NavigationTestBotParameters("DM-1on1-Albatross.PathNode1",
						"DM-1on1-Albatross.PathNode7", 20, true));
	}

}