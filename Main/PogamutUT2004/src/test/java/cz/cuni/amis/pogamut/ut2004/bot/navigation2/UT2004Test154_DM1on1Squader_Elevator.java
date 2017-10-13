package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test154_DM1on1Squader_Elevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-1on1-Squader";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test154_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-1on1-Squader.PathNode60, end: DM-1on1-Squader.PathNode0 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-1on1-Squader.PathNode60",      "DM-1on1-Squader.PathNode0",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test154_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-1on1-Squader.PathNode60, end: DM-1on1-Squader.PathNode0 number of repetitions   both ways
			new Navigation2TestBotParameters("DM-1on1-Squader.PathNode60",      "DM-1on1-Squader.PathNode0",    20,                        true)
		);
	}

}
