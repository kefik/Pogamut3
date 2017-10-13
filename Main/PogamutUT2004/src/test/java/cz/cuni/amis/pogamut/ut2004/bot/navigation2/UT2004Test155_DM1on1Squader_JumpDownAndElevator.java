package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test155_DM1on1Squader_JumpDownAndElevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-1on1-Squader";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test155_jumpdown_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-1on1-Squader.PlayerStart0, end: DM-1on1-Squader.PathNode28 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-1on1-Squader.PlayerStart0",      "DM-1on1-Squader.PathNode28",    1,                        true)
		);
	}

        @Test
	public void test155_jumpdown_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-1on1-Squader.PlayerStart0, end: DM-1on1-Squader.PathNode28 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-1on1-Squader.PlayerStart0",      "DM-1on1-Squader.PathNode28",    20,                        true)
		);
	}

}
