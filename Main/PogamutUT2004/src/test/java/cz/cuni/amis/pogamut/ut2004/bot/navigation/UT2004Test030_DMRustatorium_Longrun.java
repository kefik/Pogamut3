package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test030_DMRustatorium_Longrun extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Rustatorium";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test30_longrun_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Rustatorium.PathNode11, end: DM-Rustatorium.PathNode1 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Rustatorium.PathNode11",      "DM-Rustatorium.PathNode1",    1,                        true)
		);
	}

        @Test
	public void test30_longrun_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Rustatorium.PathNode11, end: DM-Rustatorium.PathNode1 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Rustatorium.PathNode11",      "DM-Rustatorium.PathNode1",    20,                        true)
		);
	}

}
