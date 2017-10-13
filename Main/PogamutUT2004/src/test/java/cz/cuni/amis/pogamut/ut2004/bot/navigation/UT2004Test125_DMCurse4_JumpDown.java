package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test125_DMCurse4_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Curse4";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test125_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Curse4.PlayerStart10, end: DM-Curse4.PathNode37 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Curse4.PlayerStart10",      "DM-Curse4.PathNode37",    1,                        false)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test125_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Curse4.PlayerStart10, end: DM-Curse4.PathNode37 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Curse4.PlayerStart10",      "DM-Curse4.PathNode37",    20,                        false)
		);
	}

}