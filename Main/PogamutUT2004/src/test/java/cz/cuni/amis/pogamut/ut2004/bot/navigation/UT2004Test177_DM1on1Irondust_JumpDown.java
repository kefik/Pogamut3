package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test177_DM1on1Irondust_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-1on1-Irondust";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test177_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-1on1-Irondust.PlayerStart4, end: DM-1on1-Irondust.PathNode28 number of repetitions   both ways
			new NavigationTestBotParameters("DM-1on1-Irondust.PlayerStart4",      "DM-1on1-Irondust.PathNode28",    1,                        true)
		);
	}

        @Test
	public void test177_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-1on1-Irondust.PlayerStart4, end: DM-1on1-Irondust.PathNode28 number of repetitions   both ways
			new NavigationTestBotParameters("DM-1on1-Irondust.PlayerStart4",      "DM-1on1-Irondust.PathNode28",    20,                        true)
		);
	}

}