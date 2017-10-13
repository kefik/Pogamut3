package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test157_DM1on1Spirit_Elevator extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-1on1-Spirit";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test157_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-1on1-Spirit.PathNode71, end: DM-1on1-Spirit.LiftExit1 number of repetitions   both ways
			new NavigationTestBotParameters("DM-1on1-Spirit.PathNode71",      "DM-1on1-Spirit.LiftExit1",    1,                        true)
		);
	}

        /*
        * TODO: Test fails
        */
        @Test
	public void test157_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-1on1-Spirit.PathNode71, end: DM-1on1-Spirit.LiftExit1 number of repetitions   both ways
			new NavigationTestBotParameters("DM-1on1-Spirit.PathNode71",      "DM-1on1-Spirit.LiftExit1",    20,                        true)
		);
	}

}