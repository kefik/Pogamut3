package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test168_DM1on1Roughinery_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-1on1-Roughinery";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test168_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-1on1-Roughinery.PathNode104, end: DM-1on1-Roughinery.PathNode54 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-1on1-Roughinery.PathNode104",      "DM-1on1-Roughinery.PathNode54",    1,                        false)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test168_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 3 minutes
			3,
			// test movement between        start: DM-1on1-Roughinery.PathNode104, end: DM-1on1-Roughinery.PathNode54 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-1on1-Roughinery.PathNode104",      "DM-1on1-Roughinery.PathNode54",    20,                        false)
		);
	}

}
