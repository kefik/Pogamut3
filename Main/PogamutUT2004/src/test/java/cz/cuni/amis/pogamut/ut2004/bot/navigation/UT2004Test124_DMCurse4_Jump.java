package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test124_DMCurse4_Jump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Curse4";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test124_jump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Curse4.LiftExit3, end: DM-Curse4.PathNode12 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Curse4.LiftExit3",      "DM-Curse4.PathNode11",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test124_jump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Curse4.LiftExit3, end: DM-Curse4.PathNode12 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Curse4.LiftExit3",      "DM-Curse4.PathNode11",    20,                        true)
		);
	}

}