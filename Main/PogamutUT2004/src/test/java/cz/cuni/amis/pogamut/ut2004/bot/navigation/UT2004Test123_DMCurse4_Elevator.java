package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test123_DMCurse4_Elevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Curse4";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test123_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Curse4.PlayerStart7, end: DM-Curse4.PathNode44 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Curse4.PlayerStart7",      "DM-Curse4.PathNode44",    1,                        true)
		);
	}

        @Test
	public void test123_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Curse4.PlayerStart7, end: DM-Curse4.PathNode44 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Curse4.PlayerStart7",      "DM-Curse4.PathNode44",    20,                        true)
		);
	}

}