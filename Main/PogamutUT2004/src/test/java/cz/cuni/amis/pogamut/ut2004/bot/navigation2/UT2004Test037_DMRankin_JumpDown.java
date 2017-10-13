package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test037_DMRankin_JumpDown extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Rankin";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test37_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Rankin.PlayerStart0, end: DM-Rankin.PathNode41 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Rankin.PlayerStart0",      "DM-Rankin.PathNode41",    1,                        false)
		);
	}

        @Test
	public void test37_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 2 minutes
			2,
			// test movement between        start: DM-Rankin.PlayerStart0, end: DM-Rankin.PathNode41 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-Rankin.PlayerStart0",      "DM-Rankin.PathNode41",    20,                        false)
		);
	}

}
