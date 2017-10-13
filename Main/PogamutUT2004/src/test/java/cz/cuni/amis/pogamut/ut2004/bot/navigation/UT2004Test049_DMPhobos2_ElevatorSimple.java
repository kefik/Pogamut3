package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test049_DMPhobos2_ElevatorSimple extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Phobos2";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test49_elevator_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Phobos.LiftExit1, end: DM-Phobos.LiftExit0 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Phobos2.LiftExit1",      "DM-Phobos2.LiftExit0",    1,                        true)
		);
	}

        /**
        * TODO: Test fails
        */
        @Test
	public void test49_elevator_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Phobos.LiftExit1, end: DM-Phobos.LiftExit0 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Phobos2.LiftExit1",      "DM-Phobos2.LiftExit0",    20,                        true)
		);
	}

}