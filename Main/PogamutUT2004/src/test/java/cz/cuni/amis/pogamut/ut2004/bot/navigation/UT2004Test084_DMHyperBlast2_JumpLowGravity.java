package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test084_DMHyperBlast2_JumpLowGravity extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-HyperBlast2";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test84_jump_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-HyperBlast2.PathNode91, end: DM-HyperBlast2.PathNode133 number of repetitions   both ways
			new NavigationTestBotParameters("DM-HyperBlast2.PathNode91",      "DM-HyperBlast2.PathNode133",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test84_jump_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-HyperBlast2.PathNode91, end: DM-HyperBlast2.PathNode133 number of repetitions   both ways
			new NavigationTestBotParameters("DM-HyperBlast2.PathNode91",      "DM-HyperBlast2.PathNode133",    20,                        true)
		);
	}

}