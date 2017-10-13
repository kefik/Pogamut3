package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test148_DM1on1Trite_Elevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-1on1-Trite";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test148_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-1on1-Trite.PathNode9, end: DM-1on1-Trite.PathNode49 number of repetitions   both ways
			new NavigationTestBotParameters("DM-1on1-Trite.PathNode9",      "DM-1on1-Trite.PathNode49",    1,                        false)
		);
	}

        @Test
	public void test148_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-1on1-Trite.PathNode9, end: DM-1on1-Trite.PathNode49 number of repetitions   both ways
			new NavigationTestBotParameters("DM-1on1-Trite.PathNode9",      "DM-1on1-Trite.PathNode49",    20,                        false)
		);
	}

}